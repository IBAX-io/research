package io.ibax.daemon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;

import io.ibax.data.Account;
import io.ibax.data.Block;
import io.ibax.data.Output;
import io.ibax.data.Transaction;
import io.ibax.data.TransactionInput;
import io.ibax.data.TxQueue;
import io.ibax.mapper.AccountMapper;
import io.ibax.mapper.BlockMapper;
import io.ibax.mapper.OutputMapper;
import io.ibax.mapper.TransactionMapper;
import io.ibax.mapper.TxQueueMapper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GenerateBlocks {
	private final static String UNKNOWN_TABLE = "UNKNOWN_TABLE";

	@Autowired
	private TxQueueMapper queueMapper;

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private OutputMapper outputMapper;

	@Autowired
	private BlockMapper blockMapper;

	@Autowired
	private TransactionMapper transactionMapper;

	@Scheduled(fixedRate = 4000)
	@Transactional
	public void generateBlocks2() {
		List<TxQueue> list = queueMapper.getFirst100TxQueue();

		Map<Integer, List<TxQueue>> groupMap = groupTxs(list);

		System.out.println("groupMap:" + groupMap);

		List<String> txHashs = execute(groupMap);

		packagingBlock(txHashs);

	}

	private void packagingBlock(List<String> txhash) {
		Integer height = 0;
		String parent = "0000000000000000000000000000000000000000000000000000000000000000";
		Block lastBlock = blockMapper.getLastBlock();

		if (lastBlock != null) {
			height = lastBlock.getHeight() + 1;
			parent = lastBlock.getHash();
		}
		Long timestamp = new Date().getTime();
		System.out.println("txhash\t" + txhash);
		blockMapper.insert(new Block(parent, height, timestamp, txhash));
	}

	// @Scheduled(fixedRate = 4000)
//	@Transactional
	public void generateBlocks() {
		log.debug("generateBlocks");

		List<TxQueue> list = queueMapper.getFirst100TxQueue();

		List<String> txhash = new ArrayList<String>();
		Integer height = 0;
		String parent = "0000000000000000000000000000000000000000000000000000000000000000";
		Block lastBlock = blockMapper.getLastBlock();

		if (lastBlock != null) {
			height = lastBlock.getHeight() + 1;
			parent = lastBlock.getHash();
		}

		if (list != null) {
			// mint
			Transaction mint = NFTMinter(height);
			txhash.add(mint.getHash());

			List<String> qHashs = new ArrayList<String>();
			for (TxQueue txQueue : list) {
				String contract = txQueue.getContract();
				String from = txQueue.getFrom();
				String to = txQueue.getTo();
				Long amount = txQueue.getAmount();
				Long timestamp = txQueue.getTimestamp();
				qHashs.add(txQueue.getHash());
				switch (contract) {
				case "Transfer":
					Transaction tx = Transfer(from, to, amount, timestamp, height);
					if (tx != null) {
						txhash.add(tx.getHash());
					}
					break;
				default:
					break;
				}
			}
			queueMapper.deleteByHashs(qHashs);
		}
		if (txhash.size() > 0) {
			Long timestamp = new Date().getTime();
			System.out.println("txhash\t" + txhash);
			blockMapper.insert(new Block(parent, height, timestamp, txhash));
		}

	}

	private Transaction NFTMinter(Integer height) {
		Long timestamp = new Date().getTime();
		String txhash = null;
		String from = "0000-0000-0000-0000-0000";
		String account = "1111-1111-1111-1111-1111";
		Long amount = 50L;
		String to = account;
		String inputHash = "0000000000000000000000000000000000000000000000000000000000000000";

		Transaction transaction = new Transaction(from, to, amount, timestamp, inputHash, height);
		transactionMapper.insert(transaction);
		txhash = transaction.getHash();
		Boolean targetable = true;
		Output output = new Output(height, account, amount, timestamp, targetable, 0, txhash);
		outputMapper.insert(output);

		return transaction;
	}

	private Transaction Transfer(String from, String to, Long amount, Long timestamp, Integer height) {
		log.debug("Transfer");
		Transaction transaction = null;
		List<TransactionInput> inputsFrom = transactionMapper.getTransactionInput(from);

//		List<Output> outputsFrom = outputMapper.getByAccount(from);

		Account accountTo = accountMapper.getByAccount(to);

		if (accountTo == null) {
			accountMapper.insert(to);
		}
		Long amountFrom = 0L;
		List<String> inputTxHashs = new ArrayList<String>();
		List<String> inputOHashs = new ArrayList<String>();
		if (inputsFrom != null) {
			for (TransactionInput input : inputsFrom) {
				amountFrom += input.getOamount();
				inputTxHashs.add(input.getThash());
				inputOHashs.add(input.getOhash());
			}
		}
		if (amountFrom >= amount) {

			Boolean targetable = true;
			transaction = new Transaction(from, to, amount, timestamp, Joiner.on(",").join(inputTxHashs), height);
			String txhash = transaction.getHash();
			transactionMapper.insert(transaction);

			timestamp = new Date().getTime();

			Output output0 = new Output(height, from, amountFrom - amount, timestamp, targetable, 0, txhash);
			Output output1 = new Output(height, to, amount, timestamp, targetable, 1, txhash);

			outputMapper.deleteByHashs(inputOHashs);
			outputMapper.insert(output0);
			outputMapper.insert(output1);

		} else {
			log.warn("There is not enough balance");
		}

		return transaction;
	}

	/**
	 * concurrent execution
	 *
	 * @param groupMap
	 * @return
	 */
	private List<String> execute(Map<Integer, List<TxQueue>> groupMap) {
		int cpus = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool = new ThreadPoolExecutor(2, cpus, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(10000), Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.DiscardPolicy());
		LinkedBlockingQueue<String> collectData = new LinkedBlockingQueue<>();// Collect all executed data
		long start = System.currentTimeMillis();
		while (!groupMap.isEmpty()) {
			Iterator<Map.Entry<Integer, List<TxQueue>>> it = groupMap.entrySet().iterator();
			Map<Integer, List<TxQueue>> excuGroupMap = new HashMap<>();// Collect the current groupings that can be executed in parallel
			int size = 0;
			while (it.hasNext()) {
				Entry<Integer, List<TxQueue>> entry = it.next();
				// System.out.println("group = " + entry.getKey() + ", value = " + entry.getValue());
				boolean flag = false;
				if (excuGroupMap.size() > 0 && entry.getValue().get(0).getTables().contains(UNKNOWN_TABLE)) {
					flag = true;
					break;
				} else {
					size += entry.getValue().size();
					excuGroupMap.put(entry.getKey(), entry.getValue());
					if (excuGroupMap.size() > 0) {
						if (entry.getValue().get(0).getTables().contains(UNKNOWN_TABLE)) {
							flag = true;
							it.remove();
							break;
						}
					}
				}
				if (flag) {
					break;
				}
				it.remove();
			}
			System.out.println("--------------Execute transactions in parallel-----------------");

			CountDownLatch countDownLatch = new CountDownLatch(size);
			excuGroupMap.forEach((key, value) -> {
				threadPool.execute(() -> {
					for (TxQueue txQueue : value) {
						try {
							if ((System.currentTimeMillis() - start) <= 2000) {
//								TimeUnit.MILLISECONDS.sleep(10);
								// TODO execute transaction

//								String contract = txQueue.getContract();
								String from = txQueue.getFrom();
								String to = txQueue.getTo();
								Long amount = txQueue.getAmount();
								Long timestamp = txQueue.getTimestamp();
								Transaction tx = Transfer(from, to, amount, timestamp, 0);
								if (tx != null) {
									collectData.add(tx.getHash());
//									collectData.add(tx);
								}

								log.info("transaction group {}, transaction address: {}", key, tx);
							}
							countDownLatch.countDown();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			});
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (groupMap.isEmpty()) {
					threadPool.shutdown();
				}
			}
		}
		log.info("Total number of transactions executed: {}", collectData.size());
		return new ArrayList<String>(collectData);
	}

	/**
	 * Group by contract occupancy resources and associated accounts
	 *
	 * @param txs
	 * @return
	 */
	private static Map<Integer, List<TxQueue>> groupTxs(List<TxQueue> txs) {
		int group = 0;
		HashMap<Integer, List<TxQueue>> groupMap = new HashMap<>();
		while (!txs.isEmpty()) {
			TxQueue tx0 = txs.get(0);
			Set<String> tableSet = new HashSet<>();
			tableSet.addAll(tx0.getTables());
			List<TxQueue> txadd = new ArrayList<>();
			Iterator<TxQueue> iterator = txs.iterator();
			while (iterator.hasNext()) {
				TxQueue tx = iterator.next();
				boolean flag = false;
				if (txadd.size() > 0 && tx.getTables().contains(UNKNOWN_TABLE)) {
					flag = true;
					group = extracted(group, groupMap, txadd);
					break;
				} else {
					if (tableSet.contains(UNKNOWN_TABLE)) {
						flag = true;
						txadd.add(tx);
						iterator.remove();
						groupMap.put(group, txadd);
						group++;
						break;
					} else {
						txadd.add(tx);
						iterator.remove();
						if (txs.isEmpty()) {
							group = extracted(group, groupMap, txadd);
						}
					}
				}
				if (flag) {
					break;
				}
			}
		}
		return groupMap;
	}

	private static int extracted(int group, HashMap<Integer, List<TxQueue>> groupMap, List<TxQueue> txadd) {
		while (!txadd.isEmpty()) {
			TxQueue vertex = txadd.get(0);
			Set<String> set = new HashSet<>();
			set.add(vertex.getFrom());
			set.add(vertex.getTo());
			set.addAll(vertex.getTables());
			List<TxQueue> tempTxadd = new ArrayList<>();
			Iterator<TxQueue> iterator1 = txadd.iterator();
			while (iterator1.hasNext()) {
				TxQueue tx1 = iterator1.next();
				if (set.contains(tx1.getFrom()) || set.contains(tx1.getTo())) {
					set.add(tx1.getFrom());
					set.add(tx1.getTo());
					set.addAll(tx1.getTables());
					tempTxadd.add(tx1);
					iterator1.remove();
				} else {
					for (String value : tx1.getTables()) {
						if (set.contains(value)) {
							set.add(tx1.getFrom());
							set.add(tx1.getTo());
							set.addAll(tx1.getTables());
							tempTxadd.add(tx1);
							iterator1.remove();
							break;
						}
					}
				}
			}
			groupMap.put(group, tempTxadd);
			group++;
		}
		return group;
	}
}

package io.ibax.daemon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	public void generateBlocks() {
		log.debug("generateBlocks");

		List<TxQueue> list = queueMapper.getFirst100TxQueue();
//		List<Transaction> transactions = new ArrayList<Transaction>();

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
			transaction = new Transaction(from, to, amount, timestamp, Joiner.on(",").join(inputTxHashs), 1);
			String txhash = transaction.getHash();
			transactionMapper.insert(transaction);

			timestamp = new Date().getTime();

			Output output0 = new Output(height, from, amountFrom - amount, timestamp, targetable, 0, txhash);
			Output output1 = new Output(height, to,                amount, timestamp, targetable, 1, txhash);

			outputMapper.deleteByHashs(inputOHashs);
			outputMapper.insert(output0);
			outputMapper.insert(output1);

		} else {
			log.warn("There is not enough balance");
		}

		return transaction;
	}
}

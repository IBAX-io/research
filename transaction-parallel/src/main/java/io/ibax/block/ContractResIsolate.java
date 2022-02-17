package io.ibax.block;

import java.util.ArrayList;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractResIsolate {
	private static final Logger log = LoggerFactory.getLogger(ContractResIsolate.class);

	public static void main(String[] args) {
		List<Transaction> txs = initData();// Initialize transaction data
		Map<Integer, List<Transaction>> groupMap = groupTxs(txs);// Group by contract occupancy resources and associated accounts
		System.out.println("groupMap:" + groupMap);
		execute(groupMap);// concurrent execution
		
	}

	/**
	 * concurrent execution
	 * @param groupMap
	 */
	private static void execute(Map<Integer, List<Transaction>> groupMap) {
		int cpus = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool = new ThreadPoolExecutor(2, cpus, 10L, TimeUnit.MILLISECONDS,
				new LinkedBlockingDeque<>(10000), Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.DiscardPolicy());

		LinkedBlockingQueue<Transaction> collectData = new LinkedBlockingQueue<Transaction>();// Collect all executed data
		while (!groupMap.isEmpty()) {

			Iterator<Map.Entry<Integer, List<Transaction>>> it = groupMap.entrySet().iterator();
			Map<Integer, List<Transaction>> excuGroupMap = new HashMap<Integer, List<Transaction>>();// Collect the current groupings that can be executed in parallel
			
			while (it.hasNext()) {
				Entry<Integer, List<Transaction>> entry = it.next();
				//System.out.println("group = " + entry.getKey() + ", value = " + entry.getValue());
				boolean flag = false;
				if(excuGroupMap.size() > 0 && entry.getValue().get(0).getTables().contains("x")) {
					flag = true;
					break;
				}else {
					excuGroupMap.put(entry.getKey(),entry.getValue());
					if(excuGroupMap.size() > 0) {
						if(entry.getValue().get(0).getTables().contains("x")) {
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
	        int [] size = new int[1];
			excuGroupMap.forEach((key,value)->{
				size[0] = size[0]+value.size();
			});
			CountDownLatch countDownLatch = new CountDownLatch(size[0]);
			
			excuGroupMap.forEach((key,value)->{
				threadPool.execute(()->{
					for (Transaction tx : value) { 
						try {
							TimeUnit.MILLISECONDS.sleep(10);
							collectData.add(tx);
							log.info("transaction group {}, transaction address: {}",key , tx);
							countDownLatch.countDown();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			});

			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally {
				if(groupMap.isEmpty()) {
					threadPool.shutdown();
				}
				
			}
			 
		}
		log.info("Total number of transactions executed: {}",collectData.size());
	}

	/**
	 * Group by contract occupancy resources and associated accounts
	 * @param txs
	 * @return
	 */
	private static Map<Integer, List<Transaction>> groupTxs(List<Transaction> txs){
		int group = 0;
		HashMap<Integer, List<Transaction>> groupMap = new HashMap<Integer, List<Transaction>>();

		while (!txs.isEmpty()) {
			Transaction tx0 = txs.get(0);
			Set<String> tableSet = new HashSet<String>();
			tableSet.addAll(tx0.getTables());

			List<Transaction> txadd = new ArrayList<Transaction>();

			Iterator<Transaction> iterator = txs.iterator();
			while (iterator.hasNext()) {
				Transaction tx = iterator.next();
				
				boolean flag = false;
				
				if(txadd.size() > 0 && tx.getTables().contains("x")) {
					flag = true;
					
					while(!txadd.isEmpty()) {
						Transaction vertex = txadd.get(0);
						
						Set<String> set = new HashSet<String>();
						set.add(vertex.getFrom()+"");
						set.add(vertex.getTo()+"");
						set.addAll(vertex.getTables());
						
						List<Transaction> tempTxadd = new ArrayList<Transaction>();
						Iterator<Transaction> iterator1 = txadd.iterator();
						while (iterator1.hasNext()) {
							Transaction tx1 = iterator1.next();
							if(set.contains(tx1.getFrom()+"")||set.contains(tx1.getTo()+"")) {
								set.add(tx1.getFrom()+"");
								set.add(tx1.getTo()+"");
								set.addAll(tx1.getTables());
								tempTxadd.add(tx1);
								iterator1.remove();
							}else {
								for(String value : tx1.getTables()) {
									if(set.contains(value)) {
										set.add(tx1.getFrom()+"");
										set.add(tx1.getTo()+"");
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
					break;
				}else {
					if(tableSet.contains("x")) {
						flag=true;
						txadd.add(tx);
						iterator.remove();
						groupMap.put(group, txadd);
						group++;
						break;
					}else {
						txadd.add(tx);
						iterator.remove();
						
						if(txs.isEmpty()) {
							Transaction vertex = txadd.get(0);
							
							Set<String> set = new HashSet<String>();
							set.add(vertex.getFrom()+"");
							set.add(vertex.getTo()+"");
							set.addAll(vertex.getTables());
							
							List<Transaction> tempTxadd = new ArrayList<Transaction>();
							Iterator<Transaction> iterator1 = txadd.iterator();
							while (iterator1.hasNext()) {
								Transaction tx1 = iterator1.next();
								if(set.contains(tx1.getFrom()+"")||set.contains(tx1.getTo()+"")) {
									set.add(tx1.getFrom()+"");
									set.add(tx1.getTo()+"");
									set.addAll(tx1.getTables());
									tempTxadd.add(tx1);
									iterator1.remove();
								}else {
									for(String value : tx1.getTables()) {
										if(set.contains(value)) {
											set.add(tx1.getFrom()+"");
											set.add(tx1.getTo()+"");
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
					}
				}
				if (flag) {
					break;
				}
			}
		}
		return groupMap;
	}

	/**
	 * Initialize transaction data
	 * @return
	 */
	private static List<Transaction> initData() {
		List<Transaction> txs = new ArrayList<Transaction>();
		{
			List<String> tables = new ArrayList<String>();
			tables.add("1_bad_blocks");
			tables.add("1_app_params");
			tables.add("transactions");
			Transaction tx = new Transaction(2, 1, tables);
			
			txs.add(tx);
		}
		{
			List<String> tables = new ArrayList<String>();
			tables.add("1_bad_blocks");
			tables.add("transactions");
			Transaction tx = new Transaction(4, 5, tables);
			
			txs.add(tx);
		}
		{
			List<String> tables = new ArrayList<String>();
			tables.add("2_bad_blocks");
			tables.add("2_app_params");
			Transaction tx = new Transaction(3, 2, tables);
			
			txs.add(tx);
		}
		{
			List<String> tables = new ArrayList<String>();
			tables.add("3_bad_blocks");
			tables.add("3_app_params");
			Transaction tx = new Transaction(6, 7, tables);
			
			txs.add(tx);
		}
		{
			List<String> tables = new ArrayList<String>();
			tables.add("x");
			tables.add("1_bad_blocks");
			Transaction tx = new Transaction(1, 2, tables);
			
			txs.add(tx);
		}
		{
			List<String> tables = new ArrayList<String>();
			tables.add("x");
			tables.add("1_app_params");
			tables.add("1_bad_blocks");
			Transaction tx = new Transaction(1, 2, tables);
			
			txs.add(tx);
		}
		{
			List<String> tables = new ArrayList<String>();
			tables.add("1_menu");
			tables.add("1_app_params");
			Transaction tx = new Transaction(3, 4, tables);
			txs.add(tx);
		}
		{
			List<String> tables = new ArrayList<String>();
			tables.add("1_menu");
			tables.add("1_app_params");
			Transaction tx = new Transaction(5, 6, tables);
			txs.add(tx);
		}
		{
			List<String> tables = new ArrayList<String>();
			tables.add("x");
			Transaction tx = new Transaction(1, 2, tables);
			txs.add(tx);
		}
		{
			List<String> tables = new ArrayList<String>();
			tables.add("1_bad_blocks");
			tables.add("1_app_params");
			tables.add("transactions");
			Transaction tx = new Transaction(0, 1, tables);
			
			txs.add(tx);
		}
		{
			List<String> tables = new ArrayList<String>();
			tables.add("1_bad_blocks");
			tables.add("2_app_params");
			Transaction tx = new Transaction(2, 3, tables);
			
			txs.add(tx);
		}

		return txs;
	}
}

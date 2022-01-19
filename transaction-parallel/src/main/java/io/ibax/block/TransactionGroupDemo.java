package io.ibax.block;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionGroupDemo {
	private static final Logger log = LoggerFactory.getLogger(TransactionGroupDemo.class);

	public static void main(String[] args) {
		TransactionService transactionService = new TransactionService();
		List<Transaction> txs = transactionService.getTransactionList();
		HashMap<Integer, List<Transaction>> groupMap = transactionService.transactionGroup(txs);
		
		int cpus = Runtime.getRuntime().availableProcessors();
		ExecutorService threadPool  = new ThreadPoolExecutor(
				cpus,
				cpus*10,
				3L,
				TimeUnit.SECONDS,
				new LinkedBlockingDeque<>(3),
				Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.DiscardPolicy() 
		);
		
		long start = System.currentTimeMillis();
		LinkedBlockingQueue<Transaction> collectData = new LinkedBlockingQueue<Transaction>();
		CountDownLatch countDownLatch = new CountDownLatch(100);
		
		try {
			groupMap.forEach((key, value) -> {
				threadPool.execute(()->{
					for (Transaction tx : value) {
						try {
							if((System.currentTimeMillis() - start)<=2000) {
								TimeUnit.MILLISECONDS.sleep(200);
								// collect data
								collectData.add(tx);
								countDownLatch.countDown();
								log.info("transaction group {}, transaction address: {}",key , tx);
							}else {
								break;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			});
			
	        countDownLatch.await(2000,TimeUnit.MILLISECONDS);
	        for (Transaction tx: collectData) {
	            System.out.println(tx);
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			threadPool.shutdown();
		}
	}
}

package io.ibax.block;

import java.util.HashMap;
import java.util.List;
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
		List<Transaction> txs = transactionService.getTransactionList(10000);
		HashMap<Integer, List<Transaction>> groupMap = transactionService.transactionGroup(txs);
		
		int cpus = Runtime.getRuntime().availableProcessors();
		//ExecutorService threadPool = Executors.newFixedThreadPool(100);
		ExecutorService threadPool  = new ThreadPoolExecutor(
				cpus*10,
				cpus*100,
				10L,
				TimeUnit.MILLISECONDS,
				new LinkedBlockingDeque<>(10000),
				Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.DiscardPolicy() 
		);
		
		long start = System.currentTimeMillis();
		LinkedBlockingQueue<Transaction> collectData = new LinkedBlockingQueue<Transaction>();
		   System.out.println("group:"+groupMap.size());
		try {
			groupMap.forEach((key, value) -> {
				threadPool.execute(()->{
					for (Transaction tx : value) { 
						try {
							if((System.currentTimeMillis() - start)<=2000) {
								TimeUnit.MILLISECONDS.sleep(10);
								collectData.add(tx);
								
							}else {
								break;
							}
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			});
	        
	        threadPool.shutdown();
	        while (!threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
               
            }
	        System.out.println("isTerminated:" + threadPool.isTerminated());
			System.out.println(collectData.size());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}

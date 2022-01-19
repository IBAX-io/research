package io.ibax.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionGroupDemo2 {
	private static final Logger log = LoggerFactory.getLogger(TransactionGroupDemo2.class);

	public static void main(String[] args) {
		TransactionService transactionService = new TransactionService();
		List<Transaction> txs = transactionService.getTransactionList(20000);
		HashMap<Integer, List<Transaction>> groupMap = transactionService.transactionGroup(txs);

		int cpus = Runtime.getRuntime().availableProcessors();

		List<FutureTask<List<Transaction>>> collectData = new ArrayList<FutureTask<List<Transaction>>>();

		ExecutorService threadPool = Executors.newFixedThreadPool(cpus*10);
		long start = System.currentTimeMillis();
		System.out.println("group:" + groupMap.size());

		try {
			groupMap.forEach((key, value) -> {

				FutureTask<List<Transaction>> ft = new FutureTask<List<Transaction>>(
						new ColletTransaction(value, start));
				collectData.add(ft);
				threadPool.submit(ft);

			});
			int countTx=0;
			for (FutureTask<List<Transaction>> futureTask : collectData) {
				List<Transaction> tx = futureTask.get();
				countTx += tx.size();
			}

			threadPool.shutdown();

			System.out.println("isTerminated:" + threadPool.isTerminated());
			System.out.println(countTx);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

package io.ibax.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TransactionService {
	
	public List<Transaction> getTransactionList() {
		Random random = new Random();
		List<Transaction> txs = new ArrayList<Transaction>();
		
		for (int i=0; i<100; i++) {
			txs.add(new Transaction(random.nextInt(99), random.nextInt(99)));
		}
		
		Collections.sort(txs, new Comparator<Transaction>() {

			@Override
			public int compare(Transaction o1, Transaction o2) {
				return o1.getFrom() - o2.getFrom();
			}
			
		});
		return txs;
	}
	
	public HashMap<Integer, List<Transaction>> transactionGroup(List<Transaction> txs){
		HashMap<Integer, List<Transaction>> groupMap = new HashMap<Integer, List<Transaction>>();

		int group = 0;
		while (!txs.isEmpty()) {
			Transaction vertex = txs.get(0);
			Set<Integer> set = new HashSet<Integer>();
			set.add(vertex.getFrom());
			set.add(vertex.getTo());
			Iterator<Transaction> iterator = txs.iterator();
			List<Transaction> txadd = new ArrayList<Transaction>();
			while (iterator.hasNext()) {
				Transaction tx = iterator.next();
				if(set.contains(tx.getFrom())||set.contains(tx.getTo())) {
					set.add(tx.getFrom());
					set.add(tx.getTo());
					txadd.add(tx);
					iterator.remove();
				}
			}
			groupMap.put(group, txadd);
			group++;
		}
		
		return groupMap;
		
	}
}

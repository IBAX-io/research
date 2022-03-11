package io.ibax.block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ColletTransaction implements Callable{
	private List<Transaction> txs;
	private long start;
	
	public ColletTransaction(List<Transaction> txs,long start) {
		super();
		this.txs = txs;
		this.start = start;
	}

	@Override
	public Object call() throws Exception {
		List<Transaction> tempTxs = new ArrayList<Transaction>();
		for (Transaction tx : this.txs) { 
			if((System.currentTimeMillis() - this.start)<=2000) {
				try {
					TimeUnit.MILLISECONDS.sleep(10);
					tempTxs.add(tx);
//					System.out.println(tx);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		return tempTxs;
	}
	
}

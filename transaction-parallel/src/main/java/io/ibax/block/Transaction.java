package io.ibax.block;

public class Transaction {
	private int from;

	private int to;

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public Transaction(int from, int to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		return "Transaction [from=" + from + ", to=" + to + "]";
	}
	

}
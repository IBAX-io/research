package io.ibax.block;

import java.util.List;

public class Transaction {
	private int from;

	private int to;
	
	private List<String> tables;
	
	public Transaction() {}

	public Transaction(int from, int to, List<String> tables) {
		super();
		this.from = from;
		this.to = to;
		this.tables = tables;
	}

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

	public List<String> getTables() {
		return tables;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}

	@Override
	public String toString() {
		return "Transaction [from=" + from + ", to=" + to + ", tables=" + tables + "]";
	}

}
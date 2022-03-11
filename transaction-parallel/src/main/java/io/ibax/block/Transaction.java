package io.ibax.block;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
	private int from;

	private int to;

	private List<String> tables;

	public Transaction(int from, int to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		return "Transaction [from=" + from + ", to=" + to + ", tables=" + tables + "]";
	}

}
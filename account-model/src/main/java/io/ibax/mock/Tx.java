package io.ibax.mock;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tx {

	private String from; // from account
	private String to; // to account
	private BigDecimal amount;
	private Long time;

//	public Tx() {
//
//	}
//
//	public Tx(String from, String to, BigDecimal amount, Long time) {
//		this.from = from;
//		this.to = to;
//		this.amount = amount;
//		this.time = time;
//	}

}

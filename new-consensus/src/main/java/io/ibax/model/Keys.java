package io.ibax.model;

import static org.apache.commons.codec.binary.Hex.encodeHexString;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Keys {
	private BigDecimal id;
	private byte[] pub;
	private BigDecimal amount;
	private BigDecimal maxpay;
	private BigDecimal deposit;
	private Integer multi;
	private Integer deleted;
	private Integer blocked;
	private Integer ecosystem;
	private String account;

	public Keys(BigDecimal id, byte[] pub, BigDecimal amount, BigDecimal maxpay, BigDecimal deposit, Integer multi, Integer deleted, Integer blocked, Integer ecosystem, String account) {
		this.id = id;
		this.pub = pub;
		this.amount = amount;
		this.maxpay = maxpay;
		this.deposit = deposit;
		this.multi = multi;
		this.deleted = deleted;
		this.blocked = blocked;
		this.ecosystem = ecosystem;
		this.account = account;
	}

	@Override
	public String toString() {
		return "Keys [id=" + id + ", pub=" + encodeHexString(pub) + ", amount=" + amount + ", maxpay=" + maxpay + ", deposit=" + deposit + ", multi=" + multi + ", deleted=" + deleted + ", blocked="
				+ blocked + ", ecosystem=" + ecosystem + ", account=" + account + "]";
	}

}

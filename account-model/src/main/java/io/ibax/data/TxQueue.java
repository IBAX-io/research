package io.ibax.data;

import java.io.IOException;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import io.ibax.util.Hash256Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TxQueue {

	private String hash;

	private byte[] data;
	// data extra
	private String contract;
	private String from; // from account
	private String to; // to account
	private Long amount;
	private Long timestamp;

//	public TxPool() {
//
//	}

	public TxQueue(String contract, String from, String to, Long amount, Long timestamp) {
		try {
			MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
			packer.packString(contract)//
					.packString(from)//
					.packString(to)//
					.packLong(amount)//
					.packLong(timestamp);
			packer.close();
			this.data = packer.toByteArray();
			this.hash = Hash256Util.hash256(this.data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.contract = contract;
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.timestamp = timestamp;
	}

	public TxQueue(String hash, String contract, String from, String to, Long amount, Long timestamp) {
		this.hash = hash;
		this.contract = contract;
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.timestamp = timestamp;
	}

}

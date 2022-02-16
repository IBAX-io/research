package io.ibax.data;

import java.io.IOException;
import java.math.BigInteger;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import io.ibax.util.Hash256Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
	private String hash; // TODO byte[]  hash
	private String from; // from account
	private String to; // to account
	private BigInteger amount;   // TODO  type amount
	private Long timestamp;
	private String input; // TODO byte[] input  , MessagePack input hash Array
	private String output; // TODO byte[] output , MessagePack output hash Array
	private Integer height;

	public Transaction(String from, String to, BigInteger amount, Long timestamp, String input, String output, Integer height) {

		try {
			MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
			packer.packString(from)//
					.packString(to)//
					.packBigInteger(amount)//
					.packLong(timestamp)//
					.packString(input)//
					.packString(output)//
					.packInt(height);
			packer.close();
			this.hash = Hash256Util.hash256(packer.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.from = from;
		this.to = to;
		this.amount = amount;
		this.timestamp = timestamp;
		this.input = input;
		this.output = output;
		this.height = height;
	}

}

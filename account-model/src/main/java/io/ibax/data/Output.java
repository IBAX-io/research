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
public class Output {

	private String hash; // TODO byte[] hash
	private Integer height;
	private BigInteger amount;
	private String account;
	private Long timestamp;
	private Boolean targetable; // TODO Transaction channel

	public Output(Integer height, String account, BigInteger amount,Long timestamp, Boolean targetable) {

		try {
			MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
			packer.packInt(height)//
					.packString(account)//
					.packBigInteger(amount)//
					.packLong(timestamp)//
					.packBoolean(targetable);
			packer.close();
			this.hash = Hash256Util.hash256(packer.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.height = height;
		this.account = account;
		this.amount = amount;
		this.timestamp = timestamp;
		this.targetable = targetable;
	}

}

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
public class Output {

	private String hash; // TODO byte[] hash
	private byte[] data;
	// data extra
	private Integer height;
	private Long amount;
	private String account;
	private Long timestamp;
	private Boolean targetable; // TODO Transaction channel
	private Integer index;
	private String txhash;
	
	public Output(Integer height, String account, Long amount,Long timestamp, Boolean targetable,Integer index,String txhash) {

		try {
			MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
			packer.packInt(height)//
					.packString(account)//
					.packLong(amount)//
					.packLong(timestamp)//
					.packBoolean(targetable)//
					.packInt(index);
			packer.close();
			this.data = packer.toByteArray();
			this.hash = Hash256Util.hash256(this.data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.height = height;
		this.account = account;
		this.amount = amount;
		this.timestamp = timestamp;
		this.targetable = targetable;
		this.index = index;
		this.txhash = txhash;
	}

}

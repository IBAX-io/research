package io.ibax.data;

import java.io.IOException;
import java.util.List;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

import com.google.common.base.Joiner;

import io.ibax.util.Hash256Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Block {

	private String hash; // TODO byte[]  hash
	private byte[] data;
	// data extra
	private String parent;  // TODO byte[]  previousblockhash
	private Integer height;
	private Long timestamp;
	private String txhash;   // TODO byte[] tx  , MessagePack tx hash Array
//	private Integer txNum;
	
	
	public Block(String parent, Integer height, Long timestamp, List<String> txsHash) {

		try {
			MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
			packer.packInt(height)//
			.packString(parent)//
			.packLong(timestamp);
			
			if(txsHash!=null&&txsHash.size()>0) {
				packer.packArrayHeader(txsHash.size());
				for (String txHash : txsHash) {
					packer.packString(txHash);
				}
				this.txhash= Joiner.on(",").join(txsHash);
			}
			packer.close();
			
			this.data = packer.toByteArray();
			this.hash = Hash256Util.hash256(this.data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.parent = parent;
		this.height = height;
		this.timestamp = timestamp;
	}

	
}

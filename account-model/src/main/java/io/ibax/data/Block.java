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
public class Block {

	private String hash; // TODO byte[]  hash
	private String parent;  // TODO byte[]  previousblockhash
	private Integer height;
	private Long timestamp;
//	private String txHash;   // TODO byte[] tx  , MessagePack tx hash Array
//	private Integer txNum;
	
	
	public Block(String parent, Integer height, Long timestamp, String... txsHash) {

		try {
			MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
			packer.packInt(height)//
			.packString(parent)//
			.packLong(timestamp);
			
			if(txsHash!=null&&txsHash.length>0) {
				packer.packArrayHeader(txsHash.length);
				for (String txHash : txsHash) {
					packer.packString(txHash);
				}
			}
			
			packer.close();
			this.hash = Hash256Util.hash256(packer.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.parent = parent;
		this.height = height;
		this.timestamp = timestamp;
	}

	
}

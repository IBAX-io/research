package io.ibax.daemon;

import org.apache.commons.codec.binary.Hex;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.ibax.data.TxQueue;
import io.ibax.mapper.TxQueueMapper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CollectTx {

	
	@Autowired
	private TxQueueMapper queueMapper;

	
	/**
	 * Collecting real-time transactions
	 * 
	 * @param tx
	 */
	@KafkaListener(topics = "MockTransaction", groupId = "group_id")
	public void collectTx(String tx) {
		log.debug("start collectTx");
		log.debug("collectTx\t" + tx);

		try {
			byte[] packer = Hex.decodeHex(tx);
			MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packer);
			String contract = unpacker.unpackString();
			String from = unpacker.unpackString();
			String to = unpacker.unpackString();
			Long amount = unpacker.unpackLong();
			Long timestamp = unpacker.unpackLong();
			unpacker.close();
			log.debug("contract[{}],from[{}],to[{}],amount[{}],timestamp[{}]", contract, from, to, amount, timestamp);

			queueMapper.insert(new TxQueue(contract, from, to, amount, timestamp));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		log.debug("end collectTx");
	}
}

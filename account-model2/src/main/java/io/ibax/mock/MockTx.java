package io.ibax.mock;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.apache.kafka.clients.admin.NewTopic;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.ibax.data.Output;
import io.ibax.mapper.AccountMapper;
import io.ibax.mapper.OutputMapper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MockTx {
//	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private OutputMapper outputMapper;

//	@Autowired
//	private BlockMapper blockMapper;

//	@Autowired
//	private TransactionMapper transactionMapper;

	private static final String TOPIC = "MockTransaction";
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendTx(String message) {
//		log.debug(message);
		this.kafkaTemplate.send(TOPIC, message);
	}

	@Bean
	public NewTopic createTopic() {
		return new NewTopic(TOPIC, 3, (short) 1);
	}

	/**
	 * Mock real-time transactions
	 */
	@Scheduled(fixedRate = 1000)
	public void mockTx() {
//		log.debug("start mockTx");
		try {
			Output maxOutput = outputMapper.getMaxOutput();
			if (maxOutput != null) {
				String from = maxOutput.getAccount();
//				Long amount = maxOutput.getAmount();

				int accountNum = accountMapper.countAll();
				String to = null;
				if (accountNum < 10) {
					to = UUID.randomUUID().toString();
				} else {
					Output minOutput = outputMapper.getMinOutput();
					to = minOutput.getAccount();
				}

				Random random = new Random();
				MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
				packer.packString("Transfer");
				packer.packString(from)//
						.packString(to)//
						.packLong(random.nextLong(maxOutput.getAmount()))//
						.packLong(new Date().getTime());
				packer.close();
				String tx = Hex.encodeHexString(packer.toByteArray());
				sendTx(tx);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

//		log.debug("end mockTx");
	}

}

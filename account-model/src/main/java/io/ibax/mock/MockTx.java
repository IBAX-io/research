package io.ibax.mock;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import io.ibax.mapper.AccountMapper;
import io.ibax.mapper.BlockMapper;
import io.ibax.mapper.OutputMapper;
import io.ibax.mapper.TransactionMapper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MockTx {
//	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private OutputMapper outputMapper;

	@Autowired
	private BlockMapper blockMapper;

	@Autowired
	private TransactionMapper transactionMapper;

	private static final String TOPIC = "MockTransaction";
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessage(String message) {
		log.debug(message);
		this.kafkaTemplate.send(TOPIC, message);
	}

	@Bean
	public NewTopic createTopic() {
		return new NewTopic(TOPIC, 3, (short) 1);
	}

	/**
	 * 模拟实时交易事务
	 */
//	@Scheduled(fixedRate = 1000)
	public void mockMsg() {
		Random r = new Random();
		String from = r.nextInt(99999) + "";
		String to = r.nextInt(99999) + "";
		int amount = r.nextInt(99999999);
		Tx tx = new Tx(from, to, new BigDecimal(amount), new Date().getTime());
		System.out.println("mockMsg\t" + tx);
		String json = JSONObject.toJSONString(tx);
		sendMessage(json);
	}

	public static void main(String[] args) {
		Random r = new Random();
		int from = r.nextInt(2) + 1;
		System.out.println(from);

	}

}

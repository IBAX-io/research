package io.ibax;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.ibax.mapper.KeysMapper;

@SpringBootApplication
public class NewConsensusApplication implements CommandLineRunner {

	@Autowired
	private KeysMapper keysMapper;

	public static void main(String[] args) {
		SpringApplication.run(NewConsensusApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(this.keysMapper.selectKeysById(new BigDecimal("-2235209884042477171")));
	}

}

package io.ibax;

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.ibax.data.Account;
import io.ibax.data.Block;
import io.ibax.data.Output;
import io.ibax.data.Transaction;
import io.ibax.mapper.AccountMapper;
import io.ibax.mapper.BlockMapper;
import io.ibax.mapper.OutputMapper;
import io.ibax.mapper.TransactionMapper;

@SpringBootApplication
public class AccountModelApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountModelApplication.class, args);
	}

//	CityMapper Demo
//	@Bean
//	ApplicationRunner runner(CityMapper cityMapper) {
//		return args -> {
//			cityMapper.insert(new City(null, "NYC", "NY", "USA"));
//			cityMapper.findAll().forEach(System.out::println);
//		};
//	}

	@Bean
	ApplicationRunner GenesisBlock(AccountMapper accountMapper, //
			OutputMapper outputMapper, //
			BlockMapper blockMapper, //
			TransactionMapper transactionMapper) {
		return args -> {

			String account = UUID.randomUUID().toString();
//			String account = "0"; 
			{
				accountMapper.deleteAll();
				// Account
				System.out.println("account\t" + account);
				accountMapper.insert(new Account(0, account, false));
			}

			Integer height = 0;
			BigInteger amount = new BigInteger("100000000");
			String hashUTXO = null;
			{
				outputMapper.deleteAll();
				// Output
				Long timestamp = new Date().getTime();
				Boolean targetable = true;
				Output output = new Output(height, account, amount, timestamp, targetable);
				outputMapper.insert(output);
				hashUTXO = output.getHash();
			}

			String hashTx = null;
			{
				transactionMapper.deleteAll();
				// Transaction
				String from = "0000-0000-0000-0000-0000";
				String to = account;
				Long timestamp = new Date().getTime();
				String input = hashUTXO;
				String output = hashUTXO;
				Transaction transaction = new Transaction(from, to, amount, timestamp, input, output, height);
				transactionMapper.insert(transaction);
				hashTx = transaction.getHash();
			}
			{
				blockMapper.deleteAll();
				// Block
				String parent = "0000000000000000000000000000000000000000000000000000000000000000";
				Long timestamp = new Date().getTime();
				blockMapper.insert(new Block(parent, height, timestamp, hashTx));
			}

		};
	}

}

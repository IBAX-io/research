package io.ibax;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.ibax.data.Block;
import io.ibax.data.Output;
import io.ibax.data.Transaction;
import io.ibax.mapper.AccountMapper;
import io.ibax.mapper.BlockMapper;
import io.ibax.mapper.OutputMapper;
import io.ibax.mapper.TransactionMapper;

@SpringBootApplication
@EnableScheduling
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

//	@Bean
	ApplicationRunner GenesisBlock(AccountMapper accountMapper, //
			OutputMapper outputMapper, //
			BlockMapper blockMapper, //
			TransactionMapper transactionMapper) {
		return args -> {

//			String account = UUID.randomUUID().toString();
//			String account = "0"; 
//			account="234e1345-2b92-454e-ac08-9ab95215d3cf";
			String account = "1111-1111-1111-1111-1111";

			{
				accountMapper.deleteAll();
				// Account
				System.out.println("account\t" + account);
				accountMapper.insert(account);
			}

			Integer height = 0;
			Long amount = 100_000_000_000_000L;

			String txhash = null;
			Long timestamp = new Date().getTime();
			{

				// Transaction
				transactionMapper.deleteAll();

				String from = "0000-0000-0000-0000-0000";
				String to = account;
				String inputHash = "0000000000000000000000000000000000000000000000000000000000000000";

				Transaction transaction = new Transaction(from, to, amount, timestamp, inputHash, height);
				transactionMapper.insert(transaction);
				txhash = transaction.getHash();

				// Output
				outputMapper.deleteAll();
				Boolean targetable = true;
				Output output = new Output(height, account, amount, timestamp, targetable, 0, txhash);
				outputMapper.insert(output);
			}
			{
				blockMapper.deleteAll();
				// Block
				String parent = "0000000000000000000000000000000000000000000000000000000000000000";
				List<String> txhash2 = new ArrayList<String>();
				txhash2.add(txhash);
				blockMapper.insert(new Block(parent, height, timestamp, txhash2));
			}

		};
	}

}

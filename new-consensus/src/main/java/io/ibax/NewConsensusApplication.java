package io.ibax;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling 
@SpringBootApplication
public class NewConsensusApplication{

	public static void main(String[] args) {
		SpringApplication.run(NewConsensusApplication.class, args);
	}
}

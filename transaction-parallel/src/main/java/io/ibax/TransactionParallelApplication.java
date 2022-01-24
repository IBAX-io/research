package io.ibax;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableWebMvc
public class TransactionParallelApplication {
	public static void main(String[] args) {
		SpringApplication.run(TransactionParallelApplication.class, args);

	}

}

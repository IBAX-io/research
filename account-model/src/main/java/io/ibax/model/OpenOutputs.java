package io.ibax.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OpenOutputs {

	private  byte[] hash;
	private  Integer height;

	private BigDecimal amount;
	private String account;
	private 	Integer targetable;
	private Boolean	coinbase;
	private Boolean deposit;
	private Integer ecosystem;
	private String 	scenes ;
	
	
}


//"hash" bytea NOT NULL,
//"height" int8 NOT NULL,
//"amount" numeric(30,0) NOT NULL,
//"account" char(24) COLLATE "pg_catalog"."default" NOT NULL,
//"targetable" int2 NOT NULL,
//"coinbase" bool NOT NULL,
//"deposit" bool NOT NULL,
//"ecosystem" int8 NOT NULL,
//"scenes" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,


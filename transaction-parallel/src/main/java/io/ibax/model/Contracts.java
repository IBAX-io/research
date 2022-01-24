package io.ibax.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Contracts {
	private int id;
	
	private String name;
	
	private String value;
	
	private int walletId;
	
	private int tokenId;
	
	private String conditions;
	
	private String permissions;
	
	private int appId;
	
	private int ecosystem;
	
}

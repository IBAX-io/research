package io.ibax.model;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ContractsRelation {
	private String contractsName;
	
	private List<String> contracts;
	
	private List<String> func;
	
	private List<String> optResource;
	
}

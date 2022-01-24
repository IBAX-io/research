package io.ibax.service;

import java.util.List;
import java.util.Map;

import io.ibax.model.Contracts;


public interface ContractsService {
	public List<Contracts> getContractsList();
	
	public Map<String,List<Contracts>> getContractsRelationList();
	
	public String getContractsJsonString();
}

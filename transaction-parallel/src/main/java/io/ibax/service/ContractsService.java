package io.ibax.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import io.ibax.common.JsonUtil;
import io.ibax.mapper.ContractsMapper;
import io.ibax.model.Contracts;
import io.ibax.model.ContractsRelation;

@Service
public class ContractsService {

	@Autowired
	ContractsMapper contractsMapper;


	public List<Contracts> getContractsList() {
		List<Contracts> contractsList= contractsMapper.getContractsList();

		return contractsList;
	}
	

	public String getContractsJsonString() {
		List<Contracts> contractsList= contractsMapper.getContractsList();
		Map<String,List<String>> map = new HashMap<String,List<String>>();
		
		for (Contracts contracts : contractsList) {
			List<String> list = new ArrayList<String>();
			
			for (Contracts temp : contractsList) {
				if(temp.getValue().contains(contracts.getName())) {
					list.add(temp.getName());
				}
			}
			map.put(contracts.getName(), list);
		}
		return JSON.toJSONString(map);
	}

	public Map<String, List<Contracts>> getContractsRelationList() {
		List<Contracts> contractsList= contractsMapper.getContractsList();
		Map<String,List<Contracts>> map = new HashMap<String,List<Contracts>>();
		
		for (Contracts contracts : contractsList) {
			List<Contracts> list = new ArrayList<Contracts>();
			
			for (Contracts temp : contractsList) {
				if(!contracts.getName().equals(temp.getName())) {
					if(temp.getValue().contains(contracts.getName())) {
						list.add(temp);
					}
				}
				
			}
			//if(!list.isEmpty()) {
				map.put(contracts.getName(), list);
			//}
			
		}

		return map;
	}

	public List<ContractsRelation> getContractsRelationList1() {
		List<Contracts> contractsList= contractsMapper.getContractsList();
		JsonUtil jsonUtil = new JsonUtil();
		Map<String, String[]> funcsMap = jsonUtil.readJsonFile();
		String [] funcString = funcsMap.get("EmbedFuncs");
		
		Map<String,List<String>> map = new HashMap<String,List<String>>();
		List<ContractsRelation> contractsRelationList = new ArrayList<ContractsRelation>();
		//contractsRelation.setContractsList(contractsList);
		
		for (Contracts contracts : contractsList) {
			ContractsRelation contractsRelation = new ContractsRelation();
			List<String> sbContracts = new ArrayList<String>();
			List<String> sbFunc = new ArrayList<String>();
			List<String> optResource = new ArrayList<String>();
			
			
			contractsRelation.setContractsName(contracts.getName());
			for (Contracts temp : contractsList) {
				if(!contracts.getName().equals(temp.getName())) {
					if(temp.getValue().contains(contracts.getName())) {
						sbContracts.add(temp.getName());
					}
				}
			}
			contractsRelation.setContracts(sbContracts);
			for (int i = 0; i < funcString.length; i++) {
				if(contracts.getValue().contains(funcString[i])) {
					sbFunc.add(funcString[i]);
				}
			}
			contractsRelation.setFunc(sbFunc);
			
			String str = contracts.getValue();
			if(str.contains("@")){
				for (int i = -1; (i = str.indexOf("@", i + 1)) != -1; i++) {
					//System.out.println(i);
					str = str.substring(i-1);
					
					//System.out.println(str.substring(0, str.indexOf(")")));
					optResource.add(str.substring(0, str.indexOf(")")));
					str = str.substring(str.indexOf(")"));
					i=-1;
				}
			}
			contractsRelation.setOptResource(optResource);
			
			contractsRelationList.add(contractsRelation);
			
		}
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
	            System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
	    }
		
		return contractsRelationList;
		
	}
}

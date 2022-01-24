package io.ibax.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import io.ibax.mapper.ContractsMapper;
import io.ibax.model.Contracts;
import io.ibax.service.ContractsService;

@Service
public class ContractsServiceImpl implements ContractsService {
	
	@Autowired
	ContractsMapper contractsMapper;

	@Override
	public List<Contracts> getContractsList() {
		List<Contracts> contractsList= contractsMapper.getContractsList();

		return contractsList;
	}
	

	@Override
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


	@Override
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
}

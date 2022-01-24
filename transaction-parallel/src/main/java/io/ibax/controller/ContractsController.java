package io.ibax.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.ibax.model.Contracts;
import io.ibax.service.ContractsService;

@RestController
public class ContractsController {
	@Autowired
	private ContractsService contractsService;
	
	@RequestMapping("/getContractsList")
	public String getContractsList(Model model) {
		List<Contracts> contractsList = contractsService.getContractsList();
		
		return "OK!";
	}
	
	@RequestMapping("getContractsJsonString")
	public String getContractsJsonString() {
	
		return contractsService.getContractsJsonString();
	}
	
	@RequestMapping("getContractsRelationList")
	public String getContractsRelationList(Model model) {
		Map<String,List<Contracts>> maps = contractsService.getContractsRelationList();
        model.addAttribute("maps",maps);

        return "contracts/list";
	}
	
}

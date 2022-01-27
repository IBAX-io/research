package io.ibax.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.ibax.model.Contracts;
import io.ibax.model.ContractsRelation;
import io.ibax.service.ContractsService;

@Controller
public class HelloController {
	
	@Autowired
	private ContractsService contractsService;
	
	@GetMapping("/hello")
    public String list(Model model){

		List<Contracts> list = contractsService.getContractsList();
		
        model.addAttribute("list",list);

        return "contracts/list";

    }
	
	@GetMapping("/hello1")
	public String getContractsRelationList(Model model) {
		Map<String,List<Contracts>> maps = contractsService.getContractsRelationList();
		
        model.addAttribute("maps",maps);

        return "contracts/list";
        
	}
	
	@GetMapping("/hello2")
	public String getContractsRelationList1(Model model) {
		List<ContractsRelation> contractsRelationList = contractsService.getContractsRelationList1();
		
        model.addAttribute("contractsRelationList",contractsRelationList);

        return "contracts/list2";
        
	}
}

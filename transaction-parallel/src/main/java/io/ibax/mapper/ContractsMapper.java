package io.ibax.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import io.ibax.model.Contracts;

@Mapper
@Repository
public interface ContractsMapper {
	
	public List<Contracts> getContractsList();
}

package io.ibax.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.ibax.model.CandidateNode;

/**
 * 
 * @author ak
 *
 */
@Mapper
public interface CandidateNodeMapper {
	
	/**
	 * batch insert candidate nodes
	 * @param candidateNodes
	 */
	public void batchInsert(@Param("candidateNodes") List<CandidateNode> candidateNodes);
	
	/**
	 * get candidate nodes
	 * @return
	 */
	public List<CandidateNode> getCandidateNodes();
}

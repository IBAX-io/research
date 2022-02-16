package io.ibax.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import io.ibax.data.Block;

@Mapper
public interface BlockMapper {
	@Insert("INSERT INTO t_block (hash,parent,height,timestamp) VALUES(#{hash},#{parent},#{height},#{timestamp}) ")
	void insert(Block block);

	@Delete("DELETE FROM t_block ")
	void deleteAll();

}

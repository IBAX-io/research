package io.ibax.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.ibax.data.Block;

@Mapper
public interface BlockMapper {
	@Insert("INSERT INTO t_block (hash,parent,height,timestamp,txhash) VALUES(#{hash},#{parent},#{height},#{timestamp},#{txhash}) ")
	void insert(Block block);

	@Delete("DELETE FROM t_block ")
	void deleteAll();

	@Select("SELECT count(*) FROM t_block ")
	int countAll();

	@Select("SELECT hash,parent,height,timestamp FROM t_block ORDER BY height DESC LIMIT 1 ")
	Block getLastBlock();

}

package io.ibax.mapper;

import java.util.Collection;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.ibax.data.Output;

@Mapper
public interface OutputMapper {
	@Insert("INSERT INTO t_output (hash,height,amount,account,timestamp,targetable) VALUES(#{hash},#{height},#{amount},#{account},#{timestamp},#{targetable}) ")
	void insert(Output output);

	@Delete("DELETE FROM t_output ")
	void deleteAll();

	@Select("SELECT hash,height,amount,account,targetable FROM t_output ")
	Collection<Output> findAll();

	@Select("SELECT count(*) FROM t_output ")
	int countAll();
}

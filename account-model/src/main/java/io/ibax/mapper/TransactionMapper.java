package io.ibax.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import io.ibax.data.Transaction;

@Mapper
public interface TransactionMapper {
	@Insert("INSERT INTO t_transaction (hash,\"from\",\"to\",amount,timestamp,input,output,height) VALUES(#{hash},#{from},#{to},#{amount},#{timestamp},#{input},#{output},#{height}) ")
	void insert(Transaction transaction);

	@Delete("DELETE FROM t_transaction ")
	void deleteAll();

}

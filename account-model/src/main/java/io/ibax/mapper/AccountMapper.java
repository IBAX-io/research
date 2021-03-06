package io.ibax.mapper;

import java.util.Collection;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.ibax.data.Account;

@Mapper
public interface AccountMapper {

	@Insert("INSERT INTO t_account (id,account,locked) VALUES(#{id},#{account},#{locked}) ")
	void insertAccount(Account account);

	@Insert("INSERT INTO t_account (account) VALUES(#{account}) ")
	void insert(String account);

	
	@Delete("DELETE FROM t_account ")
	void deleteAll();

	@Select("SELECT id,account,locked FROM t_account ")
	Collection<Account> findAll();

	@Select("SELECT count(*) FROM t_account ")
	int countAll();

	@Select("SELECT id,account,locked FROM t_account WHERE account = #{account} ")
	Account getByAccount(String account);
}

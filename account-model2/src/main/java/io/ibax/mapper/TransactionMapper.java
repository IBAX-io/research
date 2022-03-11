package io.ibax.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.ibax.data.Transaction;
import io.ibax.data.TransactionInput;

@Mapper
public interface TransactionMapper {
	@Insert("""

				INSERT INTO t_transaction (hash,"from","to",amount,timestamp,input,height)
				VALUES(#{hash},#{from},#{to},#{amount},#{timestamp},#{input},#{height})

			""")
	void insert(Transaction transaction);

	@Delete("DELETE FROM t_transaction ")
	void deleteAll();

	@Select("""

				SELECT
					o.hash as ohash,
					o.height as oheight,
					o.amount as oamount,
					o.account as oaccount,
					o.targetable as otargetable,
					o.timestamp as otimestamp,
					o.index as oindex,
					t.hash as thash,
					t.from as tfrom,
					t.to as tto,
					t.amount as tamount,
					t.timestamp as ttimestamp,
					t.height as theight,
					t.input as tinput
				FROM t_output AS o
				INNER   JOIN t_transaction AS t ON t.hash = o.txhash  
				WHERE o.account = #{account}

			""")
	List<TransactionInput> getTransactionInput(String account);
}

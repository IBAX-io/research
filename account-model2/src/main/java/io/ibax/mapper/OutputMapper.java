package io.ibax.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.ibax.data.Output;

@Mapper
public interface OutputMapper {

	@Insert("""

			INSERT INTO t_output (hash,height,amount,account,timestamp,targetable,index,txhash)
			VALUES(#{hash},#{height},#{amount},#{account},#{timestamp},#{targetable},#{index},#{txhash})

			""")
	void insert(Output output);

	@Delete("DELETE FROM t_output ")
	void deleteAll();

	@Select("SELECT hash,height,amount,account,targetable FROM t_output ")
	Collection<Output> findAll();

	@Select("SELECT count(*) FROM t_output ")
	int countAll();

	@Select("SELECT o.account,SUM(amount) amount FROM t_output AS o GROUP BY o.account ORDER BY amount DESC LIMIT 1 ")
	Output getMaxOutput();

	@Select("""
			SELECT ac.account , COALESCE(ou.amount,0)  as amount FROM t_account ac LEFT JOIN (
				SELECT o.account,SUM(amount) amount FROM t_output AS o GROUP BY o.account
			) ou  on  ac.account = ou.account  ORDER BY  amount asc  LIMIT 1
			""")
	Output getMinOutput();

	@Select("SELECT hash,height,amount,account,targetable,index,txhash FROM t_output WHERE account = #{account} ")
	List<Output> getByAccount(String account);

	@Delete("""
			<script>
				DELETE FROM t_output
				<where>
					<foreach collection="hashs" item="item" open="and hash in(" separator="," close=")">
						#{item}
					</foreach>
				</where>
			</script>
			""")
	void deleteByHashs(List<String> hashs);

}

package io.ibax.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import io.ibax.data.TxQueue;

@Mapper
public interface TxQueueMapper {
	@Insert("""
			INSERT INTO t_tx_queue (hash,contract,"from","to",amount,timestamp) VALUES(#{hash},#{contract},#{from},#{to},#{amount},#{timestamp})
			""")
	void insert(TxQueue txQueue);

	@Delete("DELETE FROM t_tx_queue ")
	void deleteAll();

	@Select("""
			SELECT hash,contract,"from","to",amount,"timestamp" FROM t_tx_queue ORDER BY "timestamp" asc LIMIT 100
			""")
	List<TxQueue> getFirst100TxQueue();
	@Delete("""
			<script>
				DELETE FROM t_tx_queue
				<where>
					<foreach collection="hashs" item="item" open="and hash in(" separator="," close=")">
						#{item}
					</foreach>
				</where>
			</script>
			""")
	void deleteByHashs(List<String> hashs);
}

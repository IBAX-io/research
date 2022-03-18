package io.ibax.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.ibax.model.Node;

/**
 * package node mapper
 * @author ak
 *
 */
@Mapper
public interface NodeMapper {
	/**
	 * Monitor all on-chain nodes and update network status
	 * @param node
	 */
	public void monitorUpateNote(Node node);
	
	/**
	 * Get packaging nodes on all chains
	 * @return
	 */
	public List<Node> getNodes();
	
	/**
	 * After the block is generated, update itself to FOLLOWER
	 * @param node
	 */
	public void updateNoteByFollower(Node node);
	
	/**
	 * The next packing node specified by the previous packing node
	 * @param node
	 */
	public void updateNextPackageNode(Node node);
	
	/**
	 * Find packaged nodes by id
	 * @param id
	 * @return
	 */
	public Node findById(int id);
	
	public Node findByIp(String ip);
	
	public void batchInsert(@Param("nodes") List<Node> nodes);
	
	public void insertNode(Node node);
}

package io.ibax.task;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tio.client.ClientChannelContext;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.utils.lock.SetWithLock;

import com.alibaba.fastjson.JSONObject;

import io.ibax.mapper.NodeMapper;
import io.ibax.model.Node;
import io.ibax.net.base.HelloPacket;
import io.ibax.net.base.NodeStatus;
import io.ibax.net.client.HelloClientStarter;
import io.ibax.net.conf.TioProps;

/**
 * block generation
 * @author ak
 *
 */
@Component
public class BlockGeneration {
	private static Logger log = LoggerFactory.getLogger(BlockGeneration.class);
	@Autowired
	private TioProps properties;
	@Autowired
	private NodeMapper nodeMapper;
	
	@Scheduled(fixedRate = 4000)
	public void blockGeneration(){
		List<Node> nodes = nodeMapper.getNodes();
		
		if(null != nodes || nodes.size() > 0) {
			for(Node node : nodes) {
				if(node.getNodeStatus()==NodeStatus.LEADER ) {
					log.info("start block generation...node info：{}",node.toString());
					
					SetWithLock<ChannelContext> setWithLock = HelloClientStarter.getClientTioConfig().connecteds;
					ReadLock readLock = setWithLock.readLock();
					readLock.lock();
					try {
						Set<ChannelContext> set = setWithLock.getObj();
						if(set.size() > 0) {
							// After the block is generated, update itself to FOLLOWER
							node.setNodeStatus(NodeStatus.FOLLOWER);
							nodeMapper.updateNoteByFollower(node);
							// Notify to specify the next packing node
							Node nextNode = nextGeneratedBlockNode(nodes, node);
							
							for (ChannelContext entry : set) {
								ClientChannelContext channelContext = (ClientChannelContext) entry;
								log.info("package node ip:{},port:{}",channelContext.getServerNode().getIp(),channelContext.getServerNode().getPort());
								if(nextNode.getIp().equals(channelContext.getServerNode().getIp()) && nextNode.getPort()==channelContext.getServerNode().getPort()) {
									send(channelContext);
								}
							}
						}else {
							log.error("If it is not connected to all node networks, generate blocks by yourself");
						}

					} catch (Exception e) {
						log.error("package error:{}", e.getMessage());
					} finally {
						readLock.unlock();
					}
				}
			}
		}else {
			// TODO
			// When no candidate nodes generate blocks by themselves
			log.info("start block generation...{}",properties.getServerIp());
		}
	}
	
	/**
	 * Select the next block generation node according to the conditions
	 * @param nodes
	 * @param node
	 * @return
	 */
	public static Node nextGeneratedBlockNode(List<Node> nodes,Node node) { 
		if(null != nodes || nodes.size() > 0) {
			int currentIndex = nodes.indexOf(node);
			if(currentIndex == nodes.size()) {
				 return nodes.get(0) ;
			}
			return nodes.get(currentIndex+1);
		}
		return node;
	} 
	
	/**
	 * Notify the next packing node
	 * @param channelContext
	 * @return
	 * @throws Exception
	 */
	private void send(ClientChannelContext channelContext) throws Exception {
		log.info("send message ip:{},port:{}", channelContext.getServerNode().getIp(),channelContext.getServerNode().getPort());
		HelloPacket packet = new HelloPacket();
		JSONObject json = new JSONObject();
		json.put("ip", channelContext.getServerNode().getIp());
		json.put("port", channelContext.getServerNode().getPort());
		packet.setBody(json.toString().getBytes(HelloPacket.CHARSET));
		
		Tio.send(channelContext, packet);
	}
}

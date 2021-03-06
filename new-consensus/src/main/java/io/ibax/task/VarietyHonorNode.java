package io.ibax.task;

import java.util.Set;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tio.client.ClientChannelContext;
import org.tio.client.TioClient;
import org.tio.client.TioClientConfig;
import org.tio.core.ChannelContext;
import org.tio.utils.lock.SetWithLock;

import io.ibax.mapper.NodeMapper;
import io.ibax.model.Node;
import io.ibax.net.ApplicationContextProvider;
import io.ibax.net.client.ClientContextConfig;
import io.ibax.net.client.HelloClientStarter;

//@Component
public class VarietyHonorNode {
	private static Logger log = LoggerFactory.getLogger(VarietyHonorNode.class);
	
	private Vector<Node> honorNodes = null;
	
//	@Autowired
//	private NodeMapper nodeMapper;
	@Autowired
	private ClientContextConfig clientContextConfig;
	
	@Scheduled(fixedRate = 1000 * 10)
	public void honorNode() {
		SetWithLock<ChannelContext> setWithLock = clientContextConfig.clientTioConfig().connections;
		ReadLock readLock = setWithLock.readLock();
		readLock.lock();
		NodeMapper nodeMapper = ApplicationContextProvider.getApplicationContext().getBean(NodeMapper.class);
		honorNodes = nodeMapper.getNodes(); //Check if a new node has joined
		Vector<Node> newAddHonorNodes = new Vector<Node>();
		try {
			Set<ChannelContext> set = setWithLock.getObj();
			
			for (ChannelContext entry : set) {
				ClientChannelContext channelContext = (ClientChannelContext) entry;
				newAddHonorNodes.add(new Node(channelContext.getServerNode().getIp(),channelContext.getServerNode().getPort()));
			}
			//ClientChannelContext new_channelContext = HelloClientStarter.getTioClient().connect(new org.tio.core.Node("175.24.126.240", 3001));
			honorNodes.removeAll(newAddHonorNodes);
			
			for (Node honorNode : honorNodes) {
				try {
					TioClientConfig clientTioConfig = clientContextConfig.clientTioConfig();
					TioClient aioClient = new TioClient(clientTioConfig);
					aioClient.asynConnect(honorNode);
					nodeMapper.insertNode(honorNode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			readLock.unlock();
		}
	}
}

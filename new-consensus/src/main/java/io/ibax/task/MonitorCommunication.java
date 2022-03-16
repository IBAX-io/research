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
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.client.TioClientConfig;
import org.tio.client.intf.TioClientHandler;
import org.tio.core.ChannelContext;
import org.tio.utils.lock.SetWithLock;

import io.ibax.mapper.NodeMapper;
import io.ibax.model.Node;
import io.ibax.net.base.ActiveStatus;
import io.ibax.net.client.HelloClientAioHandler;
import io.ibax.net.client.HelloClientAioListener;
import io.ibax.net.client.HelloClientStarter;
import io.ibax.net.conf.TioProps;

/**
 * Monitor whether the network of all packaging nodes is smooth
 * @author ak
 *
 */
@Component
public class MonitorCommunication {
	private static Logger log = LoggerFactory.getLogger(MonitorCommunication.class);
	
	private static List<Node> nodes = null;
	
	private static TioClient tioClient = null;
	
	private static ClientChannelContext clientChannelContext = null;
	
	private static TioClientHandler tioClientHandler = new HelloClientAioHandler();
	private static HelloClientAioListener aioListener = new HelloClientAioListener();
	private static ReconnConf reconnConf = new ReconnConf(5000L);
	private static TioClientConfig clientTioConfig = new TioClientConfig(tioClientHandler, aioListener, reconnConf);
	
	@Autowired
	private TioProps properties;
	@Autowired
	private NodeMapper nodeMapper;
	
	@Scheduled(fixedRate = 2000)
	public void monitorNodes(){
		log.info("Monitor all packaging nodes");
		//nodes = nodeMapper.getNodes(); //Check if a new node has joined
		
		SetWithLock<ChannelContext> setWithLock = HelloClientStarter.getClientTioConfig().connecteds;
		
		ReadLock readLock = setWithLock.readLock();
		readLock.lock();
		try {
			Set<ChannelContext> set = setWithLock.getObj();
			for (ChannelContext entry : set) {
				ClientChannelContext channelContext = (ClientChannelContext) entry;
				Node node = new Node(channelContext.getServerNode().getIp(),channelContext.getServerNode().getPort());
				if(channelContext.isClosed) {
					node.setActiveStatus(ActiveStatus.DIE);
					nodeMapper.monitorUpateNote(node);
				}else {
					node.setActiveStatus(ActiveStatus.ACTIVITY);
					nodeMapper.monitorUpateNote(node);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			readLock.unlock();
		}
	}
}

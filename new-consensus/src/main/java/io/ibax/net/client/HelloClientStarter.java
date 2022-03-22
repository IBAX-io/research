package io.ibax.net.client;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.client.ClientChannelContext;
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.client.TioClientConfig;
import org.tio.client.intf.TioClientHandler;
import org.tio.core.Tio;

import io.ibax.mapper.CandidateNodeMapper;
import io.ibax.mapper.NodeMapper;
import io.ibax.model.CandidateNode;
import io.ibax.model.Node;
import io.ibax.net.conf.TioProps;

/**
 * 
 * @author ak
 *
 */
@Component
public class HelloClientStarter{
	private static Logger log = LoggerFactory.getLogger(HelloClientStarter.class);
	
    private static TioClientHandler tioClientHandler = new HelloClientAioHandler();

    private static HelloClientAioListener aioListener = new HelloClientAioListener();

    private static ReconnConf reconnConf = new ReconnConf(5000L);

    public static TioClientConfig clientTioConfig = new TioClientConfig(tioClientHandler, aioListener, reconnConf);

    public static TioClient tioClient = null;
    private static ClientChannelContext clientChannelContext = null;
    
	@Autowired
	private TioProps properties;
	@Autowired
	private NodeMapper nodeMapper;
	@Autowired
	private CandidateNodeMapper candidateNodeMapper;
	
	public void clientStart() {
		try {
			int cpus = Runtime.getRuntime().availableProcessors();
			ExecutorService threadPool = new ThreadPoolExecutor(10, cpus*2, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(10000), Executors.defaultThreadFactory(),
					new ThreadPoolExecutor.DiscardPolicy());
			
			clientTioConfig.setHeartbeatTimeout(properties.getHeartTimeout());
			tioClient = new TioClient(clientTioConfig);
			
			List<CandidateNode> candidateNodes = candidateNodeMapper.getCandidateNodes();
			long start = System.currentTimeMillis();
			for (CandidateNode candidateNode : candidateNodes) {
				if ((System.currentTimeMillis() - start) <= 2000) {
					threadPool.execute(() -> {
						Node node = new Node();
						node.setIp(candidateNode.getIp());
						node.setPort(candidateNode.getPort());
						try {
							clientChannelContext = tioClient.connect(node);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if(null != clientChannelContext) {
							if(!clientChannelContext.isClosed) {
								//Tio.bindGroup(clientChannelContext, properties.getClientGroupName());
								
								Node localNode = nodeMapper.findByIp(candidateNode.getIp());
								if(null == localNode) {
									nodeMapper.insertNode(node);
								}
							}
						}
					});
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static TioClientConfig getClientTioConfig() {
		return clientTioConfig;
	}

	public static TioClient getTioClient() {
		return tioClient;
	}

}

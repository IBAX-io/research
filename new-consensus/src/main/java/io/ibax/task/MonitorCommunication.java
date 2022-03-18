package io.ibax.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
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
import org.tio.core.stat.ChannelStat;
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
	
	private static String networkDelay = "";
	
	private static List<Node> nodes = null;
	
	private static TioClient tioClient = null;
	
	private static Runtime runtime = Runtime.getRuntime();
	
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
		
		SetWithLock<ChannelContext> setWithLock = HelloClientStarter.getClientTioConfig().connections;
		
		ReadLock readLock = setWithLock.readLock();
		readLock.lock();
		try {
			Set<ChannelContext> set = setWithLock.getObj();
			for (ChannelContext entry : set) {
				ClientChannelContext channelContext = (ClientChannelContext) entry;
				ChannelStat stat = channelContext.stat;
		
				Node node = new Node(channelContext.getServerNode().getIp(),channelContext.getServerNode().getPort());
				if(channelContext.isClosed) {
					if(null != stat) {
						node.setLatestTimeOfReceivedPacket(stat.getLatestTimeOfReceivedPacket());
						node.setLatestTimeOfSentPacket(stat.getLatestTimeOfSentPacket());
						node.setTimeFirstConnected(stat.getTimeFirstConnected());
						node.setTimeClosed(stat.getTimeClosed());
						node.setSentBytes(stat.getSentBytes().get());
						node.setReceivedBytes(stat.getReceivedBytes().get());
					}
					node.setActiveStatus(ActiveStatus.DIE);
					
					nodeMapper.monitorUpateNote(node);
				}else {
					Properties props=System.getProperties();
					String osName = props.getProperty("os.name");
					if(osName.toLowerCase().contains("window")) {
						runSystemCommand("ping " + node.getIp(), osName);
					}else if(osName.toLowerCase().contains("linux")){
						runSystemCommand("ping -c 3 " + node.getIp(), osName);
					}
					
					node.setDelay(networkDelay);
					node.setActiveStatus(ActiveStatus.ACTIVITY);
					node.setActiveStatus(ActiveStatus.DIE);
					if(null != stat) {
						node.setLatestTimeOfReceivedPacket(stat.getLatestTimeOfReceivedPacket());
						node.setLatestTimeOfSentPacket(stat.getLatestTimeOfSentPacket());
						node.setTimeFirstConnected(stat.getTimeFirstConnected());
						node.setTimeClosed(stat.getTimeClosed());
						node.setSentBytes(stat.getSentBytes().get());
						node.setReceivedBytes(stat.getReceivedBytes().get());
					}
					
					nodeMapper.monitorUpateNote(node);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			readLock.unlock();
		}
	}
	
	public synchronized static void runSystemCommand(String command,String osName) {
        try {
        	Process proc = runtime.exec(command);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(proc.getInputStream(),"GBK"));
            String s = "";
            StringBuilder sb = new StringBuilder();
            
            if(osName.toLowerCase().contains("window")) {
            	while ((s = inputStream.readLine()) != null) {
                    sb.append(s);
                    log.info(s);
                }
            	int  index= sb.indexOf("平均");
                networkDelay = sb.substring(index+5,sb.length());
                
    		}else if(osName.toLowerCase().contains("linux")) {
    			while ((s = inputStream.readLine()) != null) {
    				 s = s.substring(s.indexOf("time="),s.length());
    				 sb.append(s).append("/");
                }
    			networkDelay = sb.toString();
    		}else {
    			log.info("The current system is not currently supported");
    		}
            //int exitVal = proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            //Runtime.getRuntime().exit(1);
        }finally {
            //Runtime.getRuntime().exit(1);
        	//runtime.exit(1);
        }
    }
}

package io.ibax.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.tio.core.ChannelContext;
import org.tio.core.stat.ChannelStat;
import org.tio.utils.lock.SetWithLock;

import io.ibax.mapper.NodeMapper;
import io.ibax.model.Node;
import io.ibax.net.base.ActiveStatus;
import io.ibax.net.client.HelloClientStarter;

/**
 * Monitor whether the network of all packaging nodes is smooth
 * @author ak
 *
 */
@Component
public class MonitorCommunication {
	private static Logger log = LoggerFactory.getLogger(MonitorCommunication.class);
	
	private static float networkDelay = -1;
	
	private static Runtime runtime = Runtime.getRuntime();

	@Autowired
	private NodeMapper nodeMapper;
	
	@Scheduled(fixedRate = 2000)
	public void monitorNodes(){
		log.info("Monitor all packaging nodes......");
		
		SetWithLock<ChannelContext> setWithLock = HelloClientStarter.getClientTioConfig().connections;
		log.info("Monitor add nodes connections:{}",setWithLock.size());
		
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
						node.setTimeFirstConnected(stat.getTimeFirstConnected()==null?-1:stat.getTimeFirstConnected());
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
					
					if(!"".equals(networkDelay)) {
						node.setDelay(networkDelay);
					}
					if(null != stat) {
						node.setLatestTimeOfReceivedPacket(stat.getLatestTimeOfReceivedPacket());
						node.setLatestTimeOfSentPacket(stat.getLatestTimeOfSentPacket());
						node.setTimeFirstConnected(stat.getTimeFirstConnected()==null?-1:stat.getTimeFirstConnected());
						node.setTimeClosed(stat.getTimeClosed());
						node.setSentBytes(stat.getSentBytes().get());
						node.setReceivedBytes(stat.getReceivedBytes().get());
					}
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
	
	public synchronized static void runSystemCommand(String command,String osName) {
        try {
        	Process proc = runtime.exec(command);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(proc.getInputStream(),"GBK"));
            String s = "";
            StringBuilder sb = new StringBuilder();
            
            if(osName.toLowerCase().contains("window")) {
            	while ((s = inputStream.readLine()) != null) {
                    sb.append(s);
                }
            	if(sb.lastIndexOf("=") > 0) {
                    networkDelay = Float.valueOf(sb.substring(sb.lastIndexOf("=") + 2,sb.length()).replace("ms", ""));
            	}
                
    		}else if(osName.toLowerCase().contains("linux")) {
    			List<Float> speed = new ArrayList<Float>();
    			
    			while ((s = inputStream.readLine()) != null) {
    				log.info("linux ping: {}",s);
    				 s = s.substring(s.indexOf("time="),s.length());
    				 if(s.indexOf("time=") > 0) {
    					s = s.substring(s.indexOf("time=")+5,s.length()).replace(" ms", "");
    	    			speed.add(Float.valueOf(s));
    				 }
                }
    			float sum = 0f;
    			for (Float float1 : speed) {
    				sum = sum +float1;
    			}
    			if(speed.size() > 0) {
    				networkDelay = Math.round(sum/speed.size());
    			}
    			
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

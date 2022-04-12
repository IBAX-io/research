package io.ibax.net.server;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.tio.server.TioServer;
import org.tio.server.TioServerConfig;
import org.tio.server.intf.TioServerHandler;
import org.tio.server.intf.TioServerListener;

import io.ibax.net.client.HelloClientStarter;
import io.ibax.net.conf.TioProps;

/**
 * start the server
 * @author ak
 *
 */
@Component
public class HelloServerStarter implements ApplicationRunner{
	private static Logger log = LoggerFactory.getLogger(HelloServerStarter.class);
	@Autowired
	private TioProps properties;
    
	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("-----------------------start the server--------------------------------");
  		try {
			/*
			 * TioServerHandler aioHandler = new HelloServerAioHandler(); TioServerListener
			 * aioListener = new HelloServerAioListener();
			 */
  			/*
			 * TioServerHandler aioHandler = new HelloServerAioHandler(); TioServerListener
			 * aioListener = new HelloServerAioListener();
			 */
  			TioServerHandler aioHandler = new BlockServerAioHandler(); 
  			TioServerListener aioListener = new BlockServerAioListener();
  			TioServerConfig serverTioConfig = new TioServerConfig("hello-tio-server", aioHandler, aioListener);
  			serverTioConfig.setHeartbeatTimeout(properties.getHeartTimeout());
  			
  			TioServer tioServer = new TioServer(serverTioConfig);
  			log.info("serverIp:{}, serverPort:{}, localIp:{}",properties.getServerIp(), properties.getServerPort(), InetAddress.getLocalHost().getHostAddress());
			tioServer.start("", properties.getServerPort());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}

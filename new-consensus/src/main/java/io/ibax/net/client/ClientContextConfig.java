package io.ibax.net.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tio.client.ReconnConf;
import org.tio.client.TioClientConfig;
import org.tio.client.intf.TioClientHandler;
import org.tio.client.intf.TioClientListener;

import io.ibax.net.conf.TioProps;

@Configuration
public class ClientContextConfig {
	@Autowired
	private TioProps properties;
	
	@Bean
	public TioClientConfig clientTioConfig() {
		/*
		 * TioClientHandler tioClientHandler = new HelloClientAioHandler();
		 * HelloClientAioListener aioListener = new HelloClientAioListener();
		 */
		TioClientHandler tioClientHandler = new BlockClientAioHandler();
		TioClientListener aioListener = new BlockClientAioListener();
		
		ReconnConf reconnConf = new ReconnConf(5000L);
		TioClientConfig clientTioConfig = new TioClientConfig(tioClientHandler, aioListener, reconnConf);
		clientTioConfig.setHeartbeatTimeout(properties.getHeartTimeout());
		
		return clientTioConfig;
	}
}

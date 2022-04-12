package io.ibax.net.client;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.tio.client.TioClientConfig;
import org.tio.core.Tio;

import io.ibax.event.ClientRequestEvent;
import io.ibax.net.ApplicationContextProvider;
import io.ibax.net.conf.TioProps;
import io.ibax.net.packet.BlockPacket;

/**
 * Tool class for sending messages
 * 
 */
@Component
public class PacketSender {
    @Resource
    private TioClientConfig clientTioConfig;
    @Resource
	private TioProps properties;

    public void sendGroup(BlockPacket blockPacket) {
        ApplicationContextProvider.publishEvent(new ClientRequestEvent(blockPacket));
        Tio.sendToGroup(clientTioConfig, properties.getClientGroupName(), blockPacket);
    }
}

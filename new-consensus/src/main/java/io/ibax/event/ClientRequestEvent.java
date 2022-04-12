package io.ibax.event;

import org.springframework.context.ApplicationEvent;

import io.ibax.net.packet.BlockPacket;

public class ClientRequestEvent extends ApplicationEvent {
    public ClientRequestEvent(BlockPacket blockPacket) {
        super(blockPacket);
    }
}

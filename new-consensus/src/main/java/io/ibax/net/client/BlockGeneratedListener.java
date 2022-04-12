package io.ibax.net.client;

import javax.annotation.Resource;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.ibax.block.Block;
import io.ibax.event.AddBlockEvent;
import io.ibax.net.body.RpcSimpleBlockBody;
import io.ibax.net.packet.BlockPacket;
import io.ibax.net.packet.PacketBuilder;
import io.ibax.net.packet.PacketType;

/**
 * After a new block is generated locally, all nodes in the group need to be notified
 * 
 */
@Component
public class BlockGeneratedListener {
    @Resource
    private PacketSender packetSender;

    @Order(2)
    @EventListener(AddBlockEvent.class)
    public void blockGenerated(AddBlockEvent addBlockEvent) {
        Block block = (Block) addBlockEvent.getSource();
        BlockPacket blockPacket = new PacketBuilder<>().setType(PacketType.GENERATE_COMPLETE_REQUEST).setBody(new
                RpcSimpleBlockBody(block.getHash())).build();

        //Broadcast to others for verification
        packetSender.sendGroup(blockPacket);
    }
}

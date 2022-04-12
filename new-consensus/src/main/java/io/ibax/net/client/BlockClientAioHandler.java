package io.ibax.net.client;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.TioClientHandler;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.utils.json.Json;

import io.ibax.event.VoteElectionEvent;
import io.ibax.msg.VoteMsg;
import io.ibax.net.ApplicationContextProvider;
import io.ibax.net.base.AbstractAioHandler;
import io.ibax.net.body.VoteBody;
import io.ibax.net.packet.BlockPacket;
import io.ibax.net.packet.PacketType;

public class BlockClientAioHandler extends AbstractAioHandler implements TioClientHandler {
	private static Logger logger = LoggerFactory.getLogger(BlockClientAioHandler.class);
	private static BlockPacket heartbeatPacket = new BlockPacket();
	
    protected ConcurrentHashMap<String, List<VoteMsg>> voteMsgConcurrentHashMap = new ConcurrentHashMap<>();
	
	@Override
	public Packet heartbeatPacket(ChannelContext channelContext) {
		return heartbeatPacket;
	}

    @Override
    public void handler(Packet packet, ChannelContext channelContext)  {
        BlockPacket blockPacket = (BlockPacket) packet;
        byte[] body = blockPacket.getBody();
		if (body != null) {
			try {
				String jsonStr = new String(body, BlockPacket.CHARSET);
				logger.info("Receive messages from the server：{}", jsonStr);
				
				if(blockPacket.getType()==PacketType.NETWORK_VOTE) {
					logger.info("Verify that the network consensus vote is valid......");
					VoteBody voteBody = Json.toBean(jsonStr, VoteBody.class);
					String hash = voteBody.getVoteMsg().getHash();
			        ApplicationContextProvider.publishEvent(new VoteElectionEvent(voteBody));
			       
				}else if(blockPacket.getType()==PacketType.VOTE) {
					VoteBody voteBody = Json.toBean(jsonStr, VoteBody.class);
					logger.info("Verify that the vote is valid");
					String hash = voteBody.getVoteMsg().getHash();
					if(voteBody.getVoteMsg().isAgree()) {
						
					}
				}else if(blockPacket.getType()==PacketType.GENERATE_BLOCK_REQUEST) {
					logger.info("Verify that the block is valid");
				}
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
    }
    
}

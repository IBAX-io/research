package io.ibax.net.server;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.intf.Packet;
import org.tio.server.intf.TioServerHandler;
import org.tio.utils.json.Json;

import io.ibax.block.Block;
import io.ibax.event.AddBlockEvent;
import io.ibax.net.ApplicationContextProvider;
import io.ibax.net.base.AbstractAioHandler;
import io.ibax.net.body.RpcBlockBody;
import io.ibax.net.body.VoteBody;
import io.ibax.net.packet.BlockPacket;
import io.ibax.net.packet.PacketBuilder;
import io.ibax.net.packet.PacketType;

/**
 * The server-side entry for processing all client requests
 * 
 * 
 */
public class BlockServerAioHandler extends AbstractAioHandler implements TioServerHandler {
	private static Logger logger = LoggerFactory.getLogger(BlockServerAioHandler.class);

	private Vector<Block> blocks = new Vector<Block>();
	
	@Override
	public void handler(Packet packet, ChannelContext channelContext) {
		BlockPacket blockPacket = (BlockPacket) packet;
		byte[] body = blockPacket.getBody();
		if (body != null) {
			try {
				String jsonStr = new String(body, BlockPacket.CHARSET);
				if (blockPacket.getType() == PacketType.NETWORK_VOTE) {
					VoteBody voteBody = Json.toBean(jsonStr, VoteBody.class);
					String hash = voteBody.getVoteMsg().getHash();
					voteBody.getVoteMsg().setAgree(true);
					blockPacket = new PacketBuilder<>().setType(PacketType.NETWORK_VOTE).setBody(voteBody).build();
					 
					Tio.send(channelContext, blockPacket);
				}else if (blockPacket.getType() == PacketType.GENERATE_BLOCK_REQUEST) {
					
					RpcBlockBody rpcBlockBody = Json.toBean(jsonStr, RpcBlockBody.class);
					logger.info("received from<" + rpcBlockBody.getPublicKey() + "><Request to generate Block>message，block information["+ rpcBlockBody.getBlock() + "]");
					
					if(blocks.size() ==0) {
						blocks.add(rpcBlockBody.getBlock());
						logger.info("Receive the latest block height{},latest block：{}",blocks.size(),rpcBlockBody.getBlock().toString());
						ApplicationContextProvider.publishEvent(new AddBlockEvent(rpcBlockBody.getBlock()));
					}
					if(blocks.lastElement().getHash().equals(rpcBlockBody.getBlock().getBlockHeader().getHashPreviousBlock()) && 
							blocks.lastElement().getBlockHeader().getNumber() < rpcBlockBody.getBlock().getBlockHeader().getNumber()) {
						blocks.add(rpcBlockBody.getBlock());
						logger.info("Receive the latest block height{},latest block：{}",blocks.size(),rpcBlockBody.getBlock().toString());
						ApplicationContextProvider.publishEvent(new AddBlockEvent(rpcBlockBody.getBlock()));
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}

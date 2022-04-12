package io.ibax.net.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tio.client.TioClient;
import org.tio.client.TioClientConfig;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.core.Tio;
import org.tio.utils.lock.SetWithLock;

import com.google.common.collect.Maps;

import io.ibax.block.Block;
import io.ibax.block.BlockBody;
import io.ibax.block.BlockHeader;
import io.ibax.block.MerkleTree;
import io.ibax.common.CommonUtil;
import io.ibax.common.Sha256;
import io.ibax.event.AddBlockEvent;
import io.ibax.event.NodesConnectedEvent;
import io.ibax.mapper.CandidateNodeMapper;
import io.ibax.model.CandidateNode;
import io.ibax.msg.VoteMsg;
import io.ibax.net.body.RpcBlockBody;
import io.ibax.net.body.VoteBody;
import io.ibax.net.conf.TioProps;
import io.ibax.net.packet.BlockPacket;
import io.ibax.net.packet.PacketBuilder;
import io.ibax.net.packet.PacketType;

/**
 * 
 * @author ak
 *
 */
@Component
public class HelloClientStarter {
	private static Logger logger = LoggerFactory.getLogger(HelloClientStarter.class);

	public static TioClient tioClient = null;
	//
	private Set<Node> nodes = new HashSet<>();
	private Map<String, Integer> nodesStatus = Maps.newConcurrentMap();
	private static boolean isFinishGenesisPackage = true;
	private Vector<Block> blocks = new Vector<Block>();
	//

	@Resource
	private ClientContextConfig clientContextConfig;
	@Autowired
	private PacketSender packetSender;
	@Autowired
	private TioProps properties;
	@Autowired
	private CandidateNodeMapper candidateNodeMapper;
	
	private static Vector<CandidateNode> candidateNodes;

	@Scheduled(cron = "*/2 * * * * ?")
	public void fetchOtherServer() {
		candidateNodes = candidateNodeMapper.getCandidateNodes(); // Get all candidate nodes on the chain
		logger.info("total" + candidateNodes.size() + "Members need to connect：" + candidateNodes.toString());
		nodes.clear();

		for (CandidateNode candidateNode : candidateNodes) {
			Node serverNodes = new Node(candidateNode.getIp(), candidateNode.getPort());
			nodes.add(serverNodes);
		}
		bindServerGroup(nodes);
	}

	/**
	 The client binds multiple servers here, and multiple servers are a group, 
	 which will be sent to a group when sending messages in the future. 
	 The ip of the server connected here needs to be the same as the one on the chain. 
	 If the server is deleted, the group should also be kicked out here.
	 */
	private void bindServerGroup(Set<Node> serverNodes) {
		TioClientConfig clientTioConfig = clientContextConfig.clientTioConfig();
		// currently connected
		SetWithLock<ChannelContext> setWithLock = Tio.getAll(clientTioConfig);
		Lock lock2 = setWithLock.getLock().readLock();
		lock2.lock();
		try {
			Set<ChannelContext> channelContexts = setWithLock.getObj();
			//A collection of connected nodes
			Set<Node> connectedNodes = channelContexts.stream().map(ChannelContext::getServerNode)
					.collect(Collectors.toSet());

			for (Node node : serverNodes) {
				if (!connectedNodes.contains(node)) {
					connect(node);
				}
			}
			for (ChannelContext channelContext : channelContexts) {
				org.tio.core.Node node = channelContext.getServerNode();
				if (!serverNodes.contains(node)) {
					Tio.remove(channelContext, "Active shutdown" + node.getIp());
				}

			}
		} finally {
			lock2.unlock();
		}

	}

	private void connect(Node serverNode) {
		try {
			TioClientConfig clientTioConfig = clientContextConfig.clientTioConfig();
			TioClient aioClient = new TioClient(clientTioConfig);
			logger.info("start binding: {}", serverNode.toString());
			aioClient.asynConnect(serverNode);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

	@EventListener(NodesConnectedEvent.class)
	public void onConnected(NodesConnectedEvent connectedEvent) {
		ChannelContext channelContext = connectedEvent.getSource();
		Node node = channelContext.getServerNode();
		if (channelContext.isClosed) {
			logger.info("connect" + node.toString() + "fail");
			nodesStatus.put(node.getIp(), -1);
			return;
		} else {
			logger.info("connect" + node.toString() + "success");
			nodesStatus.put(node.getIp(), 1);
			// Binding a group is to use each server node to be connected as a group
			Tio.bindGroup(channelContext, properties.getClientGroupName());
			TioClientConfig clientTioConfig = clientContextConfig.clientTioConfig();

			int csize = Tio.getAll(clientTioConfig).size();
		}
	}

	@EventListener(AddBlockEvent.class)
	public void synBlock(AddBlockEvent addBlockEvent) {
		Lock lock = new ReentrantLock();
		try {
			lock.lock();
			Block block = addBlockEvent.getSource();
			if(blocks.size() ==0) {
				blocks.add(block);
			}
			if(blocks.lastElement().getHash().equals(block.getBlockHeader().getHashPreviousBlock()) && 
					blocks.lastElement().getBlockHeader().getNumber() < block.getBlockHeader().getNumber()) {
				blocks.add(block);
			}
			logger.info("Local block blocks height: size: {}, the current block synchronized is: {}", blocks.size(), addBlockEvent.getSource());
			
		} finally {
			lock.unlock();
		}

	}

	@Scheduled(cron = "*/10 * * * * ?")
	public void electionTask() {
		if(candidateNodes == null) {
			candidateNodes = candidateNodeMapper.getCandidateNodes(); 
		}
		for (CandidateNode candidateNode : candidateNodes) {
			if (candidateNode.getIp().equals(properties.getServerIp())) {
				VoteMsg voteMsg = new VoteMsg();
				voteMsg.setNumber(blocks.size());
				voteMsg.setVoteType(PacketType.NETWORK_VOTE);
				voteMsg.setPublicKey(properties.getPublicKey());
				voteMsg.setHash(Sha256.sha256(voteMsg.toString()));
				VoteBody voteBody = new VoteBody(voteMsg);
				voteBody.setPublicKey(properties.getPublicKey());
				BlockPacket blockPacket = new PacketBuilder<>().setType(PacketType.NETWORK_VOTE).setBody(voteBody)
						.build();

				packetSender.sendGroup(blockPacket);
				break;
			}
		}
		
	}

	@Scheduled(cron = "*/4 * * * * ?")
	public void generatedBlock() {
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Block block = null;
		// 1. If it is a genesis node and the block height is 0, it will be packaged by the genesis node
		if (properties.isGenesisNode() && isFinishGenesisPackage) {
			logger.info("The genesis node begins to package 1: {}", properties.getServerIp());
			isFinishGenesisPackage = false;
			block = addBlock();
			blocks.add(block);
		} else {
			if(candidateNodes == null) {
				candidateNodes = candidateNodeMapper.getCandidateNodes(); 
			}
			// 2. If it is a genesis node, the number of candidate nodes is 0
			if (properties.isGenesisNode() && candidateNodes.size() == 0) {
				logger.info("The genesis node begins to package 2: {}", properties.getServerIp());
				addBlock();
				blocks.add(block);
			} else if (candidateNodes.size() > 0) { // 3. If the number of candidate nodes is greater than 0
				// 4. Judging that the consensus votes of all candidate nodes are 0; if they are all 0, they will be packaged by the genesis node
				if (candidateNodes.stream().filter(s -> s.getVotes() > 0).collect(Collectors.toList()).size() == 0) {
					logger.info("The genesis node begins to package 3: {}", properties.getServerIp());
					block = addBlock();
					blocks.add(block);
				} else {
					if (blocks.size() > 2) {
						logger.info("{}:{}Node tries to compete for packing 6", properties.getServerIp(), properties.getServerPort());
						// The root sign is rounded up, and the corresponding height of the block is intercepted
						List<Block> subBlocks = blocks.subList((int) Math.ceil(Math.sqrt(blocks.size())),
								blocks.size());
						logger.info("intercepted block: {}", subBlocks);

						int size = candidateNodes.size();
						for (Block block2 : subBlocks) {

							for (int i = 0; i < size; i++) {
								if (block2.getBlockHeader().getPublicKey()
										.equals(candidateNodes.get(i).getPublicKey())) {
									candidateNodes.remove(i);
									size = candidateNodes.size();
									i--;
								}
							}
						}

						// The node with the highest number of votes
						if (candidateNodes.size() > 0) {
							CandidateNode candidateNode = candidateNodes.stream().max(Comparator.comparing(CandidateNode::getVotes)).get();
							logger.info("The current packaging node is: {}:{}", candidateNode.getIp(), candidateNode.getPort());
							// If you package yourself for the current package node
							if (properties.getServerIp().equals(candidateNode.getIp()) && properties.getServerPort()==candidateNode.getPort()) {
								logger.info("The packing node is: {}:{} start packing", candidateNode.getIp(),candidateNode.getPort());
								block = addBlock();
								blocks.add(block);
							}
						}else {
							if (properties.isGenesisNode()) {
								logger.info("The genesis node begins to package 5: {}", properties.getServerIp());
								block = addBlock();
								blocks.add(block);
							}
						}
					} else {
						if (properties.isGenesisNode()) {
							logger.info("The genesis node begins to package 4: {}", properties.getServerIp());
							block = addBlock();
							blocks.add(block);
						}
					}
				}
			}
		}
		if (block != null) {
			RpcBlockBody rpcBlockBody = new RpcBlockBody(block);
			rpcBlockBody.setPublicKey(properties.getPrivateKey());
			BlockPacket blockPacket = new PacketBuilder<>().setType(PacketType.GENERATE_BLOCK_REQUEST)
					.setBody(rpcBlockBody).build();

			// Broadcast to others to verify sync blocks
			packetSender.sendGroup(blockPacket);
		}

	}

	public synchronized Block addBlock() {
		BlockHeader blockHeader = new BlockHeader();
		List<String> strs = new ArrayList<String>();
		strs.add(CommonUtil.generateUuid());
		blockHeader.setHashList(strs);
		blockHeader.setHashMerkleRoot(new MerkleTree(strs).build().getRoot());
		blockHeader.setPublicKey(properties.getPublicKey());
		blockHeader.setTimeStamp(CommonUtil.getNow());
		blockHeader.setVersion(1);
		blockHeader.setNumber(blocks.size() + 1);
		if (blocks.size() == 0) {
			blockHeader.setHashPreviousBlock(null);
		} else {
			blockHeader.setHashPreviousBlock(blocks.get(blocks.size() - 1).getHash());
		}

		BlockBody blockBody = new BlockBody();
		Block block = new Block();

		block.setBlockBody(blockBody);
		block.setBlockHeader(blockHeader);
		block.setHash(Sha256.sha256(blockHeader.toString() + blockBody.toString()));

		return block;
	}

}

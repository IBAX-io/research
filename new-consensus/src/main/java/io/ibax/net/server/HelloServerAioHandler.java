package io.ibax.net.server;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.TioConfig;
import org.tio.core.exception.TioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.TioServerHandler;

import com.alibaba.fastjson.JSONObject;

import io.ibax.mapper.NodeMapper;
import io.ibax.model.Node;
import io.ibax.net.ApplicationContextProvider;
import io.ibax.net.base.HelloPacket;
import io.ibax.net.base.NodeStatus;

/**
 * 
 * @author ak
 *
 */
@Component
public class HelloServerAioHandler implements TioServerHandler {
	private static Logger log = LoggerFactory.getLogger(HelloServerAioHandler.class);
	
	/**
	 * decoding
	 */
	@Override
	public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext)
			throws TioDecodeException {
		if (readableLength < HelloPacket.HEADER_LENGHT) {
			return null;
		}
		int bodyLength = buffer.getInt();
	
		if (bodyLength < 0) {
			throw new TioDecodeException(
					"bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getClientNode());
		}
		int neededLength = HelloPacket.HEADER_LENGHT + bodyLength;
		int isDataEnough = readableLength - neededLength;
		if (isDataEnough < 0) {
			return null;
		} else{
			HelloPacket imPacket = new HelloPacket();
			if (bodyLength > 0) {
				byte[] dst = new byte[bodyLength];
				buffer.get(dst);
				imPacket.setBody(dst);
			}
			return imPacket;
		}
	}

	/**
	 * encode
	 */
	@Override
	public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
		HelloPacket helloPacket = (HelloPacket) packet;
		byte[] body = helloPacket.getBody();
		int bodyLen = 0;
		if (body != null) {
			bodyLen = body.length;
		}
		int allLen = HelloPacket.HEADER_LENGHT + bodyLen;
		ByteBuffer buffer = ByteBuffer.allocate(allLen);
		buffer.order(tioConfig.getByteOrder());
		buffer.putInt(bodyLen);
		if (body != null) {
			buffer.put(body);
		}
		return buffer;
	}

	/**
     * process the message
     */
	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		HelloPacket helloPacket = (HelloPacket) packet;
		byte[] body = helloPacket.getBody();
		if (body != null) {
			String message = new String(body, HelloPacket.CHARSET);
			log.info("Received the message:{}",message);
			
			HelloPacket resppacket = new HelloPacket();
			resppacket.setBody(("Got your message, your message is:" + message).getBytes(HelloPacket.CHARSET));
			
			JSONObject clientJson = JSONObject.parseObject(message);
			Node node = new Node();
			node.setIp(clientJson.getString("ip"));
			node.setPort(clientJson.getInteger("port"));
			node.setNodeStatus(NodeStatus.LEADER);
			NodeMapper nodeMapper = ApplicationContextProvider.getApplicationContext().getBean(NodeMapper.class);
			nodeMapper.updateNextPackageNode(node);
			
			Tio.send(channelContext, resppacket);
		}
		return;

	}

}

package io.ibax.net.client;

import java.nio.ByteBuffer;

import org.tio.client.intf.TioClientHandler;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.exception.TioDecodeException;
import org.tio.core.intf.Packet;

import io.ibax.net.base.HelloPacket;

/**
 * 
 * @author ak
 *
 */
public class HelloClientAioHandler implements TioClientHandler {

	private static HelloPacket heartbeatPacket = new HelloPacket();

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

	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		HelloPacket helloPacket = (HelloPacket) packet;
		byte[] body = helloPacket.getBody();
		if (body != null) {
			String message = new String(body, HelloPacket.CHARSET);
			System.out.println("Received the message：" + message);
		}

		return;
	}

	@Override
	public Packet heartbeatPacket(ChannelContext channelContext) {
		return heartbeatPacket;
	}

}

package io.ibax.net.base;

import java.nio.ByteBuffer;

import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.exception.TioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.core.intf.TioHandler;

import io.ibax.net.packet.BlockPacket;

public abstract class AbstractAioHandler implements TioHandler {
  
    @Override
    public BlockPacket decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws TioDecodeException {
        if (readableLength < BlockPacket.HEADER_LENGTH) {
            return null;
        }

        byte type = buffer.get();

        int bodyLength = buffer.getInt();

        if (bodyLength < 0) {
            throw new TioDecodeException("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext
					.getClientNode());
        }

        int neededLength = BlockPacket.HEADER_LENGTH + bodyLength;
        int test = readableLength - neededLength;
        if (test < 0) {
            return null;
        }
        BlockPacket imPacket = new BlockPacket();
        imPacket.setType(type);
        if (bodyLength > 0) {
            byte[] dst = new byte[bodyLength];
            buffer.get(dst);
            imPacket.setBody(dst);
        }
        return imPacket;
    }

    @Override
    public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
        BlockPacket showcasePacket = (BlockPacket) packet;
        byte[] body = showcasePacket.getBody();
        int bodyLen = 0;
        if (body != null) {
            bodyLen = body.length;
        }

        int allLen = BlockPacket.HEADER_LENGTH + bodyLen;

        ByteBuffer buffer = ByteBuffer.allocate(allLen);
        buffer.order(tioConfig.getByteOrder());

        buffer.put(showcasePacket.getType());
        buffer.putInt(bodyLen);

        if (body != null) {
            buffer.put(body);
        }
        return buffer;
    }
}

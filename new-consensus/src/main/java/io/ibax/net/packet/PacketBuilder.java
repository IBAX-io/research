package io.ibax.net.packet;

import org.tio.utils.json.Json;

import io.ibax.net.body.BaseBody;

public class PacketBuilder<T extends BaseBody> {
    
    private byte type;

    private T body;

    public PacketBuilder<T> setType(byte type) {
        this.type = type;
        return this;
    }

    public PacketBuilder<T> setBody(T body) {
        this.body = body;
        return this;
    }

    public BlockPacket build() {
        return new BlockPacket(type, Json.toJson(body));
    }
}

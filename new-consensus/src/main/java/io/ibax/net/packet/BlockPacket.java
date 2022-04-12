package io.ibax.net.packet;

import java.io.UnsupportedEncodingException;

import org.tio.core.intf.Packet;

public class BlockPacket extends Packet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2786631442211716686L;
	public static final int HEADER_LENGTH = 5;
    public static final String CHARSET = "utf-8";
    private byte type;
    private byte[] body;
    public BlockPacket() {
        super();
    }

    /**
     * @param type type
     * @param body body
     * @author tanyaowu
     */
    public BlockPacket(byte type, byte[] body) {
        super();
        this.type = type;
        this.body = body;
    }

    public BlockPacket(byte type, String body) {
        super();
        this.type = type;
        setBody(body);
    }

    /**
     * @return the body
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * @return the type
     */
    public byte getType() {
        return type;
    }

    @Override
    public String logstr() {
        return "" + type;
    }

    /**
     * @param body
     *         the body to set
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setBody(String body) {
        try {
            this.body = body.getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param type
     *         the type to set
     */
    public void setType(byte type) {
        this.type = type;
    }
}

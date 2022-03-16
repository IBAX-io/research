package io.ibax.net.base;

import org.tio.core.intf.Packet;

/**
 * 
 * @author ak
 *
 */
public class HelloPacket extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5553564559055896765L;
	
	public static final int HEADER_LENGHT = 4;//length of message header
    public static final String CHARSET = "utf-8";
    private byte[] body;

    /**
     * @return the body
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(byte[] body) {
        this.body = body;
    }
}

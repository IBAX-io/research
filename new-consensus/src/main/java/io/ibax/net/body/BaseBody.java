package io.ibax.net.body;

import io.ibax.common.CommonUtil;

public class BaseBody {

	/**
	 * message sending time
	 */
	private Long time = System.currentTimeMillis();
    /**
     * Unique ID for each message
     */
	private String messageId = CommonUtil.generateUuid();
    /**
     * Which message did you reply to
     */
	private String responseMsgId;
    /**
     * publicKey
     */
	private String publicKey;
	
    public BaseBody() {
    }

    /**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Long time) {
		this.time = time;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

    public String getResponseMsgId() {
        return responseMsgId;
    }

    public void setResponseMsgId(String responseMsgId) {
        this.responseMsgId = responseMsgId;
    }

    public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public String toString() {
		return "BaseBody [time=" + time + ", messageId=" + messageId + ", responseMsgId=" + responseMsgId
				+ ", publicKey=" + publicKey + "]";
	}
}

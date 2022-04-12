package io.ibax.common;

import com.alibaba.fastjson.JSONObject;

public class TrustSDKException extends Exception {
	
	private static final long serialVersionUID = -4214831807802264420L;
	
	protected String rtnCd;
	protected String rtnMsg;
	
	public TrustSDKException(String rtnCd, String rtnMsg) {
		super(rtnMsg);
		this.rtnCd = rtnCd;
		this.rtnMsg = rtnMsg;
	}
	
	public TrustSDKException(String rtnCd, String rtnMsg, Throwable t) {
		super(rtnMsg, t);
		this.rtnCd = rtnCd;
		this.rtnMsg = rtnMsg;
	}
	
	public String getRtnCd() {
		return rtnCd;
	}

	public void setRtnCd(String rtnCd) {
		this.rtnCd = rtnCd;
	}

	public String getRtnMsg() {
		return rtnMsg;
	}

	public void setRtnMsg(String rtnMsg) {
		this.rtnMsg = rtnMsg;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}


package io.ibax.common.exception;

public enum ErrorNum {

	INVALID_PARAM_ERROR("001", "Parameter error"),
	DES3_ENCRYPT_ERROR("002", "DES3 encryption and decryption error"),
	AES_ENCRYPT_ERROR("003", "AES encryption and decryption error"),
	ECDSA_ENCRYPT_ERROR("004", "ECDSA encryption and decryption error"),
	SIGN_ERROR("005", "Signature error"),
	GENERATE_SIGN_ERROR("006", "generate signature error"),
	GENERATE_SQL_ERROR("007", "Generate SQL errors"),
	VERIFY_SIGN_ERROR("008", "Verify signature error");
	
	private String retCode;
	private String retMsg;
	
	ErrorNum(String retCode, String retMsg) {
		this.retCode = retCode;
		this.retMsg = retMsg;
	}
	
	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
}


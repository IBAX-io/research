package io.ibax.common.algorithm;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESAlgorithm {

	/**
	 * aesEncode:aes encryption. <br/>
	 *
	 * @param key
	 *            Secret key
	 * @param data
	 *            plaintext
	 */
	public static byte[] aesEncode(byte[] key, byte[] data) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(data);
	}

	/**
	 * aesDecode: aes decrypt. <br/>
	 *
	 * @param key key
	 * @param encryptedText  encryptedText
	 * @return encryptedText
	 * @throws Exception Exception
	 */
	public static byte[] aesDecode(byte[] key, byte[] encryptedText) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(encryptedText);
	}

}

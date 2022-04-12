package io.ibax.common.algorithm;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.Security;

public class BaseAlgorithm {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * encode bytes
	 * 
	 * @param algorithm  algorithm
	 * @param data  data
	 * @return byte[]
	 */
	public static byte[] encode(String algorithm, byte[] data) {
		if (data == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(data);
			return messageDigest.digest();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * encodeTwice bytes
	 *
	 * @param algorithm  algorithm
	 * @param data data
	 * @return byte[]
	 */
	protected static byte[] encodeTwice(String algorithm, byte[] data) {
		if (data == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(data);
			return messageDigest.digest(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

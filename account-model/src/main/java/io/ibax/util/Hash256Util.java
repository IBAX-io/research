package io.ibax.util;

import static org.apache.commons.codec.binary.Hex.encodeHexString;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash256Util {
	public static String hash256(String str) {
		String hash = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash256 = messageDigest.digest(str.getBytes());
			hash = encodeHexString(hash256);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return hash;
	}

	public static String hash256(byte[] input) {
		String hash = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash256 = messageDigest.digest(input);
			hash = encodeHexString(hash256);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hash;
	}
}

package io.ibax.common;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;

import io.ibax.block.PairKey;
import io.ibax.common.algorithm.ECDSAAlgorithm;
import io.ibax.common.exception.ErrorNum;

public class TrustSDK {

	/**
	 * generatePairKey:Generate a pair of public and private keys, and return. <br/>
	 * 
	 * @return Return public and private key pair
	 * @throws TrustSDKException
     * TrustSDKException
	 */
	public static PairKey generatePairKey() throws TrustSDKException {
		return generatePairKey(false);
	}
	
	/**
	 * generatePairKey:Generate private key public key pair. <br/>
	 *
	 * @param encodePubKey  Whether to compress
	 * @return PairKey
	 * @throws TrustSDKException
     * TrustSDKException
	 */
	public static PairKey generatePairKey(boolean encodePubKey) throws TrustSDKException {
		try {
			PairKey pair = new PairKey();
			String privateKey = ECDSAAlgorithm.generatePrivateKey();
			String pubKey = ECDSAAlgorithm.generatePublicKey(privateKey.trim(), encodePubKey);
			pair.setPrivateKey(privateKey);
			pair.setPublicKey(pubKey);
			return pair;
		} catch (Exception e) {
			throw new TrustSDKException(ErrorNum.ECDSA_ENCRYPT_ERROR.getRetCode(), ErrorNum.ECDSA_ENCRYPT_ERROR.getRetMsg(), e);
		}
	}
	
	/**
	 * checkPairKey:Verify that a pair of public and private keys match. <br/>
	 *
	 * @author ronyyang
	 * @param prvKey 
	 * @param pubKey 
	 * @return 
	 * @throws TrustSDKException TrustSDKException
	 */
	public static boolean checkPairKey(String prvKey, String pubKey) throws TrustSDKException {
		if (StringUtils.isEmpty(prvKey) || StringUtils.isEmpty(pubKey)) {
			throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
		}
		try {
			String correctPubKey = ECDSAAlgorithm.generatePublicKey(prvKey.trim(), true);
            return pubKey.trim().equals(correctPubKey);
        } catch(Exception e) {
			throw new TrustSDKException(ErrorNum.ECDSA_ENCRYPT_ERROR.getRetCode(), ErrorNum.ECDSA_ENCRYPT_ERROR.getRetMsg(), e);
		}
	}
	
	/**
	 * generatePubkeyByPrvkey: Calculate the corresponding public key from the private key. <br/>
	 *
	 * @param privateKey
	 * @param encode
	 * @return 
	 * @throws TrustSDKException
	 * TrustSDKException
	 */
	public static String generatePubkeyByPrvkey(String privateKey, boolean encode) throws TrustSDKException {
		if (StringUtils.isEmpty(privateKey)) {
			throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
		}
		try {
            return ECDSAAlgorithm.generatePublicKey(privateKey, encode);
		} catch (Exception e) {
			throw new TrustSDKException(ErrorNum.ECDSA_ENCRYPT_ERROR.getRetCode(), ErrorNum.ECDSA_ENCRYPT_ERROR.getRetMsg(), e);
		}
	}

	/**
	 * generatePubkeyByPrvkey:Calculate the corresponding public key from the private key. <br/>
	 *
	 * @param privateKey
	 * @return 
	 * @throws TrustSDKException TrustSDKException
	 */
	public static String generatePubkeyByPrvkey(String privateKey) throws TrustSDKException {
		return generatePubkeyByPrvkey(privateKey, false);
	}
	
	public static String decodePubkey(String encodePubKey) throws TrustSDKException {
		if (StringUtils.isEmpty(encodePubKey)) {
			throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
		}
		try {
            return ECDSAAlgorithm.decodePublicKey(encodePubKey);
		} catch (Exception e) {
			throw new TrustSDKException(ErrorNum.ECDSA_ENCRYPT_ERROR.getRetCode(), ErrorNum.ECDSA_ENCRYPT_ERROR.getRetMsg(), e);
		}
	}

	/**
	 * generateAddrByPubkey:Get the corresponding address from the public key. <br/>
	 *
	 * @author Rony
	 * @param pubKey
	 * @return address
	 * @throws TrustSDKException
     * TrustSDKException
	 */
	public static String generateAddrByPubkey(String pubKey) throws TrustSDKException {
		if (StringUtils.isEmpty(pubKey)) {
			throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
		}
		try {
            return ECDSAAlgorithm.getAddress(Base64.decodeBase64(pubKey));
		} catch (Exception e) {
			throw new TrustSDKException(ErrorNum.ECDSA_ENCRYPT_ERROR.getRetCode(), ErrorNum.ECDSA_ENCRYPT_ERROR.getRetMsg(), e);
		}
	}

	/**
	 * generateAddrByPrvkey:Calculate the corresponding address from the private key. <br/>
	 *
	 * @param privateKey
	 * @return Address
	 * @throws TrustSDKException TrustSDKException
	 */
	public static String generateAddrByPrvkey(String privateKey) throws TrustSDKException {
		if (StringUtils.isEmpty(privateKey)) {
			throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
		}
		try {
			String pubKey = ECDSAAlgorithm.generatePublicKey(privateKey);
            return generateAddrByPubkey(pubKey);
		} catch (Exception e) {
			throw new TrustSDKException(ErrorNum.ECDSA_ENCRYPT_ERROR.getRetCode(), ErrorNum.ECDSA_ENCRYPT_ERROR.getRetMsg(), e);
		}
	}

	/**
	 * signString:Sign a string and return the signature. <br/>
	 *
	 * @author Rony
	 * @param privateKey
	 * @param data
	 * @return 
	 * @throws TrustSDKException TrustSDKException
	 */
	public static String signString(String privateKey, byte[] data) throws TrustSDKException {
		if (StringUtils.isEmpty(privateKey)) {
			throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
		}
		try {
            return ECDSAAlgorithm.sign(privateKey, data);
		} catch (Exception e) {
			throw new TrustSDKException(ErrorNum.SIGN_ERROR.getRetCode(), ErrorNum.SIGN_ERROR.getRetMsg(), e);
		}
	}

	public static String signString(String privateKey, String data) throws TrustSDKException, UnsupportedEncodingException {
		return signString(privateKey, data.getBytes("UTF-8"));
	}

	/**
	 * verifyString:Verify that a signature is valid. <br/>
	 *
	 * @param pubKey
	 * @param srcString
	 * @param sign
	 * @return 
	 * @throws TrustSDKException TrustSDKException
	 */
	public static boolean verifyString(String pubKey, String srcString, String sign) throws TrustSDKException {
		if (StringUtils.isEmpty(pubKey) || StringUtils.isEmpty(srcString) || StringUtils.isEmpty(sign)) {
			throw new TrustSDKException(ErrorNum.INVALID_PARAM_ERROR.getRetCode(), ErrorNum.INVALID_PARAM_ERROR.getRetMsg());
		}
		try {
			return ECDSAAlgorithm.verify(srcString, sign, pubKey);
		} catch (Exception e) {
			throw new TrustSDKException(ErrorNum.ECDSA_ENCRYPT_ERROR.getRetCode(), ErrorNum.ECDSA_ENCRYPT_ERROR.getRetMsg(), e);
		}
	}


}

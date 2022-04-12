package io.ibax.common.algorithm;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.Key;

public class DESAlgorithm {
    /**
     * key algorithm
     * */
    public static final String KEY_ALGORITHM = "DESede";

    /**
     * Encryption/Decryption Algorithm/Working Mode/Padding
     * */
    public static final String CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";

    /**
     * Convert key
     * 
     * @param key
     *            binary key
     * @return Key
     * */
    public static Key toKey(byte[] key) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generateSecret(dks);
    }

    /**
     * encrypted data
     * 
     * @param data
     *            
     * @param key
     *            
     * @return byte[] 
     * */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        return cipher.doFinal(data);
    }

    /**
     * decrypt data
     * 
     * @param data
     *            
     * @param key
     *            
     * @return byte[] 
     * */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        return cipher.doFinal(data);
    }
}


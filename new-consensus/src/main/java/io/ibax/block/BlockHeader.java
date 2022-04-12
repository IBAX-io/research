package io.ibax.block;

import java.util.List;

/**
 * block header
 * 
 */
public class BlockHeader {
    /**
     * version number
     */
    private int version;
    /**
     * the hash of the previous block
     */
    private String hashPreviousBlock;
    /**
     * merkle tree root node hash
     */
    private String hashMerkleRoot;
    /**
     * Generate the public key for the block
     */
    private String publicKey;
    /**
     * block serial number
     */
    private int number;
    /**
     * timestamp
     */
    private long timeStamp;
    /**
     * 32-bit random number
     */
    private long nonce;
    /**
     * The hash set of each transaction information in the block is in order, 
     * and the root node hash can be calculated through the hash set
     */
    private List<String> hashList;

    @Override
    public String toString() {
        return "BlockHeader{" +
                "version=" + version +
                ", hashPreviousBlock='" + hashPreviousBlock + '\'' +
                ", hashMerkleRoot='" + hashMerkleRoot + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", number=" + number +
                ", timeStamp=" + timeStamp +
                ", nonce=" + nonce +
                ", hashList=" + hashList +
                '}';
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getHashPreviousBlock() {
        return hashPreviousBlock;
    }

    public void setHashPreviousBlock(String hashPreviousBlock) {
        this.hashPreviousBlock = hashPreviousBlock;
    }

    public String getHashMerkleRoot() {
        return hashMerkleRoot;
    }

    public void setHashMerkleRoot(String hashMerkleRoot) {
        this.hashMerkleRoot = hashMerkleRoot;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public List<String> getHashList() {
        return hashList;
    }

    public void setHashList(List<String> hashList) {
        this.hashList = hashList;
    }
}

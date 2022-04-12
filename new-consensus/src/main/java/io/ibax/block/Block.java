package io.ibax.block;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * block
 * 
 */
public class Block {
    /**
     * block header
     */
    private BlockHeader blockHeader;
    /**
     * block body
     */
    private BlockBody blockBody;
    /**
     * the hash of the block
     */
    private String hash;

    /**
     * Calculate sha256 based on all attributes of the block
     * @return
     * sha256hex
     */
    private String calculateHash() {
        return DigestUtil.sha256Hex(
                        blockHeader.toString() + blockBody.toString()
        );
    }

    public BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public void setBlockHeader(BlockHeader blockHeader) {
        this.blockHeader = blockHeader;
    }

    public BlockBody getBlockBody() {
        return blockBody;
    }

    public void setBlockBody(BlockBody blockBody) {
        this.blockBody = blockBody;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Block{" +
                "blockHeader=" + blockHeader +
                ", blockBody=" + blockBody +
                ", hash='" + hash + '\'' +
                '}';
    }
}

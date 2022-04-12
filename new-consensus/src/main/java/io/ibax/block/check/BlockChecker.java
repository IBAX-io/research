package io.ibax.block.check;

import io.ibax.block.Block;

/**
 * block check
 * 
 */
public interface BlockChecker {
    /**
     * Compare the target block with its own local block num size
     * @param block
     * Blocks being compared
     * @return
     * Difference between local and target block
     */
    int checkNum(Block block);

    /**
     * Check whether the permissions for operations in the block are legal
     * @param block
     * block
     * @return
     * greater than 0 is legal
     */
    int checkPermission(Block block);

    /**
     * Check hash, including prevHash, internal hash（merkle tree root hash）
     * @param block
     * block
     * @return
     * greater than 0 is legal
     */
    int checkHash(Block block);

    /**
     * Check generation time
     * @param block  block
     * @return block
     */
    int checkTime(Block block);
    
    /**
     * check signature
     * @param block  block
     * @return block
     */
    int checkSign(Block block);
    
    /**
     * Verify blocks, including signatures, hashes, and associations
     * @param block
     * @return
     */
    public String checkBlock(Block block);
    
}

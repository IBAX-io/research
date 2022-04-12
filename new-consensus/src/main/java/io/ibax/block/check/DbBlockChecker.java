package io.ibax.block.check;

import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;
import io.ibax.block.Block;
import io.ibax.common.Sha256;
import io.ibax.net.body.BlockRequestBody;

/**
 * Use the locally stored permissions and block information to verify the new block
 * @author 
 */
@Component
public class DbBlockChecker implements BlockChecker {
  

    @Override
    public int checkNum(Block block) {
        Block localBlock = getLastBlock();
        int localNum = 0;
        if (localBlock != null) {
            localNum = localBlock.getBlockHeader().getNumber();
        }
        //Only agree when the local block + 1 is equal to the new block
        if (localNum + 1 == block.getBlockHeader().getNumber()) {
            //Agree to generate blocks
            return 0;
        }

        //Refuse
        return -1;
    }

    @Override
    public int checkPermission(Block block) {
        //Verify the operation permissions on the table
        return 0;
    }

    @Override
    public int checkHash(Block block) {
        Block localLast = getLastBlock();
        //The genesis block can, or the prev of the new block is equal to the local last hash.
        if (localLast == null && block.getBlockHeader().getHashPreviousBlock() == null) {
            return 0;
        }
        if (localLast != null && StrUtil.equals(localLast.getHash(), block.getBlockHeader().getHashPreviousBlock())) {
            return 0;
        }
        return -1;
    }

    @Override
    public int checkTime(Block block) {
        Block localBlock = getLastBlock();
        //The generation time of new blocks is smaller than the local ones
        if (localBlock != null && block.getBlockHeader().getTimeStamp() <= localBlock.getBlockHeader().getTimeStamp()) {
            //Refuse
            return -1;
        }
        return 0;
    }
    
    @Override
    public int checkSign(Block block) {
    	if(!checkBlockHashSign(block)) {
    		return -1;
    	}
    	return 0;
    }

    private Block getLastBlock() {
        return new Block();
    }
    
    public String checkBlock(Block block) {
    	if(!checkBlockHashSign(block)) return block.getHash();
    	
    	String preHash = block.getBlockHeader().getHashPreviousBlock();
    	if(preHash == null) return null;
    	
    	Block preBlock = new Block();
    	if(preBlock == null) return block.getHash();
    	
		int localNum = preBlock.getBlockHeader().getNumber();
        //Only agree when the current block + 1 is equal to the next block
        if (localNum + 1 != block.getBlockHeader().getNumber()) {
            return block.getHash();
        }
        if(block.getBlockHeader().getTimeStamp() <= preBlock.getBlockHeader().getTimeStamp()) {
        	return block.getHash();
        }
    	
    		
    	return null;
    }

    /**
     * Check whether the block signature and hash match
     * @param block
     * @return
     */
	private boolean checkBlockHashSign(Block block) {
		BlockRequestBody blockRequestBody = new BlockRequestBody();
		blockRequestBody.setBlockBody(block.getBlockBody());
		blockRequestBody.setPublicKey(block.getBlockHeader().getPublicKey());
		
		String hash = Sha256.sha256(block.getBlockHeader().toString() + block.getBlockBody().toString());
		if(!StrUtil.equals(block.getHash(),hash)) return false;
		
		return true;
	}
    
}

package io.ibax.block.check;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import io.ibax.block.Block;
import io.ibax.net.body.RpcCheckBlockBody;

/**
 * block check
 * 
 */
@Component
public class CheckerManager {
    @Resource
    private BlockChecker blockChecker;

    /**
     * Basic check
     * @param block block
     * @return Check result
     */
    public RpcCheckBlockBody check(Block block) {
    	int code= blockChecker.checkSign(block);
    	if (code != 0) {
            return new RpcCheckBlockBody(-1, "The signature of the block is invalid");
        }
    	
        int number = blockChecker.checkNum(block);
        if (number != 0) {
             return new RpcCheckBlockBody(-1, "The number of block is illegal");
        }
        int time = blockChecker.checkTime(block);
        if (time != 0) {
            return new RpcCheckBlockBody(-4, "block time error");
        }
        int hash = blockChecker.checkHash(block);
        if (hash != 0) {
            return new RpcCheckBlockBody(-3, "hash check failed");
        }
        int permission = blockChecker.checkPermission(block);
        if (permission != 0) {
            return new RpcCheckBlockBody(-2, "No permission to operate on the table");
        }

        return new RpcCheckBlockBody(0, "OK", block);
    }

}

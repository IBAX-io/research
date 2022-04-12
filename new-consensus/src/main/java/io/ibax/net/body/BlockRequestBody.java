package io.ibax.net.body;

import io.ibax.block.BlockBody;

public class BlockRequestBody {
    private String publicKey;
    private BlockBody blockBody;

    @Override
    public String toString() {
        return "BlockRequestBody{" +
                "publicKey='" + publicKey + '\'' +
                ", blockBody=" + blockBody +
                '}';
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public BlockBody getBlockBody() {
        return blockBody;
    }

    public void setBlockBody(BlockBody blockBody) {
        this.blockBody = blockBody;
    }
}

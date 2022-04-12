package io.ibax.net.body;

import io.ibax.block.Block;

/**
 * Check whether the block is legal, agree or reject the block generation request
 * 
 */
public class RpcCheckBlockBody extends RpcBlockBody {
    private int code;
    private String message;
    public RpcCheckBlockBody() {
    }

    public RpcCheckBlockBody(int code, String message) {
        this(code, message, null);
    }

    public RpcCheckBlockBody(int code, String message, Block block) {
        super(block);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RpcCheckBlockBody{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}

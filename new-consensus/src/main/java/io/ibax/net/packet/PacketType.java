package io.ibax.net.packet;

public interface PacketType {
   
    /**
     * A new block has been generated
     */
    byte GENERATE_COMPLETE_REQUEST = 1;
    /**
     * request to generate block
     */
    byte GENERATE_BLOCK_REQUEST = 2;
    /**
     * vote
     */
    byte VOTE = 10;
    
    /**
     * Newsletter Poll
     */
    byte NETWORK_VOTE = 15;
}

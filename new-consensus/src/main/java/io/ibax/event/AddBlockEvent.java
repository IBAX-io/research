package io.ibax.event;

import org.springframework.context.ApplicationEvent;

import io.ibax.block.Block;

public class AddBlockEvent extends ApplicationEvent {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6389894605791581644L;

	public AddBlockEvent(Block block) {
        super(block);
    }
    
    public Block getSource() {
        return (Block) source;
    }
}
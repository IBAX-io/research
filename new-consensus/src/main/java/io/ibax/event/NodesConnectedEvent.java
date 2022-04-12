package io.ibax.event;

import org.springframework.context.ApplicationEvent;
import org.tio.core.ChannelContext;

public class NodesConnectedEvent extends ApplicationEvent {
	private static final long serialVersionUID = 526755692642414178L;

	public NodesConnectedEvent(ChannelContext channelContext) {
        super(channelContext);
    }
	
	public ChannelContext getSource() {
        return (ChannelContext) source;
    }
	
}

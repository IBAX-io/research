package io.ibax.net.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.ibax.event.VoteElectionEvent;

@Component
public class VoteElectionListener {
	private static Logger logger = LoggerFactory.getLogger(VoteElectionListener.class);
	
	@EventListener(VoteElectionEvent.class)
	public void blockGenerated(VoteElectionEvent addBlockEvent) {
		
		logger.info("poll monitoring:{}",addBlockEvent.getSource().toString());
    }
	
}

package io.ibax.event;

import org.springframework.context.ApplicationEvent;

public class VoteElectionEvent extends ApplicationEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -836584458123015956L;
	
	public VoteElectionEvent(Object source) {
		super(source);
	}

}

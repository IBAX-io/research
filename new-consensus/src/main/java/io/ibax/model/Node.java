package io.ibax.model;

import java.io.Serializable;

/**
 * 
 * @author AK
 *
 */
public class Node extends org.tio.core.Node implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5976689302132441688L;
	private int id;
	private int activeStatus; // 0:offline 1:online
	
	private int nodeStatus; // FOLLOWER(0), CANDIDATE(1), LEADER(2);
	
	private Node self;

	public Node(String ip, int port) {
		super(ip, port);
	}

	public Node() {
		super(null, 0);
	}

	public int getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(int activeStatus) {
		this.activeStatus = activeStatus;
	}

	public int getNodeStatus() {
		return nodeStatus;
	}

	public void setNodeStatus(int nodeStatus) {
		this.nodeStatus = nodeStatus;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Node getSelf() {
		return self;
	}

	public void setSelf(Node self) {
		this.self = self;
	}
	
}

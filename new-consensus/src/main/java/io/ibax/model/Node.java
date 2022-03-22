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
	private float delay;
	private Node self;
	private long latestTimeOfReceivedPacket; 
	private long latestTimeOfSentPacket; 
	private long timeFirstConnected; 
	private long timeClosed;
	private long sentBytes;
	private long receivedBytes;
	
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

	public float getDelay() {
		return delay;
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}

	public long getLatestTimeOfReceivedPacket() {
		return latestTimeOfReceivedPacket;
	}

	public void setLatestTimeOfReceivedPacket(long latestTimeOfReceivedPacket) {
		this.latestTimeOfReceivedPacket = latestTimeOfReceivedPacket;
	}

	public long getLatestTimeOfSentPacket() {
		return latestTimeOfSentPacket;
	}

	public void setLatestTimeOfSentPacket(long latestTimeOfSentPacket) {
		this.latestTimeOfSentPacket = latestTimeOfSentPacket;
	}

	public long getTimeFirstConnected() {
		return timeFirstConnected;
	}

	public void setTimeFirstConnected(long timeFirstConnected) {
		this.timeFirstConnected = timeFirstConnected;
	}

	public long getTimeClosed() {
		return timeClosed;
	}

	public void setTimeClosed(long timeClosed) {
		this.timeClosed = timeClosed;
	}

	public long getSentBytes() {
		return sentBytes;
	}

	public void setSentBytes(long sentBytes) {
		this.sentBytes = sentBytes;
	}

	public long getReceivedBytes() {
		return receivedBytes;
	}

	public void setReceivedBytes(long receivedBytes) {
		this.receivedBytes = receivedBytes;
	}
	
}

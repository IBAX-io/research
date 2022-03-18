package io.ibax.model;

import java.util.Date;

/**
 * candidate node
 * @author ak
 *
 */
public class CandidateNode {
	private int id;
	private String ip;
	private int	port;
	private int votes;
	private int black;
	private Date createTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}
	public int getBlack() {
		return black;
	}
	public void setBlack(int black) {
		this.black = black;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "CandidateNode [id=" + id + ", ip=" + ip + ", port=" + port + ", votes=" + votes + ", black=" + black
				+ ", createTime=" + createTime + "]";
	}

}

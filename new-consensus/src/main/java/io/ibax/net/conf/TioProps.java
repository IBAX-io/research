package io.ibax.net.conf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.ibax.model.Node;

/**
 * configuration information
 * @author AK
 *
 */
@Component
@ConfigurationProperties("tio")
public class TioProps {

	/**
	 * Heartbeat packet interval
	 */
	private int heartTimeout;
	
	/**
	 * Client group name
	 */
	private String clientGroupName;

	/**
	 * Server-side grouping context name
	 */
	private String serverGroupContextName;

	/**
	 * Server listening port
	 */
	private int serverPort;

	/**
	 * server-bound ip
	 */
	private String serverIp;

	private LinkedHashMap<String, Object> nodes;

	public int getHeartTimeout() {
		return heartTimeout;
	}

	public void setHeartTimeout(int heartTimeout) {
		this.heartTimeout = heartTimeout;
	}

	public String getClientGroupName() {
		return clientGroupName;
	}

	public void setClientGroupName(String clientGroupName) {
		this.clientGroupName = clientGroupName;
	}

	public String getServerGroupContextName() {
		return serverGroupContextName;
	}

	public void setServerGroupContextName(String serverGroupContextName) {
		this.serverGroupContextName = serverGroupContextName;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public List<Node> getNodes() {
		if (null == nodes) {
			return null;
		}
		ArrayList<Node> nodeList = new ArrayList<>();
		Iterator<Map.Entry<String, Object>> iterator= nodes.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry entry = iterator.next();
			Map value = (Map) entry.getValue();
			nodeList.add(new Node(value.get("ip").toString(), Integer.valueOf(value.get("port").toString())));
		}
		return nodeList;
	}

	public void setNodes(LinkedHashMap<String, Object> nodes) {
		this.nodes = nodes;
	}
}

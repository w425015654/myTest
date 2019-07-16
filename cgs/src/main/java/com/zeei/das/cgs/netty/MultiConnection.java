package com.zeei.das.cgs.netty;

import java.util.ArrayList;
import java.util.List;

public class MultiConnection {

	// 发送组
	private String groupName = "";

	// 连接数组
	private List<SingleConnection> imConnections = new ArrayList<>();

	/**
	 * 初始化连接组
	 * 
	 * @param groupName
	 * @param groupHosts
	 */
	MultiConnection(String groupName, String[][] groupHosts) {
		this.groupName = groupName;
		if (groupHosts != null && groupHosts.length > 0) {
			for (String[] groupHost : groupHosts) {
				String host = groupHost[0];
				String port = groupHost[1];
				String clinetId = String.format("%s[%s:%s]", groupName, host, port);
				SingleConnection imConnection = new SingleConnection(clinetId, host, Integer.valueOf(port));
				imConnections.add(imConnection);
			}
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param msg
	 */
	public void sendMsg(String msg) {

		if (imConnections != null && imConnections.size() > 0) {
			for (SingleConnection imConnection : imConnections) {
				imConnection.sendMsg(msg);
			}
		}
	}

	public String getGroupName() {
		return groupName;
	}
}
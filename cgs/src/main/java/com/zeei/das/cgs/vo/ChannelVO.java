/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：ChannelVO.java
* 包  名  称：com.zeei.das.cgs.vo
* 文件描述：连接通道和站点MN映射实体类
* 创建日期：2017年4月19日下午5:03:35
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月19日下午5:03:35 创建文件
*
*/

package com.zeei.das.cgs.vo;

import java.util.List;

import io.netty.channel.Channel;

/**
 * 类 名 称：ChannelVO 类 描 述：连接通道和站点MN映射实体类 功能描述：连接通道和站点MN映射实体类 创建作者：quanhongsheng
 */

public class ChannelVO {

	// 连接通道
	private Channel channel;

	// 站点MN
	private String MN;

	// 站点ID
	private String ID;

	private Integer NPNO;

	private String strPartialMsg;

	private List<String> rawMsgQueue;

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	public Integer getNPNO() {
		return NPNO;
	}

	public void setNPNO(Integer nPNO) {
		NPNO = nPNO;
	}

	public String getStrPartialMsg() {
		return strPartialMsg;
	}

	public void setStrPartialMsg(String strPartialMsg) {
		this.strPartialMsg = strPartialMsg;
	}

	public List<String> getRawMsgQueue() {
		return rawMsgQueue;
	}

	public void setRawMsgQueue(List<String> rawMsgQueue) {
		this.rawMsgQueue = rawMsgQueue;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

}

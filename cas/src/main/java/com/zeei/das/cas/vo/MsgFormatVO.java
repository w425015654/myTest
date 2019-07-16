/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：MsgFormat.java
* 包  名  称：com.zeei.das.cas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月3日下午1:37:22
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月3日下午1:37:22 创建文件
*
*/

package com.zeei.das.cas.vo;

/**
 * 类 名 称：MsgFormat 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class MsgFormatVO {

	// 站点MN
	private String MN;

	// 消息发送方
	private String MsgFrom;

	// 站点协议类型
	private String ProtocolType;

	// 消息类型
	private String MsgType;

	// 消息体
	private Object MsgBody;

	public String getMN() {
		return MN;
	}

	public void setMN(String MN) {
		this.MN = MN;
	}

	public String getMsgFrom() {
		return MsgFrom;
	}

	public void setMsgFrom(String msgFrom) {
		MsgFrom = msgFrom;
	}

	public String getProtocolType() {
		return ProtocolType;
	}

	public void setProtocolType(String protocolType) {
		ProtocolType = protocolType;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public Object getMsgBody() {
		return MsgBody;
	}

	public void setMsgBody(Object msgBody) {
		MsgBody = msgBody;
	}

}

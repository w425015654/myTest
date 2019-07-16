/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：NoticeVO.java
* 包  名  称：com.zeei.das.aps.vo
* 文件描述：通知信息实体
* 创建日期：2017年5月2日上午8:37:49
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月2日上午8:37:49 创建文件
*
*/

package com.zeei.das.aps.vo;

import java.util.Date;

/**
 * 类 名 称：NoticeVO 类 描 述：通知信息实体 功能描述：通知信息实体 创建作者：quanhongsheng
 */

public class NoticeVO {

	private String ruleId;

	// 通知人员名称
	private String userName;

	// 通知目的地
	private String sendAddress;

	// 发送时间
	private Date notifyTime;

	// 通知标题
	private String title;

	// 通知内容
	private String content;

	// 通知类型
	private String noticeType;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSendAddress() {
		return sendAddress;
	}

	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}

	public Date getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(Date notifyTime) {
		this.notifyTime = notifyTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

}

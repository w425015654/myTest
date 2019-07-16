/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：NoticeRuleUserVO.java
* 包  名  称：com.zeei.das.aps.vo
* 文件描述：通知规则-站点/用户关系实体
* 创建日期：2017年5月2日上午9:28:59
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月2日上午9:28:59 创建文件
*
*/

package com.zeei.das.aps.vo;

/**
 * 类 名 称：NoticeRuleUserVO 类 描 述：通知规则-站点/用户关系实体 功能描述：通知规则-站点/用户关系实体
 * 创建作者：quanhongsheng
 */

public class NoticeUserVO {

	// 规则id
	private String ruleId;

	private String userName;

	private String userId;

	private String phone;

	private String email;

	private String noticeType;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

}

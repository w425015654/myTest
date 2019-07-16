package com.zeei.das.aps.vo;

import com.zeei.das.common.utils.DateUtil;

/** 
* @类型名称：AlarmSyncVO
* @类型描述：
* @功能描述：告警同步
* @创建作者：zhanghu
*
*/
public class AlarmSyncVO {

	
	private  Integer  smsId;
	
	private  String   smsTitle;
	
	private  int   smsType = 2;
	
	private  String   smsContent;
	
	private  String   createTime = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");

	
	public Integer getSmsId() {
		return smsId;
	}

	public void setSmsId(Integer smsId) {
		this.smsId = smsId;
	}

	public String getSmsTitle() {
		return smsTitle;
	}

	public void setSmsTitle(String smsTitle) {
		this.smsTitle = smsTitle;
	}

	public int getSmsType() {
		return smsType;
	}

	public void setSmsType(int smsType) {
		this.smsType = smsType;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	
}

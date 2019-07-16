/** 
* Copyright (C) 2012-2018 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：T20x1Message.java
* 包  名  称：com.zeei.das.cas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2018年9月25日下午2:30:28
* 
* 修改历史
* 1.0 luoxianglin 2018年9月25日下午2:30:28 创建文件
*
*/

package com.zeei.das.cas.vo;

import java.util.Date;
import java.util.List;

/**
 * @类型名称：T20xxFormat 
 * @类型描述：监测数据格式 
 * @功能描述：监测数据格式 
 * @创建作者：quan.hongsheng
 *
 */

public class T20xxFormat<T> {

	// 系统编码
	private String ST;

	// 测点MN号
	private String MN;

	// 测点验证密码
	private String PW;

	// 消息类型
	private String CN;

	// 消息序列码
	private String QN;

	// 测点ID
	private String ID;

	private MessageBody<T> CP;

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	public String getPW() {
		return PW;
	}

	public void setPW(String pW) {
		PW = pW;
	}

	public String getCN() {
		return CN;
	}

	public void setCN(String cN) {
		CN = cN;
	}

	public String getQN() {
		return QN;
	}

	public void setQN(String qN) {
		QN = qN;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public MessageBody<T> getCP() {
		return CP;
	}

	public void setCP(MessageBody<T> cP) {
		CP = cP;
	}

	public static class MessageBody<T> {
		private List<T> Params;
		private Date DataTime;

		public List<T> getParams() {
			return Params;
		}

		public void setParams(List<T> params) {
			Params = params;
		}

		public Date getDataTime() {
			return DataTime;
		}

		public void setDataTime(Date dataTime) {
			DataTime = dataTime;
		}

	}

}

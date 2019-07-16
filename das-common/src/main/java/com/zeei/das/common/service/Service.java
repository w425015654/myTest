/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：mds
* 文件名称：ServiceInfo.java
* 包  名  称：com.zeei.das.mds.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月19日上午11:05:04
* 
* 修改历史
* 1.0 zhouyongbo 2017年5月19日上午11:05:04 创建文件
*
*/

package com.zeei.das.common.service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 类 名 称：ServiceInfo 类 描 述：服务状态信息类 功能描述：封装服务状态信息 创建作者：zhouyongbo
 */

public class Service {

	private String serviceCode;

	private String serviceName;

	// 未收到心跳而产生心跳异常告警的时间阈值，单位为秒
	private int threshold;

	// 最后一次收到心跳的时间，当发现（当前时间-lastTime）大于threshold时，产生此服务心跳异常告警
	private Date lastTime = new Date();

	// 服务是否在线
	private AtomicBoolean online = new AtomicBoolean(true);

	public String getServiceName() {
		return serviceName;
	}

	public int getThreshold() {
		return threshold;
	}

	public boolean isOnline() {
		return online.get();
	}

	public void setOnline(boolean b) {
		online.set(b);
	}

	public void updateServInfo() {
		Date now = new Date();
		synchronized (this) {
			lastTime = now;
		}
	}

	public boolean timeouted() {

		Date now = new Date();

		boolean result = false;
		long lastStamp;

		if (online.get()) {
			synchronized (this) {
				lastStamp = lastTime.getTime();
			}

			result = (now.getTime() - lastStamp) > threshold * 1000;
			if (result) {
				online.set(false);
			}
		}

		return result;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public AtomicBoolean getOnline() {
		return online;
	}

	public void setOnline(AtomicBoolean online) {
		this.online = online;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
}

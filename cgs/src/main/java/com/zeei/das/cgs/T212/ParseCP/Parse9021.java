/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：Parse2013.java
* 包  名  称：com.zeei.das.cgs.common.T212。ParseCP
* 文件描述：2013消息体解析实现
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.T212.ParseCP;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zeei.das.cgs.T212.Station;
import com.zeei.das.cgs.vo.StationCfgVO;
import com.zeei.das.common.annotation.ParseCPAnnotation;

import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;

/**
 * 类 名 称：ParseCP 类 描 述：2013消息体解析实现 功能描述：解析2013格式消息 创建作者：quanhongsheng
 */
@ParseCPAnnotation(CN = "9021")
@Component
public class Parse9021 implements ParseCP {

	@Autowired
	Station station;

	/**
	 * 登录验证
	 */
	@Override
	public void ack(JSONObject msgHead, Channel channel) {
		String PW = (String) msgHead.get("PW");
		String MN = (String) msgHead.get("MN");
		String QN = (String) msgHead.get("QN");
		String msg;

		StationCfgVO cfg = station.getStationCfg(MN);

		if (cfg != null && !StringUtil.isNullOrEmpty(cfg.getPwd()) && cfg.getPwd().equals(MN)) {
			msg = String.format("ST=91;CN=9022;PW=%s;MN=%s;Flag=0;CP=&&QN=%s;Logon=1&&", PW, MN, QN);
		} else {
			msg = String.format("ST=91;CN=9022;PW=%s;MN=%s;Flag=0;CP=&&QN=%s;Logon=0&&", PW, MN, QN);
		}

		station.send(channel, msg);
	}

	@Override
	public JSONObject parseT212Body(String cpStr) {
		return null;
	}

}

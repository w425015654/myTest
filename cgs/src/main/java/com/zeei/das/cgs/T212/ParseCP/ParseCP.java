/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：ParseCP.java
* 包  名  称：com.zeei.das.cgs.common.T212.ParseCP
* 文件描述：T212 消息体解析接口
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.T212.ParseCP;

import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;

/**
 * 类 名 称：ParseCP 类 描 述：T212 消息体解析接口 功能描述： 创建作者：quanhongsheng
 */

public interface ParseCP {

	/**
	 * 
	 * parseT212Body:T212 消息体解析函数
	 *
	 * @param cpStr
	 *            消息内容
	 * @return Map<String,Object>
	 */
	public JSONObject parseT212Body(String cpStr);

	public void ack(JSONObject msgHead, Channel channel);

}

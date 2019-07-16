/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：ConfigUtil.java
* 包  名  称：com.zeei.das.cgs.common.utils
* 文件描述：初始化配置类
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cas;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.zeei.das.common.utils.LoadConfigUtil;

/**
 * 类 名 称：ConfigUtil 类 描 述：初始化服务配置类 功能描述：主要初始化站点配置，因子编码配置 创建作者：quanhongsheng
 */

@Component
public class CasService {


	// 系统配置信息
	public static Map<String, String> cfgMap = new ConcurrentHashMap<String, String>();

	static {
		// 读取配置
		cfgMap = LoadConfigUtil.readXmlParam("cas");
	}
}

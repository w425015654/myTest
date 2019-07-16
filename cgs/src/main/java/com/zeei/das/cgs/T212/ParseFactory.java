/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：ParseFactory.java
* 包  名  称：com.zeei.das.cgs.common.T212
* 文件描述：T212 消息体解析工厂类
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.T212;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.cgs.T212.ParseCP.ParseCP;
import com.zeei.das.common.annotation.ParseCPAnnotation;
import com.zeei.das.common.utils.BeanUtil;

import io.netty.util.internal.StringUtil;

/**
 * 类 名 称：ParseFactory 类 描 述：T212 消息解析工厂类 功能描述：获取具体消息体的解析类 创建作者：quanhongsheng
 */
@Component("parseFactory")
public class ParseFactory {

	static Map<String, ParseCP> parsemap = new ConcurrentHashMap<>();
	
	@Autowired
	BeanUtil beanUtil;
	
	@PostConstruct
	public void initConfig() {

		Map<String, Object> objectMap = BeanUtil.getMapbeanwithAnnotion(ParseCPAnnotation.class);

		for (Object o : objectMap.values()) {

			ParseCP parseCP = (ParseCP) o;
			ParseCPAnnotation annotation = parseCP.getClass().getAnnotation(ParseCPAnnotation.class);
			if (annotation != null) {

				String cns = annotation.CN();
				
				if (!StringUtil.isNullOrEmpty(cns)) {
					for (String cn : cns.split(",")) {
						parsemap.put(cn, parseCP);
					}
				}
			}
		}

	}

	/**
	 * 
	 * createParse:获取具体消息体的解析类
	 *
	 * @param CN
	 *            类型类型
	 * @return ParseCP 对象实例
	 */

	public ParseCP createParse(String CN) {

		return parsemap.get(CN);

		/*
		 * switch (CN) { case "2011": case "2031": case "2051": case "2061":
		 * case "2062": case "2063": parseCp = parse20X1; break; case "9011":
		 * case "9012": parseCp = parse901X; break; case "2021": parseCp =
		 * parse2021; break; case "2076": parseCp = parse2076; break; case
		 * "9021": parseCp = parse9021; break; case "3020": parseCp = parse3020;
		 * break; } return parseCp;
		 */
	}

}

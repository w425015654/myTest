package com.zeei.das.common.utils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：BeanUtil.java
* 包  名  称：com.zeei.das.cgs.common.utils
* 文件描述：获取bean实例
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 类 名 称：BeanUtil 类 描 述：spring 获取bean实例对象 功能描述：spring 运行中获取bean实例
 * 创建作者：quanhongsheng
 */
@Component("beanUtil")
public class BeanUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		BeanUtil.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}

	public static <T> T getBean(String beanName, Class<T> clz) {
		return applicationContext.getBean(beanName, clz);
	}

	/**
	 * 根据传入的自定义注解的类,从Application获取有此注解的所有类
	 * @param cls
	 * @return
	 */
	public static Map<String, Object> getMapbeanwithAnnotion(Class<? extends Annotation> cls) {
		Map<String, Object> map = new HashMap<String, Object>();
		map = applicationContext.getBeansWithAnnotation(cls);
		return map;
	}

}

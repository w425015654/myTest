/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：mds
* 文件名称：Plugin.java
* 包  名  称：com.zeei.das.mds.plugin
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月5日下午12:57:45
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月5日下午12:57:45 创建文件
*
*/

package com.zeei.das.common.plugin;

import java.util.Date;

/**
 * 类 名 称：Plugin 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class Plugin {

	private String name;

	private String jar;

	private String desc;

	private String directory;

	private String className;

	private Date runTime=new Date();

	private int threshold = 10;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJar() {
		return jar;
	}

	public void setJar(String jar) {
		this.jar = jar;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Date getRunTime() {
		return runTime;
	}

	public void setRunTime(Date runTime) {
		this.runTime = runTime;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

}

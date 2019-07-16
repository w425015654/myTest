/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：StorageHandler.java
* 包  名  称：com.zeei.das.cas.storage
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午4:25:26
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月4日下午4:25:26 创建文件
*
*/

package com.zeei.das.cas.storage;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * 类 名 称：StorageHandler 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface StorageHandler {

	public void storage(JSONObject data);
	
	
	public void storageBatch(List<?> datas);

	public List<String> parser(JSONObject data);


}

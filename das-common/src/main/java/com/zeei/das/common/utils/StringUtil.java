/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：StringUtil.java
* 包  名  称：com.zeei.das.aas.common.utils
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月21日上午10:34:40
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月21日上午10:34:40 创建文件
*
*/

package com.zeei.das.common.utils;

/**
 * 类 名 称：StringUtil 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class StringUtil {

	/**
	 * 
	 * isEmptyOrNull:判断字符串是否为空或者空格
	 *
	 * @param str
	 * @return boolean
	 */
	public static boolean isEmptyOrNull(String str) {
		if (str == null || str.length() <= 0) {
			return true;
		} else {
			return false;
		}
	}

}

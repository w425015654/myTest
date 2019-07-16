/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：CrcUtil.java
* 包  名  称：com.zeei.das.cgs.common.utils
* 文件描述：CrcUtil 工具类
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.common.utils;

import java.io.UnsupportedEncodingException;

/**
 * 类 名 称：CrcUtil 类 描 述：CrcUtil 工具类 功能描述：计算字符串的CRC码 创建作者：quanhongsheng
 */

public class CrcUtil {

	public static String Crc16Calc(String msg) {

		byte[] data = null;

		try {
			data = msg.getBytes("GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		int reg16 = 0xffff, regHi, charOut;

		for (int chart : data) {

			// 中文取反码
			if (chart < 0) {
				chart = chart & 0xff;
			}

			// System.out.print(chart+" ");

			regHi = (reg16 >> 8);

			reg16 = regHi ^ chart;

			for (int j = 0; j < 8; j++) {

				charOut = reg16 & 0x0001;

				reg16 = reg16 >> 1;

				if (0x0001 == charOut) {
					reg16 = reg16 ^ 0xa001;
				}
			}
		}
		return String.format("%4s", Integer.toHexString(reg16).toUpperCase()).replace(' ', '0');
	}

	public static void main(String args[]) {
		String body = "ST=21;CN=2011;PW=123456;MN=000234020000100;CP=&&DataTime=20171108235700;w21003-Rtd=0.701,w21003-Flag=N;w01001-Rtd=7.51,w01001-Flag=N;w01014-Rtd=0.127,w01014-Flag=N;w01010-Rtd=20.1,w01010-Flag=N;w01009-Rtd=0.00,w01009-Flag=D;w01003-Rtd=4.05,w01003-Flag=N;w01019-Rtd=1.325,w01019-Flag=N&&";
		System.out.println("--------" + Crc16Calc(body));

	}

}

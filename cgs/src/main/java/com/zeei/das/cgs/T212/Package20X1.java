/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：Parse20X1.java
* 包  名  称：com.zeei.das.cgs.common.T212。ParseCP
* 文件描述：2011,2031,2051,2061消息体解析实现
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.T212;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.common.utils.DateUtil;

/**
 * 类 名 称：ParseCP 类 描 述：20X1消息体解析实现 功能描述：解析2051,2011,2061,2051格式消息
 * 创建作者：quanhongsheng
 */

@Component("Package20X1")
public class Package20X1 {

	public static String package2011(String str) {

		// {"data":{"d":[{"f":"N","p":"a21005","v":1.590434331771835},{"f":"N","p":"a05024","v":2.0516329520975005},{"f":"N","p":"a21004","v":3.233688271710242},{"f":"N","p":"a34002","v":4.486763210243747},{"f":"N","p":"a34004","v":5.840688460850954},{"f":"N","p":"a21026","v":6.712502444743169},{"f":"N","p":"a01001","v":7.727512021469948},{"f":"N","p":"a01002","v":8.773440920490335}],"l":[106.3515,24.265165],"t":"20190521111500"},"mn":"10050","mt":"raw"}

		JSONObject dataPackage = JSON.parseObject(str);

		JSONObject data = dataPackage.getJSONObject("data");

		String DataTime = data.getString("t");

		String MN = dataPackage.getString("mn");

		StringBuffer body = new StringBuffer();

		JSONArray params = data.getJSONArray("d");

		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				JSONObject param = params.getJSONObject(i);
				String dataflag = param.getString("f");
				String polluteCode = param.getString("p");
				Double v = param.getDouble("v");
				body.append(String.format("%s-Rtd=%s,%s-Flag=%s;", polluteCode, v, polluteCode, dataflag));
			}
		}

		String head = makeHead(MN, DataTime);

		String msg = head + body + "&&";

		msg = makeT212Msg(msg);

		return msg;

	}

	// 根据消息体组装完整的T212消息
	public static String makeT212Msg(String body) {
		String len = "000" + body.length();
		len = len.substring(len.length() - 4);
		return "##" + len + body + Crc16Calc(body) + "\r\n";
	}

	/**
	 * 
	 * @param MN
	 * @param DataTime
	 * @return
	 */
	public static String makeHead(String MN, String DataTime) {

		StringBuffer head = new StringBuffer();
		head.append("ST=22;");
		head.append(String.format("MN=%s;", MN));
		head.append("CN=2011;");
		head.append("PW=123456;");
		head.append(String.format("QN=%s;", getQN("1")));
		head.append("Flag=0;");
		head.append("CP=&&");
		if (DataTime != null) {
			head.append(String.format("DataTime=%s;", DataTime));
		}
		return head.toString();
	}

	private static AtomicLong sequence = new AtomicLong(0);

	/**
	 * 
	 * getQN:TODO 获取QN
	 *
	 * @param type
	 *            类型 1表示补数，2表示其他返控
	 * @return String
	 */
	public static String getQN(String type) {

		// 类型 1表示补数，2表示其他返控
		long random = sequence.incrementAndGet();
		String timeStr = DateUtil.getCurrentDate("yyyyMMddHHmmssSSS");
		return String.format("%s%s%04d", type, timeStr, random % 10000);
	}

	public static String Crc16Calc(String msg) {

		byte[] data = null;

		try {
			data = msg.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		int reg16 = 0xffff, regHi, charOut;

		for (int chart : data) {

			// 中文取反码
			if (chart < 0) {
				chart = chart & 0xff;
			}

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

	// 测试用
	public static void main(String args[]) {
		
	String data= "{\"data\":{\"d\":[{\"f\":\"N\",\"p\":\"a21005\",\"v\":1.590434331771835},{\"f\":\"N\",\"p\":\"a05024\",\"v\":2.0516329520975005},{\"f\":\"N\",\"p\":\"a21004\",\"v\":3.233688271710242},{\"f\":\"N\",\"p\":\"a34002\",\"v\":4.486763210243747},{\"f\":\"N\",\"p\":\"a34004\",\"v\":5.840688460850954},{\"f\":\"N\",\"p\":\"a21026\",\"v\":6.712502444743169},{\"f\":\"N\",\"p\":\"a01001\",\"v\":7.727512021469948},{\"f\":\"N\",\"p\":\"a01002\",\"v\":8.773440920490335}],\"l\":[106.3515,24.265165],\"t\":\"20190521111500\"},\"mn\":\"10050\",\"mt\":\"raw\"}";

	String msg=package2011(data);
	System.out.println(msg);
	}
}

package com.zeei.das.common.utils;

/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：common
* 文件名称：NumberFormatUtil.java
* 包  名 称：com.zeei.common.converter
* 文件描述：数字格式化工具类
* 创建日期：2017年7月4日下午4:36:24
* 
* 修改历史
* 1.0 luoxianglin 2017年7月4日下午4:36:24 创建文件
*
*/

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @类型名称：NumberFormatUtil
 * @类型描述：数字格式化工具类
 * @功能描述：提供数据格式化方法
 * @创建作者：luoxianglin
 */
public class NumberFormatUtil {

    private NumberFormatUtil() {

	}

	/**
	 * 方法描述:将数字格式化成有两位小数的百分比数
	 *
	 * @param number
	 * @return
	 * 
	 * 		格式化后：0.00%
	 */
	public static String formatPercent(double number) {
		NumberFormat format = new DecimalFormat("##0.00%");
		return format.format(number);
	}

	/**
	 * 方法描述:将数字格式化成有两位小数
	 *
	 * @param number
	 * @return 格式化后：0.00
	 */
	public static String formatDouble(double number) {
		NumberFormat format = new DecimalFormat("##0.00");
		return format.format(number);
	}

	/**
	 * 将double格式化为指定小数位的String，不足小数位用0补全
	 * 
	 * @param v
	 *            需要格式化的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return
	 */
	public static String roundByScale(String v, int scale) {
		if (StringUtil.isEmptyOrNull(v)) {
			return "";
		}

		double value = Double.parseDouble(v);
		if (scale <= 0) {
			long val = Math.round(value);
			return Long.toString(val);
		}
		String formatStr = "0.";
		for (int i = 0; i < scale; i++) {
			formatStr = String.format("%s0", formatStr);
		}

		return new DecimalFormat(formatStr).format(value);
	}

	/**
	 * 使用BigDecimal，保留小数点指定位数
	 */
	public static Double formatValue(double value, Integer accuracy) {

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(accuracy, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * 
	 * formatByScale:进舍规则： A、拟舍弃数字的最左一位数字小于5、或者大于5，采用四舍六入规则。 B、拟舍弃数字的最左一位数字是5时。
	 * ① 其后有非0数字进1； ② 其后是0或无数字时，保留的末位数字是奇数时进1；保留的末位数字是偶数时直接舍弃。 ③
	 * 负数按负数的绝对值按以上规则进行舍取，之后加入负号。
	 * 
	 * @param data
	 *            需要格式化的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return double
	 */
	public static double formatByScale(double data, Integer scale) {

		return Double.valueOf(formatByScale(String.valueOf(data), scale));
	}

	/**
	 * 
	 * formatByScale:进舍规则：(不足位数用0补齐) A、拟舍弃数字的最左一位数字小于5、或者大于5，采用四舍六入规则。
	 * B、拟舍弃数字的最左一位数字是5时。 ① 其后有非0数字进1； ②
	 * 其后是0或无数字时，保留的末位数字是奇数时进1；保留的末位数字是偶数时直接舍弃。 ③ 负数按负数的绝对值按以上规则进行舍取，之后加入负号。
	 * 
	 * @param value
	 *            需要格式化的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return double
	 */
	public static String formatByScale(String value, Integer scale) {
		if (StringUtil.isEmptyOrNull(value)) {
			return "";
		}
		String result = "";
		String formatStr = "0.";
		if (scale == null || scale < 0) {
			scale = 0;
		}

		for (int i = 0; i < scale; i++) {
			formatStr = String.format("%s0", formatStr);
		}
		BigDecimal bd = new BigDecimal(value);// 数字过大或过小时会用科学技术法展示 需要将其从科学技术法转化为具体的数字
		value = bd.toPlainString();
		int m = value.indexOf('-');
		if (m == -1) {
			int index = value.indexOf('.');
			if (index == 0) {
				result = "0";
				return result;
			} else if (index != -1) {// 有小数位
				// 小数位的个数大于要保留的位数
				if (value.substring(index + 1).length() > scale) {
					String endValue = value.substring(index + scale + 1);
					if (!"".equals(endValue)) {
						String first = endValue.substring(0, 1);
						if (Integer.parseInt(first) > 5 || Integer.parseInt(first) < 5) {
							// 采用四舍六入规则
							if (scale == 0) {
								result = Long.toString(Math.round(Double.parseDouble(value)));
							} else {
								result = new DecimalFormat(formatStr).format(Double.valueOf(value));
							}
						} else {
							// ①其后有非0数字进1；
							boolean flag = false;
							if (endValue.length() > 1) {
								for (int i = 1; i < endValue.length(); i++) {
									String a = endValue.substring(i, i + 1);
									if (Integer.parseInt(a) != 0) {
										flag = true;
										break;
									}
								}
							}
							if (flag) {
								if (scale == 0) {
									result = Long.toString(Math.round(Double.parseDouble(value)));
								} else {
									value = value.substring(0, index + scale + 1);
									BigDecimal a = new BigDecimal(value);
									BigDecimal b = new BigDecimal(formatStr.substring(0, formatStr.length() - 1) + 1);
									result = new DecimalFormat(formatStr)
											.format(Double.valueOf(a.add(b).doubleValue()));
								}
							} else if (!flag || endValue.length() == 1) {
								// ②其后是0或无数字（即endValue长度为1，endValue的值为5）时，保留的末位数字是奇数时进1；保留的末位数字是偶数时直接舍弃。
								endValue = value.substring(index + scale, index + scale + 1);
								if (".".equals(endValue)) {
									endValue = value.substring(index + scale - 1, index + scale);
								}
								if (Integer.parseInt(endValue) % 2 == 1) {
									if (scale == 0) {
										result = Long.toString(Math.round(Double.parseDouble(value)));
									} else {
										value = value.substring(0, index + scale + 1);
										BigDecimal a = new BigDecimal(value);
										BigDecimal b = new BigDecimal(
												formatStr.substring(0, formatStr.length() - 1) + 1);
										result = new DecimalFormat(formatStr)
												.format(Double.valueOf(a.add(b).doubleValue()));
									}
								} else {
									if (scale == 0) {
										result = value.substring(0, index);
									} else {
										result = value.substring(0, index + scale + 1);
									}
								}
							}

						}
					} else {
						result = value;
					}
				} else {
					// 小数位的个数小于要保留的位数
					result = new DecimalFormat(formatStr).format(Double.valueOf(value));
				}

			} else {
				// 没有小数位
				if (scale == 0) {
					result = Long.toString(Math.round(Double.parseDouble(value)));
				} else {
					result = new DecimalFormat(formatStr).format(Double.valueOf(value));
				}
			}
		} else {
			// ③负数按负数的绝对值按以上规则进行舍取，之后加入负号。
			value = String.valueOf(Math.abs(Double.valueOf(value)));
			result = formatByScale(value, scale);
			//进舍后的值不为０时　需取反
			if(!Double.valueOf(result).equals(Double.valueOf(0))){
				result = "-" + result;
			}
		}

		return result;
	}
	
	public static  void main(String[]  args){
		
		double s=formatByScale(0.55,1);
	}
}

/** 
* Copyright (C) 2012-2019 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：AvgSpeed.java
* 包  名  称：com.zeei.das.cgs.T212.ParseCP
* 文件描述：
* 创建日期：2019年5月15日上午8:25:32
* 
* 修改历史
* 1.0 lian.wei 2019年5月15日上午8:25:32 创建文件
*
*/

package com.zeei.das.cgs.T212.ParseCP;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zeei.das.cgs.vo.AvgSpeedArrVO;
import com.zeei.das.cgs.vo.AvgSpeedVO;
import com.zeei.das.common.utils.NumberFormatUtil;

/**
 * @类型名称：AvgSpeed @类型描述： @功能描述：
 * @创建作者：lian.wei
 */

public class AvgSpeed {
	private static Logger logger = LoggerFactory.getLogger(AvgSpeed.class);
	public static Map<String, AvgSpeedArrVO> mapSpeed = new HashMap<String, AvgSpeedArrVO>();
	public static int step = 0;
	public static double range = 0;
	public static String pollCodes = null;

	public static void init(int len, String pollCodeList) {
		step = len;
		pollCodes = pollCodeList;
	}

	public static AvgSpeedArrVO findSpeedArrVO(String pollCode) {
		if ((mapSpeed != null) && (mapSpeed.size() > 0)) {
			for (Entry<String, AvgSpeedArrVO> it : mapSpeed.entrySet()) {
				if (it.getKey().equals(pollCode)) {
					return it.getValue();
				}
			}
		}
		return null;
	}

	public static void printf() {
		if ((mapSpeed != null) && (mapSpeed.size() > 0)) {
			for (Entry<String, AvgSpeedArrVO> it : mapSpeed.entrySet()) {
				logger.info("----------------------------------------------------------------");
				logger.info("pollCode = :" + it.getKey());
				AvgSpeedArrVO vo = it.getValue();
				if (vo != null) {
					logger.info("speedCnt = :" + vo.getSpeedCnt());

					if ((vo.getArraySpeed() != null) && (vo.getSpeedCnt() > 0)) {
						for (int i = 0; i < vo.getSpeedCnt(); i++) {
							logger.info("value = :" + vo.getArraySpeed()[i].getValue());
							logger.info("date = :" + vo.getArraySpeed()[i].getDate());
						}
					}

				}
			}
			logger.info("----------------------------------------------------------------");
		}
	}

	/**
	 * NumberFormat:针对数据取四舍五入
	 * 
	 * @param value
	 * @param size
	 * @return Double
	 */
	private static Double NumberFormat(Double value, int size) {

		return value == null ? null : NumberFormatUtil.formatByScale(value, size);
	}
	
	public static double setArraySpeed(String mn, String cn, String pollCode, String value, String flag, String date) {
		int dataCnt = 0;
		double sum = 0;
		AvgSpeedVO[] tempArray = new AvgSpeedVO[step];

		// 找到数组
		String key = mn + cn + pollCode;
		AvgSpeedArrVO arrVO = findSpeedArrVO(key);
		if (arrVO == null) {
			AvgSpeedVO[] speedArr = new AvgSpeedVO[step];
			AvgSpeedArrVO arr = new AvgSpeedArrVO();
			arrVO = arr;
			arr.setSpeedCnt(0);
			arr.setArraySpeed(speedArr);
			mapSpeed.put(key, arr);
		}

		if (arrVO.getSpeedCnt() > (step - 1)) {
			System.arraycopy(arrVO.getArraySpeed(), 0, tempArray, 0, step);
			// 冲掉最早的一个
			System.arraycopy(tempArray, 1, arrVO.getArraySpeed(), 0, step - 1);
			AvgSpeedVO vo = new AvgSpeedVO();
			vo.setPollCode(pollCode);
			vo.setValue(value);
			vo.setDate(date);
			arrVO.getArraySpeed()[step - 1] = vo;
			dataCnt = step;

		} else {
			AvgSpeedVO vo = new AvgSpeedVO();
			vo.setPollCode(pollCode);
			vo.setValue(value);
			vo.setDate(date);

			arrVO.getArraySpeed()[arrVO.getSpeedCnt()] = vo;

			dataCnt = arrVO.getSpeedCnt() + 1;

			// 自增
			arrVO.setSpeedCnt(arrVO.getSpeedCnt() + 1);
		}

		for (int i = 0; i < dataCnt; i++) {
			sum += Double.valueOf(arrVO.getArraySpeed()[i].getValue());
		}

		if ((sum > 0) && (dataCnt > 0)) {
			return NumberFormat(sum / dataCnt, 2);
		} else {
			return NumberFormat(Double.valueOf(value), 2);
		}

	}
}

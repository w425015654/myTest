/** 
* Copyright (C) 2012-2018 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：AssembleParams.java
* 包  名  称：com.zeei.das.aas.utils
* 文件描述：TODO 请修改文件描述
* 创建日期：2018年9月29日下午3:24:15
* 
* 修改历史
* 1.0 wudahe 2018年9月29日下午3:24:15 创建文件
*
*/

package com.zeei.das.aas.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.zeei.das.aas.AasService;
import com.zeei.das.aas.vo.MonitorDataVO;
import com.zeei.das.aas.vo.SystemTableVO;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.utils.DateUtil;

/**
 * @类型名称：AssembleParams
 * @类型描述：TODO 请修改类型描述
 * @功能描述：TODO 请修改功能描述
 * @创建作者：wudahe
 *
 */

public class AssembleParamUtil {
	private static Logger logger = LoggerFactory.getLogger(AssembleParamUtil.class);

	public static JSONArray data2Params(String ST, String CN, String MN, MonitorDataVO vo) {
		JSONArray array = new JSONArray();

		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Map<String, Object> CP = new HashMap<String, Object>();
			List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();

			data.put("ST", ST);
			data.put("MN", MN);
			data.put("PW", "123456");
			data.put("CN", CN);
			data.put("QN", "");
			data.put("CP", CP);

			CP.put("Params", params);
			CP.put("DataTime",
					DateUtil.dateToStr(DateUtil.strToDate(vo.getDataTime(), "yyyy-MM-dd HH:mm:ss"), "yyyyMMddHHmmss"));

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("Rtd", vo.getDataValue());
			param.put("ParamID", vo.getPolluteCode());
			param.put("Flag", vo.getDataFlag());
			params.add(param);

			array.add(params);
		} catch (Exception e) {
			logger.error("转换数据异常:" + e.toString());
		}

		return array;
	}
}

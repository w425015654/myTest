/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：StorageHandler2021.java
* 包  名  称：com.zeei.das.cas.storage
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午4:34:30
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月4日下午4:34:30 创建文件
*
*/

package com.zeei.das.cas.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.cas.mq.Publish;
import com.zeei.das.cas.service.CtlMsgService;
import com.zeei.das.cas.vo.Msg2021VO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：StorageHandler2021 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component("storageHandler2021")
public class StorageHandler2021 implements StorageHandler {

	private static Logger logger = LoggerFactory.getLogger(StorageHandler2021.class);

	@Autowired
	CtlMsgService ctlMsgService;

	@Autowired
	Publish publish;

	@Override
	public void storage(JSONObject data) {

		try {

			List<String> list1 = parser(data);
			List<Msg2021VO> list = new ArrayList<Msg2021VO>();
			for (String json : list1) {
				list.add((Msg2021VO) JSON.parse(json));
			}

			if (list.size() > 0) {
				ctlMsgService.insert2021ByBatch(list);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
	}
	
	
	/**
	 * 批量写入
	 */
	@Override
	public void storageBatch(List<?> datas) {

		
	}

	/**
	 * 
	 * parser:消息解析
	 *
	 * @param data
	 * @return List<Msg2021VO>
	 */
	public List<String> parser(JSONObject data) {

		List<Msg2021VO> list = new ArrayList<Msg2021VO>();
		try {

			if (data != null) {

				String ID = data.getString("ID");
				String MN = data.getString("MN");

				if (StringUtil.isEmptyOrNull(ID)) {

					logger.error("测点编码为空：" + data.toString());
					return null;
				}

				@SuppressWarnings("unchecked")
				Map<String, String> CP = (Map<String, String>) data.get("CP");

				if (CP != null && CP.size() > 0) {

					Date dataTime = DateUtil.getCurrentDate();

					Map<String, String[]> alaramParam = new HashMap<String, String[]>();

					for (Map.Entry<String, String> entry : CP.entrySet()) {

						String key = entry.getKey();
						String value = entry.getValue();

						if ("DataTime".equals(key)) {
							if (!StringUtil.isEmptyOrNull(value)) {
								dataTime = DateUtil.strToDate(value, null);
							}

							continue;
						}
						String[] arr = key.split("-");
						String polluteCode = arr[0];
						String type = arr[1].substring(0, 1);
						String code = arr[1].substring(1);
						// 告警部分的数据处理
						if ("A".equals(type)) {
							alaramParam.put(key, new String[] { polluteCode, code, value });
						}
						Optional<Msg2021VO> opmsg = list.stream().filter(o -> ID.equals(o.getPointCode())
								&& code.equals(o.getStatusCode()) && polluteCode.equals(o.getPolluteCode()))
								.findFirst();

						Msg2021VO msg = new Msg2021VO();

						if (opmsg.isPresent()) {
							msg = opmsg.get();
						} else {
							list.add(msg);
						}

						msg.setDataValue(value);
						msg.setPointCode(ID);
						msg.setStatusCode(code);
						msg.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
						msg.setPolluteCode(polluteCode);
						msg.setDataTime(DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));

					}

					// 2021 告警数据
					Map<String, Object> alarmData = new HashMap<String, Object>();
					alarmData.put("MN", MN);
					alarmData.put("CN", "2021");
					alarmData.put("DataTime", dataTime);
					alarmData.put("CP", alaramParam);
					String Json = JSON.toJSONStringWithDateFormat(alarmData, "yyyy-MM-dd HH:mm:ss",
							SerializerFeature.WriteDateUseDateFormat);
					publish.send(Constant.MQ_QUEUE_TM212, Json);
				}
			}
		} catch (Exception e) {
			logger.error("",e);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}

		List<String> rets = new ArrayList<String>();

		for (Msg2021VO vo : list) {

			rets.add(JSON.toJSONString(vo));
		}

		return rets;
	}

}

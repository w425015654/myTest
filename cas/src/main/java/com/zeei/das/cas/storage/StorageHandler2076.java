/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：StorageHandler2076.java
* 包  名  称：com.zeei.das.cas.storage
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午4:35:06
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月4日下午4:35:06 创建文件
*
*/

package com.zeei.das.cas.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.cas.mq.Publish;
import com.zeei.das.cas.service.CtlMsgService;
import com.zeei.das.cas.vo.Msg2076VO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：StorageHandler2076 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component("storageHandler2076")
public class StorageHandler2076 implements StorageHandler {

	private static Logger logger = LoggerFactory.getLogger(StorageHandler2021.class);

	private final String LOGIN = "1";

	@Autowired
	CtlMsgService ctlMsgService;

	@Autowired
	Publish publish;

	@Override
	public void storage(JSONObject data) {

		try {

			List<String> list1 = parser(data);
			List<Msg2076VO> list = new ArrayList<Msg2076VO>();
			for (String json : list1) {
				list.add((Msg2076VO) JSON.parse(json));
			}

			if (list != null && list.size() > 0) {

				ctlMsgService.insert2076ByBacth(list);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	
	int batch = 1000;
	/**
	 * 批量写入
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void storageBatch(List<?> datas) {

		try {
			if (datas != null && datas.size() > 0) {

				int size = datas.size();
				int group = (int) Math.ceil((double) size / batch);

				for (int i = 0; i < group; i++) {

					int fromIndex = i * batch;

					int toIndex = (i + 1) * batch;

					if (toIndex > size) {
						toIndex = size;
					}

					long b = System.currentTimeMillis();
					ctlMsgService.insert2076ByBacth((List<Msg2076VO>) datas.subList(fromIndex, toIndex));
					long e = System.currentTimeMillis();
					logger.info(String.format("T2076入库：数量：%s 耗时：  %s ", (toIndex - fromIndex), (e - b)));
				}
			}
		} catch (Exception e) {
			logger.info(JSON.toJSONString(datas));
			logger.error(e.toString());
		}
		
	}
	
	/**
	 * 
	 * parser:TODO 请修改方法功能描述
	 *
	 * @param data
	 * @return List<Msg2076VO>
	 */
	public List<String> parser(JSONObject data) {

		List<String> list = new ArrayList<String>();
		try {

			if (data != null) {

				String ID = data.getString("ID");

				@SuppressWarnings("unchecked")
				Map<String, String> CP = (Map<String, String>) data.get("CP");

				if (CP != null && CP.size() > 0) {

					// String SN = CP.get("SN");
					String logTime = CP.get("LogTime");
					String logcCntent = CP.get("LogAct");
					String loginName = CP.get("LoginName");
					String logType = CP.get("LogType");

					Date dataTime = DateUtil.getCurrentDate();

					if (!StringUtil.isEmptyOrNull(logTime)) {
						dataTime = DateUtil.strToDate(logTime, null);
					}

					// 门禁类型
					if (LOGIN.equals(logType)) {

						String userId = "-1";
						@SuppressWarnings("unused")
						String cardNo = "-1";
						if (!StringUtil.isEmptyOrNull(logcCntent)) {
							String[] sArr1 = logcCntent.split(",");
							if (sArr1.length > 1) {

								if (sArr1[0].contains("=")) {
									userId = sArr1[0].split("=")[1];
								}
								if (sArr1[1].contains("=")) {
									cardNo = sArr1[1].split("=")[1];
								}
							}
						}
						logcCntent = "";
						if (Integer.valueOf(userId) < 0) {
							logcCntent = "用户于" + logTime + "开锁进入站房！";
						} else {
							String logUser = "";
							logcCntent = "用户:" + logUser + " 于" + logTime + " 刷卡进入站房！";
						}
						// String qRId = cardNo;
					}

					Msg2076VO msg2076 = new Msg2076VO();
					msg2076.setLogContent(logcCntent);
					msg2076.setLogTime(DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
					msg2076.setLogType(logType);
					msg2076.setUserId(loginName);
					msg2076.setPointCode(ID);

					list.add(JSON.toJSONString(msg2076));
				}

			}
		} catch (Exception e) {
			logger.error(e.toString());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}

		return list;
	}

}

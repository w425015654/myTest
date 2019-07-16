/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：StorageHandler3020.java
* 包  名  称：com.zeei.das.cas.storage
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月15日上午8:53:25
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月15日上午8:53:25 创建文件
*
*/

package com.zeei.das.cas.storage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.cas.mq.Publish;
import com.zeei.das.cas.service.CtlMsgService;
import com.zeei.das.cas.vo.Msg2021VO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：StorageHandler3020 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component
public class StorageHandler3020 implements StorageHandler {
	private static Logger logger = LoggerFactory.getLogger(StorageHandler3020.class);

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
				ctlMsgService.insert2021Latest(list);
				ctlMsgService.insert2021ByBatch(list);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
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
					ctlMsgService.insert2021ByBatch((List<Msg2021VO>) datas.subList(fromIndex, toIndex));
					long e = System.currentTimeMillis();
					logger.info(String.format("T3020入库：数量：%s 耗时：  %s ", (toIndex - fromIndex), (e - b)));
				}
			}
		} catch (Exception e) {
			logger.info(JSON.toJSONString(datas));
			logger.error("", e);
		}

	}

	public void insertLatest(List<Msg2021VO> datas) {

		try {
			if (datas != null && datas.size() > 0) {

				Map<String, Optional<Msg2021VO>> map = datas.stream().collect(
						Collectors.groupingBy(Msg2021VO::getGroupKey, Collectors.maxBy(new Comparator<Msg2021VO>() {
							@Override
							public int compare(Msg2021VO o1, Msg2021VO o2) {
								Date d1 = DateUtil.strToDate(o1.getDataTime(), "yyyy-MM-dd HH:mm:ss");
								Date d2 = DateUtil.strToDate(o2.getDataTime(), "yyyy-MM-dd HH:mm:ss");
								return d1.compareTo(d2);
							}
						})));

				List<Msg2021VO> list = new ArrayList<Msg2021VO>();

				for (Optional<Msg2021VO> optional : map.values()) {

					if (optional.isPresent()) {
						list.add(optional.get());
					}
				}

				if (list != null && list.size() > 0) {

					int size = list.size();
					int group = (int) Math.ceil((double) size / batch);

					for (int i = 0; i < group; i++) {

						int fromIndex = i * batch;

						int toIndex = (i + 1) * batch;

						if (toIndex > size) {
							toIndex = size;
						}

						long b = System.currentTimeMillis();
						ctlMsgService.insert2021Latest(list.subList(fromIndex, toIndex));
						long e = System.currentTimeMillis();
						logger.info(String.format("T3020 最新数据入库：数量：%s 耗时：  %s ", (toIndex - fromIndex), (e - b)));
					}
				}

			}
		} catch (Exception e) {
			logger.info(JSON.toJSONString(datas));
			logger.error("", e);
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
				JSONObject CP = data.getJSONObject("CP");

				if (StringUtil.isEmptyOrNull(ID)) {
					ID = "-1";
				}

				if (CP != null && CP.size() > 0) {

					String dataTime = CP.getString("DataTime");

					@SuppressWarnings("unchecked")
					Map<String, Map<String, Object>> items = (Map<String, Map<String, Object>>) CP.get("Item");

					if (items != null && items.size() > 0) {

						for (Map.Entry<String, Map<String, Object>> map : items.entrySet()) {

							String polId = map.getKey();

							Map<String, Object> item = map.getValue();

							for (Entry<String, Object> entry : item.entrySet()) {

								String code = entry.getKey();
								if (!"PolId".equals(code)) {
									String value = (String) entry.getValue();
									Msg2021VO msg = new Msg2021VO();
									msg.setDataTime(dataTime);
									msg.setDataValue(value);
									msg.setPointCode(ID);
									msg.setPolluteCode(polId);
									msg.setStatusCode(code);
									msg.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
									list.add(JSON.toJSONString(msg));
								}
							}
						}
					}

				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
		return list;
	}

}

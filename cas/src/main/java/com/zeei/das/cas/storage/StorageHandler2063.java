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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.cas.service.CtlMsgService;
import com.zeei.das.cas.vo.Msg2063VO;
import com.zeei.das.common.utils.StringUtil;

/**
 * @类 名 称：storageHandler2063
 * @类 描 述：上传监测指标加回收数据(2063)
 * @功能描述：上传监测指标加回收数据(2063) @创建作者：quanhongsheng
 */
@Component("storageHandler2063")
public class StorageHandler2063 implements StorageHandler {

	private static Logger logger = LoggerFactory.getLogger(StorageHandler2063.class);

	@Autowired
	CtlMsgService ctlMsgService;

	@Override
	public void storage(JSONObject data) {

		try {

			List<String> list = parser(data);

			List<Msg2063VO> params = new ArrayList<Msg2063VO>();

			for (String json : list) {

				params.add(JSON.parseObject(json, Msg2063VO.class));

			}

			if (list.size() > 0) {
				ctlMsgService.insert2063ByBatch(params);
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
					ctlMsgService.insert2063ByBatch((List<Msg2063VO>) datas.subList(fromIndex, toIndex));
					long e = System.currentTimeMillis();
					logger.info(String.format("T2063入库：数量：%s 耗时：  %s ", (toIndex - fromIndex), (e - b)));
				}
			}
		} catch (Exception e) {
			logger.info(JSON.toJSONString(datas));
			logger.error(e.toString());
		}
		
	}

	/**
	 * 
	 * parser:消息解析
	 *
	 * @param data
	 * @return List<Msg2021VO>
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
					List<Map<String, Object>> items = (List<Map<String, Object>>) CP.get("Params");

					if (items != null && items.size() > 0) {

						for (Map<String, Object> param : items) {

							if (param != null) {

								param.put("DataTime", dataTime);
								param.put("PointCode", ID);
								list.add(JSON.toJSONString(param));
							}
						}
					}

				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}

}

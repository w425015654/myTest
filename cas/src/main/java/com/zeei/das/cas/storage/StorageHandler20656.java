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
import com.zeei.das.cas.vo.Msg20656VO;
import com.zeei.das.common.utils.StringUtil;


/** 
* @类型名称：StorageHandler20656
* @类型描述：
* @功能描述：处理2065和2066消息
* @创建作者：zhanghu
*
*/
@Component("storageHandler20656")
public class StorageHandler20656 implements StorageHandler {

	private static Logger logger = LoggerFactory.getLogger(StorageHandler20656.class);

	@Autowired
	CtlMsgService ctlMsgService;

	@Override
	public void storage(JSONObject data) {

		try {

			List<String> list = parser(data);

			List<Msg20656VO> params = new ArrayList<Msg20656VO>();

			for (String json : list) {

				params.add(JSON.parseObject(json, Msg20656VO.class));

			}

			if (list.size() > 0) {
				ctlMsgService.insert20656ByBatch(params);
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
					ctlMsgService.insert20656ByBatch((List<Msg20656VO>) datas.subList(fromIndex, toIndex));
					long e = System.currentTimeMillis();
					logger.info(String.format("T20656入库：数量：%s 耗时：  %s ", (toIndex - fromIndex), (e - b)));
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
	 * @return List<Msg20656VO>
	 */
	public List<String> parser(JSONObject data) {

		List<String> list = new ArrayList<String>();

		try {

			if (data != null) {

				String ID = data.getString("ID");
				JSONObject CP = data.getJSONObject("CP");
				String CN = data.getString("CN");

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
								if("2065".equals(CN)){
									param.put("dataType", 1);
								}else{
									param.put("dataType", 2);
								}
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

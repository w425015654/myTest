/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：StorageHandler901x.java
* 包  名  称：com.zeei.das.cas.storage
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午4:35:34
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月4日下午4:35:34 创建文件
*
*/

package com.zeei.das.cas.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.cas.mq.Publish;
import com.zeei.das.cas.service.CtlMsgService;
import com.zeei.das.cas.vo.CtlRecDetailVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;

/**
 * 类 名 称：StorageHandler901x 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component("storageHandler901x")
public class StorageHandler901x implements StorageHandler {

	private static final Logger logger = LoggerFactory.getLogger(StorageHandler901x.class);

	@Autowired
	CtlMsgService ctlMsgService;

	@Autowired
	Publish publish;

	@Override
	public void storage(JSONObject data) {

		try {

			List<String> list1 = parser(data);
			List<CtlRecDetailVO> list = new ArrayList<CtlRecDetailVO>();
			for (String json : list1) {
				list.add((CtlRecDetailVO) JSON.parse(json));
			}

			if (list != null && list.size() > 0) {
				// 实时更改补数命令记录数据状态
				ctlMsgService.updateCtlRecByBacth(list);
				// 实时添加补数命令详情记录
				ctlMsgService.insertCtrlRecDetailByBacth(list);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	int batch = 1000;

	/**
	 * 批量写入
	 */
	@Override
	public void storageBatch(List<?> datas) {

	}

	public void updateBatch(List<CtlRecDetailVO> datas, int type) {

		try {
			if (datas != null && datas.size() > 0) {

				Map<String, List<CtlRecDetailVO>> rets = datas.stream()
						.collect(Collectors.groupingBy(CtlRecDetailVO::getCodeID, Collectors.toList()));

				String[] cns = new String[] { "9011", "9012" };

				for (String cn : cns) {

					List<CtlRecDetailVO> list = rets.get(cn);

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

							if (type == 1) {
								ctlMsgService.updateCompRecByBacth(list.subList(fromIndex, toIndex));
							} else {
								ctlMsgService.updateCtlRecByBacth(list.subList(fromIndex, toIndex));
							}

							long e = System.currentTimeMillis();

							logger.info(String.format("T90x1 状态更新：数量：%s 耗时：  %s ", (toIndex - fromIndex), (e - b)));
						}
					}
				}
			}
		} catch (Exception e) {
			logger.info(JSON.toJSONString(datas));
			logger.error(e.toString());
		}
	}

	public void storageBatch(List<CtlRecDetailVO> datas, int type) {

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
					if (type == 1) {
						ctlMsgService.insertCompRecDetailByBacth(datas.subList(fromIndex, toIndex));
					} else {
						ctlMsgService.insertCtrlRecDetailByBacth(datas.subList(fromIndex, toIndex));
					}
					long e = System.currentTimeMillis();
					logger.info(String.format("T90x1入库：数量：%s 耗时：  %s ", (toIndex - fromIndex), (e - b)));
				}
			}
		} catch (Exception e) {
			logger.info(JSON.toJSONString(datas));
			logger.error("",e);
		}

	}

	/**
	 * 
	 * parser:TODO 请修改方法功能描述
	 *
	 * @param data
	 * @return List<Msg2076VO>
	 */
	@Override
	public List<String> parser(JSONObject data) {

		List<String> list = new ArrayList<String>();

		try {

			if (data != null) {

				String pointCode = data.getString("ID");
				String CN = data.getString("CN");
				String QN = data.getString("QN");

				JSONObject CP = data.getJSONObject("CP");

				if (CP != null) {

					String mrqStr = CP.getString("mrqStr");
					String mrqFlag = CP.getString("mrqFlag");

					String ret = "";
					String status = "";
					String remark = "";

					if ("9011".equals(CN)) {
						ret = CP.getString("QnRtn");
						if ("1".equals(ret)) {
							status = "3";
							remark = "命令接收成功";
						} else {
							status = "4";
							remark = "命令接收失败";
						}
					} else {
						ret = CP.getString("ExeRtn");
						if ("1".equals(ret)) {
							status = "5";
							remark = "命令执行成功";
						} else {
							status = "6";
							remark = "命令执行失败";
						}
					}

					CtlRecDetailVO ctrRecDetailVO = new CtlRecDetailVO();
					ctrRecDetailVO.setQN(QN);
					ctrRecDetailVO.setResult(ret);
					ctrRecDetailVO.setRemark(remark);
					ctrRecDetailVO.setPointCode(pointCode);
					ctrRecDetailVO.setMrqFlag(mrqFlag);
					ctrRecDetailVO.setMrqStr(mrqStr);
					ctrRecDetailVO.setDataTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
					ctrRecDetailVO.setCmdStatus(status);
					ctrRecDetailVO.setCodeID(CN);

					list.add(JSON.toJSONString(ctrRecDetailVO));

				}

			}
		} catch (Exception e) {
			logger.error(e.toString());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}

		return list;
	}

}

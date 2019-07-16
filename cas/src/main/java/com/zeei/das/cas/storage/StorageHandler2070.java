package com.zeei.das.cas.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.cas.mq.Publish;
import com.zeei.das.cas.service.CtlMsgService;
import com.zeei.das.cas.vo.Msg2070VO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：StorageHandler2070 类 描 述：质控结果数据入库 创建作者：zhanghu
 */
@Component("storageHandler2070")
public class StorageHandler2070 implements StorageHandler {

	private static Logger logger = LoggerFactory.getLogger(StorageHandler2070.class);

	@Autowired
	CtlMsgService ctlMsgService;

	@Autowired
	Publish publish;

	@Override
	public void storage(JSONObject data) {

		try {

			List<String> list1 = parser(data);
			List<Msg2070VO> list = new ArrayList<Msg2070VO>();
			for (String json : list1) {
				list.add((Msg2070VO) JSON.parse(json));
			}

			if (list.size() > 0) {
				ctlMsgService.insert2070ByBatch(list);
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

				List<Msg2070VO> data2070s = (List<Msg2070VO>) datas;
				for (Msg2070VO data2070 : data2070s) {
					// 时间转换
					data2070.setDataTime(data2070.getDataTime());
				}

				for (int i = 0; i < group; i++) {

					int fromIndex = i * batch;

					int toIndex = (i + 1) * batch;

					if (toIndex > size) {
						toIndex = size;
					}

					long b = System.currentTimeMillis();
					ctlMsgService.insert2070ByBatch(data2070s.subList(fromIndex, toIndex));
					long e = System.currentTimeMillis();
					logger.info(String.format("T2070入库：数量：%s 耗时：  %s ", (toIndex - fromIndex), (e - b)));
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
	 * @return List<Msg2070VO>
	 */
	@SuppressWarnings("unchecked")
	public List<String> parser(JSONObject data) {

		List<Msg2070VO> list = new ArrayList<Msg2070VO>();
		try {

			if (data != null) {

				String ID = data.getString("ID");
				String QN = data.getString("QN");
				JSONObject CP = data.getJSONObject("CP");

				if (StringUtil.isEmptyOrNull(ID)) {

					logger.error("测点编码为空：" + data.toString());
					return null;
				}

				if (CP != null && !CP.isEmpty()) {

					Date dataTime = CP.getDate("DataTime");

					if (dataTime == null) {
						dataTime = DateUtil.getCurrentDate();
					}

					JSONArray cpList = CP.getJSONArray("Params");

					for (Object cpObj : cpList) {

						JSONObject cpMap = (JSONObject) cpObj;

						Msg2070VO msg = new Msg2070VO();

						String alarmVal = cpMap.getString("AlarmVal");
						String devVal = cpMap.getString("DevVal");
						String resVal = cpMap.getString("ResVal");
						String tagVal = cpMap.getString("TagVal");
						String ctrlVal = cpMap.getString("CtrlVal");

						if (StringUtils.isNotEmpty(alarmVal)) {
							msg.setAlarmVal(Float.valueOf(alarmVal));
						}
						if (StringUtils.isNotEmpty(devVal)) {
							msg.setDeval(Float.valueOf(devVal));
						}
						if (StringUtils.isNotEmpty(resVal)) {
							msg.setResval(Float.valueOf(resVal));
						}
						if (StringUtils.isNotEmpty(tagVal)) {
							msg.setTagval(Float.valueOf(tagVal));
						}
						if (StringUtils.isNotEmpty(ctrlVal)) {
							msg.setCtrlVal(Float.valueOf(ctrlVal));
						}
						msg.setCmdType(cpMap.getString("Cmd"));
						msg.setDataTime(dataTime);
						msg.setPointCode(Integer.valueOf(ID));
						msg.setPolluteCode(cpMap.getString("PolId"));
						msg.setQcResult(cpMap.getString("Result"));
						msg.setQcSource(cpMap.getString("Source"));
						msg.setQn(QN);

						list.add(msg);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}

		List<String> rets = new ArrayList<String>();

		for (Msg2070VO vo : list) {

			rets.add(JSON.toJSONString(vo));
		}

		return rets;
	}

}

/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：AlarmFactory.java
* 包  名  称：com.zeei.das.aas.alarm
* 文件描述：告警采集适配器
* 创建日期：2017年4月21日上午8:10:43
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月21日上午8:10:43 创建文件
*
*/

package com.zeei.das.aas.alarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.aas.AasService;
import com.zeei.das.aas.alarm.custom.AqiAlarm;
import com.zeei.das.aas.alarm.custom.DoubtfulAlarm;
import com.zeei.das.aas.alarm.custom.FlagAlarm;
import com.zeei.das.aas.alarm.custom.FlowAlarm;
import com.zeei.das.aas.alarm.custom.FrequencyAlarm;
import com.zeei.das.aas.alarm.custom.QGAlarm;
import com.zeei.das.aas.alarm.custom.SlowAlarm;
import com.zeei.das.aas.alarm.custom.YSAlarm;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.vo.StationVO;
import com.zeei.das.aas.vo.T20x1Message;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.T212Code;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：AlarmFactory 类 描 述：告警采集适配器 功能描述：告警采集适配器 创建作者：quanhongsheng
 */
@Component("alarmFactory")
public class AlarmFactory {

	private static Logger logger = LoggerFactory.getLogger(Alarm.class);

	@Autowired
	Publish publish;

	@Autowired
	T212Alarm t212Alarm;

	@Autowired
	ServiceAlarm serviceAlarm;

	@Autowired
	T2021Alarm t2021Alarm;

	@Autowired
	T3020Alarm t3020Alarm;

	@Autowired
	QGAlarm qgAlarm;

	@Autowired
	YSAlarm ysAlarm;

	@Autowired
	SlowAlarm slowAlarm;

	@Autowired
	FlowAlarm flowAlarm;

	@Autowired
	DoubtfulAlarm doubtfulAlarm;

	@Autowired
	FlagAlarm flagAlarm;

	@Autowired
	AqiAlarm aqiAlarm;

	@Autowired
	FrequencyAlarm frequencyAlarm;

	public boolean alarmHandler(String data) {

		try {

			T20x1Message t20x1 = JSON.parseObject(data, T20x1Message.class);

			// 强制转换，用com.alibaba.fastjson.serializer.PascalNameFilter，直接把首字母转成大写。
			// 如：JSON.toJSONString(bean,new PascalNameFilter());
			String json = JSON.toJSONString(t20x1);

			JSONObject map = JSON.parseObject(json);

			String CN = map.getString("CN");
			String MN = map.getString("MN");

			// 接收到心跳检测的数据包 ， 更新站点接收数据时间,
			if (!StringUtil.isEmptyOrNull(MN) && !StringUtil.isEmptyOrNull(map.getString("HT"))) {
				upDataTime(map);
			}

			switch (CN) {
			case T212Code.T2011:
			case T212Code.T2051:
			case T212Code.T2061:
				// 对原始数据及分钟小时数据 计算周期
				// frequencyAlarm.alarmHandler(map);
			case T212Code.T2031:
				t212Alarm.alarmHandler(map);
				qgAlarm.alarmHandler(map);
				flagAlarm.alarmHandler(map);
				ysAlarm.alarmHandler(map);
				// 缓慢告警
				slowAlarm.alarmHandler(map);
				// 流量告警
				flowAlarm.alarmHandler(map);
				// 存疑小时数据
				// doubtfulAlarm.alarmHandler(map);
				break;
			case T212Code.T2021:
				t2021Alarm.alarmHandler(map);
				break;
			case T212Code.T3020:
				t3020Alarm.alarmHandler(map);
				break;
			case T212Code.TAQI:
				aqiAlarm.alarmHandler(map);
				break;
			default:
				serviceAlarm.alarmHandler(map);
				break;
			}

		} catch (Exception e) {
			logger.error("", e);
			logger.error("数据格式错误:", data);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
		return true;
	}

	/**
	 * 
	 * upDataTime:更新站点最新数据时间
	 *
	 * @param MN
	 *            void
	 */
	public void upDataTime(JSONObject data) {

		String MN = data.getString("MN");

		StationVO station = AasService.stationMap.get(MN);

		if (station != null) {
			station.setHeartTime(DateUtil.getCurrentDate());
		}
	}

	public static void main(String[] args) {

		String json = "{\"ST\":\"22\",\"mN\":\"AQMS0018003\",\"PW\":\"123456\",\"CN\":\"2051\",\"ID\":\"8\",\"HT\":\"2019-06-27 17:40:15\",\"QN\":\"20190627174015448\",\"Flag\":\"0\",\"CP\":{\"Params\":[{\"Avg\":\"76.94296264648438\",\"Rtd\":\"76.94296264648438\",\"ParamID\":\"a01002\",\"Flag\":\"N\"},{\"Avg\":\"2.361360549926758\",\"Rtd\":\"2.361360549926758\",\"ParamID\":\"a01007\",\"Flag\":\"N\"},{\"Avg\":\"1008.8681030273438\",\"Rtd\":\"1008.8681030273438\",\"ParamID\":\"a01006\",\"Flag\":\"N\"},{\"Avg\":\"32.410118103027344\",\"Rtd\":\"32.410118103027344\",\"ParamID\":\"a01008\",\"Flag\":\"N\"},{\"Avg\":\"0.8869057893753051\",\"Rtd\":\"0.8869057893753051\",\"ParamID\":\"a21005\",\"Flag\":\"N\"},{\"Avg\":\"39.603614807128906\",\"Rtd\":\"39.603614807128906\",\"ParamID\":\"a21004\",\"Flag\":\"N\"},{\"Avg\":\"17.217052459716797\",\"Rtd\":\"17.217052459716797\",\"ParamID\":\"a21026\",\"Flag\":\"N\"},{\"Avg\":\"0.0\",\"Rtd\":\"0.0\",\"ParamID\":\"Leq\",\"Flag\":\"N\"},{\"Avg\":\"75.85665130615233\",\"Rtd\":\"75.85665130615233\",\"ParamID\":\"a34002\",\"Flag\":\"N\"},{\"Avg\":\"23.92491912841797\",\"Rtd\":\"23.92491912841797\",\"ParamID\":\"a01001\",\"Flag\":\"N\"},{\"Avg\":\"109.90145874023438\",\"Rtd\":\"109.90145874023438\",\"ParamID\":\"a05024\",\"Flag\":\"N\"},{\"Avg\":\"29.177051544189453\",\"Rtd\":\"29.177051544189453\",\"ParamID\":\"a34004\",\"Flag\":\"N\"}],\"DataTime\":\"2019-06-25 05:45:00\"}}";

		T20x1Message msg = JSON.parseObject(json, T20x1Message.class);

		String json1 = JSON.toJSONString(msg);

		System.out.println(json1);
	}

}

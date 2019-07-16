/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：GkAlarmHandler.java
* 包  名  称：com.zeei.das.aps.alarm
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年7月31日上午11:34:01
* 
* 修改历史
* 1.0 quanhongsheng 2017年7月31日上午11:34:01 创建文件
*
*/

package com.zeei.das.aps.alarm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.aps.notice.NoticeHandler;
import com.zeei.das.aps.service.AlarmProcessService;
import com.zeei.das.aps.vo.AlarmInfoVO;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：ScanAlarmHandler 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：wudahe
 */
@Component("scanAlarmHandler")
public class ScanAlarmHandler {

	@Autowired
	AlarmProcessService service;

	@Autowired
	NoticeHandler noticeHandler;

	private static long count = 0;

	private String getStringNoBlank(String str) {
		if (str != null && !"".equals(str)) {
			Pattern p = Pattern.compile("\\r|\\n|\\s");
			Matcher m = p.matcher(str);
			String strNoBlank = m.replaceAll("");
			return strNoBlank;
		} else {
			return str;
		}
	}

	private String readJson(String fileName) {
		BufferedReader reader = null;
		String laststr = "";
		try {
			FileInputStream fileInputStream = new FileInputStream(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr += tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return getStringNoBlank(laststr);

	}

	public static JSONObject scanJson = new JSONObject();

	static String time = "60";

	public void alarmHandler() {

		// 读取配置文件
		String rootPath = System.getProperty("common.dir");
		String smsFile = rootPath + "sms.properties";
		String config = readJson(smsFile);

		JSONObject json = JSON.parseObject(config);
		JSONArray array = json.getJSONArray("sms");
		if (array != null && !array.isEmpty()) {
			for (Object o : array) {
				JSONObject jo = (JSONObject) o;
				String ruleId = jo.getString("ruleid");
				String phones = jo.getString("phone");
				if (!StringUtil.isEmptyOrNull(ruleId) && !StringUtil.isEmptyOrNull(phones)) {
					scanJson.put(ruleId, phones);
				}
			}
		}

		time = json.getString("intervaltime");

		try {
			Runnable runnable = new Runnable() {
				public void run() {

					// 刚启动，先把所有的历史告警记录（离线、超标）update为1，避免发很多历史数据通知
					if (count == 0) {
						if (service.updateAlarmPush() == false) {
							System.out.print("更新ISPUSH状态失败！");
						}
					} else {
						Date endTime = DateUtil.getCurrentDate();

						int interval = 0;
						if (!StringUtils.isEmpty(time) && StringUtils.isNumeric(time)) {
							interval = 0 - Integer.parseInt(time);
						} else {
							interval = 0 - 30; // 默认半小时
						}

						Date beginTime = DateUtil.dateAddMin(endTime, interval); // 多少分钟前（配置）

						List<AlarmInfoVO> alarms = service.scanAlarmInfo(beginTime, endTime);

						if (alarms != null) {

							for (AlarmInfoVO alarm : alarms) {
								noticeHandler.scanHandler(alarm);

								AlarmInfoVO vo = JSON.parseObject(JSON.toJSONString(alarm), AlarmInfoVO.class);
								vo.setIsPush(1);
								if (service.updateAlarmInfo(vo) == false) {
									System.out.print("扫描告警，更新Push状态失败！");
								}
							}
						}
					}
					count++;
				}
			};
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
			service.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.MINUTES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

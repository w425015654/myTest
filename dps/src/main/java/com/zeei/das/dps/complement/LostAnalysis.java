/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：LostAnalysis.java
* 包  名  称：com.zeei.das.dps.complement
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月22日下午3:37:34
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月22日下午3:37:34 创建文件
*
*/

package com.zeei.das.dps.complement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.service.LostDataServiceImpl;
import com.zeei.das.dps.vo.LostDataRecordVO;
import com.zeei.das.dps.vo.PointSiteVO;
import com.zeei.das.dps.vo.T20x1Message;

/**
 * 类 名 称：LostAnalysis 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component
public class LostAnalysis {

	@Autowired
	Publish publish;

	@Autowired
	LostDataServiceImpl lostDataService;

	private static Logger logger = LoggerFactory.getLogger(LostAnalysis.class);


	public void lostAnalysisHandler(T20x1Message data) {

		try {

			String MN = data.getMN();
			String CN = data.getCN();
			Date dataTime = data.getCP().getDataTime();

			// 获取内存站点配置数据
			PointSiteVO station = DpsService.stationCfgMap.get(MN);

			if (station == null) {
				return;
			}

			// 站点数据间隔
			int interval = 5;
			// 站点数据时间
			Date preDataTime = null;
			long diff = 0;
			switch (CN) {
			case DataType.RTDATA:
				interval = station.getrInterval();
				preDataTime = station.getrDataTime();

				if (preDataTime == null) {
					station.setrDataTime(dataTime);
					return;
				}

				diff = DateUtil.dateDiffSecond(preDataTime, dataTime);
				if (diff > 0) {
					station.setrDataTime(dataTime);
				}
				break;
			case DataType.MINUTEDATA:
				interval = station.getmInterval();
				preDataTime = station.getmDataTime();
				if (preDataTime == null) {
					station.setmDataTime(dataTime);
					return;
				}

				diff = DateUtil.dateDiffMin(preDataTime, dataTime);
				if (diff > 0) {
					station.setmDataTime(dataTime);
				}
				break;
			case DataType.HOURDATA:
				interval = station.gethInterval();
				preDataTime = station.gethDataTime();
				if (preDataTime == null) {
					station.sethDataTime(dataTime);
					return;
				}

				diff = DateUtil.dateDiffHour(preDataTime, dataTime);
				if (diff > 0) {
					station.sethDataTime(dataTime);
				}
				break;
			case DataType.DAYDATA:
				interval = station.getdInterval();
				preDataTime = station.getdDataTime();
				if (preDataTime == null) {
					station.setdDataTime(dataTime);
					return;
				}

				diff = DateUtil.dateDiffDay(preDataTime, dataTime);
				if (diff > 0) {
					station.setdDataTime(dataTime);
				}
				break;
			}

			// logger.info(String.format("站点[%s]：%s %s - %s - %s - %s",
			// station.getPointCode(),CN,station.getrDataTime(),station.getmDataTime(),station.gethDataTime(),station.getdDataTime()));

			// 不支持补数
			if ("0".equals(DpsService.complement) || !station.isSupplement()) {
				return;
			}

			// 判断是否丢数
			if (diff >= interval * 2) {

				Date currDataTime = preDataTime;

				List<LostDataRecordVO> list = new ArrayList<LostDataRecordVO>();

				do {

					LostDataRecordVO lostVO = new LostDataRecordVO();
					switch (CN) {
					case DataType.RTDATA:
						currDataTime = DateUtil.dateAddSecond(currDataTime, interval);
						break;
					case DataType.MINUTEDATA:
						currDataTime = DateUtil.dateAddMin(currDataTime, interval);
						break;
					case DataType.HOURDATA:
						currDataTime = DateUtil.dateAddHour(currDataTime, interval);
						break;
					case DataType.DAYDATA:
						currDataTime = DateUtil.dateAddDay(currDataTime, interval);
						break;
					}

					lostVO.setCN(CN);
					lostVO.setDataTime(currDataTime);
					lostVO.setMN(MN);
					lostVO.setPreDataTime(preDataTime);
					lostVO.setPointCode(station.getPointCode());
					lostVO.setInterval(interval);
					list.add(lostVO);
					logger.info("缺失数据点：" + JSON.toJSONString(lostVO));

				} while (currDataTime.getTime() < dataTime.getTime());

				if (list.size() > 0) {
					lostDataService.insertLostDataBatch(list);
				}
			}

			// 补数完成，删除丢失数据表
			if (diff < 0) {

				LostDataRecordVO lostVO = new LostDataRecordVO();
				lostVO.setCN(CN);
				lostVO.setDataTime(dataTime);
				lostVO.setCN(CN);
				lostDataService.deleteLostData(lostVO);
			}

		} catch (Exception e) {
			e.printStackTrace();
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}

	}

}

/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：LatestDataTimeJob.java
* 包  名  称：com.zeei.das.dps.complement
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月26日上午10:18:07
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月26日上午10:18:07 创建文件
*
*/

package com.zeei.das.dps.complement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.service.LostDataServiceImpl;
import com.zeei.das.dps.vo.DataTimeVO;
import com.zeei.das.dps.vo.PointSiteVO;

/**
 * 类 名 称：LatestDataTimeJob 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

@Component
public class LatestDataTimeJob {

	@Autowired
	Publish publish;

	@Autowired
	LostDataServiceImpl lostDataService;

	private static int CYCLE = 5;

	public void jobHandler() {

		try {
			Runnable runnable = new Runnable() {
				public void run() {

					try {

						Map<String, DataTimeVO> map = new HashMap<String, DataTimeVO>();

						for (PointSiteVO station : DpsService.stationCfgMap.values()) {

							DataTimeVO vo = new DataTimeVO();
							vo.setrDataTime(station.getrDataTime());
							vo.setmDataTime(station.getmDataTime());
							vo.sethDataTime(station.gethDataTime());
							vo.setdDataTime(station.getdDataTime());
							vo.setPointCode(station.getPointCode());
							vo.setUpTime(DateUtil.getCurrentDate());
							map.put(station.getPointCode(), vo);
						}

						List<DataTimeVO> list = new ArrayList<DataTimeVO>();

						for (DataTimeVO vo : map.values()) {
							list.add(vo);
						}

						if (list.size() > 0) {
							lostDataService.insertDataTimeBatch(list);
						}

					} catch (Exception e) {
						e.printStackTrace();
						publish.send(Constant.MQ_QUEUE_LOGS,
								LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
					}
				}
			};
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间

			service.scheduleAtFixedRate(runnable, 1, CYCLE, TimeUnit.MINUTES);
		} catch (Exception e) {
			e.printStackTrace();
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}

}

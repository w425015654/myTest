/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：CompCmdJob.java
* 包  名  称：com.zeei.das.dps.complement
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月26日上午10:19:12
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月26日上午10:19:12 创建文件
*
*/

package com.zeei.das.dps.complement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.QNUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.service.LostDataServiceImpl;
import com.zeei.das.dps.vo.CtlRecVO;
import com.zeei.das.dps.vo.LostDataRecordVO;
import com.zeei.das.dps.vo.PointSiteVO;

/**
 * 类 名 称：CompCmdJob 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component
public class CompCmdJob {

	@Autowired
	Publish publish;

	@Autowired
	LostDataServiceImpl lostDataService;

	private static int CYCLE = 1;

	public void jobHandler() {

		try {
			Runnable runnable = new Runnable() {
				public void run() {

					try {

						String beginTime = DateUtil.dateToStr(DateUtil.dateAddDay(new Date(), -30),
								"yyyy-MM-dd HH:mm:ss");
						String endTime = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
						List<LostDataRecordVO> list = lostDataService.queryLostData(beginTime, endTime);

						String CN = "";
						String MN = "";
						Date preDataTime = null;
						Date nextDataTime = null;

						if (list != null && list.size() > 0) {

							List<CtlRecVO> cmds = new ArrayList<CtlRecVO>();

							for (LostDataRecordVO lost : list) {

								PointSiteVO vo = DpsService.stationCfgMap.get(lost.getMN());

								// 判断站点是否支持补数
								if (vo == null || !vo.isSupplement()) {
									continue;
								}

								if (StringUtil.isEmptyOrNull(MN) || StringUtil.isEmptyOrNull(CN)
										|| preDataTime == null) {
									CN = lost.getCN();
									MN = lost.getMN();
									preDataTime = lost.getDataTime();
									nextDataTime = lost.getDataTime();
									continue;
								}

								int interval = lost.getInterval();
								String cmd_endTime = DateUtil.dateToStr(nextDataTime, "yyyyMMddHHmmss");

								switch (CN) {

								case "2011":
									nextDataTime = DateUtil.dateAddSecond(nextDataTime, interval);
									break;
								case "2051":
									nextDataTime = DateUtil.dateAddMin(nextDataTime, interval);
									break;
								case "2061":
									nextDataTime = DateUtil.dateAddHour(nextDataTime, interval);
									break;
								case "2031":
									nextDataTime = DateUtil.dateAddDay(nextDataTime, interval);
									break;

								}

								Date dataTime = lost.getDataTime();
								String cmd_beginTime = DateUtil.dateToStr(preDataTime, "yyyyMMddHHmmss");

								String ST = "";

								if (MN.equals(lost.getMN())) {

									if (CN.equals(lost.getCN())) {

										if (nextDataTime.getTime() == dataTime.getTime()) {
											continue;
										}
										CtlRecVO recVo = generateCmd(CN, ST, MN, cmd_beginTime, cmd_endTime);
										cmds.add(recVo);
										preDataTime = dataTime;
										nextDataTime = dataTime;
										CN = lost.getCN();
										MN = lost.getMN();

									} else {

										CtlRecVO recVo = generateCmd(CN, ST, MN, cmd_beginTime, cmd_endTime);
										cmds.add(recVo);
										preDataTime = dataTime;
										nextDataTime = dataTime;
										CN = lost.getCN();
										MN = lost.getMN();
									}

								} else {
									CtlRecVO recVo = generateCmd(CN, ST, MN, cmd_beginTime, cmd_endTime);
									cmds.add(recVo);
									preDataTime = dataTime;
									nextDataTime = dataTime;
									CN = lost.getCN();
									MN = lost.getMN();
								}
							}

							if (cmds.size() > 0) {
								// lostDataService.insertLostDataCmdBatch(cmds);
							}

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

			service.scheduleAtFixedRate(runnable, 1, CYCLE, TimeUnit.HOURS);
		} catch (Exception e) {
			e.printStackTrace();
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}

	private CtlRecVO generateCmd(String CN, String ST, String MN, String beginTime, String endTime) {

		String QN = QNUtil.getQN("1");

		// 生成补数命令
		// QN=201705121111580025588045987112;ST=32;CN=2061;PW=123456;MN=0001;Flag=0;CP=&&BeginTime=20170512000000002,EndTime=20170512111158002&&

		String msg = String.format("QN=%s;ST=%s;CN=%s;PW=123456;MN=%s;Flag=0;CP=&&BeginTime=%s,EndTime=%s&&", QN, ST,
				CN, MN, beginTime, endTime);

		// System用户在2017-05-12
		// 11:13:24进行了补发站点(0001)在时间段(2017-05-12
		// 0:00:00到2017-05-12 11:11:58)内所有数据操作

		String remark = String.format("系统自动补数在%s进行了补发站点(%s)在时间段(%s到%s)内所有数据操作",
				DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"), MN, beginTime, endTime);

		CtlRecVO recVO = new CtlRecVO();
		recVO.setDataTime(DateUtil.getCurrentDate());
		recVO.setQN(QN);
		recVO.setMN(MN);
		recVO.setMsg(msg);
		recVO.setRemark(remark);

		publish.send(Constant.MQ_QUEUE_TCC, JSON.toJSONString(recVO));
		return recVO;

	}

}

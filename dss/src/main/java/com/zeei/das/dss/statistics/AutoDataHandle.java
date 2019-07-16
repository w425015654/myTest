/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：YMStatisticsJob.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月8日上午8:29:00
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月8日上午8:29:00 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.mq.Publish;
import com.zeei.das.dss.service.QueryDataService;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.AuditLogVO;
import com.zeei.das.dss.vo.MonitorDataVO;

/**
 * @类 名 称：AutoReviewJob
 * @类 描 述：自动复核
 * @功能描述：自动复核
 * @创建作者：quanhongsheng
 */
@Component("autoDataHandle")
public class AutoDataHandle {

	@Autowired
	StroageService stroageService;

	@Autowired
	QueryDataService queryDataService;

	@Autowired
	Publish publish;

	private static Logger logger = LoggerFactory.getLogger(AutoDataHandle.class);

	/**
	 * 
	 * autoAudit:自动审核
	 * 
	 * @param MN
	 * @param pointCode
	 * @param datatime
	 * @param ST
	 *            void
	 */
	public void autoAudit(String MN, String pointCode, Date datatime, String ST) {

		try {

			String tableName = PartitionTableUtil.getTableName(ST, DataType.HOURDATA, pointCode, datatime);

			Date end = DateUtil.dateAddHour(datatime, DssService.auditTime * (-1));
			String endTime = DateUtil.dateToStr(end, "yyyy-MM-dd HH:mm:ss");

			Date begin = DateUtil.dateAddDay(end, -1);
			String beginTime = DateUtil.dateToStr(begin, "yyyy-MM-dd HH:mm:ss");

			List<MonitorDataVO> rets = queryDataService.queryAuditData(tableName, pointCode, beginTime, endTime);

			Set<String> times = new HashSet<String>();

			if (rets != null && rets.size() > 0) {
				rets.stream().forEach(s -> {
					String time = s.getDataTime();
					times.add(time);
				});
			}

			if (!times.isEmpty()) {

				Integer count = stroageService.auditData(tableName, pointCode, beginTime, endTime);

				if (count > 0) {
					for (String t : times) {
						Date tt = DateUtil.strToDate(t, "yyyy-MM-dd HH:mm:ss");
						// 发送审核日志
						AuditLogVO log = new AuditLogVO();
						log.setPointCode(pointCode);
						log.setRemark("数据自动审核!");
						log.setDataTime(tt);
						log.setAuditType(3);
						log.setAuditTime(DateUtil.getCurrentDate());
						Map<String, Object> log_map = new HashMap<String, Object>();
						log_map.put("logType", LogType.LOG_TYPE_AUDIT);
						log_map.put("logContent", log);
						publish.send(Constant.MQ_QUEUE_LOGS, JSON.toJSONString(log_map));
						logger.info(String.format("自动审核小时数据 %s %s ", t, pointCode));
					}
				}
			}

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 
	 * autoReview:自动复核
	 * 
	 * @param MN
	 * @param pointCode
	 * @param datatime
	 * @param ST
	 *            void
	 */
	public void autoReview(String MN, String pointCode, Date datatime, String ST) {

		try {
			String tableName = PartitionTableUtil.getTableName(ST, DataType.HOURDATA, pointCode, datatime);

			Date end = DateUtil.dateAddHour(datatime, DssService.reviewTime * (-1));
			String endTime = DateUtil.dateToStr(end, "yyyy-MM-dd HH:mm:ss");

			Date begin = DateUtil.dateAddDay(end, -1);
			String beginTime = DateUtil.dateToStr(begin, "yyyy-MM-dd HH:mm:ss");

			List<AuditLogVO> rets = queryDataService.queryAuditLog(pointCode, beginTime, endTime);

			Set<String> times = new HashSet<String>();

			Date maxTime = null;

			if (rets != null && rets.size() > 0) {

				maxTime = rets.get(0).getDataTime();

				rets.stream().forEach(s -> {
					Date time = s.getDataTime();
					if (time != null) {
						times.add(DateUtil.dateToStr(time, "yyyy-MM-dd HH:mm:ss"));
					}
				});

				if (!times.isEmpty()) {

					Integer count = stroageService.reviewData(tableName, pointCode, null,
							DateUtil.dateToStr(maxTime, "yyyy-MM-dd HH:mm:ss"));

					if (count > 0) {

						for (String t : times) {

							Date tt = DateUtil.strToDate(t, "yyyy-MM-dd HH:mm:ss");
							if (null == tt) {
								logger.error("时间转换异常,转换时间串为:" + t + ";转换格式为yyyy-MM-dd HH:mm:ss");
							}

							// 发送审核日志
							AuditLogVO log = new AuditLogVO();
							log.setPointCode(pointCode);
							log.setRemark("数据自动复核!");
							log.setDataTime(tt);
							log.setAuditType(4);
							log.setAuditTime(DateUtil.getCurrentDate());
							Map<String, Object> log_map = new HashMap<String, Object>();
							log_map.put("logType", LogType.LOG_TYPE_AUDIT);
							log_map.put("logContent", log);

							logger.info(String.format("自动复核小时数据 %s %s ", t, pointCode));
							publish.send(Constant.MQ_QUEUE_LOGS, JSON.toJSONString(log_map));

						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 
	 * auditData:审核数据
	 * 
	 * @param CN
	 * @param pointCode
	 * @param datatime
	 * @param ST
	 *            void
	 */
	public void autoAuditData(String MN, String CN, String pointCode, Date datatime, String ST) {

		try {

			String beginTime = DateUtil.dateToStr(datatime, "yyyy-MM-dd HH:mm:ss");
			String endTime = null;

			switch (CN) {

			case DataType.DAYDATA:
				endTime = DateUtil.dateToStr(DateUtil.dateAddDay(datatime, 1), "yyyy-MM-dd HH:mm:ss");
				break;
			case DataType.MONTHDATA:
				endTime = DateUtil.dateToStr(DateUtil.dateAddMonth(datatime, 1), "yyyy-MM-dd HH:mm:ss");
				break;
			case DataType.YEARDATA:
				endTime = DateUtil.dateToStr(DateUtil.dateAddYear(datatime, 1), "yyyy-MM-dd HH:mm:ss");
				break;
			}

			String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, datatime);
			stroageService.auditData(tableName, pointCode, beginTime, endTime);

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 
	 * multiAudit:多级级联审核
	 * 
	 * @param pointCode
	 * @param datatime
	 * @param ST
	 *            void
	 */
	public void multiAudit(String MN, String pointCode, Date datatime, String ST) {
		try {
			String time = DateUtil.dateToStr(datatime, "yyyy-MM-dd");
			String ctime = DateUtil.getCurrentDate("yyyy-MM-dd");

			String dtime = null;

			if (!ctime.equals(time)) {

				dtime = String.format("%s 00:00:00", time);
				autoAuditData(MN, DataType.DAYDATA, pointCode, DateUtil.strToDate(dtime, "yyyy-MM-dd HH:mm:ss"), ST);

				logger.error(String.format("审核日数据 %s %s ", dtime, pointCode));

				time = DateUtil.dateToStr(datatime, "yyyy-MM");
				ctime = DateUtil.getCurrentDate("yyyy-MM");

				if (!ctime.equals(time)) {

					dtime = String.format("%s-01 00:00:00", time);
					autoAuditData(MN, DataType.MONTHDATA, pointCode, DateUtil.strToDate(dtime, "yyyy-MM-dd HH:mm:ss"),
							ST);
					logger.error(String.format("审核月数据 %s %s ", dtime, pointCode));

					time = DateUtil.dateToStr(datatime, "yyyy");
					ctime = DateUtil.getCurrentDate("yyyy");
					if (!ctime.equals(time)) {

						dtime = String.format("%s-01-01 00:00:00", time);
						autoAuditData(MN, DataType.YEARDATA, pointCode,
								DateUtil.strToDate(dtime, "yyyy-MM-dd HH:mm:ss"), ST);
						logger.error(String.format("审核年数据 %s %s ", dtime, pointCode));
					}
				}
			}
		} catch (Exception e) {
			logger.error("级联审核日，月，年数据异常:", e);
		}

	}

}

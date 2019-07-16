package com.zeei.das.dps.audit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.service.LostDataServiceImpl;
import com.zeei.das.dps.vo.AuditLogVO;
import com.zeei.das.dps.vo.PointSiteVO;
import com.zeei.das.dps.vo.PolluteVO;
import com.zeei.das.dps.vo.T20x1Message.MessageBody.Parameter;
import com.zeei.das.dps.vo.T20x1VO;

/**
 * @类型名称：DoubtfulAudit
 * @类型描述：【气态参数（SO2、NO、O3、CO）6小时无变化，颗粒物（PM10、PM2.5） 4小时无变化】 @功能描述：对空气数据长时间保持不变的情况下增加存疑标识
 * @创建作者：zhanghu
 *
 */
@Component
public class SuppDoubtfulAudit {

	private static Logger logger = LoggerFactory.getLogger(SuppDoubtfulAudit.class);

	@Autowired
	Publish publish;

	@Autowired
	LostDataServiceImpl lostDataService;

	public void auditHandler(String MN, Date dataTime, List<Parameter> data) {

		try {
			PointSiteVO station = DpsService.stationCfgMap.get(MN);

			// 数据为空不进一步判断
			if (data == null) {
				return;
			}
			List<T20x1VO> suppList = lostDataService.querySuppData(station.getPointCode(),
					DoubtfulAudit.errTimeMap.keySet(),
					DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, -6), "yyyy-MM-dd HH:mm:ss"),
					DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, 6), "yyyy-MM-dd HH:mm:ss"),
					"T_ENV_MONI_AIR_DATAHH");

			Map<String, List<T20x1VO>> suppMap = suppList.stream()
					.collect(Collectors.groupingBy(T20x1VO::getPolluteCode));
			// 结果集
			List<T20x1VO> resultList = new ArrayList<>();
			// 判断数据是否已经持续一段时间未发生变化
			for (Parameter param : data) {

				String polluteCode = param.getParamID();
				// 只比较SO2、NO、O3、CO、pm25和pm10的数值，否则跳过
				if (DoubtfulAudit.errTimeMap.containsKey(polluteCode) && suppMap.containsKey(polluteCode)) {

					// 发送审核日志
					String polluteName = "";
					PolluteVO pollute = DpsService.polluteMap.get(polluteCode);

					if (pollute != null) {
						polluteName = pollute.getPolluteName();
					}
					AuditLogVO log = new AuditLogVO();
					log.setPointCode(station.getPointCode());
					log.setPolluteCode(param.getParamID());
					Map<String, Object> log_map = new HashMap<String, Object>();
					log_map.put("logType", LogType.LOG_TYPE_AUDIT);

					// 存疑超标时长
					int timeSize = DoubtfulAudit.errTimeMap.get(polluteCode);
					List<T20x1VO> doubtfulList = suppMap.get(polluteCode);

					// 4小时时 过滤掉多余数据
					doubtfulList = doubtfulList.stream()
							.filter(u -> Math.abs(DateUtil.dateDiffHour(dataTime, u.getDeDataTime())) <= timeSize)
							.collect(Collectors.toList());
					// 获取当前补数数据是否在数据库存在（补数不会覆盖存在数据，所以需要判断，存在数据不变，不存在则使用补数数据替代）
					List<T20x1VO> nowData = doubtfulList.stream().filter(u -> u.getDeDataTime().equals(dataTime))
							.collect(Collectors.toList());
					if (CollectionUtils.isEmpty(nowData)) {
						T20x1VO t20x1 = new T20x1VO();
						t20x1.setAuditValue(param.getAvg());
						t20x1.setDataTime(DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
						t20x1.setPointCode(Integer.valueOf(station.getPointCode()));
						t20x1.setPolluteCode(polluteCode);
						doubtfulList.add(t20x1);
					}
					// 从小到大排序
					doubtfulList = doubtfulList.stream().sorted(Comparator.comparing(T20x1VO::getDataTime))
							.collect(Collectors.toList());
					int size = 0;
					Double value = null;
					Date lastTime = null;
					for (T20x1VO doubtful : doubtfulList) {
						// 判断时间记录是否是第一次
						if (size == 0) {
							size++;
							value = doubtful.getAuditValue();
							lastTime = doubtful.getDeDataTime();
							continue;
						}
						// 连续相同则视为连续数据 值计为连续数据，否则重新开始记录
						if (value.equals(doubtful.getAuditValue())
								&& DateUtil.dateDiffHour(lastTime, doubtful.getDeDataTime()) == 1) {
							size++;
							// 数据连续时间是否到达存疑限制
							if (size > timeSize) {
								// 当前数据改为存疑
								resultList.add(doubtful);
								// 发送审核日志
								log.setDataTime(dataTime);
								String info = String.format(
										"由于%s下%s因子数据%s长时间未发生变化(SO2、NO、O3、CO为6小时，PM10、PM2.5为4小时)数据审核设置为可疑数据(补数情况)",
										station.getPointName(), polluteName, doubtful.getDataTime());
								logger.info(info);
								log.setRemark(info);
								log_map.put("logContent", log);
								publish.send(Constant.MQ_QUEUE_LOGS, JSON.toJSONString(log_map));
							}

						} else {
							size = 1;
							value = doubtful.getAuditValue();
						}
						lastTime = doubtful.getDeDataTime();
					}
				}

			}
			// 如果有需要处理的存疑数据，则入库
			if (CollectionUtils.isNotEmpty(resultList)) {
				lostDataService.updateDoubtfulHH(resultList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

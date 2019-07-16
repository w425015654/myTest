package com.zeei.das.dss.statistics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.SpecialFactor;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.mq.Publish;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.SitePolluterVo;
import com.zeei.das.dss.vo.StationVO;

@Component("antipateDataStatis")
public class AntipateDataStatis {

	@Autowired
	Publish publish;

	@Autowired
	StroageService stroageService;

	private static Logger logger = LoggerFactory.getLogger(AntipateDataStatis.class);

	// @Scheduled(cron = "0/30 * * * * ?")

	// 每15秒钟触发
	@Scheduled(cron = "0/15 * * * * ? ")
	public void jobHandler() {
		try {	
 
			for (StationVO station : DssService.stationMap.values()) {
				cycleHandler(station, DataType.RTDATA);
				cycleHandler(station, DataType.MINUTEDATA);
				cycleHandler(station, DataType.HOURDATA);
			}

		} catch (Exception e) {
			logger.error("",e);
		}
	}

	/**
	 * 
	 * calculationShouldTransmission:应传个数
	 * 
	 * @param station
	 * @param CN
	 * @param dataTime
	 * @return
	 */
	public void calculationShouldTransmission(StationVO station, String CN, Date dataTime) {

		try {

			Set<String> pollutes = DssService.validFactor.get(station.getPointCode());
			int size = 0;
			if (pollutes != null) {

				size = pollutes.size();
			}

			String ST = station.getST();
			String MN = station.getMN();
			String pointCode = station.getPointCode();
			int total = 0;
			int cycle = 0;
			
			String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);
			//表名不存在，不统计
			if(StringUtils.isBlank(tableName)){
				
				logger.error(String.format("统计应传个数异常: 系统%s下测点%s的CN为%s对应表名不存在",ST,pointCode,CN));
				return;
			}
			// 按不同时间类型计数
			switch (CN) {
			case DataType.MINUTEDATA:

				total = station.getmInterval() * 60;
				cycle = station.getrInterval();

				break;
			case DataType.HOURDATA:
				total = station.gethInterval() * 60;
				cycle = station.getmInterval();

				break;
			case DataType.DAYDATA:
				// 水的特别处理，确定参数后需计算特定因子数量
				total = 24;
				cycle = 1;

				break;
			case DataType.MONTHDATA:
				// 获取没有的天数
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dataTime);
				total = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				cycle = 1;
				break;
			case DataType.YEARDATA:
				// 1年12月
				total = 12;
				cycle = 1;
				break;
			}

			Double yc = 0d;

			if (cycle != 0) {
				yc = Math.ceil(total / cycle);
			}
			Double yg = yc * size;

			// 小时数据量需额外计算
			if (DataType.DAYDATA.equalsIgnoreCase(CN)) {
				
				Integer hhcycletime = null;
				yg = 0d;
				//特殊小时周期数据处理
				if(StringUtils.isNotEmpty(station.getPointCode())){
					//获取测点下所有因子
					Map<String, List<SitePolluterVo>> siteMap = DssService.waterHourPollutes.get(station.getPointCode());
					if(siteMap != null){
						for(Entry<String, List<SitePolluterVo>> sitePolluter : siteMap.entrySet()){
							//参与统计的因子
							if(sitePolluter.getValue().get(0).getIsstat()==1){
								int hhcyc = sitePolluter.getValue().get(0).getHhcycletime();
								yg = yg + 24/hhcyc;
								if(hhcycletime == null || hhcyc<hhcycletime){
									hhcycletime = hhcyc;
								}
							}
						}
					}
				}
				yc = hhcycletime==null?0d:Math.ceil(24/hhcycletime);
			}

			MonitorDataVO vo = new MonitorDataVO();
			vo.setDataTime(RegularTime.regularDateStr(CN,MN, dataTime));
			vo.setDataValue(yc);
			vo.setMaxValue(yc);
			vo.setMinValue(yc);
			vo.setAuditValue(yc);
			vo.setPolluteCode(SpecialFactor.TransmissionShouldGroupCode);
			vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			vo.setPointCode(pointCode);

			// 應傳組數
			MonitorDataVO vo1 = new MonitorDataVO();
			vo1.setDataTime(RegularTime.regularDateStr(CN,MN, dataTime));
			vo1.setDataValue(yg);
			vo1.setMaxValue(yg);
			vo1.setMinValue(yg);
			vo1.setAuditValue(yg);
			vo1.setPolluteCode(SpecialFactor.TransmissionShouldCode);
			vo1.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			vo1.setPointCode(pointCode);

			List<MonitorDataVO> list = new ArrayList<MonitorDataVO>();

			list.add(vo);
			list.add(vo1);

			if (DataType.MONTHDATA.equals(CN) || DataType.YEARDATA.equals(CN)) {
				stroageService.insertYMDataByBatch(tableName, list);
			} else {
				stroageService.insertDataBatch(tableName, list);
			}

			logger.info(String.format("应传个数[%s]：%s", CN, JSON.toJSONString(vo)));

		} catch (Exception e) {
			logger.error("统计应传个数异常:", e);
		}

	}

	/**
	 * 
	 * cycleHandler:监测数据周期处理
	 *
	 * @param data
	 *            T212 消息数据
	 * 
	 *            void
	 */
	public void cycleHandler(StationVO station, String CN) {

		try {

			String SCN = CN;
			Date date = DateUtil.getCurrentDate();
			// 站点数据上报周期
			int interval = 5;

			// 站点数据周期
			long preCycle = 0;

			switch (CN) {
			case DataType.RTDATA:
				interval = station.getmInterval() * 60;
				preCycle = station.getmCycle();
				SCN = DataType.MINUTEDATA;
				break;
			case DataType.MINUTEDATA:
				interval = station.gethInterval() * 60 * 60;
				preCycle = station.gethCycle();
				SCN = DataType.HOURDATA;
				break;
			case DataType.HOURDATA:
				interval = 1 * 24 * 60 * 60;
				preCycle = station.getdCycle();
				SCN = DataType.DAYDATA;
				break;
			}
			// 获取当前数据周期
			long cycle = getCycle(date, interval);

			if (cycle > preCycle) {
				switch (CN) {
				case DataType.RTDATA:
					station.setmCycle(cycle);
					break;
				case DataType.MINUTEDATA:
					station.sethCycle(cycle);
					break;
				case DataType.HOURDATA:
					station.setdCycle(cycle);
					break;
				}
			}
			// 相邻2个数据周期不等，发送应传个数统计消息
			if (cycle != preCycle) {

				long miss = cycle * interval * 1000;
				date.setTime(miss);
				//处理数据
				calculationShouldTransmission(station, SCN, date);
			}

		} catch (Exception e) {
			e.printStackTrace();
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}

	/**
	 * 
	 * getCycle:根据上报时间间隔， 计算数据隶属周期
	 *
	 * @param date
	 *            数据时间
	 * @param interval
	 *            上报时间间隔
	 * @return long
	 */
	public static long getCycle(Date date, int interval) {
		// 默认值
		if (interval == 0) {
			interval = 5;
		}
		interval = interval * 1000;
		Long time = date.getTime();
		long cycle = time / interval;
		return cycle;
	}


}

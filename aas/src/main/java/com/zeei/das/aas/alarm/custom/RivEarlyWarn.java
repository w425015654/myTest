package com.zeei.das.aas.alarm.custom;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.aas.AasService;
import com.zeei.das.aas.alarm.AlarmIDUtil;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.service.QueryDataService;
import com.zeei.das.aas.utils.RegressionLine;
import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.aas.vo.DataPointVo;
import com.zeei.das.aas.vo.MonitorDataVO;
import com.zeei.das.aas.vo.PolluterLevelVo;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.RivNineParam;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;

/** 
* @类型名称：RivEarlyWarn
* @类型描述：
* @功能描述：水质趋势预警计算
* @创建作者：zhanghu
*
*/
@EnableScheduling
@Component("rivEarlyWarn")
public class RivEarlyWarn {
	
	@Autowired
	QueryDataService queryDataService;
	
	@Autowired
	Publish publish;
	 
	//参与统计的周期为一小时的数据因子
	private static List<String> oneHourPollute = Arrays.asList(new String[] { RivNineParam.WT,RivNineParam.PH,
			RivNineParam.DO,RivNineParam.EC,RivNineParam.NTU });
	
	private static String alarmCode = "90007";

	private static String alarmType = "34";
	
	//趋势预警设置周期数量
	public static Integer periodNum;
	
	private static Logger logger = LoggerFactory.getLogger(RivEarlyWarn.class);
	// 每个小时的第6分钟触发统计
	@Scheduled(cron = "0 6 * * * ?")
	public void jobHandler() {
		try {
			
			if (periodNum != null && periodNum > 1) {
				logger.info("执行水质趋势预警计算！");
				String tableName = "T_ENV_MONI_RIV_DATAHH";
				List<String> pollutrs = oneHourPollute;
				String endTime = DateUtil.getCurrentDate("yyyy-MM-dd HH") + ":00:00";
				Date dataTime = DateUtil.strToDate(endTime,"yyyy-MM-dd HH:mm:ss");
				
				for(Entry<Integer, List<String>> waterMaps : AasService.waterHourPolluteMaps.entrySet()){
					
					int cyc = waterMaps.getKey();
					//处理当前周期因子的趋势预警
					if(dataTime.getTime()%(3600000*cyc)==0){
						
						pollutrs = waterMaps.getValue();
						String beginTime = DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, cyc*periodNum*-1),"yyyy-MM-dd HH:mm:ss");
						List<MonitorDataVO> monDatas = queryDataService.queryMonitorData(tableName, SystemTypeCode.RIV, beginTime, endTime, null, pollutrs);
						//对于周期小时的数据  统计趋势预警
						warnStatis(monDatas,dataTime,cyc);
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
	}
	
	
	/**
	 * warnStatis:趋势预警计算统计
	 * 
	 * @param monDatas
	 * @param period void
	 */
	private void warnStatis(List<MonitorDataVO> monDatas,Date dataTime,int period){
		
		//数据分组按测点，污染因子 分组
		Map<String,Map<String,List<MonitorDataVO>>> monDataMaps = monDatas.stream().collect(Collectors.groupingBy(MonitorDataVO
				:: getPointCode,Collectors.groupingBy(MonitorDataVO :: getPolluteCode)));
		
		if(monDataMaps.isEmpty()){
			return;
		}
		//计算预期周期时间
		Date nowDate = DateUtil.dateAddHour(dataTime, period);
		//遍历所有站点数据
		for(Entry<String, Map<String, List<MonitorDataVO>>> monDataMap : monDataMaps.entrySet()){
			
			String pointCode = monDataMap.getKey();
			//不为空 ，需要遍历数据
			if(monDataMap.getValue().isEmpty()){
				continue;
			}

			//遍历测点下的所有因子数据
			for(Entry<String, List<MonitorDataVO>>  monentry : monDataMap.getValue().entrySet()){
				
				String polluteCode = monentry.getKey();
				// 根据站点ID，告警码和因子ID，取md5 作为规则的ID
				String alarmId = AlarmIDUtil.generatingAlarmID(pointCode, alarmCode, polluteCode, DataType.T2061);
				//获取告警
				AlarmInfoVO alarm = AasService.alarmMap.get(alarmId);
				if( alarm != null){
					
					alarm.setEndTime(DateUtil.dateAddMin(alarm.getStartTime(), 5));
					alarm.setNewAlarm(false);
	
					String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
							SerializerFeature.WriteDateUseDateFormat);
					publish.send(Constant.MQ_QUEUE_ALARM, json);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));
					String info = String.format("水站点:%s 小时数据[%s趋势预警超标]消警---%s", pointCode,polluteCode,
							DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
					logger.info(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));
					AasService.alarmMap.remove(alarmId);
				}
				List<MonitorDataVO> monitors = monentry.getValue();
				//数量不达标或者缺失数量大于1,未配置相关评判标准也不计算趋势预警
				if(!AasService.rivPolluteLevelMap.containsKey(polluteCode) || monitors.size() < 2 || monitors.size() < periodNum-1){
					continue;
				}
				
				Float rivEarlyValue = getRivEarlyValue(monitors, dataTime, period);
				//预期告警超标 且不存在告警
				if(isOverweight(polluteCode, rivEarlyValue)){
					
					alarm = new AlarmInfoVO();
					alarm.setAlarmCode(alarmCode);
					alarm.setDataType(DataType.T2061);
					alarm.setStartTime(nowDate);
					alarm.setStorage(true);
					alarm.setNewAlarm(true);
					alarm.setPointCode(pointCode);
					alarm.setPolluteCode(polluteCode);
					alarm.setAlarmType(alarmType);
					alarm.setAlarmValue(String.valueOf(rivEarlyValue));
					AasService.alarmMap.put(alarmId, alarm);
					
					String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
							SerializerFeature.WriteDateUseDateFormat);

					publish.send(Constant.MQ_QUEUE_ALARM, json);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));
					String info = String.format("水站点:%s 小时数据[%s趋势预警超标]---%s", pointCode,polluteCode,
							DateUtil.dateToStr(nowDate, "yyyy-MM-dd HH:mm:ss"));
					logger.info(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));
					
				}
			}
		}
		
	}
	
	/**
	 * getRivEarlyValue:获取预期值
	 * 
	 * @param datas
	 * @param dataTime
	 * @param period
	 * @return Double
	 */
	private Float getRivEarlyValue(List<MonitorDataVO> datas,Date dataTime,int period){
		
		Float result = null;
		
		try {
			RegressionLine regressionLine = new RegressionLine();
			//遍历 按时间排序，定义坐标，定线性方程
			for(MonitorDataVO data : datas){
				//日期差值
				long difHh = DateUtil.dateDiffHour(data.getDeDataTime(), dataTime);
				long size = difHh/period;
				
				regressionLine.addDataPoint(new DataPointVo(periodNum-size, data.getDataValue()));
			}
			result = regressionLine.getK() * (periodNum + 1) + regressionLine.getB();
			
		} catch (Exception e) {
			
			logger.error("计算趋势告警，线性回归函数出错" + e.getMessage());
			return result;
		}
		return result != null && result < 0 ? 0 : result;
	}
	
	
	/**
	 * isOverweight:
	 * 判断预期值 是否超标
	 * @param pollute
	 * @param value
	 * @return boolean
	 */
	private boolean isOverweight(String pollute, Float value){
		
		PolluterLevelVo pollLevel = AasService.rivPolluteLevelMap.get(pollute);
		
		if(pollLevel != null && value != null){
			//ph特殊计算处理
			if(RivNineParam.PH.equals(pollute)){
				if(value.compareTo(pollLevel.getsMaxValue())>0 || value.compareTo(pollLevel.getsMinValue())<0){
					//不在设定的不超标范围内，则表示超标
					return true;
				}
				return false;
			}
			int orderNum = pollLevel.getOrderNum();
			float standard;
			//排序字段来确立判断标准
			if(orderNum < 0){
				standard = pollLevel.getsMinValue();
			}else{
				standard = pollLevel.getsMaxValue();
			}
			if(orderNum * value > orderNum * standard){
				//预期值超标
				return true;
			}
		}
		return false;
		
	}

}

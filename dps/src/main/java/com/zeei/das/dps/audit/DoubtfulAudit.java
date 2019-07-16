package com.zeei.das.dps.audit;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.AirSixParam;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.NumberFormatUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.vo.AuditLogVO;
import com.zeei.das.dps.vo.DoubtfulVo;
import com.zeei.das.dps.vo.PointSiteVO;
import com.zeei.das.dps.vo.PolluteVO;
import com.zeei.das.dps.vo.T20x1Message.MessageBody.Parameter;

/** 
* @类型名称：DoubtfulAudit
* @类型描述：【气态参数（SO2、NO、O3、CO）6小时无变化，颗粒物（PM10、PM2.5） 4小时无变化】
* @功能描述：对空气数据长时间保持不变的情况下增加存疑标识
* @创建作者：zhanghu
*
*/
@Component
public class DoubtfulAudit {

	private static Logger logger = LoggerFactory.getLogger(DoubtfulAudit.class);
	
	public static Map<String,Integer> errTimeMap = new HashMap<>();
	//初始化时间集，表示污染物在多长时间未改变则增加存疑标识
	static {
		errTimeMap.put(AirSixParam.CO, 6);
		errTimeMap.put(AirSixParam.O3, 6);
		errTimeMap.put(AirSixParam.NO, 6);
		errTimeMap.put(AirSixParam.NO2, 6);
		errTimeMap.put(AirSixParam.SO2, 6);
		errTimeMap.put(AirSixParam.NOx, 6);
		
		errTimeMap.put(AirSixParam.SK_CO, 6);
		errTimeMap.put(AirSixParam.SK_O3, 6);
		errTimeMap.put(AirSixParam.SK_NO, 6);
		errTimeMap.put(AirSixParam.SK_NO2, 6);
		errTimeMap.put(AirSixParam.SK_SO2, 6);
		errTimeMap.put(AirSixParam.SK_NOx, 6);
		
		errTimeMap.put(AirSixParam.PM10, 4);
		errTimeMap.put(AirSixParam.PM25, 4);
		errTimeMap.put(AirSixParam.SK_PM10, 4);
		errTimeMap.put(AirSixParam.SK_PM25, 4);
	}

	@Autowired
	Publish publish;

	public void auditHandler(String MN, Date dataTime, List<Parameter> data) {

		try {
			PointSiteVO station = DpsService.stationCfgMap.get(MN);
	
			// 数据为空不进一步判断
			if (data == null) {
				return;
			}
			//对日期取整
			dataTime = DateUtil.strToDate(DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH")+":00:00", "yyyy-MM-dd HH:mm:ss");

			// 判断数据是否已经持续一段时间未发生变化
			for (Parameter param : data) {

				String polluteCode = param.getParamID(); 

				// 只比较SO2、NO、O3、CO、pm25和pm10的数值，否则跳过
				if (errTimeMap.containsKey(polluteCode)) {

					PolluteVO pollute = DpsService.polluteMap.get(polluteCode);
					String polluteName = "";
					int numPre = 0;

					if (pollute != null) {
						polluteName = pollute.getPolluteName();
						numPre = pollute.getNumprecision();
					}
					//取整
					Double auditData = param.getAvg()==null?null:NumberFormatUtil.formatByScale(param.getAvg(), numPre);
					
					//站点mn加污染因子编码组合key
					String douKey = MN + "+" + polluteCode;
					DoubtfulVo doubtfulVo = DpsService.doubtfulMap.get(douKey);
					// 判断map是否已存在站点对应因子记录，有则进一步判断，没有则新增
					if(doubtfulVo != null && doubtfulVo.getAudValue() != null && doubtfulVo.getAudValue().equals(auditData)){
						
						//与上次时间间隔超过一小时，即中间缺数，重新开始计算
						if(DateUtil.dateDiffHour(doubtfulVo.getLastDataTime(), dataTime)>1){
							
							doubtfulVo.setAudValue(auditData);
							doubtfulVo.setDataTime(dataTime);
							doubtfulVo.setLastDataTime(dataTime);
							return;
						}
						//设定上次数据时间
						doubtfulVo.setLastDataTime(dataTime);
						// 判断数值是否连续不变且时间超过限定，不是则跳过
						if(DateUtil.dateDiffHour(doubtfulVo.getDataTime(), dataTime)>errTimeMap.get(polluteCode)){
							
							param.setDoubtful(1);
							// 发送审核日志
							AuditLogVO log = new AuditLogVO();
							String info = String.format("由于%s下%s因子数据长时间%s未发生变化(SO2、NO、O3、CO为6小时，PM10、PM2.5为4小时)数据审核设置为可疑数据", station.getPointName(),
									polluteName,DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));

							log.setPointCode(station.getPointCode());
							log.setPolluteCode(param.getParamID());
							log.setRemark(info);
							log.setDataTime(dataTime);

							Map<String, Object> log_map = new HashMap<String, Object>();
							log_map.put("logType", LogType.LOG_TYPE_AUDIT);
							log_map.put("logContent", log);
							logger.info(info);							
							publish.send(Constant.MQ_QUEUE_LOGS, JSON.toJSONString(log_map));
						}
						
					}else{
						doubtfulVo = new DoubtfulVo();
						doubtfulVo.setAudValue(auditData);
						doubtfulVo.setDataTime(dataTime);
						doubtfulVo.setLastDataTime(dataTime);
						DpsService.doubtfulMap.put(douKey, doubtfulVo);
				  }
					
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}


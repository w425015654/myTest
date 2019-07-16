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
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.vo.AuditLogVO;
import com.zeei.das.dps.vo.PointSiteVO;
import com.zeei.das.dps.vo.PolluteVO;
import com.zeei.das.dps.vo.T20x1Message.MessageBody.Parameter;

/**
 * @类型名称：SpecialValueAudit @类型描述：
 * @功能描述：特殊数值修约， 现在暂时只有pm10和pm25的特殊值修约
 * @创建作者：zhanghu
 *
 */
@Component
public class SpecialValueAudit {

	private static Logger logger = LoggerFactory.getLogger(SpecialValueAudit.class);

	@Autowired
	Publish publish;

	public void auditHandler(String MN, Date dataTime, List<Parameter> data) {

		try {
			PointSiteVO station = DpsService.stationCfgMap.get(MN);
	
			// 只计算空气下的因子
			if (data == null) {
				return;
			}

			// 判断数据是否是国家标准中的985无效值
			for (Parameter param : data) {

				String polluteCode = param.getParamID();

				// 只比较pm25和pm10的数值，否则跳过
				if (AirSixParam.PM10.equals(polluteCode) || AirSixParam.PM25.equals(polluteCode)
						|| AirSixParam.SK_PM10.equals(polluteCode) || AirSixParam.SK_PM25.equals(polluteCode)) {

					Double auditData = param.getAvg();
					// 判断数值是否是985，不是则跳过
					if (auditData !=null && auditData == 985d) {

						param.setIsValided(0);

						String polluteName = "";

						PolluteVO pollute = DpsService.polluteMap.get(polluteCode);

						if (pollute != null) {
							polluteName = pollute.getPolluteName();
						}

						// 发送审核日志
						AuditLogVO log = new AuditLogVO();
						String info = String.format("根据国家标准将测点%s(%s为985国家限定无效值)数据自动审核为%s", station.getPointName(),
								polluteName, "无效");

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
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

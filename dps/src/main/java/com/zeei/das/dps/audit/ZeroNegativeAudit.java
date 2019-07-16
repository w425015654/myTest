/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：ZeroNegativeAudit.java
* 包  名  称：com.zeei.das.dps.audit
* 文件描述：空气质量监测数据0与负值的自动审核实现类
* 创建日期：2017年4月27日上午10:11:45
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月27日上午10:11:45 创建文件
*
*/

package com.zeei.das.dps.audit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.vo.AuditLogVO;
import com.zeei.das.dps.vo.PolluteVO;
import com.zeei.das.dps.vo.RuleParamVO;
import com.zeei.das.dps.vo.T20x1Message.MessageBody.Parameter;
import com.zeei.das.dps.vo.ZeroNegativeVO;

/**
 * 类 名 称：ZeroNegativeAudit 类 描 述：空气质量监测数据0与负值的自动审核实现类 功能描述：空气质量监测数据0与负值的自动审核实现类
 * 创建作者：quanhongsheng
 */
@Component
public class ZeroNegativeAudit implements Audit {

	@Autowired
	Publish publish;

	private static Logger logger = LoggerFactory.getLogger(ZeroNegativeAudit.class);

	@Override
	public void auditHandler(RuleParamVO rule, Date dataTime, List<Parameter> data) {

		try {
			// 修约上限
			Double upperLimit = 0d;
			// 修约下限
			Double lowerLimit = 0d;
			// 修约值
			Double roundingValue = 3d;

			ZeroNegativeVO parameter = (ZeroNegativeVO) rule.getParameter();

			if (parameter == null || data == null) {
				return;
			}

			Optional<Parameter> params = data.stream().filter(o -> o.getParamID().equals(parameter.getPolluteCode()))
					.findFirst();

			if (!params.isPresent()) {
				return;
			}
			Parameter param = params.get();

			// 站点数据标识位优先，如果判定为无效，即为无效，如果属于有效数据再进行0与负值的修约。
			if (param != null && param.getIsValided() == 1) {

				upperLimit = parameter.getSmaxValue();
				lowerLimit = parameter.getSminValue();
				roundingValue = parameter.getAuditValue();

				if (upperLimit == null || lowerLimit == null) {
					String err = String.format("0与负值的自动审核规则错误：%s", JSON.toJSONString(parameter));
					logger.error(err);
					return;
				}

				Double value = null;

				if (DataType.RTDATA.equalsIgnoreCase(rule.getDataType())) {
					value = param.getRtd();
				} else {
					value = param.getAvg();
				}

				if (value == null) {
					param.setIsValided(0);
					return;
				}

				int isValided = 1;

				Double auditValue = value;

				if (value > lowerLimit && value <= upperLimit) {
					auditValue = roundingValue;
				} else if (value <= lowerLimit) {
					isValided = 0;
				}

				if (isValided != param.getIsValided() || auditValue != value) {

					String polluteCode = param.getParamID();
					String polluteName = "";
					PolluteVO pollute = DpsService.polluteMap.get(polluteCode);

					if (pollute != null) {
						polluteName = pollute.getPolluteName();
					}

					// 发送审核日志
					AuditLogVO log = new AuditLogVO();
					String info = String.format("根据0与负值审核规则将测点(%s)[%s]数据审核为%s[原始值：%s 审核值：%s]", rule.getPointName(),
							polluteName, isValided == 0 ? "无效" : "有效", value, dataRounding(auditValue, 3));
					log.setAuditData(auditValue);
					log.setDataRT(value);
					log.setPointCode(rule.getPointCode());
					log.setPolluteCode(rule.getPolluteCode());
					log.setRemark(info);
					log.setDataTime(dataTime);

					Map<String, Object> log_map = new HashMap<String, Object>();
					log_map.put("logType", LogType.LOG_TYPE_AUDIT);
					log_map.put("logContent", log);
					logger.info(info);

					publish.send(Constant.MQ_QUEUE_LOGS, JSON.toJSONString(log_map));
				}

				param.setRound(auditValue);
				// 无效数据0不能再修约成有效数据1
				if (0 != param.getIsValided() || isValided != 1) {
					param.setIsValided(isValided);
				}

			}
		} catch (Exception e) {
			logger.error("", e);
		}

	}

	/**
	 * 
	 * dataRounding:数据修约
	 *
	 * @param value
	 * @param precision
	 *            精度
	 * @return Double
	 */
	private Double dataRounding(Double value, int precision) {

		try {
			if (value == null) {
				return null;
			}

			BigDecimal bg = new BigDecimal(value).setScale(precision, RoundingMode.UP);

			return bg.doubleValue();
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;

	}

}

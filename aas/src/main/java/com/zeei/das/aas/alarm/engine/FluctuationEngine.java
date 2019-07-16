/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：FluctuationEngine.java
* 包  名  称：com.zeei.das.aas.alarm.engine
* 文件描述：波动告警采集
* 创建日期：2017年4月21日上午8:40:39
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月21日上午8:40:39 创建文件
*
*/

package com.zeei.das.aas.alarm.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.aas.AasService;
import com.zeei.das.aas.alarm.Alarm;
import com.zeei.das.aas.alarm.ParamUtil;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.vo.AlarmRuleVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：FluctuationEngine 类 描 述：波动告警采集 功能描述：波动告警采集 创建作者：quanhongsheng
 */
@Component("fluctuationEngine")
public class FluctuationEngine implements RuleEngine {

	private static Logger logger = LoggerFactory.getLogger(Alarm.class);

	@Autowired
	Publish publish;

	@Override
	public int analysis(AlarmRuleVO rule, JSONArray params) {

		String paramId = rule.getPolluteCode();
		String pointCode = rule.getPointCode();

		String expression = rule.getFormula();

		if (StringUtil.isEmptyOrNull(expression)) {
			logger.warn(String.format("站点：%s 波动告警波动值为空！", rule.getPointCode()));
			return 2;
		}

		try {

			// 查询因子对象
			JSONObject param = ParamUtil.findParam(paramId, params);

			if (param == null) {
				String err = String.format("站点:%s因子(%s)不存在，请检查公式配置！", pointCode, paramId);
				logger.error(err);
				return 2;
			}

			// 监测值
			Double value = 0d;

			if (DataType.T2011.equalsIgnoreCase(rule.getDataType())) {
				value = param.getDouble("Rtd");
			} else {
				value = param.getDouble("Avg");
			}

			String ruleId = rule.getRuleId();

			// 根据规则id ,获取规则分析内存数据
			List<Double> f_a = AasService.fluctuationMap.get(ruleId);

			// 内存数据不存在，添加到内存中，并直接返回
			if (f_a == null || f_a.size() < 2) {
				f_a = new ArrayList<Double>();
				f_a.add(value);
				f_a.add(value);
				AasService.fluctuationMap.put(ruleId, f_a);
				return 2;
			}

			// 将当期检测值加入，内存数据中
			f_a.add(Double.valueOf(value));
			// 内存数据进行排序
			Collections.sort(f_a);
			// 移除中间数据
			f_a.remove(1);

			Double min = f_a.get(0);
			Double max = f_a.get(1);

			// 计算最大值最小值差值，取绝对值
			Double abs = Math.abs(max - min);

			// 数据波动范围
			Double fluctuation = Double.valueOf(rule.getFormula());

			rule.setAlarmValue(value.toString());

			//
			if (abs <= fluctuation) {
				return 1;

			} else {

				// 移除内存数据
				AasService.fluctuationMap.remove(ruleId);
				return 0;
			}

		} catch (Exception e) {

			String error = String.format("站点：%s 因子[%s] 波动告警分析失败！", pointCode, paramId);
			logger.error(error);

			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, error));
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
			return 2;
		}
	}
}

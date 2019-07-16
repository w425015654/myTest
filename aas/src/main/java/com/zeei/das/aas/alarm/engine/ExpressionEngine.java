/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：ExpressionEngine.java
* 包  名  称：com.zeei.das.aas.alarm.engine
* 文件描述：表达式告警采集
* 创建日期：2017年4月21日上午8:40:39
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月21日上午8:40:39 创建文件
*
*/

package com.zeei.das.aas.alarm.engine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.aas.alarm.Alarm;
import com.zeei.das.aas.alarm.ParamUtil;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.vo.AlarmRuleVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

import net.sourceforge.jeval.Evaluator;

/**
 * 类 名 称：ExpressionEngine 类 描 述：表达式告警采集   超标告警 功能描述：表达式告警采集 创建作者：quanhongsheng
 */
@Component("expressionEngine")
public class ExpressionEngine implements RuleEngine {

	private static Logger logger = LoggerFactory.getLogger(Alarm.class);

	// 表达式计算器
	private static Evaluator evaluator = new Evaluator();

	@Autowired
	Publish publish;

	@Override
	public int analysis(AlarmRuleVO rule, JSONArray params) {

		int result = 0;
		String expression = rule.getFormula();

		if (StringUtil.isEmptyOrNull(expression)) {
			logger.warn(String.format("站点：%s表达式为空！", rule.getPointCode()));
			return 2;
		}
		String pointCode = rule.getPointCode();
		try {
			// 替换表达式中的 and or 英文字符，忽略大小写
			expression = expression.replaceAll("(?i)and", "&&").replaceAll("(?i)or", "||");
			// 头部统一为（Key)=(Value) 格式的表达式
			String reg = "([\\w]+).Rtd";
			Double alarmValue = 0d;
			// 找出全部匹配的 K-V
			// 编译正则表达式
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(expression);
			while (matcher.find()) {
				String paramId = matcher.group(1);

				JSONObject param = ParamUtil.findParam(paramId, params);

				if (param == null) {
					String err = String.format("站点:%s因子(%s)不存在，请检查公式配置！", pointCode, paramId);
					logger.error(err);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, err));
					result = 2;
					break;
				}

				Double value;
				if (DataType.T2011.equalsIgnoreCase(rule.getDataType())) {
					// 取2011消息的检测值
					value = param.getDouble("Rtd");
				} else {
					// 去2031,3051,2061消息监测值
					value = param.getDouble("Avg");
				}

				alarmValue = value;
				// 替换表达式中的因子占位符
				expression = expression.replaceAll(String.format("%s.Rtd", paramId), value.toString());
			}

			if (result == 2) {
				return result;
			}

			rule.setAlarmValue(alarmValue.toString());

			// 获取表达式的结果
			boolean ret = evaluator.getBooleanResult(expression);

			return ret ? 1 : 0;

		} catch (Exception e) {

			String error = String.format("站点:%s 表达式【%s】解析异常. %s", pointCode, expression,JSON.toJSONString(params));			
			logger.error(error);
			
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, error));
			return 2;
		}

	}

}

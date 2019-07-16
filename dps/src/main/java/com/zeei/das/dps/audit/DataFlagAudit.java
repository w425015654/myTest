/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：ExpressionAudit.java
* 包  名  称：com.zeei.das.dps.audit
* 文件描述：表达式规则审核实现类
* 创建日期：2017年4月27日上午10:11:45
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月27日上午10:11:45 创建文件
*
*/

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
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.vo.AuditLogVO;
import com.zeei.das.dps.vo.DataFlagAuditVO;
import com.zeei.das.dps.vo.PolluteVO;
import com.zeei.das.dps.vo.RuleParamVO;
import com.zeei.das.dps.vo.T20x1Message.MessageBody.Parameter;

/**
 * @类 名 称：ExpressionAudit
 * @类 描 述：表达式规则审核实现类
 * @功能描述：表达式规则审核实现类
 * @ 创建作者：quanhongsheng
 */
@Component
public class DataFlagAudit implements Audit {

	private static Logger logger = LoggerFactory.getLogger(DataFlagAudit.class);

	@Autowired
	Publish publish;

	@Override
	public void auditHandler(RuleParamVO rule, Date dataTime, List<Parameter> data) {

		try {
			DataFlagAuditVO parameter = (DataFlagAuditVO) rule.getParameter();

			if (parameter == null || data == null) {
				return;
			}

			String dataFlag = parameter.getDataFlag();

			Integer isvalid = parameter.getIsvalid();

			int isValided = 0;

			for (Parameter param : data) {

				if (!StringUtil.isEmptyOrNull(dataFlag) && dataFlag.equalsIgnoreCase(param.getFlag())) {

					isValided = param.getIsValided();
                    param.setIsValided(isvalid);					

					if (isValided != isvalid) {

						String polluteCode = param.getParamID();

						String polluteName = "";

						PolluteVO pollute = DpsService.polluteMap.get(polluteCode);

						if (pollute != null) {
							polluteName = pollute.getPolluteName();
						}

						// 发送审核日志
						AuditLogVO log = new AuditLogVO();
						String info = String.format("根据%s将测点(%s)[%s]数据自动审核为%s", parameter.getRuleName(),
								rule.getPointName(), polluteName, isvalid == 0 ? "无效" : "有效");

						log.setPointCode(rule.getPointCode());
						log.setPolluteCode(param.getParamID());
						log.setRemark(info);
						log.setDataTime(dataTime);

						if ("1".equals(rule.getDataType())) {
							log.setAuditData(param.getRtd());
							log.setDataRT(param.getRtd());
						} else {
							log.setAuditData(param.getAvg());
							log.setDataRT(param.getAvg());
						}

						Map<String, Object> log_map = new HashMap<String, Object>();
						log_map.put("logType", LogType.LOG_TYPE_AUDIT);
						log_map.put("logContent", log);
						logger.info(info);

						publish.send(Constant.MQ_QUEUE_LOGS, JSON.toJSONString(log_map));
					}
				}

			}
		} catch (Exception e) {
			logger.error("", e);
		}

	}

}

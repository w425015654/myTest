/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：AuditFactory.java
* 包  名  称：com.zeei.das.dps.audit
* 文件描述：自动修约工厂类
* 创建日期：2017年4月27日上午10:10:27
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月27日上午10:10:27 创建文件
*
*/

package com.zeei.das.dps.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.vo.AuditRuleVO;
import com.zeei.das.dps.vo.RuleParamVO;
import com.zeei.das.dps.vo.T20x1Message;
import com.zeei.das.dps.vo.T20x1Message.MessageBody;

/**
 * 类 名 称：AuditFactory 类 描 述：自动修约工厂类 功能描述：自动修约工厂类 创建作者：quanhongsheng
 */
@Component
public class AuditFactory {

	private static Logger logger = LoggerFactory.getLogger(AuditFactory.class);

	@Autowired
	Publish publish;

	@Autowired
	ZeroNegativeAudit zeroNegativeAudit;

	@Autowired
	DataFlagAudit dataFlagAudit;

	@Autowired
	ExcludeTimeAudit excludeTimeAudit;

	@Autowired
	SpecialValueAudit specialValueAudit;
	
	@Autowired
	DoubtfulAudit doubtfulAudit;
	
	@Autowired
	SuppDoubtfulAudit suppDoubtfulAudit;
	
	@Autowired
	RivDataAuditing rivDataAuditing;


	public void audtHandler(T20x1Message data) {

		try {

			String MN = data.getMN();
			String CN = data.getCN();
			String ST = data.getST();
			MessageBody body = data.getCP();

			// 启动异常申报/停产时段，数据无效修约
			if (DpsService.isTC == 1) {
				excludeTimeAudit.auditHandler(MN, body.getDataTime(), body.getParams());
			}

			AuditRuleVO ruleVO = DpsService.auditRuleMap.get(MN);

			if (ruleVO != null) {

				List<RuleParamVO> auditRules = new ArrayList<RuleParamVO>();

				switch (CN) {
				case DataType.RTDATA:
					auditRules = ruleVO.getR2011();
					break;
				case DataType.DAYDATA:
					auditRules = ruleVO.getR2031();
					break;
				case DataType.MINUTEDATA:
					auditRules = ruleVO.getR2051();
					break;
				case DataType.HOURDATA:
					auditRules = ruleVO.getR2061();
					break;
				}

				Audit engine = null;
				
				// 优先级排序
				Collections.sort(auditRules); 


				for (RuleParamVO rule : auditRules) {

					if (rule == null) {
						continue;
					}

					String ruleCode = rule.getRuleCode();

					// 根据修约规则码，构造对应的分析引擎
					switch (ruleCode) {
					// 空气质量监测数据0与负值的自动审核
					case "90001":
						engine = zeroNegativeAudit;
						break;
					case "90002":
						engine = dataFlagAudit;
						break;
					}
					rule.setDataType(CN);
					if (engine != null) {
						engine.auditHandler(rule, body.getDataTime(), body.getParams());
					}

				}
			}

			if (SystemTypeCode.AIR.equals(ST)) {
				// 判断空气下的pm10和pm25的值
				specialValueAudit.auditHandler(MN, body.getDataTime(), body.getParams());
				//对空气下某些因子 长时间值不变化，增加数据存疑标识
				if(DataType.HOURDATA.equals(CN)){
					
					String SUPP = data.getSUPP();
					//补数数据需要存疑审核
					if("T".equals(SUPP)){
						suppDoubtfulAudit.auditHandler(MN, body.getDataTime(), body.getParams());
					}else{
						//只针对小时数据存疑审核
						doubtfulAudit.auditHandler(MN, body.getDataTime(), body.getParams());
					}
				}
			}else if(SystemTypeCode.RIV.equals(ST) && DataType.HOURDATA.equals(CN)){
				//地表水对应的数据审核标识
				rivDataAuditing.auditHandler(MN, body.getDataTime(), body.getParams());
			}

		} catch (Exception e) {

			logger.error("", e);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}

	}

}

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
import java.util.List;

import org.springframework.stereotype.Component;

import com.zeei.das.dps.vo.RuleParamVO;
import com.zeei.das.dps.vo.T20x1Message.MessageBody.Parameter;

/**
 * 类 名 称：ExpressionAudit 类 描 述：表达式规则审核实现类 功能描述：表达式规则审核实现类 创建作者：quanhongsheng
 */
@Component
public class ExpressionAudit implements Audit {

	@Override
	public void auditHandler(RuleParamVO rule, Date dataTime, List<Parameter> data) {

	}

}

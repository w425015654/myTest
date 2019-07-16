/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：Audit.java
* 包  名  称：com.zeei.das.dps.audit
* 文件描述：数据自动审核接口类
* 创建日期：2017年4月27日上午10:10:55
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月27日上午10:10:55 创建文件
*
*/

package com.zeei.das.dps.audit;

import java.util.Date;
import java.util.List;

import com.zeei.das.dps.vo.RuleParamVO;
import com.zeei.das.dps.vo.T20x1Message.MessageBody.Parameter;

/**
 * 类 名 称：Audit 类 描 述：数据自动审核接口类 功能描述：数据自动审核接口类 创建作者：quanhongsheng
 */

public interface Audit {

	/**
	 * 
	 * auditHandler:自动修约处理函数
	 *
	 * @param rule
	 *            修约规则
	 * @param data
	 *            修约数据
	 * 
	 *            void
	 */
	public void auditHandler(RuleParamVO rule, Date dataTime, List<Parameter> data);

}

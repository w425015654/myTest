/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：RuleEngine.java
* 包  名  称：com.zeei.das.aas.alarm
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月21日上午8:21:51
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月21日上午8:21:51 创建文件
*
*/

package com.zeei.das.aas.alarm.engine;

import com.alibaba.fastjson.JSONArray;
import com.zeei.das.aas.vo.AlarmRuleVO;

/**
 * 类 名 称：RuleEngine 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface RuleEngine {

	public int analysis(AlarmRuleVO expression, JSONArray params);

}

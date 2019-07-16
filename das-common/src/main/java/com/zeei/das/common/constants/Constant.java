/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：MQConstant.java
* 包  名  称：com.zeei.das.cgs.common.constants
* 文件描述：MQ 队列常量
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.common.constants;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 类 名 称：MQConstant 类 描 述：MQ 队列常量 功能描述：MQ 队列常量 创建作者：quanhongsheng
 */

public class Constant {

	// 监测数据跟踪队列
	public static final String MQ_QUEUE_TT212 = "TT212";

	// 实时数据待处理队列
	public static final String MQ_QUEUE_TT2011 = "ET2011";

	// 日数据待处理队列
	public static final String MQ_QUEUE_TT2031 = "ET2031";

	// 分钟数据待处理队列
	public static final String MQ_QUEUE_TT2051 = "ET2051";

	// 小时数据待处理队列
	public static final String MQ_QUEUE_TT2061 = "ET2061";

	// 进行告警分析的数据队列
	public static final String MQ_QUEUE_TM212 = "TM212";

	// 其他数据待处理队列
	public static final String MQ_QUEUE_TC212 = "TC212";

	// 命令下发
	public static final String MQ_QUEUE_TCC = "TCC";
	
	// 质控命令队列
	public static final String MQ_QUEUE_QC = "QcAudit";

	// 站点配置更新
	public static final String MQ_QUEUE_STATIONCFG = "StationCfg";

	// 因子映射更新
	public static final String MQ_QUEUE_PARAMRULE = "ParamRule";

	// 异常数据
	public static final String MQ_QUEUE_TE212 = "TE212";
	
	// 补传超时数据
	public static final String MQ_QUEUE_TP212 = "TP212";

	// 日志数据队列
	public static final String MQ_QUEUE_LOGS = "Logs";

	// 服务心跳报告
	public static final String MQ_QUEUE_REPORT = "Report";

	// 告警信息队列
	public static final String MQ_QUEUE_ALARM = "Alarm";

	// 告警信息队列
	public static final String MQ_QUEUE_ALARMRULE = "AlarmRule";

	// web端发送消息队列
	public static final String MQ_QUEUE_WEB = "Control";

	// 规则更新通知队列
	public static final String MQ_QUEUE_NOTICERULE = "NoticeRule";

	// 自动审核规则
	public static final String MQ_QUEUE_AUDITRULE = "AuditRule";

	// 告警入库失败队列
	public static final String MQ_QUEUE_FAIL = "AlarmFail";

	// 通知信息数据
	public static final String MQ_QUEUE_NOTICE = "Notice";

	// 通知信息数据
	public static final String MQ_QUEUE_NOTICE_FAIL = "NoticeFail";

	// 时间格式
	public static final SimpleDateFormat JSONDATEFORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

	// 入库失败队列
	public static final String RDPS_FAIL_QUEUE = "T2011Fail";
	public static final String MDPS_FAIL_QUEUE = "T2051Fail";
	public static final String HDPS_FAIL_QUEUE = "T2061Fail";
	public static final String DDPS_FAIL_QUEUE = "T2031Fail";

	public static final Map<String, String> T20X1_FAIL_QUEUES = new HashMap<String, String>();

	// 实时数据队列 =
	public static final String RDPS_QUEUE = "T2011";
	// 分钟数据队列
	public static final String MDPS_QUEUE = "T2051";
	// 小时数据队列
	public static final String HDPS_QUEUE = "T2061";
	// 天数据队列
	public static final String DDPS_QUEUE = "T2031";

	// 生存期限制临时队列, 超过生存期自动清除，调试用
	public static final String RDPS_TTL_QUEUE = "T2011-TTL";
	public static final String MDPS_TTL_QUEUE = "T2051-TTL";
	public static final String HDPS_TTL_QUEUE = "T2061-TTL";
	public static final String DDPS_TTL_QUEUE = "T2031-TTL";

	// 时间格式
	public static final SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// t212 数据周期
	public static final String MQ_QUEUE_CYCLE = "T212Cycle";

	static {
		T20X1_FAIL_QUEUES.put("2011", "T2011Fail");
		T20X1_FAIL_QUEUES.put("2031", "T2031Fail");
		T20X1_FAIL_QUEUES.put("2051", "T2051Fail");
		T20X1_FAIL_QUEUES.put("2061", "T2061Fail");
	}

}

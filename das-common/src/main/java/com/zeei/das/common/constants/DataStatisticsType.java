/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：das.common
* 文件名称：DataStatisticsType.java
* 包  名  称：com.zeei.das.common.constants
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年7月18日上午8:25:16
* 
* 修改历史
* 1.0 quanhongsheng 2017年7月18日上午8:25:16 创建文件
*
*/

package com.zeei.das.common.constants;

/**
 * 类 名 称：DataStatisticsType 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

public class DataStatisticsType {

	// 监测数据有效值统计
	public final static String DST_20X1A = "20x1";
	
	// 应传个数统计
	public final static String DST_YCGS = "y20x1";

	// 监测数据统计
	public final static String DST_20X1G = "g20x1";

	// 通用数据统计，包含有效个数,传输个数，传输组数，联通个数
	public final static String DST_General = "general";

	// 空气质量AQI统计
	public final static String DST_AQI = "aqi";

	// 质控合格个数统计
	public final static String DST_QC = "qc";
	
	// 實況數據統計
	public final static String DST_SK = "sk";

	// 应传个数统计
	public final static String DST_SP = "sp";

	// 工况统计---网络异常
	public final static String DST_GK_Network = "network";

	// 工况统计---参数异常
	public final static String DST_GK_ParamException = "paramexception";

	// 工况统计---超标时长
	public final static String DST_GK_Overproof = "overproof";

	// 工况统计---停运时长
	public final static String DST_GK_Outage = "outage";

	// 工况统计---运行时长
	public final static String DST_GK_Runtime = "runtime";

}

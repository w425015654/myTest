/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：das.common
* 文件名称：SpecialFactor.java
* 包  名  称：com.zeei.das.common.constants
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月24日上午9:07:26
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月24日上午9:07:26 创建文件
*
*/

package com.zeei.das.common.constants;

/**
 * 类 名 称：SpecialFactor 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class SpecialFactor {

	public static String PH = "w01001";

	// 风机状态编码
	public final static String FJ = "ua34006";

	// 精净化器状态编码
	public final static String JHQ = "ua34007";
	
	// 风机净化器传输个数
	public final static String FJ_JHQ = "ua34022";

	// 传输个数 
	public final static String TransmissionCode = "ua34009";

	// 有效个数 
	public final static String EffectivenessCode = "ua34010";

	// 联动个数
	public final static String CONNECTIVITYPOLLUTECODE = "ua34011";

	// 传输个数 按组统计
	public final static String TransmissionGroupCode = "ua34012";

	// 應收個數
	public final static String TransmissionShouldCode = "ua34013";
	
	// 應收個數(按组统计)
	public final static String TransmissionShouldGroupCode = "ua34021";

	// 质控通过个数
	public final static String TransmissionQCCode = "ua34014";

	// 设施停运时长
	public final static String GKOutageCode = "ua34015";

	// 设施参数异常时长
	public final static String GKParamExceptionCode = "ua34016";

	// 网络异常时长
	public final static String GKOnlineCode = "ua34017";

	// 超标告警时长
	public final static String GKOverproofCode = "ua34018";

	// 污染物排放时长
	public final static String GKEmissionsCode = "ua34019";

	// 设施运行时长
	public final static String GKRuntimeCode = "ua34020";
	
}

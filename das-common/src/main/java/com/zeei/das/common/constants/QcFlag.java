package com.zeei.das.common.constants;

/** 
* @类型名称：QcFlag
* @类型描述：
* @功能描述：质控审核结果标识
* @创建作者：zhanghu
*
*/
public interface QcFlag {

	/**
	 * QCF:质控不合格 优先级1
	 */
	String QCF = "QCF";
	
	/**
	 * KPF:关键参数异常 优先级2
	 */
	String KPF = "KKP";
	
	/**
	 * LRF:不符合逻辑关系 优先级22
	 */
	String LRF = "LRF";
	
	/**
	 * OVL:离群偏大数据 优先级23
	 */
	String OVL = "OVL";
	
	/**
	 * OVS:离群偏小数据 优先级23
	 */
	String OVS = "OVS";
	
	/**
	 * ZF:零值异常 优先级24
	 */
	String ZF = "ZF";
	
	/**
	 * WQB:水质明显好转 优先级25
	 */
	String WQB = "WQB";
	
	/**
	 * WQW:水质明显变差 优先级25
	 */
	String WQW = "WQW";
	
	/**
	 * CF:恒值不变 优先级26
	 */
	String CF = "CF";
}

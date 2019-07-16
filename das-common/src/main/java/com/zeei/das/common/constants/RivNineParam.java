package com.zeei.das.common.constants;

import java.util.Arrays;
import java.util.List;

/** 
* @类型名称：RivNineParam
* @类型描述：
* @功能描述：水质九参数据
* @创建作者：zhanghu
*
*/
public interface RivNineParam {
	
	
	//水温
	String WT = "w01010";
	
	//PH
	String PH = "w01001";
	
	//溶解氧
	String DO = "w01009";
	
	//电导率
	String EC = "w01014";
	
	//浊度
	String NTU = "w01003";
	
	//高锰酸钾盐
	String KMNO = "w01019";
	
	//总磷
	String TP = "w21011";
	
	//总氮
	String TNB = "w21001";
	
	//氨氮
	String NH_N = "w21003";
	
	//水质评价中四小时一条的数据,也用作特殊质控因子，若要变更，请注意兼容两个地方
	List<String> fourHourPollute = Arrays.asList(new String[] { KMNO, TP, TNB, NH_N });
	
	//常规五参
	List<String> normalPollute = Arrays.asList(new String[] { WT, PH, DO, EC, NTU });
	
	//水质九参
	List<String> ninePollute = Arrays.asList(new String[] { WT, PH, DO, EC, NTU, KMNO, TP, TNB, NH_N});
}

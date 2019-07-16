package com.zeei.das.aas.vo;

/** 
* @类型名称：PolluterLevelVo
* @类型描述：
* @功能描述：污染等级标准判别等级
* @创建作者：zhanghu
*
*/
public class PolluterLevelVo {
	
	//污染因子
	private String  polluteCode;
	
	//对应等级的最大值
	private Float  sMaxValue;

	//对应等级的最小值，最大和最小只有一个，根据等级排序逻辑来
	private Float  sMinValue;
	
	//排序，正序为1，倒叙为负一
	private Integer  orderNum;
	
	//是否参与首要污染物计算的标识
	private String  isJudge;
	

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}

	public Float getsMaxValue() {
		return sMaxValue;
	}

	public void setsMaxValue(Float sMaxValue) {
		this.sMaxValue = sMaxValue;
	}

	public Float getsMinValue() {
		return sMinValue;
	}

	public void setsMinValue(Float sMinValue) {
		this.sMinValue = sMinValue;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public String getIsJudge() {
		return isJudge;
	}

	public void setIsJudge(String isJudge) {
		this.isJudge = isJudge;
	}
	
}

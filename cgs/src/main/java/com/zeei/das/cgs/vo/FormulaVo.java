package com.zeei.das.cgs.vo;


/** 
* @类型名称：FormulaVo
* @类型描述：站点转换计算公式实体vo
* @功能描述：
* @创建作者：zhanghu
*
*/
public class FormulaVo {
	
	/**
	 * formulaStr:公式具体内容
	 */
	private String formulaStr;
	
	/**
	 * polluteCode:污染因子编码
	 */
	private String polluteCode;
	
	/**
	 * mN:站点对应MN
	 */
	private String mN;
	
	/**
	 * pointCode:站点编码
	 */
	private String pointCode;
	
	
	


	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getmN() {
		return mN;
	}

	public void setmN(String mN) {
		this.mN = mN;
	}

	public String getFormulaStr() {
		return formulaStr;
	}

	public void setFormulaStr(String formulaStr) {
		this.formulaStr = formulaStr;
	}

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}
	
	
}

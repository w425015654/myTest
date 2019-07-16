package com.zeei.das.dss.vo;

/**
 * @ClassName: SitePolluterVo
 * @Description: (站点对应污染物)
 * @author zhanghu
 * @date 2018年9月14日
 *
 */

public class SitePolluterVo {

	// 站点编码
	private String pointCode;
	// 污染编码
	private String polluteCode;
	// 小时数据周期
	private Integer hhcycletime = 1;
	// 是否统计该因子
	private Integer isstat = 0;
	
	
	public Integer getHhcycletime() {
		return hhcycletime;
	}

	public void setHhcycletime(Integer hhcycletime) {
		this.hhcycletime = hhcycletime;
	}

	public Integer getIsstat() {
		return isstat;
	}

	public void setIsstat(Integer isstat) {
		this.isstat = isstat;
	}

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}

}

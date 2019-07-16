package com.zeei.das.aas.vo;

/** 
* @类型名称：DataPoint
* @类型描述：
* @功能描述：测点对应的横坐标和纵坐标
* @创建作者：zhanghu
*
*/
public class DataPointVo {
	 
	/** 坐标x轴的值，即周期数 */
	public float x;
 
	/** 坐标y轴的值，即对应周期的值 */
	public float y;
 
	
	 /**
	 * 构造方法 DataPoint
	 * 
	 * @param x 参数
	 * @param y
	 */
	public DataPointVo(long x, double y) {
		this.x = x;
		this.y = (float) y;
	}
}

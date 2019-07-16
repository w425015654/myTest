package com.zeei.das.aas.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.zeei.das.aas.vo.DataPointVo;


/** 
* @类型名称：RegressionLine
* @类型描述：
* @功能描述：计算线性回归函数
* @创建作者：zhanghu
*
*/
public class RegressionLine
{
	/** x求和 */
	private double sumX;
 
	/**  y求和 */
	private double sumY;
 
	/** x*x求和 */
	private double sumXX;
 
	/** x*y求和 */
	private double sumXY;
 
	/* y*y求和 */
	private double sumYY;
 
	/** 求和sumDeltaY^2 */
	private double sumDeltaY2;
 
	private double sst;
 
	private double E;
 
	private String[] xy;
 
	private List<String> listX;
 
	private List<String> listY;
 
	private int XMin;
	
	private int XMax;
	
	private int YMin;
	
	private int YMax;
 
	/** 线 性系数 a0 */
	private float a0;
 
	/**线 性系数  a1 */
	private float a1;
 
	/** 点数 */
	private int pn;
 
	/** 系数是否有效， */
	private boolean coefsValid;
 
	/**
	 * 构造.
	 */
	public RegressionLine() {
		XMax = 0;
		YMax = 0;
		pn = 0;
		xy = new String[2];
		listX = new ArrayList<>();
		listY = new ArrayList<>();
	}
 
	/**
	 * 构造.
	 * 
	 * @param data
	 *   测点参数         
	 */
	public RegressionLine(DataPointVo data[]) {
		pn = 0;
		xy = new String[2];
		listX = new ArrayList<>();
		listY = new ArrayList<>();
		for (int i = 0; i < data.length; ++i) {
			addDataPoint(data[i]);
		}
	}
 

	/**
	 * getDataPointCount:点数
	 * 
	 * @return int
	 */
	public int getDataPointCount() {
		return pn;
	}
 

	/**
	 * getB:获取系数B
	 * * 即y=kx+b 中的b
	 * @return float
	 */
	public float getB() {
		validateCoefficients();
		return a0;
	}
 

	/**
	 * getK:获取系数K
	 * 即y=kx+b 中的k
	 * 
	 * @return float
	 */
	public float getK() {
		validateCoefficients();
		return a1;
	}
 

	/**
	 * getSumX:获取x和
	 * 
	 * @return double
	 */
	public double getSumX() {
		return sumX;
	}
 

	/**
	 * getSumY:获取y坐标和
	 * 
	 * @return double
	 */
	public double getSumY() {
		return sumY;
	}

	/**
	 * getSumXX:获取xx只和
	 * 
	 * @return double
	 */
	public double getSumXX() {
		return sumXX;
	}
 

	/**
	 * getSumXY:获取xy和
	 * 
	 * @return double
	 */
	public double getSumXY() {
		return sumXY;
	}
 
	public double getSumYY() {
		return sumYY;
	}
 
	public int getXMin() {
		return XMin;
	}
 
	public int getXMax() {
		return XMax;
	}
 
	public int getYMin() {
		return YMin;
	}
 
	public int getYMax() {
		return YMax;
	}
 
	
	/**
	 * addDataPoint:增加点位信息x轴和y轴的值
	 * 进行数据计算
	 * 
	 * @param dataPoint void
	 */
	public void addDataPoint(DataPointVo dataPoint) {
		sumX += dataPoint.x;
		sumY += dataPoint.y;
		sumXX += dataPoint.x * dataPoint.x;
		sumXY += dataPoint.x * dataPoint.y;
		sumYY += dataPoint.y * dataPoint.y;
 
		if (dataPoint.x > XMax) {
			XMax = (int) dataPoint.x;
		}
		if (dataPoint.y > YMax) {
			YMax = (int) dataPoint.y;
		}
 
		// 把每个点的具体坐标存入ArrayList中，备用
 
		xy[0] = (int) dataPoint.x + "";
		xy[1] = (int) dataPoint.y + "";
		
		try {
			listX.add(pn, xy[0]);
			listY.add(pn, xy[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		++pn;
		coefsValid = false;
	}
 
	
	/**
	 * at:返回回归直线函数在x点的值
	 * 
	 * @param x
	 * @return float
	 */
	public float at(int x) {
		if (pn < 2)
			return Float.NaN;
 
		validateCoefficients();
		return a0 + a1 * x;
	}
 
	/**
	 * Reset.
	 */
	public void reset() {
		pn = 0;
		sumX = sumY = sumXX = sumXY = 0;
		coefsValid = false;
	}
 

	/**
	 * validateCoefficients:计算方程系数 y=kx+b 中的k，b
	 *  void
	 */
	private void validateCoefficients() {
		if (coefsValid){
			return;
		}
		if (pn >= 2) {
			float xBar = (float) sumX / pn;
			float yBar = (float) sumY / pn;
 
			a1 =  ((pn * sumXX - sumX* sumX)==0.0 ? 0 : (float) ((pn * sumXY - sumX * sumY) / (pn * sumXX - sumX* sumX)));
			a0 = (float) (yBar - a1 * xBar);
		} else {
			a0 = a1 = Float.NaN;
		}
 
		coefsValid = true;
	}
 
	/**
	 * 返回误差
	 */
	public double getR() {
		// 遍历这个list并计算分母
		for (int i = 0; i < pn - 1; i++) {
			float Yi = (float) Integer.parseInt(listY.get(i).toString());
			float Y = at(Integer.parseInt(listX.get(i).toString()));
			float deltaY = Yi - Y;
			float deltaY2 = deltaY * deltaY;
 
			sumDeltaY2 += deltaY2;
 
		}
		sst = sumYY - (sumY * sumY) / pn;
		E = 1 - sumDeltaY2 / sst;
 
		return round(E, 4);
	}
 
	// 用于实现精确的四舍五入
	public double round(double v, int scale) {
 
		if (scale < 0) {
			throw new IllegalArgumentException(
					"比例必须是正整数或零---");
		}
 
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
 
	}
 
	public float round(float v, int scale) {
 
		if (scale < 0) {
			throw new IllegalArgumentException(
					"比例必须是正整数或零----------");
		}
 
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).floatValue();
 
	}
}
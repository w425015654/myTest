/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：T20x1Message.java
* 包  名  称：com.zeei.das.dps.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午4:24:24
* 
* 修改历史
* 1.0 zhou.yongbo 2017年5月4日下午4:24:24 创建文件
*
*/

package com.zeei.das.aas.vo;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class T20x1Message {

	private String ST;

	private String MN;

	private String PW;

	private String CN;

	private String QN;

	private String ID;

	private Date HT;

	private MessageBody CP;

	@JSONField(name = "ST")
	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

	@JSONField(name = "MN")
	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	@JSONField(name = "PW")
	public String getPW() {
		return PW;
	}

	public void setPW(String pW) {
		PW = pW;
	}

	@JSONField(name = "CN")
	public String getCN() {
		return CN;
	}

	public void setCN(String cN) {
		CN = cN;
	}

	@JSONField(name = "QN")
	public String getQN() {
		return QN;
	}

	public void setQN(String qN) {
		QN = qN;
	}

	@JSONField(name = "CP")
	public MessageBody getCP() {
		return CP;
	}

	public void setCP(MessageBody cP) {
		CP = cP;
	}

	@JSONField(name = "ID")
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	@JSONField(name = "HT")
	public Date getHT() {
		return HT;
	}

	public void setHT(Date hT) {
		HT = hT;
	}

	/**
	 * 类 名 称：MessageBody 类 描 述：T20x1消息体 创建作者：zhou.yongbo
	 */
	public class MessageBody {

		
		private List<Parameter> Params;

		private Date DataTime;

		private Date DT;

		@JSONField(name = "DT")
		public Date getDT() {
			return DT;
		}

		public void setDT(Date dT) {
			DT = dT;
		}

		@JSONField(name = "Params")
		public List<Parameter> getParams() {
			return Params;
		}

		public void setParams(List<Parameter> params) {
			Params = params;
		}

		@JSONField(name = "DataTime")
		public Date getDataTime() {

			return DataTime;
		}

		public void setDataTime(Date dataTime) {
			DataTime = dataTime;
		}

		/**
		 * 类 名 称：Parameter 类 描 述：T20x1数据因子 创建作者：zhou.yongbo
		 */
		/**
		 * @类型名称：Parameter
		 * @类型描述：TODO 请修改类型描述
		 * @功能描述：TODO 请修改功能描述
		 * @创建作者：zhanghu
		 *
		 */
		public class Parameter {

			private String ParamID;

			private String Flag;

			private Double Rtd = null;

			private Double Avg;

			private Double Min;

			private Double Max;

			@JSONField(name = "ParamID")
			public String getParamID() {
				return ParamID;
			}

			public void setParamID(String paramID) {
				ParamID = paramID;
			}

			@JSONField(name = "Flag")
			public String getFlag() {
				return Flag;
			}

			public void setFlag(String flag) {
				Flag = flag;
			}

			@JSONField(name = "Rtd")
			public Double getRtd() {
				return Rtd;
			}

			public void setRtd(Double rtd) {
				Rtd = rtd;
			}

			@JSONField(name = "Avg")
			public Double getAvg() {
				return Avg;
			}

			public void setAvg(Double avg) {
				Avg = avg;
			}

			@JSONField(name = "Min")
			public Double getMin() {
				return Min;
			}

			public void setMin(Double min) {
				Min = min;
			}

			@JSONField(name = "Max")
			public Double getMax() {
				return Max;
			}

			public void setMax(Double max) {
				Max = max;
			}

		}
	}
}
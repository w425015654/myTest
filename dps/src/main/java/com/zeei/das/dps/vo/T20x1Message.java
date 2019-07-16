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

package com.zeei.das.dps.vo;

import java.util.Date;
import java.util.List;

import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dps.storage.RegularTime;

/**
 * 类 名 称：T20x1Message 类 描 述：T20x1消息封装 创建作者：zhou.yongbo
 */
public class T20x1Message {
	private String ST;
	private String MN;
	private String PW;
	private String CN;
	private String QN;
	private String ID;
	private Date samplingTime;
	private Date dataTime;
    //补数标识，暂定T为  补数数据，F为统计数据
	private String SUPP;
	// 此条消息的原始数据

	// 此条消息的rabbitmq的DeliveryTag
	private long deliveryTag = 0;

	// 此条消息的处理状态，0表示未进行处理，1表示此条消息的数据已入库， 2表示此条消息的数据入库失败
	private int status = 0;

	private MessageBody CP;
	

	public String getSUPP() {
		return SUPP;
	}

	public void setSUPP(String sUPP) {
		SUPP = sUPP;
	}

	public Date getSamplingTime() {

		return samplingTime;
	}

	public void setSamplingTime(Date samplingTime) {

		this.samplingTime = samplingTime;
	}

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	public String getPW() {
		return PW;
	}

	public void setPW(String pW) {
		PW = pW;
	}

	public String getCN() {
		return CN;
	}

	public void setCN(String cN) {
		CN = cN;
	}

	public String getQN() {
		return QN;
	}

	public void setQN(String qN) {
		QN = qN;
	}

	public MessageBody getCP() {
		return CP;
	}

	public void setCP(MessageBody cP) {
		CP = cP;
	}

	public long getDeliveryTag() {
		return deliveryTag;
	}

	public void setDeliveryTag(long deliveryTag) {
		this.deliveryTag = deliveryTag;
	}

	public int getStatus() {
		return status;
	}

	public int getParamNumber() {
		return CP.getParams().size();
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getDataTime() {

		return dataTime;
	}

	public void setDataTime(Date dataTime) {

		this.dataTime = dataTime;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	@Override
	public String toString() {
		return "T20x1Message [ST=" + ST + ", MN=" + MN + ", PW=" + PW + ", CN=" + CN + ", QN=" + QN + ", deliveryTag="
				+ deliveryTag + ", SUPP=" + SUPP + "]";
	}

	public boolean valid() {
		if (ST == null || ST.length() == 0 || MN == null || MN.length() == 0 || CN == null
				|| !(CN.equals("2011") || CN.equals("2031") || CN.equals("2051") || CN.equals("2061")) || CP == null
				|| !CP.valid()) {
			return false;
		}

		return true;
	}

	/**
	 * 类 名 称：MessageBody 类 描 述：T20x1消息体 创建作者：zhou.yongbo
	 */
	public class MessageBody {
		private List<Parameter> Params;
		private Date DataTime;
		private Date DT;

		public Date getDT() {

			if (DT != null && !StringUtil.isEmptyOrNull(CN)) {
				return RegularTime.regular(CN,MN, DT);
			}

			return DT;
		}

		public void setDT(Date dT) {
			DT = dT;
		}

		public List<Parameter> getParams() {
			return Params;
		}

		public void setParams(List<Parameter> params) {
			Params = params;
		}

		public Date getDataTime() {
			if (DataTime != null && !StringUtil.isEmptyOrNull(CN)) {
				return RegularTime.regular(CN,MN, DataTime);
			}

			return DataTime;
		}

		public void setDataTime(Date dataTime) {
			DataTime = dataTime;
		}

		public boolean valid() {
			if (DataTime == null || Params == null) {
				return false;
			}
			for (Parameter p : Params) {
				if (!p.valid()) {
					return false;
				}
			}
			return true;
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
			
			private String dataFlag;
		    //数据存疑标识
			private Integer doubtful;
			// 自动续约值
			private Double Round;

			private String dataStatus;
			// 数据是否有效
			private Integer isValided = 1;

			public boolean valid() {
				if (ParamID == null || ParamID.length() == 0) {
					return false;
				}
				return true;
			}

			public String getParamID() {
				return ParamID;
			}

			public String getDataFlag() {
				return dataFlag;
			}

			public void setDataFlag(String dataFlag) {
				this.dataFlag = dataFlag;
			}

			public void setParamID(String paramID) {
				ParamID = paramID;
			}

			public String getFlag() {
				return Flag;
			}

			public void setFlag(String flag) {
				Flag = flag;
				isValided = "N".equalsIgnoreCase(flag) ? 1 : 0;
			}

			public Double getRtd() {
				return Rtd;
			}

			public void setRtd(Double rtd) {

				Rtd = rtd;

				if (Round == null) {
					Round = Rtd == null ? Avg : Rtd;
				}
			}

			public Double getAvg() {
				return Avg;
			}

			public void setAvg(Double avg) {
				Avg = avg;

				if (Round == null) {
					Round = Rtd == null ? Avg : Rtd;
				}
			}

			public Double getMin() {
				return Min;
			}

			public void setMin(Double min) {
				Min = min;
			}

			public Double getMax() {
				return Max;
			}

			public void setMax(Double max) {
				Max = max;
			}

			public Double getRound() {
				return Round;
			}

			public void setRound(Double round) {
				Round = round;
			}

			public String getDataStatus() {
				return dataStatus;
			}

			public void setDataStatus(String dataStatus) {
				this.dataStatus = dataStatus;
			}

			public Integer getIsValided() {
				return isValided;
			}

			public void setIsValided(Integer isValided) {
				this.isValided = isValided;
			}

			public Integer getDoubtful() {
				return doubtful;
			}

			public void setDoubtful(Integer doubtful) {
				this.doubtful = doubtful;
			}

		}
	}
}
/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：TccDataVO.java
* 包  名  称：com.zeei.das.cas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年8月17日下午1:38:47
* 
* 修改历史
* 1.0 quanhongsheng 2017年8月17日下午1:38:47 创建文件
*
*/

package com.zeei.das.cas.vo;

import java.util.List;

/**
 * 类 名 称：TccDataVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class TccDataVO {

	private String CN;

	private List<String> body;

	private long deliveryTag;

	public String getCN() {
		return CN;
	}

	public void setCN(String cN) {
		CN = cN;
	}

	public long getDeliveryTag() {
		return deliveryTag;
	}

	public void setDeliveryTag(long deliveryTag) {
		this.deliveryTag = deliveryTag;
	}

	public List<String> getBody() {
		return body;
	}

	public void setBody(List<String> body) {
		this.body = body;
	}

}

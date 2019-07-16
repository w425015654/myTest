/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：StationCfgMsg.java
* 包  名  称：com.zeei.das.cas.msg
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月3日下午3:13:59
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月3日下午3:13:59 创建文件
*
*/

package com.zeei.das.cas.msg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.cas.mq.Publish;
import com.zeei.das.cas.service.ParamService;
import com.zeei.das.cas.vo.ParamVO;

/**
 * 类 名 称：StationCfgMsg 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component("stationCfgMsg")
public class StationCfgMsg implements MsgGeneration {

	@Autowired
	ParamService paramService;

	@Autowired
	Publish publish;

	private static Logger logger = LoggerFactory.getLogger(StationCfgMsg.class);

	@Override
	public void generation(String MN) {

		List<ParamVO> params = paramService.queryParamByMN(MN);

		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("MN", MN);
		msg.put("MsgType", "1");
		msg.put("MsgBody", params);

		Map<String, Object> cmd = new HashMap<String, Object>();
		cmd.put("CN", "cmd");
		cmd.put("MN", MN);
		cmd.put("CP", msg);
		String json = JSON.toJSONString(cmd);

		logger.info("站点获取站点配置:" + json);

		publish.send("CMD", json);
	}

}

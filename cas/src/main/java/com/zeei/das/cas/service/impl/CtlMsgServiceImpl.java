/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：CtlMsgServiceImpl.java
* 包  名  称：com.zeei.das.cas.service.impl
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午5:17:37
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月4日下午5:17:37 创建文件
*
*/

package com.zeei.das.cas.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.cas.dao.CtlMsgDao;
import com.zeei.das.cas.service.CtlMsgService;
import com.zeei.das.cas.vo.CtlRecDetailVO;
import com.zeei.das.cas.vo.Msg2021VO;
import com.zeei.das.cas.vo.Msg2062VO;
import com.zeei.das.cas.vo.Msg2063VO;
import com.zeei.das.cas.vo.Msg20656VO;
import com.zeei.das.cas.vo.Msg2070VO;
import com.zeei.das.cas.vo.Msg2076VO;
import com.zeei.das.common.utils.DateUtil;

/**
 * 类 名 称：CtlMsgServiceImpl 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

@Service("ctlMsgService")
public class CtlMsgServiceImpl implements CtlMsgService {

	@Autowired
	CtlMsgDao ctlMsgDao;

	@Override
	public Integer insert2021Latest(List<Msg2021VO> data) {
		return ctlMsgDao.insert2021Latest(data);
	}

	@Override
	public Integer insert2076(Msg2076VO data) {
		return ctlMsgDao.insert2076(data);
	}

	@Override
	public Integer insertCtrlRecDetail(CtlRecDetailVO data) {
		return ctlMsgDao.insertCtrlRecDetail(data);
	}

	@Override
	public Integer updateCtlRec(CtlRecDetailVO data) {
		return ctlMsgDao.updateCtlRec(data);
	}

	@Override
	public Integer insert2021ByBatch(List<Msg2021VO> t2021) {
		return ctlMsgDao.insert2021ByBatch(t2021);
	}

	@Override
	public Integer insert2076ByBacth(List<Msg2076VO> t2076) {
		return ctlMsgDao.insert2076ByBatch(t2076);
	}

	@Override
	public Integer insertCtrlRecDetailByBacth(List<CtlRecDetailVO> ctlRecDetails) {
		return ctlMsgDao.insertCtrlRecDetailByBatch(ctlRecDetails);
	}

	public Integer updateCtlRecByBacth(List<CtlRecDetailVO> ctlRecs) {

		return ctlMsgDao.updateCtlRecByBatch(ctlRecs);
	}

	@Override
	public Integer updateCompRecByBacth(List<CtlRecDetailVO> ctlRecs) {

		return ctlMsgDao.updateCompRecByBatch(ctlRecs);
	}

	@Override
	public Integer insertCompRecDetailByBacth(List<CtlRecDetailVO> ctlRecDetails) {

		return ctlMsgDao.insertCompRecDetailByBatch(ctlRecDetails);
	}

	@Override
	public Integer insert2062ByBatch(List<Msg2062VO> t2062) {

		return ctlMsgDao.insert2062ByBatch(t2062);

	}
	
	@Override
	public Integer insert20656ByBatch(List<Msg20656VO> t20656) {

		return ctlMsgDao.insert20656ByBatch(t20656);

	}

	@Override
	public Integer insert2063ByBatch(List<Msg2063VO> t2063) {

		if(CollectionUtils.isNotEmpty(t2063)){
			
			for(Msg2063VO t63 : t2063){
				
				String waterTime = t63.getWaterTime();
				if(StringUtils.isNotEmpty(waterTime) && waterTime.length()==14){
					t63.setWaterTime(DateUtil.dateToStr(DateUtil.strToDate(waterTime,
							"yyyyMMddHHmmss"), "yyyy-MM-dd HH:mm:ss"));
				}
			}
		}	
		return ctlMsgDao.insert2063ByBatch(t2063);

	}

	@Override
	public Integer insert2070ByBatch(List<Msg2070VO> t2070) {
		
		return ctlMsgDao.insert2070ByBatch(t2070);
	}

	@Override
	public void delete2063ByBatch(Map<String, Object> map) {
		
		ctlMsgDao.delete2063ByBatch(map);
	}

}

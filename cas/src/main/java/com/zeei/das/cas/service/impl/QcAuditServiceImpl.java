package com.zeei.das.cas.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.cas.dao.QcAuditDao;
import com.zeei.das.cas.service.QcAuditService;
import com.zeei.das.cas.vo.HourAuditVO;
import com.zeei.das.cas.vo.Msg2061VO;
import com.zeei.das.cas.vo.Msg2062VO;
import com.zeei.das.cas.vo.Msg2063VO;
import com.zeei.das.cas.vo.Msg20656VO;

/** 
* @类型名称：QcAuditServiceImpl
* @类型描述：
* @功能描述： 质控  数据处理实现
* @创建作者：zhanghu
*
*/
@Service("qcAuditServiceImpl")
public class QcAuditServiceImpl implements QcAuditService{

	
	@Autowired
	QcAuditDao qcAuditDao;
	
	@Override
	public List<Msg2061VO> queryRivHourData(Map<String,Object> map) {
		
		return qcAuditDao.queryRivHourData(map);
	}

	@Override
	public void insertHourAuditDatas(List<HourAuditVO> datas) {
		
		qcAuditDao.insertHourAuditDatas(datas);
	}

	@Override
	public List<Msg2062VO> query2062Datas(Map<String, Object> map) {
		
		return qcAuditDao.query2062Datas(map);
	}

	@Override
	public List<Msg2063VO> query2063Datas(Map<String, Object> map) {
		
		return qcAuditDao.query2063Datas(map);
	}

	@Override
	public List<Msg20656VO> query20656Datas(Map<String, Object> map) {
		
		return qcAuditDao.query20656Datas(map);
	}

	@Override
	public void updateRivHhFlag(Map<String, Object> map) {
		
		 qcAuditDao.updateRivHhFlag(map);
	}

	@Override
	public List<Msg2062VO> queryCheckData(Map<String, Object> map) {
		
		return qcAuditDao.queryCheckData(map);
	}

	@Override
	public List<Msg20656VO> query20656DataCount(Map<String, Object> map) {
		
		return qcAuditDao.query20656DataCount(map);
	}

}

package com.zeei.das.cas.service;

import java.util.List;
import java.util.Map;

import com.zeei.das.cas.vo.HourAuditVO;
import com.zeei.das.cas.vo.Msg2061VO;
import com.zeei.das.cas.vo.Msg2062VO;
import com.zeei.das.cas.vo.Msg2063VO;
import com.zeei.das.cas.vo.Msg20656VO;

/** 
* @类型名称：QcAuditService
* @类型描述：
* @功能描述： 质控相关 数据处理
* @创建作者：zhanghu
*
*/
public interface QcAuditService {

	/**
	 * queryRivHourData:相关河流小时数据查询
	 * 
	 * @return List<Msg2061VO>
	 */
	List<Msg2061VO> queryRivHourData(Map<String,Object> map);
	
	/**
	 * insertHourAuditDatas:入库相关的月质控数据
	 * 
	 * @param datas void
	 */
	void  insertHourAuditDatas(List<HourAuditVO> datas);
	
	
	/**
	 * query2062Datas:相关2062数据查询
	 * 
	 * @return List<Msg2062VO>
	 */
	List<Msg2062VO> query2062Datas(Map<String,Object> map);
	
	/**
	 * query2063Datas:相关2063数据查询
	 * 
	 * @return List<Msg2063VO>
	 */
	List<Msg2063VO> query2063Datas(Map<String,Object> map);
	
	/**
	 * query20656Datas:相关20656数据查询
	 * 
	 * @return List<Msg20656VO>
	 */
	List<Msg20656VO> query20656Datas(Map<String,Object> map);
	
	
	/**
	 * updateRivHhFlag:更新地表水小时数据表标识
	 * 
	 * @param map void
	 */
	void updateRivHhFlag(Map<String,Object> map);
	
	/**
	 * query2062Datas:标液核查飞行检查标准值查询
	 * 
	 * @return List<Msg2062VO>
	 */
	List<Msg2062VO> queryCheckData(Map<String,Object> map);
	
	
	/**
	 * query20656DataCount:查询零点和质控在时间内的数量
	 * 
	 * @param map
	 * @return Msg20656VO
	 */
	List<Msg20656VO> query20656DataCount(Map<String,Object> map);
}

package com.zeei.das.dps.audit;

import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.QcFlag;
import com.zeei.das.common.constants.RivNineParam;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.service.LostDataServiceImpl;
import com.zeei.das.dps.vo.AuditLogVO;
import com.zeei.das.dps.vo.DoubtfulVo;
import com.zeei.das.dps.vo.PointSiteVO;
import com.zeei.das.dps.vo.PolluteVO;
import com.zeei.das.dps.vo.T20x1Message.MessageBody.Parameter;
import com.zeei.das.dps.vo.T20x1VO;

/** 
* @类型名称：RivDataAuditing
* @类型描述：
* @功能描述：地表水对应数据审核标识修改
* @创建作者：zhanghu
*
*/
@Component
public class RivDataAuditing {
	
	@Autowired
	Publish publish;
	
	@Autowired
	LostDataServiceImpl lostDataService;
	
	private static Logger logger = LoggerFactory.getLogger(DoubtfulAudit.class);

	/**
	 * auditHandler:数据审核
	 * 不符合逻辑关系>离群偏大小数据>零值异常>恒值不变
	 * @param MN
	 * @param dataTime
	 * @param data void
	 */
	public void auditHandler(String MN, Date dataTime, List<Parameter> data) {
		
		// 数据为空不进一步判断
		if (data == null) {
			return;
		}
		PointSiteVO station = DpsService.stationCfgMap.get(MN);
		
		boolean flag = false;
		//氨氮
		Double ad = null; 
		//总氮
		Double zd = null;
		//氨氮总氮的值
		for (Parameter param : data) {
			//氨氮
			if(RivNineParam.NH_N.equals(param.getParamID())){
				ad = param.getAvg();
			}
			//总氮
			if(RivNineParam.TNB.equals(param.getParamID())){
				zd = param.getAvg();
			}
		}
		//判断氨氮总氮数据合理性
		if(ad != null && zd != null){
			flag = ad > zd;
		}
		//结束时间
		String beginTime = DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, -7), "yyyy-MM-dd HH:mm:ss");
		//查询一周内的数据，按因子分组
		Map<String, DoubleSummaryStatistics> dataMaps = new HashMap<>();
		Map<String, Integer> polluteMaps = new HashMap<>();
		if(station != null && StringUtils.isNotEmpty(station.getPointCode())){
			
			List<T20x1VO> datas = lostDataService.querySuppData(station.getPointCode(), null,beginTime, DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss")
					, "T_ENV_MONI_RIV_DATAHH");
			dataMaps = datas.stream().filter(u -> u.getDataValue() != null && u.getIsValided()!=null && u.getIsValided()==1).collect(Collectors.groupingBy(T20x1VO
					:: getPolluteCode,Collectors.summarizingDouble(T20x1VO :: getDataValue)));
			
			List<PolluteVO> pollutes = lostDataService.queryHhCycletime(station.getPointCode());
			polluteMaps = pollutes.stream().collect(Collectors.toMap(PolluteVO::getPolluteCode, PolluteVO::getHhcycletime));
		}
		// 对数据遍历审核
		for (Parameter param : data) {
			String polluteCode = param.getParamID();
			//是否连续不变
			boolean isCf = checkCfFlag(MN, dataTime, param, polluteMaps);
			//不符合逻辑关系
			if(flag && (RivNineParam.NH_N.equals(param.getParamID()) || RivNineParam.TNB.equals(param.getParamID()))){
				
				confirmDataFlag(param, QcFlag.LRF);
				logger.info(String.format("测点MN-%s的%s因子 不符合逻辑关系", MN, polluteCode));
				continue;
			}
			//离群偏大数据
			DoubleSummaryStatistics polluteDatas = dataMaps.get(polluteCode);
			if(polluteDatas != null){
				//离群偏大概念是 当前数据大于一周最大值  并且大于平均值的两倍
				if(param.getAvg()!=null && param.getAvg()>polluteDatas.getMax() && param.getAvg()>2*polluteDatas.getAverage()){

					confirmDataFlag(param, QcFlag.OVL);
					logger.info(String.format("测点MN-%s的%s因子 离群偏大", MN, polluteCode));
					continue;
				}
			}
			//零值异常 ,PH不可以为零
            if(param.getAvg()!=null && param.getAvg() == 0.0 && RivNineParam.PH.equals(polluteCode)){
				
				confirmDataFlag(param, QcFlag.ZF);
				param.setIsValided(0);
				logger.info(String.format("测点MN-%s的%s因子 值为零异常", MN, polluteCode));
				
				// 发送审核日志
				AuditLogVO log = new AuditLogVO();
				String info = String.format("根据国家标准将测点%s(%s为零值异常)数据自动审核为%s", station.getPointName(),
						param.getParamID(), "无效");

				log.setPointCode(station.getPointCode());
				log.setPolluteCode(param.getParamID());
				log.setRemark(info);
				log.setDataTime(dataTime);

				Map<String, Object> log_map = new HashMap<String, Object>();
				log_map.put("logType", LogType.LOG_TYPE_AUDIT);
				log_map.put("logContent", log);
				logger.info(info);

				publish.send(Constant.MQ_QUEUE_LOGS, JSON.toJSONString(log_map));
				
				continue;
			}
            //恒值不变,连续三个周期不变
            if(isCf){

            	confirmDataFlag(param, QcFlag.CF);
    			logger.info(String.format("测点MN-%s的%s因子 连续三个周期不变异常", MN, polluteCode));
            }
            
		}
		
	}
	
	
	/**
	 * checkCfFlag: 恒值不变，
	 * 
	 * @param MN
	 * @param dataTime
	 * @param param void
	 */
	private boolean checkCfFlag(String MN, Date dataTime, Parameter param, Map<String, Integer> polluteMaps){
		
		boolean isCf = false;
		String polluteCode = param.getParamID();
        Double auditData = param.getAvg();
		String douKey = MN + "+" + polluteCode;
		DoubtfulVo doubtfulVo = DpsService.rivCfMap.get(douKey);
		int per = 1;
		// 判断map是否已存在站点对应因子记录，有则进一步判断，没有则新增
		if(doubtfulVo != null && doubtfulVo.getAudValue() != null && doubtfulVo.getAudValue().equals(auditData)){
			
			per = polluteMaps.get(polluteCode)==null?per:polluteMaps.get(polluteCode);
			//与上次时间间隔超过数据周期，即中间缺数，重新开始计算
			if(DateUtil.dateDiffHour(doubtfulVo.getLastDataTime(), dataTime)>per){
				
				doubtfulVo.setAudValue(auditData);
				doubtfulVo.setDataTime(dataTime);
				doubtfulVo.setLastDataTime(dataTime);
				return isCf;
			}
			//设定上次数据时间
			doubtfulVo.setLastDataTime(dataTime);
			// 判断数值是否连续不变且时间超过限定，不是则跳过
			if(DateUtil.dateDiffHour(doubtfulVo.getDataTime(), dataTime)/per>=3){
				
				isCf = true;
			}
			
		 }else{
			doubtfulVo = new DoubtfulVo();
			doubtfulVo.setAudValue(auditData);
			doubtfulVo.setDataTime(dataTime);
			doubtfulVo.setLastDataTime(dataTime);
			DpsService.rivCfMap.put(douKey, doubtfulVo);
	     }
		return  isCf;
	}
	
	/**
	 * confirmDataFlag:确定配置数据标识
	 * 
	 * @param param
	 * @param flag void
	 */
	private void confirmDataFlag(Parameter param, String flag){
		
		//旧的数据标识
		String oldFlag = param.getFlag();
		//标识是否覆盖
		if(StringUtils.isEmpty(oldFlag) || oldFlag.equalsIgnoreCase("N")){
			
			param.setDataFlag(flag);
		}
		
	}
	
}

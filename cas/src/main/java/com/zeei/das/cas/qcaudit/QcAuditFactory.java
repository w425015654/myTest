package com.zeei.das.cas.qcaudit;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.cas.mq.Publish;
import com.zeei.das.cas.service.QcAuditService;
import com.zeei.das.cas.vo.Msg20656VO;
import com.zeei.das.cas.vo.MsgQcAudit;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.utils.DateUtil;

/** 
* @类型名称：QcAuditFactory
* @类型描述：
* @功能描述：质控处理工厂
* @创建作者：zhanghu
*
*/
@Component("qcAuditFactory")
@EnableScheduling
public class QcAuditFactory {
	
	@Autowired
	QC2061Dispose qC2061Dispose;
	
	@Autowired
	QC2062Dispose qC2062Dispose;
	
	@Autowired
	QC2063Dispose qC2063Dispose;
	
	@Autowired
	QC20656Dispose qC20656Dispose;
	
	@Autowired
	QcAuditService qcAuditService;
	
	@Autowired
	Publish publish;
	
	private static Logger logger = LoggerFactory.getLogger(QcAuditFactory.class);
	
	/**
	 * checkData:分别对不同的CN数据分流处理
	 * 
	 * @param msgQcAudit
	 * @return boolean true 为正常处理完成
	 */
	public boolean checkData(MsgQcAudit msgQcAudit){
		
		String CN = msgQcAudit.getCN();
		//处理结果
		boolean result = true;
		
		switch(CN){
		     case "2061" :
		    	 result = qC2061Dispose.Dispose(msgQcAudit);
		    	 break;
		     case "2062" :
		    	 result = qC2062Dispose.Dispose(msgQcAudit);
		    	 break;
		     case "2063" :
		    	 result = qC2063Dispose.Dispose(msgQcAudit);
		         break;
		     case "2065" :
		     case "2066" :
		    	 result = qC20656Dispose.Dispose(msgQcAudit);
		    	 break;
		     default:
		         break;
		}
		
		return result;
	}

	/**
	 * minAQIHandler:对零点核查，跨度核查定时巡检，若前一天没有人工执行则自动自行一次
	 *  每天的01点执行
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void minAQIHandler() {
		try {
			//对该时间周期的数据查询
			String endTime = DateUtil.getCurrentDate("yyyy-MM-dd") + " 00:00:00";
			Date beginDate = DateUtil.dateAddDay(DateUtil.strToDate(endTime, "yyyy-MM-dd HH:mm:ss"), -1);
			String beginTime = DateUtil.dateToStr(beginDate, "yyyy-MM-dd HH:mm:ss"); 
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("endTime", endTime);
			map.put("beginTime", beginTime);
			List<Msg20656VO> msg20656VOs = qcAuditService.query20656DataCount(map);
			//处理判断前一天是否有做相关质控
			if(CollectionUtils.isEmpty(msg20656VOs)){
				//遍历数据结果
				for(Msg20656VO msg20656VO : msg20656VOs){
					//没有相关数据，补发前一天的质控
					if(StringUtils.isNotEmpty(msg20656VO.getAuditTime()) && 
							(msg20656VO.getDataSum()==null || msg20656VO.getDataSum() ==0)){
						// 数据命令下发
						Map<String, Object> qcData = new HashMap<String, Object>();
						qcData.put("pointCode", msg20656VO.getPointCode());
						qcData.put("CN", msg20656VO.getDataType()==1?"2065":"2066");
						qcData.put("DataTime", DateUtil.dateToStr(beginDate, "yyyy-MM-dd")
								+ DateUtil.dateToStr(DateUtil.strToDate(msg20656VO.getAuditTime(), "yyyy-MM-dd HH:mm:ss")," HH:mm:ss"));
						qcData.put("ST", "21");
						qcData.put("DATATYPE", msg20656VO.getDataType());
						String Json = JSON.toJSONString(qcData);
						publish.send(Constant.MQ_QUEUE_QC, Json);
					}
				}
			}
			
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

}

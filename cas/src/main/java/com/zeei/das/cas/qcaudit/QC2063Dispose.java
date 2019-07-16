package com.zeei.das.cas.qcaudit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.cas.service.CtlMsgService;
import com.zeei.das.cas.service.QcAuditService;
import com.zeei.das.cas.vo.Msg2063VO;
import com.zeei.das.cas.vo.MsgQcAudit;
import com.zeei.das.common.constants.RivNineParam;
import com.zeei.das.common.utils.DateUtil;

/** 
* @类型名称：QC2063Dispose
* @类型描述：
* @功能描述：对应的2063消息处理
* @创建作者：zhanghu
*
*/
@Component("qC2063Dispose")
public class QC2063Dispose implements IQcDataDispose{
	
	@Autowired
	QcAuditService qcAuditService;
	
	@Autowired
	CtlMsgService ctlMsgService;

	@Override
	public boolean Dispose(MsgQcAudit msgQcAudit) {
		
		String dataType = msgQcAudit.getDataType();
		//加标回收相关测试因子
		List<String> pollutes = new ArrayList<>(RivNineParam.fourHourPollute);
		//参数集合
		Map<String,Object> map = new HashMap<>();
		String dataTime = msgQcAudit.getDataTime();
        if(CollectionUtils.isNotEmpty(msgQcAudit.getPolluteList())){
			
			pollutes = msgQcAudit.getPolluteList();
		}
		map.put("polluteCodes", pollutes);
		map.put("pointCode", msgQcAudit.getPointCode());
		map.put("beginTime", dataTime);
		//这里时间是查询这一月内的数据，结束时间为下个月一号
		Date endDate = DateUtil.strToDate((dataTime.substring(0, 8) + "01 00:00:00"), "yyyy-MM-dd HH:mm:ss");
		endDate = DateUtil.dateAddMonth(endDate, 1);
		String endTime = DateUtil.dateToStr(endDate, "yyyy-MM-dd HH:mm:ss") ;
		
		map.put("endTime", endTime);
		//查询 时间内的标液核查数据
		List<Msg2063VO> datas = qcAuditService.query2063Datas(map);
		//对符合条件的结果数据进行处理
		if(CollectionUtils.isNotEmpty(datas)){
			
			Map<String,Map<Date, List<Msg2063VO>>> dataMaps = datas.stream().collect(Collectors.groupingBy(Msg2063VO :: getParamID,
					Collectors.groupingBy(Msg2063VO :: getDeDataTime)));
			//结果集
			List<Msg2063VO> results = new ArrayList<>();
			//遍历因子数据
			for(Entry<String, Map<Date, List<Msg2063VO>>> dataMap : dataMaps.entrySet()){
				
				Map<Date, List<Msg2063VO>> dataM = dataMap.getValue();
				//最小时间
			    Date minDate = Collections.min(dataM.keySet());
			    List<Msg2063VO> metadatas = dataM.get(minDate);
			    Msg2063VO metadata = metadatas.get(0);
			    metadata.setQn(msgQcAudit.getQN());
		    	metadata.setDataStatus(1);
		    	metadata.setDataType(Integer.valueOf(dataType));
		    	
		    	results.add(metadata);
		    	pollutes.remove(dataMap.getKey());
			} 
		    
		    ctlMsgService.insert2063ByBatch(results);
		    //当所有因子都已上传，结束质控
		    msgQcAudit.setPolluteList(pollutes);
		    return CollectionUtils.isEmpty(pollutes);
		//当前时间超过了限制的数据时段，且依旧没有数据，则默认消息不再继续处理了
		}else if(DateUtil.getCurrentDate().after(endDate)){
				
			return true;
		}
		
		return false;
	}

}

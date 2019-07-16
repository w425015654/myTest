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
import com.zeei.das.cas.vo.Msg20656VO;
import com.zeei.das.cas.vo.MsgQcAudit;
import com.zeei.das.common.constants.QcFlag;
import com.zeei.das.common.constants.RivNineParam;
import com.zeei.das.common.utils.DateUtil;

/** 
* @类型名称：QC20656Dispose
* @类型描述：
* @功能描述：对应的20656消息处理
* @创建作者：zhanghu
*
*/
@Component("qC20656Dispose")
public class QC20656Dispose implements IQcDataDispose{
	
	@Autowired
	QcAuditService qcAuditService;
	
	@Autowired
	CtlMsgService ctlMsgService;

	@Override
	public boolean Dispose(MsgQcAudit msgQcAudit) {
		
		String dataType = msgQcAudit.getDataType();
		//使用这些因子
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
		map.put("dataType", dataType);
		//这里时间是查询的数据，结束时间为审核后24小时
		Date endDate = DateUtil.dateAddHour(DateUtil.strToDate(dataTime, "yyyy-MM-dd HH:mm:ss"),24);
		String endTime = DateUtil.dateToStr(endDate, "yyyy-MM-dd HH:mm:ss") ;
		
		map.put("endTime", endTime);
		//查询 时间内的标液核查数据
		List<Msg20656VO> datas = qcAuditService.query20656Datas(map);
		//最小时间集合
		 List<Date> minDateList = new ArrayList<>();
		//对符合条件的结果数据进行处理
		if(CollectionUtils.isNotEmpty(datas)){	
			
		    //需要修改的因子集合
		    List<String> pollList = new ArrayList<>();
		    Map<String,Map<Date, List<Msg20656VO>>> dataMaps = datas.stream().collect(Collectors.groupingBy(Msg20656VO :: getParamID,
					Collectors.groupingBy(Msg20656VO :: getDeDataTime)));
			//结果集
			List<Msg20656VO> results = new ArrayList<>();
			//遍历因子数据
			for(Entry<String, Map<Date, List<Msg20656VO>>> dataMap : dataMaps.entrySet()){
				
				Map<Date, List<Msg20656VO>> dataM = dataMap.getValue();
				//最小时间
			    Date minDate = Collections.min(dataM.keySet());
			    minDateList.add(minDate);
			    List<Msg20656VO> metadatas = dataM.get(minDate);
			    Msg20656VO metadata = metadatas.get(0);
			    
			    metadata.setQn(msgQcAudit.getQN());
		    	metadata.setAuditTime(dataTime);
		    	metadata.setDataStatus(1);
		    	metadata.setDataType(Integer.valueOf(dataType));
		    	//默认不超标为false
		    	boolean flag = false;
		    	if(metadata.getCheck() != null && metadata.getStandardValue() != null){
		    		//零点核查1
			    	if("1".equals(dataType)){
			    		double value = Math.abs(metadata.getCheck()-metadata.getStandardValue());
			    		switch(metadata.getParamID()){
			    		     //高门酸盐
			    		     case "w01019":
			    		    	 flag = value>1;
			    		    	 break;
			    		     //氨氮
			    		     case "w21003":
			    		    	 flag = value>0.2;
			    		    	 break;
			    		     //总氮
			    		     case "w21001":
			    		    	 flag = value>0.3;
			    		    	 break;
			    		     //总麟
			    		     case "w21011":
			    		    	 flag = value>0.02;
			    		    	 break;
			    		}
			    	//跨度核查2
			    	}else if("2".equals(dataType)){
			    		double value = (metadata.getCheck()-metadata.getStandardValue())/metadata.getStandardValue();
			    		//因子结果判定
			    		if(Math.abs(value)>0.1){
			    			flag = true;
			    		}
			    	}
		    	}
		    	//修改质控结果
		    	if(flag){
		    		metadata.setFlag(QcFlag.QCF);
		    		pollList.add(metadata.getParamID());
		    	}
		    	results.add(metadata);
		    	pollutes.remove(dataMap.getKey());
			}
		    //需要修改因子集不为空，则需要修改这些因子对应时间内的质控结果
		    if(CollectionUtils.isNotEmpty(pollList) && CollectionUtils.isNotEmpty(minDateList)){
		    	
		    	Date dateValue = Collections.min(minDateList);
		    	map.put("polluteCodes", pollList);
		    	map.put("QcFlag", QcFlag.QCF);
		    	map.put("endTime", DateUtil.dateToStr(dateValue, "yyyy-MM-dd HH:mm:ss"));
		    	map.put("beginTime", DateUtil.dateToStr(DateUtil.dateAddDay(dateValue, -1), "yyyy-MM-dd HH:mm:ss"));
		    	qcAuditService.updateRivHhFlag(map);
		    }
		    //数据入库
		    ctlMsgService.insert20656ByBatch(results);
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

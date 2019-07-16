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
import com.zeei.das.cas.vo.Msg2062VO;
import com.zeei.das.cas.vo.MsgQcAudit;
import com.zeei.das.common.constants.RivNineParam;
import com.zeei.das.common.utils.DateUtil;

/** 
* @类型名称：QC2062Dispose
* @类型描述：
* @功能描述：对应的2062消息处理
* @创建作者：zhanghu
*
*/
@Component("qC2062Dispose")
public class QC2062Dispose implements IQcDataDispose{
	
	@Autowired
	QcAuditService qcAuditService;
	
	@Autowired
	CtlMsgService ctlMsgService;

	@Override
	public boolean Dispose(MsgQcAudit msgQcAudit) {
		
		String dataType = msgQcAudit.getDataType();
		//默认使用常规五参的因子，常规五参数周核查1
		List<String> pollutes = new ArrayList<>(RivNineParam.normalPollute);
		//数据类型，多点线性核查2
		if("2".equals(dataType)){
			 //叶绿素 a、蓝绿藻密度,这两个因子暂时没有
			 pollutes = new ArrayList<>(RivNineParam.fourHourPollute);
		}else if("3".equals(dataType)){
			pollutes = new ArrayList<>(RivNineParam.ninePollute);
		}
		if(CollectionUtils.isNotEmpty(msgQcAudit.getPolluteList())){
			
			pollutes = msgQcAudit.getPolluteList();
		}
		//参数集合
		Map<String,Object> map = new HashMap<>();
		String dataTime = msgQcAudit.getDataTime();
		map.put("polluteCodes", pollutes);
		map.put("pointCode", msgQcAudit.getPointCode());
		map.put("beginTime", dataTime);
		//这里时间是查询这一周的数据，结束时间为周日的23:59:59
		Date endDate = DateUtil.strToDate((dataTime.substring(0, 10) + " 23:59:59"), "yyyy-MM-dd HH:mm:ss");
		endDate = DateUtil.dateAddDay(endDate, 7 - (DateUtil.getWeekOfDate(endDate)==0?7:DateUtil.getWeekOfDate(endDate)));
		if("3".equals(dataType)){
			endDate = DateUtil.dateAddDay(DateUtil.strToDate(dataTime, "yyyy-MM-dd HH:mm:ss"), 1); 
		}
		String endTime = DateUtil.dateToStr(endDate, "yyyy-MM-dd HH:mm:ss") ;
		
		map.put("endTime", endTime);
		//查询 时间内的标液核查数据
		List<Msg2062VO> datas = qcAuditService.query2062Datas(map);
		//对符合条件的结果数据进行处理
		if(CollectionUtils.isNotEmpty(datas)){
			//查询结果，飞行审核，需要将值存入到审核标准中去
			Map<String, List<Msg2062VO>> msg2062Maps = new HashMap<>();
			if("3".equals(dataType)){
				map.put("dataTime", dataTime);
				List<Msg2062VO> msg2062s = qcAuditService.queryCheckData(map);
				msg2062Maps = msg2062s.stream().collect(Collectors.groupingBy(Msg2062VO :: getParamID));
			}
			Map<String,Map<Date, List<Msg2062VO>>> dataMaps = datas.stream().collect(Collectors.groupingBy(Msg2062VO :: getParamID,
					Collectors.groupingBy(Msg2062VO :: getDeDataTime)));
			//结果集
			List<Msg2062VO> results = new ArrayList<>();
			//遍历因子数据
			for(Entry<String, Map<Date, List<Msg2062VO>>> dataMap : dataMaps.entrySet()){
				
				Map<Date, List<Msg2062VO>> dataM = dataMap.getValue();
				//最小时间
			    Date minDate = Collections.min(dataM.keySet());
			    List<Msg2062VO> metadatas = dataM.get(minDate);
			    Msg2062VO metadata = metadatas.get(0);
			    metadata.setQn(msgQcAudit.getQN());
		    	metadata.setDataStatus(1);
		    	metadata.setDataType(Integer.valueOf(dataType));
		    	//更新标准值
		    	if("3".equals(dataType)){
		    		
		    		List<Msg2062VO> msg2062List = msg2062Maps.get(metadata.getParamID());
		    		if(CollectionUtils.isNotEmpty(msg2062List)){
		    			
		    			metadata.setStandardValue(msg2062List.get(0).getStandardValue());
		    		}
				}
		    	results.add(metadata);
		    	pollutes.remove(dataMap.getKey());
			}
		    //数据入库
		    ctlMsgService.insert2062ByBatch(results);
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

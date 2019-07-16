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

import com.zeei.das.cas.service.QcAuditService;
import com.zeei.das.cas.vo.HourAuditVO;
import com.zeei.das.cas.vo.Msg2061VO;
import com.zeei.das.cas.vo.MsgQcAudit;
import com.zeei.das.common.constants.RivNineParam;
import com.zeei.das.common.utils.DateUtil;

/** 
* @类型名称：QC2061Dispose
* @类型描述：
* @功能描述：对应的2061消息处理
* @创建作者：zhanghu
*
*/
@Component("qC2061Dispose")
public class QC2061Dispose implements IQcDataDispose{
	
	@Autowired
	QcAuditService qcAuditService;

	@Override
	public boolean Dispose(MsgQcAudit msgQcAudit) {
		
		String dataType = msgQcAudit.getDataType();
		//默认使用集成干预的因子
		List<String> pollutes = new ArrayList<>(RivNineParam.fourHourPollute);
		//数据类型，实际水样比对1，集成干预核查2，非集成干预核查3
		if("1".equals(dataType)){
			
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
		//这里时间还没确定，暂时设置为24小时
		Date endDate = DateUtil.dateAddHour(DateUtil.strToDate(dataTime, "yyyy-MM-dd HH:mm:ss"),24);
		String endTime = DateUtil.dateToStr(endDate, "yyyy-MM-dd HH:mm:ss") ;
		map.put("endTime", endTime);
		//查询 符合条件的小时数据，
		List<Msg2061VO> datas = qcAuditService.queryRivHourData(map);
		//对符合条件的结果数据进行处理
		if(CollectionUtils.isNotEmpty(datas)){
			//数据结果
			List<HourAuditVO> results = new ArrayList<>();
			//按因子和时间分组
			Map<String,Map<Date, List<Msg2061VO>>> dataMaps = datas.stream().collect(Collectors.groupingBy(Msg2061VO :: getPolluteCode,
					Collectors.groupingBy(Msg2061VO :: getDeDataTime)));
			//遍历因子数据
			for(Entry<String, Map<Date, List<Msg2061VO>>> dataMap : dataMaps.entrySet()){
				
				Map<Date, List<Msg2061VO>> dataM = dataMap.getValue();
				//最小时间
			    Date minDate = Collections.min(dataM.keySet());
			    List<Msg2061VO> metadatas = dataM.get(minDate);
			    Msg2061VO metadata = metadatas.get(0);
			    HourAuditVO hourAudit = new HourAuditVO();
		    	hourAudit.setAuditTime(dataTime);
		    	hourAudit.setDataFlag(metadata.getDataFlag());
		    	hourAudit.setDataStatus(1);
		    	hourAudit.setDataTime(metadata.getDataTime());
		    	hourAudit.setDataType(Integer.valueOf(dataType));
		    	hourAudit.setDataValue(metadata.getDataValue());
		    	hourAudit.setOriTime(metadata.getDataTime());
		    	hourAudit.setPointCode(metadata.getPointCode());
		    	hourAudit.setPolluteCode(metadata.getPolluteCode());
		    	hourAudit.setQn(msgQcAudit.getQN());
		    	
		    	results.add(hourAudit);
		    	pollutes.remove(dataMap.getKey());
			}
		    //数据入库
		    qcAuditService.insertHourAuditDatas(results);
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

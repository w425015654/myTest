package com.zeei.das.cgs.T212.ParseCP;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.cgs.T212.Station;
import com.zeei.das.cgs.vo.StationCfgVO;
import com.zeei.das.common.utils.DateUtil;

import io.netty.channel.Channel;

/**
 * 类 名 称：ParseCP 类 描 述：2070消息体解析实现 功能描述：解析2070格式消息 创建作者：zhanghu
 */

@com.zeei.das.common.annotation.ParseCPAnnotation(CN="2070")
@Component("parse2070")
public class Parse2070 implements ParseCP {
	
	@Autowired
	Station station;

	@Override
	public JSONObject parseT212Body(String cpStr) {

		String DataTime = "";
		String regDataTime = "DataTime=(\\d{14})";
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regDataTime);
		Matcher matcher = pattern.matcher(cpStr);

		// DataTime格式(K-V)与数据格式(P-T-V)不同，需要单独解析
		if (matcher.find()) {
			DataTime = matcher.group(1);
		} else {
			return null;
		}
		JSONObject cpMap = new JSONObject();
		
		cpMap.put("DataTime", DateUtil.strToDate(DataTime, "yyyyMMddHHmmss"));
		
		String []  cpS = cpStr.split(";");
		
		JSONArray list = new JSONArray();
		
		JSONObject resMap = new JSONObject();
		
		for(String cp : cpS){
			//时间已统计
			if(StringUtils.isEmpty(cp) || cp.contains("DataTime")){
				continue;
			}
			resMap = new JSONObject();
			String [] res = cp.split(",");
			//转map
			for(String re : res){
				String [] mps = re.split("=");
				if(mps.length>1){
					resMap.put(mps[0], mps[1]);
				}
			}
			//加入结果集合
			if(resMap.size()>0){
				list.add(resMap);
			}
		}
		cpMap.put("Params", list);
		
		return cpMap;
	}

	
	@Override
	public void ack(JSONObject msgHead,Channel channel) {
		
		String MN = (String) msgHead.get("MN");
		String Flag = (String) msgHead.get("Flag");
		StationCfgVO cfg = station.getStationCfg(MN);

		if (cfg != null && cfg.isMsgAck()) {
			String PW = (String) msgHead.get("PW");
			String QN = (String) msgHead.get("QN");
			String ST = (String) msgHead.get("ST");

			String msg = String.format("QN=%s;ST=%s;PW=%s;MN=%s;Flag=%s;CN=9014;CP=&&Flag=%s&&", QN, ST, PW, MN ,Flag,Flag);

			station.send(channel, msg);
		}
		
	}

}

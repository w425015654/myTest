package com.zeei.das.aas.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.utils.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zeei.das.aas.AasService;
import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.aas.vo.MonitorDataVO;
import com.zeei.das.aas.vo.StationVO;
import com.zeei.das.aas.vo.SystemTableVO;

public class PartitionTableUtil {

	private static Logger logger = LoggerFactory.getLogger(PartitionTableUtil.class);

	public static String getTableName(String ST, String CN, String pointCode, Date dataTime) {
		String tableName = "";
		try {
			String prefix = "";
			String suffi = "";
			SystemTableVO table = AasService.tableNameMap.get(ST);

			switch (CN) {
			case DataType.RTDATA:
				if (table != null) {
					prefix = table.getTableNameRT();
				}
				suffi = String.format("%s%s", DateUtil.dateToStr(dataTime, "yyyy"), pointCode);
				break;
			case DataType.MINUTEDATA:
				if (table != null) {
					prefix = table.getTableNameMin();
				}
				suffi = DateUtil.dateToStr(dataTime, "yyyyMM");
				break;
			case DataType.HOURDATA:
				if (table != null) {
					prefix = table.getTableNameHH();
				}
				break;
			case DataType.DAYDATA:
				if (table != null) {
					prefix = table.getTableNameDD();
				}
				break;
			case DataType.MONTHDATA:
			case DataType.YEARDATA:
				if (table != null) {
					prefix = table.getTableNameMY();
				}
				break;
			}
			tableName = String.format("%s%s", prefix, suffi);
		} catch (Exception e) {
			logger.error("创建表名异常:" + e.toString());
		}
		return tableName.toUpperCase();
	}
}

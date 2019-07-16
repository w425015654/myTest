package com.zeei.das.dss.statistics;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.service.StationService;
import com.zeei.das.dss.vo.SystemTableVO;

@Component
public class PartitionTableUtil {

	@Autowired
	StationService stationService;

	// 静态初使化 一个工具类 这样是为了在spring初使化之前
	private static PartitionTableUtil partitionTableUtil;

	// 通过@PostConstruct 和 @PreDestroy 方法 实现初始化和销毁bean之前进行的操作
	@PostConstruct
	public void init() {
		partitionTableUtil = this;
	}

	private static Logger logger = LoggerFactory.getLogger(PartitionTableUtil.class);

	public static String getTableName(String ST, String CN, String pointCode, Date dataTime) {
		String tableName = "";
		try {
			String prefix = "";
			String suffi = "";
			SystemTableVO table = DssService.tableNameMap.get(ST);

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
				return prefix;
			}
			tableName = String.format("%s%s", prefix, suffi);

			if (!StringUtil.isEmptyOrNull(prefix)) {
				createTableIfnotExist(tableName);
			} else {
				return null;
			}

		} catch (Exception e) {
			logger.error("创建表名异常:" + e.toString());
		}
		return tableName.toUpperCase();
	}

	/**
	 * 
	 * createTableIfnotExist:表不存在则新建
	 *
	 * @param tableName
	 *            void
	 */
	public static synchronized void createTableIfnotExist(String table) {

		String tableName = table.toUpperCase();
		List<String> tables = DssService.systemTable;
		boolean e = tables.stream().anyMatch(o -> o.toUpperCase().equals(tableName));
		if (!e) {
			String pkname = "PK_" + tableName.substring(2);
			partitionTableUtil.stationService.createDataTable(tableName, pkname);
			String indexSql = String.format("CREATE INDEX PD_%s ON %s(POINTCODE,DATATIME)", tableName.substring(2),
					tableName);
			partitionTableUtil.stationService.createIndex(indexSql);

			String timeIndexSql = String.format("CREATE INDEX D_%s ON %s(DATATIME)", tableName.substring(2), tableName);
			partitionTableUtil.stationService.createIndex(timeIndexSql);

			DssService.systemTable.add(tableName);

			logger.info("创建数据表：" + tableName);
		}

	}

}

package com.zeei.das.dps.storage;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.dao.PointSiteDAO;
import com.zeei.das.dps.vo.PointSiteVO;
import com.zeei.das.dps.vo.PointSystemVO;
import com.zeei.das.dps.vo.T20x1Message;

@Component
public class PartitionTableUtil {

	@Autowired
	PointSiteDAO pointSiteDAO;

	private Logger logger = LoggerFactory.getLogger(PartitionTableUtil.class);

	public String getTableName(T20x1Message msg) {

		String ST = msg.getST();
		String CN = msg.getCN();
		String pointCode = msg.getID();
		Date dataTime = msg.getCP().getDataTime();

		String prefix = "";
		String suffi = "";
		PointSystemVO table = DpsService.tableNameMap.get(ST);

		switch (CN) {
		case DataType.RTDATA:
			if (table != null) {
				prefix = table.getRealtimeTable();
			}
			suffi = String.format("%s%s", DateUtil.dateToStr(dataTime, "yyyy"), pointCode);
			break;
		case DataType.MINUTEDATA:
			if (table != null) {
				prefix = table.getMinuteTable();
			}
			suffi = DateUtil.dateToStr(dataTime, "yyyyMM");
			break;
		case DataType.HOURDATA:
			if (table != null) {
				prefix = table.getHourTable();
			}
			break;
		case DataType.DAYDATA:
			if (table != null) {
				prefix = table.getDayTable();
			}
			break;
		}
		String tableName = String.format("%s%s", prefix, suffi);

		List<String> tables = DpsService.systemTable;

		boolean e = tables.stream().anyMatch(o -> o.toUpperCase().equals(tableName.toUpperCase()));
		if (!e) {
			createTableIfnotExist(tableName);
		}

		return tableName.toUpperCase();
	}

	/**
	 * 
	 * createTableIfnotExist:TODO 请修改方法功能描述
	 *
	 * @param tableName
	 *            void
	 */
	public synchronized void createTableIfnotExist(String table) {

		String tableName = table.toUpperCase();
		List<String> tables = DpsService.systemTable;

		boolean e = tables.stream().anyMatch(o -> o.toUpperCase().equals(tableName));
		if (!e) {

			String pkname = "PK_" + tableName.substring(2);

			pointSiteDAO.createDataTable(tableName, pkname);

			String indexSql = String.format("CREATE INDEX PD_%s ON %s(POINTCODE,DATATIME)", tableName.substring(2),
					tableName);
			pointSiteDAO.createIndex(indexSql);

			String timeIndexSql = String.format("CREATE INDEX D_%s ON %s(DATATIME)", tableName.substring(2), tableName);
			pointSiteDAO.createIndex(timeIndexSql);

			DpsService.systemTable.add(tableName);

			logger.info("创建数据表：" + tableName);
		}

	}

	/**
	 * 
	 * insertPoint:新建站点
	 *
	 * @param ST
	 * @param MN
	 * @return PointSiteVO
	 */
	public synchronized PointSiteVO newStation(String ST, String MN) {

		PointSiteVO site = DpsService.stationCfgMap.get(MN);

		if (site == null) {
			try {

				site = pointSiteDAO.getStation(MN);

				if (site != null) {

					DpsService.stationCfgMap.put(MN, site);
				}
				// 没有该测点，新建测点
				else {
					// 新建测点的pointcode

					PointSystemVO poinsys = DpsService.tableNameMap.get(ST);

					if (poinsys != null && !StringUtil.isEmptyOrNull(poinsys.getPointName())) {

						pointSiteDAO.insertSite(poinsys.getPointName(), MN, ST);

						site = pointSiteDAO.getStation(MN);

						if (site == null) {
							site = new PointSiteVO();
						}

						DpsService.stationCfgMap.put(MN, site);
						logger.info("新建测点：" + MN);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return site;
	}

}

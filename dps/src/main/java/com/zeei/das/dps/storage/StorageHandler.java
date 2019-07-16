/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：StorageBath.java
* 包  名  称：com.zeei.das.dps.storage
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年8月10日上午9:02:11
* 
* 修改历史
* 1.0 quanhongsheng 2017年8月10日上午9:02:11 创建文件
*
*/

package com.zeei.das.dps.storage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.complement.LostAnalysis;
import com.zeei.das.dps.cycle.DataCycleUtil;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.vo.PointSiteVO;
import com.zeei.das.dps.vo.T20x1Message;
import com.zeei.das.dps.vo.T20x1VO;

/**
 * 类 名 称：StorageBath 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component
public class StorageHandler {

	private static Logger logger = LoggerFactory.getLogger(StorageHandler.class);

	@Autowired
	private PartitionTableUtil partitionTableUtil;
	@Autowired
	private T20x1Storage t20x1Store;
	@Autowired
	private DataCycleUtil dataCycleUtil;
	@Autowired
	LostAnalysis lostAnalysis;
	@Autowired
	private Publish publish;

	int batch = 600;

	/**
	 * 
	 * packedMsg:消息提取
	 *
	 * @return Map<String,List<T20x1Message>>
	 */
	public String getTableName(T20x1Message msg) {

		String tableName = "";

		try {

			String pointCode = msg.getID();
			String MN = msg.getMN();
			String ST = msg.getST();

			Date dataTime = msg.getCP().getDataTime();
			msg.setDataTime(dataTime);

			Date samplingTime = msg.getCP().getDT();
			msg.setSamplingTime(samplingTime);

			PointSiteVO site = DpsService.stationCfgMap.get(MN);

			if (site == null) {
				site = partitionTableUtil.newStation(ST, MN);
			}

			if (site != null) {
				pointCode = String.valueOf(site.getPointCode());
			}

			if (!StringUtil.isEmptyOrNull(pointCode)) {
				msg.setID(pointCode);
			}

			tableName = partitionTableUtil.getTableName(msg);

		} catch (Exception e) {
			logger.error("解析错误:", JSON.toJSONString(msg));
			logger.error("", e);
		}

		return tableName;
	}

	/**
	 * 
	 * translateMessage:TODO 请修改方法功能描述
	 *
	 * @param msg
	 * @return List<T20x1VO>
	 */
	public List<T20x1VO> translateMessage(T20x1Message msg) {
		List<T20x1VO> list = new ArrayList<T20x1VO>();

		String pointCode = msg.getID();
		String dataTime = DateUtil.dateToStr(msg.getCP().getDataTime(), "yyyy-MM-dd HH:mm:ss");
		String updateTime = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");

		String samplingTime = null;

		if (msg.getCP().getDT() != null) {
			samplingTime = DateUtil.dateToStr(msg.getCP().getDT(), "yyyy-MM-dd HH:mm:ss");
		}

		for (T20x1Message.MessageBody.Parameter para : msg.getCP().getParams()) {
			T20x1VO vo = new T20x1VO(para);
			vo.setDataTime(dataTime);
			vo.setUpdateTime(updateTime);
			vo.setSamplingTime(samplingTime);
			vo.setPointCode(Integer.valueOf(pointCode));
			vo.setDataType(msg.getCN());
			list.add(vo);
		}

		return list;
	}

	/**
	 * 
	 * insertLatestData:写入最新数据
	 *
	 * @param datas
	 *            void
	 */
	public void insertLatestData(List<T20x1Message> datas) {

		try {
			if (datas != null && datas.size() > 0) {

				List<T20x1VO> pl = new ArrayList<T20x1VO>();

				for (T20x1Message msg : datas) {
					pl.addAll(translateMessage(msg));
				}

				Map<Integer, Map<String, Optional<T20x1VO>>> pmap = pl.stream().filter(o -> o.getDataValue() != null)
						.collect(Collectors.groupingBy(T20x1VO::getPointCode, Collectors
								.groupingBy(T20x1VO::getPolluteCode, Collectors.maxBy(new Comparator<T20x1VO>() {
									@Override
									public int compare(T20x1VO o1, T20x1VO o2) {

										Date d1 = DateUtil.strToDate(o1.getDataTime(), "yyyy-MM-dd HH:mm:ss");
										Date d2 = DateUtil.strToDate(o2.getDataTime(), "yyyy-MM-dd HH:mm:ss");

										return d1.compareTo(d2);
									}
								}))));

				List<T20x1VO> list = new ArrayList<T20x1VO>();

				if (!pmap.isEmpty()) {
					for (Map<String, Optional<T20x1VO>> m : pmap.values()) {
						if (!m.isEmpty()) {
							for (Optional<T20x1VO> o : m.values()) {
								if (o.isPresent()) {
									T20x1VO v = o.get();
									if (v != null) {
										list.add(v);
									}
								}
							}
						}
					}
				}

				if (list != null && list.size() > 0) {

					list = list.stream().distinct().filter(o -> o.getDataValue() != null).collect(Collectors.toList());

					int size = list.size();
					int group = (int) Math.ceil((double) size / batch);

					for (int i = 0; i < group; i++) {

						int fromIndex = i * batch;

						int toIndex = (i + 1) * batch;

						if (toIndex > size) {
							toIndex = size;
						}
						t20x1Store.insertLatestByBatch(list.subList(fromIndex, toIndex));
					}
					logger.info(String.format("写入最新数据:%s", JSON.toJSON(list)));
					logger.info(String.format("写入最新数据[%s]", list.size()));
				}

			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 
	 * insertBatch:TODO 请修改方法功能描述
	 *
	 * @param table
	 * @param site
	 * @param datas
	 *            void
	 */
	public void insertBatch(String table, List<T20x1Message> datas) {

		boolean isSucess = true;

		try {
			if (datas != null && datas.size() > 0) {

				// 是否覆盖的标识
				String isOverride = null;
				List<T20x1VO> list = new ArrayList<T20x1VO>();

				for (T20x1Message msg : datas) {
					list.addAll(translateMessage(msg));
					if ("F".equals(msg.getSUPP())) {
						isOverride = "true";
					}
				}

				list = list.stream().distinct().collect(Collectors.toList());

				int size = list.size();
				int group = (int) Math.ceil((double) size / batch);

				for (int i = 0; i < group; i++) {

					int fromIndex = i * batch;

					int toIndex = (i + 1) * batch;

					if (toIndex > size) {
						toIndex = size;
					}
					t20x1Store.insertT20x1ByBatch(table, list.subList(fromIndex, toIndex), isOverride);
				}
				isSucess = true;

			}
		} catch (Exception e) {

			isSucess = false;
			logger.error("", e);

		} finally {

			if (datas != null && datas.size() > 0) {
				msgHandler(datas, isSucess);
			}
		}
	}

	/**
	 * 
	 * msgHandler:TODO 请修改方法功能描述
	 *
	 * @param list
	 * @param isSucess
	 *            void
	 */
	public void msgHandler(List<T20x1Message> list, boolean isSucess) {

		if (list != null && list.size() > 0) {
			list.forEach(msg -> {

				try {
					// 此消息数据已入库
					if (isSucess) {
						// 数据周期分析
						dataCycleUtil.cycleHandler(msg);
						// 缺失数据分析
						lostAnalysis.lostAnalysisHandler(msg);
					}
					// 此消息入库失败
					else {

						String json = JSON.toJSONString(msg);
						publish.publishFail(json, msg.getCN());
						publish.publishLog(json);
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			});
		}
	}

}

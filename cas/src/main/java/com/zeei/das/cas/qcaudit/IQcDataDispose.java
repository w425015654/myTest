package com.zeei.das.cas.qcaudit;

import com.zeei.das.cas.vo.MsgQcAudit;

/** 
* @类型名称：IQcDataDispose
* @类型描述：
* @功能描述：质控数据处理
* @创建作者：zhanghu
*
*/
public interface IQcDataDispose {

	  /**
	 * Dispose:具体的处理类，返回对应处理结果
	 * 
	 * @return boolean
	 */
	boolean  Dispose(MsgQcAudit msgQcAudit);
}

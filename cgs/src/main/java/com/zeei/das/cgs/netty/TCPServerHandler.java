/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：TCPServerHandler.java
* 包  名  称：com.zeei.das.cgs.common.netty
* 文件描述：netty 服务处理类
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zeei.das.cgs.CgsService;
import com.zeei.das.cgs.T212.T212;
import com.zeei.das.cgs.mq.Publish;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.BeanUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.ReportUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 类 名 称：TCPServerHandler 类 描 述：netty 服务器处理类 功能描述：继承netty基类，重写部分方法
 * 创建作者：quanhongsheng
 */

public class TCPServerHandler extends ChannelInboundHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(TCPServerHandler.class);

	private ReportUtil reportUtil = (ReportUtil) BeanUtil.getBean("reportUtil");

	private Publish publish = (Publish) BeanUtil.getBean("publish");;

	/**
	 * 
	 * channelActive:连接建立回调
	 * 
	 * @param ctx
	 *            连接通道处理上下文
	 * @throws Exception
	 *             void
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);

		String error = String.format("站点：%s  接入连接", getRemoteAddress(ctx));
		logger.info(error);
		publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, error));
	}

	/**
	 * 
	 * channelInactive:重建连接回调
	 * 
	 * @param ctx
	 *            连接通道处理上下文
	 * @throws Exception
	 *             void
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// 删除Channel Map中的失效Client

		String error = String.format("站点：%s  连接断开", getRemoteAddress(ctx));
		logger.info(error);
		publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, error));

		CgsService.channelMap.remove(ctx.channel().id().asShortText());
		ctx.close();
	}

	/**
	 * 
	 * channelRead:读取通道数据
	 * 
	 * @param ctx
	 *            连接通道处理上下文
	 * @param msg
	 *            接收消息
	 * @throws Exception
	 *             void
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		try {
			String body = new String((byte[]) msg, "GB2312");
			
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_DATA, body));

			// 获取消息处理类实例
			T212 t212 = (T212) BeanUtil.getBean("t212");

			// 通道Id
			String channelId = ctx.channel().id().asShortText();

			if (CgsService.channelMap.containsKey(channelId)) {
				t212.onT212Msg(body, ctx.channel());
			} else {
				t212.onNewConnection(body, ctx.channel());
			}

			// 发送服务状态报告
			reportUtil.report();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * channelReadComplete:数据读取完成
	 * 
	 * @param ctx
	 *            连接通道处理上下文
	 * @throws Exception
	 *             void
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	/**
	 * 
	 * exceptionCaught:异常处理回调
	 * 
	 * @param ctx
	 *            连接通道处理上下文
	 * @param cause
	 *            异常信息
	 * @throws Exception
	 *             void
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		publish.send(Constant.MQ_QUEUE_LOGS, cause.getMessage());

		String error = String.format("站点：%s 连接异常 【%s】", getRemoteAddress(ctx), cause.getMessage());
		logger.error(error);
		publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, error));

		ctx.close();
	}

	/**
	 * 
	 * getRemoteAddress:获取通道地址
	 *
	 * @param ctx
	 *            连接通道处理上下文
	 * @return String
	 */
	public static String getRemoteAddress(ChannelHandlerContext ctx) {
		String socketString = "";
		socketString = ctx.channel().remoteAddress().toString();
		return socketString;
	}

}

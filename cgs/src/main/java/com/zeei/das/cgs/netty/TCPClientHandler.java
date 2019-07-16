/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：TCPClientHandler.java
* 包  名  称：com.zeei.das.cgs.common.netty
* 文件描述：netty 客服端处理类
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.netty;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 类 名 称：TCPClientHandler 类 描 述：netty 客服端处理类 功能描述：继承netty基类，重写部分方法
 * 创建作者：quanhongsheng
 */

public class TCPClientHandler extends ChannelInboundHandlerAdapter {

	private SingleConnection imConnection;

	private static Logger logger = LoggerFactory.getLogger(TCPClientHandler.class);

	public TCPClientHandler(SingleConnection client) {
		this.imConnection = client;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		String body;
		try {

			body = new String((byte[]) msg, "UTF-8");
			String info = String.format("接收数据：client[%s]:%s", imConnection.getMN(), body);

			logger.info(info);
			System.out.println(info);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {

		String info = String.format("client[%s]:连接服务器成功", imConnection.getMN());
		logger.info(info);
		System.out.println(info);
		// 连接成功发送积压数据
		imConnection.sendMsg(null);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		
		String info = String.format("异常离线 client[%s]:%", imConnection.getMN(),cause);
		logger.info(info);
		System.out.println(info);	
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String info = String.format("client[%s]:掉线了...", imConnection.getMN());

		logger.info(info);
		System.out.println(info);

		// 使用过程中断线重连
		final EventLoop eventLoop = ctx.channel().eventLoop();
		eventLoop.schedule(new Runnable() {
			@Override
			public void run() {
				imConnection.connect();
			}
		}, 1L, TimeUnit.SECONDS);
		super.channelInactive(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state().equals(IdleState.READER_IDLE)) {
				System.out.println("长期没收到服务器推送数据");
			} else if (event.state().equals(IdleState.WRITER_IDLE)) {
				System.out.println("长期未向服务器发送数据");
				// 发送心跳包
				// ctx.writeAndFlush(MessageProto.Message.newBuilder().setType(1));
			} else if (event.state().equals(IdleState.ALL_IDLE)) {
				// System.out.println("ALL");
			}
		}
	}
}
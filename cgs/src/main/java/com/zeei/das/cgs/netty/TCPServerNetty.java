/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：TCPServerNetty.java
* 包  名  称：com.zeei.das.cgs.common.netty
* 文件描述：netty tcp连接服务
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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.StringUtil;

/**
 * 类 名 称：TCPServerNetty 类 描 述：netty tcp连接服务 功能描述：启动tcp服务，监听端口 创建作者：quanhongsheng
 */

public class TCPServerNetty {
	private static String PORT = "9001";
	private static int maxFrameLength = 1024 * 10;

	static {
		if (!StringUtil.isNullOrEmpty(CgsService.cfgMap.get("PORT"))) {
			PORT = CgsService.cfgMap.get("PORT");
		}
	};

	private static Logger logger = LoggerFactory.getLogger(TCPServerNetty.class);

	public void start() throws Exception {
		
		logger.info("开始启动tcp服务,端口:" + PORT);
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();

			bootstrap.group(bossGroup, workerGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.option(ChannelOption.SO_BACKLOG, 128);

			bootstrap.handler(new LoggingHandler(LogLevel.INFO));

			// 通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			// 保持长连接状态
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();

					// 消息分割策略，使用特殊字符串分割
					ByteBuf delimiter = Unpooled.copiedBuffer(System.getProperty("line.separator").getBytes());
					p.addLast(new DelimiterBasedFrameDecoder(maxFrameLength, delimiter));
					// p.addLast(new
					// LengthFieldBasedFrameDecoder(maxFrameLength,2,4));
					p.addLast("bytesDecoder", new ByteArrayDecoder());
					p.addLast("bytesEncoder", new ByteArrayEncoder());
					p.addLast(new TCPServerHandler());
				}
			});

			// Start the server.
			ChannelFuture f = bootstrap.bind(Integer.valueOf(PORT)).sync();
			if (f.isSuccess()) {
				/*
				 * logger.info("tcp服务启动完成");				
				 */
			}

			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();

		} catch (Exception e) {
			String error = String.format("tcp服务启动失败:%s", e.getMessage());
			logger.error(error);

		} finally {
			// Shut down all event loops to terminate all threads.
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}

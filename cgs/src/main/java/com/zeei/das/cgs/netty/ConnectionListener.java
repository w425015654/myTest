package com.zeei.das.cgs.netty;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

/**
 * 负责监听启动时连接失败，重新连接功能
 * 
 * @author
 *
 */
public class ConnectionListener implements ChannelFutureListener {

	private SingleConnection imConnection;

	private static Logger logger = LoggerFactory.getLogger(ConnectionListener.class);

	public ConnectionListener(SingleConnection client) {
		this.imConnection = client;
	}

	@Override
	public void operationComplete(ChannelFuture channelFuture) throws Exception {
		if (!channelFuture.isSuccess()) {
			final EventLoop loop = channelFuture.channel().eventLoop();
			loop.schedule(new Runnable() {
				@Override
				public void run() {
					String info = String.format(" client[%s]:端链接不上，开始重连操作...", imConnection.getMN());
					logger.info(info);
					System.out.println(info);
					imConnection.connect();
				}
			}, 1L, TimeUnit.SECONDS);
		}
	}
}

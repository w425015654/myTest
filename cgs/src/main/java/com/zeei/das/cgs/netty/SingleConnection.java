package com.zeei.das.cgs.netty;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zeei.das.common.utils.StringUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class SingleConnection {

	private static Logger logger = LoggerFactory.getLogger(SingleConnection.class);

	private Channel channel;

	private String host = "127.0.0.1";

	private int port = 9016;

	private String MN = "";

	// 待发消息队列
	ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

	SingleConnection(String MN, String host, int port) {
		this.host = host;
		this.port = port;
		this.MN = MN;
		connect();
	}

	/**
	 * 建立连接
	 * 
	 * @return
	 */
	public Channel connect() {
		doConnect();
		return this.channel;
	}

	/**
	 * 连接连接
	 */
	private void doConnect() {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();

			final TCPClientHandler handler = new TCPClientHandler(this);

			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.option(ChannelOption.TCP_NODELAY, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast("bytesDecoder", new ByteArrayDecoder());
					pipeline.addLast("bytesEncoder", new ByteArrayEncoder());
					pipeline.addLast(handler);
				}
			});

			ChannelFuture f = b.connect(host, port);
			f.addListener(new ConnectionListener(this));
			channel = f.channel();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param msg
	 */
	public void sendMsg(String msg) {

		if (channel != null && channel.isActive()) {

			while (!queue.isEmpty()) {
				try {
					String old = queue.poll();
					if (!StringUtil.isEmptyOrNull(old)) {
						channel.writeAndFlush(old.getBytes("UTF-8"));
						String info = String.format("发送数据 client[%s]:%s", MN, old);

						logger.info(info);
						System.out.println(info);
					}

				} catch (Exception e) {
					logger.error("", e);
				}
			}
			if (!StringUtil.isEmptyOrNull(msg)) {
				try {
					channel.writeAndFlush(msg.getBytes("UTF-8"));
					String info = String.format("发送数据 client[%s]:%s", MN, msg);

					logger.info(info);
					System.out.println(info);

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		} else {
			queue.offer(msg);
		}

	}

	public String getMN() {
		return MN;
	}
}
package com.zeei.das.cgs.netty;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.zeei.das.cgs.T212.Package20X1;

@Component
public class TCPClientNetty {

	private static String[][] HOSTS = { { "127.0.0.1", "9016" } };

	static ConcurrentHashMap<String, MultiConnection> clients = new ConcurrentHashMap<>();

	public static void sendMsg(String msg, String MN) throws Exception {

		MultiConnection multiConnection = clients.get(MN);

		if (multiConnection == null) {
			multiConnection = new MultiConnection(MN, HOSTS);
			clients.put(MN, multiConnection);
		}
		multiConnection.sendMsg(msg);
	}

	public static void main(String[] args) throws Exception {

		ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
		// 延迟1秒执行
		System.out.println("--++--");
		scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
			public void run() {

				String mn = "www";
				String data = "{\"data\":{\"d\":[{\"f\":\"N\",\"p\":\"a21005\",\"v\":1.590434331771835},{\"f\":\"N\",\"p\":\"a05024\",\"v\":2.0516329520975005},{\"f\":\"N\",\"p\":\"a21004\",\"v\":3.233688271710242},{\"f\":\"N\",\"p\":\"a34002\",\"v\":4.486763210243747},{\"f\":\"N\",\"p\":\"a34004\",\"v\":5.840688460850954},{\"f\":\"N\",\"p\":\"a21026\",\"v\":6.712502444743169},{\"f\":\"N\",\"p\":\"a01001\",\"v\":7.727512021469948},{\"f\":\"N\",\"p\":\"a01002\",\"v\":8.773440920490335}],\"l\":[106.3515,24.265165],\"t\":\"20190521111500\"},\"mn\":\"10050\",\"mt\":\"raw\"}";

				String msg = Package20X1.package2011(data);
				// 打印正在执行的缓存线程信息
				try {
					TCPClientNetty.sendMsg(msg, mn);
					TCPClientNetty.sendMsg(msg, "w2");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 1, 1, TimeUnit.SECONDS);

	}
}
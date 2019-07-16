/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：mds
* 文件名称：PluginManager.java
* 包  名  称：com.zeei.das.mds.plugin
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月5日下午1:00:40
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月5日下午1:00:40 创建文件
*
*/

package com.zeei.das.common.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类 名 称：PluginManager 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class PluginManager {

	private static Logger logger = LoggerFactory.getLogger(PluginManager.class);

	/**
	 * 
	 * startPlugin:启动插件
	 *
	 * @param plugin
	 *            void
	 */
	public static void startPlugin(Plugin plugin) {

		try {

			String pname = plugin.getName();

			if (processIsExist(pname)) {
				logger.info(String.format("进程：%s 在运行中。。。", pname));
				killProcess(pname);	
			}

			String cmd = "cmd.exe /c copy \"%JAVA_HOME%\\bin\\java.exe\" \"%JAVA_HOME%\\bin\\" + pname + ".exe \"";
			Runtime.getRuntime().exec(cmd);

			File file = new File(plugin.getDirectory());

			String jar = plugin.getJar();

			// ProcessBuilder 例子 Java程序自重启
			// 用一条指定的命令去构造一个进程生成器
			ProcessBuilder pb = new ProcessBuilder(pname, "-jar", jar);
			// 让这个进程的工作区空间改为F:\dist
			// 这样的话,它就会去F:\dist目录下找Test.jar这个文件
			pb.directory(file);
			// 得到进程生成器的环境 变量,这个变量我们可以改,
			// 改了以后也会反应到新起的进程里面去
			// Map<String, String> env = pb.environment();

			String logPath = file.getParent() + File.separator + "logs";

			if (!new File(logPath).exists()) {
				new File(logPath).mkdirs();
			}

			logPath = logPath + File.separator + pname + ".txt";

			pb.redirectOutput(new File(logPath));
			
			pb.start();
			logger.info(String.format("进程：%s 启动完成。。。", pname));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * restartPlugin:重启插件
	 *
	 * @param plugin
	 *            void
	 */
	public static void restartPlugin(Plugin plugin) {

		try {

			String pname = plugin.getName();

			boolean isExist = processIsExist(pname);

			if (isExist) {
				killProcess(pname);
				logger.info(String.format("进程：%s 被终止。。。", pname));
			}

			File file = new File(plugin.getDirectory());

			// ProcessBuilder 例子 Java程序自重启
			ProcessBuilder pb = new ProcessBuilder(pname, "-jar", plugin.getJar());
			pb.directory(file);
			String logPath = file.getParent() + File.separator + "pluginlog" + File.separator + pname + ".txt";
			pb.redirectOutput(new File(logPath));
			pb.start();
			logger.info(String.format("进程：%s 已重启。。。", pname));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * processIsExist:根据进程名称查询进程
	 *
	 * @param pName
	 * @return Process
	 */
	public static boolean processIsExist(String pName) {

		BufferedReader br = null;
		try {

			String cmd = "TASKLIST /NH /FO CSV /FI \"IMAGENAME EQ " + pName + ".exe\"";

			Process proc = Runtime.getRuntime().exec(cmd);
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				// 判断指定的进程是否在运行
				if (line.contains(pName)) {
					return true;
				}
			}

			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception ex) {
				}
			}

		}

	}

	/**
	 * 
	 * killProcess:杀死进程
	 *
	 * @param pname
	 *            void
	 */
	public static void killProcess(String pname) {

		try {

			String killcmd = String.format("TASKKILL /F /IM %s.exe /T", pname);

			Runtime.getRuntime().exec(killcmd);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	

}

package com.ethan.gapclient.biz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUtil {
	private static final Logger logger = LoggerFactory .getLogger(ConfigUtil.class);
	private static String bizname="gapclient_redis.properties";
	private static String bizpath="/app/gapclient/conf/gapclient_redis.properties";
	
	private volatile static Properties p = null;

	public static Properties getProperties() {
		if (null == p) {
	        synchronized (ConfigUtil.class) {
		        if (null == p) {
		        	p = new Properties();
		        	loadConsoleProperties();
		        }
	        }
	    }
		
		return p;
	}

	private static void loadConsoleProperties() {
		try {
			File file = new File(bizpath);
			if (!file.exists()) {
				p.load(new InputStreamReader(ConfigUtil.class .getClassLoader().getResourceAsStream(bizname), "UTF-8"));
				logger.info("load local file");
			} else {
				p.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

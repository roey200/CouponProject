package com.rands.couponproject.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class LogUtils {

	/**
	 * initLogger - initialize the log4j configuration. call this method before using the logger
	 */
	public static void initLogger() {
		Properties props = new Properties();
		InputStream is = Utils.findInputStream("log4j.properties");
		try {
			props.load(is);
			PropertyConfigurator.configure(props);
		} catch (Exception e) {
			System.out.println("initLogger failed : " + e.toString());
		}

	}
}
package com.rands.couponproject.utils;

import org.apache.log4j.PropertyConfigurator;

public class LogUtils {

	/**
	 * initLogger - initialize the log4j configuration. call this method before using the logger
	 */
	public static void initLogger() {
		String logProprtiesFile = System.getenv("LOG4JCFG");
		if(null != logProprtiesFile && !logProprtiesFile.isEmpty()){
			PropertyConfigurator.configure(logProprtiesFile);
			return;
		}

		String homePath = System.getenv("HOMEPATH");
		PropertyConfigurator.configure(homePath + "/log4j.properties");
	}	

}
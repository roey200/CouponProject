package com.rands.couponproject.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Props {
	private static final Logger logger = Logger.getLogger(Props.class);

	private Properties props = null;
	private String fileName = "comon.properties";
	
	public Props(String fileName) {
		this.fileName = fileName;
	}
	
	
	private void loadProps() {
		if (null != props)
			return;
		
    	props = new Properties();
    	
    	try {
    		ClassLoader cl = Props.class.getClassLoader();
    		//InputStream settingsStream = cl.getResourceAsStream(fileName);
    		InputStream settingsStream = Utils.findInputStream(fileName);
    		props.load(settingsStream);
    	} catch (Exception e) {
    		logger.error("can't load " + fileName + " : " + e.toString());
    		return;
    	}
    	
	}

	public String getString(String key,String defVal) {
		String rslt = null;
    	try {
   			loadProps();
    		rslt = props.getProperty(key);
    	} catch (Exception e) {
    	}
		return (null == rslt) ? defVal : rslt;
	}
	
	public String getString(String key) {
		return getString(key,null);
	}

	public boolean getBool(String key,boolean defVal) {
    	try {
   			loadProps();
   			String val = props.getProperty(key);
    		return Boolean.parseBoolean(val);
    	} catch (Exception e) {
    	}
		return defVal;
	}
	public boolean getBool(String key) {
		return getBool(key,false);
	}
	public int getInt(String key,int defVal) {
    	try {
   			loadProps();
   			String val = props.getProperty(key);
    		return Integer.parseInt(val);
    	} catch (Exception e) {
    	}
		return defVal;
	}
	public int getInt(String key) {
		return getInt(key,0);
	}
	public long getLong(String key,long defVal) {
    	try {
   			loadProps();
   			String val =props.getProperty(key);
    		return Long.parseLong(val);
    	} catch (Exception e) {
    	}
		return defVal;
	}
	public long getLong(String key) {
		return getLong(key,0L);
	}
	public Double getDouble(String key,double defVal) {
    	try {
   			loadProps();
   			String val =props.getProperty(key);
    		return Double.parseDouble(val);
    	} catch (Exception e) {
    	}
		return defVal;
	}
	public Double getDouble(String key) {
		return getDouble(key,0D);
	}

}

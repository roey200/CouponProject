package com.rands.couponproject.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Utils {
	
	private static final Logger logger = Logger.getLogger(Utils.class);
	
	public static final long second = 1000;
	public static final long minute = 60 * second;
	public static final long houer = 60 * minute;
	
	/**
	 * expandEnvVars : replaces environment variables in string to their value.
	 * @param text - the string containing the environment variables (like ${HOME}).
	 * @return - the string with the environment variables replaced/
	 * 
	 * see : http://stackoverflow.com/questions/4752817/expand-environment-variables-in-text
	 */
	public static String expandEnvVars(String text) {
		Map<String, String> envMap = System.getenv();
//		String pattern = "\\$\\{([A-Za-z0-9]+)\\}"; // pattern for things like ${USERNAME}
		String pattern = "\\$\\{([\\w]+)\\}"; // pattern for things like ${USERNAME}
		Pattern expr = Pattern.compile(pattern);
		Matcher matcher = expr.matcher(text);
		while (matcher.find()) {
			String envVarName = matcher.group(1).toUpperCase(); // group(1) is the inner group
		    String envValue = envMap.get(envVarName);
		    if (envValue == null) { // no such variable or empty
		        envValue = "";
		    }
		    String textToReplace = matcher.group(0); // group(0) is the entire match (with the ${ and })
		    text = text.replace(textToReplace,envValue);
		}
		return text;
	}
	
	
	/**
	 * executeSqlScript - executes an sql script 
	 * @param conn - an (opened) data base connection 
	 * @param in - the sql commands to be executed (the commands should be splitted by ;  and may contain comments)
	 * @throws Exception
	 * 
	 * see : http://stackoverflow.com/questions/1044194/running-a-sql-script-using-mysql-with-jdbc
	 */
	public static void executeSqlScript(Connection conn, InputStream in) throws SQLException {

		// this scanner will support the following :
		// 		1) lines ending with ;
		// 		2) comment lines starting with --
		// 		3) comments starting with /* and ending with */
		//
		Scanner s = new Scanner(in);
		s.useDelimiter("/\\*[\\s\\S]*?\\*/|--[^\\r\\n]*|;");

		Statement st = null;

		try
		{
			st = conn.createStatement();

			while (s.hasNext())
			{
				String line = s.next().trim();

				if (!line.isEmpty()) {
					logger.info("executing sql : " + line);
					try {
						st.execute(line);
					} catch (Exception e) {
						logger.info("executing sql failed : " + e.toString());
					}
				}
			}
		} finally {
			if (st != null)
				st.close();
		}
	}
	
	public static void executeSqlScript(Connection conn, String sqlFileName) throws Exception {
		logger.info("executing sqlfile : " + sqlFileName);
		//InputStream is = new FileInputStream(sqlFileName);
		
		ClassLoader cl = Utils.class.getClassLoader();
		InputStream is = cl.getResourceAsStream(sqlFileName);
		executeSqlScript(conn,is);
	}
	
	public static void executeSqlCommand(Connection conn, String sqlCommand) throws SQLException {
		logger.info("executing sql : " + sqlCommand);
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute(sqlCommand);
		} catch (Exception e) {
			logger.info("executing sql failed : " + e.toString());
		} finally {
			if (st != null)
				st.close();
		}			
		
	}
	
	private static final String knownFormats = "yyyy-MM-dd hh:mm;yyyy-MM-dd;dd/MM/yyyy hh:mm;dd/MM/yyyy";
	/**
	 * string2Date - Converts String to Date. 
	 * @param dateString the string to be converted. it may be in any of the following formats {@value #knownFormats}
	 * @return the converted date
	 * @throws Exception if dateString is not in any of the known date formats
	 */
	public static java.util.Date string2Date(String dateString) throws Exception {
		String dateFormats[] = knownFormats.split(";");
		for (String dateFormat : dateFormats) {// try a date format
			try {
				SimpleDateFormat df = new SimpleDateFormat(dateFormat);

				java.util.Date date = df.parse(dateString);
				return date;
			} catch (Exception e) {
			}
		}
		throw new ParseException(dateString, 0);
	}
}

package com.rands.couponproject.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Utils {
	
	private static final Logger logger = Logger.getLogger(Utils.class);
	
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
		} finally
		{
			if (st != null)
				st.close();
		}
	}
	
	public static void executeSqlScript(Connection conn, String sqlFileName) throws Exception {
		logger.info("executing sqlfile : " + sqlFileName);
		InputStream is = new FileInputStream(sqlFileName);
		executeSqlScript(conn,is);
	}	
}

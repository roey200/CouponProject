package com.rands.couponproject.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	/**
	 * expandEnvVars : replaces environment variables in string to their value.
	 * @param text - the string containing the environment variables (like ${HOME}).
	 * @return - the string with the environment variables replaced/
	 * 
	 * see : http://stackoverflow.com/questions/4752817/expand-environment-variables-in-text
	 */
	public static String expandEnvVars(String text) {
		Map<String, String> envMap = System.getenv();
		String pattern = "\\$\\{([A-Za-z0-9]+)\\}"; // pattern for things like ${USERNAME}
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
}

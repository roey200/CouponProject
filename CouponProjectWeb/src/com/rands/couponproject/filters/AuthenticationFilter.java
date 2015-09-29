package com.rands.couponproject.filters;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Scanner;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class RequestLoggingFilter
 */
//@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter implements Filter {

	private ServletContext context;

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		context = fConfig.getServletContext();
		logIt("AuthenticationFilter initialized");

		// Get init parameters 
		String param;
		param = fConfig.getInitParameter("test-param");
		logIt("test-param=" + param);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		// the incoming direction (request)
		logIt("<< AuthenticationFilter");

		// pass the request along the filter chain
		chain.doFilter(request, response);
		
		// the outgoing direction (response)
		logIt(">> AuthenticationFilter");
	}

	@Override
	public void destroy() {
		//we can close resources here
	}

	private void logIt(String string) {
		//context.log(string);
		System.out.println(string);
	}

}

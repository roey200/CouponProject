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
//@WebFilter("/LoggingFilter")
public class LoggingFilter implements Filter {

	private ServletContext context;

	private boolean logRequest = false;
	private boolean logResponse = false;

	private boolean logRequestHeaders = false;
	private boolean logRequestParameters = false;
	private boolean logRequestCookies = false;
	private boolean logRequestBody = false;

	private boolean logResponseHeaders = false;
	private boolean logResponseBody = false;
	
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		context = fConfig.getServletContext();
		logIt("LoggingFilter initialized");

		// Get init parameters 
		String param;
		param = fConfig.getInitParameter("logRequest");
		logIt("logRequest=" + param);

		parseRequestOptions(param);

		param = fConfig.getInitParameter("logResponse");
		logIt("logResponse=" + param);
		parseResponseOptions(param);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		ServletRequest requestWrapper = request;
		ServletResponse responseWrapper = response;

		if (logRequestBody) {
			requestWrapper = new MultiReadHttpServletRequest((HttpServletRequest) request);
		}
		
		if (logResponseBody) {
			responseWrapper = new MultiReadHttpServletResponse((HttpServletResponse) response);
		}

		// the incoming direction (request)
		try {
			if (logRequest)
				log(requestWrapper);
		} catch (Exception e) {
			context.log("LoggingFilter logRequest failed", e);
		}

		// pass the request along the filter chain
		chain.doFilter(requestWrapper, responseWrapper);
		
		// the outgoing direction (response)
		try {
			if (logResponse)
				log(responseWrapper);
		} catch (Exception e) {
			context.log("LoggingFilter logResponse failed", e);
		}
	}

	@Override
	public void destroy() {
		//we can close resources here
	}

	private void logIt(String string) {
		//context.log(string);
		System.out.println(string);
	}

	private void log(ServletRequest request) {
		HttpServletRequest r = (HttpServletRequest) request;

		// the incoming direction (request)
		logIt("<< LoggingFilter");

		String queryString = (null != r.getQueryString()) ? "?" + r.getQueryString() : "";
		logIt("<< Request : " + r.getMethod() + " " + r.getRequestURL() + queryString
				+ " ContentLength=" + r.getContentLength() + " ContentType=" + r.getContentType() + " AuthType=" + r.getAuthType());

		if (null != r.getRequestedSessionId())
			logIt("<< " + "sessionId=" + r.getRequestedSessionId() + " valid=" + r.isRequestedSessionIdValid());

		//logIt("<< " + "getPathTranslated()=" + r.getPathTranslated());

		if (logRequestParameters) {
			Enumeration<String> params = r.getParameterNames();
			while (params.hasMoreElements()) {
				String name = params.nextElement();
				String value = request.getParameter(name);
				this.logIt("<< Params {" + name + "=" + value + "}");
			}
		}

		if (logRequestCookies) {
			Cookie[] cookies = r.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					this.logIt("<< Cookie {" + cookie.getName() + "," + cookie.getValue() + "}");
				}
			}
		}

		if (logRequestHeaders) {
			Enumeration<String> names = r.getHeaderNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				Enumeration<String> headers = r.getHeaders(name);
				while (headers.hasMoreElements()) {
					String value = headers.nextElement();
					logIt("<< Header {" + name + "=" + value + "}");
				}
			}
		}

		if (logRequestBody) {
			String body = ((MultiReadHttpServletRequest)r).getContent();
			
			if (null != body)
				logIt("<< Body {" + body + "}");
		}
	}

	private void log(ServletResponse response) {
		HttpServletResponse r = (HttpServletResponse) response;

		// the outgoing direction (response)
		logIt(">> LoggingFilter response");

		logIt("response : " + r.getStatus() + " " + r.getContentType());
		if (logResponseHeaders) {
			for (String name : r.getHeaderNames()) {
				for (String value : r.getHeaders(name)) {
					logIt(">> Header {" + name + "=" + value + "}");
				}
			}
		}
		
		if (logResponseBody) {
			String body = ((MultiReadHttpServletResponse)r).getContent();

			if (null != body)
				logIt("<< Body {" + body + "}");
		}		
	}
	
	private String extractBody(HttpServletRequest request) {
		String method = request.getMethod().toUpperCase();
		if ("POST".equals(method) || "PUT".equals(method) ) {
			try {
				// wrap the request in order to read the inputstream multiple times
			    MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest(request);

			    //use multiReadRequest.getInputStream(); or multiReadRequest.getReader();
				Scanner s = new Scanner(multiReadRequest.getInputStream(), "UTF-8").useDelimiter("\\A");
				return s.hasNext() ? s.next() : "";
			} catch (Exception e) {

			}
		}
		return null;

	}
	
//	private String extractBody(ServletResponse response) {
//		String method = response.getMethod().toUpperCase();
//		if ("POST".equals(method) || "PUT".equals(method) ) {
//			try {
//				Scanner s = new Scanner(response.getOutputStream(), "UTF-8").useDelimiter("\\A");
//				return s.hasNext() ? s.next() : "";
//			} catch (Exception e) {
//
//			}
//		}
//		return null;
//	}	

	private void parseRequestOptions(String param) {
		if (null == param || param.isEmpty())
			return;

		logRequest = true;
		if (null != param) {
			for (String option : param.split(",")) {
				if (option.equals("headers"))
					logRequestHeaders = true;
				else if (option.equals("parameters"))
					logRequestParameters = true;
				else if (option.equals("cookies"))
					logRequestCookies = true;
				else if (option.equals("body"))
					logRequestBody = true;
			}
		}
	}

	private void parseResponseOptions(String param) {
		if (null == param || param.isEmpty())
			return;

		logResponse = true;
		if (null != param) {
			for (String option : param.split(",")) {
				if (option.equals("headers"))
					logResponseHeaders = true;
				else if (option.equals("body"))
					logResponseBody = true;
			}
		}
	}

}

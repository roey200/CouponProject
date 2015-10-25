package com.rands.couponproject.filters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
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
import javax.servlet.http.Part;

/**
 * Servlet Filter implementation class RequestLoggingFilter
 */
//@WebFilter("/LogFilter")
public class LogFilter implements Filter {

	private static final int MAX_CONTENT_LEN_TO_LOG = (8 * 1024); // 8K
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
		logIt("LogFilter initialized");

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

		boolean logRequestBody = this.logRequestBody;
		boolean logResponseBody = this.logResponseBody;
		
		HttpServletRequest r = (HttpServletRequest) request;
		
		String contentType = r.getContentType();
		int contentLength = r.getContentLength();
		
		//if (contentType.contains("multipart/form-data")) // probably an upload request
		//	logRequestBody = false;
		
		if (contentLength > MAX_CONTENT_LEN_TO_LOG)
			logRequestBody = false; // MultiReadHttpServletRequest can't handle long requests

		if (logRequestBody) {
			request = new MultiReadHttpServletRequest((HttpServletRequest) request);
		}

		if (logResponseBody) {
			response = new MultiReadHttpServletResponse((HttpServletResponse) response);
		}		

		// the incoming direction (request)
		try {
			if (logRequest)
				log(request);
		} catch (Exception e) {
			context.log("LogFilter logRequest failed", e);
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
		
		// the outgoing direction (response)
		try {
			if (logResponse)
				log(response);
		} catch (Exception e) {
			context.log("LogFilter logResponse failed", e);
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
		logIt("<< LogFilter");

		String queryString = (null != r.getQueryString()) ? "?" + r.getQueryString() : "";
		logIt("<< Request : " + r.getMethod() + " " + r.getRequestURL() + queryString
				+ " ContentLength=" + r.getContentLength() + " ContentType=" + r.getContentType() + " AuthType=" + r.getAuthType());

		if (null != r.getRequestedSessionId())
			logIt("<< " + "sessionId=" + r.getRequestedSessionId() + " valid=" + r.isRequestedSessionIdValid());

		//logIt("<< " + "getPathTranslated()=" + r.getPathTranslated());

		logIt("<< *** " + "getContextPath=" + r.getContextPath());
		logIt("<< *** " + "getPathInfo=" + r.getPathInfo());
		logIt("<< *** " + "getRequestURI=" + r.getRequestURI());
		logIt("<< *** " + "getServletPath=" + r.getServletPath());
		
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
		
		if (logRequestParameters) {
			if (isMultiPart(r)) {
				logParts(r);
			} else { // parameters are not supported with multipart/form-data content
				Enumeration<String> params = r.getParameterNames();
				while (params.hasMoreElements()) {
					String name = params.nextElement();
					String value = request.getParameter(name);
					this.logIt("<< Params {" + name + "=" + value + "}");
				}
			}
		}

		//if (logRequestBody) {
		if (r instanceof MultiReadHttpServletRequest) {
			String body = ((MultiReadHttpServletRequest)r).getContent();
			
			if (null != body)
				logIt("<< Body {" + body + "}");
		}
	}

	private void logParts(HttpServletRequest r) {
		Collection<Part> parts = null;
		try {
			parts = r.getParts();
		} catch (Exception e) {
			logIt("** ERROR ** getParts() failed : " + e.toString());
		}
		if (null == parts)
			return;
		for (Part part:parts) {
			String name = part.getName();
			String contentType = part.getContentType();
			long size = part.getSize();
			String fileName = part.getSubmittedFileName();
			if (null != fileName) {
				logIt("<< Part name=" + name + ", Submitted filename=" + fileName + ", size = " + size + ", content type = " + contentType);
			} else {
				logIt("<< Part name=" + name + ", data=" + getPartData(part) + ", size = " + size);
			}
		}
	}
	
	private String getPartData(Part part) {
		if (null == part)
			return "";
		try {
			InputStream is = part.getInputStream();
			java.util.Scanner scanner = new java.util.Scanner(is,"UTF-8").useDelimiter("\\A");
			String data = scanner.hasNext() ? scanner.next() : "";
			return data;
		} catch (Exception e) {
			return e.toString();
		}
	}	

	private void log(ServletResponse response) {
		HttpServletResponse r = (HttpServletResponse) response;

		// the outgoing direction (response)
		logIt(">> LogFilter response");

		logIt("response : " + r.getStatus() + " " + r.getContentType());
		if (logResponseHeaders) {
			for (String name : r.getHeaderNames()) {
				for (String value : r.getHeaders(name)) {
					logIt(">> Header {" + name + "=" + value + "}");
				}
			}
		}
		
		//if (logResponseBody) {
		if (r instanceof MultiReadHttpServletResponse) {
			String body = ((MultiReadHttpServletResponse)r).getContent();

			if (null != body)
				logIt("<< Body {" + body + "}");
		}		
	}
	
//	private String extractBody(HttpServletRequest request) {
//		String method = request.getMethod().toUpperCase();
//		if ("POST".equals(method) || "PUT".equals(method) ) {
//			try {
//				// wrap the request in order to read the inputstream multiple times
//			    MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest(request);
//
//			    //use multiReadRequest.getInputStream(); or multiReadRequest.getReader();
//				Scanner s = new Scanner(multiReadRequest.getInputStream(), "UTF-8").useDelimiter("\\A");
//				return s.hasNext() ? s.next() : "";
//			} catch (Exception e) {
//
//			}
//		}
//		return null;
//
//	}
	
	private boolean isMultiPart(HttpServletRequest request) {
		String contentType = request.getContentType();

		return (null != contentType && contentType.toLowerCase().contains("multipart/form-data"));
	}

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

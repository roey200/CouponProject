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
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.rands.couponproject.auth.AuthUtils;
import com.rands.couponproject.exceptions.CouponProjectException.AccessForbiddenException;
import com.rands.couponproject.exceptions.CouponProjectException.AdminLoginException;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;
import com.rands.couponproject.facede.AdminFacade;
import com.rands.couponproject.facede.CouponClientFacade;
import com.rands.couponproject.rest.services.AdminService;

/**
 * Servlet Filter implementation class RequestLoggingFilter
 */
//@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter implements Filter {
	
	static Logger logger = Logger.getLogger(AuthenticationFilter.class);

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
		boolean redirect = false;
		
		// the incoming direction (request)
		logIt("<< AuthenticationFilter");
		
		HttpServletRequest httpRequest = request instanceof HttpServletRequest ? (HttpServletRequest) request : null;
        HttpServletResponse httpResponse = response instanceof HttpServletResponse ? (HttpServletResponse) response : null;

        if (httpRequest == null || httpResponse == null) { // we only handle http based requests
			// pass the request along the filter chain
			chain.doFilter(request, response);
		
			// the outgoing direction (response)
			logIt(">> AuthenticationFilter");
			return;
        }
		
        if (isAuthenticationRequired(httpRequest)) { // authentication is required
        	logIt("AuthenticationFilter : authentication is required");
        	
        	if (isRestServiceRequest(httpRequest)) { // request is for a rest web service
            	logIt("AuthenticationFilter : aborting rest request");
        		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        		return;
        	}
        	
           	logIt("AuthenticationFilter : redirecting to login page");
   			String loginPage = "/login.html";
    			
   			httpResponse.sendRedirect(httpRequest.getContextPath() + loginPage);
   			return;
        }
		// proceed with the request

		// pass the request along the filter chain
		chain.doFilter(request, response);
		
		// the outgoing direction (response)
		logIt(">> AuthenticationFilter");
	}

	/**
	 * isRestServiceRequest : checks checks if the incoming request is for a rest web service
	 * note that all the rest web services in this web application are "under" the /rest path.
	 * @param httpRequest
	 * @return true if the incoming request is for a rest web service, false otherwise
	 */
	private boolean isRestServiceRequest(HttpServletRequest httpRequest) {
		return httpRequest.getServletPath().equals("/rest");
	}

	/**
	 * isAuthenticationRequired : checks if the incoming request may proceed or not.
	 * the incoming request may proceed, if it is a request for authentication or if it was already authenticated.
	 * @param httpRequest
	 * @return true if authentication is required, false otherwise (the request may proceed)
	 */
	private boolean isAuthenticationRequired(HttpServletRequest httpRequest) {
		
		if (httpRequest.getRequestURI().contains("/login")){ // this is a login request, let it proceed
			return false;
		}
		if (httpRequest.getRequestURI().contains("/logout")){ // this is a logout request, let it proceed
			return false;
		}
//		if (httpRequest.getRequestURI().contains("/upload")){ // for testing
//			return false;
//		}
		
		// check if it was already authenticated
		try {
			AuthUtils.getCredentials(CouponClientFacade.class, httpRequest);
		} catch (Exception e) {
			return true;
		}
		
		return false; // let it proceed
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

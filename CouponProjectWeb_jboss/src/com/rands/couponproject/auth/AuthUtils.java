package com.rands.couponproject.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.rands.couponproject.exceptions.CouponProjectException.AccessForbiddenException;
import com.rands.couponproject.exceptions.CouponProjectException.AdminLoginException;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;
import com.rands.couponproject.facede.CouponClientFacade;

public class AuthUtils {
	private static final String CredentialsKey =  "loginCredentials";
	
	public static <T> T getCredentials(java.lang.Class<T> CredentialsClass,HttpServletRequest request) throws LoginException {
		HttpSession session = request.getSession(false);
		if (null == session) { // now session yet
			throw new LoginException("not logged in yet");
		}

		T credentials;
		try {
			credentials = (T) session.getAttribute(CredentialsKey);
		} catch (ClassCastException e) { 
			throw new AccessForbiddenException("access forbidden");
		}
		if (null == credentials) {
			throw new LoginException("not logged in yet");
		}
		return credentials;
	}
	
	public static void setCredentials(Object credentials,HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute(CredentialsKey, credentials);
	}
}

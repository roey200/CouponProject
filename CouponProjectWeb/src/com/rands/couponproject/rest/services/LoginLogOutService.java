package com.rands.couponproject.rest.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import com.rands.couponproject.CouponSystem;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;
import com.rands.couponproject.facede.CouponClientFacade;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.rest.Globals;

@Path("/")
public class LoginLogOutService {

	static Logger logger = Logger.getLogger(LoginLogOutService.class);

	@Context
	HttpServletRequest request;

	@Path("/login")
	@POST
	public void login(@FormParam("UserName") String userName, @FormParam("password") String password, @FormParam("type") ClientType clientType) throws LoginException {
		logger.debug("login parameters " + userName + " " + password + " " + clientType);
		CouponClientFacade facade;
		try {
			facade = CouponSystem.getInstance().login(userName, password, clientType);
		} catch (Exception e) {
			logger.error("login failed : " + e.toString());
			throw e;
		}
		HttpSession session = request.getSession();
		session.setAttribute(Globals.FACADE_KEY, facade);

	}

	@Path("/logout")
	@POST
	public void logout() throws LoginException {
		logger.debug("logout");
		HttpSession session = request.getSession(false);
		if (session == null)
			logger.debug("not logged in");
		else
			session.invalidate();

	}
	
	
}

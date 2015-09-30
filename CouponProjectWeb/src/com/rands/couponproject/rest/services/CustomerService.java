package com.rands.couponproject.rest.services;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.rands.couponproject.CouponSystem;
import com.rands.couponproject.facede.AdminFacade;
import com.rands.couponproject.facede.CustomerFacade;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;
import com.rands.couponproject.model.Customer;
import com.rands.couponproject.rest.Globals;
import com.rands.couponproject.exceptions.CouponProjectException;
import com.rands.couponproject.exceptions.CouponProjectException.AccessForbiddenException;
import com.rands.couponproject.exceptions.CouponProjectException.AdminLoginException;
import com.rands.couponproject.exceptions.CouponProjectException.CustomerLoginException;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;

//Sets the path to base URL + /customer
@Path("/customer")
public class CustomerService {

	static Logger logger = Logger.getLogger(CustomerService.class);

	@Context
	HttpServletRequest request;

	private CustomerFacade getCustomerFacade() throws LoginException {
		HttpSession session = request.getSession();

		CustomerFacade facade;
		try {
			facade = (CustomerFacade) session.getAttribute(Globals.FACADE_KEY);
		} catch (ClassCastException e) { // may be logged in as admin or company
			throw new AccessForbiddenException("customer access forbidden");
		}
		if (null == facade) {
			throw new CustomerLoginException("not logged in yet");
		}
		return facade;
	}
	
	

	// example :
	// http://localhost:9090/CouponProjectWeb/customer/coupons
	//
	@Path("/coupons")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getAllPurchasedCoupons() throws Exception {
		logger.debug("getAllPurchasedCoupons");

		CustomerFacade customerFacade = getCustomerFacade();
    	return customerFacade.getAllPurchasedCoupons();
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/customer/coupons/SPORTS
	//
	@Path("/coupons/{CouponType}") 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getAllPurchasedCouponsByType(@PathParam("CouponType") CouponType couponType) throws Exception {
		logger.debug("getAllPurchasedCouponsByType " + couponType);
		
		CustomerFacade customerFacade = getCustomerFacade();
    	return customerFacade.getAllPurchasedCouponsByType(couponType);
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/customer/coupons/100
	//
	@Path("/coupons/{price : \\d+}") 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getAllPurchasedCouponsByPrice(@PathParam("price") int couponPrice) throws Exception {
		logger.debug("getAllPurchasedCouponsByPrice " + couponPrice);		

		CustomerFacade customerFacade = getCustomerFacade();
    	return customerFacade.getAllPurchasedCouponsByPrice(couponPrice);
	}


	
	
	@Path("/buy") 
	@POST
//	@Consumes(MediaType.APPLICATION_TEXT)
	public void purchaseCoupon(String couponName) throws Exception {
		logger.debug("purchaseCoupon " + couponName);		

		CustomerFacade customerFacade = getCustomerFacade();
    	customerFacade.purchaseCoupon(couponName);
	}
	
	
	@Path("/buy") 
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void purchaseCoupon(Coupon Coupon) throws Exception {
		logger.debug("purchaseCoupon " + Coupon);		

		CustomerFacade customerFacade = getCustomerFacade();
    	customerFacade.purchaseCoupon(Coupon);
	}
	
	
}
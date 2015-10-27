package com.rands.couponproject.rest.services;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.rands.couponproject.facede.CompanyFacade;
import com.rands.couponproject.facede.CustomerFacade;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;
import com.rands.couponproject.utils.Utils;
import com.rands.couponproject.auth.AuthUtils;
import com.rands.couponproject.exceptions.CouponProjectException;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;

//Sets the path to base URL + /customer
@Path("/customer")
public class CustomerService {

	static Logger logger = Logger.getLogger(CustomerService.class);

	@Context
	HttpServletRequest request;

//	private CustomerFacade getCustomerFacade() throws LoginException {
//		HttpSession session = request.getSession();
//
//		CustomerFacade facade;
//		try {
//			facade = (CustomerFacade) session.getAttribute(Globals.FACADE_KEY);
//		} catch (ClassCastException e) { // may be logged in as admin or company
//			throw new AccessForbiddenException("customer access forbidden");
//		}
//		if (null == facade) {
//			throw new CustomerLoginException("not logged in yet");
//		}
//		return facade;
//	}
	
	private CustomerFacade getCustomerFacade() throws LoginException {
		return AuthUtils.getCredentials(CustomerFacade.class, request);
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
	
	// example :
	// http://localhost:9090/CouponProjectWeb/company/coupons/2016-12-24
	//
	@Path("/coupons/date/{toDate}") 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getAllPurchasedCouponsByDate(@PathParam("toDate") String toDate) throws Exception {
		logger.debug("getAllPurchasedCouponsByDate " + toDate);
		
		CustomerFacade customerFacade = getCustomerFacade();
		try {
			Date date = Utils.string2Date(toDate);
	    	return customerFacade.getAllPurchasedCouponsByDate(date);
		}
		catch (Exception e) {
			throw new CouponProjectException("Invalid date : " + toDate);
		}
	}	
	

	// example :
	// http://localhost:9090/CouponProjectWeb/customer/coupons
	//
	@Path("/buylist")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getPurchableCoupons() throws Exception {
		logger.debug("getPurchableCoupons");

		CustomerFacade customerFacade = getCustomerFacade();
    	return customerFacade.getPurchableCoupons();
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
	
	// example :
	// http://localhost:9090/CouponProjectWeb/customer/coupons
	//
	@Path("/coupontypes")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CouponType[] getCouponTypes() throws Exception {
		logger.debug("getCouponTypes");
		
		return CouponType.values();
	}	
	
	
}
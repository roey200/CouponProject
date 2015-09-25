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
import com.rands.couponproject.facede.CustomerFacade;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;
import com.rands.couponproject.model.Customer;
import com.rands.couponproject.exceptions.CouponProjectException;
import com.rands.couponproject.exceptions.CouponProjectException.CustomerLoginException;

//Sets the path to base URL + /customer
@Path("/customer")
public class CustomerService {

	static Logger logger = Logger.getLogger(CustomerService.class);

	static final String FACADE_KEY =  "loginFacade";
	
//	static CouponSystem couponSystem;
//	static CustomerFacade customerFacade;
//	couponSystem = CouponSystem.getInstance();
//
//	customerFacade = (CustomerFacade) couponSystem.login("customer", "1234", ClientType.ADMIN);
//	
	
	@Context
	HttpServletRequest request;

	private CustomerFacade getCustomerFacade() throws CouponProjectException {
		HttpSession session = request.getSession();

		try {
			CustomerFacade customerFacade = (CustomerFacade) session.getAttribute(FACADE_KEY);
			if (null == customerFacade) {
				logger.error("customer not logged in session = " + session.getId());
				//throw new CustomerLoginException("customer not logged in session = " + session.getId());
				
				CouponSystem couponSystem = CouponSystem.getInstance();
				customerFacade = (CustomerFacade) couponSystem.login("roey", "r1234", ClientType.CUSTOMER);
				
				session.setAttribute(FACADE_KEY, customerFacade);

			}
			return customerFacade;
		} catch (CustomerLoginException e) {
			throw e;

		} catch (Exception e) {
			logger.error("getCustomerFacade() failed : " + e.toString());
			throw new CustomerLoginException("could not get CustomerFacade object");
		}
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
		
		
		if (CouponType.FOOD == couponType) {
			throw new CouponProjectException("123 Exception");
//			throw new WebApplicationException(
//			        Response
//			          .status(Status.BAD_REQUEST)
//			          .entity("bad coupon type: " + couponType) // + " (" + e.getMessage() + ")")
//			          .build()
//			      );

		}
		
		
		logger.debug("123");
		logger.debug("37");
		
		
		

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

	
}
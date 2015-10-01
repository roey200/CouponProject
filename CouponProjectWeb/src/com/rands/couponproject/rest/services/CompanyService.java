package com.rands.couponproject.rest.services;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.rands.couponproject.auth.AuthUtils;
import com.rands.couponproject.exceptions.CouponProjectException;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;
import com.rands.couponproject.facede.CompanyFacade;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;
import com.rands.couponproject.utils.Utils;


//Sets the path to base URL + /company
@Path("/company")
public class CompanyService {

	static Logger logger = Logger.getLogger(CompanyService.class);

	@Context
	HttpServletRequest request;

//	private CompanyFacade getCompanyFacade() throws LoginException {
//		HttpSession session = request.getSession();
//
//		CompanyFacade facade;
//		try {
//			facade = (CompanyFacade) session.getAttribute(Globals.FACADE_KEY);
//		} catch (ClassCastException e) { // may be logged in as admin or customer
//			throw new AccessForbiddenException("company access forbidden");
//		}
//		if (null == facade) {
//			throw new AdminLoginException("not logged in yet");
//		}
//		return facade;
//	}
	
	private CompanyFacade getCompanyFacade() throws LoginException {
		return AuthUtils.getCredentials(CompanyFacade.class, request);
	}	
	
	// Handling companies
	
	// example :
	// http://localhost:9090/CouponProjectWeb/company/coupon/5
	//
	@Path("/coupon/{id : \\d+}") // id pattern (digits only)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon getCoupon( @PathParam("id") long couponId ) throws Exception {
		logger.debug("getCoupon " + couponId);

		CompanyFacade companyFacade = getCompanyFacade();
    	return companyFacade.getCoupon(couponId);
	}
	
//	// example :
//	// http://localhost:9090/CouponProjectWeb/company/coupon/cocacola
//	//	
//	@Path("/coupon/{name: [a-zA-Z][a-zA-Z_0-9%]+}") // name pattern
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public Company getCoupon( @PathParam("name") String couponName ) throws Exception {
//		logger.debug("getCoupon " + couponName);
//
//		CompanyFacade companyFacade = getCompanyFacade();
//    	return companyFacade.getCoupon(couponName);
//	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/company/coupons
	//	
	@Path("/coupons") 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getCoupons() throws Exception{
		logger.debug("getCoupons" );

		CompanyFacade companyFacade = getCompanyFacade();
    	return companyFacade.getAllCoupons();
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/company/coupons/SPORTS
	//
	@Path("/coupons/{CouponType}") 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getCouponsByType(@PathParam("CouponType") CouponType couponType) throws Exception {
		logger.debug("getCouponsByType " + couponType);
		

		CompanyFacade companyFacade = getCompanyFacade();
    	return companyFacade.getCouponsByType(couponType);
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/company/coupons/150
	//
	@Path("/coupons/{price : \\d+}") 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getCouponsByPrice(@PathParam("price") int couponPrice) throws Exception {
		logger.debug("getCouponsByPrice " + couponPrice);		

		CompanyFacade companyFacade = getCompanyFacade();
    	return companyFacade.getCouponsByPrice(couponPrice);
	}

	// example :
	// http://localhost:9090/CouponProjectWeb/company/coupons/2016-12-24
	//
	@Path("/coupons/date/{toDate}") 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Coupon> getCouponsByDate(@PathParam("toDate") String toDate) throws Exception {
		logger.debug("getCouponsByDate " + toDate);
		
		CompanyFacade companyFacade = getCompanyFacade();
		try {
			Date date = Utils.string2Date(toDate);
	    	return companyFacade.getCouponsByDate(date);
		}
		catch (Exception e) {
			throw new CouponProjectException("Invalid date : " + toDate);
		}
	}	

	// example :
	// http://localhost:9090/CouponProjectWeb/company/coupon
	// and json Company object like (id may be 0 or ommited):
	// {"id":0,"companyName":"cocacola","password":"c1234","email":"cocacola@cola.com","coupons":[]}
	//
	@POST
	@Path("/coupon")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	public void createCoupon(Coupon coupon) throws Exception {
		logger.debug("createCoupon " + coupon);

		CompanyFacade companyFacade = getCompanyFacade();
		
		try {
			companyFacade.createCoupon(coupon);
		} catch (Exception e) {
			logger.error("createCoupon failed : " + e.toString());
			throw e;
		}

	}	
	
	
	// example :
	// http://localhost:9090/CouponProjectWeb/company/coupon
	// and json Company object like :
	// {"id":1,"companyName":"cocacola","password":"c1234","email":"cocacola@cola.com","coupons":[]}
	//
	@PUT
	@Path("/coupon")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	public void updateCoupon(Coupon coupon) throws Exception {
		logger.debug("updateCoupon " + coupon);

		CompanyFacade companyFacade = getCompanyFacade();
		
		try {
			companyFacade.updateCoupon(coupon);
		} catch (Exception e) {
			logger.error("updateCoupon failed : " + e.toString());
			throw e;
		}

	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/company/coupon/1
	//	
	@Path("/coupon/{id : \\d+}") // id pattern (digits only)
	@DELETE
	public void removeCoupon( @PathParam("id") long couponId ) throws Exception {
		logger.debug("removeCoupon " + couponId);
		
		CompanyFacade companyFacade = getCompanyFacade();
		companyFacade.removeCoupon(couponId);
	}
	
	@DELETE
	@Path("/coupon")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	public void removeCoupon(Coupon coupon) throws Exception {
		logger.debug("removeCoupon " + coupon);

		CompanyFacade companyFacade = getCompanyFacade();
		try {
			companyFacade.removeCoupon(coupon);
		} catch (Exception e) {
			logger.error("removeCoupon failed : " + e.toString());
			throw e;
		}

	}

} 
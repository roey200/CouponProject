package com.rands.couponproject.rest.services;

import java.util.Collection;


//import javax.ejb.EJB;
//import javax.ejb.LocalBean;
//import javax.ejb.Stateless;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
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

import com.rands.couponproject.CouponSystem;
import com.rands.couponproject.facede.AdminFacade;
import com.rands.couponproject.facede.CompanyFacade;
import com.rands.couponproject.jpa.Income;
import com.rands.couponproject.jpa.IncomeType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Customer;
import com.rands.couponproject.utils.Utils;
import com.rands.couponproject.auth.AuthUtils;
import com.rands.couponproject.ejb.IncomeServiceBean;
import com.rands.couponproject.ejb.client.IncomeEJBClient;
import com.rands.couponproject.exceptions.CouponProjectException;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;

//Sets the path to base URL + /payments
@Path("/payments")
//@Stateless
//@LocalBean
public class PaymentsService {

	static Logger logger = Logger.getLogger(PaymentsService.class);

	@Context
	HttpServletRequest request;
	
//	@EJB
	IncomeServiceBean isb;

	
	private AdminFacade getAdminFacade() throws LoginException {
		return AuthUtils.getCredentials(AdminFacade.class, request);
	}
	
	private CompanyFacade getCompanyFacade() throws LoginException {
		return AuthUtils.getCredentials(CompanyFacade.class, request);
	}	
	
	// example :
	// http://localhost:9090/CouponProjectWeb/payments/all
	//
	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Income> viewAllIncome() throws Exception {
		logger.debug("viewAllIncome");

		//AdminFacade adminFacade = getAdminFacade();
    	isb = IncomeEJBClient.lookup();
		Collection<Income> rslt = isb.viewAllIncome();
		return rslt;
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/payments/company
	//	
	@Path("/company")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Income> viewIncomeByCompany() throws Exception {
		logger.debug("viewIncomeByCompany");

		CompanyFacade companyFacade = getCompanyFacade();
		String currentCompany = companyFacade.getCompany().getCompanyName();

		return viewIncomeByCompany(currentCompany);
	}
	
	
	// example :
	// http://localhost:9090/CouponProjectWeb/payments/company/cocacola
	//	
	@Path("/company/{name}") // name pattern
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Income> viewIncomeByCompany( @PathParam("name") String companyName ) throws Exception {
		logger.debug("viewIncomeByCompany " + companyName);

		//AdminFacade adminFacade = getAdminFacade();
    	isb = IncomeEJBClient.lookup();
		Collection<Income> rslt = isb.viewIncomeByCompany(companyName);
		return rslt;
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/payments/customer/roey
	//	
	@Path("/customer/{name}") // name pattern
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Income> viewIncomeByCustomer( @PathParam("name") String customerName ) throws Exception {
		logger.debug("viewIncomeByCustomer " + customerName);

		//AdminFacade adminFacade = getAdminFacade();
    	isb = IncomeEJBClient.lookup();
		Collection<Income> rslt = isb.viewIncomeByCustomer(customerName);
		return rslt;
	}
	
	
}
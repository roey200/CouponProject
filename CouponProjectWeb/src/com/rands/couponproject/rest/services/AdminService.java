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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.rands.couponproject.CouponSystem;
import com.rands.couponproject.facede.AdminFacade;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Customer;
import com.rands.couponproject.exceptions.CouponProjectException;
import com.rands.couponproject.exceptions.CouponProjectException.AdminLoginException;

//Sets the path to base URL + /admin
@Path("/admin")
public class AdminService {

	static Logger logger = Logger.getLogger(AdminService.class);

	static final String FACADE_KEY =  "loginFacade";
	
//	static CouponSystem couponSystem;
//	static AdminFacade adminFacade;
//	couponSystem = CouponSystem.getInstance();
//
//	adminFacade = (AdminFacade) couponSystem.login("admin", "1234", ClientType.ADMIN);
//	
	
	@Context
	HttpServletRequest request;

	private AdminFacade getAdminFacade() throws CouponProjectException {
		HttpSession session = request.getSession();

		try {
			AdminFacade adminFacade = (AdminFacade) session.getAttribute(FACADE_KEY);
			if (null == adminFacade) {
				logger.error("admin not logged in session = " + session.getId());
				//throw new AdminLoginException("admin not logged in session = " + session.getId());
				
				CouponSystem couponSystem = CouponSystem.getInstance();
				adminFacade = (AdminFacade) couponSystem.login("admin", "1234", ClientType.ADMIN);
				
				session.setAttribute(FACADE_KEY, adminFacade);

			}
			return adminFacade;
		} catch (AdminLoginException e) {
			throw e;

		} catch (Exception e) {
			logger.error("getAdminFacade() failed : " + e.toString());
			throw new AdminLoginException("could not get AdminFacade object");
		}
	}
	
	
	@Path("/hello")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {

		return "Hello Admin  ";

	}
	
	// Handling companies
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/company/5
	//
	@Path("/company/{id : \\d+}") // id pattern (digits only)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Company getCompany( @PathParam("id") long companyId ) throws Exception {
		logger.debug("getCompany " + companyId);

		AdminFacade adminFacade = getAdminFacade();
    	return adminFacade.getCompany(companyId);
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/company/cocacola
	//	
	@Path("/company/{name: [a-zA-Z][a-zA-Z_0-9%]+}") // name pattern
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Company getCompany( @PathParam("name") String companyName ) throws Exception {
		logger.debug("getCompany " + companyName);

		AdminFacade adminFacade = getAdminFacade();
    	return adminFacade.getCompany(companyName);
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/companyies
	//	
	@Path("/companies") 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Company> getAllCompanies() throws Exception{
		logger.debug("getAllCompanies" );

		AdminFacade adminFacade = getAdminFacade();
    	return adminFacade.getAllCompanies();
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/company
	// and json Company object like (id may be 0 or ommited):
	// {"id":0,"companyName":"cocacola","password":"c1234","email":"cocacola@cola.com","coupons":[]}
	//
	@POST
	@Path("/company")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	public void createCompany(Company company) throws Exception {
		logger.debug("createCompany " + company);

		AdminFacade adminFacade = getAdminFacade();
		
		try {
			adminFacade.createCompany(company);
		} catch (Exception e) {
			logger.error("createCompany failed : " + e.toString());
			throw e;
		}

	}	
	
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/company
	// and json Company object like :
	// {"id":1,"companyName":"cocacola","password":"c1234","email":"cocacola@cola.com","coupons":[]}
	//
	@PUT
	@Path("/company")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	public void updateCompany(Company company) throws Exception {
		logger.debug("updateCompany " + company);

		AdminFacade adminFacade = getAdminFacade();
		
		try {
			adminFacade.updateCompany(company);
		} catch (Exception e) {
			logger.error("updateCompany failed : " + e.toString());
			throw e;
		}

	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/company/1
	//	
	@Path("/company/{id : \\d+}") // id pattern (digits only)
	@DELETE
	public void removeCompany( @PathParam("id") long companyId ) throws Exception {
		logger.debug("removeCompany " + companyId);
		
		AdminFacade adminFacade = getAdminFacade();
		adminFacade.removeCompany(companyId);
	}
	
	@DELETE
	@Path("/company")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	public void removeCompany(Company company) throws Exception {
		logger.debug("removeCompany " + company);

		AdminFacade adminFacade = getAdminFacade();
		try {
			adminFacade.removeCompany(company);
		} catch (Exception e) {
			logger.error("removeCompany failed : " + e.toString());
			throw e;
		}

	}
	
	
	// Handling customers
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/customer/5
	//
	@Path("/customer/{id : \\d+}") // id pattern (digits only)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Customer getCustomer( @PathParam("id") long customerId ) throws Exception {
		logger.debug("getCustomer " + customerId);

		AdminFacade adminFacade = getAdminFacade();
    	return adminFacade.getCustomer(customerId);
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/customer/"miss piggie"
	//
	@Path("/customer/{name: [a-zA-Z][a-zA-Z_0-9%]+}") // name pattern
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Customer getCustomer( @PathParam("name") String customerName ) throws Exception {
		logger.debug("getCustomer " + customerName);

		AdminFacade adminFacade = getAdminFacade();
    	return adminFacade.getCustomer(customerName);
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/customers
	//
	@Path("/customers") // name pattern
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Customer> getAllCustomers() throws Exception{
		logger.debug("getAllCompanies" );

		AdminFacade adminFacade = getAdminFacade();
    	return adminFacade.getAllCustomers();
	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/customer
	// and json Company object like (id may be 0 or ommited):
	// {"customerName":"miss piggie","password":"m1234","coupons":[]}
	//	
	@POST
	@Path("/customer")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	public void createCustomer(Customer customer) throws Exception {
		logger.debug("createCustomer " + customer);

		AdminFacade adminFacade = getAdminFacade();
		
		try {
			adminFacade.createCustomer(customer);
		} catch (Exception e) {
			logger.error("createCustomer failed : " + e.toString());
			throw e;
		}

	}	
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/customer
	// and json Company object like (id may be 0 or ommited):
	// {"id":5,"customerName":"miss piggie","password":"m1234","coupons":[]}
	//		
	@PUT
	@Path("/customer")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	public void updateCustomer(Customer customer) throws Exception {
		logger.debug("updateCustomer " + customer);

		AdminFacade adminFacade = getAdminFacade();
		
		try {
			adminFacade.updateCustomer(customer);
		} catch (Exception e) {
			logger.error("updateCustomer failed : " + e.toString());
			throw e;
		}

	}
	
	// example :
	// http://localhost:9090/CouponProjectWeb/admin/customer/5
	//			
	@Path("/customer/{id : \\d+}") // id pattern (digits only)
	@DELETE
	public void removeCustomer( @PathParam("id") long customerId ) throws Exception {
		logger.debug("removeCustomer " + customerId);
		
		AdminFacade adminFacade = getAdminFacade();
		adminFacade.removeCustomer(customerId);
	}
	
	@DELETE
	@Path("/customer")
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON)
	public void removeCustomer(Customer customer) throws Exception {
		logger.debug("removeCustomer " + customer);

		AdminFacade adminFacade = getAdminFacade();
		try {
			adminFacade.removeCustomer(customer);
		} catch (Exception e) {
			logger.error("removeCustomer failed : " + e.toString());
			throw e;
		}

	}
	
	
	
}
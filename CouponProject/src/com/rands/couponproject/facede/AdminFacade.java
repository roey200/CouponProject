package com.rands.couponproject.facede;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.rands.couponproject.dao.CompanyDAO;
import com.rands.couponproject.dao.CompanyDBDAO;
import com.rands.couponproject.dao.CustomerDAO;
import com.rands.couponproject.dao.CustomerDBDAO;
import com.rands.couponproject.exceptions.CouponProjectException.AdminLoginException;
import com.rands.couponproject.exceptions.CouponProjectException.CompanyException;
import com.rands.couponproject.exceptions.CouponProjectException.CustomerException;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Customer;

public class AdminFacade implements CouponClientFacade {
	static Logger logger = Logger.getLogger(AdminFacade.class);

//	private CompanyDAO companyDAO;
//	private CustomerDAO customerDAO;
//	private CouponDAO couponDAO;
	
	private AdminFacade() {
//		companyDAO = new CompanyDBDAO();
//		customerDAO = new CustomerDBDAO();
//		couponDAO = new CouponDBDAO();
	}
	

	public static CouponClientFacade login(String name, String password, ClientType clientType) throws LoginException {
		String rightPassword = "1234";
		
		if (clientType != ClientType.ADMIN) {
			logger.error("admin login failed mismitch clientType = " + clientType);
			throw new AdminLoginException("Invalid ClientType : " + clientType);
		}
		
		if (clientType == ClientType.ADMIN && "admin".equals(name) && rightPassword.equals(password))
		{
			return new AdminFacade();
		}
		
		logger.error("admin login failed name = " + name);
		throw new AdminLoginException(name);		
	}
	
	// Handling companies
	
	public void createCompany(Company c) throws Exception
	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		String name = c.getCompanyName();
		if (null != (companyDAO.getCompany(name))) {
			logger.error("createCompany " + name + " already exists");
			throw new CompanyException("createCompany " + name + " already exists");
		}
		try {
			companyDAO.createCompany(c);
		} catch (Exception e) {
			logger.error("createCompany " + name + " failed : " + e.toString());
			throw e;
		}
	}

	public void removeCompany(long companyId) throws Exception	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		companyDAO.removeCompany(companyId);
	}

	public void removeCompany(Company company) throws Exception	{
		removeCompany(company.getId());
	}

	public void updateCompany(Company c) throws Exception
	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		try {
			companyDAO.updateCompany(c);
		} catch (Exception e) {
			logger.error("updateCompany failed : " + e.toString());
			throw e;
		}
	}

	public Company getCompany(long id)
	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		return companyDAO.getCompany(id);
	}
	
	public Company getCompany(String name)
	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		return companyDAO.getCompany(name);
	}
	
	public Collection<Company> getAllCompanies() throws Exception
	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		try {
			return companyDAO.getAllCompanies();
		} catch (Exception e) {
			logger.error("getAllCompanies failed : " + e.toString());
			throw e;
		}
	}
	
	// Handling customers	

	public void createCustomer(Customer c) throws Exception
	{
		CustomerDAO customerDAO = new CustomerDBDAO();
		String name = c.getCustomerName();
		if (null != customerDAO.getCustomer(name)) {
			logger.error("createCustomer " + name + " already exists");
			throw new CustomerException("createCustomer " + name + " already exists");			
		}
		try {
			customerDAO.createCustomer(c);
		} catch (Exception e) {
			logger.error("createCustomer failed : " + e.toString());
			throw e;
		}
	}

	public void removeCustomer(long customerId) throws Exception {
		CustomerDAO customerDAO = new CustomerDBDAO();
		customerDAO.removeCustomer(customerId);
	}

	public void removeCustomer(Customer customer) throws Exception {
		removeCustomer(customer.getId());
	}	

	public void updateCustomer(Customer c) throws Exception
	{
		CustomerDAO customerDAO = new CustomerDBDAO();
		try {
			customerDAO.updateCustomer(c);
		} catch (Exception e) {
			logger.error("updateCustomer " + c.getCustomerName() + " failed : " + e.toString());
			throw e;
		}
	}

	public Collection<Customer> getAllCustomers()
	{
		CustomerDAO customerDAO = new CustomerDBDAO();
		return customerDAO.getAllCustomers();
	}

	public Customer getCustomer(long id)
	{
		CustomerDAO customerDAO = new CustomerDBDAO();
		return customerDAO.getCustomer(id);
	}
	public Customer getCustomer(String name)
	{
		CustomerDAO customerDAO = new CustomerDBDAO();
		return customerDAO.getCustomer(name);
	}

}

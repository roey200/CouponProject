package com.rands.couponproject.facede;

import java.sql.Connection;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.rands.couponproject.ConnectionPool;
import com.rands.couponproject.dao.CompanyDAO;
import com.rands.couponproject.dao.CompanyDBDAO;
import com.rands.couponproject.dao.CouponDAO;
import com.rands.couponproject.dao.CouponDBDAO;
import com.rands.couponproject.dao.CustomerDAO;
import com.rands.couponproject.dao.CustomerDBDAO;
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
	

	public static CouponClientFacade login(String name, String password, ClientType clientType) throws Exception {
		String rightPassword = "1234";
		
		if (clientType != ClientType.ADMIN) {
			logger.error("admin login failed mismitch clientType = " + clientType  );
			throw new Exception("LoginFailed");
		}
		
		if (clientType == ClientType.ADMIN && "admin".equals(name) && rightPassword.equals(password))
		{
			return new AdminFacade();
		}
		
		logger.error("admin login failed name = " + name);
		throw new Exception("LoginFailed");		
	}
	
	public void createCompany(Company c)
	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		String name = c.getCompanyName();
		if (null == (companyDAO.getCompany(name)))
			companyDAO.createCompany(c);
		else
		{
			System.out.println("company " + c.getCompanyName() + " already exists in the DB");
		}
	}

	public void removeCompany(Company c)
	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		companyDAO.removeCompany(c); //*************************************** not yet finished
	}

	public void updateCompany(Company c)
	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		companyDAO.updateCompany(c);
	}

	public Company getCompany(long id)
	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		return companyDAO.getCompany(id);
	}

	public Collection<Company> getAllCompanies()
	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		return companyDAO.getAllCompanies();
	}

	public void createCustomer(Customer c)
	{
		CustomerDAO customerDAO = new CustomerDBDAO();
		String name = c.getCustomerName();
		if (null == customerDAO.getCustomer(name))
			customerDAO.createCustomer(c);
	}

	public void removeCustomer(Customer c)
	{
		CustomerDAO customerDAO = new CustomerDBDAO();
		customerDAO.removeCustomer(c); //*****************************************not yet finnished
	}

	public void updateCustomer(Customer c)
	{
		CustomerDAO customerDAO = new CustomerDBDAO();
		customerDAO.updateCustomer(c);
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

}

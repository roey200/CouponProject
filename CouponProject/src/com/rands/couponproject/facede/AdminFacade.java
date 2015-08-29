package com.rands.couponproject.facede;

import java.util.Collection;

import org.apache.log4j.Logger;

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

	private CompanyDAO companyDAO;
	private CustomerDAO customerDAO;
	private CouponDAO couponDAO;

	public static CouponClientFacade login(String name, String password, ClientType clientType) throws Exception {
		String rightPassword = "1234";
		
		if ("admin".equals(name) && rightPassword.equals(password) && clientType == ClientType.ADMIN)
		{
			return new AdminFacade();
		}
		
		logger.error("company login failed mismitch clientType =" +clientType  );
		throw new Exception("LoginFailed");
	}
	
	private AdminFacade() {
		companyDAO = new CompanyDBDAO();
		customerDAO = new CustomerDBDAO();
		couponDAO = new CouponDBDAO();
	};

	public void createCompany(Company c)
	{
		String name = c.getCompanyName();
		if (null == (companyDAO.getCompany(name)))
			this.companyDAO.createCompany(c);
		else
		{
			System.out.println("company " + c.getCompanyName() + " already exists in the DB");
		}
	}

	public void removeCompany(Company c)
	{

		companyDAO.removeCompany(c); //*************************************** not yet finished
	}

	public void updateCompany(Company c)
	{
		companyDAO.updateCompany(c);
	}

	public Company getCompany(long id)
	{
		return companyDAO.getCompany(id);
	}

	public Collection<Company> getAllCompanies()
	{
		return companyDAO.getAllCompanies();
	}

	public void createCustomer(Customer c)
	{
		String name = c.getCustomerName();
		if (null == customerDAO.getCustomer(name))
			customerDAO.createCustomer(c);
	}

	public void removeCustomer(Customer c)
	{
		customerDAO.removeCustomer(c); //*****************************************not yet finnished
	}

	public void updateCustomer(Customer c)
	{
		customerDAO.updateCustomer(c);
	}

	public Collection<Customer> getAllCustomers()
	{
		return customerDAO.getAllCustomers();
	}

	public Customer getCustomer(long id)
	{
		return customerDAO.getCustomer(id);
	}

}

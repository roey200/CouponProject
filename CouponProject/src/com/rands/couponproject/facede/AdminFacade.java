package com.rands.couponproject.facede;

import java.sql.Connection;
import java.sql.SQLException;
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
import com.rands.couponproject.model.Coupon;
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
	
	// Handling companies
	
	public void createCompany(Company c)
	{
		CompanyDAO companyDAO = new CompanyDBDAO();
		String name = c.getCompanyName();
		if (null == (companyDAO.getCompany(name))) {
			companyDAO.createCompany(c);
		} else {
			logger.error("createCompany ,Company " + name + " already exists");
		}
	}

	public void removeCompany(long companyId) throws Exception	{
		ConnectionPool pool;
		Connection conn;
		//long companyId = company.getId();
		
		try {
			pool = ConnectionPool.getInstance();
			conn = pool.getConnection();
		} catch (Exception e) {
			logger.error("removeCustomer failed to get a connection : " + e.toString());
			throw e;
		}

		CompanyDAO companyDAO = new CompanyDBDAO(conn); // use the same connection for the transaction
		CouponDAO couponDAO = new CouponDBDAO(conn);

		try {
			conn.setAutoCommit(false); // begin transaction
			Collection<Coupon> coupons = couponDAO.getCompanyCoupons(companyId);
			for (Coupon coupon:coupons) {
//				couponDAO.removeCoupon(coupon); // remove the coupon and the links
				couponDAO.removeCoupon(coupon.getId()); // remove the coupon and the links
			}
			
			//companyDAO.removeCompany(company); 
			companyDAO.removeCompany(companyId); 
			
			conn.commit(); // end the transaction
		} catch (Exception e) {
			logger.error("removeCompany failed : " + e.toString());
			try {
				conn.rollback(); // abort the transaction
			} catch (SQLException e1) {
				logger.error("removeCompany rollback failed : " + e.toString());
			}
			throw (e);
		} finally {
			pool.returnConnection(conn);
		}
	}
	
	public void removeCompany(Company company) throws Exception	{
		removeCompany(company.getId());
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
	
	// Handling customers	

	public void createCustomer(Customer c)
	{
		CustomerDAO customerDAO = new CustomerDBDAO();
		String name = c.getCustomerName();
		if (null == customerDAO.getCustomer(name)) {
			customerDAO.createCustomer(c);
		} else {
			logger.error("createCustomer ,Customer " + name + " already exists");
		}
	}

	public void removeCustomer(long customerId) throws Exception {
		ConnectionPool pool;
		Connection conn;
		//long customerId = customer.getId();
		
		try {
			pool = ConnectionPool.getInstance();
			conn = pool.getConnection();
		} catch (Exception e) {
			logger.error("removeCustomer failed to get a connection : " + e.toString());
			throw e;
		}

		CustomerDAO customerDAO = new CustomerDBDAO(conn);
		CouponDAO couponDAO = new CouponDBDAO(conn);

		try {
			conn.setAutoCommit(true); // begin transaction
			Collection<Coupon> coupons = couponDAO.getCustomerCoupons(customerId);
			for (Coupon coupon:coupons) {
				//couponDAO.removeCustomerCoupon(coupon); // remove the links
				couponDAO.removeCustomerCoupon(coupon.getId()); // remove the links
			}
			
			customerDAO.removeCustomer(customerId); 
			
			conn.commit(); // end the transaction
		} catch (Exception e) {
			logger.error("removeCustomer failed : " + e.toString());
			try {
				conn.rollback(); // abort the transaction
			} catch (SQLException e1) {
				logger.error("removeCustomer rollback failed : " + e.toString());
			}
			throw (e);
		} finally {
			pool.returnConnection(conn);
		}
	}
	
	public void removeCustomer(Customer customer) throws Exception {
		removeCustomer(customer.getId());
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
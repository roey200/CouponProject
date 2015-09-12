package com.rands.couponproject.tests;

import java.sql.Connection;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.rands.couponproject.ConnectionPool;
import com.rands.couponproject.CouponSystem;
import com.rands.couponproject.facede.AdminFacade;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Customer;
import com.rands.couponproject.utils.LogUtils;
import com.rands.couponproject.utils.Utils;

public class TestMain {
	static Logger logger = Logger.getLogger(TestMain.class);
	
	static CouponSystem couponSystem;
	static AdminFacade a;

	public static void main(String[] args) {
		//LogUtils.initLogger(); // use this if the log4j.properties file is not in your path
		
		logger.debug("starting tests");
		
		try {
			couponSystem = CouponSystem.getInstance();
//			a = (AdminFacade) couponSystem.login("admin", "1234", ClientType.ADMIN);
		} catch (Exception e) {
			logger.error("TestMain failed : " + e.toString());
			return;
		}
		
		Connection conn = null;
		try {
			conn = ConnectionPool.getInstance().getConnection();
			Utils.executeSqlScript(conn,"scrapbook_derby");
		} catch (Exception e) {
			logger.error("TestMain failed : " + e.toString());
			return;
		} finally {
			try {
				ConnectionPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
			}
		}

//		logger.info("creating companies");
//		createComapny ("cocacola");
//		createComapny ("google");
//		createComapny ("alphabet");
//		createComapny ("google");
//		createComapny ("amazon");
//		
//		PrintCompanies();
//		
//		//updateCompany (4);
//		
//		//PrintCompanies();
//		logger.info("creating customers");
//		createCustomer("arik");
//		createCustomer("bents");
//		createCustomer("moshe");
//		createCustomer("kermit");
//		createCustomer("miss piggie");
//		
//		printCustomers();


	}


	private static void PrintCompanies() {
		Collection<Company> companies = a.getAllCompanies();
		for(Company each :companies)
			System.out.println(each);
	}

	private static void updateCompany(long id) {
		Company c = a.getCompany(id);
		c.setPassword("comp9999");
		a.updateCompany(c);
	}

	private static void createComapny(String name) {
		
		Company c = new Company();
		c.setId(0);
		c.setCompanyName(name);
		c.setPassword("comp1234");
		c.setEmail(name + "@gmail.com");
		a.createCompany(c);
	}
	
	private static void printCustomers() {
		Collection<Customer> coustomers = a.getAllCustomers();
		for(Customer each :coustomers)
			System.out.println(each);
	}
	
	private static void createCustomer(String name) {
		Customer c = new Customer();
		c.setCustomerName(name);
		c.setId(0);
		c.setPassword("cust1234");
		a.createCustomer(c);
	}
	
	private static void updateCustomer(long id) {
		Customer c = a.getCustomer(id);
		c.setPassword("cust9999");
		a.updateCustomer(c);
	}
}

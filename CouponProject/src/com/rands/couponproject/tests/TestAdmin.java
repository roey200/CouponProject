package com.rands.couponproject.tests;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.rands.couponproject.facede.AdminFacade;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Customer;

public class TestAdmin {
	Logger logger = Logger.getLogger(TestAdmin.class);

	private AdminFacade admin;

	public TestAdmin(AdminFacade a) {

		admin = a;
	}

	public void Test() {

		logger.info("creating companies");
		createComapny("cocacola");
		createComapny("google");
		createComapny("alphabet");
		createComapny("google");
		createComapny("amazon");

		printCompanies();

		logger.info("updating companies");
		updateCompany("alphabet");
		updateCompany("mass");
		
		printCompanies();

		logger.info("removing companies");
		removeCompany("amazon");
		removeCompany("mass");
		
		printCompanies();

		//PrintCompanies();
		logger.info("creating customers");
		createCustomer("arik");
		createCustomer("bents");
		createCustomer("moshe");
		createCustomer("kermit");
		createCustomer("miss piggie");

		printCustomers();

		logger.info("updating customers");
		updateCustomer("moshe");
		updateCustomer("mass");
		
		printCustomers();
		
		logger.info("removing customers");
		removeCustomer("moshe");
		removeCustomer("moshe");

		printCustomers();
	}

	private void printCompanies() {
		Collection<Company> companies;
		try {
			companies = admin.getAllCompanies();
			for (Company each : companies)
				System.out.println(each);
		} catch (Exception e) {
			logger.error("printCompanies failed : " + e.toString());
		}
	}

	private void createComapny(String name) {

		Company c = new Company();
		c.setId(0);
		c.setCompanyName(name);
		String password = name.substring(0, 1) + "1234";
		c.setPassword(password);
		c.setEmail(name + "@gmail.com");
		admin.createCompany(c);
	}
	
	private void updateCompany(String companyName) {
		Company c = admin.getCompany(companyName);
		if (null == c){
			logger.error("company does not exist : " + companyName);
			return;
		}
		c.setEmail(c.getCompanyName() + "@yahoo.com");
		String password = c.getCompanyName().substring(0, 2) + "9999";
		c.setPassword(password);
		admin.updateCompany(c);
	}
	
	private void removeCompany(String companyName) {
		logger.info("removing company : " + companyName);
		Company c = admin.getCompany(companyName);
		try {
			admin.removeCompany(c.getId());
		} catch (Exception e) {
			logger.error("removeCompany " + companyName + " failed : " + e.toString());
		}
	}	

	private void printCustomers() {
		Collection<Customer> coustomers = admin.getAllCustomers();
		for (Customer each : coustomers)
			System.out.println(each);
	}

	private void createCustomer(String name) {
		Customer c = new Customer();
		c.setCustomerName(name);
		String password = name.substring(0, 1) + "1234"; //take the first char of name and add 1234 
		c.setPassword(password);
		admin.createCustomer(c);
	}

	private void updateCustomer(String customerName) {
		Customer c = admin.getCustomer(customerName);
		if (null == c){
			logger.error("Customer does not exist : " + customerName);
			return;
		}
		String password = c.getCustomerName().substring(0,2)+"9999";
		c.setPassword(password);
		admin.updateCustomer(c);
	}
	
	private void removeCustomer(String customerName) {
		logger.info("removing customer : " + customerName);

		Customer c = admin.getCustomer(customerName);
		try {
			admin.removeCustomer(c.getId());
		} catch (Exception e) {
			logger.error("removeCustomer " + customerName + " failed : " + e.toString());
		}
	}	
	
}

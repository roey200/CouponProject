package com.rands.couponproject.tests;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.rands.couponproject.ConnectionPool;
import com.rands.couponproject.CouponSystem;
import com.rands.couponproject.facede.AdminFacade;
import com.rands.couponproject.facede.CompanyFacade;
import com.rands.couponproject.facede.CustomerFacade;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Customer;
import com.rands.couponproject.utils.LogUtils;
import com.rands.couponproject.utils.Utils;

public class TestMain {
	static Logger logger = Logger.getLogger(TestMain.class);

	static CouponSystem couponSystem;
	static AdminFacade adminFacade;

	static boolean optCreateDatabase = true;

	public static void main(String[] args) {
		LogUtils.initLogger(); // set log4j configuration properties 
		
		
		printHeader("starting tests");
		
		try {
			ConnectionPool.getInstance();
		} catch (Exception e) {
			logger.error("ConnectionPool.getInstance() failed : " + e);
			System.out.println("The ConnectionPool was not initialized properly : " + e.toString());
			return;
		}
		
		try {
			couponSystem = CouponSystem.getInstance();
			couponSystem.setDailyTaskSleepTime(1 * Utils.MINUTE);
			
			if (optCreateDatabase) {
				createDataBase();
			}
			
			printHeader("Deleting all records");
			deleteAll(); // delete all records from the data base
			
			printHeader("Teseting the AdminFacade");

			adminFacade = (AdminFacade) couponSystem.login("admin", "1234", ClientType.ADMIN);
			TestAdmin testAdmin = new TestAdmin(adminFacade);
			testAdmin.test();
			
			printHeader("Teseting the CompanyFacade");
			CompanyFacade companyFacade;
			
			try {
				companyFacade = (CompanyFacade) couponSystem.login("RandS EveryThingGoes", "R1234", ClientType.COMPANY);
				logger.error("company " + "RandS EveryThingGoes" + " login with old password"); // this is wrong since we changed the password
			} catch (Exception e) {
				logger.info("company " + "RandS EveryThingGoes" + " login with old password failed"); // this is ok since we changed the password
			}

			companyFacade = (CompanyFacade) couponSystem.login("RandS EveryThingGoes", "Ra9999", ClientType.COMPANY);
			logger.info("company " + "RandS EveryThingGoes" + " login with new password is ok");
			TestCompany testCompany = new TestCompany(companyFacade);
			testCompany.test();
			
			printHeader("Teseting the CustomerFacade");
			CustomerFacade customerFacade;
			
			customerFacade = (CustomerFacade) couponSystem.login("roey", "r1234", ClientType.CUSTOMER);
			logger.info("customer " + "roey" + " login ok");
			TestCustomer testCustomer = new TestCustomer(customerFacade);
			testCustomer.test();
			
			printHeader("Teseting the DailyCouponExpirationTask");
			testCompany.test2(); // a test to expire a coupon 
			
			printHeader("Shutting down");
			System.out.println("Shutting down in 2 min, please wait");
			Thread.sleep(2 * Utils.MINUTE); // waiting two minutes to allow the daily task to do some work
			couponSystem.shutdown();
			
			printHeader("All done");

			
		} catch (Exception e) {
			logger.error("TestMain failed : " + e.toString());
			return;
		}
		
	}

	private static void printHeader(String text) {
		System.out.println("***********************************************************************");
		System.out.println(text);
		System.out.println("***********************************************************************");
	}

	/**
	 * createDataBase - creates the database tables from the scrapbook file
	 */
	private static void createDataBase() {
		System.out.println("Creating the database");
		Connection conn = null;
		try {
			conn = ConnectionPool.getInstance().getConnection();
			Utils.executeSqlScript(conn, "scrapbook.sql");
		} catch (Exception e) {
			logger.error("createDataBase failed : " + e.toString());
			return;
		} finally {
			try {
				ConnectionPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
			}
		}
		System.out.println("Creating the database done");

	}
	
	/**
	 * deleteAll - deletes all db records so we can start with empty tables.
	 */
	private static void deleteAll() {
		System.out.println("Emptying the database");

		Connection conn = null;
		try {
			conn = ConnectionPool.getInstance().getConnection();
			Utils.executeSqlCommand(conn,"DELETE from APP.customer_coupon");
			Utils.executeSqlCommand(conn,"DELETE from APP.company_coupon");
			Utils.executeSqlCommand(conn,"DELETE from APP.coupon");
			Utils.executeSqlCommand(conn,"DELETE from APP.company");
			Utils.executeSqlCommand(conn,"DELETE from APP.customer");
		} catch (Exception e) {
			logger.error("deleteAll failed : " + e.toString());
			return;
		} finally {
			try {
				ConnectionPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
			}
		}
		System.out.println("Emptying the database done");		
	}


}

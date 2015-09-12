package com.rands.couponproject.tests;

import java.sql.Connection;
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

	public static void main(String[] args) {
		//LogUtils.initLogger(); // use this if the log4j.properties file is not in your path
		
		boolean optCreateDatabase = true;
		
		logger.debug("starting tests");
		
		try {
			couponSystem = CouponSystem.getInstance();
			
			if (optCreateDatabase) {
				createDataBase();
			}
			
			adminFacade = (AdminFacade) couponSystem.login("admin", "1234", ClientType.ADMIN);
			TestAdmin testAdmin = new TestAdmin(adminFacade);
			testAdmin.Test();
			
			CompanyFacade companyFacade;
			
			try {
				companyFacade = (CompanyFacade) couponSystem.login("RandS BurgersBar", "R1234", ClientType.COMPANY);
				logger.error("company " + "RandS BurgersBar" + " login with old password"); // this is wrong since we changed the password
			} catch (Exception e) {
				logger.info("company " + "RandS BurgersBar" + " login with old password failed"); // this is ok since we changed the password
			}

			companyFacade = (CompanyFacade) couponSystem.login("RandS BurgersBar", "Ra9999", ClientType.COMPANY);
			logger.info("company " + "RandS BurgersBar" + " login with new password is ok");
			TestCompany testCompany = new TestCompany(companyFacade);
			testCompany.Test();
			
			CustomerFacade customerFacade;
			
			customerFacade = (CustomerFacade) couponSystem.login("roey", "r1234", ClientType.CUSTOMER);
			logger.info("customer " + "roey" + " login ok");
			TestCustomer testCustomer = new TestCustomer(customerFacade);
			testCustomer.Test();
			
			
			
			
			
			
		} catch (Exception e) {
			logger.error("TestMain failed : " + e.toString());
			return;
		}
		
	}

	/**
	 * createDataBase - creates the database tables from the scrapbook file
	 */
	private static void createDataBase() {
		logger.info("Creating the database");
		Connection conn = null;
		try {
			conn = ConnectionPool.getInstance().getConnection();
			Utils.executeSqlScript(conn, "scrapbook_derby");
		} catch (Exception e) {
			logger.error("TestMain failed : " + e.toString());
			return;
		} finally {
			try {
				ConnectionPool.getInstance().returnConnection(conn);
			} catch (Exception e) {
			}
		}
		logger.info("Creating the database done");

	}

}

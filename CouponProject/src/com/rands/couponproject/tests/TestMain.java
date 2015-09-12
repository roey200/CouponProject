package com.rands.couponproject.tests;

import java.sql.Connection;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.rands.couponproject.ConnectionPool;
import com.rands.couponproject.CouponSystem;
import com.rands.couponproject.facede.AdminFacade;
import com.rands.couponproject.facede.CompanyFacade;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Customer;
import com.rands.couponproject.utils.LogUtils;
import com.rands.couponproject.utils.Utils;

public class TestMain {
	static Logger logger = Logger.getLogger(TestMain.class);

	static CouponSystem couponSystem;
	static AdminFacade admin;

	public static void main(String[] args) {
		//LogUtils.initLogger(); // use this if the log4j.properties file is not in your path
		
		boolean optCreateDatabase = true;
		
		logger.debug("starting tests");
		
		try {
			couponSystem = CouponSystem.getInstance();
			
			if (optCreateDatabase) {
				createDataBase();
			}
			
			admin = (AdminFacade) couponSystem.login("admin", "1234", ClientType.ADMIN);
			TestAdmin testAdmin = new TestAdmin(admin);
			testAdmin.Test();
			
			CompanyFacade company;
			
			try {
				company = (CompanyFacade) couponSystem.login("alphabet", "a1234", ClientType.COMPANY);
				logger.error("company " + "alphabet" + " login with old password"); // this is wrong since we changed the password
			} catch (Exception e) {
				logger.info("company " + "alphabet" + " login with old password failed"); // this is ok since we changed the password
			}

			company = (CompanyFacade) couponSystem.login("alphabet", "al9999", ClientType.COMPANY);
			logger.error("company " + "alphabet" + " login with new password is ok");
			TestCompany testCompany = new TestCompany(company);
			testCompany.Test();
			
			
			
			
			
			
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

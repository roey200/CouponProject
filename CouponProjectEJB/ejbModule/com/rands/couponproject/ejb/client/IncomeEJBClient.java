package com.rands.couponproject.ejb.client;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.rands.couponproject.ejb.IncomeServiceBean;

public class IncomeEJBClient {

	static Logger logger = Logger.getLogger(IncomeEJBClient.class);


	public static IncomeServiceBean lookup() {
		logger.info("lookup");

		final String EJB_LOOKUP = "java:global/CouponProjectEAR/CouponProjectEJB/IncomeServiceBean";
		IncomeServiceBean isb = null;
		try {
			javax.naming.Context context = new InitialContext();
			Object o = context.lookup(EJB_LOOKUP);
			
			isb = (IncomeServiceBean)o;
			return isb;
			
		} catch (Exception e) {
			logger.error("lookup : " + e.toString());
		}
		return isb;
	}
}
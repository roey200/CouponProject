package com.rands.couponproject.rest;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.rands.couponproject.CouponSystem;

public class CouponpPojectContextListener implements ServletContextListener
{
	static Logger logger = Logger.getLogger(CouponpPojectContextListener.class);
	public static CouponSystem couponSystem;

	public void contextInitialized(ServletContextEvent arg0)
	{
		logger.info("CouponpPojectContextListener contextInitialized");
		try {
			logger.info("starting the CouponSystem");
			couponSystem = CouponSystem.getInstance();
		} catch (Exception e) {
			logger.error("CouponSystem.getInstance() failed : " + e.toString());
		}
		 
	}

	public void contextDestroyed(ServletContextEvent arg0)
	{
		logger.info("CouponpPojectContextListener contextDestroyed");
		
		if (null != couponSystem) {
			logger.info("terminating the CouponSystem");
			couponSystem.shutdown();
		} else {
			logger.info("CouponSystem was not initialized");

		}
	}

}
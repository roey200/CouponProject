package com.rands.couponproject.rest;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.rands.couponproject.CouponSystem;

/**
 * 
 * CouponpPojectContextListener : starts and terminates the CouponSystem.
 * 
 * It is impotent to terminate the CouponSystem by calling it's shutdown() method so that it will release it's resources.
 * the CouponSystem uses a ConnectionPool that is not managed by the Web Container. The Connections should be closed and
 * the DriverManager should be deregistered. Otherwise whenever this Web application will be redeployed  memory may leak. 
 */
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
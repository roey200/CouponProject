package com.rands.couponproject.rest;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.rands.couponproject.CouponSystem;
import com.rands.couponproject.utils.Utils;

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

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent)
	{
		logger.info("CouponpPojectContextListener contextInitialized");
		
		try {
			logger.info("starting the CouponSystem");
			couponSystem = CouponSystem.getInstance();
			
			long sleep = getLongParam(servletContextEvent, "dailytask.sleep");
			if (sleep > 0) 
				couponSystem.setDailyTaskSleepTime(sleep * Utils.MINUTE);
			
//			boolean createDb = getBooleanParam(servletContextEvent, "createDb");
//			if (createDb)
//				couponSystem.createDB();
			
		} catch (Exception e) {
			logger.error("CouponSystem.getInstance() failed : " + e.toString());
		}
		 
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		logger.info("CouponpPojectContextListener contextDestroyed");
		
		if (null != couponSystem) {
			logger.info("terminating the CouponSystem");
			couponSystem.shutdown();
		} else {
			logger.info("CouponSystem was not initialized");

		}
	}

	// helper functions to retrieve <context-param> parameters
	
	private String getContextParam(ServletContextEvent servletContextEvent,String paramName) {
		ServletContext ctx = servletContextEvent.getServletContext();
        return ctx.getInitParameter(paramName);
    }
	
	private int getIntParam(ServletContextEvent servletContextEvent,String paramName) {
		try {
			return Integer.parseInt(getContextParam(servletContextEvent,paramName));
		} catch (Exception e) {
			return 0;
		}
	}

	private long getLongParam(ServletContextEvent servletContextEvent,String paramName) {
		try {
			return Long.parseLong(getContextParam(servletContextEvent,paramName));
		} catch (Exception e) {
			return 0;
		}
	}

	private boolean getBooleanParam(ServletContextEvent servletContextEvent,String paramName) {
		try {
			return Boolean.parseBoolean(getContextParam(servletContextEvent,paramName));
		} catch (Exception e) {
			return false;
		}
	}

}
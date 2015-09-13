package com.rands.couponproject;

//import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.rands.couponproject.dao.CouponDAO;
import com.rands.couponproject.dao.CouponDBDAO;
import com.rands.couponproject.model.Coupon;

public class DailyCouponExpirationTask extends Thread {

	static Logger logger = Logger.getLogger(DailyCouponExpirationTask.class);
	private boolean quit = false;

	private static final long second = 1000;
	private static final long minute = 60 * second;
	private static final long houer = 60 * minute;

	@Override
	public void run() {
		logger.info("DailyCouponExpirationTask starting");

		CouponDAO couponDAO = new CouponDBDAO();
		Collection<Coupon> coupons;

		while (!quit) {
			try {
				coupons = couponDAO.getAllCoupons();
				long millis = System.currentTimeMillis();
				Date currentDate = new Date(millis);
				int nRemoved = 0;
				for (Coupon coupon : coupons) {
					if (coupon.getEndDate().before(currentDate)) {
						try {
							couponDAO.removeCoupon(coupon);
							nRemoved++;
						} catch (Exception e) {
							logger.error("remove expired coupon failed");
						}
					}
				}
				logger.info("DailyCouponExpirationTask removed " + nRemoved + " expierd coupons");
			} catch (SQLException e) {
				logger.error("DailyCouponExpirationTask run failed : " + e.toString());
			}

			try {
				//sleep(15 * minute);
				sleep(1 * minute);
			} catch (InterruptedException e) {
				logger.error("DailyCouponExpirationTask interrupted");
			}
		}
		logger.info("DailyCouponExpirationTask finished");
	}

	public void stopTask() {
		if (!quit) {
			logger.error("Stopping the DailyCouponExpirationTask");
			quit = true;
		} else {
			logger.error("DailyCouponExpirationTask already stopped");
		}
	}

}

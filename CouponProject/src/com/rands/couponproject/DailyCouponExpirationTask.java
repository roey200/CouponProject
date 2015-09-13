package com.rands.couponproject;

//import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.rands.couponproject.dao.CouponDAO;
import com.rands.couponproject.dao.CouponDBDAO;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.utils.Utils;

public class DailyCouponExpirationTask extends Thread {

	static Logger logger = Logger.getLogger(DailyCouponExpirationTask.class);
	private boolean quit = false;

	

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
				//sleep(24 * Utils.houer);
				//sleep(15 * Utils.minute);
				sleep(1 * Utils.minute); // 1 minute so that we can see the work in testMain
			} catch (InterruptedException e) {
				logger.error("DailyCouponExpirationTask interrupted");
			}
		}
		logger.info("DailyCouponExpirationTask finished");
	}

	public void stopTask() {
		if (!quit) {
			logger.info("Stopping the DailyCouponExpirationTask");
			quit = true;
		} else {
			logger.info("DailyCouponExpirationTask already stopped");
		}
	}

}

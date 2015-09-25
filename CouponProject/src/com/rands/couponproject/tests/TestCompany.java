package com.rands.couponproject.tests;

import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.rands.couponproject.facede.CompanyFacade;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;
import com.rands.couponproject.utils.Utils;

public class TestCompany {

	Logger logger = Logger.getLogger(TestCompany.class);

	CompanyFacade compFacade;

	public TestCompany(CompanyFacade compFacade) {

		this.compFacade = compFacade;

	}

	public void test() {
		printCompany();

		long couponId1;
		long couponId2;

		System.out.println("creating coupons");

		createCoupon("HienikenBeer", "2016-01-01 17:00", "2020-09-20", 3, CouponType.FOOD, "All You Can Drink Hieniken Beer", 35);
		createCoupon("BurgerUpgrade", "2015-09-12 16:30", "2016-10-24 22:00", 40, CouponType.FOOD, "50% off ", 25);
		couponId1 = createCoupon("DessertSpeacial", "14/09/2015", "2018-09-20 2:00", 10, CouponType.FOOD, "Banna Split Discount", 10);
		couponId2 = createCoupon("wineBottle", "2015-07-10 22:00 16:30", "2016-12-20 04:30 23:45", 99, CouponType.FOOD, "1+1 wine bottels", 50);
		createCoupon("wineBottle", "2015-10-10", "2015-12-31", 40, CouponType.FOOD, "1+1 wine bottels", 10); //should fial

		createCoupon("ExtreamJava", "2015-01-01", "2020-12-31", 1, CouponType.CAMPING, "1+1 flight and camping to Java islend", 1000);
		createCoupon("ExtreamC#", "2015-01-01", "2025-01-31", 0, CouponType.TECH, "1+1 .NET Course", 1200); // note amount = 0
		createCoupon("ExtreamUnix", "1970-01-02", "2032-12-31", 10000, CouponType.TECH, "100% discount for FreeBSD", 500);

		printCompany();

		System.out.println("removing coupons");

		removeCoupon(couponId1);
		removeCoupon(couponId1); //this should fail

		printCompany();

		System.out.println("updating coupons");

		updateCoupon(couponId2);
		printCompany();

		printCouponsByType(CouponType.FOOD);
		printCouponsByType(CouponType.ELECTRICITY); // supposed to be empty
		printCouponsByType(CouponType.TECH);
		printCouponsByPrice(30);
		printCouponByDate("2015-11-01");
		
	}
	
	public void test2() {
		long couponId;
		
		couponId = createCoupon("Phantom", "2/1/1970", "2032-12-31", 1, CouponType.TECH, "This Coupon should expire!", 100);
		expireCoupon(couponId);
		
	}

	private void printCoupons(Collection<Coupon> coupons, String prefix) {
		for (Coupon coupon : coupons) {
			System.out.println(prefix + coupon);
		}
	}

	private void printCoupons(Collection<Coupon> coupons) {
		printCoupons(coupons, ""); // empty prefix
	}

	private void printCouponByDate(String toDate) {
		try {
			Collection<Coupon> coupon = compFacade.getCouponsByDate(Utils.string2Date(toDate));
			System.out.println("coupons ending before " + toDate);
			printCoupons(coupon);
		} catch (Exception e) {
			logger.error("printCouponByDate  failed : " + e.toString());

		}
	}

	private void printCouponsByPrice(long price) {
		try {
			Collection<Coupon> coupon = compFacade.getCouponsByPrice(price);
			System.out.println("coupons cheaper then " + price);
			printCoupons(coupon);
		} catch (Exception e) {
			logger.error("printCouponsByPrice  failed : " + e.toString());

		}
	}

	private void printCouponsByType(CouponType type) {
		try {
			Collection<Coupon> coupon = compFacade.getCouponsByType(type);
			System.out.println("coupons of type " + type);
			printCoupons(coupon);
		} catch (Exception e) {
			logger.error("printCouponsByType  failed : " + e.toString());

		}

	}

	private void printCompany() {
		try {
			Company company = compFacade.getCompany(); // get the company that is associated with the facade
			System.out.println(company + " num coupons = " + company.getCoupons().size());

			printCoupons(company.getCoupons(), "\t");
		} catch (Exception e) {
			logger.error("printCompany failed : " + e.toString());
		}
	}

	private long createCoupon(String title, String startDate, String endDate, int amount, CouponType type, String massage, double price) {

		System.out.println("creating coupon : " + title);

		Coupon coupon = new Coupon();
		try {
			coupon.setStartDate(Utils.string2Date(startDate));
			coupon.setEndDate(Utils.string2Date(endDate));

			coupon.setTitle(title);
			coupon.setAmount(amount);
			coupon.setPrice(price);
			coupon.setType(type);
			coupon.setMassage(massage);
			String image = title + ".img";
			coupon.setImage(image);

			compFacade.createCoupon(coupon);
			return coupon.getId();

		} catch (Exception e) {
			logger.error("create coupons " + title + "  failed : " + e.toString());
			return 0;
		}

	}

	private void updateCoupon(long couponId) {
		System.out.println("updating coupon with id : " + couponId);

		try {
			Coupon coupon = compFacade.getCoupon(couponId);
			coupon.setAmount(100);
			coupon.setPrice(coupon.getPrice() / 2);
			compFacade.updateCoupon(coupon);
		} catch (Exception e) {
			logger.error("updateCoupon failed : " + e.toString());
		}
	}

	private void removeCoupon(long couponId) {
		System.out.println("removing coupon with id : " + couponId);
		try {
			compFacade.removeCoupon(couponId);
		} catch (Exception e) {
			logger.error("removeCoupon failed : " + e.toString());
		}

	}
	
	private void expireCoupon(long couponId) {
		System.out.println("expiering coupon with id : " + couponId);

		try {
			Coupon coupon = compFacade.getCoupon(couponId);
			coupon.setEndDate(new Date()); // the DailyCouponExpirationTask will delete this coupon 
			compFacade.updateCoupon(coupon);
		} catch (Exception e) {
			logger.error("expireCoupon failed : " + e.toString());
		}
	}

}

package com.rands.couponproject.tests;

import java.util.Collection;

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

	public void Test() {
		printCompany();

		long couponId1;
		long couponId2;

		System.out.println("creating coupons");

		createCoupon("HienikenBeer", "2015-09-14 17:00", "2015-09-20", 3, CouponType.FOOD, "All You Can Drink Hieniken Beer", 35);
		createCoupon("BurgerUpgrade", "2015-09-12 16:30", "2015-10-24 22:00", 40, CouponType.FOOD, "50% off ", 25);
		couponId1 = createCoupon("DessertSpeacial", "14/09/2015", "2018-09-20 2:00", 10, CouponType.FOOD, "Banna Split Discount", 10);
		couponId2 = createCoupon("wineBottle", "2015-07-10 22:00 16:30", "2015-12-20 04:30 23:45", 99, CouponType.FOOD, "1+1 wine bottels", 50);
		createCoupon("wineBottle", "2015-10-10", "2015-10-20", 40, CouponType.FOOD, "1+1 wine bottels", 10); //should fial

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
		printCouponsByPrice(30);
		printCouponByDate("2015-11-01");

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
			Collection<Coupon> coupon = compFacade.getCouponByType(type);
			System.out.println("coupons of type " + type);
			printCoupons(coupon);
		} catch (Exception e) {
			logger.error("printCouponsByType  failed : " + e.toString());

		}

	}

	private void printCompany() {
		Company company = compFacade.getCompany(); // get the company that is associated with the facade
		System.out.println(company + " num coupons = " + company.getCoupons().size());

		printCoupons(company.getCoupons(), "\t");
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

		Coupon coupon = compFacade.getCoupon(couponId);
		coupon.setAmount(100);
		coupon.setPrice(coupon.getPrice() / 2);
		compFacade.updateCoupon(coupon);
	}

	private void removeCoupon(long couponId) {
		System.out.println("removing coupon with id : " + couponId);
		try {
			compFacade.removeCoupon(couponId);
		} catch (Exception e) {
			logger.error("removeCoupon failed : " + e.toString());
		}

	}

}

package com.rands.couponproject.tests;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.rands.couponproject.facede.CustomerFacade;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;
import com.rands.couponproject.model.Customer;

public class TestCustomer {

	Logger logger = Logger.getLogger(TestCustomer.class);

	CustomerFacade customerFacade;

	public TestCustomer(CustomerFacade customerFacade) {

		this.customerFacade = customerFacade;

	}

	public void test() {
		printCustomer();
		
		purchaseCoupon("HienikenBeer");
		purchaseCoupon("BurgerUpgrade");
		purchaseCoupon("wineBottle");
		purchaseCoupon("wineBottle");
		
		purchaseCoupon("ExtreamJava");
		purchaseCoupon("ExtreamC#");
		purchaseCoupon("ExtreamUnix");
		purchaseCoupon("ExtreamWindows"); // no such coupon
		
		printCustomer();
		
		printCouponsByType(CouponType.FOOD);
		printCouponsByType(CouponType.CAMPING);
		printCouponsByType(CouponType.TECH);
		printCouponsByType(CouponType.ELECTRICITY); // will not print anything

		printCouponsByPrice(50);
		printCouponsByPrice(2000);
	}
	
	private void purchaseCoupon(String couponTitle) {
		System.out.println("purchasing : " + couponTitle);
		try {
			Collection<Coupon> coupons = customerFacade.getAllPurchasableCoupons();
			for (Coupon coupon : coupons) {
				if (coupon.getTitle().equals(couponTitle)) {
					customerFacade.purchaseCoupon(coupon);
					return;
				}

			} 
			// did not find coupon with that title in the list of purchable coupons
			logger.error("cannot purchase coupon : " + couponTitle);

		} catch (Exception e) {
			logger.error("purchaseCoupon " + couponTitle + " failed : " + e.toString());

		}

	}


	private void printCustomer() {
		Customer customer = customerFacade.getCustomer(); // get the customer that is associated with the facade
		System.out.println(customer + " num coupons = " + customer.getCoupons().size());

		printCoupons(customer.getCoupons(), "\t");
	}

	private void printCoupons(Collection<Coupon> coupons, String prefix) {
		for (Coupon coupon : coupons) {
			System.out.println(prefix + coupon);
		}
	}

	private void printCoupons(Collection<Coupon> coupons) {
		printCoupons(coupons, ""); // empty prefix

	}
	

	private Collection<Coupon> getCouponByType(CouponType type) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();
		Customer customer = customerFacade.getCustomer(); // get the customer that is associated with the facade
		for (Coupon coupon : customer.getCoupons()) {
			if (coupon.getType() == type)
				coupons.add(coupon);
		}

		return coupons;
	}

	private Collection<Coupon> getCouponsByPrice(long price) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();

		Customer customer = customerFacade.getCustomer(); // get the customer that is associated with the facade
		for (Coupon coupon : customer.getCoupons()) {
			if (coupon.getPrice() <= price)
				coupons.add(coupon);
		}

		return coupons;
	}
	

	private void printCouponsByPrice(long price) {
		try {
			Collection<Coupon> coupon = getCouponsByPrice(price);
			System.out.println("coupons cheaper then " + price);
			printCoupons(coupon);
		} catch (Exception e) {
			logger.error("printCouponsByPrice  failed : " + e.toString());

		}
	}

	private void printCouponsByType(CouponType type) {
		try {
			Collection<Coupon> coupon = getCouponByType(type);
			System.out.println("coupons of type " + type);
			printCoupons(coupon);
		} catch (Exception e) {
			logger.error("printCouponsByType  failed : " + e.toString());

		}

	}

	
	
}
package com.rands.couponproject.tests;

import java.sql.SQLException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.rands.couponproject.facede.CustomerFacade;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.Customer;

public class TestCustomer {

	Logger logger = Logger.getLogger(TestCustomer.class);

	CustomerFacade customerFacade;

	public TestCustomer(CustomerFacade customerFacade) {

		this.customerFacade = customerFacade;

	}

	public void Test() {
		printCustomer();
		
		purchaseCoupon("HienikenBeer");
		purchaseCoupon("BurgerUpgrade");
		purchaseCoupon("wineBottle");
		purchaseCoupon("wineBottle");
		
		printCustomer();

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
		Customer customer = customerFacade.getCustomer(); // get the company that is associated with the facade
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
}
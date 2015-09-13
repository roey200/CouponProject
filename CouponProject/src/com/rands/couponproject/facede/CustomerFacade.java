package com.rands.couponproject.facede;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.rands.couponproject.dao.CompanyDAO;
import com.rands.couponproject.dao.CompanyDBDAO;
import com.rands.couponproject.dao.CouponDAO;
import com.rands.couponproject.dao.CouponDBDAO;
import com.rands.couponproject.dao.CustomerDAO;
import com.rands.couponproject.dao.CustomerDBDAO;
import com.rands.couponproject.exceptions.CouponException;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;
import com.rands.couponproject.model.Customer;

public class CustomerFacade implements CouponClientFacade {
	static Logger logger = Logger.getLogger(CustomerFacade.class);

	private long customerId;

	private CustomerFacade() {
	}

	public static CouponClientFacade login(String name, String password, ClientType clientType) throws Exception {

		if (clientType != ClientType.CUSTOMER) {
			logger.error("company login failed mismitch clientType =" + clientType);
			throw new Exception("LoginFailed");
		}

		CustomerDAO customerDAO = new CustomerDBDAO();
		if (!customerDAO.login(name, password)) {
			logger.error("customer login failed ,customer " + name);
			throw new Exception("LoginFailed");
		}

		CustomerFacade facade = new CustomerFacade();
		Customer customer = customerDAO.getCustomer(name);
		facade.customerId = customer.getId();
		return facade;
	}

	private Customer getLogedinCustomer() throws Exception {
		CustomerDAO customerDAO = new CustomerDBDAO();
		Customer customer = customerDAO.getCustomer(customerId);
		if (null == customer) {
			logger.error("getLogedinCustomer customer does not exist any more");
			throw new Exception("getLogedinCustomer customer does not exist any more");
		}

		CouponDAO couponDAO = new CouponDBDAO();
		Collection<Coupon> coupons = couponDAO.getCustomerCoupons(customerId);
		customer.setCoupons(coupons);

		return customer;
	}

	public void purchaseCoupon(Coupon coupon) throws Exception {

		Customer customer = getLogedinCustomer();
		Collection<Coupon> coupons = customer.getCoupons();
		if (coupons.contains(coupon)) {
			throw new CouponException("customer already owns coupon " + coupon.toString());
		}

		if (coupon.getAmount() == 0) {
			throw new CouponException("coupon sold out " + coupon.toString());
		}

		Date currentDate = new Date();
		if (currentDate.before(coupon.getStartDate())) {
			throw new CouponException("not availabe yet " + coupon.toString());
		}
		if (currentDate.after(coupon.getEndDate())) {
			throw new CouponException("not availabe anymore " + coupon.toString());

		}

		CouponDAO couponDAO = new CouponDBDAO();
		couponDAO.createCustomerCoupon(customerId, coupon.getId());
	}
	
	/**
	 * purchaseCoupon - a helper function  
	 * @param couponTitle the title of the coupon to be purchased
	 * @throws Exception 
	 */
	public void purchaseCoupon(String couponTitle) throws Exception {
		CouponDAO couponDAO = new CouponDBDAO();
		for (Coupon coupon : couponDAO.getAllCoupons()) { // find a coupon with the requested title
			if (coupon.getTitle().equals(couponTitle)) {
				purchaseCoupon(coupon);
				return;
			}
		}
		throw new Exception("Coupon with title " + couponTitle + " dose not exist");
	}

	public Collection<Coupon> getAllPurchasedCoupons() throws Exception {
		Customer customer = getLogedinCustomer();
		return customer.getCoupons();

	}

	public Collection<Coupon> getAllPurchasedCouponsByType(CouponType type) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();

		for (Coupon coupon : getAllPurchasedCoupons()) {
			if (coupon.getType() == type)
				coupons.add(coupon);
		}

		return coupons;
	}

	public Collection<Coupon> getAllPurchasedCouponsByPrice(long price) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();

		for (Coupon coupon : getAllPurchasedCoupons()) {
			if (coupon.getPrice() <= price)
				coupons.add(coupon);
		}

		return coupons;
	}

//	public Collection<Coupon> getAllPurchasableCoupons() throws SQLException {
//
//		CouponDAO couponDAO = new CouponDBDAO();
//		return couponDAO.getAllPurchasableCoupons();
//	}
//
//	public Collection<Coupon> getAllPurchasableCouponsByType(CouponType couponType) throws SQLException {
//
//		CouponDAO couponDAO = new CouponDBDAO();
//		return couponDAO.getAllPurchasableCouponsByType(couponType);
//	}

	/**
	 * 
	 * @return the currently logedin Customer 
	 */
	public Customer getCustomer() {

		CustomerDAO customerDAO = new CustomerDBDAO();
		return customerDAO.getCustomer(customerId);
	}

	private boolean customerHasCoupon(Coupon coupon) throws Exception {
		for (Coupon cup : getAllPurchasedCoupons()) { // check all coupons of the current (logedin) customer 
			if (cup.getTitle().equals(coupon.getTitle()))
				return true;
		}
		return false;
	}

}

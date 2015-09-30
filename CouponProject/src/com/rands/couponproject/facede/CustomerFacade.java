package com.rands.couponproject.facede;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.rands.couponproject.dao.CouponDAO;
import com.rands.couponproject.dao.CouponDBDAO;
import com.rands.couponproject.dao.CustomerDAO;
import com.rands.couponproject.dao.CustomerDBDAO;
import com.rands.couponproject.exceptions.CouponProjectException.CouponException;
import com.rands.couponproject.exceptions.CouponProjectException.CustomerException;
import com.rands.couponproject.exceptions.CouponProjectException.CustomerLoginException;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;
import com.rands.couponproject.model.Customer;


/**
 * CustomerFacade - The CustomerFacade operates on behalf of a specific Customer (the logedin Customer). <br>
 * 					A CustomerFacade object can only be acquired throw the login method.
  */
public class CustomerFacade implements CouponClientFacade {
	static Logger logger = Logger.getLogger(CustomerFacade.class);

	private long customerId;

	private CustomerFacade() {
	}

	public static CouponClientFacade login(String name, String password, ClientType clientType) throws LoginException {

		if (clientType != ClientType.CUSTOMER) {
			logger.error("customer login failed mismitch clientType =" + clientType);
			throw new CustomerLoginException("Invalid ClientType : " + clientType);
		}

		CustomerDAO customerDAO = new CustomerDBDAO();
		if (!customerDAO.login(name, password)) {
			logger.error("customer login failed name = " + name);
			throw new CustomerLoginException(name);							
		}

		CustomerFacade facade = new CustomerFacade();
		Customer customer = customerDAO.getCustomer(name);
		facade.customerId = customer.getId();
		return facade;
	}

	/**
	 * 
	 * @return the currently logedin Customer 
	 * @throws Exception 
	 */
	public Customer getCustomer() throws Exception {

		CustomerDAO customerDAO = new CustomerDBDAO();
		Customer customer = customerDAO.getCustomer(customerId);
		if (null == customer) {
			logger.error("getCompany customer does not exist any more : id = " + customerId);
			throw new CustomerException("getCompany customer does not exist any more : id = " + customerId);			
		}		

		return customer;
	}	

	public void purchaseCoupon(Coupon coupon) throws Exception {

		Customer customer = getCustomer();
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
		coupon.setAmount(coupon.getAmount() - 1);
		couponDAO.updateCoupon(coupon);
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
		throw new CouponException("Coupon with title " + couponTitle + " dose not exist");
	}

	/**
	 * 
	 * @return the coupons that were purchased by the logedin customer
	 * @throws Exception
	 */
	public Collection<Coupon> getAllPurchasedCoupons() throws Exception {
		Customer customer = getCustomer();
		return customer.getCoupons();
	}

	/**
	 * 
	 * @param type
	 * @return the coupons that were purchased by the logedin customer limited by type
	 * @throws Exception
	 */
	
	public Collection<Coupon> getAllPurchasedCouponsByType(CouponType type) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();

		for (Coupon coupon : getAllPurchasedCoupons()) {
			if (coupon.getType() == type)
				coupons.add(coupon);
		}
		return coupons;
	}

	/**
	 * 
	 * @param price
	 * @return the coupons that were purchased by the logedin customer limited by price 
	 * @throws Exception
	 */
	
	public Collection<Coupon> getAllPurchasedCouponsByPrice(long price) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();

		for (Coupon coupon : getAllPurchasedCoupons()) {
			if (coupon.getPrice() <= price)
				coupons.add(coupon);
		}

		return coupons;
	}

	/**
	 * 
	 * @return all coupons that the customer can buy
	 * @throws Exception
	 */
	public Collection<Coupon> getPurchableCoupons() throws Exception {
		CouponDAO couponDAO = new CouponDBDAO();
		Collection<Coupon> allCoupons= couponDAO.getAllCoupons();
		Collection<Coupon> purchableCoupons = new ArrayList<Coupon>();
		Collection<Coupon> purchasedCoupons = getAllPurchasedCoupons(); //the coupons that the customer already owns

		//Date currentDate = new Date();
		for(Coupon coupon : allCoupons){
			if(coupon.getAmount()<1) 
				continue;
			//if(coupon.getEndDate().before(currentDate))
			if(coupon.isExpired())
				continue;
			if(purchasedCoupons.contains(coupon))   //contains uses the equals method of Coupon class that we wrote
				continue;
			
			purchableCoupons.add(coupon);
		}
		
		
		return purchableCoupons;
	}
}

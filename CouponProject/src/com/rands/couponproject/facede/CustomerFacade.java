package com.rands.couponproject.facede;

import java.util.Date;

import org.apache.log4j.Logger;

import com.rands.couponproject.dao.CouponDAO;
import com.rands.couponproject.dao.CouponDBDAO;
import com.rands.couponproject.dao.CustomerDAO;
import com.rands.couponproject.dao.CustomerDBDAO;
import com.rands.couponproject.exceptions.CouponException;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.Customer;

public class CustomerFacade implements CouponClientFacade {
	static Logger logger = Logger.getLogger(CustomerFacade.class);

	private CustomerDAO customerDAO;
	private CouponDAO couponDAO;

	public static CouponClientFacade login(String name, String password,ClientType clientType) throws Exception {
		
		if (clientType != ClientType.CUSTOMER){
			logger.error("company login failed mismitch clientType =" + clientType );
			throw new Exception("LoginFailed");
		}
			
		CustomerFacade facade = new CustomerFacade();
		
		Customer customer = facade.customerDAO.getCustomer(name);
		if (null == customer) {
			logger.error("customer login failed ,customer " + name + " dose not exist");
			throw new Exception("LoginFailed");
		}
				
		if (customer.getPassword().equals(password))
			return facade;
		
		logger.error("customer login failed ,customer " + name + " password mismatch");
		throw new Exception("LoginFailed");
	}

	private CustomerFacade() {

		customerDAO = new CustomerDBDAO();
		couponDAO = new CouponDBDAO();

	}

	public void purchaseCoupon(Coupon coupon) throws Exception {

		if (coupon.getAmount() == 0) {
			throw new CouponException("sold out " + coupon.toString());
		}

		Date currentDate = new Date();
		if (currentDate.before(coupon.getStartDate())) {
			throw new CouponException("not availabe yet " + coupon.toString());
		}
		if (currentDate.after(coupon.getEndDate())) {
			throw new CouponException("not availabe anymore " + coupon.toString());

		}

	}

	public void getAllPurchasedCoupons() {

	}
	//
	// getAllPurchasedCouponsByType(Type type){
	//
	// }
	//
	// getAllPurchasedCouponsByPrice(long price){
	//
	//
	// }

}

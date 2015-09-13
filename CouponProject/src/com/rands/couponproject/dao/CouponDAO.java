package com.rands.couponproject.dao;

import java.sql.SQLException;
import java.util.Collection;

import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;

public interface CouponDAO {
	public void createCoupon(Coupon coupon) throws Exception;
	public void removeCoupon(Coupon coupon) throws Exception;
	public void updateCoupon(Coupon coupon) throws Exception;
	public Coupon getCoupon(long id);
	
	public Collection<Coupon> getAllCoupons() throws SQLException;
	public Collection<Coupon> getCouponByType(CouponType couponType);
	
	Collection<Coupon> getCompanyCoupons(long companyId);
	Collection<Coupon> getCustomerCoupons(long customerId);
	
	void createCompanyCoupon(long companyId,long couponId) throws SQLException;
	void createCustomerCoupon(long customerId,long couponId) throws SQLException;
	
	void removeCoupon(long id) throws SQLException;

	void removeCompanyCoupon(long couponId) throws SQLException;
	void removeCustomerCoupon(long couponId) throws SQLException;

//	/**
//	 * getCustomerCoupon - gets a Coupon of a specific Company
//	 * @param customerId
//	 * @param couponId
//	 * @return the Coupon (or null if such Coupon does not exist)
//	 */
//	Coupon getCompanyCoupon(long companyId, long couponId);
//	
//	/**
//	 * getCustomerCoupon - gets a Coupon of a specific Customer
//	 * @param customerId
//	 * @param couponId
//	 * @return the Coupon (or null if such Coupon does not exist)
//	 */
//	Coupon getCustomerCoupon(long customerId, long couponId);
	
//	Collection<Coupon> getAllPurchasableCoupons() throws SQLException;
//	Collection<Coupon> getAllPurchasableCouponsByType(CouponType couponType) throws SQLException;
}

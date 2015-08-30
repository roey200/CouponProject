package com.rands.couponproject.dao;

import java.sql.SQLException;
import java.util.Collection;

import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;

public interface CouponDAO {
	public void createCoupon(Coupon coupon);
	public void removeCoupon(Coupon coupon) throws SQLException;
	public void updateCoupon(Coupon coupon);
	public Coupon getCoupon(long id);
	public Collection<Coupon> getAllCoupons();
	public Collection<Coupon> getCouponByType(CouponType couponType);
	
	Collection<Coupon> getCompanyCoupons(long companyId);
	Collection<Coupon> getCustomerCoupons(long customerId);

}

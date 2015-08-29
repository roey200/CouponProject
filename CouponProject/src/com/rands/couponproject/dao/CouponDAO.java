package com.rands.couponproject.dao;

import java.util.Collection;

import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;

public interface CouponDAO {
	public void createCoupon(Coupon coupon);
	public void removeCoupon(Coupon coupon);
	public void updateCoupon(Coupon coupon);
	public Coupon getCoupon(long id);
	public Collection<Coupon> getAllCoupons();
	public Collection<Coupon> getCouponByType(CouponType couponType);

}

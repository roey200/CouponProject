package com.rands.couponproject.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Coupon {

	private long id;
	private String title;
	private Date startDate;
	private Date endDate;
	private int amount;
	private CouponType type;
	private String massage;
	private double price;
	private String image;

	public Coupon()
	{

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public CouponType getType() {
		return type;
	}

	public void setType(CouponType type) {
		this.type = type;
	}

	public String getMassage() {
		return massage;
	}

	public void setMassage(String massage) {
		this.massage = massage;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * checks if the coupon is expired.
		
	 * @return - true if endDate is before the current date
	 */
	public boolean isExpired() {
		return endDate.before(new Date());
	}

	@Override
	public String toString() {
		return "Coupon [id=" + id + ", title=" + title + ", startDate="
				+ startDate + ", endDate=" + endDate + ", amount=" + amount
				+ ", type=" + type + ", massage=" + massage + ", price="
				+ price + ", image=" + image + "]";
	}
	
	// remember : this is used in Collection.contains
	public boolean equals(Object other) {
		if (this == other) // same reference
			return true;
		
		if (other instanceof Coupon) {
			Coupon otherCoupon = (Coupon)other;
			
			if (this.title != null)
				return this.title.equals(otherCoupon.title); // title should be unique
			
		}
		return false;
	}
	
}

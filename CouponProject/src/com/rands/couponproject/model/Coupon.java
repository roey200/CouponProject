package com.rands.couponproject.model;

import java.sql.Date;

public class Coupon {

	private long id;
	private String title;
	private Date StartDate;
	private Date EndDate;
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
		return StartDate;
	}

	public void setStartDate(Date startDate) {
		StartDate = startDate;
	}

	public Date getEndDate() {
		return EndDate;
	}

	public void setEndDate(Date endDate) {
		EndDate = endDate;
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

	@Override
	public String toString() {
		return "Coupon [id=" + id + ", title=" + title + ", StartDate="
				+ StartDate + ", EndDate=" + EndDate + ", amount=" + amount
				+ ", type=" + type + ", massage=" + massage + ", price="
				+ price + ", image=" + image + "]";
	}
	public boolean equals(Object other){
		if (other instanceof Coupon) {
			Coupon otherCoupon = (Coupon)other ;
			if (otherCoupon.title.equals(this.title))
				return true;
		}
		return false;
	}
	
}

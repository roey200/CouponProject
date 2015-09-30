package com.rands.couponproject.model;

import java.util.ArrayList;
import java.util.Collection;

public class Customer {
	private long id;
	private String customerName;
	private String password;
	private Collection<Coupon> coupons;

	public Customer()
	{
		coupons = new ArrayList<Coupon>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + customerName + "]";
	}
	
	// remember : this is used in Collection.contains
	public boolean equals(Object other) {
		
		if(other instanceof Customer)
		{
			Customer otherCustomer=((Customer)other);
			
			if (this.customerName != null)
				return this.customerName.equals(otherCustomer.customerName); // name should be unique
			
		}
		return false;
	}		

}

package com.rands.couponproject.model;

import java.util.ArrayList;
import java.util.Collection;

public class Company {
	private long id;
	private String companyName;
	private String password;
	private String email;
	private Collection<Coupon> coupons;

	public Company()
	{
		coupons = new ArrayList<Coupon>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Collection<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(Collection<Coupon> coupons) {
		this.coupons = coupons;
	}

	public String toString()
	{
		return "Commpany [id=" + id + ", name=" + companyName + "]";
	}
	
	// remember : this is used in Collection.contains
	public boolean equals(Object other) {
		if (this == other) // same reference
			return true;
		
		if (other instanceof Company) {
			Company otherCompany=((Company)other);
			
			if (this.companyName != null)
				return this.companyName.equals(otherCompany.companyName); // name should be unique
			
		}
		return false;
	}	

}

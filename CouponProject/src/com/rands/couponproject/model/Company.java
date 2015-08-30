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
	
	public boolean equals(Object other)
	{
		if(other instanceof Company)
		{
			Company otherCompany=((Company)other);
			if(otherCompany.companyName.equals(this.companyName))
				if(otherCompany.id==this.id)
					return true;
		}
		return false;
	}

}

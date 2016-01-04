package com.rands.couponproject.jpa;

public enum IncomeType {

	CUSTOMER_PURCHASE("Customer purchased coupon"),
	COMPANY_CREATE_COUPON("Company created coupon"),
	COMPANT_UPDATE_COUPON("Company updated a coupon");
	
	private String description;

	private IncomeType(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return this.getDescription();
	}
	
	
	
	
}

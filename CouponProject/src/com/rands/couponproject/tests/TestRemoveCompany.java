package com.rands.couponproject.tests;

import com.rands.couponproject.facede.AdminFacade;
import com.rands.couponproject.model.Company;

public class TestRemoveCompany {

	static AdminFacade a;

	public static void main(String[] args) {
		a = new AdminFacade();
		removeComapny (3);
		
		

	}

	private static void removeComapny(int id) {
		
		Company c = a.getCompany(id);
		a.removeCompany(c);
	}

}

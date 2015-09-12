package com.rands.couponproject.dao;

import java.sql.SQLException;
import java.util.Collection;

import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;

public interface CompanyDAO {
	public void createCompany(Company company) throws Exception; // you don't have to write public here. all functions of an interface are public
	public void removeCompany(Company company) throws Exception;
	public void updateCompany(Company company) throws Exception;
	public Company getCompany(long id);
	public Collection<Company> getAllCompanies() throws SQLException;
	public Company getCompany(String name);
	Collection<Coupon> getCoupons(long companyId);
	public boolean login(String companyName,  String password);
	void removeCompany(long id) throws SQLException;

}

package com.rands.couponproject.dao;

import java.sql.SQLException;
import java.util.Collection;

import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.Customer;

public interface CustomerDAO {
	public void createCustomer(Customer customer) throws Exception;
	public void removeCustomer (Customer customer) throws Exception;
	public void updateCustomer (Customer customer) throws Exception;
	public Customer getCustomer(String name);
	public Customer getCustomer (long id);
	public Collection<Customer> getAllCustomers();
	public Collection<Coupon> getCoupons(long id);
	public boolean login(String CustomerName, String password);
	void removeCustomer(long id) throws SQLException;

}

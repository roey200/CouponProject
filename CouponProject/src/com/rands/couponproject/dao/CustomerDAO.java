package com.rands.couponproject.dao;

import java.util.Collection;

import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.Customer;

public interface CustomerDAO {
	public void createCustomer(Customer customer);
	public void removeCustomer (Customer customer);
	public void updateCustomer (Customer customer);
	public Customer getCustomer (long id);
	public Collection<Customer> getAllCustomers();
	public Collection<Coupon> getCoupon();
	public boolean login(String CustomerName, String password);
	public Customer getCustomer(String name);

}

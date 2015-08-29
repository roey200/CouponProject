package com.rands.couponproject.facede;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.rands.couponproject.CouponSystem;
import com.rands.couponproject.dao.CompanyDAO;
import com.rands.couponproject.dao.CompanyDBDAO;
import com.rands.couponproject.dao.CouponDAO;
import com.rands.couponproject.dao.CouponDBDAO;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;

public class CompanyFacade implements CouponClientFacade {
	static Logger logger = Logger.getLogger(CompanyFacade.class);

	private CompanyDAO companyDAO;
	private CouponDAO couponDAO;
	
	public static CouponClientFacade login(String name, String password,ClientType clientType) throws Exception {
		
		if (clientType != ClientType.COMPANY){
			logger.error("company login failed mismitch clientType =" +clientType  );
			throw new Exception("LoginFailed");
		}
			
		CompanyFacade facade = new CompanyFacade();
		
		Company company = facade.companyDAO.getCompany(name);
		if (null == company) {
			logger.error("company login failed ,company " + name + " dose not exist");
			throw new Exception("LoginFailed");
		}
				
		if (company.getPassword().equals(password))
			return facade;
		
		logger.error("company login failed ,company " + name + " password mismatch");
		throw new Exception("LoginFailed");
	}
	
	private  CompanyFacade() {
		companyDAO= new CompanyDBDAO();
		couponDAO = new CouponDBDAO();
	}
	
	public void createCoupon(Coupon coupon) {
		String title=coupon.getTitle(); // not finisheddddddddddd++++++++++++++++++++
		couponDAO.createCoupon(coupon);
		
	}
	
	public void removeCoupon(Coupon coupon) {
		couponDAO.removeCoupon(coupon);// not finisheddddddd
	}
	
	public void updateCoupon(Coupon coupon){
		long id = coupon.getId();
		Coupon coupon2 = couponDAO.getCoupon(id);
		coupon2.setPrice(coupon.getPrice());
		coupon2.setEndDate(coupon.getEndDate());
		couponDAO.updateCoupon(coupon2);
	}
	public void getCoupon(long id){
		couponDAO.getCoupon(id);
	}
	
	public Collection<Coupon> getAllCoupons()
	{
		return companyDAO.getCoupons();
	} 
	
	public Collection<Coupon> getCouponByType(CouponType couponType) {
		return couponDAO.getCouponByType(couponType);
	}
	
	


	
	
	
	
}

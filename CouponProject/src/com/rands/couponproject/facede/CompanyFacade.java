package com.rands.couponproject.facede;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.rands.couponproject.ConnectionPool;
import com.rands.couponproject.CouponSystem;
import com.rands.couponproject.dao.CompanyDAO;
import com.rands.couponproject.dao.CompanyDBDAO;
import com.rands.couponproject.dao.CouponDAO;
import com.rands.couponproject.dao.CouponDBDAO;
import com.rands.couponproject.dao.CustomerDAO;
import com.rands.couponproject.dao.CustomerDBDAO;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;

public class CompanyFacade implements CouponClientFacade {
	static Logger logger = Logger.getLogger(CompanyFacade.class);

	//private CompanyDAO companyDAO;
	//private CouponDAO couponDAO;
	
	private long companyId; 
	
	private  CompanyFacade() {
//		companyDAO= new CompanyDBDAO();
//		couponDAO = new CouponDBDAO();
	}
	
//	public static CouponClientFacade login(String name, String password,ClientType clientType) throws Exception {
//		
//		if (clientType != ClientType.COMPANY){
//			logger.error("company login failed mismitch clientType =" +clientType  );
//			throw new Exception("LoginFailed");
//		}
//		
//		CompanyDAO companyDAO = new CompanyDBDAO();
//		
//		Company company = companyDAO.getCompany(name);
//		if (null == company) {
//			logger.error("company login failed ,company " + name + " dose not exist");
//			throw new Exception("LoginFailed");
//		}
//				
//		if (!company.getPassword().equals(password)) {
//			logger.error("company login failed ,company " + name + " password mismatch");
//			throw new Exception("LoginFailed");
//		}
//		
//		CompanyFacade facade = new CompanyFacade();
//		facade.companyId = company.getId();
//		return facade;
//	}

	public static CouponClientFacade login(String name, String password, ClientType clientType) throws Exception {

		if (clientType != ClientType.COMPANY) {
			logger.error("company login failed mismitch clientType =" + clientType);
			throw new Exception("LoginFailed");
		}

		CompanyDAO companyDAO = new CompanyDBDAO();

		if (!companyDAO.login(name, password)) {
			logger.error("company login failed ,company " + name);
			throw new Exception("LoginFailed");
		}

		CompanyFacade facade = new CompanyFacade();
		Company company = companyDAO.getCompany(name);
		facade.companyId = company.getId(); 
		return facade;
	}
	
	public void createCoupon(Coupon coupon) {
		CouponDAO couponDAO = new CouponDBDAO();
		couponDAO.createCoupon(coupon);
	}
	
	public void removeCoupon(Coupon coupon) throws Exception {
		ConnectionPool pool;
		Connection conn;

		try {
			pool = ConnectionPool.getInstance();
			conn = pool.getConnection();
		} catch (Exception e) {
			logger.error("removeCoupon failed to get a connection : " + e.toString());
			throw e;
		}

		CouponDAO couponDAO = new CouponDBDAO(conn);

		try {
			conn.setAutoCommit(true); // begin transaction
			
			couponDAO.removeCoupon(coupon); 
			
			conn.commit(); // end the transaction
		} catch (Exception e) {
			logger.error("removeCoupon failed : " + e.toString());
			try {
				conn.rollback(); // abort the transaction
			} catch (SQLException e1) {
				logger.error("removeCoupon rollback failed : " + e.toString());
			}
			throw (e);
		} finally {
			pool.returnConnection(conn);
		}		
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

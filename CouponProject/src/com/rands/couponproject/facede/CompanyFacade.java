package com.rands.couponproject.facede;

import java.sql.Connection;
//import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
import com.rands.couponproject.model.Customer;

public class CompanyFacade implements CouponClientFacade {
	static Logger logger = Logger.getLogger(CompanyFacade.class);

	private long companyId;

	private CompanyFacade() {
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

	private Company getLogedinCompany() throws Exception {
		CompanyDAO companyDAO = new CompanyDBDAO();
		Company company = companyDAO.getCompany(companyId);
		if (null == company) {
			logger.error("getLogedinCompany company does not exist any more");
			throw new Exception("getLogedinCompany company does not exist any more");
		}

		CouponDAO couponDAO = new CouponDBDAO();
		Collection<Coupon> coupons = couponDAO.getCompanyCoupons(companyId);
		company.setCoupons(coupons);

		return company;
	}

	private Connection getConnection() throws Exception {
		try {
			ConnectionPool pool = ConnectionPool.getInstance();
			Connection conn = pool.getConnection();
			return conn;
		} catch (Exception e) {
			logger.error("getConnection failed : " + e.toString());
			throw e;
		}
	}

	private void returnConnection(Connection conn) {
		try {
			ConnectionPool pool = ConnectionPool.getInstance();
			pool.returnConnection(conn);
		} catch (Exception e) {
			logger.error("returnConnection failed : " + e.toString());
		}
	}

	public void createCoupon(Coupon coupon) throws Exception {
		if (companyHasCoupon(coupon)) {
			throw new Exception("duplicate coupon " + coupon.getTitle() + " for company " +  companyId);
		}

		Connection conn = getConnection();
		CouponDAO couponDAO = new CouponDBDAO(conn);
		try {
			conn.setAutoCommit(true); // begin transaction
			couponDAO.createCoupon(coupon);
			couponDAO.createCompanyCoupon(companyId, coupon.getId());

			conn.commit(); // end the transaction
		} catch (Exception e) {
			logger.error("createCoupon failed : " + e.toString());
			try {
				conn.rollback(); // abort the transaction
			} catch (SQLException e1) {
				logger.error("createCoupon rollback failed : " + e.toString());
			}
			throw (e);
		} finally {
			returnConnection(conn);
		}
	}

	public void removeCoupon(long couponId) throws Exception {
		Connection conn = getConnection();

		CouponDAO couponDAO = new CouponDBDAO(conn);

		try {
			conn.setAutoCommit(true); // begin transaction

			//couponDAO.removeCoupon(coupon); 
			couponDAO.removeCoupon(couponId);

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
			returnConnection(conn);
		}
	}

	public void removeCoupon(Coupon coupon) throws Exception {
		removeCoupon(coupon.getId());
	}

	public void updateCoupon(Coupon coupon) {
		CouponDAO couponDAO = new CouponDBDAO();
		try {
			couponDAO.updateCoupon(coupon);
		} catch (Exception e) {
			logger.error("update coupon failed : " + e.toString());
		}

	}

	public Coupon getCoupon(long id) {
		CouponDAO couponDAO = new CouponDBDAO();
		return couponDAO.getCoupon(id);
	}

	public Collection<Coupon> getAllCoupons() throws Exception
	{
		Company company = getLogedinCompany();
		return company.getCoupons();
	}

	public Collection<Coupon> getCouponByType(CouponType type) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();

		for (Coupon coupon : getAllCoupons()) {
			if (coupon.getType() == type)
				coupons.add(coupon);
		}

		return coupons;
	}

	public Collection<Coupon> getCouponsByPrice(long price) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();

		for (Coupon coupon : getAllCoupons()) {
			if (coupon.getPrice() <= price)
				coupons.add(coupon);
		}

		return coupons;
	}

	public Collection<Coupon> getCouponsByDate(Date toDate) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();

		for (Coupon coupon : getAllCoupons()) {
			//			if (toDate.after(coupon.getEndDate()))
			//				coupons.add(coupon);
			if (coupon.getEndDate().before(toDate))
				coupons.add(coupon);
		}

		return coupons;
	}

	public Company getCompany() {

		CompanyDAO companyDao = new CompanyDBDAO();
		return companyDao.getCompany(companyId);
	}
	
	private boolean companyHasCoupon(Coupon coupon) throws Exception {
		for (Coupon cup : getAllCoupons()) { // check all coupons of the current (logedin) company 
			if (cup.getTitle().equals(coupon.getTitle()))
				return true;
		}
		return false;
	}

}

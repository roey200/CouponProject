package com.rands.couponproject.facede;

import java.sql.Connection;
//import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.rands.couponproject.ConnectionPool;
import com.rands.couponproject.dao.CompanyDAO;
import com.rands.couponproject.dao.CompanyDBDAO;
import com.rands.couponproject.dao.CouponDAO;
import com.rands.couponproject.dao.CouponDBDAO;
import com.rands.couponproject.exceptions.CouponProjectException.CompanyException;
import com.rands.couponproject.exceptions.CouponProjectException.CompanyLoginException;
import com.rands.couponproject.exceptions.CouponProjectException.CouponException;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;
import com.rands.couponproject.model.ClientType;
import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;

/**
 * CompanyFacade - The CompanyFacade operates on behalf of a specific Company (the logedin Company). <br>
 * 					A CompanyFacade object can only be acquired throw the login method.
  */
public class CompanyFacade implements CouponClientFacade {
	static Logger logger = Logger.getLogger(CompanyFacade.class);

	private long companyId;

	private CompanyFacade() {
	}

	public static CouponClientFacade login(String name, String password, ClientType clientType) throws LoginException {

		if (clientType != ClientType.COMPANY) {
			logger.error("company login failed mismitch clientType =" + clientType);
			throw new CompanyLoginException("Invalid ClientType : " + clientType);
		}

		CompanyDAO companyDAO = new CompanyDBDAO();

		if (!companyDAO.login(name, password)) {
			logger.error("company login failed name = " + name);
			throw new CompanyLoginException(name);					
		}

		CompanyFacade facade = new CompanyFacade();
		Company company = companyDAO.getCompany(name);
		facade.companyId = company.getId();
		return facade;
	}

	/**
	 * 
	 * @return the currently logedin Company 
	 * @throws Exception 
	 */
	public Company getCompany() throws Exception {
		CompanyDAO companyDAO = new CompanyDBDAO();
		Company company = companyDAO.getCompany(companyId);
		if (null == company) {
			logger.error("getCompany company does not exist any more : id = " + companyId);
			throw new CompanyException("getCompany company does not exist any more : id = " + companyId);
		}
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
		if (companyHasCoupon(coupon.getTitle())) {
			throw new CouponException("coupon exists" + coupon.getTitle() + " for company " +  companyId);
		}

		Connection conn = getConnection();
		CouponDAO couponDAO = new CouponDBDAO(conn);
		try {
			conn.setAutoCommit(false); // begin transaction
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
			throw e;
		} finally {
			returnConnection(conn);
		}
	}

	public void removeCoupon(long couponId) throws Exception {
		if (!companyHasCoupon(couponId)) {
			throw new CouponException("company " +  companyId + " does not own coupon " + couponId);
		}

		Connection conn = getConnection();
		CouponDAO couponDAO = new CouponDBDAO(conn);

		try {
			conn.setAutoCommit(false); // begin transaction

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
			throw e;
		} finally {
			returnConnection(conn);
		}
	}

	public void removeCoupon(Coupon coupon) throws Exception {
		removeCoupon(coupon.getId());
	}
	
	public void updateCoupon(Coupon coupon) throws Exception {
		if (!companyHasCoupon(coupon.getId())) {
			throw new CouponException("company " +  companyId + " does not own coupon " + coupon.getId());
		}

		CouponDAO couponDAO = new CouponDBDAO();
		try {
			couponDAO.updateCoupon(coupon);
		} catch (Exception e) {
			logger.error("update coupon failed : " + e.toString());
			throw e;
		}

	}

	/**
	 * 
	 * @param couponId
	 * @return the coupon with the couponId id or null if the current logedin company does not own that coupon
	 * @throws Exception
	 */
	public Coupon getCoupon(long couponId) throws Exception {
//		CouponDAO couponDAO = new CouponDBDAO();
//		return couponDAO.getCoupon(couponId);

		for (Coupon coupon : getAllCoupons()) { // check all coupons of the current (logedin) company 
			if (coupon.getId() == couponId)
				return coupon;
		}
		return null;		
	}
	
	/**
	 * 
	 * @return the coupons of the logedin company
	 * @throws Exception
	 */
	public Collection<Coupon> getAllCoupons() throws Exception
	{
		Company company = getCompany();
		return company.getCoupons();
	}

	/**
	 * 
	 * @param type
	 * @return the coupons of the logedin company of a specific type 
	 * @throws Exception
	 */
	public Collection<Coupon> getCouponByType(CouponType type) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();

		for (Coupon coupon : getAllCoupons()) {
			if (coupon.getType() == type)
				coupons.add(coupon);
		}

		return coupons;
	}

	/**
	 * 
	 * @param price
	 * @return the coupons of the logedin company limited by price
	 * @throws Exception
	 */
	public Collection<Coupon> getCouponsByPrice(long price) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();

		for (Coupon coupon : getAllCoupons()) {
			if (coupon.getPrice() <= price)
				coupons.add(coupon);
		}

		return coupons;
	}

	/**
	 * 
	 * @param toDate
	 * @return the coupons of the logedin company where endDate < toDate
	 * @throws Exception
	 */
	public Collection<Coupon> getCouponsByDate(Date toDate) throws Exception {
		Collection<Coupon> coupons = new ArrayList<Coupon>();

		for (Coupon coupon : getAllCoupons()) {
			if (coupon.getEndDate().before(toDate))
				coupons.add(coupon);
		}

		return coupons;
	}

	private boolean companyHasCoupon(long couponId) throws Exception {
		for (Coupon coupon : getAllCoupons()) { // check all coupons of the current (logedin) company 
			if (coupon.getId() == couponId)
				return true;
		}
		return false;
	}

	private boolean companyHasCoupon(String title) throws Exception {
		for (Coupon coupon : getAllCoupons()) { // check all coupons of the current (logedin) company 
			if (coupon.getTitle().equals(title))
				return true;
		}
		return false;
	}

}

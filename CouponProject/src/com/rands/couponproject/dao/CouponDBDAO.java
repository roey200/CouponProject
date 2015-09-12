 package com.rands.couponproject.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.rands.couponproject.exceptions.CouponException;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;


public class CouponDBDAO extends BaseDBDAO implements CouponDAO {
	static Logger logger = Logger.getLogger(CouponDBDAO.class);
	
	public CouponDBDAO()
	{
		super();
	}
	
	public CouponDBDAO(Connection conn)
	{
		super(conn);
	}
	
	private Coupon getFromResultSet(ResultSet rs) throws SQLException {
		Coupon coupon = new Coupon();       
		coupon.setId(rs.getInt("id"));
		coupon.setTitle(rs.getString("title"));
		coupon.setAmount(rs.getInt("amount"));
		coupon.setStartDate(rs.getDate("start_date"));
		coupon.setEndDate(rs.getDate("end_date"));
		coupon.setImage(rs.getString("image"));
		coupon.setMassage(rs.getString("message"));
		coupon.setPrice(rs.getDouble("price"));
		
		String type = rs.getString("coupon_type");
		coupon.setType(CouponType.valueOf(type));
		
		return coupon;
	}

	@Override
	public void createCoupon(Coupon coupon) throws Exception {
		 
		if(coupon.getEndDate().before(coupon.getStartDate())) {
			throw new Exception("coupon endDate < startDate");   // not finished
		}
		Date currentDate = new Date();
//		if (currentDate.before(coupon.getEndDate())) {
//			throw new CouponException("coupon endDate < currentDate");
//		}	
		
		Connection conn =getConnection();
		try {
			String sql = "insert into APP.coupon (title,start_date , end_date,amount , coupon_type, message , price , image) values(?,?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, coupon.getTitle());
			ps.setDate(2,coupon.getStartDate());
			ps.setDate(3,coupon.getEndDate());
			ps.setInt(4,coupon.getAmount());
			ps.setString(5,coupon.getType().name());
			ps.setString(6,coupon.getMassage());
			ps.setDouble(7,coupon.getPrice());
			ps.setString(8,coupon.getImage());

			int affectedRows = ps.executeUpdate();
	        if (affectedRows == 0) {
	            throw new SQLException("Creating coupon failed, no rows affected.");
	        }
        	long id = getGeneratedKey(ps);
        	coupon.setId(id);
            System.out.println("coupon=" + coupon.getTitle() + " added to database, id=" + id);

		} catch (SQLException e) {
			logger.error("createCoupon " + coupon.getTitle() + " failed : " + e.toString());
			throw e;
		} finally {
			returnConnection(conn);
		}		
	}
	
	@Override
	public void createCompanyCoupon(long companyId,long couponId) throws SQLException {
		Connection conn =getConnection();
		try {
			String sql = "insert into APP.company_coupon (comp_id,coupon_id) values(?,?)";
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			ps.setLong(1, companyId);
			ps.setLong(2,couponId);
			
			int affectedRows = ps.executeUpdate();
	        if (affectedRows == 0) {
	            throw new SQLException("createCompanyCoupon, no rows affected.");
	        }
        	long id = getGeneratedKey(ps);
            System.out.println("company_coupon added to database, id=" + id);

		} catch (SQLException e) {
			logger.error("createCompanyCoupon failed : " + e.toString());
			throw e;
		} finally {
			returnConnection(conn);
		}					
		
	}
	
	@Override
	public void createCustomerCoupon(long customerId,long couponId) throws SQLException {
		Connection conn =getConnection();
		try {
			String sql = "insert into APP.customer_coupon (cust_id,coupon_id) values(?,?)";
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			ps.setLong(1, customerId);
			ps.setLong(2,couponId);
			
			int affectedRows = ps.executeUpdate();
	        if (affectedRows == 0) {
	            throw new SQLException("createCustomerCoupon, no rows affected.");
	        }
        	long id = getGeneratedKey(ps);
            System.out.println("customer_coupon added to database, id=" + id);

		} catch (SQLException e) {
			logger.error("createCustomerCoupon failed : " + e.toString());
			throw e;
		} finally {
			returnConnection(conn);
		}					
		
	}
	
	private void doUpdate(String sql,long param) throws SQLException {

		Connection conn = getConnection();
		try {
			PreparedStatement ps;
			int n;

			ps = conn.prepareStatement(sql);
			ps.setLong(1, param);
			n = ps.executeUpdate();
			logger.debug(n + " records affected : " + sql);

		} catch (SQLException e) {
			logger.error("doUpdate failed : " + e.toString());
			throw e;
		} finally {
			returnConnection(conn);
		}

	}
	
	@Override
	public void removeCoupon(long id) throws SQLException {
		removeCustomerCoupon(id); // remove the links
		removeCompanyCoupon(id);
		
		doUpdate("delete from APP.coupon where id=?",id);
	}	

//	@Override
//	public void removeCoupon(long id) throws SQLException {
//
//		Connection conn = getConnection();
//		try {
////			long couponId = id;
//
//			PreparedStatement ps;
//			int n;
//
//			removeCustomerCoupon(id); // remove the links
//			removeCompanyCoupon(id);
//			
//			ps = conn.prepareStatement("delete from APP.coupon where id=?");
//			ps.setLong(1, id);
//			n = ps.executeUpdate();
//			logger.debug(n + " records removed from APP.coupon");
//			
//
//		} catch (SQLException e) {
//			logger.error("removeCoupon failed : " + e.toString());
//			throw e;
//		} finally {
//			returnConnection(conn);
//		}
//
//	}	

	@Override
	public void removeCoupon(Coupon coupon) throws SQLException {
		removeCoupon(coupon.getId());
	}
	
	@Override
	public void removeCustomerCoupon(long couponId) throws SQLException {
		doUpdate("delete from APP.customer_coupon where coupon_id=?",couponId);
	}
	
//	/**
//	 * removes all of the customer_coupon records of a given customer.
//	 */
//	@Override
//	public void removeCustomerCoupons(long customerId) throws SQLException {
//		doUpdate("delete from APP.customer_coupon where cust_id=?",customerId);
//	}
	
	@Override
	public void removeCompanyCoupon(long couponId) throws SQLException {
		doUpdate("delete from APP.company_coupon where coupon_id=?",couponId);
	}

	@Override
	public void updateCoupon(Coupon coupon) throws Exception {
		Connection conn = getConnection();

		try {
			String sql = "update APP.coupon set title=?,start_date=? , end_date=?,amount=? , coupon_type=?, message=? , price=? , image=? where id=?";
			//Statement st =conn.createStatement();
			PreparedStatement ps = conn.prepareStatement(sql);
			
		
			ps.setString(1, coupon.getTitle());
			ps.setDate(2,coupon.getStartDate());
			ps.setDate(3,coupon.getEndDate());
			ps.setInt(4,coupon.getAmount());
			ps.setString(5,coupon.getType().name());
			ps.setString(6,coupon.getMassage());
			ps.setDouble(7,coupon.getPrice());
			ps.setString(8,coupon.getImage());
			
			ps.setLong(9, coupon.getId());

			ps.execute();
		} catch (SQLException e) {
			logger.error("updateCoupon failed : " + e.toString());
			throw e;
		} finally {
			returnConnection(conn);
		}

	}

	@Override
	public Coupon getCoupon(long id) {

		Coupon coupon = null;
		Connection conn = getConnection();

		try {
			String sql = "select * from APP.coupon where id=?";
			PreparedStatement ps = conn.prepareStatement(sql);   
            ps.setLong(1,id);
            ResultSet rs = ps.executeQuery();   
			
			if (!rs.next())
				return null;
			
			coupon = getFromResultSet(rs);
			//coupon.setId(id);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			returnConnection(conn);
		}
		return coupon ; 
	}

	public Coupon getCoupon(String title) {

		Coupon coupon = null;
		Connection conn = getConnection();

		try {
			String sql = "select * from APP.coupon where title=?";
			PreparedStatement ps = conn.prepareStatement(sql);   
            ps.setString(1,title);
            ResultSet rs = ps.executeQuery();   
			
			if(!rs.next())
				return null;
			
			coupon = getFromResultSet(rs);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			returnConnection(conn);
		}
		return coupon ; 
	}


	@Override
	public Collection<Coupon> getAllCoupons() throws SQLException {
		 
		Collection<Coupon> coupons = new ArrayList<Coupon>();
		Connection conn = getConnection();

		try {
			String sql = "SELECT * from APP.coupon";
			Statement st = conn.createStatement(); //connects to company DB			
			ResultSet rs = st.executeQuery(sql);

			while (rs.next())
			{
				Coupon coupon = getFromResultSet(rs);
				coupons.add(coupon);
			}
		} catch (SQLException e) {
			logger.error("getAllCoupons() failed : " + e.toString());
			throw e;
		} finally {
			returnConnection(conn);
			conn = null;
		}

		return coupons;
	}
	
	@Override
	public Collection<Coupon> getCouponByType(CouponType couponType) {
		 
		Collection<Coupon> coupons = new ArrayList<Coupon>();
		Connection conn = getConnection();
		
		String typeString = couponType.toString();

		try {
			String sql = "select * from APP.coupon where coupon_type=?";
			PreparedStatement ps = conn.prepareStatement(sql);   
            ps.setString(1,typeString);
            ResultSet rs = ps.executeQuery();   			
	
			while (rs.next())
			{
				Coupon coupon = getFromResultSet(rs);
				coupons.add(coupon);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnConnection(conn);
			conn = null;
		}

		return coupons;
	}
	
	@Override
	public Collection<Coupon> getCompanyCoupons(long companyId) {
		 
		Collection<Coupon> coupons = new ArrayList<Coupon>();
		Connection conn = getConnection();

		try {
			String sql = "SELECT * from APP.coupon c JOIN APP.company_coupon cc ON c.id = cc.coupon_id WHERE cc.comp_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1,companyId);

			ResultSet rs = ps.executeQuery();

			while (rs.next())
			{
				Coupon coupon = getFromResultSet(rs);
				coupons.add(coupon);
			}
		} catch (SQLException e) {
			logger.error("getCompanyCoupons failed : " + e.toString());
		} finally {
			returnConnection(conn);
		}

		return coupons;
	}
	
	@Override
	public Collection<Coupon> getCustomerCoupons(long customerId) {
		 
		Collection<Coupon> coupons = new ArrayList<Coupon>();
		Connection conn = getConnection();

		try {
			String sql = "SELECT * from APP.coupon c JOIN APP.customer_coupon cc ON c.id = cc.coupon_id WHERE cc.cust_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1,customerId);

			ResultSet rs = ps.executeQuery();

			while (rs.next())
			{
				Coupon coupon = getFromResultSet(rs);
				coupons.add(coupon);
			}
		} catch (SQLException e) {
			logger.error("getCompanyCoupons failed : " + e.toString());
		} finally {
			returnConnection(conn);
		}

		return coupons;
	}
}	

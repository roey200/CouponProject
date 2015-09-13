package com.rands.couponproject.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.CouponType;

public class CompanyDBDAO extends BaseDBDAO implements CompanyDAO {
	
	static Logger logger = Logger.getLogger(CompanyDBDAO.class);

	public CompanyDBDAO() {
		super();
	}

	public CompanyDBDAO(Connection conn) {
		super(conn);
	}
	
	private Company getFromResultSet(ResultSet rs) throws SQLException {
		long id = rs.getLong("id");
		String name = rs.getString("comp_name");
		String password = rs.getString("password");
		String email = rs.getString("email");

		Company company = new Company();
		company.setId(id);
		company.setCompanyName(name);
		company.setPassword(password);
		company.setEmail(email);
		
		return company;
	}	

	@Override
	public void createCompany(Company company) throws Exception { 
		// TODO Auto-generated method stub
		Connection conn = getConnection();
		try {
//			String sql = "insert into APP.company (id,comp_name, password, email) values(?,?,?,?)";
//			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//
//			ps.setLong(1, company.getId()); // sets the new values
//			ps.setString(2, company.getCompanyName());
//			ps.setString(3, company.getPassword());
//			ps.setString(4, company.getEmail());

			String sql = "insert into APP.company (comp_name, password, email) values(?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, company.getCompanyName());
			ps.setString(2, company.getPassword());
			ps.setString(3, company.getEmail());
			
			int affectedRows = ps.executeUpdate();
	        if (affectedRows == 0) {
	            throw new SQLException("Creating company failed, no rows affected.");
	        }
        	long id = getGeneratedKey(ps);
        	company.setId(id);
            logger.debug("Company created : " + company);

		} catch (Exception e) {
            logger.debug("createCompany failed : " + e.toString());
			throw e;
		} finally {
			returnConnection(conn);
		}
	}
	
	private void removeCompanyCoupons(long companyId) throws SQLException {
		CouponDAO couponDAO = new CouponDBDAO(conn);
		Collection<Coupon> coupons = couponDAO.getCompanyCoupons(companyId);
		for (Coupon coupon:coupons) {
//			couponDAO.removeCoupon(coupon); // remove the coupon and the links
			couponDAO.removeCoupon(coupon.getId()); // remove the coupon and the links
		}		
	}

	@Override
	public void removeCompany(long id) throws SQLException {
		
		Connection conn = getConnection();
		boolean doTransaction = false;
		try {
			doTransaction = conn.getAutoCommit(); // if auto commit is false we assume that a transaction has already been started
			if (doTransaction)
				conn.setAutoCommit(false); // begin transaction
			
			removeCompanyCoupons(id);
			
			String sql = "delete from APP.company where id=?";
			PreparedStatement ps = conn.prepareStatement(sql);//
			ps.setLong(1, id);
			ps.execute();
			
			if (doTransaction)
				conn.commit(); // end the transaction
		} catch (SQLException e) {
			logger.error("removeCompany failed : " + e.toString());
			
			try {
				if (doTransaction)				
				  conn.rollback(); // abort the transaction
			} catch (SQLException e1) {
				logger.error("removeCompany rollback failed : " + e.toString());
			}			
			
			throw e;
		} finally {
//			if (doTransaction)
//				conn.setAutoCommit(true);
			returnConnection(conn);
			conn = null;
		}

	}

	@Override
	public void removeCompany(Company company) throws SQLException {
		removeCompany(company.getId());
	}
	
	@Override
	public void updateCompany(Company company) throws SQLException {
		Connection conn = getConnection();

		try {
			String sql = "update APP.company set comp_name=? , password=? , email=? where id=?";
			// Statement st = conn.createStatement(); //connects to company DB
			PreparedStatement ps = conn.prepareStatement(sql);//

			ps.setString(1, company.getCompanyName());
			ps.setString(2, company.getPassword());
			ps.setString(3, company.getEmail());
			ps.setLong(4, company.getId());
			ps.execute();

			logger.debug("Company has been updated : " + company);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("updateCompany failed : " + e.toString());
			throw e;
		} finally {
			returnConnection(conn);
			conn = null;
		}

	}

	@Override
	public Company getCompany(long id) {
		// TODO Auto-generated method stub
		Company company = null;
		Connection conn = getConnection();

		try {
			String sql = "select * from APP.company where id=?";
			PreparedStatement ps = conn.prepareStatement(sql);   
            ps.setLong(1,id);
            ResultSet rs = ps.executeQuery();   			

			// long id= rs.getLong("id");
			if(!rs.next())
				return null;

			company = getFromResultSet(rs);
			//company.setId(id);
			company.setCoupons(getCoupons(company.getId(),conn));


		} catch (SQLException e) {
			logger.error("getCompany(" + id + ") failed : " + e.toString());
		} finally {
			returnConnection(conn);
		}
		return company;
	}

	public Company getCompany(String name) {
		// TODO Auto-generated method stub
		Company company = null;
		Connection conn = getConnection();

		try {
			String sql = "select * from APP.company where comp_name=?";
			PreparedStatement ps = conn.prepareStatement(sql);   
            ps.setString(1,name);
            ResultSet rs = ps.executeQuery();   			
			
			// long id= rs.getLong("id");
			if (!rs.next())
				return null;
			
			company = getFromResultSet(rs);
			company.setCoupons(getCoupons(company.getId(),conn));

		} catch (SQLException e) {
			logger.error("getCompany(" + name + ") failed : " + e.toString());
		} finally {
			returnConnection(conn);
		}
		return company;
	}

	@Override
	public Collection<Company> getAllCompanies() throws SQLException {
		// TODO Auto-generated method stub
		Collection<Company> companies = new ArrayList<Company>();
		Connection conn = getConnection();

		try {
			String sql = "select * from APP.company";
			Statement st = conn.createStatement(); // connects to company DB
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				Company company = getFromResultSet(rs);
				companies.add(company);
			}
		} catch (SQLException e) {
			logger.error("getAllCompanies failed : " + e.toString());
			throw e;
		} finally {
			returnConnection(conn);
		}

		return companies;
	}
	
	private Collection<Coupon> getCoupons(long companyId, Connection conn) {
		CouponDAO couponDAO = new CouponDBDAO(conn);
		return couponDAO.getCompanyCoupons(companyId);
	}
	
	@Override
	public Collection<Coupon> getCoupons(long companyId) {
		return getCoupons(companyId,null);
	}	

	@Override
	public boolean login(String companyName, String password) {
		Connection conn = getConnection();
		boolean isCorrectPassword = false;
		try {
			String sql = "select password from APP.company where comp_name='" + companyName + "'";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if(!rs.next())
				return false;
			
			String dbPassword = rs.getString(1);
			isCorrectPassword = (password.equals(dbPassword));
		} catch (SQLException e) {
			logger.error("login failed : " + e.toString());
		} finally {
			returnConnection(conn);
		}
		return isCorrectPassword;
	}

}

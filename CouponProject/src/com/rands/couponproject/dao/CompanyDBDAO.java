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
import com.rands.couponproject.utils.DBUtils;

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
	public void createCompany(Company company) { // takes a company object and
													// inserts its values to
													// company table
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
        	long id = DBUtils.getGeneratedKey(ps);
        	company.setId(id);
            System.out.println("company=" + company.getCompanyName() + " added to database, id=" + id);

		} catch (SQLException e) {
			System.out.println("failed to insert company " + company.getCompanyName() + " : " + e.toString());
			e.printStackTrace();
		} finally {
			returnConnection(conn);
		}
	}

	@Override
	public void removeCompany(Company company) { // takes a company object and
													// deletes it, if it does
													// exist in the db
TODO remove company coupons !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		Connection conn = getConnection();
		try {
			String sql = "delete from APP.company where id=?";
			// Statement st = conn.createStatement(); //connects to company DB
			PreparedStatement ps = conn.prepareStatement(sql);//

			ps.setLong(1, company.getId());
			ps.execute();
			System.out
					.println("company has successfully been removed from database");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("somthing went worng please try again");
		} finally {
			returnConnection(conn);
			conn = null;
		}

	}

	@Override
	public void updateCompany(Company company) { // takes a company object and
													// updates its parameters
													// (-id) to the db
		// TODO Auto-generated method stub
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

			System.out.println("the company has been updated");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("something went worng please try again");
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

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnConnection(conn);
			conn = null;
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

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnConnection(conn);
			conn = null;
		}
		return company;
	}

	@Override
	public Collection<Company> getAllCompanies() {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnConnection(conn);
			conn = null;
		}

		return companies;
	}
	
	@Override
	public Collection<Coupon> getCoupons(long companyId) {
		CouponDAO couponDAO = new CouponDBDAO();
		return couponDAO.getCompanyCoupons(companyId);
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
		}
		return isCorrectPassword;
	}

}

package com.rands.couponproject.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.rands.couponproject.model.Company;
import com.rands.couponproject.model.Coupon;
import com.rands.couponproject.model.Customer;
import com.rands.couponproject.utils.DBUtils;

public class CustomerDBDAO extends BaseDBDAO implements CustomerDAO {

	static Logger logger = Logger.getLogger(CustomerDBDAO.class);

	public CustomerDBDAO()
	{
		super();
	}

	public CustomerDBDAO(Connection conn)
	{
		super(conn);
	}

	private Customer getFromResultSet(ResultSet rs) throws SQLException {

		long id = rs.getLong("id");
		String name = rs.getString("cust_name");
		String password = rs.getString("password");

		Customer customer = new Customer();
		customer = new Customer();
		customer.setId(id);
		customer.setCustomerName(name);
		customer.setPassword(password);

		return customer;
	}

	@Override
	public void createCustomer(Customer customer) {
		Connection conn = getConnection();
		try {
			String sql = "insert into APP.customer (cust_name, password) values(?,?)";
			//PreparedStatement ps = conn.prepareStatement(sql);
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, customer.getCustomerName());
			ps.setString(2, customer.getPassword());

			int affectedRows = ps.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Creating customer failed, no rows affected.");
			}
			long id = DBUtils.getGeneratedKey(ps);
			customer.setId(id);
			System.out.println("customer=" + customer.getCustomerName() + " added to database, id=" + id);

		} catch (SQLException e) {
			System.out.println("failed to insert customer " + customer.getCustomerName() + " : " + e.toString());
			e.printStackTrace();
		} finally {
			returnConnection(conn);
		}

	}
	
	private void removeCustomerCoupons(long customerId) throws SQLException {
		CouponDAO couponDAO = new CouponDBDAO(conn);
		Collection<Coupon> coupons = couponDAO.getCustomerCoupons(customerId);
		for (Coupon coupon:coupons) {
			couponDAO.removeCustomerCoupon(coupon.getId()); // remove the coupon links
		}		
	}

	@Override
	public void removeCustomer(long id) throws SQLException {

		Connection conn = getConnection();
		boolean doTransaction = false;
		try {
			doTransaction = conn.getAutoCommit(); // if auto commit is false we assume that a transaction has already been started
			if (doTransaction)
				conn.setAutoCommit(false); // begin transaction			
			removeCustomerCoupons(id);
			
			String sql = "delete from APP.customer where id=?";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setLong(1, id);
			ps.execute();
			
			if (doTransaction)
				conn.commit(); // end the transaction

		} catch (SQLException e) {
			logger.error("removeCustomer failed : " + e.toString());
			
			try {
				if (doTransaction)				
				  conn.rollback(); // abort the transaction
			} catch (SQLException e1) {
				logger.error("removeCustomer rollback failed : " + e.toString());
			}						
			throw e;
		} finally {
//			if (doTransaction)
//				conn.setAutoCommit(true);
			returnConnection(conn);
		}

	}
	
	@Override
	public void removeCustomer(Customer customer) throws SQLException {
		removeCustomer(customer.getId());
	}

	@Override
	public void updateCustomer(Customer customer) {
		// TODO Auto-generated method stub

		Connection conn = getConnection();

		try {
			String sql = "update APP.customer cust_name=? , password=? where id=?";
			//Statement st =conn.createStatement();
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, customer.getCustomerName());
			ps.setString(2, customer.getPassword());
			ps.setLong(3, customer.getId());
			ps.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnConnection(conn);
		}

	}

	@Override
	public Customer getCustomer(long id) {
		Customer customer = null;
		Connection conn = getConnection();

		try {
			String sql = "select * from APP.customer where id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();

			if (!rs.next())
				return null;

			customer = getFromResultSet(rs);
			customer.setCoupons(getCoupons(customer.getId(),conn));
			//customet.setId(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnConnection(conn);
		}

		return customer;
	}

	public Customer getCustomer(String name) {
		Customer customer = null;
		Connection conn = getConnection();

		try {
			String sql = "select * from APP.customer where cust_name=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();

			if (!rs.next())
				return null;

			customer = getFromResultSet(rs);
			//customer.setCustomerName(name);
			customer.setCoupons(getCoupons(customer.getId(),conn));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnConnection(conn);
		}

		return customer;
	}

	@Override
	public Collection<Customer> getAllCustomers() {
		// TODO Auto-generated method stub
		Collection<Customer> customers = new ArrayList<Customer>();
		Connection conn = getConnection();

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select * from APP.customer;");

			while (rs.next())
			{
				Customer customer = getFromResultSet(rs);
				customers.add(customer);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnConnection(conn);
		}

		return customers;

	}
	
	private Collection<Coupon> getCoupons(long customerId,Connection conn) {
		CouponDAO couponDAO = new CouponDBDAO(conn);
		return couponDAO.getCustomerCoupons(customerId);
	}	

	@Override
	public Collection<Coupon> getCoupons(long customerId) {
		return getCoupons(customerId,null);
	}

	@Override
	public boolean login(String customerName, String password) {
		Connection conn = getConnection();
		boolean isCorrectPassword = false;
		try {

			String sql = "select password from APP.Coustomer where cust_name=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, customerName);
			ResultSet rs = ps.executeQuery();

			if (!rs.next())
				return false;

			String dbPassword = rs.getString(1);
			isCorrectPassword = (password.equals(dbPassword));
		} catch (SQLException e) {
			logger.error("login failed : " + e.toString());
		}
		return isCorrectPassword;
	}

}

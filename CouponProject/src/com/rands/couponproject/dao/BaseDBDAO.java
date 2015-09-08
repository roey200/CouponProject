package com.rands.couponproject.dao;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.rands.couponproject.ConnectionPool;

/**
 * BaseDBDAO - is the base class for all of our DBDAOs. It's primary use is for sharing a Connection when 
 * database transaction that involves several DAOs is needed.
 * If a transaction that involves several DAOs is needed then :
 * 1) a Connection should be acquired and passed to the DAOs via the constructor of the DAOs.
 * 2) call the connection's setAutoCommit(false); method.
 */
public abstract class BaseDBDAO {
	static Logger logger = Logger.getLogger(BaseDBDAO.class);
	
	protected Connection conn;
	
	public BaseDBDAO()
	{
		conn = null;
	}
	
	/**
	 * 
	 * @param conn - the connection to be used. use this constructor if a database transaction is needed
	 */
	public BaseDBDAO(Connection conn)
	{
		this();
		this.conn = conn;
	}
	
	public Connection getConnection() {
		if (null != conn) // a connection was supplied externally
			return conn;

		ConnectionPool pool = null;
		try {
			pool = ConnectionPool.getInstance();
		} catch (Exception e) {
			logger.error("getConnection failed to get ConnectionPool instance : " + e.toString());
		}		
		return pool.getConnection();
	}
	
	public void returnConnection(Connection conn) {
		if (this.conn == conn) // not acquired by this DAO (the connection was supplied externally)
			return;
		
		ConnectionPool pool = null;
		try {
			pool = ConnectionPool.getInstance();
		} catch (Exception e) {
			logger.error("returnConnection failed to get ConnectionPool instance : " + e.toString());
		}
		
		pool.returnConnection(conn);
	}

}
package com.rands.couponproject.dao;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.rands.couponproject.ConnectionPool;

public class BaseDBDAO {
	static Logger logger = Logger.getLogger(BaseDBDAO.class);
	
	protected Connection conn;
	
	public BaseDBDAO()
	{
		conn = null;
	}
	
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
		if (this.conn == conn) // not acquired by this DAO
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

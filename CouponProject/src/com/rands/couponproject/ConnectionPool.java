package com.rands.couponproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import org.apache.log4j.Logger;

import com.rands.couponproject.utils.Props;
import com.rands.couponproject.utils.Utils;

public class ConnectionPool {
	static Logger logger = Logger.getLogger(ConnectionPool.class);
	Props props = new Props("ConnectionPool.properties"); // put this file in the src directory

	// a singleton is implemented by :
	//	1) a private constructor
	//	2) a public static method (normaly called getInstance) that is responsible for creating the singleton object
	//		the first time it is being called and to return that object on every call.
	//  3) a private static (reference) object (the singleton)

	private static ConnectionPool singleton = null; // the singleton (reference)

	private ArrayList<Connection> freeConnections; // the free connection that can be used
	private ArrayList<Connection> allConnections; // this will hold all connections that were created by the connectionpool

	private String driver, url, username, password;
	private int minConnections;

	/**
	 * getInstance - created the singleton on the 1st call.
	 * 
	 * @return - the singleton
	 * @throws Exception
	 */
	public static ConnectionPool getInstance() throws Exception {

		if (singleton == null)
		{
			synchronized (ConnectionPool.class) {
				if (singleton == null)
					singleton = new ConnectionPool(); //this will invoke the constructor
			}
		}
		return singleton;

	}

	// the private constructor
	private ConnectionPool() throws Exception {

		initProps();

		loadDriver(driver);
		
		url = Utils.expandEnvVars(url); // replace environment variables

		logger.info("creating " + minConnections + " connections url = " + url);
		
		freeConnections = new ArrayList<Connection>();
		allConnections = new ArrayList<Connection>();

		for (int i = 0; i < minConnections; i++)
		{
			addNewConnection(url, username, password);
		}
		logger.info(freeConnections.size() + " connections were created");
	}

	private void loadDriver(String driver) throws ClassNotFoundException {
		logger.info("loading driver : " + driver);
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			logger.error(driver + " not found " + e.toString());
			throw new ClassNotFoundException(driver + " not found");
		}
	}

	private void addNewConnection(String url, String user, String password) throws SQLException {
		try {
			//Connection conn = DriverManager.getConnection(url);
			Connection conn = DriverManager.getConnection(url, user, password);
			freeConnections.add(conn);
			allConnections.add(conn);

		} catch (SQLException e) {
			logger.error("addNewConnection " + url + " failed : " + e.toString());
			throw new SQLException(url + " not found");
		}
	}

	private void initProps() {
		driver = props.getString("dbdriver", "org.apache.derby.jdbc.ClientDriver");
		url = props.getString("dburl", "jdbc:derby://localhost:1527/sample");

		minConnections = props.getInt("minConnections", 5);

		username = props.getString("dbuser");
		password = props.getString("dbpassword");
	}

	public Connection getConnection()
	{
		long threadId = Thread.currentThread().getId();

		while (true) {
			synchronized (freeConnections) {

				if (freeConnections.size() != 0) { // free connection is available 
					Connection conn = freeConnections.get(0);
					freeConnections.remove(0);

					try {
						conn.setAutoCommit(true);
					} catch (SQLException e) {
						logger.error("getConnection failed, the connection is closed or part of a transaction : " + e.toString());
						e.printStackTrace();
					}
					return conn;
				}
				try {
					logger.debug("thread " + threadId + " waiting for connection to become free");
					freeConnections.wait();
				} catch (InterruptedException e) {
					logger.debug("thread " + threadId + " done waiting");
				}
			}
		}
	}

	public void returnConnection(Connection c)
	{
		if (null == c) // ignore invalid value
			return;
		
		if (!allConnections.contains(c)) {
			logger.error("attempt to return a connection no from the connectionpool");
			return;
		}
		synchronized (freeConnections) {
			if (freeConnections.contains(c)) {
				logger.error("duplicate attempt to return a connection ");
				return;
			}
			
			freeConnections.add(c);
			freeConnections.notifyAll();
			return;
		}
	}

	public void closeAllconnections()
	{
		for (Connection conn : freeConnections) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error("failed to close connection " + conn + " : " + e.toString());
			}
		}
		logger.info("all connections are closed");
	}

}

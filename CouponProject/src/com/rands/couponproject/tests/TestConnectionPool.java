package com.rands.couponproject.tests;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.rands.couponproject.ConnectionPool;

public class TestConnectionPool {
	static Logger logger = Logger.getLogger(TestConnectionPool.class);
	static ConnectionPool pool;

	public static void main(String[] args) {

		logger.debug("testing the connectionpool");
		try {
			pool = ConnectionPool.getInstance();
		} catch (Exception e) {
			logger.error("cannot get connection pool instance : " + e.toString());
			return;
		}

		List<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < 6; i++) {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					long threadId = Thread.currentThread().getId();

					logger.debug("thread " + threadId + " requesting connection");
					Connection conn = pool.getConnection();
					logger.debug("thread " + threadId + " got connection and going for a 10 sec sleep");

					try {
						Thread.sleep(10 * 1000); // 10 seconds
					} catch (InterruptedException e) {
					}
					logger.debug("thread " + threadId + " returning the connection");
					pool.returnConnection(conn);

				}

			});
			threads.add(t);
			t.start();
		}

		for (Thread t : threads) { // wait for all threads to finish
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}
		logger.debug("test is done");

	}

}

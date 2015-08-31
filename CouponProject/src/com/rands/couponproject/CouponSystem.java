package com.rands.couponproject;

import org.apache.log4j.Logger;

import com.rands.couponproject.facede.AdminFacade;
import com.rands.couponproject.facede.CompanyFacade;
import com.rands.couponproject.facede.CouponClientFacade;
import com.rands.couponproject.facede.CustomerFacade;
import com.rands.couponproject.model.ClientType;

public class CouponSystem {

	static Logger logger = Logger.getLogger(CouponSystem.class);

	// a singleton is implemented by :
	//	1) a private constructor
	//	2) a public static method (normaly called getInstance) that is responsible for creating the singleton object
	//		the first time it is being called and to return that object on every call.
	//  3) a private static (reference) object (the singleton)

	private static CouponSystem singleton = null; // the singleton (reference) object

	/**
	 * getInstance - created the singleton on the 1st call.
	 * 
	 * @return - the singleton
	 * @throws Exception
	 */
	public static /*synchronized*/ CouponSystem getInstance() throws Exception {

		if (singleton == null)
		{
			synchronized (CouponSystem.class) {
				if (singleton == null)
					singleton = new CouponSystem();
			}
		}
		return singleton;

	}

	// the private constructor
	private CouponSystem() {
		Thread t = new DailyCouponExpirationTask();
		t.start();
	}

	public CouponClientFacade login(String name, String password, ClientType clientType) throws Exception {

		CouponClientFacade facade = null;
		switch (clientType) {

		case ADMIN:
			facade = AdminFacade.login(name, password, clientType);
			break;
			
		case COMPANY:
			facade = CompanyFacade.login(name, password, clientType);

			break;
		case CUSTOMER:
			facade = CustomerFacade.login(name, password, clientType);
			break;
		}
		
		if (null == facade){
			logger.error("login faild clientType = " + clientType + "name =" + name  );
			throw new Exception("LoginFailed");
		}
		return facade;
	}

}

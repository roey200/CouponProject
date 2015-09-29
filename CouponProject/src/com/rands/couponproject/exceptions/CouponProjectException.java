package com.rands.couponproject.exceptions;

public class CouponProjectException extends Exception {

	public CouponProjectException(String message) {
		super(message);
	}

	public static class CouponException extends CouponProjectException {

		public CouponException(String message) {
			super(message);
		}
		
	}

	public static class CompanyException extends CouponProjectException {

		public CompanyException(String message) {
			super(message);
		}
		
	}

	public static class CustomerException extends CouponProjectException {

		public CustomerException(String message) {
			super(message);
		}
		
	}

	public static class LoginException extends CouponProjectException {

		public LoginException(String message) {
			super(message);
		}
		
	}
	
	public static class AccessForbiddenException extends LoginException {

		public AccessForbiddenException(String message) {
			super(message);
		}
		
	}
	
	public static class AdminLoginException extends LoginException {

		public AdminLoginException(String message) {
			super(message);
		}
		
	}
	

	public static class CompanyLoginException extends LoginException {

		public CompanyLoginException(String message) {
			super(message);
		}
		
	}

	public static class CustomerLoginException extends LoginException {

		public CustomerLoginException(String message) {
			super(message);
		}
		
	}


}

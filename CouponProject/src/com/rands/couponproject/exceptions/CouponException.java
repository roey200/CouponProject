package com.rands.couponproject.exceptions;

public class CouponException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8624400600786602804L;

	public CouponException(String message){
		super(message);
	}
	
	public static class CouponException1 extends CouponException {

		public CouponException1(String message) {
			super(message);
		}
		
	}


}

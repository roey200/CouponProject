package com.rands.couponproject.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.rands.couponproject.exceptions.CouponProjectException.CompanyException;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;

/**
 * CouponProjectExceptionMapper - an ExceptionMapper.<br><br>
 * 
 * The ExceptionMapper is used to map (translate) Exception(s) to a proper servlet Response.
 * That response can then be handled by the client (the web service client).
 *
 */
@Provider
public class CouponProjectExceptionMapper implements ExceptionMapper<Exception> {
	
	public CouponProjectExceptionMapper() {
		super();
        System.out.println("CouponProjectExceptionMapper created");
    }

	@Override
	public Response toResponse(Exception e) {
		//return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type("text/plain").build();
		

		if (e instanceof LoginException) {
			return Response.status(Response.Status.UNAUTHORIZED).entity(e.toString()).type("text/plain").build();
			
		}

		if (e instanceof CompanyException) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.toString()).type("text/plain").build();
			
		}
		
		
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).type("text/plain").build();
	}
}

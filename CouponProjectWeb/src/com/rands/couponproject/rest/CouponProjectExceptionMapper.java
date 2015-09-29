package com.rands.couponproject.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.rands.couponproject.exceptions.CouponProjectException.CompanyException;
import com.rands.couponproject.exceptions.CouponProjectException.CustomerException;
import com.rands.couponproject.exceptions.CouponProjectException.LoginException;
import com.sun.jersey.api.NotFoundException;

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
		
		// default
		Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

		// CouponProject exception
		if (e instanceof LoginException) {
			status = Response.Status.UNAUTHORIZED;
		} 
		else if (e instanceof CompanyException) {
			status = Response.Status.BAD_REQUEST;
		} 
		else if (e instanceof CustomerException) {
			status = Response.Status.BAD_REQUEST;
		} 
		// jersey exception
		else if (e instanceof NotFoundException) {
			status = Response.Status.NOT_FOUND;
		}
		// json exceptions
		else { // check by class name 
			String name = e.getClass().getSimpleName();
		    if (name.equals("JsonMappingException")) {
		    	status = Response.Status.BAD_REQUEST;
		    }
		    else if (name.equals("UnrecognizedPropertyException")) {
		    	status = Response.Status.BAD_REQUEST;
		    }
		}
		
		return Response.status(status).entity(e.toString()).type("text/plain").build();
	}
}

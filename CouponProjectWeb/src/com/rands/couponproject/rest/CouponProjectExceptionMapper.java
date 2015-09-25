package com.rands.couponproject.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CouponProjectExceptionMapper implements ExceptionMapper<Exception> {

	public Response toResponse(Exception e) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type("text/plain").build();
	}
}
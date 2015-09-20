package com.rands.couponproject.rest.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


//Sets the path to base URL + /company
@Path("/company")
public class CompanyService {


	@Path("/hello/{name}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello(@PathParam("name") String companyName) {
		
		return "hello company :  " + companyName;
		
	}


} 
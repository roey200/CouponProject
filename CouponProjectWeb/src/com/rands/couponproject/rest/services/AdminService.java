package com.rands.couponproject.rest.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.rands.couponproject.model.Company;

//Sets the path to base URL + /admin
@Path("/admin")
public class AdminService {

	@Path("/hello")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {

		return "Hello Admin  ";

	}
	
	@Path("/company/{name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Company produceJSON( @PathParam("name") String companyName ) {

		Company c = new Company();
		c.setId(1);
		c.setCompanyName(companyName);
		c.setEmail("info@" + companyName + ".com");
		c.setPassword("1234");
		
		return c;

	}
	

}
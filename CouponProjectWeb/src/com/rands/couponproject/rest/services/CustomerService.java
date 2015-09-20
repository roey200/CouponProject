package com.rands.couponproject.rest.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


//Sets the path to base URL + /customer
@Path("/customer")
public class CustomerService {


@Path("/hello/{name}")
@GET
@Produces(MediaType.TEXT_PLAIN)
public String hello(@PathParam("name") String customerName) {
	
	return "hello customer :  " + customerName;
	
}

} 
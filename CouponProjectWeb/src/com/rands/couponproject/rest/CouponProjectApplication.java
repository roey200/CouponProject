package com.rands.couponproject.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.rands.couponproject.rest.services.AdminService;
import com.rands.couponproject.rest.services.CompanyService;
import com.rands.couponproject.rest.services.CustomerService;

import java.util.HashSet;
import java.util.Set;

//@ApplicationPath("/")
//@ApplicationPath("") // same
public class CouponProjectApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        
        // register root resource
        classes.add(AdminService.class);
        //classes.add(CompanyService.class);
        classes.add(CustomerService.class);

        return classes;
    }

}

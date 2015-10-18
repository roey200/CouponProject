package com.rands.couponproject.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.rands.couponproject.rest.services.AdminService;
import com.rands.couponproject.rest.services.CompanyService;
import com.rands.couponproject.rest.services.CustomerService;
import com.rands.couponproject.rest.services.LoginLogOutService;
import com.rands.couponproject.rest.services.UploadService;

import java.util.HashSet;
import java.util.Set;

//@ApplicationPath("/")
//@ApplicationPath("") // same
@ApplicationPath("/rest")
public class CouponProjectApplication extends Application {
	
	/**
	 * Registers the classes that should be used by jersey.
	 * This is needed when using deployment agnostic application model.
	 */
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        
        // register root resource
        classes.add(LoginLogOutService.class);

        classes.add(AdminService.class);
        classes.add(CompanyService.class);
        classes.add(CustomerService.class);

        classes.add(UploadService.class);
        
        // need to add ExceptionMapper class as well
        classes.add(CouponProjectExceptionMapper.class);        

        return classes;
    }

}

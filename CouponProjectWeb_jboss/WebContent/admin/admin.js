var app = angular.module('adminApp', ['ngRoute']);

//disable http get caching (for internet explorer)
app.config(function($httpProvider){
	  $httpProvider.defaults.headers.common['Cache-Control'] = 'no-cache';
	  $httpProvider.defaults.cache = false;

	  if (!$httpProvider.defaults.headers.get) {
	      $httpProvider.defaults.headers.get = {};
	  }
	  $httpProvider.defaults.headers.get['If-Modified-Since'] = '0';
});

// configure our routes
app.config(['$routeProvider' ,function($routeProvider) {
	$routeProvider

		// route for the customers list
		.when('/', {
			templateUrl : 'customers.html',
			controller  : 'CustomerController as customerController'
		})

		// route for the customers list 
		.when('/customers', {
			templateUrl : 'customers.html',
			controller  : 'CustomerController as customerController'
		})

		// route for the companies list 
		.when('/companies', {
			templateUrl : 'companies.html',
			controller  : 'CompanyController as companyController'
		})
	
		// route for the payments list 
		.when('/payments', {
			templateUrl : 'payments.html',
			controller  : 'PaymentController as paymentController'
		});
	
}]);

//create the controller for the nav bar and inject Angular's $scope and $location
app.controller('navController', function($scope,$location,AuthService) {
	// highlight the selected item from the navbar
    $scope.isActive = function (viewLocation) { 
        return viewLocation === $location.path();
    };
    //performs log-out
    $scope.logout = function () {
        AuthService.logout();
    };
});

// the controller for the customer template
app.controller('CustomerController',['AdminService','$window', function(AdminService,$window) {
	
	this.id = 0;
	this.customerName = '';
	this.passw1 = '';
	this.passw2 = '';
	this.customers = [];

	this.create = false;
	this.edit = true;
	this.error = false;
	this.incomplete = false; 

	/* editCustomer : handles the id,customerName,passw1,passw2 and create fields.
	 * when the Create new Customer button is clicked we call this function with 'new' as the parameter.
	 * when the Edit button is clicked we call this function with the row index ($index) of the customer.
	 * the create field marks the requested operation (create/update).
	 */
	this.editCustomer = function(indx) {
		if ( indx == 'new') { // the Create new Customer button was clicked
			this.create = true;
			this.edit = true;
			this.incomplete = true;
			
			this.id = 0;
			this.customerName = '';
			this.passw1 = '';
			this.passw2 = '';			
		} else { // the edit (customer row) button was clicked
			this.create = false;
			this.edit = false;
			
			var customer = this.customers[indx];
			this.id = customer.id;
			this.customerName = customer.customerName;
			this.passw1 = customer.password;
			this.passw2 = customer.password;
		}
		this.scrollTo('bottom');		
	};

	/* watch : checks if the save changes button may by clicked. it checks that : customerName is not empty,
	 * passw1 and passw2 are equal and not empty.
	 */
	this.watch = function() {
		if (!this.customerName.length)
			return false;
		if (!this.passw1.length || this.passw1 !== this.passw2)
			return false;
		return true;
	};
	
	/*
	 * saveChanges : performs the create/update customer.
	 * note that we pass this to the AdminService functions so that when can refresh the customers list after the
	 * $http (asynchronous) call finishes. 
	 */
	this.saveChanges = function() {
		//this.test();
		
		// create a customer object from the fields in the form
		var customer = {'id':this.id,'customerName': this.customerName,'password':this.passw1,'coupons':[]};
		if (this.create) {
			AdminService.createCustomer(this,customer);
		} else {
			AdminService.updateCustomer(this,customer);
		}
	};
	
	this.removeCustomer = function(indx){
		var id = this.customers[indx].id;
		AdminService.removeCustomer(this , id);
		
	}

	/* refresh : refreshes the customer list (by calling getCustomers). this function should be called after every 
	 * change that was made by the rest services.
	 */
	this.refresh = function() {
		AdminService.getCustomers(this);
	}
	
	this.scrollTo = function(where) {
		if ('top' == where)
			$window.scrollTo(0,0);
		else if ('bottom' == where)
			$window.scrollTo(0,document.body.scrollHeight);
	}

	// refresh the customers list
	this.refresh();

}]);

app.controller('CompanyController',['AdminService','$window', function(AdminService,$window) {
	
	this.id = 0;
	this.companyName = '';
	this.email = '';
	this.passw1 = '';
	this.passw2 = '';
	this.companies = [];

	this.create = false;
	this.edit = true;
	this.error = false;
	this.incomplete = false; 

	/* editCustomer : handles the id,companyName,passw1,passw2 and create fields.
	 * when the Create new Customer button is clicked we call this function with 'new' as the parameter.
	 * when the Edit button is clicked we call this function with the row index ($index) of the company.
	 * the create field marks the requested operation (create/update).
	 */
	this.editCompany = function(indx) {
		if ( indx == 'new') { // the Create new Customer button was clicked
			this.create = true;
			this.edit = true;
			this.incomplete = true;
			
			this.id = 0;
			this.companyName = '';
			this.email = ''
			this.passw1 = '';
			this.passw2 = '';			
		} else { // the edit (company row) button was clicked
			this.create = false;
			this.edit = false;
			
			var company = this.companies[indx];
			this.id = company.id;
			this.companyName = company.companyName;
			this.email = company.email;
			this.passw1 = company.password;
			this.passw2 = company.password;
		}
		this.scrollTo('bottom');
	};

	/* watch : checks if the save changes button may by clicked. it checks that : companyName and email are not empty,
	 * passw1 and passw2 are equal and not empty.
	 */
	this.watch = function() {
		if (!this.companyName.length)
			return false;
		if (!this.email.length)
			return false;
		if (!this.passw1.length || this.passw1 !== this.passw2)
			return false;
		return true;
	};
	
	/*
	 * saveChanges : performs the create/update company.
	 * note that we pass this to the AdminService functions so that when can refresh the companies list after the
	 * $http (asynchronous) call finishes. 
	 */
	this.saveChanges = function() {
		//this.test();
		
		// create a company object from the fields in the form
		var company = {'id':this.id,'companyName': this.companyName,'email': this.email,'password':this.passw1,'coupons':[]};
		if (this.create) {
			AdminService.createCompany(this,company);
		} else {
			AdminService.updateCompany(this,company);
		}
	};
	
	this.removeCompany = function(indx){
		var id = this.companies[indx].id;
		AdminService.removeCompany(this , id);
		
	}

	/* refresh : refreshes the company list (by calling getCustomers). this function should be called after every 
	 * change that was made by the rest services.
	 */
	this.refresh = function() {
		AdminService.getCompanies(this);
	}
	
	this.scrollTo = function(where) {
		if ('top' == where)
			$window.scrollTo(0,0);
		else if ('bottom' == where)
			$window.scrollTo(0,document.body.scrollHeight);
	}

	// refresh the companies list
	this.refresh();

}]);

//the controller for the payment template
app.controller('PaymentController',['PaymentService','$window', function(PaymentService,$window) {
	
	this.payments = [];
	this.fromType = '';
	this.fromName = '';

	/* refresh : refreshes the payments list. this function should be called after every 
	 * change that was made by the rest services.
	 */
	this.refresh = function() {
		this.fromType = '';
		this.fromName = '';
		
		this.search();

	}
	
	this.search = function() {
		//alert("search");
		if (this.fromType == 'ALL') {
			PaymentService.getAllPayments(this);
		}
		else if (this.fromType == 'CUSTOMER') {
			//alert('serach by customer ' + this.fromName);
			PaymentService.getCustomerPayments(this,this.fromName);
		} else if (this.fromType == 'COMPANY') {
			//alert('serach by company ' + this.fromName);
			PaymentService.getCompanyPayments(this,this.fromName);
		}
		else {
			//alert('serach all');
			PaymentService.getAllPayments(this);
		}
	}	
	
	
	this.scrollTo = function(where) {
		if ('top' == where)
			$window.scrollTo(0,0);
		else if ('bottom' == where)
			$window.scrollTo(0,document.body.scrollHeight);
	}

	// refresh the customers list
	this.refresh();

}]);

// services

/* AuthService : a collection of functions that uses the rest authentication services.
 */
app.service('AuthService', ['$http','$window' ,function($http,$window) {
	
	// logout : terminates the session and redirects to the login page
	this.logout = function() {
    	//alert('logout req');

		$http({
			method: 'POST',
			url: '/CouponProjectWeb/rest/logout',
		})
		.success(function(data, status, headers, config) { // redirect to login page
			//alert('logout OK ' + $window);
	    	$window.location.href = '/CouponProjectWeb/login.html';
		})
		.error(function(data, status, headers, config) {
			//alert('logout NOTTTTTTTTTTT OK ' + $window);
	    	$window.location.href = '/CouponProjectWeb/login.html';
		})

	};
}]);

/* AdminService : a collection of functions that call the rest services.
 * note that since the $http call is an asynchronous call. we pass a adminCtrl to each of thees functions so that
 * we can refresh the customers list in the adminCtrl.
 */
app.service('AdminService', ['$http' ,function($http) {
	// customers
	
	// getCustomers : gets all the customers. (this function is also used to refresh the list).
	this.getCustomers = function(adminCtrl) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/admin/customers',
//			data: encodedString,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			adminCtrl.customers = data;
		})
		.error(function(data, status, headers, config) {
			console.log("getCustomers failed data=" + data + " status=" + status);
			alert("getCustomers failed status=" + status + " : " + data);
			/*
			console.log("data=" + data + " status=" + status);
			if (status == 401)
				$scope.errorMsg = "Login not correct";
			else
				$scope.errorMsg = 'Unable to submit form';
				*/
			
		})

	};

	// createCustomer : creates a customer
	this.createCustomer = function(adminCtrl,customer) {
		
		$http({
			method: 'POST',
			url: '/CouponProjectWeb/rest/admin/customer',
			data: customer,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			adminCtrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("createCustomer failed status=" + status + " : " + data);
			
		})

	};

	// updateCustomer : updates a customer
	this.updateCustomer = function(adminCtrl,customer) {
		
		$http({
			method: 'PUT',
			url: '/CouponProjectWeb/rest/admin/customer',
			data: customer,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			adminCtrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("updateCustomer failed status=" + status + " : " + data);
		})

	};
	
	// removeCustomer : remove a customer
	this.removeCustomer = function(adminCtrl,id) {
		
		$http({
			method: 'DELETE',
			url: '/CouponProjectWeb/rest/admin/customer/'+ id
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			adminCtrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("removeCustomer failed status=" + status + " : " + data);
		})

	};
	
	
	// companies
	
	// getCompanies : gets all the companies. (this function is also used to refresh the list).
	this.getCompanies = function(adminCtrl) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/admin/companies',
//			data: encodedString,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			adminCtrl.companies = data;
		})
		.error(function(data, status, headers, config) {
			alert("getCompanies failed status=" + status + " : " + data);
			/*
			console.log("data=" + data + " status=" + status);
			if (status == 401)
				$scope.errorMsg = "Login not correct";
			else
				$scope.errorMsg = 'Unable to submit form';
				*/
			
		})

	};

	// createCompany : creates a company
	this.createCompany = function(adminCtrl,company) {
		
		$http({
			method: 'POST',
			url: '/CouponProjectWeb/rest/admin/company',
			data: company,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			adminCtrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("createCompany failed status=" + status + " : " + data);
		})

	};

	// updateCompany : updates a company
	this.updateCompany = function(adminCtrl,company) {
		
		$http({
			method: 'PUT',
			url: '/CouponProjectWeb/rest/admin/company',
			data: company,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			adminCtrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("updateCompany failed status=" + status + " : " + data);
		})

	};
	
	// removeCompany : remove a company
	this.removeCompany = function(adminCtrl,id) {
		
		$http({
			method: 'DELETE',
			url: '/CouponProjectWeb/rest/admin/company/'+ id
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			adminCtrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("removeCompany failed status=" + status + " : " + data);
		})

	};	
	
}]);

//
/* PaymentService : a collection of functions that call the rest services.
 * note that since the $http call is an asynchronous call. we pass a adminCtrl to each of thees functions so that
 * we can refresh the payments list in the adminCtrl.
 */
app.service('PaymentService', ['$http' ,function($http) {
	// customers
	
	// getAllPayments : gets all the payments. (this function is also used to refresh the list).
	this.getAllPayments = function(adminCtrl) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/payments/all',
//			data: encodedString,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			adminCtrl.payments = data;
		})
		.error(function(data, status, headers, config) {
			console.log("getAllPayments failed data=" + data + " status=" + status);
			alert("getAllPayments failed status=" + status + " : " + data);
			/*
			console.log("data=" + data + " status=" + status);
			if (status == 401)
				$scope.errorMsg = "Login not correct";
			else
				$scope.errorMsg = 'Unable to submit form';
				*/
			
		})

	};
	
	this.getCustomerPayments = function(adminCtrl,name) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/payments/customer/' + name,
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			adminCtrl.payments = data;
		})
		.error(function(data, status, headers, config) {
			console.log("getCustomerPayments failed data=" + data + " status=" + status);
			alert("getCustomerPayments failed status=" + status + " : " + data);
		})

	};

	this.getCompanyPayments = function(adminCtrl,name) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/payments/company/' + name,
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			adminCtrl.payments = data;
		})
		.error(function(data, status, headers, config) {
			console.log("getCompanyPayments failed data=" + data + " status=" + status);
			alert("getCompanyPayments failed status=" + status + " : " + data);
		})

	};
	
}]);

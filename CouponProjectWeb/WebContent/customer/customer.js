var app = angular.module('customerApp', ['ngRoute']);

// configure our routes
app.config(['$routeProvider' ,function($routeProvider) {
	$routeProvider
	
	// route for the coupons list
	.when('/', {
		templateUrl : 'coupons.html',
		controller  : 'CouponController as couponController'
	})	

	// route for the customer coupons list 
	.when('/coupons', {
		templateUrl : 'coupons.html',
		controller  : 'CouponController as couponController'
	})

	// route for the customer available coupons to buy 
	.when('/buy', {
		templateUrl : 'buy.html',
		controller  : 'CouponController as couponController'
	})	
	// route for the customer settings 
	.when('/settings', {
		templateUrl : 'settings.html',
		controller  : 'CustomerController as customerController'
	});
	
}]);

//// create the controller and inject Angular's $scope
//app.controller('mainController', function($scope) {
//	// create a message to display in our view
//	$scope.message = '123 Testing';
//});

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
app.controller('CustomerController',['CustomerService','$window', function(CustomerService,$window) {
	
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
	 * note that we pass this to the CustomerService functions so that when can refresh the customers list after the
	 * $http (asynchronous) call finishes. 
	 */
	this.saveChanges = function() {
		//this.test();
		
		// create a customer object from the fields in the form
		var customer = {'id':this.id,'customerName': this.customerName,'password':this.passw1,'coupons':[]};
		if (this.create) {
			CustomerService.createCustomer(this,customer);
		} else {
			CustomerService.updateCustomer(this,customer);
		}
	};
	
	this.removeCustomer = function(indx){
		var id = this.customers[indx].id;
		CustomerService.removeCustomer(this , id);
		
	}

	/* refresh : refreshes the coupons list (by calling getAllPurchasedCoupons).
	 */
	this.refresh = function() {
		alert("refresh");
		CustomerService.getAllPurchasedCoupons(this);
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

app.controller('CouponController',['CustomerService','$window', function(CustomerService,$window) {
	this.id = 0;
	this.title = '';
	this.startDate = '';
	this.endDate = '';
	this.image = '';
	this.massage = '';
	this.coupons = [];

	/* refresh : refreshes the coupons list (by calling getAllPurchasedCoupons).
	 */
	this.refresh = function() {
		CustomerService.getAllPurchasedCoupons(this);
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


// services

/* AuthService : a collection of functions that call the rest services.
 * note that since the $http call is an asynchronous call. we pass a customerCtrl to each of thees functions so that
 * we can refresh the customers list in the customerCtrl.
 */
app.service('AuthService', ['$http','$location' ,function($http,$location) {
	
	// logout : terminates the session
	this.logout = function() {
    	alert('logout req');

		$http({
			method: 'POST',
			url: '/CouponProjectWeb/rest/logout',
		})
		.success(function(data, status, headers, config) {
	    	alert('logout OKKKKK');
	        $location.path('/login.html');

		})
		.error(function(data, status, headers, config) {
			alert("logout NNNNNNNNNNNNNNNNNNNNOT ok");
		})

	};
}]);

/* CustomerService : a collection of functions that call the rest services.
 * note that since the $http call is an asynchronous call. we pass a customerCtrl to each of thees functions so that
 * we can refresh the customers list in the customerCtrl.
 */
app.service('CustomerService', ['$http' ,function($http) {
	// customers
	
	// getAllPurchasedCoupons : gets all the coupons that the customer bought. 
	this.getAllPurchasedCoupons = function(customerCtrl) {
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/coupons',

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			customerCtrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
		})
	};
	
	// getAllPurchasedCouponsByType : gets all the coupons that the customer bought by type. 
	this.getAllPurchasedCouponsByType = function(customerCtrl , couponType) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/coupons/' + couponType,

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			customerCtrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
		})

	};
	
	// getAllPurchasedCouponsByPrice : gets all the coupons that the customer bought by price. 
	this.getAllPurchasedCouponsByPrice = function(customerCtrl , price) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/coupons/' + price,

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			customerCtrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
		})

	};
	
	// getPurchableCoupons : gets all the coupons that the customer can buy. 
	this.getPurchableCoupons = function(customerCtrl) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/buylist',

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			customerCtrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
		})
	};
	
	
	this.purchaseCoupon = function(couponCtrl,id) {
		
		$http({
			method: 'POST',
			url: '/CouponProjectWeb/rest/customer/buy/'+ id
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			couponCtrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("purchaseCoupon failed status=" + status);
		})

	};
	
	
	
//////////////not implemented yet

	// updateCustomer : updates a customer
	this.updateCustomer = function(customerCtrl,customer) {
		
		$http({
			method: 'PUT',
			url: '/CouponProjectWeb/rest/customer/customer',
			data: customer,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			customerCtrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("updateCustomer failed status=" + status);
		})

	};
	
	
	
}]);
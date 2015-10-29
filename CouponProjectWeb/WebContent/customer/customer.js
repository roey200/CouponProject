var app = angular.module('customerApp', ['ngRoute','ngFileUpload']);

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
		controller  : 'PurchaseController as purchaseController'
	})	
	// route for the customer settings 
	.when('/settings', {
		templateUrl : 'settings.html',
		controller  : 'CustomerController as customerController'
	});
	
}]);

//create the controller for the nav bar and inject Angular's $scope and $location
app.controller('navController', function($scope,$location,AuthService,CustomerService) {
	// highlight the selected item from the navbar
    $scope.isActive = function (viewLocation) { 
        return viewLocation === $location.path();
    };
    //performs log-out
    $scope.logout = function () {
        AuthService.logout();
    };
    
    // get the current customer (it will be shown in the nav bar as : hello <name>
    CustomerService.getCurrentCustomer($scope);
    
});

// the controller for the customer template
app.controller('CustomerController',['CustomerService','$window', function(CustomerService,$window) {
	
	this.id = 0;
	this.customerName = '';
	this.passw1 = '';
	this.passw2 = '';
	
	this.error = false;
	this.incomplete = false; 

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

		CustomerService.updateCurrentCustomer(this,customer);
	};
	
	/* refresh : refreshes the coupons list (by calling getAllPurchasedCoupons).
	 */
	this.refresh = function() {
		//alert("refresh");
		CustomerService.getCurrentCustomer(this);
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

app.controller('CouponController',['CustomerService','$window','Upload', function(CustomerService,$window,Upload) {

	this.coupons = [];
	this.couponTypes = [];
	
	Object.defineProperty(this,'couponType', { // clear price and endDate when type is set
		  get: function() {
		    return this._couponType;
		  },
		  set: function(value) {
			  this._couponType = value;
			  this._couponPrice = '';
			  this._endDate = '';
		  }
		});
	Object.defineProperty(this,'couponPrice', { // clear type and endDate when price is set
		  get: function() {
		    return this._couponPrice;
		  },
		  set: function(value) {
			  this._couponPrice = value;
			  this._couponType = '';
			  this._endDate = '';
		  }
		});
	Object.defineProperty(this,'endDate', { // clear type and price when date is set
		  get: function() {
		    return this._endDate;
		  },
		  set: function(value) {
			  this._endDate = value;
			  this._couponPrice = '';
			  this._couponType = '';
		  }
		});
	
	
	this.couponType = '';
	this.couponPrice = '';
	this.endDate = '';

	/* refresh : refreshes the coupons list (by calling getAllPurchasedCoupons... ).
	 */
	this.refresh = function() {
		this.couponType = '';
		this.couponPrice = '';
		this.endDate = '';
		
		this.search();

//		if (this.couponType.length) {
//			//alert('serach by type ' + this.couponType);
//			CustomerService.getAllPurchasedCouponsByType(this,this.couponType);
//		}
//		else if (angular.isNumber(this.couponPrice)) {
//			//alert('serach by price ' + this.couponPrice);
//			CustomerService.getAllPurchasedCouponsByPrice(this,this.couponPrice);
//		} else if (this.endDate) {
//			//alert('serach by date ' + this.endDate);
//			CustomerService.getAllPurchasedCouponsByDate(this,this.endDate);
//		}
//		else {
//			//alert('serach all');
//			CustomerService.getAllPurchasedCoupons(this);
//		}
	}
	
//	this.search = function() {
//		this.refresh(this);
//	}
	
	this.search = function() {
		
		if (this.couponType.length) {
			//alert('serach by type ' + this.couponType);
			CustomerService.getAllPurchasedCouponsByType(this,this.couponType);
		}
		else if (angular.isNumber(this.couponPrice)) {
			//alert('serach by price ' + this.couponPrice);
			CustomerService.getAllPurchasedCouponsByPrice(this,this.couponPrice);
		} else if (this.endDate) {
			//alert('serach by date ' + this.endDate);
			CustomerService.getAllPurchasedCouponsByDate(this,this.endDate);
		}
		else {
			//alert('serach all');
			CustomerService.getAllPurchasedCoupons(this);
		}
	}	

	this.scrollTo = function(where) {
		if ('top' == where)
			$window.scrollTo(0,0);
		else if ('bottom' == where)
			$window.scrollTo(0,document.body.scrollHeight);
	}
	
	this.onFileSelect = function($files) {
		alert('onFileSelect $files = ' + $files);
		//$files: an array of files selected, each file has name, size, and type.
		for (var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			alert('file=' + $file.name);
			Upload.upload({
				url : 'my/upload/url',
				file : $file,
				progress : function(e) {
				}
			}).then(function(data, status, headers, config) {
				// file is uploaded successfully
				console.log(data);
			});
		}
	}	

	CustomerService.getCouponTypes(this);
	// refresh the companies list
	this.refresh();

}]);



app.controller('PurchaseController',['CustomerService','$window','Upload', function(CustomerService,$window,Upload) {

	this.coupons = [];
	this.couponTypes = [];
	
	Object.defineProperty(this,'couponType', { // clear price and endDate when type is set
		  get: function() {
		    return this._couponType;
		  },
		  set: function(value) {
			  this._couponType = value;
			  this._couponPrice = '';
			  this._endDate = '';
		  }
		});
	Object.defineProperty(this,'couponPrice', { // clear type and endDate when price is set
		  get: function() {
		    return this._couponPrice;
		  },
		  set: function(value) {
			  this._couponPrice = value;
			  this._couponType = '';
			  this._endDate = '';
		  }
		});
	Object.defineProperty(this,'endDate', { // clear type and price when date is set
		  get: function() {
		    return this._endDate;
		  },
		  set: function(value) {
			  this._endDate = value;
			  this._couponPrice = '';
			  this._couponType = '';
		  }
		});
	
	
	this.couponType = '';
	this.couponPrice = '';
	this.endDate = '';

	/* refresh : refreshes the coupons list (by calling getAllPurchasedCoupons... ).
	 */
	this.refresh = function() {
		this.couponType = '';
		this.couponPrice = '';
		this.endDate = '';
		
		this.search();

	}
	
	this.search = function() {
		//alert("search");
		if (this.couponType.length) {
			alert('serach by type ' + this.couponType);
			CustomerService.getPurchableCouponsByType(this,this.couponType);
		}
		else if (angular.isNumber(this.couponPrice)) {
			//alert('serach by price ' + this.couponPrice);
			CustomerService.getPurchableCouponsByPrice(this,this.couponPrice);
		} else if (this.endDate) {
			//alert('serach by date ' + this.endDate);
			CustomerService.getPurchableCouponsByDate(this,this.endDate);
		}
		else {
			//alert('serach all');
			CustomerService.getPurchableCoupons(this);
		}
	}	

	this.scrollTo = function(where) {
		if ('top' == where)
			$window.scrollTo(0,0);
		else if ('bottom' == where)
			$window.scrollTo(0,document.body.scrollHeight);
	}	

	this.purchaseCoupon = function(indx){
		var coupon = this.coupons[indx];
		CustomerService.purchaseCoupon(this , coupon);
		
	}

	
	CustomerService.getCouponTypes(this);
	// refresh the companies list
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

/* CustomerService : a collection of functions that call the rest services.
 * note that since the $http call is an asynchronous call. we pass a ctrl to each of thees functions so that
 * we can refresh the customers list in the ctrl.
 */
app.service('CustomerService', ['$http' ,function($http) {
	// customers
	
	// getCouponTypes : gets the types of coupons that are defined in the system. 	
	this.getCouponTypes = function(ctrl) {
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/coupontypes',

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			ctrl.couponTypes = data;
			//ctrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("getCouponTypes failed status=" + status + " : " + data);
		})		
		
	}
	
	
	
	// getCurrentCustomer : gets the current customer. 
	this.getCurrentCustomer = function(ctrl) {
		//alert('getCurrentCustomer');
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/current',

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			//ctrl.customer = data;
			ctrl.id = data.id;
			ctrl.customerName = data.customerName;
			ctrl.passw1 = data.password;
			ctrl.passw2 = data.password;
		})
		.error(function(data, status, headers, config) {
			alert("getCurrentCustomer failed status=" + status + " : " + data);
		})
	};

	// updateCustomer : updates a customer
	this.updateCurrentCustomer = function(ctrl,customer) {
		
		$http({
			method: 'PUT',
			url: '/CouponProjectWeb/rest/customer/current',
			data: customer,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			ctrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("updateCustomer failed status=" + status + " : " + data);
		})

	};	
	
	// getAllPurchasedCoupons : gets all the coupons that the customer bought. 
	this.getAllPurchasedCoupons = function(ctrl) {
		//alert('getAllPurchasedCoupons');
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/coupons',

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
			alert("getAllPurchasedCoupons failed status=" + status + " : " + data);
		})
	};
	
	// getAllPurchasedCouponsByType : gets all the coupons that the customer bought by type. 
	this.getAllPurchasedCouponsByType = function(ctrl , couponType) {
		//alert('getAllPurchasedCouponsByType=' + couponType);

		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/coupons/' + couponType,

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
			alert("getAllPurchasedCouponsByType failed status=" + status + " : " + data);
		})

	};
	
	// getAllPurchasedCouponsByPrice : gets all the coupons that the customer bought by price. 
	this.getAllPurchasedCouponsByPrice = function(ctrl , price) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/coupons/' + price,

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
			alert("getAllPurchasedCouponsByPrice failed status=" + status + " : " + data);
		})

	};
	
	// getAllPurchasedCouponsByDate : gets all the coupons that the customer bought by price. 
	this.getAllPurchasedCouponsByDate = function(ctrl , date) {
		//alert("getAllPurchasedCouponsByDate=" + date);
		dt = formattedDate(date);
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/coupons/date/' + dt,

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
			alert("getAllPurchasedCouponsByDate failed status=" + status + " : " + data);
		})

	};
	
	
	// getPurchableCoupons : gets all the coupons that the customer can buy. 
	this.getPurchableCoupons = function(ctrl) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/buylist',

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
			alert("getPurchableCoupons failed status=" + status + " : " + data);
		})
	};
	

	// getPurchableCouponsByType : gets all the coupons that the customer can buy by type 
	this.getPurchableCouponsByType = function(ctrl , couponType) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/buylist/' + couponType,

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
			alert("getPurchableCouponsByType failed status=" + status + " : " + data);
		})
	};
	
	// getPurchableCouponsByPrice : gets all the coupons that the customer can buy by price.
	this.getPurchableCouponsByPrice = function(ctrl , couponPrice) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/buylist/' + couponPrice,

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
			alert("getPurchableCouponsByPrice failed status=" + status + " : " + data);
		})
	};

	// getPurchableCouponsByDate : gets all the coupons that the customer can buy by date 
	this.getPurchableCouponsByDate = function(ctrl , endDate) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/customer/buylist/' + endDate,

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
			alert("getPurchableCouponsByDate failed status=" + status + " : " + data);
		})
	};
	
	this.purchaseCoupon = function(ctrl,coupon) {
		
		$http({
			method: 'POST',
			url: '/CouponProjectWeb/rest/customer/buy',
			data : coupon
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			ctrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("purchaseCoupon failed status=" + status + " : " + data);
		})

	};


}]);

function formattedDate(date) {
	if (!date)
		return '';
	
    var month = date.getMonth() + 1;
    var    day = date.getDate();
    var    year = date.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-'); // + ' 00:00:00';
}


var app = angular.module('companyApp', ['ngRoute','ngFileUpload']);

// configure our routes
app.config(['$routeProvider' ,function($routeProvider) {
	$routeProvider
	
	// route for the coupons list
	.when('/', {
		templateUrl : 'coupons.html',
		controller  : 'CouponController as couponController'
	})	

	// route for the company coupons list 
	.when('/coupons', {
		templateUrl : 'coupons.html',
		controller  : 'CouponController as couponController'
	})

	// route for the company settings 
	.when('/settings', {
		templateUrl : 'settings.html',
		controller  : 'CompanyController as companyController'
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

// the controller for the company template
app.controller('CompanyController',['CompanyService','$window', function(CompanyService,$window) {
	
	this.id = 0;
	this.companyName = '';
	this.passw1 = '';
	this.passw2 = '';
	this.companys = [];

	this.create = false;
	this.edit = true;
	this.error = false;
	this.incomplete = false; 

	/* editCustomer : handles the id,companyName,passw1,passw2 and create fields.
	 * when the Create new Customer button is clicked we call this function with 'new' as the parameter.
	 * when the Edit button is clicked we call this function with the row index ($index) of the company.
	 * the create field marks the requested operation (create/update).
	 */
	this.editCustomer = function(indx) {
		if ( indx == 'new') { // the Create new Customer button was clicked
			this.create = true;
			this.edit = true;
			this.incomplete = true;
			
			this.id = 0;
			this.companyName = '';
			this.passw1 = '';
			this.passw2 = '';			
		} else { // the edit (company row) button was clicked
			this.create = false;
			this.edit = false;
			
			var company = this.companys[indx];
			this.id = company.id;
			this.companyName = company.companyName;
			this.passw1 = company.password;
			this.passw2 = company.password;
		}
		this.scrollTo('bottom');		
	};

	/* watch : checks if the save changes button may by clicked. it checks that : companyName is not empty,
	 * passw1 and passw2 are equal and not empty.
	 */
	this.watch = function() {
		if (!this.companyName.length)
			return false;
		if (!this.passw1.length || this.passw1 !== this.passw2)
			return false;
		return true;
	};
	
	/*
	 * saveChanges : performs the create/update company.
	 * note that we pass this to the CompanyService functions so that when can refresh the companys list after the
	 * $http (asynchronous) call finishes. 
	 */
	this.saveChanges = function() {
		//this.test();
		
		// create a company object from the fields in the form
		var company = {'id':this.id,'companyName': this.companyName,'password':this.passw1,'coupons':[]};
		if (this.create) {
			CompanyService.createCustomer(this,company);
		} else {
			CompanyService.updateCustomer(this,company);
		}
	};
	
	this.removeCustomer = function(indx){
		var id = this.companys[indx].id;
		CompanyService.removeCustomer(this , id);
		
	}

	/* refresh : refreshes the coupons list (by calling getCoupons).
	 */
	this.refresh = function() {
		alert("refresh");
		CompanyService.getCoupons(this);
	}
	
	this.scrollTo = function(where) {
		if ('top' == where)
			$window.scrollTo(0,0);
		else if ('bottom' == where)
			$window.scrollTo(0,document.body.scrollHeight);
	}

	// refresh the companys list
	this.refresh();

}]);

app.controller('CouponController',['CompanyService','$window','Upload', function(CompanyService,$window,Upload) {
//app.controller('CouponController',['CompanyService','$window', function(CompanyService,$window) {
	
	// coupon fields
	this.id = 0;
	this.title = '';
	this.type = '';
	this.startDate = '';
	this.endDate = '';
	this.image = '';
	this.massage = '';
	this.price = '';
	this.amount = '';
	
	this.coupons = [];
	this.couponTypes = [];
	
	Object.defineProperty(this,'couponType', { // clear price and couponEndDate when type is set
		  get: function() {
		    return this._couponType;
		  },
		  set: function(value) {
			  this._couponType = value;
			  this._couponPrice = '';
			  this._couponEndDate = '';
		  }
		});
	Object.defineProperty(this,'couponPrice', { // clear type and couponEndDate when price is set
		  get: function() {
		    return this._couponPrice;
		  },
		  set: function(value) {
			  this._couponPrice = value;
			  this._couponType = '';
			  this._couponEndDate = '';
		  }
		});
	Object.defineProperty(this,'couponEndDate', { // clear type and price when date is set
		  get: function() {
		    return this._couponEndDate;
		  },
		  set: function(value) {
			  this._couponEndDate = value;
			  this._couponPrice = '';
			  this._couponType = '';
		  }
		});
	
	// search fields
	this.couponType = '';
	this.couponPrice = '';
	this.couponEndDate = '';

	/* refresh : refreshes the coupons list (by calling getCoupons... ).
	 */
	this.refresh = function() {
		this.couponType = '';
		this.couponPrice = '';
		this.couponEndDate = '';
		
		this.coupons = [];
		
		this.search();

	}
	
	this.search = function() {
		
		if (this.couponType.length) {
			//alert('serach by type ' + this.couponType);
			CompanyService.getCouponsByType(this,this.couponType);
		}
		else if (angular.isNumber(this.couponPrice)) {
			//alert('serach by price ' + this.couponPrice);
			CompanyService.getCouponsByPrice(this,this.couponPrice);
		} else if (this.couponEndDate) {
			//alert('serach by date ' + this.couponEndDate);
			CompanyService.getCouponsByDate(this,this.couponEndDate);
		}
		else {
			alert('serach all');
			CompanyService.getCoupons(this);
		}
	}	

	this.scrollTo = function(where) {
		if ('top' == where)
			$window.scrollTo(0,0);
		else if ('bottom' == where)
			$window.scrollTo(0,document.body.scrollHeight);
	}
	
	// edit / create coupon 
	
	this.create = false;
	this.edit = true;
	this.error = false;
	this.incomplete = false; 

	/* editCoupon : handles the coupon fields.
	 * when the Create new Coupon button is clicked we call this function with 'new' as the parameter.
	 * when the Edit button is clicked we call this function with the row index ($index) of the coupon.
	 * the create field marks the requested operation (create/update).
	 */
	this.editCoupon = function(indx) {
		if ( indx == 'new') { // the Create new Coupon button was clicked
			this.create = true;
			this.edit = true;
			this.incomplete = true;
			
			this.id = 0;
			this.title = '';
			this.type = '';
			this.startDate = '';
			this.endDate = '';
			this.image = '';
			this.massage = '';
			this.price = '';
			this.amount = '';		
		} else { // the edit (coupon row) button was clicked
			this.create = false;
			this.edit = false;
			
			var coupon = this.coupons[indx];
			this.id = coupon.id;
			this.title = coupon.title;
			this.type = coupon.type;
			this.startDate = new Date(coupon.startDate);
			this.endDate = new Date(coupon.endDate);
			this.image = coupon.image;
			this.massage = coupon.massage;
			this.price = coupon.price;
			this.amount = coupon.amount;
		}
		this.scrollTo('bottom');		
	};

	/* watch : checks if the save changes button may by clicked. it checks that : customerName is not empty,
	 * passw1 and passw2 are equal and not empty.
	 */
	this.watch = function() {
		if (!this.title.length || !this.massage.length || !this.image.length)
			return false;
		if (!this.startDate || !this.endDate)
			return false;
		if (!this.price || !this.amount)
			return false;
		return true;
	};
	
	/*
	 * saveChanges : performs the create/update coupon.
	 * note that we pass this to the CouponService functions so that when can refresh the customers list after the
	 * $http (asynchronous) call finishes. 
	 */
	this.saveChanges = function() {
		alert('saveChanges');

		var coupon = {'id':this.id,'title': this.title,'type':this.type
				     ,'startDate':this.startDate,'endDate':this.endDate
				     ,'image':this.image,'massage':this.massage
				     ,'price':this.price,'amount':this.amount
				     };
		
		if (this.create) {
			alert('creating coupon');
			CompanyService.createCoupon(this,coupon);
		} else {
			alert('updating coupon');
			CompanyService.updateCoupon(this,coupon);
		}
	};
	
	this.removeCoupon = function(indx){
		//var id = this.coupon[indx].id;
		var coupon = this.coupon[indx];
		CompanyService.removeCoupon(this , coupon);
		
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

	CompanyService.getCouponTypes(this);
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

/* CompanyService : a collection of functions that call the rest services.
 * note that since the $http call is an asynchronous call. we pass a ctrl to each of thees functions so that
 * we can refresh the companys list in the ctrl.
 */
app.service('CompanyService', ['$http' ,function($http) {
	// companys
	
	// getCouponTypes : gets the types of coupons that are defined in the system. 	
	this.getCouponTypes = function(ctrl) {
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/company/coupontypes',

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			ctrl.couponTypes = data;
			//ctrl.refresh();
		})
		.error(function(data, status, headers, config) {
		})		
		
	}
	
	// getCoupons : gets all the coupons that the company bought. 
	this.getCoupons = function(ctrl) {
		alert('getCoupons');
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/company/coupons',

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			alert("data=" + data);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
		})
	};
	
	// getCouponsByType : gets all the coupons that the company bought by type. 
	this.getCouponsByType = function(ctrl , couponType) {
		//alert('getCouponsByType=' + couponType);

		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/company/coupons/' + couponType,

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
		})

	};
	
	// getCouponsByPrice : gets all the coupons that the company bought by price. 
	this.getCouponsByPrice = function(ctrl , price) {
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/company/coupons/' + price,

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
		})

	};
	
	// getCouponsByDate : gets all the coupons that the company bought by price. 
	this.getCouponsByDate = function(ctrl , date) {
		//alert("getCouponsByDate=" + date);
		dt = formattedDate(date);
		
		$http({
			method: 'GET',
			url: '/CouponProjectWeb/rest/company/coupons/date/' + dt,

		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			//alert("data=" + data);
			ctrl.coupons = data;
		})
		.error(function(data, status, headers, config) {
		})

	};
	
	// createCoupon : creates a company coupon
	this.createCoupon = function(ctrl,coupon) {
		alert('createCoupon ' + coupon);
		$http({
			method: 'POST',
			url: '/CouponProjectWeb/rest/company/coupon',
			data: coupon,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			ctrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("createCoupon failed status=" + status);
		})

	};
	

	// updateCoupon : updates a company coupon
	this.updateCoupon = function(ctrl,coupon) {
		alert('updateCoupon ' + coupon);

		$http({
			method: 'PUT',
			url: '/CouponProjectWeb/rest/company/coupon',
			data: coupon,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			ctrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("updateCoupon failed status=" + status);
		})

	};

	// removeCoupon : remove a company coupon
	this.removeCoupon = function(ctrl,coupon) {
		
		$http({
			method: 'DELETE',
			url: '/CouponProjectWeb/rest/company/coupon',
			data: coupon,
		//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		})
		.success(function(data, status, headers, config) {
			console.log("data=" + data + " status=" + status);
			ctrl.refresh();
		})
		.error(function(data, status, headers, config) {
			alert("removeCoupon failed status=" + status);
		})

	};
	
}]);

app.controller('MyCtrl', [ '$scope', 'Upload', function($scope, Upload) {
	$scope.onFileSelect = function($files) {
		alert('onFileSelect');
		//$files: an array of files selected, each file has name, size, and type.
		for (var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			alert('file=' + $file);
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



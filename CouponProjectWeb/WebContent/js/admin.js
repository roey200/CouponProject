var app = angular.module('adminApp', []);
app.controller('AdminCtrl',['AdminService', function(AdminService) {
	
	this.id = 0;
	this.customerName = '';
	this.passw1 = '';
	this.passw2 = '';
	this.customers = [];

	this.create = false;
	this.edit = true;
	this.error = false;
	this.incomplete = false; 

	this.editCustomer = function(indx) {
		if ( indx == 'new') {
			this.create = true;
			this.edit = true;
			this.incomplete = true;
			
			this.id = 0;
			this.customerName = '';
			this.passw1 = '';
			this.passw2 = '';			
		} else {
			this.create = false;
			this.edit = false;
			
			var customer = this.customers[indx];
			this.id = customer.id;
			this.customerName = customer.customerName;
			this.passw1 = customer.password;
			this.passw2 = customer.password;
		}
	};

/*
alert("111");
$scope.$watch('passw1',function() {this.test();});
alert("222");
this.$watch('passw2',function() {this.test();});
 this.$watch('fName', function() {this.test();});
this.$watch('lName', function() {this.test();});
*/

	this.test = function() {
		if (!this.customerName.length)
			return false;
		if (!this.passw1.length || this.passw1 !== this.passw2)
			return false;
		return true;
//		if (this.passw1 !== this.passw2) {
//			this.error = true;
//		} else {
//			this.error = false;
//		}
//		this.incomplete = false;
//		if (this.edit && (!this.customerName ||	!this.passw1.length || !this.passw2.length)) {
//			this.incomplete = true;
//		}
	};
	
	this.saveChanges = function() {
		//this.test();
		
		var customer = {'id':this.id,'customerName': this.customerName,'password':this.passw1,'coupons':[]};
		if (this.create) {
			AdminService.createCustomer(customer,this);
		} else {
			AdminService.updateCustomer(customer,this);
		}
	};	

this.refresh = function() {
	AdminService.getCustomers(this);
}

this.refresh();

}]);

// services

app.service('AdminService', ['$http' ,function($http) {
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
			/*
			console.log("data=" + data + " status=" + status);
			if (status == 401)
				$scope.errorMsg = "Login not correct";
			else
				$scope.errorMsg = 'Unable to submit form';
				*/
			
		})

	};

	this.createCustomer = function(customer,adminCtrl) {
		
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
			alert("createCustomer failed status=" + status);
			/*
			console.log("data=" + data + " status=" + status);
			if (status == 401)
				$scope.errorMsg = "Login not correct";
			else
				$scope.errorMsg = 'Unable to submit form';
				*/
			
		})

	};

	this.updateCustomer = function(customer,adminCtrl) {
		
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
			alert("updateCustomer failed status=" + status);
			/*
			console.log("data=" + data + " status=" + status);
			if (status == 401)
				$scope.errorMsg = "Login not correct";
			else
				$scope.errorMsg = 'Unable to submit form';
				*/
			
		})

	};
	
}]);

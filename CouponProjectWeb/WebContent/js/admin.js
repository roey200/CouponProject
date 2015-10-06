var app = angular.module('adminApp', []);
app.controller('AdminCtrl',['AdminService', function(AdminService) {
	
	this.customerName = 'AAAA';
	//this.password = '***';
	this.passw1 = '';
	this.passw2 = '';
	this.customers = [];

	this.edit = true;
	this.error = false;
	this.incomplete = false; 

	this.editCustomer = function(indx) {
		if ( indx == 'new') {
			this.edit = true;
			this.incomplete = true;
			this.customerName = '';
			this.lName = '';
		} else {
			this.edit = false;
			this.customerName = this.customers[indx].customerName;
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
		if (this.passw1 !== this.passw2) {
			this.error = true;
		} else {
			this.error = false;
		}
		this.incomplete = false;
		if (this.edit && (!this.fName.length ||	!this.lName.length || !this.passw1.length || !this.passw2.length)) {
			this.incomplete = true;
		}
	};
	
	this.saveChanges = function() {
		//this.test();
		
		var customer = {'customerName': this.customerName,'password':this.passw1,'coupons':[]};
		this.customers.push(customer);
	};	

this.getCustomers = function() {
	AdminService.getCustomers(this);
};

this.getCustomers();

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
	
}]);

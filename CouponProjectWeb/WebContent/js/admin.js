var app = angular.module('adminApp', []);
app.controller('AdminCtrl',['AdminService','$window', function(AdminService,$window) {
	
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

// services

/* AdminService : a collection of functions that call the rest services.
 * note that since the $http call is an asynchronous call. we pass a adminCtrl to each of thees functions so that
 * we can refresh the customers list in the adminCtrl.
 */
app.service('AdminService', ['$http' ,function($http) {
	
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
			alert("createCustomer failed status=" + status);
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
			alert("updateCustomer failed status=" + status);
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
			alert("removeCustomer failed status=" + status);
		})

	};
	
	
}]);

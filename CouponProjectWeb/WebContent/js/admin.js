app = angular.module('adminApp', []);
app.controller('AdminCtrl',['$http', function($http) {
this.customerName = 'AAAA';
this.passw1 = '';
this.passw2 = '';
this.customers = [];
/*
{id:1, fName:'Hege', lName:"Pege" },
 {id:2, fName:'Kim',  lName:"Pim" },
{id:3, fName:'Sal',  lName:"Smith" },
 {id:4, fName:'Jack', lName:"Jones" },
{id:5, fName:'John', lName:"Doe" },
{id:6, fName:'Peter',lName:"Pan" }
];
*/

this.edit = true;
this.error = false;
 this.incomplete = false; 

this.editCustomer = function(id) {
  if (id == 'new') {
     this.edit = true;
    this.incomplete = true;
    this.customerName = '';
    this.lName = '';
    } else {
    this.edit = false;
    this.customerName = this.customers[id-1].customerName;
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
  if (this.edit && (!this.fName.length ||
  !this.lName.length ||
  !this.passw1.length || !this.passw2.length)) {
     this.incomplete = true;
  }
};

this.getCustomers = function() {
	$http({
		method: 'GET',
		url: '/CouponProjectWeb/rest/admin/customers',
//		data: encodedString,
	//	headers: {'Content-Type': 'application/x-www-form-urlencoded'}
	})
	.success(function(data, status, headers, config) {
		console.log("data=" + data + " status=" + status);
		alert("data=" + data);
		return data;


	})
	.error(function(data, status, headers, config) {
		console.log("data=" + data + " status=" + status);
		if (status == 401)
			$scope.errorMsg = "Login not correct";
		else
			$scope.errorMsg = 'Unable to submit form';
	})

};

alert("222");

this.getCustomers();
alert("customers=" + this.customers);

}]);
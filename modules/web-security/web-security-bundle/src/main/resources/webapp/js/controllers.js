'use strict';

function UserCtrl($scope, Roles, Users, $http) {
       $scope.user = {
           externalId : "",
           userName: "",
           password: "",
           email: "",
           roles: [],
           active: true
       }
       $scope.currentPage=0;
       $scope.pageSize=15;
       $scope.selectedItem='';
       $scope.deleteU=false;
       $scope.successfulMessage='';
       $scope.failureMessage='';

       $scope.roleList = Roles.query();

       $scope.userList = Users.query();

       $scope.selectedRole = -1;

       $scope.generatePassword = function() {

       }

       $scope.activeRole = function(roleName) {
             if ($scope.user.roles.indexOf(roleName)==-1) {
                $scope.user.roles.push(roleName);
             } else {
                $scope.user.roles.removeObject(roleName);
             }
       }

       $scope.getClass = function(roleName) {
            if ($scope.user.roles.indexOf(roleName)!=-1){
                return "btn btn-success";
            } else {
                return "btn disabled";
            }
       }

       $scope.saveUser = function() {
           $http.post('../websecurity/api/users/create', $scope.user).
                success(function(){$scope.successfulMessage="successAdd"; $scope.userList = Users.query();}).
                error(function(){$scope.failureMessage="failureAdd"});
       }


       $scope.numberOfPages = function(){
           return Math.ceil($scope.userList.length/$scope.pageSize);
       }

       $scope.changeCurrentPage = function(page) {
           $scope.currentPage=page;
       }

       $scope.getUser = function(user)  {
           $scope.successfulMessage='';
           $scope.failureMessage='';
           $http.post('../websecurity/api/users/getuser', user.userName).success(function(data) {
                   $scope.user = data;
                   $scope.user.password='';
           });
       }

       $scope.updateUser = function(){
           $http.post('../websecurity/api/users/update', $scope.user).
               success(function(){$scope.successfulMessage="successUpdate"}).
               error(function(){$scope.failureMessage="failureUpdate"});
       }

       $scope.deleteUser = function() {
           $http.post('../websecurity/api/users/delete', $scope.user).
                success(function(){$scope.successfulMessage="successRemove"}).
                error(function(){$scope.failureMessage="failureRemove"});
       }

       $scope.resetValues = function() {
            $scope.user = {
                externalId : "",
                userName: "",
                password: "",
                email: "",
                roles: [],
                active: true
            }
            $scope.successfulMessage='';
            $scope.failureMessage='';
       }
}

function RoleCtrl($scope, Roles, Permissions, $http) {
       $scope.currentPage=0;
       $scope.pageSize=15;

       $scope.roleList = Roles.query();
       $scope.permissionList = Permissions.query();
       $scope.numberOfPages=function(){
           return Math.ceil($scope.roleList.length/$scope.pageSize);
       }

       $scope.changeCurrentPage = function(page) {
           $scope.currentPage=page;
       }

       $scope.uniqePermissionList = function(list) {
           var newArr = [],
           listLen = list.length,
           found, x, y;
           for (x = 0; x < listLen; x++) {
               found = undefined;
               for (y = 0; y < newArr.length; y++) {
                   if (list[x].bundleName === newArr[y].bundleName) {
                       found = true;
                       break;
                   }
               }
               if (!found) {
                   newArr.push(list[x]);
               }
           }
           return newArr;
       }

}

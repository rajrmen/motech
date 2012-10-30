'use strict';

/* App Module */

angular.module('motech-web-security', ['motech-dashboard', 'roleService', 'userService', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/users', {templateUrl: '../websecurity/partials/user.html', controller: UserCtrl}).
            when('/roles', {templateUrl: '../websecurity/partials/role.html', controller: RoleCtrl}).
            otherwise({redirectTo: '/welcome'});
}]).filter('filterPagination', function() {return function(input, start) { start= +start; return input.slice(start);}}).
filter('repeat', function(){return function(input, total) {total = parseInt(total);for (var i=0; i<total; i++) input.push(i);return input;}});;



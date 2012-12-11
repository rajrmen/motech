'use strict';

/* App Module */

angular.module('motech-ivr-reports', ['motech-dashboard', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/reports', {templateUrl: '../ivr_reports/partials/reports.html', controller: IvrReportsController}).
            otherwise({redirectTo: '/reports'});
}]);

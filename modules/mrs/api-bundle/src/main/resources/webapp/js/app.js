'use strict';

/* App Module */

angular.module('motech-mrs', ['motech-dashboard', 'patientsServices', 'patientService', 'ngCookies', 'bootstrap', 'motech-widgets']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/dashboard', {templateUrl: '../mrs/partials/patients.html', controller: DashboardMrsCtrl}).
            when('/mrs/new', {templateUrl: '../mrs/partials/form.html', controller: ManageMrsCtrl}).
            when('/mrs/:mrsId/edit', {templateUrl: '../mrs/partials/form.html', controller: ManageMrsCtrl}).
            when('/mrs/:mrsId/editAttributes', {templateUrl: '../mrs/partials/attributes.html', controller: ManageMrsCtrl}).
            otherwise({redirectTo: '/dashboard'});
    }
]);
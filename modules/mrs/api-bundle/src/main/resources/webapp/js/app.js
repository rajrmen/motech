'use strict';

/* App Module */

angular.module('motech-mrs', ['motech-dashboard', 'patientsServices', 'patientService', 'ngCookies', 'bootstrap', 'motech-widgets']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/patients', {templateUrl: '../mrs/partials/patients.html', controller: PatientMrsCtrl}).
            when('/patients/:mrsId', {templateUrl: '../mrs/partials/patients.html', controller: PatientMrsCtrl}).
            when('/settings', {templateUrl: '../mrs/partials/settings.html', controller: SettingsMrsCtrl}).
            when('/mrs/new', {templateUrl: '../mrs/partials/form.html', controller: ManagePatientMrsCtrl}).
            when('/mrs/:mrsId/edit', {templateUrl: '../mrs/partials/form.html', controller: ManagePatientMrsCtrl}).
            when('/mrs/:mrsId/editAttributes', {templateUrl: '../mrs/partials/attributes.html', controller: ManagePatientMrsCtrl}).
            otherwise({redirectTo: '/patients'});
    }
]);
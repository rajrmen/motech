'use strict';

/* put your routes here */

angular.module('${artifactId}', ['motech-dashboard', 'YourModuleServices', 'ngCookies', 'bootstrap'])
    .config(['$stateProvider', function ($stateProvider) {

        $stateProvider
            .state('${artifactId}', {abstract: true, templateUrl: '../${artifactId}/resources/index.html'})
            .state('${artifactId}.welcome', {url: '/welcome', templateUrl: '../${artifactId}/resources/partials/welcome.html', controller: YourController });
    }]);

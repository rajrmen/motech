'use strict';

/* put your routes here */

angular.module('${artifactId}', ['motech-dashboard', 'YourModuleServices', 'ngCookies', 'bootstrap'])
    .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {

        $stateProvider
            .state('welcome', {url: '/welcome', templateUrl: '../${artifactId}/resources/partials/welcome.html', controller: YourController });

        $urlRouterProvider.otherwise("/welcome");
    }]);

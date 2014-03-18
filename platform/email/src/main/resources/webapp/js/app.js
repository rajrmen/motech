(function () {
    'use strict';

    /* App Module */

    function safeApply(rootScope, fun) {
        var phase = rootScope.$$phase;

        if (phase === '$apply' || phase === '$digest') {
            if(fun && (typeof(fun) === 'function')) {
                fun();
            }
        } else {
            scope.$apply(fun);
        }
    }

    var app = angular.module('email', ['motech-dashboard', 'ngCookies', 'ngRoute',
        'email.controllers', 'email.directives', 'email.services']);

    app.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/send', {
            templateUrl: '../email/resources/partials/sendEmail.html',
            controller: 'SendEmailController',
        }).when('/logging', {
            templateUrl: '../email/resources/partials/emailLogging.html',
            controller: 'EmailLoggingController'
        }).when('/settings', {
            templateUrl: '../email/resources/partials/settings.html',
            controller: 'SettingsController'
        }).otherwise({redirectTo: '/send'});
    }]);
}());

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

    app.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise("/send");

        $stateProvider.state('email.send', {
            url: '/send',
            templateUrl: '../email/resources/partials/sendEmail.html',
            controller: 'SendEmailController'
        }).state('email.logging', {
            url: '/logging',
            templateUrl: '../email/resources/partials/emailLogging.html',
            controller: 'EmailLoggingController'
        }).state('email.settings', {
            url: '/settings',
            templateUrl: '../email/resources/partials/settings.html',
            controller: 'SettingsController'
        });
    }]);
}());

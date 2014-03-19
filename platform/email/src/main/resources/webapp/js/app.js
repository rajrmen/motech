(function () {
    'use strict';

    /* App Module */

    var app = angular.module('email', ['motech-dashboard', 'ngCookies', 'ngRoute',
        'email.controllers', 'email.directives', 'email.services']);

    app.config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider.state('email_send', {
            url: '/send',
            templateUrl: '../email/resources/partials/sendEmail.html',
            controller: 'SendEmailController'
        }).state('email_logging', {
            url: '/logging',
            templateUrl: '../email/resources/partials/emailLogging.html',
            controller: 'EmailLoggingController'
        }).state('email_settings', {
            url: '/settings',
            templateUrl: '../email/resources/partials/settings.html',
            controller: 'SettingsController'
        });

        $urlRouterProvider.otherwise('/send');
    });
}());

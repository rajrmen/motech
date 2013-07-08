(function () {
    'use strict';

    /* App Module */

    angular.module('motech-email', ['motech-dashboard', 'ngCookies', 'bootstrap', 'sendEmailService', 'settingsService']).config(['$stateProvider', '$urlRouterProvider',
        function ($stateProvider, $urlRouterProvider) {
            $stateProvider
                .state('send', {url: '/send', templateUrl: '../email/resources/partials/sendEmail.html', controller: 'SendEmailController'})
                .state('moduleSettings', {url: '/settings', templateUrl: '../email/resources/partials/settings.html', controller: 'SettingsController'});

            $urlRouterProvider.otherwise("/send");
    }]);
}());

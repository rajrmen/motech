(function () {
    'use strict';

    /* App Module */

    angular.module('motech-email', ['motech-dashboard', 'ngCookies', 'bootstrap', 'sendEmailService', 'settingsService']).config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('email', {abstract: true, templateUrl: '../email/resources/index.html'})
                .state('email.send', {url: '/email/send', templateUrl: '../email/resources/partials/sendEmail.html', controller: 'SendEmailController'})
                .state('email.settings', {url: '/email/settings', templateUrl: '../email/resources/partials/settings.html', controller: 'SettingsController'});
    }]);
}());

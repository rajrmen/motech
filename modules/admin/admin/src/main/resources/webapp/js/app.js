(function () {
    'use strict';

    /* App Module */

    angular.module('motech-admin', ['motech-dashboard', 'bundleServices', 'messageServices', 'platformSettingsServices',
        'moduleSettingsServices', 'logService', 'ngCookies', 'bootstrap', "notificationRuleServices", "notificationRuleDtoServices"])
        .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
          $stateProvider
              .state('bundles', {url: '/bundles', templateUrl: '../admin/partials/bundles.html', controller: 'BundleListCtrl'})
              .state('messages', {url: '/messages', templateUrl: '../admin/partials/messages.html', controller: 'StatusMsgCtrl'})
              .state('settings', {url: '/settings', templateUrl: '../admin/partials/settings.html', controller: 'SettingsCtrl'})
              .state('bundle', {url: '/bundle/:bundleId', templateUrl: '../admin/partials/bundle.html', controller: 'ModuleCtrl'})
              .state('bundleSettings', {url: '/bundleSettings/:bundleId', templateUrl: '../admin/partials/bundleSettings.html', controller: 'BundleSettingsCtrl'})
              .state('modulePanels', {url: '/modulePanels', templateUrl: '../admin/partials/modulePanels.html'})
              .state('operations', {url: '/operations', templateUrl: '../admin/partials/operations.html', controller: 'OperationsCtrl'})
              .state('log', {url: '/log', templateUrl: '../admin/partials/log.html', controller: 'ServerLogCtrl'})
              .state('queues', {url: '/queues', templateUrl: '../admin/partials/queue_stats.html', controller: 'QueueStatisticsCtrl'})
              .state('queuesBrowse', {url: '/queues/browse', templateUrl: '../admin/partials/queue_message_stats.html', controller: 'MessageStatisticsCtrl'})
              .state('logOptions', {url: '/logOptions', templateUrl: '../admin/partials/logOptions.html', controller: 'ServerLogOptionsCtrl'})
              .state('notificationRules', {url: '/notificationRules', templateUrl: '../admin/partials/notificationRules.html', controller: 'NotificationRuleCtrl'});

          $urlRouterProvider.otherwise("/bundles");
    }]).filter('moduleName', function () {
        return function (input) {
            return input.replace(/(motech\s|\sapi|\sbundle)/ig, '');
        };
    }).directive('sidebar', function () {
       return function (scope, element, attrs) {
           $(element).sidebar({
               position:"right"
           });
       };
    });
}());

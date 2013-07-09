(function () {
    'use strict';

    /* App Module */

    angular.module('motech-admin', ['motech-dashboard', 'bundleServices', 'messageServices', 'platformSettingsServices',
        'moduleSettingsServices', 'logService', 'ngCookies', 'bootstrap', "notificationRuleServices", "notificationRuleDtoServices"])
        .config(['$stateProvider', function ($stateProvider) {
          $stateProvider
              .state('admin', {abstract: true, templateUrl: '../admin/index.html'})
              .state('admin.bundles', {url: '/bundles', templateUrl: '../admin/partials/bundles.html', controller: 'BundleListCtrl'})
              .state('admin.messages', {url: '/messages', templateUrl: '../admin/partials/messages.html', controller: 'StatusMsgCtrl'})
              .state('admin.settings', {url: '/settings', templateUrl: '../admin/partials/settings.html', controller: 'SettingsCtrl'})
              .state('admin.bundle', {url: '/bundle/:bundleId', templateUrl: '../admin/partials/bundle.html', controller: 'ModuleCtrl'})
              .state('admin.bundleSettings', {url: '/bundleSettings/:bundleId', templateUrl: '../admin/partials/bundleSettings.html', controller: 'BundleSettingsCtrl'})
              .state('admin.modulePanels', {url: '/modulePanels', templateUrl: '../admin/partials/modulePanels.html'})
              .state('admin.operations', {url: '/operations', templateUrl: '../admin/partials/operations.html', controller: 'OperationsCtrl'})
              .state('admin.log', {url: '/log', templateUrl: '../admin/partials/log.html', controller: 'ServerLogCtrl'})
              .state('admin.queues', {url: '/queues', templateUrl: '../admin/partials/queue_stats.html', controller: 'QueueStatisticsCtrl'})
              .state('admin.queuesBrowse', {url: '/queues/browse', templateUrl: '../admin/partials/queue_message_stats.html', controller: 'MessageStatisticsCtrl'})
              .state('admin.logOptions', {url: '/logOptions', templateUrl: '../admin/partials/logOptions.html', controller: 'ServerLogOptionsCtrl'})
              .state('admin.notificationRules', {url: '/notificationRules', templateUrl: '../admin/partials/notificationRules.html', controller: 'NotificationRuleCtrl'});
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

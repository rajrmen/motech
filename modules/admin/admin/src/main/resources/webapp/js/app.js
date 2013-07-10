(function () {
    'use strict';

    /* App Module */

    angular.module('motech-admin', ['motech-dashboard', 'bundleServices', 'messageServices', 'platformSettingsServices',
        'moduleSettingsServices', 'logService', 'ngCookies', 'bootstrap', "notificationRuleServices", "notificationRuleDtoServices"])
        .config(['$stateProvider', function ($stateProvider) {
          $stateProvider
              .state('admin', {abstract: true, templateUrl: '../admin/index.html'})
              .state('admin.bundles', {url: '/admin/bundles', templateUrl: '../admin/partials/bundles.html', controller: 'BundleListCtrl'})
              .state('admin.messages', {url: '/admin/messages', templateUrl: '../admin/partials/messages.html', controller: 'StatusMsgCtrl'})
              .state('admin.settings', {url: '/admin/settings', templateUrl: '../admin/partials/settings.html', controller: 'SettingsCtrl'})
              .state('admin.bundle', {url: '/admin/bundle/:bundleId', templateUrl: '../admin/partials/bundle.html', controller: 'ModuleCtrl'})
              .state('admin.bundleSettings', {url: '/admin/bundleSettings/:bundleId', templateUrl: '../admin/partials/bundleSettings.html', controller: 'BundleSettingsCtrl'})
              .state('admin.modulePanels', {url: '/admin/modulePanels', templateUrl: '../admin/partials/modulePanels.html'})
              .state('admin.operations', {url: '/admin/operations', templateUrl: '../admin/partials/operations.html', controller: 'OperationsCtrl'})
              .state('admin.log', {url: '/admin/log', templateUrl: '../admin/partials/log.html', controller: 'ServerLogCtrl', onExit: function () { $('div[id^="jquerySideBar"]').remove(); }})
              .state('admin.queues', {url: '/admin/queues', templateUrl: '../admin/partials/queue_stats.html', controller: 'QueueStatisticsCtrl'})
              .state('admin.queuesBrowse', {url: '/admin/queues/browse', templateUrl: '../admin/partials/queue_message_stats.html', controller: 'MessageStatisticsCtrl'})
              .state('admin.logOptions', {url: '/admin/logOptions', templateUrl: '../admin/partials/logOptions.html', controller: 'ServerLogOptionsCtrl'})
              .state('admin.notificationRules', {url: '/admin/notificationRules', templateUrl: '../admin/partials/notificationRules.html', controller: 'NotificationRuleCtrl'});
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

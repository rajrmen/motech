(function () {
    'use strict';

    /* App Module */

    angular.module('motech-tasks', ['motech-dashboard', 'channelServices', 'taskServices', 'activityServices',
                                    'manageTaskUtils', 'dataSourceServices', 'settingsServices', 'ngCookies', 'bootstrap',
                                    'motech-widgets']).config(['$stateProvider', '$urlRouterProvider',
        function ($stateProvider, $urlRouterProvider) {
            $stateProvider
                .state('taskList', {url: '/dashboard', templateUrl: '../tasks/partials/tasks.html', controller: 'DashboardCtrl'})
                .state('taskNew', {url: '/task/new', templateUrl: '../tasks/partials/form.html', controller: 'ManageTaskCtrl'})
                .state('taskEdit', {url: '/task/:taskId/edit', templateUrl: '../tasks/partials/form.html', controller: 'ManageTaskCtrl'})
                .state('taskLog', {url: '/task/:taskId/log', templateUrl: '../tasks/partials/history.html', controller: 'LogCtrl'})
                .state('moduleSettings', {url: '/settings', templateUrl: '../tasks/partials/settings.html', controller: 'SettingsCtrl'});

            $urlRouterProvider.otherwise("/dashboard");
        }]);
}());

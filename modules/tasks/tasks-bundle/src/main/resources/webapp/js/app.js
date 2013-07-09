(function () {
    'use strict';

    /* App Module */

    angular.module('motech-tasks', ['motech-dashboard', 'channelServices', 'taskServices', 'activityServices',
                                    'manageTaskUtils', 'dataSourceServices', 'settingsServices', 'ngCookies', 'bootstrap',
                                    'motech-widgets']).config(['$stateProvider',
        function ($stateProvider) {
            $stateProvider
                .state('tasks', {abstract: true, templateUrl: '../tasks/index.html'})
                .state('tasks.dashboard', {url: '/dashboard', templateUrl: '../tasks/partials/tasks.html', controller: 'DashboardCtrl'})
                .state('tasks.taskNew', {url: '/task/new', templateUrl: '../tasks/partials/form.html', controller: 'ManageTaskCtrl'})
                .state('tasks.taskEdit', {url: '/task/:taskId/edit', templateUrl: '../tasks/partials/form.html', controller: 'ManageTaskCtrl'})
                .state('tasks.taskLog', {url: '/task/:taskId/log', templateUrl: '../tasks/partials/history.html', controller: 'LogCtrl'})
                .state('tasks.settings', {url: '/settings', templateUrl: '../tasks/partials/settings.html', controller: 'SettingsCtrl'});
        }]);
}());

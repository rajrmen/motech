'use strict';

/* App Module */

angular.module('motech-tasks', ['motech-dashboard', 'channelServices', 'taskServices', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/dashboard', {templateUrl: '../tasks/partials/tasks.html', controller: DashboardCtrl}).
            when('/task/new', {templateUrl: '../tasks/partials/create.html', controller: ManageTaskCtrl}).
            when('/task/:taskId/edit', {templateUrl: '../tasks/partials/create.html', controller: ManageTaskCtrl}).
            otherwise({redirectTo: '/dashboard'});
    }
]).directive('draggable', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.draggable({ revert: true });
        }
    }
}).directive('droppable', function($compile) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.droppable({
                drop: function(event, ui) {
                    var dragIndex, dropIndex, channelName, moduleName, moduleVersion,
                        parent, value, position, eventKey;

                    if (angular.element(ui.draggable).hasClass('triggerField') && element.hasClass('actionField')) {
                        dragIndex = angular.element(ui.draggable).data('index');
                        dropIndex = angular.element(element).data('index');
                        eventKey = '{{' + scope.triggerInputFields[dragIndex].eventKey + '}}';
                        position = element.caret();
                        value = scope.actionInputFields[dropIndex].value || '';

                        scope.actionInputFields[dropIndex].value = value.insert(position, eventKey);
                    } else if (angular.element(ui.draggable).hasClass('task-panel') && (element.hasClass('trigger') || element.hasClass('action'))) {
                        channelName = angular.element(ui.draggable).data('channel-name');
                        moduleName = angular.element(ui.draggable).data('module-name');
                        moduleVersion = angular.element(ui.draggable).data('module-version');

                        if (element.hasClass('trigger')) {
                            scope.setTaskEvent('trigger', channelName, moduleName, moduleVersion);
                        } else if (element.hasClass('action')) {
                            scope.setTaskEvent('action', channelName, moduleName, moduleVersion);
                        }
                    } else if (angular.element(ui.draggable).hasClass('selected') && element.hasClass('task-selector')) {
                        parent = angular.element(ui.draggable).parent();

                        if (parent.hasClass('trigger')) {
                            delete scope.trigger;
                        } else if (parent.hasClass('action')) {
                            delete scope.action;
                        }
                    }

                    scope.$apply();
                }
            });
        }
    }
});
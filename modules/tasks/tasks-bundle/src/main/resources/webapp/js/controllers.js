'use strict';

/* Controllers */

function DashboardCtrl($scope, Tasks) {
    $scope.tasks = Tasks.query();

    $scope.get = function(taskEvent, prop) {
        var index;

        switch (prop) {
            case 'displayName': index = 0; break;
            case 'moduleName': index = 1; break;
            case 'moduleVersion': index = 2; break;
            case 'subject': index = 3; break;
            default: index = 0; break;
        }

        return taskEvent.split(':')[index];
    }

    $scope.deleteTask = function(task) {
        jConfirm(jQuery.i18n.prop('task.confirm.remove'), jQuery.i18n.prop("header.confirm"), function (val) {
            if (val) {
                task.$remove(function () {
                    $scope.tasks = Tasks.query();
                }, alertHandler('task.error.removed', 'header.error'));
            }
        });
    }
}

function ManageTaskCtrl($scope, Channels, Tasks, $routeParams, $http) {
    $scope.task = {};
    $scope.triggerInputFields = [];
    $scope.actionInputFields = [];
    $scope.channels = Channels.query();

    if ($routeParams.taskId != undefined) {
        $scope.task = Tasks.get({ taskId: $routeParams.taskId });
    }

    $scope.setTaskEvent = function(taskEventType, channelName, moduleName, moduleVersion) {
        var channel, i;

        for (i = 0; i < $scope.channels.length; ++i) {
            channel = $scope.channels[i];

            if (channel.displayName == channelName && channel.moduleName == moduleName && channel.moduleVersion == moduleVersion) {
                if (taskEventType === 'trigger') {
                    $scope.trigger = channel;
                } else if (taskEventType === 'action') {
                    $scope.action = channel;
                }

                break;
            }
        }
    }

    $scope.selectTaskEvent = function(taskEventType, taskEvent) {
        if (taskEventType === 'trigger') {
            $scope.task.trigger = "{0}:{1}:{2}:{3}".format($scope.trigger.displayName, $scope.trigger.moduleName, $scope.trigger.moduleVersion, taskEvent.subject);
            delete $scope.task.actionInputFields;
            $scope.triggerInputFields = [];

            for (i = 0; i < taskEvent.eventParameters.length; i += 1) {
                $scope.triggerInputFields.push(taskEvent.eventParameters[i]);
            }

            for (i = 0; i < $scope.actionInputFields.length; i += 1) {
                delete $scope.actionInputFields[i].value;
            }

            $scope.selectedTrigger = taskEvent;
        } else if (taskEventType === 'action') {
            $scope.task.action = "{0}:{1}:{2}:{3}".format($scope.action.displayName, $scope.action.moduleName, $scope.action.moduleVersion, taskEvent.subject);
            delete $scope.task.actionInputFields;
            $scope.actionInputFields = [];

            for (i = 0; i < taskEvent.eventParameters.length; i += 1) {
                $scope.actionInputFields.push(taskEvent.eventParameters[i]);
            }

            $scope.selectedAction = taskEvent;
        }
    }

    $scope.save = function(enabled) {
        var i, eventKey, value;

        $scope.task.actionInputFields = {};
        $scope.task.enabled = enabled;

        for (i = 0; i < $scope.actionInputFields.length; i += 1) {
            eventKey = $scope.actionInputFields[i].eventKey;
            value = $scope.actionInputFields[i].value || '';

            $scope.task.actionInputFields[eventKey] = value;
        }

        blockUI();
        $http.post('../tasks/api/task/save', $scope.task).
            success(function () {
                var msg = enabled ? 'task.success.savedAndEnabled' : 'task.success.saved', loc, indexOf;

                unblockUI();

                motechAlert(msg, 'header.saved', function () {
                    loc = new String(window.location);
                    indexOf = loc.indexOf('#');

                    window.location = loc.substring(0, indexOf) + "#/dashboard";
                });
            }).error(function () {
                delete $scope.task.actionInputFields;
                delete $scope.task.enabled;

                alertHandler('task.error.saved', 'header.error');
            });
    }

}

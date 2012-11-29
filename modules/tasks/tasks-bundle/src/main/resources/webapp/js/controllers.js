'use strict';

/* Controllers */

function DashboardCtrl($scope, Tasks, Activities) {
    $scope.activeTasks = [];
    $scope.pausedTasks = [];

    var tasks = Tasks.query(function () {
        var item, task, i;

        for (i = 0; i < tasks.length; ++i) {
            task = tasks[i];
            item = {
                task: task,
                success: Activities.query({taskId: task._id, type: 'success'}),
                error: Activities.query({taskId: task._id, type: 'error'})
            }

            if (task.enabled) {
                $scope.activeTasks.push(item);
            } else {
                $scope.pausedTasks.push(item);
            }
        }
    });

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

    $scope.deleteTask = function(item) {
        var enabled = item.task.enabled;

        jConfirm(jQuery.i18n.prop('task.confirm.remove'), jQuery.i18n.prop("header.confirm"), function (val) {
            if (val) {
                item.task.$remove(function () {
                    if (enabled) {
                        $scope.activeTasks.removeObject(item);
                    } else {
                        $scope.pausedTasks.removeObject(item);
                    }
                }, alertHandler('task.error.removed', 'header.error'));
            }
        });
    }
}

function ManageTaskCtrl($scope, Channels, Tasks, $routeParams, $http) {
    $scope.task = {};
    $scope.channels = Channels.query();

    if ($routeParams.taskId != undefined) {
        $scope.task = Tasks.get({ taskId: $routeParams.taskId });
    }

    $scope.setTaskEvent = function(taskEventType, channelName, moduleName, moduleVersion) {
        var channel, selected, i;

        for (i = 0; i < $scope.channels.length; ++i) {
            channel = $scope.channels[i];

            if (channel.displayName == channelName && channel.moduleName == moduleName && channel.moduleVersion == moduleVersion) {
                selected = {
                    display: channelName,
                    channel: channelName,
                    module: moduleName,
                    version: moduleVersion,
                }

                if (taskEventType === 'trigger') {
                    $scope.draggedTrigger = selected;
                    $scope.draggedTrigger.events = channel.triggerTaskEvents;
                } else if (taskEventType === 'action') {
                    $scope.draggedAction = selected;
                    $scope.draggedAction.events = channel.actionTaskEvents;
                }

                break;
            }
        }
    }

    $scope.selectTaskEvent = function(taskEventType, taskEvent) {
        if (taskEventType === 'trigger') {
            $scope.draggedTrigger.display = taskEvent.displayName;
            $scope.task.trigger = "{0}:{1}:{2}:{3}".format($scope.draggedTrigger.channel, $scope.draggedTrigger.module, $scope.draggedTrigger.version, taskEvent.subject);
            delete $scope.task.actionInputFields;

            if ($scope.action != undefined) {
                for (i = 0; i < $scope.action.eventParameters.length; i += 1) {
                    delete $scope.action.eventParameters[i].value;
                }
            }

            $scope.selectedTrigger = taskEvent;
        } else if (taskEventType === 'action') {
            $scope.draggedAction.display = taskEvent.displayName;
            $scope.task.action = "{0}:{1}:{2}:{3}".format($scope.draggedAction.channel, $scope.draggedAction.module, $scope.draggedAction.version, taskEvent.subject);
            delete $scope.task.actionInputFields;
            $scope.selectedAction = taskEvent;
        }
    }

    $scope.save = function(enabled) {
        var action = $scope.selectedAction, i, eventKey, value;

        $scope.task.actionInputFields = {};
        $scope.task.enabled = enabled;

        for (i = 0; i < action.eventParameters.length; i += 1) {
            eventKey = action.eventParameters[i].eventKey;
            value = action.eventParameters[i].value || '';

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

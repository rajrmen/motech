'use strict';

/* Controllers */

function DashboardCtrl($scope, Tasks, Activities) {
    var RECENT_TASK_COUNT = 7;

    $scope.activeTasks = [];
    $scope.pausedTasks = [];
    $scope.activities = [];

    var tasks = Tasks.query(function () {
        var activities = Activities.query(function () {
            var item, i, j;

            for (i = 0; i < tasks.length; i += 1) {
                item = {
                    task: tasks[i],
                    success: 0,
                    error: 0
                };

                for (j = 0; j < activities.length; j += 1) {
                    if (activities[j].task === item.task._id && activities[j].activityType === 'SUCCESS') {
                        item.success += 1;
                    }

                    if (activities[j].task === item.task._id && activities[j].activityType === 'ERROR') {
                        item.error += 1;
                    }
                }

                if (item.task.enabled) {
                    $scope.activeTasks.push(item);
                } else {
                    $scope.pausedTasks.push(item);
                }
            }

            for (i = 0; i < RECENT_TASK_COUNT && i < activities.length; i += 1) {
                for (j = 0 ; j < tasks.length; j += 1) {
                    if (activities[i].task === tasks[j]._id) {
                        $scope.activities.push({
                            task: activities[i].task,
                            trigger: tasks[j].trigger,
                            action: tasks[j].action,
                            date: activities[i].date,
                            type: activities[i].activityType
                        });
                        break;
                    }
                }
            }
        });
    });

    $scope.get = function (taskEvent, prop) {
        var index;

        switch (prop) {
            case 'displayName': index = 0; break;
            case 'moduleName': index = 1; break;
            case 'moduleVersion': index = 2; break;
            case 'subject': index = 3; break;
            default: index = 0; break;
        }

        return taskEvent.split(':')[index];
    };

    $scope.enableTask = function (item, enabled) {
        item.task.enabled = enabled;

        item.task.$save(function () {
            if (item.task.enabled) {
                $scope.pausedTasks.removeObject(item);
                $scope.activeTasks.push(item);
            } else {
                $scope.activeTasks.removeObject(item);
                $scope.pausedTasks.push(item);
            }
        });
    }

    $scope.deleteTask = function (item) {
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
    };
}

function ManageTaskCtrl($scope, Channels, Tasks, DataSources, $routeParams, $http) {
    $scope.currentPage = 0;
    $scope.pageSize = 10;
    $scope.task = {};
    $scope.filters = [];
    $scope.negationOperators = [{key:'info.filter.is',value:'true'}, {key:'info.filter.isNot',value:'false'}];
    $scope.selectedDataSources = [];
    $scope.availableDataSources = [];
    $scope.allDataSources = DataSources.query(function () {
        $.merge($scope.availableDataSources, $scope.allDataSources);
    });

    $scope.channels = Channels.query(function (){
        if ($routeParams.taskId != undefined) {
            $scope.task = Tasks.get({ taskId: $routeParams.taskId }, function () {
                var trigger = $scope.task.trigger.split(':'), action = $scope.task.action.split(':'),
                    i, source, dataSource, ds, object, obj;

                $scope.setTaskEvent('trigger', trigger[0], trigger[1], trigger[2]);
                $scope.setTaskEvent('action', action[0], action[1], action[2]);

                for (i = 0; i < $scope.draggedTrigger.events.length; i += 1) {
                    if ($scope.draggedTrigger.events[i].subject == trigger[3]) {
                        $scope.selectedTrigger = $scope.draggedTrigger.events[i];
                        $scope.draggedTrigger.display = $scope.selectedTrigger.displayName;
                        break;
                    }
                }

                for (i = 0; i < $scope.draggedAction.events.length; i += 1) {
                    if ($scope.draggedAction.events[i].subject == action[3]) {
                        $scope.selectedAction = $scope.draggedAction.events[i];
                        $scope.draggedAction.display = $scope.selectedAction.displayName;
                        break;
                    }
                }

                for (source in $scope.task.additionalData) {
                    ds = $scope.findDataSource($scope.allDataSources, source);
                    dataSource = { name: source, objects: []};

                    for (i = 0; i < $scope.task.additionalData[source].length; i += 1) {
                        object = $scope.task.additionalData[source][i];
                        obj = $scope.findObject(ds, object.type);

                        dataSource.objects.push({
                            id: object.id,
                            displayName: obj.displayName,
                            type: object.type,
                            fields: obj.fields,
                            lookup: {
                                displayName: $scope.findTriggerEventParameter(object.lookupValue).displayName,
                                by: object.lookupValue,
                                field: object.lookupField
                            }
                        });
                    }

                    $scope.selectedDataSources.push(dataSource);

                    ds = $scope.findDataSource($scope.availableDataSources, source);
                    $scope.availableDataSources.removeObject(ds);
                }

                for (i = 0; i < $scope.selectedAction.eventParameters.length; i += 1) {
                    $scope.selectedAction.eventParameters[i].value = $scope.createDraggableElement($scope.task.actionInputFields[$scope.selectedAction.eventParameters[i].eventKey]);
                }

                $scope.filters = [];
                if ($scope.task.filters) {
                    for (i = 0; i<$scope.task.filters.length; i += 1) {
                        for (var j = 0; j <  $scope.selectedTrigger.eventParameters.length; j+=1) {
                            if ( $scope.selectedTrigger.eventParameters[j].displayName==$scope.task.filters[i].eventParameter.displayName) {
                                $scope.task.filters[i].eventParameter=$scope.selectedTrigger.eventParameters[j];
                                break;
                            }
                        }
                        if ($scope.task.filters[i].negationOperator) {
                            $scope.task.filters[i].negationOperator = $scope.negationOperators[0];
                        } else {
                            $scope.task.filters[i].negationOperator = $scope.negationOperators[1];
                        }
                        $scope.filters.push($scope.task.filters[i]);
                    }
                }
            });
        }
    });

    $scope.setTaskEvent = function (taskEventType, channelName, moduleName, moduleVersion) {
        var channel, selected, i, j;

        for (i = 0; i < $scope.channels.length; i += 1) {
            channel = $scope.channels[i];

            if (channel.displayName == channelName && channel.moduleName == moduleName && channel.moduleVersion == moduleVersion) {
                selected = {
                    display: channelName,
                    channel: channelName,
                    module: moduleName,
                    version: moduleVersion,
                };

                if (taskEventType === 'trigger') {
                    $scope.draggedTrigger = selected;
                    $scope.draggedTrigger.events = channel.triggerTaskEvents;
                } else if (taskEventType === 'action') {
                    for (j = 0; j < channel.actionTaskEvents.length; j += 1) {
                        delete channel.actionTaskEvents[j].value;
                    }

                    $scope.draggedAction = selected;
                    $scope.draggedAction.events = channel.actionTaskEvents;
                }

                break;
            }
        }
    };

    $scope.selectTaskEvent = function (taskEventType, taskEvent) {
        if (taskEventType === 'trigger') {
            $scope.draggedTrigger.display = taskEvent.displayName;
            $scope.task.trigger = "{0}:{1}:{2}:{3}".format($scope.draggedTrigger.channel, $scope.draggedTrigger.module, $scope.draggedTrigger.version, taskEvent.subject);
            $scope.selectedTrigger = taskEvent;
        } else if (taskEventType === 'action') {
            $scope.draggedAction.display = taskEvent.displayName;
            $scope.task.action = "{0}:{1}:{2}:{3}".format($scope.draggedAction.channel, $scope.draggedAction.module, $scope.draggedAction.version, taskEvent.subject);
            $scope.selectedAction = taskEvent;
        }

        delete $scope.task.actionInputFields;

        if ($scope.selectedAction != undefined) {
            var i;

            for (i = 0; i < $scope.selectedAction.eventParameters.length; i += 1) {
                delete $scope.selectedAction.eventParameters[i].value;
            }
        }
    };

    $scope.getTooltipMsg = function(selected) {
        return selected !== undefined ? $scope.msg('help.doubleClickToEdit') : '';
    }

    $scope.save = function (enabled) {
        var action = $scope.selectedAction, i, eventKey, value;

        $scope.task.actionInputFields = {};
        $scope.task.enabled = enabled;

        $scope.task.filters = [];
        if ($scope.filters.length!=0) {
            for (i = 0; i < $scope.filters.length; i += 1) {
                value = $scope.filters[i];
                value.negationOperator = $scope.filters[i].negationOperator.value;
                $scope.task.filters.push(value);
            }
        }

        $scope.task.additionalData = {};
        for (i = 0; i < action.eventParameters.length; i += 1) {
            $('<div>' + action.eventParameters[i].value + "</div>").find('span[data-prefix="ad"]').each(function(index, value) {
                var span = $(value),
                    source = span.data('source'),
                    objectType = span.data('object-type'),
                    objectId = span.data('object-id'),
                    exists = false,
                    dataSource, object, i;

                if ($scope.task.additionalData[source] === undefined) {
                    $scope.task.additionalData[source] = [];
                }

                for (i = 0; i < $scope.task.additionalData[source].length; i += 1) {
                    if ($scope.task.additionalData[source][i].id === objectId) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    dataSource = $scope.findDataSource($scope.selectedDataSources, source);
                    object = $scope.findObject(dataSource, objectType, objectId);

                    $scope.task.additionalData[source].push({
                        id: object.id,
                        type: object.type,
                        lookupField: object.lookup.field,
                        lookupValue: object.lookup.by
                    });
                }
            });
        }

        for (i = 0; i < action.eventParameters.length; i += 1) {
            eventKey = action.eventParameters[i].eventKey;
            value = $scope.refactorDivEditable(action.eventParameters[i].value  || '');

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
                delete $scope.task.additionalData;

                alertHandler('task.error.saved', 'header.error');
            });
    };

    $scope.refactorDivEditable = function (value) {
        var result = $('<div>' + value + '</div>');

        result.find('span').replaceWith(function() {
            var eventKey = '', source = $(this).data('source'),
                type = $(this).data('object-type'), objectDisplayName = $(this).data('object'),
                prefix = $(this).data('prefix'), field = $(this).data('field'), id = $(this).data('object-id'),
                val;

            if (prefix === 'trigger') {
                for (var i = 0; i < $scope.selectedTrigger.eventParameters.length; i += 1) {
                    if ($scope.selectedTrigger.eventParameters[i].displayName == $(this).text()) {
                        eventKey = $scope.selectedTrigger.eventParameters[i].eventKey;
                    }
                }
            } else if (prefix === 'ad') {
                eventKey = field;
            }

            var manipulation = this.attributes.getNamedItem('manipulate')!=null ? this.attributes.getNamedItem('manipulate').value : '';
            if (manipulation && manipulation != "" ) {
                if (this.attributes.getNamedItem('data-type').value == 'UNICODE' || this.attributes.getNamedItem('data-type').value == 'TEXTAREA') {
                    var man = manipulation.split(" ");
                    for (var i = 0; i<man.length; i++) {
                        eventKey = eventKey +"?" + man[i];
                    }
                } else if (this.attributes.getNamedItem('data-type').value == 'DATE') {
                    eventKey = eventKey + "?" + manipulation;
                }
            }

            if (prefix === 'trigger') {
                val = '{{' + prefix + '.' + eventKey + '}}';
            } else if (prefix === 'ad') {
                val = '{{' + prefix + '.' + source + '.' + type + '#' + id + '.' + eventKey + '}}';
            }

            return val;
        });
        result.find('em').remove();
        return result.text();
    }

    $scope.createDraggableElement = function (value) {
        value = value.replace(/{{.*?}}/g, $scope.buildSpan);
        return value;
    }

    $scope.buildSpan = function(eventParameterKey) {
        var key = eventParameterKey.slice(eventParameterKey.indexOf('.') + 1, -2).split("?"),
            prefix = eventParameterKey.slice(2, eventParameterKey.indexOf('.')),
            span = "", param, source, type, field, cuts, dataSource, object, id;

        eventParameterKey = key[0];
        key.remove(0);
        var manipulation = key;

        if (prefix === 'trigger') {
            param = $scope.findTriggerEventParameter(eventParameterKey);
            span = '<span manipulationpopover contenteditable="false" class="badge badge-info triggerField ng-scope ng-binding ui-draggable" data-index="' + i +
                   '" data-type="' + param.type + '" data-prefix="' + prefix + '" style="position: relative;" ' +
                   (manipulation.length == 0 ? "" : 'manipulate="' + manipulation.join(" ") + '"') + '>' + param.displayName + '</span>';
        } else if (prefix === 'ad') {
            cuts = eventParameterKey.split('.');

            source = cuts[0];
            type = cuts[1].split('#');
            id = type.last();

            cuts.remove(0, 1);
            type.removeObject(id);

            field = cuts.join('.');
            type = type.join('#');

            dataSource = $scope.findDataSource($scope.selectedDataSources, source);
            object = $scope.findObject(dataSource, type);
            param = $scope.findObjectField(object, field);

            span = '<span manipulationpopover contenteditable="false" class="badge badge-warning triggerField ng-scope ng-binding ui-draggable" data-type="' + param.type +
                   '" data-prefix="' + prefix + '" data-source="' + source + '" data-object="' + param.displayName +'" data-object-type="' + type + '" data-field="' + field +
                   '" data-object-id="' + id + '" style="position: relative;" ' + (manipulation.length == 0 ? "" : 'manipulate="' + manipulation.join(" ") + '"') + '>' +
                   source + '.' + object.displayName + '#' + id + '.' + param.displayName + '</span>';
        }

        return span;
    }

    $scope.operators = function(event) {
        var operator = ['exist'];
        if (event && (event.type==='UNICODE' || event.type==='TEXTAREA')) {
            operator.push("equals");
            operator.push("contains");
            operator.push("startsWith");
            operator.push("endsWith");
        } else if (event && event.type==='NUMBER') {
            operator.push("gt");
            operator.push("lt");
            operator.push("equal");
        }
        return operator;
    }

    $scope.addFilter = function() {
        $scope.filters.push({})
    }

    $scope.validateForm = function() {
        var i, param;

        if ($scope.selectedAction !== undefined) {
            for (i = 0; i < $scope.selectedAction.eventParameters.length; i += 1) {
                param = $scope.refactorDivEditable($scope.selectedAction.eventParameters[i].value || '');

                if (param === null || param === undefined || !param.trim().length) {
                    return false;
                }
            }
        }

        return !($scope.filterForm.$invalid && $scope.filters.length != 0);
    }

    $scope.isDisabled = function(prop) {
        if(!prop) {
            return true;
        } else {
            return false;
        }
    }

    $scope.cssClass = function(prop) {
        var msg = 'validation-area';

        if (!prop) {
            msg = msg.concat(' error');
        }

        return msg;
    }

    $scope.actionCssClass = function(prop) {
        var msg = "control-group", value = $scope.refactorDivEditable(prop.value || '');

        if (value.length === 0) {
            msg = msg.concat(' error');
        }

        return msg;
    }

    $scope.addDataSource = function () {
        $scope.selectedDataSources.push({name: $scope.availableDataSources[0].name, objects: []});
        $scope.availableDataSources.remove(0);
    }

    $scope.changeDataSource = function (dataSource, available) {
        motechConfirm('task.confirm.changeDataSource', 'header.confirm', function (r) {
            if (r) {
                $('.actionField span[data-source="' + dataSource.name + '"]').remove();

                $scope.availableDataSources.removeObject(available);
                $scope.selectedDataSources.removeObject(dataSource);
                $scope.availableDataSources.push($scope.findDataSource($scope.allDataSources, dataSource.name));

                dataSource = cloneObj(available);
            }
        });
    }

    $scope.getAvailableLookupFields = function (dataSourceName, objectType) {
        var dataSource = $scope.findDataSource($scope.allDataSources, dataSourceName),
            object = $scope.findObject(dataSource, objectType);

        return object === undefined ? [] : object.lookupFields;
    }

    $scope.selectObject = function (dataSourceName, object, selected) {
        motechConfirm('task.confirm.changeObject', 'header.confirm', function (r) {
            if (r) {
                $('.actionField span[data-source="' + dataSourceName + '"][data-object-type="' + object.type + '"][data-object-id="' + object.id + '"]').remove();

                object.displayName = selected.displayName;
                object.type = selected.type;
                object.fields = selected.fields;
                object.lookup.field = selected.lookupFields[0];
            }
        });
    }

    $scope.selectLookup = function (object, lookup) {
        object.lookup.displayName = lookup.displayName;
        object.lookup.by = lookup.eventKey;
    }

    $scope.addObject = function (dataSource) {
        var first = $scope.findDataSource($scope.allDataSources, dataSource.name).objects[0],
            last = dataSource.objects.last();

        dataSource.objects.push({
            id: (last === undefined ? 0 : last.id) + 1,
            displayName: first.displayName,
            type: first.type,
            fields: first.fields,
            lookup: {
                displayName: $scope.selectedTrigger.eventParameters[0].displayName,
                by: $scope.selectedTrigger.eventParameters[0].eventKey,
                field: first.lookupFields[0]
            }
        });
    }

    $scope.findTriggerEventParameter = function (eventKey) {
        var i, found;

        for (i = 0; i < $scope.selectedTrigger.eventParameters.length; i += 1) {
            if ($scope.selectedTrigger.eventParameters[i].eventKey === eventKey) {
                found = $scope.selectedTrigger.eventParameters[i];
                break;
            }
        }

        return found;
    }

    $scope.findDataSource = function (dataSources, name) {
        var i, found;

        for (i = 0; i < dataSources.length; i += 1) {
            if (dataSources[i].name === name) {
                found = dataSources[i];
                break;
            }
        }

        return found;
    }

    $scope.findObject = function (dataSource, type, id) {
        var i, expression, found;

        for (i = 0; i < dataSource.objects.length; i += 1) {
            expression = dataSource.objects[i].type === type;

            if (expression && id !== undefined) {
                expression = expression && dataSource.objects[i].id === id;
            }

            if (expression) {
                found = dataSource.objects[i];
                break;
            }
        }

        return found;
    }

    $scope.findObjectField = function (object, field) {
        var i, found;

        for (i = 0; i < object.fields.length; i += 1) {
            if (object.fields[i].eventKey === field) {
                found = object.fields[i];
                break;
            }
        }

        return found;
    }

}

function LogCtrl($scope, Tasks, Activities, $routeParams) {
    if ($routeParams.taskId != undefined) {
        var data = { taskId: $routeParams.taskId }, task;

        task = Tasks.get(data, function () {
            $scope.activities = Activities.query(data);

            setInterval(function () {
                $scope.activities = Activities.query(data);
            }, 30 * 1000);

            $scope.trigger = {
                display: $scope.get(task.trigger, 'displayName'),
                module: $scope.get(task.trigger, 'moduleName'),
                version: $scope.get(task.trigger, 'moduleVersion')
            };

            $scope.action = {
                display: $scope.get(task.action, 'displayName'),
                module: $scope.get(task.action, 'moduleName'),
                version: $scope.get(task.action, 'moduleVersion')
            };
        });
    }

    $scope.get = function (taskEvent, prop) {
        var index;

        switch (prop) {
            case 'displayName': index = 0; break;
            case 'moduleName': index = 1; break;
            case 'moduleVersion': index = 2; break;
            case 'subject': index = 3; break;
            default: index = 0; break;
        }

        return taskEvent.split(':')[index];
    };
}

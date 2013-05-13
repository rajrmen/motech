(function () {
    'use strict';

    /* Services */

    angular.module('manageTaskUtils', ['ngResource']).factory('ManageTaskUtils', function () {
        return {
            FILTER_SET_PATH: '../tasks/partials/widgets/filter-set.html',
            FILTER_SET_ID: '#filter-set',
            DATA_SOURCE_PATH: '../tasks/partials/widgets/data-source.html',
            DATA_SOURCE_PREFIX_ID: '#data-source-',
            BUILD_AREA_ID: "#build-area",
            TRIGGER_PREFIX: 'trigger',
            DATA_SOURCE_PREFIX: 'ad',
            channels: {
                withTriggers: function (channels) {
                    var array = [];

                    angular.forEach(channels, function (channel) {
                        if (channel.triggerTaskEvents.length) {
                            array.push(channel);
                        }
                    });

                    return array;
                },
                withActions: function (channels) {
                    var array = [];

                    angular.forEach(channels, function (channel) {
                        if (channel.actionTaskEvents.length) {
                            array.push(channel);
                        }
                    });

                    return array;
                }
            },
            trigger: {
                select: function (scope, channel, trigger) {
                    scope.task.trigger = {
                        displayName: trigger.displayName,
                        channelName: channel.displayName,
                        moduleName: channel.moduleName,
                        moduleVersion: channel.moduleVersion,
                        subject: trigger.subject
                    };

                    angular.element("#trigger-" + channel.moduleName).parent('li').addClass('selectedTrigger').addClass('active');
                    angular.element("#collapse-trigger").collapse('hide');

                    scope.selectedTrigger = trigger;

                    if (!scope.$$phase) {
                        scope.$apply();
                    }
                },
                remove: function (scope) {
                    var li = angular.element("#trigger-" + scope.task.trigger.moduleName).parent('li');

                    li.removeClass('selectedTrigger');
                    li.removeClass("active");

                    delete scope.task.trigger;

                    if (!scope.$$phase) {
                        scope.$apply();
                    }
                }
            },
            action: {
                select: function (scope, action) {
                    scope.task.action = {
                        displayName: action.displayName,
                        channelName: scope.selectedActionChannel.displayName,
                        moduleName: scope.selectedActionChannel.moduleName,
                        moduleVersion: scope.selectedActionChannel.moduleVersion
                    };

                    if (action.subject) {
                        scope.task.action.subject = action.subject;
                    }

                    if (action.serviceInterface && action.serviceMethod) {
                        scope.task.action.serviceInterface = action.serviceInterface;
                        scope.task.action.serviceMethod = action.serviceMethod;
                    }

                    scope.selectedAction = action;

                    if (!scope.$$phase) {
                        scope.$apply();
                    }
                }
            },
            dataSource: {
                find: {
                    byId: function (dataSources, id) {
                        var found;

                        angular.forEach(dataSources, function (ds) {
                            if (ds._id === id) {
                                found = ds;
                            }
                        });

                        return found;
                    },
                    byName: function (dataSources, name, msg) {
                        var found;

                        angular.forEach(dataSources, function (ds) {
                            if (ds.dataSourceName === name || msg(ds.dataSourceName) === name) {
                                found = ds;
                            }
                        });

                        return found;
                    },
                    object: function (objects, type, id) {
                        var found;

                        angular.forEach(objects, function (obj) {
                            var expression = obj.type === type;

                            if (expression && id !== undefined) {
                                expression = expression && obj.id === id;
                            }

                            if (expression) {
                                found = obj;
                            }
                        });

                        return found;
                    }
                },
                select: function (scope, data, selected) {
                    data.dataSourceName = selected.name;
                    data.dataSourceId = selected._id;

                    delete data.displayName;
                    delete data.type;

                    if (!scope.$$phase) {
                        scope.$apply(data);
                    }
                },
                selectObject: function (scope, data, selected) {
                    data.displayName = selected.displayName;
                    data.type = selected.type;

                    if (!scope.$$phase) {
                        scope.$apply(data);
                    }
                }
            },
            isText: function (value) {
                return value && $.inArray(value, ['UNICODE', 'TEXTAREA']) !== -1;
            },
            isNumber: function (value) {
                return value && $.inArray(value, ['INTEGER', 'LONG', 'DOUBLE']) !== -1;
            },
            isDate: function (value) {
                return value && $.inArray(value, ['DATE']) !== -1;
            },
            isChrome: function (scope) {
                return scope.BrowserDetect.browser === 'Chrome';
            },
            isIE: function (scope) {
                return scope.BrowserDetect.browser !== 'Explorer';
            },
            canHandleModernDragAndDrop: function (scope) {
                return this.isChrome(scope) || this.isIE(scope);
            },
            createBooleanSpan: function (scope, value) {
                var badgeType = (value ? 'success' : 'important'),
                    msg = (value ? scope.msg('yes') : scope.msg('no')),
                    span = $('<span/>');

                span.attr('contenteditable', 'false');
                span.attr('data-value', value);
                span.attr('data-prefix', 'other');
                span.addClass('badge').addClass('badge-' + badgeType);
                span.text(msg);

                return $('<div/>').append(span).html();
            },
            createErrorMessage: function (scope, response) {
                var msg = scope.msg('task.error.saved') + '\n';

                angular.forEach(response, function (r) {
                    msg += ' - ' + scope.msg(r.message, r.args) + '\n';
                });

                return msg;
            }
        };
    });

}());

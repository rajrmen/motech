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
            getChannelsWithTriggers: function (channels) {
                var array = [];

                angular.forEach(channels, function (channel) {
                    if (channel.triggerTaskEvents.length) {
                        array.push(channel);
                    }
                });

                return array;
            },
            getChannelsWithActions: function (channels) {
                var array = [];

                angular.forEach(channels, function (channel) {
                    if (channel.actionTaskEvents.length) {
                        array.push(channel);
                    }
                });

                return array;
            },
            selectTrigger: function (scope, channel, trigger) {
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

                if(!scope.$$phase) {
                  scope.$apply();
                }
            },
            safeSelectTrigger: function (scope, channel, trigger) {
                var that = this;

                if (scope.task.trigger) {
                    motechConfirm('task.confirm.trigger', "header.confirm", function (val) {
                        var li;

                        if (val) {
                            that.removeTrigger(scope);
                            that.selectTrigger(scope, channel, trigger);
                        }
                    });
                } else {
                    that.selectTrigger(scope, channel, trigger);
                }
            },
            removeTrigger: function (scope) {
                var li = angular.element("#trigger-" + scope.task.trigger.moduleName).parent('li');

                li.removeClass('selectedTrigger');
                li.removeClass("active");

                delete scope.task.trigger;

                if(!scope.$$phase) {
                  scope.$apply();
                }
            },
            safeRemoveTrigger: function (scope) {
                var that = this;

                motechConfirm('task.confirm.trigger', "header.confirm", function (val) {
                    if (val) {
                        that.removeTrigger(scope);
                    }
                });
            },
            safeSelectActionChannel: function (scope, channel) {
                if (scope.selectedActionChannel && scope.selectedAction) {
                    motechConfirm('task.confirm.action', "header.confirm", function (val) {
                        if (val) {
                            scope.task.action = {};
                            scope.selectedActionChannel = channel;
                            delete scope.selectedAction;

                            if(!scope.$$phase) {
                              scope.$apply();
                            }
                        }
                    });
                } else {
                    scope.selectedActionChannel = channel;

                    if(!scope.$$phase) {
                      scope.$apply();
                    }
                }
            },
            selectAction: function (scope, action) {
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

                if(!scope.$$phase) {
                  scope.$apply();
                }
            },
            safeSelectAction: function (scope, action) {
                var that = this;

                if (scope.selectedAction) {
                    motechConfirm('task.confirm.action', "header.confirm", function (val) {
                        if (val) {
                            that.selectAction(scope, action);
                        }
                    });
                } else {
                    that.selectAction(scope, action);
                }
            },
            selectDataSource: function (scope, data, selected) {
                data.dataSourceName = selected.name;
                data.dataSourceId = selected._id;

                delete data.displayName;
                delete data.type;

                if(!scope.$$phase) {
                  scope.$apply(data);
                }
            },
            safeSelectDataSource: function (scope, data, selected) {
                var that = this;

                if (data.dataSourceId) {
                    motechConfirm('task.confirm.changeDataSource', 'header.confirm', function (val) {
                        if (val) {
                            that.selectDataSource(scope, data, selected);
                        }
                    });
                } else {
                    that.selectDataSource(scope, data, selected);
                }
            },
            selectDataSourceObject: function (scope, data, selected) {
                data.displayName = selected.displayName;
                data.type = selected.type;

                if(!scope.$$phase) {
                  scope.$apply(data);
                }
            },
            safeSelectDataSourceObject: function (scope, data, selected) {
                var that = this;

                if (data.type) {
                    motechConfirm('task.confirm.changeObject', 'header.confirm', function (val) {
                        if (val) {
                            that.selectDataSourceObject(scope, data, selected);
                        }
                    });
                } else {
                    that.selectDataSourceObject(scope, data, selected);
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
                var msg = scope.msg('task.error.saved') + '\n', i;

                for (i = 0; i < response.length; i += 1) {
                    msg += ' - ' + scope.msg(response.message, [response.field, response.objectName]) + '\n';
                }

                return msg;
            }
        };
    });

}());

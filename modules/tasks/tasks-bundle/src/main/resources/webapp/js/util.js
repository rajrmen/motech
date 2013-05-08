(function () {
    'use strict';

    /* Services */

    angular.module('channelUtils', ['ngResource']).factory('ChannelUtils', function () {
        return {
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
            selectAction: function (scope, action) {
                scope.task.action = {
                    displayName: action.displayName,
                    channelName: scope.selectedActionChannel.displayName,
                    moduleName: scope.selectedActionChannel.moduleName,
                    moduleVersion: scope.selectedActionChannel.moduleVersion,
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
            }
        };
    });

}());

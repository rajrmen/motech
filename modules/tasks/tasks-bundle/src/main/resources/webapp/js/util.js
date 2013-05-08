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
            removeTrigger: function (scope) {
                var li = angular.element("#trigger-" + scope.task.trigger.moduleName).parent('li');

                li.removeClass('selectedTrigger');
                li.removeClass("active");

                delete scope.task.trigger;

                if(!scope.$$phase) {
                  scope.$apply();
                }
            },
            selectAction: function (scope, action) {
                scope.task.action = {
                    displayName: action.displayName,
                    channelName: scope.selectedActionChannel.displayName,
                    moduleName: scope.selectedActionChannel.moduleName,
                    moduleVersion: scope.selectedActionChannel.moduleVersion,
                };

                if (action.subject !== undefined) {
                    scope.task.action.subject = action.subject;
                }

                if (action.serviceInterface !== undefined && action.serviceMethod !== undefined) {
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

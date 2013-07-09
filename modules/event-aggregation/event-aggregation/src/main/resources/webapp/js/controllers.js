(function () {
    'use strict';

    var eventAggregationModule = angular.module('event-aggregation');

    eventAggregationModule.controller('NewRulesController', function ($scope, AggregationRules, i18nService, $stateParams, $http) {
        $scope.scheduleTypeList = ['periodic', 'cron', 'custom'];

        if ($stateParams.ruleName !== undefined) {

            $scope.editMode = true;

            $scope.rule = AggregationRules.find({
                ruleName: $stateParams.ruleName
            }, function success(response) {
                $scope.scheduleType = $scope.rule.aggregationSchedule.scheduleType.split('_')[0];
                $scope.scheduleTypePartial = '../event-aggregation/resources/partials/new_' + $scope.scheduleType + '_schedule.html';
                $("#tagsinput").val($scope.rule.fields);
            });

        } else {
            $scope.scheduleType = 'periodic';

            $scope.changeScheduleType = function(scheduleType) {
                $scope.scheduleType = scheduleType;
            };

            if ($stateParams.scheduleType === 'cron' || $stateParams.scheduleType === 'custom') {
                $scope.scheduleType = $stateParams.scheduleType;
            }

            if ($scope.scheduleType === 'periodic') {
                $scope.rule = {
                    "aggregationSchedule": {
                        "scheduleType": $scope.scheduleType + "_request",
                        "startTimeInMillis": moment().valueOf().toString()
                    },
                    "state": "running"
                };
            } else {
                $scope.rule = {
                    "aggregationSchedule": {
                        "scheduleType": $scope.scheduleType + "_request"
                    },
                    "state": "running"
                };
            }
            $scope.scheduleTypePartial = '../event-aggregation/resources/partials/new_' + $scope.scheduleType + '_schedule.html';
        }

        $scope.errors = [];
        $scope.isSuccess = false;

        $scope.update = function() {
            $scope.rule.fields = $('#tagsinput').val().split(',');

            AggregationRules.update($scope.rule,
                function success(response) {
                    $scope.isSuccess = true;
                    $scope.errors = [];
                    window.location = "#/";
                },
                function error(response) {
                    $scope.errors = response.data;
                });
        };
    });

    eventAggregationModule.controller('RulesController', function ($scope, AggregationRules, i18nService, $stateParams, $http) {

        $scope.allRules = AggregationRules.query();
    });

    eventAggregationModule.controller('AggregationsController', function ($scope, AggregationRules, Aggregations, i18nService, $stateParams, $http) {

        $scope.eventStatus = $stateParams.eventStatus;

        $scope.rule = AggregationRules.find({
            ruleName: $stateParams.ruleName
        });

        $scope.aggregations = Aggregations.find({
            ruleName: $stateParams.ruleName,
            eventStatus: $stateParams.eventStatus
        });
    });
}());

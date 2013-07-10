(function () {
    'use strict';

    var app = angular.module('event-aggregation', ['motech-dashboard', 'AggregationRuleServices', 'ngCookies', 'bootstrap'])
        .config(['$stateProvider', function($stateProvider) {
            $stateProvider
                .state('event-aggregation', {abstract: true, templateUrl: '../event-aggregation/resources/index.html'})
                .state('event-aggregation.create', {url: '/event-aggregation/rules/create/:scheduleType', templateUrl: '../event-aggregation/resources/partials/new_rule.html', controller: 'NewRulesController'})
                .state('event-aggregation.rules', {url: '/event-aggregation/rules', templateUrl: '../event-aggregation/resources/partials/rules.html', controller: 'RulesController'})
                .state('event-aggregation.aggregations', {url: '/event-aggregation/rules/:ruleName/aggregations/:eventStatus', templateUrl: '../event-aggregation/resources/partials/aggregations.html', controller: 'AggregationsController'})
                .state('event-aggregation.ruleEdit', {url: '/event-aggregation/rules/:ruleName/edit', templateUrl: '../event-aggregation/resources/partials/new_rule.html', controller: 'NewRulesController'});
        }]);
}());

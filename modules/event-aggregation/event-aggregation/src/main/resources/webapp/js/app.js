(function () {
    'use strict';

    var app = angular.module('event-aggregation', ['motech-dashboard', 'AggregationRuleServices', 'ngCookies', 'bootstrap'])
        .config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
            $stateProvider
                .state('root', {url: '/', templateUrl: '../event-aggregation/resources/partials/rules.html', controller: 'RulesController'})
                .state('create', {url: '/rules/create/:scheduleType', templateUrl: '../event-aggregation/resources/partials/new_rule.html', controller: 'NewRulesController'})
                .state('rules', {url: '/rules', templateUrl: '../event-aggregation/resources/partials/rules.html', controller: 'RulesController'})
                .state('aggregations', {url: '/rules/:ruleName/aggregations/:eventStatus', templateUrl: '../event-aggregation/resources/partials/aggregations.html', controller: 'AggregationsController'})
                .state('ruleEdit', {url: '/rules/:ruleName/edit', templateUrl: '../event-aggregation/resources/partials/new_rule.html', controller: 'NewRulesController'});

                $urlRouterProvider.otherwise("/");
        }]);
}());

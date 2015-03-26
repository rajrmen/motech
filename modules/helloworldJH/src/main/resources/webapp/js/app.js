(function () {
    'use strict';

    /* App Module */

    angular.module('helloworldJHHelloWorld', ['motech-dashboard', 'helloworldJHHelloWorld.controllers', 'helloworldJHHelloWorld.directives', 'helloworldJHHelloWorld.services', 'ngCookies'])
        .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/helloWorld', {templateUrl: '../helloworldJH/resources/partials/say-hello.html', controller: 'HelloWorldController'});
    }]);
}());

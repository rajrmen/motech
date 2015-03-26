(function () {
    'use strict';

    /* Services */

    var services = angular.module('helloworldJHHelloWorld.services', ['ngResource']);

    services.factory('HelloWorld', function($resource) {
        return $resource('../helloworldJH/sayHello');
    });
}());

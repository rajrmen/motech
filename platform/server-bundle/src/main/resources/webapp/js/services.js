(function () {
    'use strict';

    angular.module('uiServices', ['ngResource']).factory('Menu', function($resource) {
        return $resource('module/menu');
    });

    angular.module('uiServices', ['ngResource']).factory('Critical', function($resource) {
        return $resource('module/critical/:moduleName');
    });

}());

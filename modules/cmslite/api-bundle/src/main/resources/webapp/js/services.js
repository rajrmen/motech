(function () {
    'use strict';

    /* Services */

    angular.module('resourceServices', ['ngResource']).factory('Resources', function ($resource) {
        return $resource('../cmsliteapi/resource/:type/:language/:name', {}, {
            allLanguages: { method: 'GET', params: { type: 'all', language: 'languages' }, isArray: true }
        });
    });

}());
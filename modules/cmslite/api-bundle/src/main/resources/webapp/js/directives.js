(function () {
    'use strict';

    /* Directives */

    var widgetModule = angular.module('motech-cmslite');

    widgetModule.directive('clearForm', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                $(element).on('hidden', function () {
                    $('#' + attrs.clearForm).clearForm();
                });
            }
        };
    });

}());
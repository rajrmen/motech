(function () {

    'use strict';

    /* Controllers */

    var widgetModule = angular.module('motech-cmslite');

    widgetModule.controller('ResourceCtrl', function ($scope, $http, Resources) {
        $scope.resources = [];
        $scope.resources = Resources.query();

        $scope.saveNewResource = function () {
            blockUI();
            $('#newResourceForm').ajaxSubmit({
                success: function (data) {
                    $scope.resources = Resources.query();
                    $('#newResourceModal').modal('hide');
                    unblockUI();
                },
                error: function (response) {
                    handleWithStackTrace('header.error', 'error.resource.save', response);
                    unblockUI();
                }
            });
        };

    });

}());

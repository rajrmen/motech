(function () {
    'use strict';

    /* Controllers */
    var emailModule = angular.module('motech-email');

    emailModule.controller('SendEmailController', function ($scope, SendEmailService) {
        $scope.mail = {};

        $scope.sendEmail = function () {
            SendEmailService.save(
                {},
                $scope.mail,
                function () {
                    motechAlert('email.header.success', 'email.sent');
                },
                function (response) {
                    handleWithStackTrace('email.header.error', 'server.error', response);
                }
            );
        };
    });

    emailModule.controller('EmailLoggingController', function($scope, EmailAuditService) {

        $scope.availableRange = ['all','table', 'month'];
        $scope.loggingRange = $scope.availableRange[0];

        $scope.change = function(selectedRange) {
            $scope.loggingRange = selectedRange;

            if($scope.loggingRange === 'month') {
                $('#exportDate').removeClass('hidden');
            } else {
                $('#exportDate').addClass('hidden');
            }
        };

        $(".monthPicker").focus(function () {
            $(".ui-datepicker-calendar").hide();
            $(".ui-datepicker-current").hide();
        });

        $scope.exportEmailLog = function () {
            //blockUI();
            $('#exportEmailLogModal').modal('hide');
            //window.open( "data:text/csv;charset=utf-8," + escape($scope.filter))
            $('#exportEmailLogForm').ajaxSubmit({
                success: function () {

                    $('#exportEmailLogForm').resetForm();
                    $('#exportEmailLogModal').modal('hide');
                    //unblockUI();
                },
                error: function (response) {
                    //handleResponse('email.header.error', 'email.logging.import.error', response);
                }
            });
        };

        $scope.closeExportEmailLogModal = function () {
            $('#exportEmailLogForm').resetForm();
            $('#exportEmailLogModal').modal('hide');
        };

        $scope.setFilterTitle = function(params) {
            var filters = [], s,
                statuses = [];
                filters = params;

            if (filters.length > 0) {
                s = "<b>Filtered by</b> " + filters;
            } else {
                s = "Filter by";
            }
            $('#filter-title').html(s);
        };
    });

    emailModule.controller('SettingsController', function ($scope, SettingsService) {
        $scope.settings = SettingsService.get();

        $scope.timeMultipliers = {
            'hours': $scope.msg('email.settings.log.units.hours'),
            'days': $scope.msg('email.settings.log.units.days'),
            'weeks': $scope.msg('email.settings.log.units.weeks'),
            'months': $scope.msg('email.settings.log.units.months'),
            'years': $scope.msg('email.settings.log.units.years')
        };

        $scope.submit = function () {

            SettingsService.save(
                {},
                $scope.settings,
                function () {
                    motechAlert('email.header.success', 'email.settings.saved');
                    $scope.settings = SettingsService.get();
                },
                function (response) {
                    handleWithStackTrace('email.header.error', 'server.error', response);
                }
            );
        };

        $scope.isNumeric = function (prop) {
            return $scope.settings.hasOwnProperty(prop) && /^[0-9]+$/.test($scope.settings[prop]);
        };

        $scope.purgeTimeControlsDisabled = function () {
            if ($scope.settings.logPurgeEnable.localeCompare("true") === 0) {
                return false;
            } else {
                return true;
            }
        };

    });
}());

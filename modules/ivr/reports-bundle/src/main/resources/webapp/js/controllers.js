'use strict';

/* Controllers */

function IvrReportsController($scope, $http) {
    $http({method: "GET", url: "../ivr_reports/api/call_detail_records"}).
        success(function(data) {
            $scope.callDetailRecords = data;
            console.log(data);
        });
}


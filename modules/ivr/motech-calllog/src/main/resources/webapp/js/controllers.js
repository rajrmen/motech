function CalllogController($scope, $rootScope,  CalllogService) {
    $rootScope.currentPage = 0;
    $scope.phoneNumber = "";
    $scope.sortColumn = "";

    $scope.$on('$viewContentLoaded', function () {

        $("#slider").slider({
            range:true,
            min:0,
            max:500,
            values:[0, 300],
            slide:function (event, ui) {
                $("#duration").text("" + ui.values[0] + " - " + ui.values[1] + " seconds");
            }
        });

        $("#duration").text("" + $("#slider").slider("values", 0) + " - " + $("#slider").slider("values", 1) + " seconds");
        $("#from").datetimepicker();
        $("#to").datetimepicker();
        $("#jqxexpander").jqxExpander();
    });

    $scope.search = function () {
        var min = $("#slider").slider("values",0);
        var max = $("#slider").slider("values",1);
        $scope.calllogs = CalllogService.query(
            {'phoneNumber': $scope.phoneNumber,
             'minDuration': min,
             'maxDuration': max,
             'fromDate': $("#from").val(),
             'toDate': $("#to").val(),
             'answered':$("#answered").is(':checked'),
             'busy':$("#busy").is(':checked'),
             'failed' :$('#failed').is(':checked'),
             'noAnswer': $('#noAnswer').is(':checked'),
             'unknown':$('#unknown').is(':checked'),
              'page' : $rootScope.currentPage,
              'sortColumn': $scope.sortColumn
            });
    };

    //$scope.calllogs = $scope.search();
    $scope.next = function() {
        $rootScope.currentPage++;
        $scope.search();
    };
    $scope.prev = function() {
        $rootScope.currentPage--;
        search();
    };
}

function CalllogController($scope, CalllogSearch, CalllogCount) {
    $scope.phoneNumber = "";
    $scope.sortColumn = "";
    $scope.currentPage = 0;
    $scope.pageCount = 0;

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

        $scope.pageCount=CalllogCount.query(
          {'phoneNumber': $scope.phoneNumber,
           'minDuration': min,
           'maxDuration': max,
           'fromDate': $("#from").val(),
           'toDate': $("#to").val(),
           'answered': $("#answered").is(':checked'),
           'busy': $("#busy").is(':checked'),
           'failed': $('#failed').is(':checked'),
           'noAnswer': $('#noAnswer').is(':checked'),
           'unknown': $('#unknown').is(':checked'),
           'page': $scope.currentPage,
           'sortColumn': $scope.sortColumn,
           'sortReverse' : false
          });

        $scope.calllogs = CalllogSearch.query(
            {'phoneNumber': $scope.phoneNumber,
             'minDuration': min,
             'maxDuration': max,
             'fromDate': $("#from").val(),
             'toDate': $("#to").val(),
             'answered': $("#answered").is(':checked'),
             'busy': $("#busy").is(':checked'),
             'failed': $('#failed').is(':checked'),
             'noAnswer': $('#noAnswer').is(':checked'),
             'unknown': $('#unknown').is(':checked'),
             'page': $scope.currentPage,
             'sortColumn': $scope.sortColumn,
             'sortReverse' : false
            });
    };

    $scope.prevPage = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
            $scope.search();
        }
    };

    $scope.nextPage = function () {
        if ($scope.currentPage < $scope.pageCount.count - 1) {
            $scope.currentPage++;
            $scope.search();
        }
    };

    $scope.setPage = function () {
        $scope.currentPage = this.selectedPage;
        $scope.search();
    };

    $scope.range = function (start, end) {
        var ret = [];
        if (!end) {
            end = start;
            start = 0;
        }
        for (var i = start; i < end; i++) {
            ret.push(i);
        }
        return ret;
    };
}

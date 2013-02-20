var widgetModule = angular.module('motech-mrs');

widgetModule.directive('datepicker', function() {

    var momentDateFormat = 'DD/MM/YYYY';

    function fromFormattedDate(formattedDate) {
        return moment(parseInt(moment(formattedDate, momentDateFormat).valueOf().toString())).format();
    }

    function toFormattedDate(startTimeInMillis) {
        if (typeof(startTimeInMillis) != 'undefined') {
            return moment(parseInt(startTimeInMillis)).format(momentDateFormat);
        }
    }

    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, element, attrs, ngModel) {
            ngModel.$parsers.push(fromFormattedDate);
            ngModel.$formatters.push(toFormattedDate);
            element.datepicker({
                dateFormat: 'dd/mm/yy',
                onSelect: function(formattedDate) {
                    ngModel.$setViewValue(element.val());
                }
            });
        }
    }
});

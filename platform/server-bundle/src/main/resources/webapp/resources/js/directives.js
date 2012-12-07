var widgetModule = angular.module('motech-widgets', []);

widgetModule.directive('bsPopover', function() {
    return function(scope, element, attrs) {
        $(element).popover();
    }
});

widgetModule.directive('taskPopover', function() {
    return function(scope, element, attrs) {
        $(element).popover({
            html: true,
            content: function() {
                return $(element).find('.content-task').html();
                }
        });
    }
});

widgetModule.directive('taskinputPopover', function() {
    return function(scope, element, attrs) {
        $(element).popover({
            html: true,
            content: function() {
                return $(element).find('.content-task').html();
                }
        });
    }
});
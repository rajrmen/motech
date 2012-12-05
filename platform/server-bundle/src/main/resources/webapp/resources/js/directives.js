var widgetModule = angular.module('motech-widgets', []);

widgetModule.directive('bsPopover', function() {
    return function(scope, element, attrs) {
        $(element).popover();
    }
});
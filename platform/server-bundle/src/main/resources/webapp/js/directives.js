(function () {
    'use strict';

    var widgetModule = angular.module('motech-widgets', []);

    widgetModule.directive('layout', function() {
        return {
            link: function(scope, elm, attrs) {
                var pageLayout, innerLayout, westSelector, eastSelector, $Container;


               /* function sizeCenterPane () {
                var $Container = $('#container')
                , $Pane = $('#outer-center')
                , $Content = $('#inner-center')
                , outerHeight = $Pane.outerHeight()
                // use a Layout utility to calc total height of padding+borders (also handles IE6)
                , panePadding = outerHeight - $.layout.cssHeight($Pane, outerHeight);
                // update the container height - *just* tall enough to accommodate #Content without scrolling
                $Container.height( $Pane.position().top + $Content.outerHeight() + panePadding + 30);
                // now resize panes to fit new container size
                pageLayout.resizeAll();
                }*/
                /*
                * Define options for all the layouts
                */
                scope.pageLayoutOptions = {

                    name: 'pageLayout',
                    resizeWithWindowDelay: 250, // delay calling resizeAll when window is *still* resizing
                    // resizeWithWindowMaxDelay: 2000, // force resize every XX ms while window is being resized
                    resizable: true,
                    slidable: true,
                    closable: true,
                    north__paneSelector: "#outer-north",
                    west__paneSelector: "#outer-west",
                    center__paneSelector: "#outer-center",
                    south__paneSelector: "#outer-south",
                    south__spacing_open: 0,
                    south__spacing_closed: 0,
                    north__spacing_open: 0,
                    west__spacing_open: 12,
                    spacing_closed: 20,
                    north__size: 100,
                   // north__showOverflowOnHover: true,
                    west__size: 350,
                    useStateCookie: true,
                    cookie__keys: "north.size,north.isClosed,south.size,south.isClosed,west.size,west.isClosed,east.size,east.isClosed",
                    showErrorMessages: true, // some panes do not have an inner layout
                    resizeWhileDragging: true,
                    center__minHeight: 100,
                    contentSelector: ".ui-widget-content",
                    togglerContent_open: '<i class=""></i>',
                    togglerContent_closed: '<i class=""></i>',
                    togglerAlign_closed: "top", // align to top of resizer
                    togglerAlign_open: "top", // align to top of resizer
                    togglerLength_open: 35, // NONE - using custom togglers INSIDE west-pane
                    togglerLength_closed: 35,
                    buttonClass: "button", // default = 'ui-layout-button'
                    slideTrigger_open: "click",  // default
                    initClosed: false,
                    south__initClosed: false

                };

                // first set a 'fixed height' on the container so it does not collapse...
                /*$Container = $('#container');
                $Container.height( $(window).height() - $Container.offset().top );*/

                // create the page-layout, which will ALSO create the tabs-wrapper child-layout
                pageLayout = angular.element(elm).layout( scope.pageLayoutOptions);

                pageLayout.sizePane('north', 101);
                pageLayout.sizePane('south', 30);
                // save selector strings to vars so we don't have to repeat it
                // must prefix paneClass with "body > " to target ONLY the outerLayout panes
                westSelector = "body span .ui-layout-west"; // outer-west pane
                eastSelector = "body > span > .outer-center > .ui-layout-pane-east"; // outer-east pane
                // CREATE SPANs for close-buttons - using unique IDs as identifiers
                //$("<span>close</span>").attr("id", "west-closer" ).prependTo( westSelector );
                // $("<span>eclose</span>").attr("id", "east-closer").prependTo( eastSelector );

                scope.pageInnerLayoutOptions = {
                    name: 'pageLayout',
                    resizeWithWindowDelay: 250, // delay calling resizeAll when window is *still* resizing
                    // resizeWithWindowMaxDelay: 2000, // force resize every XX ms while window is being resized
                    resizable: true,
                    slidable: true,
                    closable: true,
                    east__paneSelector: "#inner-east",
                    center__paneSelector: "#inner-center",
                    east__spacing_open: 12,
                    spacing_closed: 20,
                    // north__showOverflowOnHover: true,
                    east__size: 250,
                    showErrorMessages: true, // some panes do not have an inner layout
                    resizeWhileDragging: true,
                    center__minHeight: 100,
                    contentSelector: ".ui-widget-content",
                    togglerContent_open: '<i class=""></i>',
                    togglerContent_closed: '<i class=""></i>',
                    autoReopen: false, // auto-open panes that were previously auto-closed due to 'no room'
                    noRoom: true,
                    togglerAlign_closed: "top", // align to top of resizer
                    togglerAlign_open: "top",
                    togglerLength_open: 35,
                    togglerLength_closed: 35,
                    initClosed: true
                };
                innerLayout = pageLayout.panes.center.layout(scope.pageInnerLayoutOptions);

                // now RESIZE the container to be a perfect fit
               // sizeCenterPane();
            }
        };
    });

    widgetModule.directive('elementactive', function() {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                $(element).find('li').livequery(function () {
                    $(this).on({
                         click: function (e) {
                             $(element).children().removeClass('active');
                             $(this).addClass('active');
                             $(element).children().children().attr("href");
                             $('#side-nav').children().addClass('hidden');
                             //$($(this).children().attr("href")).removeClass('hidden');
                             $($(this).children().attr("href").replace('#', ".")).removeClass('hidden');
                         }
                    });

                });
            }
        };
    });

    widgetModule.directive('overflowChangePanel', function () {
            return {
                restrict: 'A',
                link: function (scope, element, attrs) {
                    $(element).find('.overflowChange').livequery(function () {
                        $(this).on({
                            mouseover: function (e) {
                                if (!e.target.classList.contains("overflowChange")) {
                                    $(element).parent().parent().css('overflow', 'visible');
                                    $(element).parent().parent().css('z-index', '10');
                                }
                            },
                            mouseout: function (e) {
                                if (!$(this).hasClass("open")) {
                                    $(element).parent().parent().css('overflow', 'hidden');
                                    $(element).parent().parent().css('z-index', '0');
                                }
                            }
                        });
                    });
                }
            };
        });

    widgetModule.directive('bsPopover', function() {
        return function(scope, element, attrs) {
            $(element).popover({
                content: function () {
                    return attrs.bsPopover;
                }
            });
        };
    });

    widgetModule.directive('elementFocus', function() {
        return function (scope, element, atts) {
            element[0].focus();
        };
    });

    widgetModule.directive('goToTop', function () {
        return function (scope, element, attrs) {
            $(element).click(function () {
                $('body, html').animate({
                    scrollTop: 0
                }, 800);

                return false;
            });
        };
    });

    widgetModule.directive('goToEnd', function () {
        return function (scope, element, attrs) {
            $(element).click(function () {
                $('body, html').animate({
                    scrollTop: $(document).height()
                }, 800);

                return false;
            });
        };
    });

    widgetModule.directive('ngDateTimePicker', function () {
        return function (scope, element, attributes) {
            $(element).datetimepicker();
        };
    });

    widgetModule.directive('serverTime', function () {
        return function (scope, element, attributes) {
            var getTime = function() {
                $.post('gettime', function(time) {
                     $(element).text(time);
                });
            };

            getTime();
            window.setInterval(getTime, 60000);
        };
    });

    widgetModule.directive('serverUpTime', function () {
        return function (scope, element, attributes) {
            var getUptime = function() {
                $.post('getUptime', function(time) {
                     $(element).text(moment(time).fromNow());
                });
            };

            getUptime();
            window.setInterval(getUptime, 60000);
        };
    });

    widgetModule.directive('motechModules', function ($compile, $timeout, $http, $templateCache) {
        var templateLoader;

        return {
            restrict: 'E',
            replace : true,
            transclude: true,
            compile: function (tElement, tAttrs, scope) {
                var url = '../server/resources/partials/motech-modules.html',

                templateLoader = $http.get(url, {cache: $templateCache})
                    .success(function (html) {
                        tElement.html(html);
                    });

                return function (scope, element, attrs) {
                    templateLoader.then(function () {
                        element.html($compile(tElement.html())(scope));
                    });
                };
            }
        };
    });

}());

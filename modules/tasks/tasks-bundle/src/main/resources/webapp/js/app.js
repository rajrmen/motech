'use strict';

/* App Module */

angular.module('motech-tasks', ['motech-dashboard', 'channelServices', 'taskServices', 'activityServices', 'ngCookies', 'bootstrap']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/dashboard', {templateUrl: '../tasks/partials/tasks.html', controller: DashboardCtrl}).
            when('/task/new', {templateUrl: '../tasks/partials/form.html', controller: ManageTaskCtrl}).
            when('/task/:taskId/edit', {templateUrl: '../tasks/partials/form.html', controller: ManageTaskCtrl}).
            when('/task/:taskId/log', {templateUrl: '../tasks/partials/history.html', controller: LogCtrl}).
            otherwise({redirectTo: '/dashboard'});
    }
]).filter('filterPagination', function () {
    return function (input, start) {
        start = +start;
        return input.slice(start);
    }
}).filter('fromNow', function () {
    return function(date) {
        return moment(date).fromNow();
    };
}).directive('doubleClick', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.dblclick(function () {
                var parent = element.parent();

                if (parent.hasClass('trigger')) {
                    delete scope.selectedTrigger;
                    delete scope.task.trigger;
                    scope.draggedTrigger.display = scope.draggedTrigger.channel;
                } else if (parent.hasClass('action')) {
                    delete scope.selectedAction;
                    delete scope.task.action;
                    scope.draggedAction.display = scope.draggedAction.channel;
                }

                scope.$apply();
            });
        }
    }
}).directive('expandaccordion', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            $('.accordion').on('show', function (e) {
                $(e.target).siblings('.accordion-heading').find('.accordion-toggle i').removeClass("icon-chevron-right").addClass('icon-chevron-down');
            });

            $('.accordion').on('hide', function (e) {
                $(e.target).siblings('.accordion-heading').find('.accordion-toggle i').removeClass("icon-chevron-down").addClass("icon-chevron-right");
            });
        }
    }
}).directive('draggable', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.draggable({
                revert: true,
                start: function (event, ui) {
                    if (element.hasClass('draggable')) {
                        element.find("div:first-child").popover('hide');
                    }
                }
            });
        }
    }
}).directive('droppable', function ($compile) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.droppable({
                drop: function (event, ui) {
                    var channelName, moduleName, moduleVersion,
                        parent, value, position, eventKey, dragType, dropType, dropElement;

                    var position = function(dropElement, dragElement) {
                        var sel, range;
                        if (window.getSelection) {
                            sel = window.getSelection();
                            if (sel.getRangeAt && sel.rangeCount) {
                                range = sel.getRangeAt(0);
                                range.deleteContents();
                                if (range.commonAncestorContainer.parentNode == dropElement[0] || range.commonAncestorContainer == dropElement[0]) {
                                    var el = document.createElement("div");
                                    el.innerHTML = dragElement[0].outerHTML;
                                    var frag = document.createDocumentFragment(), node, lastNode;
                                    while ( (node = el.firstChild) ) {
                                        lastNode = frag.appendChild(node);
                                    }
                                    $compile(frag)(scope);
                                    range.insertNode(frag);

                                    if (lastNode) {
                                        range = range.cloneRange();
                                        range.setStartAfter(lastNode);
                                        range.collapse(true);
                                        sel.removeAllRanges();
                                        sel.addRange(range);
                                    }
                                } else {
                                    $compile(dragElement)(scope);
                                    dropElement.append(dragElement);
                                }
                            }
                        } else if (document.selection && document.selection.type != "Control") {
                            document.selection.createRange().pasteHTML(dragElement[0].outerHTML);
                        }
                    }

                    if (angular.element(ui.draggable).hasClass('triggerField') && element.hasClass('actionField')) {
                        dragType = angular.element(ui.draggable).data('type');
                        dropType = angular.element(element).data('type');

                        dropElement = angular.element(element);

                        if (dropType === 'DATE') {
                            dropElement.text('');
                        }

                        dropElement.find('em').remove();

                        var dragElement = angular.element(ui.draggable).clone()
                        dragElement.css("position", "relative");
                        dragElement.css("left", "0px");
                        dragElement.css("top", "0px");
                        position(dropElement, dragElement);

                    } else if (angular.element(ui.draggable).hasClass('task-panel') && (element.hasClass('trigger') || element.hasClass('action'))) {
                        channelName = angular.element(ui.draggable).data('channel-name');
                        moduleName = angular.element(ui.draggable).data('module-name');
                        moduleVersion = angular.element(ui.draggable).data('module-version');

                        if (element.hasClass('trigger')) {
                            scope.setTaskEvent('trigger', channelName, moduleName, moduleVersion);
                            delete scope.task.trigger;
                            delete scope.selectedTrigger;
                        } else if (element.hasClass('action')) {
                            scope.setTaskEvent('action', channelName, moduleName, moduleVersion);
                            delete scope.task.action;
                            delete scope.selectedAction;
                        }
                    } else if (angular.element(ui.draggable).hasClass('dragged') && element.hasClass('task-selector')) {
                        parent = angular.element(ui.draggable).parent();

                        if (parent.hasClass('trigger')) {
                            delete scope.draggedTrigger;
                            delete scope.task.trigger;
                            delete scope.selectedTrigger;
                        } else if (parent.hasClass('action')) {
                            delete scope.draggedAction;
                            delete scope.task.action;
                            delete scope.selectedAction;
                        }
                    }

                    scope.$apply();
                }
            });
        }
    }
}).directive('number', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.keypress(function (evt) {
                var charCode = (evt.which) ? evt.which : evt.keyCode,
                    allow = [44, 46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57]; // char code: , . 0 1 2 3 4 5 6 7 8 9

                return allow.indexOf(charCode) >= 0;
            });
        }
    };
}).directive('contenteditable', function() {
    return {
        restrict: 'A',
        require: '?ngModel',
        link: function(scope, element, attrs, ngModel) {
            var read;
            if (!ngModel) {
                return;
            }
            ngModel.$render = function() {
                var container = $('<div></div>');
                container.html(ngModel.$viewValue);
                container.find('.editable').attr('contenteditable', true);
                return element.html(container.html());
            };

            element.bind('blur keyup change mouseleave', function() {
                if (ngModel.$viewValue !== $.trim(element.html())) {
                    return scope.$apply(read);
                }
            });

            return read = function() {
                var container = $('<div></div>');
                container.html($.trim(element.html()));
                container.find('.editable').attr('contenteditable', false);
                return ngModel.$setViewValue(container.html());
            };
        }
    }
}).directive('editableContent', function($compile, $timeout) {
   return {
       restrict: 'E',
       transclude: true,
       scope: {
           data: '=',
           index: '='
       },
       link: function(scope, elm, attrs) {
           var type = scope.data.type, toCompile;

           switch (type) {
            case 'TEXTAREA':
                toCompile = '<div contenteditable ng-model="data.value" droppable class="actionField input-block-level" data-index="{{index}}" data-type="{{data.type}}"><span ng-bind-html-unsafe="data.value" ng-model="data.value" class="html"><h1 contentEditable="false"><span class="editable"></span></h1></span></div>';
                break;
            case 'NUMBER':
                toCompile = '<div number div-placeholder="placeholder.numberOnly" contenteditable ng-model="data.value" droppable class="actionField input-block-level" data-index="{{index}}" data-type="{{data.type}}"><span ng-bind-html-unsafe="data.value" ng-model="data.value" class="html"><h1 contentEditable="false"><span class="editable"></span></h1></span></div>';
                break;
            case 'UNICODE':
                toCompile = '<div contenteditable ng-model="data.value" droppable class="actionField input-block-level" data-index="{{index}}" data-type="{{data.type}}"><span ng-bind-html-unsafe="data.value" ng-model="data.value" class="html"><h1 contentEditable="false"><span class="editable"></span></h1></span></div>';
                break;
            case 'DATE':
                toCompile = '<input type="hidden" data-index="{{index}}" datetime-picker-input/><div date div-placeholder="placeholder.dateOnly" contenteditable ng-model="data.value" droppable class="actionField input-block-level" data-index="{{index}}" data-type="{{data.type}}" datetime-picker><span ng-bind-html-unsafe="data.value" ng-model="data.value" class="html"><h1 contentEditable="false"><span class="editable"></span></h1></span></div>';
                break;
           }

           elm.append($compile(toCompile)(scope));

           $timeout(function () {
                elm.find('div').focusout();
           });
       }

   }
}).directive('manipulationpopover', function($compile, $timeout) {
    return {
        restrict: 'A',
        link: function(scope, el, attrs) {
            var data = '<ul><li><span ng-click="selectManipulation(\'ToUpper\', i)">test</span></li><li><span ng-click="selectManipulation(\'ToLower\', i)">{{msg("ToLower")}}</span></li><li><span ng-click="selectManipulation(\'CapitalizeFirstLetter\', i)">{{msg("capitalizeFirstLetter")}}</span></li><li><span ng-click="selectManipulation(\'Join\', i)">{{msg("join")}}</span></li></ul>';
            el.popover({
                template : '<div contenteditable="false" class="popover"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title"></h3><div class="popover-content"><p></p></div></div></div>',
                title: "String Manipulation",
                html: true,
                content: function() {

                    $timeout(function(){
                        $compile(el.data('popover').tip())(scope);
                    });
                    return data;
                },
                placement: "top",
                trigger: 'click'
            });
        },
    };
}).directive('datetimePicker', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.focus(function () {
                $(this).prev('input').datepicker('show');
            });
        }
    }
}).directive('datetimePickerInput', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.datetimepicker({
                showTimezone: true,
                useLocalTimezone: true,
                dateFormat: 'yy-M-dd',
                timeFormat: 'HH:mm z',
                onSelect: function(dateTex, inst) {
                    (inst.input || inst.$input).next('div').text(dateTex);
                }
            });
        }
    }
}).directive('divPlaceholder', function() {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var parent = scope, curText;

            while (parent.msg === null || parent.msg === undefined) {
                parent = parent.$parent;
            }

            curText = parent.msg(attrs.divPlaceholder);

            if (!element.text().trim().length) {
                element.text('<em style="color: gray;">' + curText + '</em>');
            }

            element.focusin(function() {
                if ($(this).text().toLowerCase() == curText.toLowerCase() || !$(this).text().length) {
                    $(this).empty();
                }
            });

            element.focusout(function() {
                if ($(this).text().toLowerCase() == curText.toLowerCase() || !$(this).text().length) {
                    $(this).html('<em style="color: gray;">' + curText + '</em>');
                }
            });
        }
    }
});

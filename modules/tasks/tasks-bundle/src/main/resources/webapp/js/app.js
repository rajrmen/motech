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
                                if (range.commonAncestorContainer.parentNode == dropElement) {
                                    var el = document.createElement("div");
                                    el.innerHTML = dragElement[0].outerHTML;
                                    var frag = document.createDocumentFragment(), node, lastNode;
                                    while ( (node = el.firstChild) ) {
                                        lastNode = frag.appendChild(node);
                                    }
                                    range.insertNode(frag);

                                    if (lastNode) {
                                        range = range.cloneRange();
                                        range.setStartAfter(lastNode);
                                        range.collapse(true);
                                        sel.removeAllRanges();
                                        sel.addRange(range);
                                    }
                                }
                            }
                        } else if (document.selection && document.selection.type != "Control") {
                            document.selection.createRange().pasteHTML(dragElement[0].outerHTML);
                        }
                    }

                    if (angular.element(ui.draggable).hasClass('triggerField') && element.hasClass('actionField')) {
                        dragType = angular.element(ui.draggable).data('type');
                        dropType = angular.element(element).data('type');

                        if ((dragType == 'UNICODE' || dragType == 'TEXTAREA') && dropType == 'NUMBER') {
                            return;
                        }

                        dropElement = angular.element(element);
                        var dragElement = angular.element(ui.draggable).clone()
                        dragElement.css("position", "relative");
                        dragElement.css("left", "0px");
                        dragElement.css("top", "0px");
                        position(dropElement, dragElement)

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
}).directive('integer', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.keypress(function (evt) {
                var charCode = (evt.which) ? evt.which : evt.keyCode,
                    caret = element.caret(), value = element.val(),
                    begin = value.indexOf('{{'), end = value.indexOf('}}') + 2;

                if (begin !== -1) {
                    while (end !== -1) {
                        if (caret > begin && caret < end) {
                            return false;
                        }

                        begin = value.indexOf('{{', end);
                        end = begin === -1 ? -1 : value.indexOf('}}', begin) + 2;
                    }
                }

                return !(charCode > 31 && (charCode < 48 || charCode > 57));
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
}).directive('editableContent', function($compile) {
   return {
       restrict: 'E',
       transclude: true,
       scope: {
           data: '='
       },
       link: function(scope, elm, attrs) {
           elm.append($compile(
               '<div contenteditable ui-if="data.type == \'TEXTAREA\'" rows="5" ng-model="data.value" droppable class="actionField input-block-level" data-index="{{$index}}" data-type="{{data.type}}"><span ng-bind-html-unsafe="data.value" ng-model="data.value" class="html"><h1 contentEditable="false"><span class="editable"></span></h1></span></div>' +
               '<div contenteditable ui-if="data.type == \'NUMBER\'" type="text" ng-model="data.value" droppable class="actionField input-block-level" data-index="{{$index}}" data-type="{{data.type}}" integer placeholder="{{msg(\'placeholder.numberOnly\')}}"><span ng-bind-html-unsafe="data.value" ng-model="data.value" class="html"><h1 contentEditable="false"><span class="editable"></span></h1></span></div>' +
               '<div contenteditable ui-if="data.type == \'UNICODE\'" type="text" ng-model="data.value" droppable class="actionField input-block-level" data-index="{{$index}}" data-type="{{data.type}}"><span ng-bind-html-unsafe="data.value" ng-model="data.value" class="html"><h1 contentEditable="false"><span class="editable">Title</span></h1></span></div>'
           )(scope));
       }

   }

});

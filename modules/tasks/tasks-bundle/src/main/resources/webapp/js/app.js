'use strict';

/* App Module */

angular.module('motech-tasks', ['motech-dashboard', 'channelServices', 'taskServices', 'activityServices',
                                'dataSourceServices', 'ngCookies', 'bootstrap']).config(['$routeProvider',
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
                            document.selection.createRange().pasteHTML($compile(dragElement[0].outerHTML)(scope));
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

                        var dragElement = angular.element(ui.draggable).clone();
                        dragElement.css("position", "relative");
                        dragElement.css("left", "0px");
                        dragElement.css("top", "0px");
                        dragElement.attr("manipulationpopover", "");
                        dragElement.addClass('pointer');
                        dragElement.removeAttr("ng-repeat");
                        dragElement.removeAttr("draggable");

                        if (dragElement.data('prefix') === 'ad') {
                            dragElement.text(dragElement.data('source') + '.' + dragElement.data('object') + "#" + dragElement.data('object-id') + '.' + dragElement.text());
                        }

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
}).directive('contenteditable', function($compile) {
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
                if (container.text()!="") {
                    container = container.contents();
                    $compile(container)(scope);
                } else {
                    container = container.html();
                }
                return element.html(container);
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
            var manipulationOptions = '', title = '';
            var elType = el.data('type');
            var msgScope = scope;
            while (msgScope.msg==undefined) {
                msgScope = msgScope.$parent;
            }
            if (elType == 'UNICODE' || elType == 'TEXTAREA') {
                title = msgScope.msg('stringManipulation', '');
                manipulationOptions = '<ul><li class="padding-botton6"><span class="pointer" setmanipulation="toUpper">'+msgScope.msg('toUpper')+'</span></li><li class="padding-botton6"><span class="pointer" setmanipulation="toLower">'+msgScope.msg('toLower')+'</span></li><li class="padding-botton6"><span class="pointer" setmanipulation="capitalize">'+msgScope.msg('capitalizeFirstLetter')+'</span></li><li class="padding-botton6"><span class="pointer" setmanipulation="join">'+msgScope.msg('join')+'</span><input id="joinSeparator" join-update class="input-popover" style="display:none" type="text" ng-model="joinSeparator"/></li></ul>';
            } else if (elType == 'DATE') {
                title = msgScope.msg('dataManipulation', '');
                manipulationOptions = '<span>'+msgScope.msg('date.time')+'</span></br><input id="dateFormat" setmanipulation="dateTime" type="text" ng-model="dateFormat"/>';
            }

            el.bind('click', function() {
                var man = $("[ismanipulate=true]").text();
                if (man.length == 0) {
                    angular.element(this).attr('ismanipulate', 'true');
                } else {
                    angular.element(this).removeAttr('ismanipulate');
                }
            });

            if (elType == 'UNICODE' || elType == 'TEXTAREA' || elType == 'DATE') {
                el.popover({
                    template : '<div contenteditable="false" class="popover dragpopover"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title"></h3><div class="popover-content"><p></p></div></div></div>',
                    title: title,
                    html: true,
                    content: function() {
                        var elem = $(manipulationOptions);
                        elem.find('span').replaceWith(function() {
                            var element = $("[ismanipulate=true]");
                            var manipulation = element.attr('manipulate');
                            if (manipulation != undefined && manipulation.indexOf(this.attributes.getNamedItem('setmanipulation').value) != -1) {
                                $(this).append('<span class="icon-ok" style="float: right"></span>');
                                if (manipulation.indexOf("join") != -1) {
                                    $(this.nextElementSibling).css({ 'display' : '' });
                                    elem.find('input').val(manipulation.slice(manipulation.indexOf("join")+5, manipulation.indexOf(")")));
                                } else {
                                    elem.find('input').val("");
                                }
                            }
                            return $(this)[0].outerHTML;
                        });
                        if (elem.is('span')) {
                           var element = $("[ismanipulate=true]");
                           var manipulation = element.attr('manipulate');
                           if (manipulation != undefined) {
                               elem.first().append('<span class="icon-ok" style="float: right"></span>');
                               elem.find('input').val(manipulation.slice(manipulation.indexOf("dateTime")+9, manipulation.indexOf(")")));
                           } else {
                               elem.find('input').val("");
                           }
                        }
                        return $compile(elem)(msgScope);
                    },
                    placement: "top",
                    trigger: 'click'
                });
            }
        }
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
}).directive('setmanipulation', function() {
    return {
        restrict : 'A',
        require: '?ngModel',
        link : function (scope, el, attrs, ngModel) {

            el.bind("click", function() {
                var manipulateElement = $("[ismanipulate=true]");
                var joinSeparator = "";
                var reg;
                if (manipulateElement.data('type') != "DATE") {
                    var manipulation = this.getAttribute("setManipulation");
                    var manipulateAttributes = manipulateElement.attr("manipulate") ? manipulateElement.attr("manipulate") : "";
                    if (manipulateAttributes.indexOf(manipulation) != -1) {
                        var manipulationAttributesIndex = manipulateElement.attr("manipulate").indexOf(manipulation);
                        if (manipulation != "join") {
                            reg = new RegExp(manipulation, "g")
                            manipulateAttributes = manipulateAttributes.replace(reg, '');
                        } else {
                            joinSeparator = manipulation + "\\(" + this.nextElementSibling.value + "\\)";
                            reg = new RegExp(joinSeparator, "g")
                            manipulateAttributes = manipulateAttributes.replace(reg, '');
                        }
                    } else {
                        manipulateAttributes = manipulateAttributes.replace(/ +(?= )/g, '');
                        if (manipulation != "join") {
                            manipulateAttributes = manipulateAttributes + manipulation + " ";
                        } else {
                            joinSeparator = this.nextElementSibling.value;
                            manipulateAttributes = manipulateAttributes + manipulation + "(" + $("#joinSeparator").val() + ")" + " ";
                        }
                    }
                    manipulateElement.attr('manipulate', manipulateAttributes);
                }
                if (this.children.length == 0) {
                   $(this).append('<span class="icon-ok" style="float: right"></span>');
                   $(this.nextElementSibling).css({ 'display' : '' });
                } else {
                    $(this).children().remove();
                    $(this.nextElementSibling).css({ 'display' : 'none' });
                }
            });

            el.bind("focusout focusin keyup", function() {
                var dateFormat = ngModel.$viewValue ? ngModel.$viewValue : "";
                var manipulateElement = $("[ismanipulate=true]");
                if (manipulateElement.data("type") == 'DATE') {
                    var deleteButton = $('<span class="icon-remove" style="float: right"></span>');
                    var manipulation = this.getAttribute("setManipulation") + "("+ dateFormat + ")";
                        manipulateElement.removeAttr("manipulate");
                    if (dateFormat.length != 0 && this.previousSibling.previousSibling.children.length == 0) {
                        manipulateElement.attr("manipulate", manipulation) ;
                        $(this.previousSibling.previousSibling).append(deleteButton);
                        $(this.previousSibling.previousSibling).append('<span class="icon-ok" style="float: right"></span>');
                    } else if (dateFormat.length == 0) {
                        $(this.previousSibling.previousSibling).children().remove();
                    }
                    $('span.icon-remove').live('click', function() {
                        $(this.parentElement).children().remove();
                        $("[ismanipulate=true]").removeAttr("manipulate");
                        $("#dateFormat").value('');
                    });
                }
            });
        }
    }
}).directive('joinUpdate', function() {
      return {
          restrict : 'A',
          require: '?ngModel',
          link : function (scope, el, attrs, ngModel) {
              el.bind("focusout focusin keyup", function() {
                  var joinSeparator = ngModel.$viewValue ? ngModel.$viewValue : "";
                  var manipulateElement = $("[ismanipulate=true]");
                  var manipulation = "join("+ joinSeparator + ")";
                  var elementManipulation = manipulateElement.attr("manipulate") ;
                  elementManipulation = elementManipulation.replace(/join\(.*?\)/g, manipulation);
                  manipulateElement.attr("manipulate", elementManipulation);
              });
          }
      }
});

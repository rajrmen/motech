(function () {
    'use strict';

    var emailModule = angular.module('motech-email');

    emailModule.directive('richTextEditor', function() {
        return {
            restrict : "A",
            require : 'ngModel',
            link : function(scope, element, attrs, ctrl) {
                var textarea = element.find('.textarea');

                textarea.livequery(function() {
                    var editor;

                    $(this).wysihtml5({
                        "image": false,
                        "color": false,
                        "link": false,
                        events: {
                            change: function() {
                                scope.$apply(function() {
                                    ctrl.$setViewValue(editor.getValue());
                                });
                            }
                        }
                    });

                    editor = $(this).data('wysihtml5').editor;

                    // model -> view
                    ctrl.$render = function() {
                        textarea.html(ctrl.$viewValue);
                        editor.setValue(ctrl.$viewValue);
                    };

                    // load init value from DOM
                    ctrl.$render();
                });
            }
        };
    }).directive('purgeTime', function(){
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, modelCtrl) {
                modelCtrl.$parsers.push(function (inputValue) {
                    if (inputValue === undefined) {
                        return '';
                    }

                    var transformedInput = inputValue.toLowerCase().replace(/[a-z]+$/, '');

                    if (transformedInput !== inputValue) {
                        modelCtrl.$setViewValue(transformedInput);
                        modelCtrl.$render();
                    }

                    return transformedInput;
                });
            }
        };
    });

    emailModule.directive('gridDatePickerFrom', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element),
                    endDateTextBox = angular.element('#dateTimeTo');

                elem.datetimepicker({
                    dateFormat: "yy-mm-dd",
                    changeMonth: true,
                    changeYear: true,
                    maxDate: +0,
                    timeFormat: "HH:mm:ss",
                    onSelect: function (selectedDateTime){
                        endDateTextBox.datetimepicker('option', 'minDate', elem.datetimepicker('getDate') );
                    }
                });
            }
        };
    });

    emailModule.directive('gridDatePickerTo', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element),
                    startDateTextBox = angular.element('#dateTimeFrom');

                elem.datetimepicker({
                    dateFormat: "yy-mm-dd",
                    changeMonth: true,
                    changeYear: true,
                    maxDate: +0,
                    timeFormat: "HH:mm:ss",
                    onSelect: function (selectedDateTime){
                        startDateTextBox.datetimepicker('option', 'maxDate', elem.datetimepicker('getDate') );
                    }
                });
            }
        };
    });

    emailModule.directive('autoComplete', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                angular.element(element).autocomplete({
                    minLength: 2,
                    source: function (request, response) {
                       $.getJSON('../email/emails/available/?autoComplete=' + attrs.autoComplete, request, function (data) {
                            response(data);
                        });
                    }
                });
            }
        };
    });

    emailModule.directive('jqgridSearch', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element),
                    table = angular.element('#' + attrs.jqgridSearch),
                    eventType = elem.data('event-type'),
                    timeoutHnd,
                    filter = function (time) {
                        var field = elem.data('search-field'),
                            value = elem.data('search-value'),
                            type = elem.data('field-type') || 'string',
                            url = parseUri(table.jqGrid('getGridParam', 'url')),
                            query = {},
                            params = '?',
                            array = [],
                            prop;

                        for (prop in url.queryKey) {
                            if (prop !== field) {
                                query[prop] = url.queryKey[prop];
                            }
                        }

                        switch (type) {
                        case 'boolean':
                            query[field] = url.queryKey[field].toLowerCase() !== 'true';

                            if (query[field]) {
                                elem.find('i').removeClass('icon-ban-circle').addClass('icon-ok');
                            } else {
                                elem.find('i').removeClass('icon-ok').addClass('icon-ban-circle');
                            }
                            break;
                        case 'array':
                            if (elem.children().hasClass("icon-ok")) {
                                elem.children().removeClass("icon-ok").addClass("icon-ban-circle");
                            } else if (elem.children().hasClass("icon-ban-circle")) {
                                elem.children().removeClass("icon-ban-circle").addClass("icon-ok");
                                array.push(value);
                            }
                            angular.forEach(url.queryKey[field].split(','), function (val) {
                                if (angular.element('#' + val).children().hasClass("icon-ok")) {
                                    array.push(val);
                                }
                            });

                            query[field] = array.join(',');
                            break;
                        default:
                            query[field] = elem.val();
                        }

                        for (prop in query) {
                            params += prop + '=' + query[prop] + '&';
                        }

                        params = params.slice(0, params.length - 1);

                        if (timeoutHnd) {
                            clearTimeout(timeoutHnd);
                        }

                        timeoutHnd = setTimeout(function () {
                            scope.setFilterTitle(params);
                            jQuery('#' + attrs.jqgridSearch).jqGrid('setGridParam', {
                                url: '../email/emails' + params
                            }).trigger('reloadGrid');
                        }, time || 0);
                    };

                switch (eventType) {
                case 'keyup':
                    elem.keyup(function () {
                        filter(500);
                    });
                    break;
                case 'change':
                    elem.change(filter);
                    break;
                default:
                    elem.click(filter);
                }
            }
        };
    });

    emailModule.directive('emailloggingGrid', function($compile, $http, $templateCache) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters, colPos;

                elem.jqGrid({
                    url: '../email/emails?name=&deliveryStatus=PENDING,ERROR,RECEIVED,SENT,UNKNOWN',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    shrinkToFit: true,
                    autowidth: true,
                    rownumbers: true,
                    rowNum: 10,
                    rowList: [10, 20, 50],
                    colModel: [{
                        name: 'direction',
                        index: 'direction',
                        hidden: true
                    }, {
                        name: 'deliveryStatus',
                        index: 'deliveryStatus',
                        align: 'center'
                    }, {
                        name: 'toAddress',
                        index: 'toAddress'
                    },{
                        name: 'fromAddress',
                        index: 'fromAddress'
                    }, {
                        name: 'subject',
                        index: 'subject'
                    }, {
                        name: 'message',
                        index: 'message',
                        sortable: false
                    }, {
                        name: 'deliveryTime',
                        index: 'deliveryTime'
                    }, {
                        name: 'modifiedDate',
                        index: 'modifiedDate',
                        sortable: false
                    }],
                    pager: '#' + attrs.emailloggingGrid,
                    width: '100%',
                    height: 'auto',
                    sortname: 'deliveryTime',
                    sortorder: 'asc',
                    viewrecords: true,
                    gridComplete: function () {
                        angular.forEach(['direction', 'deliveryStatus', 'toAddress', 'fromAddress', 'subject', 'message', 'deliveryTime', 'modifiedDate'], function (value) {
                            elem.jqGrid('setLabel', value, scope.msg('email.logging.' + value));
                            var dataUser = elem.jqGrid('getRowData')[0];
                            if (dataUser !== undefined && !dataUser.hasOwnProperty(value)) {
                                elem.jqGrid('hideCol', value);
                            }
                        });

                        $('#outsideEmailLoggingTable').children('div').width('100%');
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-bdiv').width('100%');
                        $('.ui-jqgrid-hdiv').width('100%');
                        $('.ui-jqgrid-hbox').width('100%');
                        $('.ui-jqgrid-view').width('100%');
                        $('#t_emailLoggingTable').width('auto');
                        $('.ui-jqgrid-pager').width('100%');
                        $('#outsideEmailLoggingTable').children('div').each(function() {
                            $('table', this).width('100%');
                            $(this).find('#emailLoggingTable').width('100%');
                            $(this).find('table').width('100%');
                       });

                    }
                });
            }
        };
    });
}());

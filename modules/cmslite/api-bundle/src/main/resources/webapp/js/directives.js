(function () {
    'use strict';

    /* Directives */

    var widgetModule = angular.module('motech-cmslite');

    widgetModule.directive('clearForm', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                $(element).on('hidden', function () {
                    $('#' + attrs.clearForm).clearForm();
                });
            }
        };
    });

    widgetModule.directive('autoComplete', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                angular.element(element).autocomplete({
                    minLength: 2,
                    source: function (request, response) {
                        $.getJSON('../cmsliteapi/resource/available/' + attrs.autoComplete, request, function (data) {
                            response(data);
                        });
                    }
                });
            }
        };
    });

    widgetModule.directive('resourcesGrid', function ($compile) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                angular.element(element).jqGrid({
                    caption: 'Resources',
                    url: '../cmsliteapi/resource',
                    datatype: 'json',
                    jsonReader: {
                        repeatitems: false,
                        root: function (obj) {
                            return obj;
                        },
                        records: function (obj) {
                            return obj.length;
                        }
                    },
                    shrinkToFit: true,
                    rowNum: 5,
                    rowList: [5, 20, 30],
                    colNames: [
                        scope.msg('resource.name'),
                        scope.msg('resource.languages'),
                        scope.msg('resource.type')
                    ],
                    colModel: [{
                        name: 'name',
                        index: 'name'
                    }, {
                        name: 'languages',
                        index: 'languages',
                        formatter: function (value, options, data) {
                            var ul = $('<ul>');

                            $.each(value, function (index, val) {
                                var a = $('<a>');

                                a.append(val);
                                a.attr('ng-click', 'showResource("{0}", "{1}", "{2}")'.format(scope.showType(data.type), val, data.name));
                                a.attr('href', '#');

                                ul.append($('<li>').append(a));
                            });

                            $compile(ul)(scope);

                            return '<ul>' + ul.html() + '</ul>';
                        }
                    }, {
                        name: 'type',
                        index: 'type',
                        formatter: function (value) {
                            return scope.msg('resource.type.' + scope.showType(value));
                        }
                    }],
                    pager: '#' + attrs.resourcesGrid,
                    width: '100%',
                    height: 'auto',
                    viewrecords: true
                });

                scope.$apply();
            }
        };
    });
}());
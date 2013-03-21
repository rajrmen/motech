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
                var elem = angular.element(element);

                elem.jqGrid({
                    caption: 'Resources',
                    url: '../cmsliteapi/resource',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false
                    },
                    shrinkToFit: true,
                    rowNum: 5,
                    rowList: [5, 20, 30],
                    colModel: [{
                        name: 'name',
                        index: 'name'
                    }, {
                        name: 'languages',
                        index: 'languages',
                        sortable: false,
                        formatter: function (array, options, data) {
                            var ul = $('<ul>');

                            angular.forEach(array, function (value) {
                                ul.append($('<li>').append($('<a>')
                                    .append(value)
                                    .attr('ng-click', 'showResource("{0}", "{1}", "{2}")'.format(data.type, value, data.name))
                                    .css('cursor', 'pointer')
                                ));
                            });

                            return '<ul>' + ul.html() + '</ul>';
                        }
                    }, {
                        name: 'type',
                        index: 'type',
                        formatter: function (value) {
                            return scope.msg('resource.type.' + value);
                        }
                    }],
                    pager: '#' + attrs.resourcesGrid,
                    width: '100%',
                    height: 'auto',
                    viewrecords: true,
                    gridComplete: function () {
                        angular.forEach(elem.find('ul'), function(value) {
                            $compile(value)(scope);
                        });

                        angular.forEach(['name', 'languages', 'type'], function (value) {
                            elem.jqGrid('setLabel', value, scope.msg('resource.' + value));
                        });
                    }
                });
            }
        };
    });
}());
(function () {
    'use strict';

    /* Directives */

    var widgetModule = angular.module('motech-sms');

   widgetModule.directive('jqgridSearch', function () {
           return {
               restrict: 'A',
               link: function (scope, element, attrs) {
                   var elem = angular.element(element),
                       table = angular.element('#' + attrs.jqgridSearch),
                       eventType = elem.data('event-type'),
                       filter = function (time) {
                           var field = elem.data('search-field'),
                               type = elem.data('field-type') || 'string',
                               url = parseUri(table.jqGrid('getGridParam', 'url')),
                               query = {},
                               params = '?',
                               prop;

                           // copy existing url parameters
                           for (prop in url.queryKey) {
                               if (prop !== field) {
                                   query[prop] = url.queryKey[prop];
                               }
                           }

                           // set parameter for given element
                           switch (type) {
                           case 'boolean':
                               query[field] = url.queryKey[field].toLowerCase() !== 'true';

                               if (query[field]) {
                                   elem.find('i').removeClass('icon-ban-circle').addClass('icon-ok');
                               } else {
                                   elem.find('i').removeClass('icon-ok').addClass('icon-ban-circle');
                               }
                               break;
                           default:
                               query[field] = elem.val();
                           }

                           // create raw parameters
                           for (prop in query) {
                               params += prop + '=' + query[prop] + '&';
                           }

                           // remove last '&'
                           params = params.slice(0, params.length - 1);

                           if (timeoutHnd) {
                               clearTimeout(timeoutHnd);
                           }

                           timeoutHnd = setTimeout(function () {
                               jQuery('#' + attrs.jqgridSearch).jqGrid('setGridParam', {
                                   url: '../cmsliteapi/resource' + params
                               }).trigger('reloadGrid');
                           }, time || 0);
                       },
                       timeoutHnd;

                   switch (eventType) {
                   case 'keyup':
                       elem.keyup(function () {
                           filter(500);
                       });
                       break;
                   default:
                       elem.click(filter);
                   }
               }
           };
       });

    widgetModule.directive('loggingGrid', function ($compile) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elem = angular.element(element);

                elem.jqGrid({
                    caption: 'SMS Logging',
                    url: '../smsapi/smslogging',
                    datatype: 'json',
                    jsonReader:{
                        repeatitems:false
                    },
                    shrinkToFit: true,
                    autowidth: true,
                    rownumbers: true,
                    rowNum: 5,
                    rowList: [5, 20, 30],
                    colModel: [{
                        name: 'phoneNumber',
                        index: 'phoneNumber'
                    }, {
                        name: 'Direction',
                        index: 'Direction'
                    }, {
                        name: 'MessageTime',
                        index: 'MessageTime'
                    }, {
                        name: 'DeliveryStatus',
                        index: 'DeliveryStatus'
                    }, {
                        name: 'Message',
                        index: 'Message',
                        sortable: false
                    }],
                    pager: '#' + attrs.loggingGrid,
                    width: '100%',
                    height: 'auto',
                    sortname: 'phoneNumber',
                    sortorder: 'asc',
                    viewrecords: true,
                    toolbar: [true,'top'],
                    gridComplete: function () {
                        $('#outsideResourceTable').children('div').width('100%');
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-bdiv').width('100%');
                        $('.ui-jqgrid-hdiv').width('100%');
                        $('.ui-jqgrid-hbox').width('100%');
                        $('.ui-jqgrid-view').width('100%');
                        $('#t_resourceTable').width('auto');
                        $('.ui-jqgrid-pager').width('100%');
                        $('.ui-jqgrid-pager').height('auto');
                        $('#outsideResourceTable').children('div').each(function() {
                            $('table', this).width('100%');
                            $(this).find('#resourceTable').width('100%');
                            $(this).find('table').width('100%');
                       });
                    }
                });

                $('#t_resourceTable').append($compile($('#operations-resource'))(scope));
                $('#t_resourceTable').append($compile($('#collapse-resource'))(scope));
            }
        };
    });
}());
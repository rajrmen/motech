(function () {
    'use strict';

    /* Directives */

    var widgetModule = angular.module('motech-admin');

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


    widgetModule.directive('schedulerGrid', function($compile, $http, $templateCache, MotechScheduler) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                var elem = angular.element(element), filters, i, j, k, rows, activity, status;

                elem.jqGrid({
                    url:"../admin/api/jobs?name=&activity=NOW,FINISHED,LATER&status=ERROR,BLOCKED,PAUSED,OK&dateFrom=&dateTo=",
                    datatype:"json",
                    jsonReader:{
                        repeatitems: false
                    },
                    prmNames: {
                        sort: 'sortColumn',
                        order: 'sortDirection'
                    },
                    shrinkToFit: true,
                    autowidth: true,
                    rowNum: 10,
                    rowList: [10, 20, 50],
                    colModel:[
                    {name:'activity',index:'activity', width:50, align:"center"},
                    {name:'status',index:'status', width:40, align:"center"},
                    {name:'name',index:'name', width:180, align:"left"},
                    {name:'startDate',index:'startDate', width:80, align:"center", sorttype:"date"},
                    {name:'info',index:'info', width:100,align:"center"}
                    ],
                    pager: '#' + attrs.schedulerGrid,
                    width: '100%',
                    height: 'auto',
                    rownumbers: true,
                    rownumWidth: 20,
                    sortname: 'startDate',
                    sortorder: 'asc',
                    viewrecords: true,
                    multiselect: false,
                    subGrid: true,
                    subGridOptions: {
                        "plusicon" : "ui-icon-triangle-1-e",
                        "minusicon" : "ui-icon-triangle-1-s",
                        "openicon" : "ui-icon-arrowreturn-1-e"
                    },
                    subGridRowExpanded: function(subgrid_id, row_id) {
                        var subgrid_table_id, pager_id;
                        subgrid_table_id = subgrid_id+"_t";
                        pager_id = "p_"+subgrid_table_id;
                        $("#"+subgrid_id).html("<table id='"+subgrid_table_id+"' class='scroll'></table>");

                        jQuery("#"+subgrid_table_id).jqGrid({
                        url:'../admin/api/jobs/'+row_id,
                        datatype:"json",
                        jsonReader:{
                            repeatitems: false,
                            root: 'eventInfoList',
                            records: function (obj) { return obj.length; }
                        },
                        colNames: ['Subject', 'Key', 'Value'],
                        colModel: [
                            {name:"subject",index:"subject", align:"center"},
                            {name:"parameters",index:"parameters", width:80, align:"center",
                            formatter: function (array, options, data) {
                                var div = $('<div>');
                                $.each(array, function (i, value) {
                                    div.append($('<div>').append(i)
                                    .addClass('parameters')
                                    );
                                });
                            return '<div>' + div.html() + '</div>';
                            }},
                            {name:"parameters",index:"parameters", width:80, align:"center",
                            formatter: function (array, options, data) {
                                var div2 = $('<div>');
                                $.each(array, function (i, value) {
                                    div2.append($('<div>').append($('<span>').append(value))
                                    .addClass('parameters')
                                    );
                                });
                                return '<div>' + div2.html() + '</div>';
                            }}
                            ],
                        rowNum:99, pager: pager_id, sortname: 'num', sortorder: "asc", height: '100%' });
                        jQuery("#"+subgrid_table_id).jqGrid('navGrid',"#"+pager_id,{edit:false,add:false,del:false});
                        jQuery("#"+subgrid_table_id).jqGrid('setGroupHeaders', {
                            useColSpanStyle: true,
                            groupHeaders:[
                                {startColumnName: 'Key', numberOfColumns: 2, titleText: 'Parameters'}
                            ]
                        });

                        $('div.ui-widget-content').width('100%');
                        $('div.ui-jqgrid-bdiv').width('100%');
                        $('div.ui-jqgrid-view').width('100%');
                        $('div.ui-jqgrid-hdiv').width('100%');
                        $('table.ui-jqgrid-htable').width('100%');
                        $('table.ui-jqgrid-btable').width('100%');
                        $('div.ui-jqgrid-hbox').css({'padding-right':'0'});

                    },

                    gridComplete: function (data) {
                        angular.forEach(['activity', 'status', 'name', 'startDate', 'info'], function (value) {
                            elem.jqGrid('setLabel', value, scope.msg('admin.scheduler.' + value));
                        });

                        $('#outsideSchedulerTable').children('div').width('100%');
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-btable').addClass("table-lightblue");
                        $('.ui-jqgrid-htable').addClass('table-lightblue');
                        $('.ui-jqgrid-bdiv').width('100%');
                        $('.ui-jqgrid-hdiv').width('100%');
                        $('.ui-jqgrid-hbox').width('100%');
                        $('.ui-jqgrid-view').width('100%');
                        $('#t_resourceTable').width('auto');
                        $('.ui-jqgrid-pager').width('100%');
                        $('#outsideSchedulerTable').children('div').each(function() {
                            $('table', this).width('100%');
                            $(this).find('#schedulerTable').width('100%');
                            $(this).find('table').width('100%');
                        });
                        rows = $("#schedulerTable").getDataIDs();
                        for (k = 0; k < rows.length; k+=1) {
                            activity = $("#schedulerTable").getCell(rows[k],"activity").toLowerCase();
                            status = $("#schedulerTable").getCell(rows[k],"status").toLowerCase();
                            if (activity !== undefined && status !== undefined) {
                                switch (activity) {
                                    case 'now':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'activity','','now',{ },'');
                                        break;
                                    case 'stopped':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'activity','','stopped',{ },'');
                                        break;
                                    case 'finished':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'activity','','finished',{ },'');
                                        break;
                                    case 'later':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'activity','','later',{ },'');
                                        break;
                                    default:
                                        break;
                                }
                                switch (status) {
                                    case 'ok':
                                        if (activity === 'now') {
                                            $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i class="icon-spinner icon-spin icon-large icon icon-green"></i>','idle',{ },'');
                                        } else if (activity === 'later'){
                                            $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i class="icon-time icon-large icon icon-gold"></i>','waiting',{ },'' );
                                        } else if (activity === 'error'){
                                            $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i class="icon-exclamation-sign icon-large icon icon-red"></i>','error',{ },'');
                                        } else {
                                            $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i class="icon-ok-sign icon-large icon icon-green"></i>','ok',{ },'');
                                        }
                                        break;
                                    case 'waiting':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i class="icon-time icon-large icon icon-gold"></i>','waiting',{ },'');
                                        break;
                                    case 'error':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i class="icon-exclamation-sign icon-large icon icon-red"></i>','error',{ },'');
                                        break;
                                    case 'idle':
                                        $("#schedulerTable").jqGrid('setCell',rows[k],'status','<i class="icon-spinner icon-spin icon-large icon icon-green"></i>','idle',{ },'');
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                });
            }
        };
    });

}());
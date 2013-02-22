function CampaignsCtrl($scope, Campaigns) {

    $scope.$on('$viewContentLoaded', function () {
        $scope.campaigns = Campaigns.query();
    });
}

function EnrollmentsCtrl($scope, $routeParams, Enrollments) {

    $scope.$on('$viewContentLoaded', function () {

        $scope.campaignName = $routeParams.campaignName;

        var createOrUpdateEnrollementUrl = "../messagecampaign/web-api/enrollments/users/" + +$scope.campaignName + "/users/";
        var getEnrollementsUrl = "../messagecampaign/web-api/enrollments/users?enrollmentStatus=ACTIVE&campaignName=" + $scope.campaignName;
        var deleteEnrollementUrl = "../messagecampaign/web-api/enrollments/" + $scope.campaignName + "/users/";

        jQuery("#enrollmentsTable").jqGrid({
            caption:"Enrollments for Campaign - " + $scope.campaignName,
            url:getEnrollementsUrl,
            datatype:"json",
            jsonReader:{
                root:"enrollments",
                id:"0",
                repeatitems:false
            },
            colNames:['ID', 'Edit', 'Delete'],
            colModel:[
                {name:'externalId', index:'externalId', sortable:false, editable:true},
                {name:'edit', formatter:'actions',
                    formatoptions:{keys:true, editbutton:true, delbutton:false, url:createOrUpdateEnrollementUrl + this.externalId, mtype:"POST" }},
                {name:'delete', formatter:'actions',
                    formatoptions:{editbutton:false, delbutton:true, delOptions:{url:deleteEnrollementUrl + this.externalId, mtype:"DELETE"}}}
            ],
            autowidth:true,
            multiselect:true
        });

        jQuery("#deleteEnrollments").click(function () {
            var rowIds = jQuery("#enrollmentsTable").jqGrid('getGridParam', 'selarrrow');
            if (rowIds.length > 0) {
                for (i = 0; rowIds[i]; i++) {
                    jQuery.ajax({
                        type:"DELETE",
                        url:deleteEnrollementUrl + rowIds[i]
                    });
                }
                jQuery("#tableid").trigger('reloadGrid');
            }
        });

        jQuery("#addEnrollment").click(function () {
            var rowId = Math.round(Math.random()*10000);
            jQuery("#enrollmentsTable").jqGrid('addRowData', rowId, {});
            jQuery(jQuery("#enrollmentsTable").jqGrid('getInd', rowId, true)).find('.ui-inline-edit').click();
        });
    });

    /**
     * loadonce
     * multiselect
     * beforeSubmitCell:function (rowid, celname, value, iRow, iCol) {
     *   return {externalId: value};
     * }
     *
     * formatter - delOptions: {reloadAfterSubmit:false},editOptions: {reloadAfterSubmit:false}
     *
     * cellEdit:true,
     * cellsubmit:"remote",
     * cellurl:'../messagecampaign/enrollments/' + $scope.campaignName + "/users/"
     */
}

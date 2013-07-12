<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>MOTECH - Mobile Technology for Community Health</title>

    <link rel="stylesheet" type="text/css" href="resources/css/jquery-ui-1.9.1-redmond.css">
    <link rel="stylesheet" type="text/css" href="resources/css/angular-ui.css">
    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap-responsive.css">
    <link rel="stylesheet" type="text/css" href="resources/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap-fileupload.min.css">
    <link rel="stylesheet" type="text/css" href="resources/css/jquery-ui-min.css"/>
    <link rel="stylesheet" type="text/css" href="resources/css/tagsinput/jquery.tagsinput.css">
    <link rel="stylesheet" type="text/css" href="resources/css/timepicker/jquery-ui-timepicker-addon.css">
    <link rel="stylesheet" type="text/css" href="resources/css/jquery-cron/jquery-gentleSelect.css">
    <link rel="stylesheet" type="text/css" href="resources/css/jquery-cron/jquery-cron.css">
    <link rel="stylesheet" type="text/css" href="resources/css/jquery-sidebar.css"/>
    <link rel="stylesheet" type="text/css" href="resources/css/jqGrid/ui.jqgrid.css"/>
    <link rel="stylesheet" type="text/css" href="resources/css/jqGrid/ui.jqgrid.override.css"/>
    <link rel="stylesheet" type="text/css" href="resources/css/index.css"/>
    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap-notify.css"/>
    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap-switch.css"/>
    <link rel="stylesheet" type="text/css" href="resources/css/alert-blackgloss.css"/>

    <script src="resources/lib/jquery/jquery.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.form.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery-ui.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.alerts.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.i18n.properties-min-1.0.9.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.tools.min.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.blockUI.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.caret.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.sidebar.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.livequery.min.js" type="text/javascript"></script>

    <script src="resources/lib/angular/angular.min.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-resource.min.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-cookies.min.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-bootstrap.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-ui.min.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-ui-router.min.js" type="text/javascript"></script>

    <script src="resources/lib/bootstrap/bootstrap.min.js" type="text/javascript"></script>
    <script src="resources/lib/bootstrap/bootstrap-fileupload.min.js" type="text/javascript"></script>
    <script src="resources/lib/bootstrap/bootstrap-notify.js" type="text/javascript"></script>
    <script src="resources/lib/bootstrap/bootstrap-switch.js" type="text/javascript"></script>

    <script src="resources/lib/tagsinput/jquery.tagsinput.js" type="text/javascript"></script>

    <script src="resources/lib/timepicker/jquery-ui-sliderAccess.js" type="text/javascript"></script>
    <script src="resources/lib/timepicker/jquery-ui-timepicker-addon.js" type="text/javascript"></script>

    <script src="resources/lib/jquery-cron/jquery-gentleSelect.js" type="text/javascript"></script>
    <script src="resources/lib/jquery-cron/jquery-cron.js " type="text/javascript"></script>

    <script src="resources/lib/moment/moment.min.js" type="text/javascript"></script>
    <script src="resources/lib/moment/langs.js " type="text/javascript"></script>

    <script src="resources/lib/parseuri/parseuri.js" type="text/javascript"></script>

    <script src="resources/js/app.js" type="text/javascript"></script>
    <script src="resources/js/util.js" type="text/javascript"></script>
    <script src="resources/js/common.js" type="text/javascript"></script>
    <script src="resources/js/localization.js" type="text/javascript"></script>
    <script src="resources/js/directives.js" type="text/javascript"></script>
    <script src="resources/js/controllers.js" type="text/javascript"></script>
    <script src="resources/js/browser-detect.js" type="text/javascript"></script>
</head>

<body ng-class="showDashboardLogo.backgroudUpDown()" ng-controller="RootCtrl">
<div id="app-content">
    <div id="content-header" style="display: none;" ng-include="'../server/resources/partials/header.html'"></div>

    <div id="splash" class="splash">
        <div class="splash-logo"></div>
        <div class="clearfix"></div>
        <div class="splash-loader"><img src="resources/img/loader.gif" alt="loading"/></div>
        <div class="clearfix"></div>
    </div>

    <div id="content-template" style="display: none;">
        <div ng-include="'../server/resources/partials/${pageToLoad}.html'"></div>

        <c:choose>
            <c:when test="${pageToLoad == 'home'}">
                <script src="resources/js/home.js" type="text/javascript"></script>
            </c:when>
            <c:otherwise>
                <script type="text/javascript">
                    angular.element(document).ready(function () {
                        angular.bootstrap(document, ['motech-dashboard']);
                    });
                </script>
            </c:otherwise>
        </c:choose>
    </div>
</div>

</body>
</html>

<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${pageLang}" />
<fmt:setBundle basename="org.motechproject.resources.messages" var="bundle"/>

<!DOCTYPE html>
<html>
<head>
    <%@include file="head.jsp" %>

    <c:if test="${! empty currentModule}">
       ${currentModule.header}
    </c:if>

    <c:if test="${empty currentModule}">
        <script type="text/javascript">
            $(window).load(function() {
                initAngular();
            });
        </script>
    </c:if>
</head>

<body ng-controller="MasterCtrl" id="container" ng-class="showDashboardLogo.backgroudUpDown()" class="custom ui-layout-container" layout state="bodyState" ng-init="bodyState = true">
    <span ng-controller="HomeCtrl">

        <div class="ui-layout-pane ui-layout-pane-north" id="outer-north">
            <div ng-show="ready" id="content-header" ng-include="'../server/resources/partials/header.html'"></div>
        </div>

        <div id="page-loading">Loading...</div>

        <div ng-show="ready" id="outer-south" class="ui-layout-pane ui-layout-pane-south inside">
            <span id="tbarCloseSouth" class="southpane-open pull-right" title="Close This Pane"><i class="icon-caret-down button"></i></span>
            <div ng-include="'../server/resources/partials/footer.html'"></div>
        </div>

        <div id="outer-west" class="ui-layout-pane ui-layout-pane-west">
            <div class="header-toolbar header-footer"><i id="tbarCloseWest" class="button icon-caret-left"></i></div>
            <div class="ui-layout-content">
                <motech-modules></motech-modules>
            </div>
        </div>

        <div innerlayout id="outer-center" class="outer-center ui-layout-pane ui-layout-pane-center ui-layout-container">
            <c:if test="${! empty currentModule}">
                <div id="main-content">
                    <div class="row-fluid">
                        <div class="span12">
                            <div class="splash" ng-hide="ready">
                                <div class="splash-logo"></div>
                                <div class="clearfix"></div>
                                <div class="splash-loader"><img src="../server/resources/img/loader.gif" alt="loading" /></div>
                                <div class="clearfix"></div>
                                <div class="splash-msg">{{msg('server.module.loading')}}</div>
                                <div class="clearfix"></div>
                            </div>
                            <c:if test="${criticalNotification != null && criticalNotification != ''}">
                                <div id="criticalNotification" class="alert alert-error">
                                    ${criticalNotification}
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div id="module-content" ng-show="ready">
                        <script type="text/javascript">
                            loadModule('${currentModule.url}', ${currentModule.angularModulesStr});
                        </script>
                    </div>
                </div>
            </c:if>
        </div> <!-- #outer-center-->

        <div id="southpane-closed" class="header-toolbar">
            <span id="tbarOpenSouth2"><i class="icon-caret-up button"></i></span>
        </div>

    </span>
</body>
</html>

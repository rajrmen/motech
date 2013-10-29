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

<div id="outer-south" class="ui-layout-pane ui-layout-pane-south inside"><div ng-show="ready" ng-include="'../server/resources/partials/footer.html'"></div></div>

<div id="outer-west" class="ui-layout-west ui-layout-pane ui-layout-pane-west">

    <div class="ui-layout-content">
        <motech-modules></motech-modules>

    </div>
</div>

<div  id="outer-center" class="outer-center ui-layout-pane ui-layout-pane-center ui-layout-container">

    <div id="inner-center" class="inner-center ui-layout-pane ui-layout-pane-center">
        <div class="">
            <ul class="breadcrumb" role="navigation">
                <li><a role="menu" href=".">{{msg('server.home')}}</a><span class="divider">/</span></li>
            </ul>
        </div>
        <div class="ui-widget-content">
            <div class="row-fluid">
                <div id="main-content" class="span12">
                    <c:if test="${! empty currentModule}">
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
                        <div id="module-content" ng-show="ready">
                            <script type="text/javascript">
                                loadModule('${currentModule.url}', ${currentModule.angularModulesStr});
                            </script>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <div id="inner-east" class="inner-east ui-layout-pane ui-layout-pane-east">
        <div class="ui-layout-center ui-layout-pane ui-layout-pane-center">
            <div class="ui-widget-content">
            </div>
        </div>
    </div>

</div><!-- /#tabpanels -->

<div class="ui-layout-resizer ui-layout-resizer-north ui-layout-resizer-open ui-layout-resizer-north-open" id="tabbuttons-resizer"></div>
</div><!-- /#outer-center -->


<div class="ui-layout-resizer ui-layout-resizer-north ui-layout-resizer-open ui-layout-resizer-north-open" id="outer-north-resizer"></div>

<div class="ui-layout-resizer ui-layout-resizer-south ui-layout-resizer-open ui-layout-resizer-south-open" id="outer-south-resizer"></div>
</span>
</body>
</html>

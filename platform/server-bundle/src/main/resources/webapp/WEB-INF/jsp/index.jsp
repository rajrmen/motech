<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <%@include file="head.jsp" %>
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
        <c:choose>
            <c:when test="${pageToLoad == 'startup'}">
                <div ng-include="'../server/resources/partials/startup.html'"></div>
            </c:when>
            <c:when test="${pageToLoad == 'login'}">
                <div ng-include="'../server/resources/partials/login.html'"></div>
            </c:when>
            <c:when test="${pageToLoad == 'home'}">
                <div ng-include="'../server/resources/partials/home.html'"></div>
            </c:when>
            <c:when test="${pageToLoad == 'accessdenied'}">
                <div ng-include="'../server/resources/partials/accessdenied.html'"></div>
            </c:when>
        </c:choose>

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

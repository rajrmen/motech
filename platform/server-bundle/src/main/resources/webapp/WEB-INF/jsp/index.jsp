<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html ng-app="motech-dashboard">
<head>
    <%@include file="includes/head.jsp" %>
</head>

<body ng-class="showDashboardLogo.backgroudUpDown()" ng-controller="RootCtrl">
<div id="app-content">
    <div id="content-header">
        <%@include file="includes/header.jsp" %>
    </div>

    <div id="splash" class="splash">
        <div class="splash-logo"></div>
        <div class="clearfix"></div>
        <div class="splash-loader"><img src="resources/img/loader.gif" alt="loading"/></div>
        <div class="clearfix"></div>
    </div>

    <div id="content-template" style="display: none;">
        <c:choose>
            <c:when test="${pageToLoad == 'startup'}">
               <%@include file="includes/startup.jsp" %>
            </c:when>
            <c:when test="${pageToLoad == 'login'}">
                <%@include file="includes/login.jsp" %>
            </c:when>
            <c:when test="${pageToLoad == 'home'}">
                <%@include file="includes/home.jsp" %>
            </c:when>
            <c:when test="${pageToLoad == 'accessdenied'}">
                <%@include file="includes/accessdenied.jsp" %>
            </c:when>
        </c:choose>
    </div>
</div>

</body>
</html>

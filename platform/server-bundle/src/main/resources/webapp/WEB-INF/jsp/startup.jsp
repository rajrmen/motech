<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>MOTECH - Mobile Technology for Community Health</title>
    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap-responsive.css">
    <link rel="stylesheet" type="text/css" href="resources/css/index.css" />

    <script src="resources/lib/jquery/jquery-1.8.2.min.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.form.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery-ui.min.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.alerts.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.i18n.properties-min-1.0.9.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.tools.min.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.blockUI.js" type="text/javascript"></script>

    <script src="resources/lib/angular/angular.min.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-resource.min.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-cookies.min.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-bootstrap.js" type="text/javascript"></script>

    <script src="resources/lib/bootstrap/bootstrap-modal.js"></script>

    <script src="resources/js/util.js" type="text/javascript"></script>
    <script src="resources/js/common.js" type="text/javascript"></script>
    <script src="resources/js/localization.js"></script>
    <script src="resources/js/app.js"></script>
    <script src="resources/js/controllers.js"></script>

    <script src="resources/js/startup.js"></script>
    <script src="resources/js/dashboard.js"></script>

    <script type="text/javascript">
        $(window).load(function() {
            initAngular();
        });
    </script>

</head>
<body ng-controller="MasterCtrl" class="body-startup">
<div class="bodywrap">
    <div class="startup" ng-hide="ready">
        <div class="startup-logo"><img src="resources/img/motech-logo.jpg" alt="motech-logo" /></div>
        <div class="startup-title ng-binding">Mobile Technology for Community Health</div>
        <div class="clearfix"></div>
        <div class="startup-strip">
            <div class="control-group">
            <h2 class="title ng-binding">{{msg('welcome.startup')}}</h2>
            </div>
        </div>
        <div class="clearfix"></div>
        <div class="startup-form">
            <div class="diver">
                <form action="startup.jsp" method="POST" class="form-horizontal">
                    <div class="control-group">
                        <label class="control-label">{{msg('select.language')}}</label>
                        <div class="controls">
                            <c:forEach var="lang" items="${languages}">
                                <input ng-click="setUserLang('${lang}')" type="radio" value="${lang}" name="language" <c:if test="${startupSettings.language == lang}">checked</c:if> /><i class="flag flag-${lang}"></i>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">{{msg('userName')}}</label>
                        <div class="controls">
                            <input class="input-large" type="text" ng-model="user.userName" placeholder="{{msg('userName')}}">
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">{{msg('password')}}</label>
                        <div class="controls btn-group">
                            <input ng-model="user.password" placeholder="______">
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">{{msg('enter.databaseUrl')}}</label>
                        <div class="controls">
                            <input class="input-large" name="databaseUrl" value="${startupSettings.databaseUrl}"/>
                            <c:if test="${ not empty suggestions.databaseUrls }">
                                <div id="database.urls">
                                <c:forEach var="url" items="${suggestions.databaseUrls}" varStatus="status">
                                    <div id="database.url.${status.count}">
                                        <span><i>{{msg('suggestion')}} #${status.count}: </i>${url}</span>
                                        <button type="button" class="btn btn-mini">{{msg('use')}}</button>
                                    </div>
                                </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">{{msg('enter.queueUrl')}}</label>
                        <div class="controls">
                            <input class="input-large" name="queueUrl" value="${startupSettings.queueUrl}"/>
                            <c:if test="${ not empty suggestions.queueUrls }">
                                <div id="queue.urls">
                                <c:forEach var="url" items="${suggestions.queueUrls}" varStatus="status">
                                    <div id="queue.url.${status.count}">
                                        <span><i>{{msg('suggestion')}} #${status.count}: </i>${url}</span>
                                        <button type="button" class="btn btn-mini">{{msg('use')}}</button>
                                    </div>
                                </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">{{msg('enter.schedulerUrl')}}</label>
                        <div class="controls">
                            <input class="input-large" name="schedulerUrl" value="${startupSettings.schedulerUrl}"/>
                            <c:if test="${ not empty suggestions.schedulerUrls }">
                                <div id="scheduler.urls">
                                <c:forEach var="url" items="${suggestions.schedulerUrls}" varStatus="status">
                                    <div id="scheduler.url.${status.count}">
                                        <span><i>{{msg('suggestion')}} #${status.count}: </i>${url}</span>
                                        <button type="button" class="btn btn-mini">{{msg('use')}}</button>
                                    </div>
                                </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div class="control-group">
                        <div class="controls">
                            <input class="btn btn-primary" type="submit" name="SUBMIT" value="{{msg('submit')}}"/>
                            <input class="btn" type="submit" name="START" value="{{msg('submitAndStart')}}"/>
                        </div>
                    </div>
                    <c:if test="${not empty errors}">
                        <div class="alert alert-error">
                        <c:forEach var="error" items="${errors}">
                            {{msg('${error}')}}<br/>
                        </c:forEach>
                        </div>
                    </c:if>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
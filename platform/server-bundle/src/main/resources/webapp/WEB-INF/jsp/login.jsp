<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${pageLang}" />
<fmt:setBundle basename="org.motechproject.resources.messages" var="bundle"/>

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
    <script src="resources/lib/bootstrap/bootstrap-tabs.js"></script>

    <script src="resources/js/util.js" type="text/javascript"></script>
    <script src="resources/js/common.js" type="text/javascript"></script>
    <script src="resources/js/localization.js"></script>
    <script src="resources/js/app.js"></script>
    <script src="resources/js/controllers.js"></script>

    <script src="resources/js/dashboard.js"></script>

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
<body class="body-down" ng-controller="MasterCtrl">
<div class="bodywrap">
    <div class="header">
        <div class="container">
            <div class="dashboard-logo"></div>
            <div class="header-title"><fmt:message key="motechTitle" bundle="${bundle}"/></div>
            <div class="clearfix"></div>
        </div>
    </div>
    <div class="clearfix"></div>
    <div id="content" class="container">
        <div class="row-fluid">
            <div id="main-content">
                <div id="login" class="well2 span4">
                    <div class="box-header"><fmt:message key="security.signInUser" bundle="${bundle}"/></div>
                    <div class="box-content clearfix">
                        <div class="well3">
                            <form action="${contextPath}/j_spring_security_check" method="POST" class="inside">
                                <div class="control-group">
                                    <h4><fmt:message key="security.signInWithId" bundle="${bundle}"/></h4>
                                </div>
                                <div class="control-group">
                                    <h4><fmt:message key="motechId" bundle="${bundle}"/></h4>
                                </div>
                                <div class="control-group">
                                    <input type="text" name="j_username"/>
                                </div>
                                <div class="control-group">
                                    <input type="password" name="j_password"/>
                                </div>
                                <div class="control-group">
                                    <input class="btn btn-primary" value="Sign in" type="submit"/>
                                </div>
                                <div class="control-group">
                                <a><fmt:message key="security.enterEmailQuestions" bundle="${bundle}"/></a>
                                </div>
                            </form>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
                <!--
                <div class="well2 span8">
                    <div class="box-header"><fmt:message key="security.signInUnsuccessful" bundle="${bundle}"/></div>
                    <div class="box-content clearfix">
                        <div class="row-fluid">
                            <div class="span6 inside">
                                <div class="well3">
                                    <div class="control-group">
                                        <h5><fmt:message key="security.didnotRecognizeMsg" bundle="${bundle}"/></h5>
                                    </div>
                                    <div class="control-group">
                                        <h5><fmt:message key="security.thinkForgotMsg" bundle="${bundle}"/></h5>
                                    </div>
                                    <div class="control-group">
                                        <h5><fmt:message key="security.donotRememberMsg1" bundle="${bundle}"/>
                                            <button class="btn btn-mini btn-link"><fmt:message key="clickHere" bundle="${bundle}"/></button> <fmt:message key="security.donotRememberMsg2" bundle="${bundle}"/></h5>
                                    </div>
                                </div>
                            </div>
                            <div class="span6">
                                <div class="well3"><div class="left-divider">
                                    <form class="inside">
                                        <div class="control-group">
                                            <h4><fmt:message key="security.signInWithId" bundle="${bundle}"/></h4>
                                        </div>
                                        <div class="control-group">
                                            <h4><fmt:message key="motechId" bundle="${bundle}"/></h4>
                                        </div>
                                        <div class="control-group">
                                            <input type="text" ng-model="" placeholder="<fmt:message key="username" bundle="${bundle}"/>">
                                        </div>
                                        <div class="control-group">
                                            <input type="text" ng-model="" placeholder="<fmt:message key="password" bundle="${bundle}"/>">
                                        </div>
                                        <div class="control-group">
                                            <input class="btn btn-primary" type="submit" value="<fmt:message key="signIn" bundle="${bundle}"/>" ng-click=""/>
                                        </div>
                                    </form>
                                </div></div>
                            </div>
                        </div>
                    </div>
                </div>


                <div class="well2 span4">
                    <div class="box-header"><fmt:message key="security.resetInstructions" bundle="${bundle}"/></div>
                    <div class="box-content">
                        <form class="inside">
                            <div class="well3">
                                <div class="control-group">
                                    <h4><fmt:message key="security.enterEmailQuestions" bundle="${bundle}"/></h4>
                                </div>
                                <div class="control-group">
                                    <p><fmt:message key="security.enterEmailMsg" bundle="${bundle}"/></p>
                                </div>
                                <div class="control-group">
                                    <label><fmt:message key="security.enterEmail" bundle="${bundle}"/></label>
                                    <input type="text" ng-model="" placeholder="_______">
                                </div>
                                <div class="control-group">
                                    <input class="btn btn-primary" type="submit" value="<fmt:message key="security.sendReset" bundle="${bundle}"/>" ng-click=""/>
                                </div>
                            </div>
                        </form>
                        <div class="clearfix"></div>
                    </div>
                </div>


                <div class="well2 span4">
                    <div class="box-header"><fmt:message key="security.openIdConsumer" bundle="${bundle}"/></div>
                    <div class="box-content">
                        <div class="well3">
                            <form class="inside">
                                <div class="control-group">
                                    <h4><fmt:message key="Sign in?" bundle="${bundle}"/></h4>
                                </div>
                                <div class="control-group">
                                    <p><fmt:message key="security.signUsingAccount" bundle="${bundle}"/></p>
                                </div>
                                <div class="control-group">
                                    <input class="btn btn-primary" type="submit" value="<fmt:message key="signIn" bundle="${bundle}"/>" ng-click=""/>
                                </div>
                            </form>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>


                <div class="well2 span4">
                    <div class="box-header"><fmt:message key="security.resetYourPassword" bundle="${bundle}"/></div>
                    <div class="box-content">
                        <div class="well3">
                            <form class="inside">
                                <div class="control-group">
                                    <h4><fmt:message key="password" bundle="${bundle}"/></h4>
                                </div>
                                <div class="control-group">
                                    <label><fmt:message key="password" bundle="${bundle}"/></label>
                                    <input type="text" ng-model="" placeholder="_______">
                                </div>
                                <div class="control-group">
                                    <label><fmt:message key="confirmPassword" bundle="${bundle}"/></label>
                                    <input type="text" ng-model="" placeholder="_______">
                                </div>
                                <div class="control-group">
                                    <input class="btn btn-primary" type="submit" value="<fmt:message key="changePassword" bundle="${bundle}"/>" ng-click=""/>
                                </div>
                            </form>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
                -->
            </div>
        </div>
    </div>
</div>
</body>
</html>
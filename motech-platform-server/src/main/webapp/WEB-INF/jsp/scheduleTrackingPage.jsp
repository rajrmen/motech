<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Schedule tracking demo with Open MRS</title>
</head>
<body>
	Schedule Tracking
	<br><br>

	Register a user ID (coincides with Open MRS patient ID) into the Demo Concept Schedule
	<form method="post" action="/motech-platform-server/enroll/start">
		ID:<input type="text" name="externalId" size="12" maxlength="12" />
		<input type="hidden" name="scheduleName" value="Demo Concept Schedule">
		<input type="submit" value="Register User" />
	</form>
</body>
</html>
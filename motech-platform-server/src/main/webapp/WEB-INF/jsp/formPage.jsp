<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Campaign demonstration using Voxeo</title>
</head>
<body>

	This is a demonstration of message campaigns. It uses the core platform (server, server-api, common) along with scheduler, message-campaign, cmslite and voxeo.


	Campaign registration demo, using Voxeo. Registering a duplicate id will overwrite update its phone
	number which would result in redirecting the campaign messages to that
	number)
	Register a user into the system
	<form method="post" action="/motech-platform-server/user/add">
		ID:<input type="text" name="externalId" size="12" maxlength="12" />
		Phone Number:<input type="text" name="phoneNum" size="24"
			maxlength="24" /> <input type="submit" value="Register User" />
	</form>
	Unregister a user from the system
	<form method="post" action="/motech-platform-server/user/remove">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="submit" value="Unregister User" />
	</form>

	Register a user in an offset campaign (Call every 2 minutes)
	<form method="post" action="/motech-platform-server/campaign/start">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> 
		Offset time:<input type="text" name="offset" size="12" maxlength="12" />
		<input type="hidden" name="campaignName" value="Relative Dates Message Program" />
		<input type="submit" value="Register in campaign" />
	</form>

	Unregister a user from the campaign
	<form method="post" action="/motech-platform-server/campaign/stop">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> 
		<input type="hidden" name="campaignName" value="Relative Dates Message Program" />
		<input type="submit" value="Unregister" />
	</form>

	The list of all registered patients (by ID)
	<table>
		<c:forEach var="patients" items="${patients}">
			<tr>
				<td>${patients.externalid}</td>
			</tr>
		</c:forEach>
	</table>


</body>
</html>
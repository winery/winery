<%--
/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Michael Wurster - Initial implementation
 *******************************************************************************/
--%>
<%@page language="java" contentType="text/html; charset=utf-8"
		pageEncoding="utf-8" isErrorPage="true" %>

<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>

<%! static Logger logger = LoggerFactory.getLogger("error.jsp"); %>

<% if (exception != null) { %>
<% logger.error("An error occurred: {}", exception.getMessage(), exception); %>

<!DOCTYPE html>
<html>
<head>
	<title>Winery Topologymodeler &ndash; Error Page</title>
	<meta http-equiv="content-type" content="text/html;charset=utf-8"/>
</head>
<body>
<h3>Error: <%= exception.getMessage() %></h3>
</body>
</html>

<% } %>

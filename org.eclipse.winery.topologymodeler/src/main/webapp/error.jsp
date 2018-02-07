<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright (c) 2017 Contributors to the Eclipse Foundation
  ~
  ~ See the NOTICE file(s) distributed with this work for additional
  ~ information regarding copyright ownership.
  ~
  ~ This program and the accompanying materials are made available under the
  ~ terms of the Eclipse Public License 2.0 which is available at
  ~ http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
  ~ which is available at https://www.apache.org/licenses/LICENSE-2.0.
  ~
  ~ SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>
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
<h3>Error: <%= exception.getMessage() %>
</h3>
</body>
</html>

<% } %>

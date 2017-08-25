<%--
/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
--%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@page buffer="none" %>

<%@page import="org.eclipse.winery.repository.Prefs" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="w" uri="http://www.eclipse.org/winery/repository/functions" %>
<%
	String uiURL = request.getParameter("uiURL");
	if (StringUtils.isEmpty(uiURL)) {
		uiURL = "";
	}
%>
<html>
<head>
	<meta name="application-name" content="Winery"/>
	<meta charset="UTF-8">
	<link rel="icon" href="${w:topologyModelerURI()}/favicon.png" type="image/png">

	<link rel="stylesheet" href="${pageContext.request.contextPath}/components/bootstrap/dist/css/bootstrap.css"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/components/bootstrap/dist/css/bootstrap-theme.css"/>

	<script type='text/javascript' src='${pageContext.request.contextPath}/components/requirejs/require.js'></script>

	<!-- jquery and jquery UI have to be loaded using the old fashioned way to avoid incompatibilities with bootstrap v3 -->
	<script type='text/javascript' src='${pageContext.request.contextPath}/components/jquery/jquery.js'></script>
	<script type='text/javascript' src='${pageContext.request.contextPath}/3rdparty/jquery-ui/js/jquery-ui.js'></script>
	<script type='text/javascript'
			src='${pageContext.request.contextPath}/components/bootstrap/dist/js/bootstrap.js'></script>
	<script>
		require.config({
			baseUrl: "${pageContext.request.contextPath}/js",
			paths: {
				"datatables": "../components/datatables/media/js/jquery.dataTables",
				"jquery": "../components/jquery/jquery",

				// required for jsplumb
				"jquery.ui": "../3rdparty/jquery-ui/js/jquery-ui",

				"jsplumb": "../components/jsPlumb/dist/js/jquery.jsPlumb-1.5.4",

				"winery-sugiyamaLayouter": "${w:topologyModelerURI()}/js/winery-sugiyamaLayouter"
			}
		});
	</script>
	<c:if test="${not empty it.additionalScript}">
		<script type='text/javascript' src='${it.additionalScript}'></script>
	</c:if>
</head>
<body>

<t:topologyTemplateRenderer topology="${it.topologyTemplate}" repositoryURL="<%=Prefs.INSTANCE.getResourcePath()%>"
							uiURL="<%=uiURL%>" client="${it.client}" fullscreen="true" additonalCSS="${it.additonalCSS}"
							autoLayoutOnLoad="${it.autoLayoutOnLoad}"/>

</body>
</html>

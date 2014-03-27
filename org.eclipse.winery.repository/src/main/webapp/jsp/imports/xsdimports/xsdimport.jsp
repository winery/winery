<%--
/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
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
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Quick hack without XML view --%>

<%--
FIXME: parameters cssClass and selected are somehow ignored, but replaced by the data provided by GenericComponentPageData or similar...
Symptom: in header.jsp "param.selected" is "XSDImport" instead of "xsdimport"
--%>
<t:genericpage windowtitle="XSD Imports" cssClass="mainContentContainer xsdimport" selected="xsdimport">

	<div class="top">
		<%@ include file="/jsp/componentnaming.jspf" %>
		<!--
		<div style="float:right; margin-right:29px; margin-top: -20px; position:relative;">
			<div style="float:right;">
				<a href="?definitions" class="btn btn-info">XML</a>
				<a href="?csar" class="btn btn-info">ZIP</a>
			</div>
		</div>  -->
	</div>

	<div class="middle" id="ccontainer">

Associated file:
<c:if test="${empty it.location}">none</c:if>
<c:if test="${not empty it.location}"><a href="${it.location}">${it.location}</a></c:if>

<br />

Modification not yet implemented

	</div>

</t:genericpage>



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
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage windowtitle="Other Elements" selected="OtherElements" cssClass="otherelements">

<p>
The following items list TOSCA elements contained in TOSCA's <code>Definitions</code> element, which are not listed as separate tabs.
</p>

<h4>Artifacts</h4>
<a class="btn btn-default" href="${pageContext.request.contextPath}/artifacttypes/">Artifact Types</a>
<a class="btn btn-default" href="${pageContext.request.contextPath}/artifacttemplates/">Artifact Templates</a>

<h4>Requirements and Capabilities</h4>
<a class="btn btn-default" href="${pageContext.request.contextPath}/requirementtypes/">Requirement Types</a>
<a class="btn btn-default" href="${pageContext.request.contextPath}/capabilitytypes/">Capability Types</a>

<h4>Implementations</h4>
<a class="btn btn-default" href="${pageContext.request.contextPath}/nodetypeimplementations/">Node Type Implementations</a>
<a class="btn btn-default" href="${pageContext.request.contextPath}/relationshiptypeimplementations/">Relationship Type Implementations</a>

<h4>Policies</h4>
<a class="btn btn-default" href="${pageContext.request.contextPath}/policytypes/">Policy Types</a>
<a class="btn btn-default" href="${pageContext.request.contextPath}/policytemplates/">Policy Templates</a>

<h4>Imports</h4>
<a class="btn btn-default" href="${pageContext.request.contextPath}/imports/http%253A%252F%252Fwww.w3.org%252F2001%252FXMLSchema">XML Schema Definitions</a>
<a class="btn btn-default" href="${pageContext.request.contextPath}/imports/http%253A%252F%252Fschemas.xmlsoap.org%252Fwsdl%252F">WSDLs</a>

</t:genericpage>

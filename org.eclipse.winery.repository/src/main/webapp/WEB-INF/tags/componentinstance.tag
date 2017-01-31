<%--
/*******************************************************************************
 * Copyright (c) 2012-2013, 2015 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Tino Stadelmaier, Philipp Meyer - rename id and/or namespace
 *******************************************************************************/
--%>
<%@tag description="Wrapper for resources, which are backed by definitions and thus offer an XML div" pageEncoding="UTF-8"%>

<%--
  quick hack to avoid specifying windowtitle at elements having it.name, too.
  TODO: check why in this class a check on it.name is done, although there is componentinstancewithname.tag
--%>
<%@attribute name="windowtitle" description="If it.name is not available, this parameter should be given"%>

<%@attribute name="selected" required="true"%>

<%@attribute name="cssClass" required="true"%>

<%@attribute name="image" required="false"%>

<%@attribute name="libs" fragment="true" %>

<%@attribute name="subMenus" required="false" type="java.util.List" description="list of SubMenuData objects stating the content of the submenus. The first submenu is used as default page. Subpage #xml must not be included, it is added automatically."%>

<%@attribute name="implementationFor" description="In case the component instance is an implementation for another type, the link (a href) to the type is put here"%>

<%@attribute name="type" description="In case the component instance is a template, the link (a href) to the type is put here"%>

<%@attribute name="twolines" required="false" description="if set, two lines are required for the tabs"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="w" uri="http://www.eclipse.org/winery/repository/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:if test="${empty windowtitle}">
  <c:set var="windowtitle" value="${it.name}" />
</c:if>

<t:genericpage windowtitle="${windowtitle}" selected="${selected}" cssClass="mainContentContainer ${cssClass}" libs="${libs}">

	<div class="top<c:if test="${not empty twolines}"> twolines</c:if>">
		<c:if test="${not empty image}">
			<img src='visualappearance/50x50' style='position: absolute; margin-top: 32px; margin-left: 27px; height: 40px; width: 40px;' />
		</c:if>

		<%-- Quick hack to enable usage of this tag at adminresource --%>
		<c:catch var="exception"><c:if test="${empty it.name}"></c:if></c:catch>
		<c:if test="${empty exception}">
			<%@ include file="/jsp/componentnaming.jspf" %>
			<div style="float:right; margin-right:29px; margin-top: -20px; position:relative;">
				<div style="float:right;">
					<button type="button" class="btn btn-danger" onclick="deleteResource('${it.name}', '.', openOverviewPage)">Delete</button>
					<a href="?definitions" class="btn btn-info">XML</a>
					<a href="?csar" class="btn btn-info">CSAR</a>
					<c:if test="${w:isContainerLocallyAvailable()}">
						<button id="deployBtn" type="button" onclick="deployOnOpenTOSCAContainer()" class="btn btn-info" data-loading-text="Deploying...">Deploy</button>
					</c:if>
				</div>
				<c:if test="${not empty implementationFor}">
					<div style="clear:both; float:right;">
					Implementation for ${implementationFor}
					</div>
				</c:if>
				<c:if test="${not empty type}">
					<div style="clear:both; float:right;">
					Type ${type}
					</div>
				</c:if>
			</div>
		</c:if>
		<div class="subMenu">
			<c:if test="${not empty subMenus}">
				<c:forEach items="${subMenus}" var="subMenu" varStatus="status">
					<t:submenu selected="${status.first}" subMenuData="${subMenu}">
					</t:submenu>
				</c:forEach>
			</c:if>

			<%-- Quick hack to enable usage of this tag at adminresource --%>
			<c:if test="${empty exception}">
				<t:submenu subMenuData="<%=org.eclipse.winery.repository.resources.SubMenuData.SUBMENU_DOCUMENTATION%>" selected="${empty subMenus}">
				</t:submenu>
				<t:submenu subMenuData="<%=org.eclipse.winery.repository.resources.SubMenuData.SUBMENU_XML%>" selected="${empty subMenus}">
				</t:submenu>
			</c:if>
		</div>
	</div>

	<div class="middle" id="ccontainer">
	</div>

	<c:if test="${empty subMenus}">
		<jsp:include page="/jsp/hashloading.jsp">
			<jsp:param name="validpages" value="['#documentation', '#xml']" />
			<jsp:param name="defaultpage" value="#documentation" />
		</jsp:include>
	</c:if>
	<c:if test="${not empty subMenus}">
		<c:forEach items="${subMenus}" var="subMenu" varStatus="status">
			<c:if test="${status.first}">
				<c:set var="defaultpage" value="${subMenu.href}"></c:set>
				<c:set var="additionalHashes" value="'${subMenu.href}'"></c:set>
			</c:if>
			<c:if test="${not status.first}">
				<c:set var="additionalHashes" value="${additionalHashes}, '${subMenu.href}'"></c:set>
			</c:if>
		</c:forEach>

		<jsp:include page="/jsp/hashloading.jsp">
			<jsp:param name="validpages" value="[${additionalHashes}, '#documentation', '#xml']" />
			<jsp:param name="defaultpage" value="${defaultpage}" />
		</jsp:include>
	</c:if>

	<div class="bottom">
	</div>

<script>

$(function() {
	$("#component_name").editable({
		ajaxOptions: {
			type: 'post'
		},
		mode: 'inline',

		params: function (params) {
			// adjust params according to Winery's expectations
			delete params.pk;
			params.id = params.value;
			delete params.value;
			delete params.name;
			return params;
		},
		error: function(e, params) {
			vShowError("id/name " +params + " already exists in the current namespace, please enter a new id/name.");
		},
	}).on("save", function (e, params) {
		window.location.replace(params.response);
	});

	$('#editNameButton').on('click', function (e) {
		e.stopPropagation();
		$('#component_name').editable('toggle');
	});

	$("#component_namespace").editable({
		ajaxOptions: {
			type: 'post'
		},
		mode: 'inline',

		params: function (params) {
			// adjust params according to Winery's expectations
			delete params.pk;
			params.ns = params.value;
			params.id="";
			delete params.value;
			delete params.name;
			return params;
		},
		error: function(e, params) {
			debugger;
			vShowError("id/name already exists in the chosen namespace, please choose a different namespace.");
		},
	}).on("save", function (e, params) {
		window.location.replace(params.response);
	});

	$('#editNamespaceButton').on('click', function (e) {
		e.stopPropagation();
		$('#component_namespace').editable('toggle');
	});
});

function openOverviewPage() {
	window.location="../../";
}

function deployOnOpenTOSCAContainer() {
	$("#deployBtn").button('loading');

	var urlToUpload = window.location.href;
	var hash = window.location.hash;
	if (hash != "") {
		urlToUpload = urlToUpload.substr(0, urlToUpload.length - hash.length)
	}
	var search = window.location.search;
	if (search != "") {
		urlToUpload = urlToUpload.substr(0, urlToUpload.length - search.length)
	}

	urlToUpload = urlToUpload + "?csar";

	var data = {
		urlToUpload: urlToUpload
	}

	// we assume the container runs at the same host and port
	$.ajax({
		url: "/admin/uploadCSARFromURL.action",
		data: data
	}).always(function () {
		$("#deployBtn").button('reset');
	}).fail(function (jqXHR, textStatus, errorThrown) {
		vShowAJAXError("Could not trigger CSAR deployment at OpenTOSCA container", jqXHR, errorThrown);
	}).done(function (result) {
		vShowSuccess("Successfully triggered CSAR deployment at OpenTOSCA container");
	});
}
</script>

</t:genericpage>

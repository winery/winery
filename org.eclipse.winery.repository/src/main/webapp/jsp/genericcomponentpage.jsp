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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="v"  uri="http://www.eclipse.org/winery/repository/functions" %>
<%@taglib prefix="t"  tagdir="/WEB-INF/tags" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions" %>

<%-- In English, one can usually form a plural by adding an "s". Therefore, we resue the label to form the window title --%>
<t:genericpage windowtitle="${it.label}s" selected="${it.type}" cssClass="${it.CSSclass}">

<c:choose>
<c:when test="${empty pageContext.request.contextPath}">
<c:set var="URL" value="/" />
</c:when>
<c:otherwise>
<c:set var="URL" value="${pageContext.request.contextPath}/" />
</c:otherwise>
</c:choose>
<t:simpleSingleFileUpload
	title="Upload CSAR"
	text="CSAR file"
	URL="${URL}"
	type="POST"
	id="upCSAR"
	accept="application/zip,.csar"/>

<t:addComponentInstance
	label="${it.label}"
	typeSelectorData="${it.typeSelectorData}"
	/>

<div class="middle" id="ccontainer">
	<br />

	<table cellpadding=0 cellspacing=0 style="margin-top: 0px; margin-left: 30px;">
		<tr>
			<td valign="top" style="padding-top: 25px; width: 680px;">

				<div id="searchBoxContainer">

					<input id="searchBox" />

					<script>

						$('#searchBox').keyup(function() {
							var searchString = $(this).val();
							searchString = searchString.toLowerCase();

							$(".entityContainer").each (function() {
								var name = $(this).find(".informationContainer > .name").text();
								var namespace = $(this).find(".informationContainer > .namespace").text();

								var t = name + namespace;
								t = t.toLowerCase();

								if (t.indexOf(searchString) == -1) {
									$(this).hide();
								} else {
									$(this).show();
								}

							});

						});

					</script>

				</div>

			<c:forEach var="t" items="${it.componentInstanceIds}">
				<%-- even though the id is an invalid XML, it is used for a simple implementation on a click on the graphical rendering to trigger opening the editor --%>
				<div class="entityContainer ${it.CSSclass}" id="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/">
					<div class="left">
						<c:if test="${it.type eq 'NodeType'}">
							<a href="./${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?edit">
								<img src='./${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/visualappearance/50x50' style='margin-top: 21px; margin-left: 30px; height: 40px; width: 40px;' />
							</a>
						</c:if>
					</div>
					<div class="center">
						<div class="informationContainer">
							<div class="name">
								${wc:escapeHtml4(t.xmlId.decoded)}
							</div>
							<div class="namespace" alt="${wc:escapeHtml4(t.namespace.decoded)}">
								${wc:escapeHtml4(t.namespace.decoded)}
							</div>
						</div>
						<div class="buttonContainer">
							<a href="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?csar" class="exportButton"></a>
							<a href="${v:URLencode(t.namespace.encoded)}/${v:URLencode(t.xmlId.encoded)}/?edit" class="editButton"></a>
							<%-- we need double encoding of the URL as the passing to javascript: decodes the given string once --%>
							<a href="javascript:deleteCI('${wc:escapeHtml4(t.xmlId.decoded)}', '${v:URLencode(v:URLencode(t.namespace.encoded))}/${v:URLencode(v:URLencode(t.xmlId.encoded))}/');" class="deleteButton" onclick="element = $(this).parent().parent().parent();"></a>
						</div>
					</div>
					<div class="right"></div>
				</div>
			</c:forEach>
			</td>
			<td id="gcprightcolumn" valign="top">
				<div id="overviewtopshadow"></div>
				<div id="overviewbottomshadow"></div>
			</td>
			<td valign="top">
				<div class="btn-group-vertical" id="buttonList">
					<button type="button" class="btn btn-default" onclick="openNewCIdiag();">Add new</button>
					<button type="button" class="btn btn-default" onclick="importCSAR();">Import CSAR</button>
				</div>
			</td>
		</tr>
	</table>
</div>

<script>

function entityContainerClicked(e) {
	var target = $(e.target);
	if (target.is("a")) {
		// do nothing as a nested a element is clicked
	} else {
		var ec = target.parents("div.entityContainer");
		var url = ec.attr('id');
		if (e.ctrlKey) {
			// emulate browser's default behavior to open a new tab
			window.open(url);
		} else {
			window.location = url;
		}
	}
}

$("div.entityContainer").on("click", entityContainerClicked);

/**
 * deletes given component instance
 * uses global variable "element", which stores the DOM element to delete upon successful deletion
 */
function deleteCI(name, URL) {
	deleteResource(name, URL, function() {
		element.remove();
	});
}

function importCSAR() {
	$('#upCSARDiag').modal('show');
}

// If export button is clicked with "CTRL", the plain XML is shown, not the CSAR
// We use "on" with filters instead as new elements could be added when pressing "Add new" (in the future)
// contained code is the same as the code of the CSAR button at the topology modeler (see index.jsp)
$(document).on("click", ".exportButton", function(evt) {
	var url = $(this).attr("href");
	if (evt.ctrlKey) {
		url = url.replace(/csar$/, "definitions");
	}
	window.open(url);
	return false;
});

<%-- Special feature in the case of the service template --%>
<c:if test="${it.type eq 'ServiceTemplate'}">
//If edit button is clicked with "CTRL", the topology modeler is opened, not the service template editor
//We use "on" with filters instead as new elements could be added when pressing "Add new" (in the future)
$(document).on("click", ".editButton", function(evt) {
	var url = $(this).attr("href");
	if (evt.ctrlKey) {
		url = url.replace(/\?edit$/, "topologytemplate/?edit");
		// open in new tab
		var newWin = window.open(url);
		// focussing the new window does not work in Chrome
		newWin.focus();
	} else {
		// normal behavior
		window.location = url;
	}
	evt.preventDefault();
});
</c:if>

$(".exportButton").tooltip({
	placement: 'bottom',
	html: true,
	title: "Export CSAR.<br/>Hold CTRL key to export XML only."
});
$(".editButton").tooltip({
	placement: 'bottom',
	html: true,
	title: <c:if test="${it.type eq 'ServiceTemplate'}">"Edit.<br/>Hold CTRL key to directly open the topology modeler."</c:if><c:if test="${not (it.type eq 'ServiceTemplate')}">"Edit"</c:if>
});
$(".deleteButton").tooltip({
	placement: 'bottom',
	title: "Delete"
});
</script>

</t:genericpage>

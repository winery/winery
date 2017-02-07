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
<%@tag description="used by genericcomponentpage.jsp and by implementations.jsp to create a component instance" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="w" uri="http://www.eclipse.org/winery/repository/functions"%>
<%--
function createResource(nameOfResource, fields, url, onSuccess) cannot be used as this method is more diverse
--%>

<%@attribute name="label" required="true" description="The lable to display"%>

<%@attribute name="URL" description=""%>
<%@attribute name="onSuccess" description=""%>
<%@attribute name="type" description="added to dataToSend when doing a POST"%>
<%@attribute name="typeSelectorData" type="java.util.Collection" description="All available types when creating a template. We do not support types with names (additional to the id) as the current TOSCA specification does not foresee the usage of both name and id at types"%>
<%@attribute name="openinnewwindow" description="if true, the editor for the created component instance is openend in a new window"%>

<div class="modal fade" id="addComponentInstanceDiag">
<div class="modal-dialog">
<div class="modal-content">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h4 class="modal-title">Add ${label}</h4>
	</div>
	<div class="modal-body" style="overflow-y: inherit;">
		<form id="addComponentInstanceForm" enctype="multipart/form-data">
			<%-- we send namespace + name to server. There, the ID is generated out of the name --%>
			<fieldset>
				<div class="form-group">
					<label for="nameOfNewCI" class="control-label">Name</label>
					<input class="form-control" name="name" id="nameOfNewCI" type="text" required="required" />
				</div>

				<t:namespaceChooser idOfInput="namespace" allNamespaces="${w:allNamespaces()}"></t:namespaceChooser>

			<%-- (optional) the type, for instance at an artifact template or node type implementation --%>
			<c:choose>
				<%-- Either directly given ... --%>
				<c:when test="${not empty type}">
					<%-- then, we just submit it together with the other data --%>
					<input id="ciType" type="hidden" class="form-control" name="type" value="${type}"/>
				</c:when>
				<%-- ... or a list is given given. --%>
				<%-- This is somewhat ugly as the UI displays no type dialog if no types are existing, but a template is to be created.
					We consider that as special case and do not add code to work around that issue.
					A good solution is to present an error dialog to the user if he hits that case:
					A hint should be presented to state that the user has to add a type first. --%>
				<c:when test="${empty type and not empty typeSelectorData}">
					<div class="form-group">
						<label for="ciType" class="control-label">Type</label>
						<%-- similar code to artifacts.jsp.openLink${name}ArtifactDiag().ajax.success --%>
						<select id="ciType" name="type" class="form-control">
							<c:forEach var="typeId" items="${typeSelectorData}">
								<option value="${typeId.QName}">${typeId.xmlId.decoded}</option>
							</c:forEach>
						</select>
					</div>
				</c:when>
			</c:choose>

			</fieldset>

		</form>
	</div>
	<div class="modal-footer">
		<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
		<button type="button" class="btn btn-primary" onclick="addComponentInstance();">Add</button>
	</div>
</div>
</div>
</div>

<script>
<c:if test="${empty type and not empty typeSelectorData}">
$("#ciType").select2();
</c:if>

$("#addComponentInstanceDiag").on("shown.bs.modal", function() {
	$("#nameOfNewCI").focus();
});

function addComponentInstance() {
	if (highlightRequiredFields()) {
		vShowError("Please fill in all required fields");
		return;
	}

	var namespace = $("#namespace").val();
	require(["URIjs/URI"], function(URI) {
		if (!URI(namespace).is("absolute")) {
			vShowError("Please enter a valid namespace");
			return;
		}

		var dataToSend = $('#addComponentInstanceForm').serialize();
		$.ajax({
			type: "POST",
			async: false,
			"data": dataToSend,
			<c:if test="${not empty URL}">"url": "${URL}",</c:if>
			dataType: "text",
			error: function(jqXHR, textStatus, errorThrown) {
				vShowAJAXError("Could not add ${label}", jqXHR, errorThrown);
			},
			success: function(resData, textStatus, jqXHR) {
				<c:if test="${not empty onSuccess}">
				${onSuccess}
				</c:if>

				//if we want to add the new entry directly in the list, we have to start with following:
				//var name = $('#nameOfNewCI').val();
				//var namespace = $('#namespaceOfNewCI').val();

				//otherwise: directly open edito
				$('#addComponentInstanceDiag').modal('hide');
				// open editor for newly created component (assumption: window.location ends with "/")
				var loc = jqXHR.getResponseHeader('Location');
				<c:choose>
					<c:when test="${openinnewwindow}">
						window.open(loc, "_blank");
					</c:when>
					<c:otherwise>
						window.location = loc;
					</c:otherwise>
				</c:choose>
			}
		});
	});
}

function openNewCIdiag() {
	$('#addComponentInstanceDiag').modal('show');
}

</script>

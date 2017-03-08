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
 *    Kálmán Képes - initial API and implementation and/or initial documentation
 *******************************************************************************/
--%>

<%@tag import="org.eclipse.winery.repository.Utils"%>
<%@tag import="org.eclipse.winery.repository.backend.BackendUtils"%>
<%@tag
	import="org.eclipse.winery.common.ids.definitions.ServiceTemplateId"%>
<%@tag import="org.eclipse.winery.repository.backend.Repository"%>
<%@tag
	import="org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource"%>
<%@tag import="org.eclipse.winery.model.tosca.TServiceTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TTag"%>
<%@tag import="java.util.ArrayList"%>
<%@tag import="java.util.Collection"%>
<%@tag import="java.util.Set"%>
<%@tag import="java.util.HashSet"%>
<%@tag import="javax.xml.namespace.QName"%>
<%@tag
	description="Dialog to create ServiceTemplates from a given artefact"
	pageEncoding="UTF-8"%>


<%@attribute name="allSubResources" required="true"
	type="java.util.TreeSet" description="All available ServiceTemplates"%>
<%@attribute name="allNodeTypes" required="true"
	type="java.util.Collection" description="All available Node Types"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<link href="${pageContext.request.contextPath}/css/tag-basic-style.css" rel="stylesheet"></link>
<script type="text/javascript" src="${pageContext.request.contextPath}/components/taggingJS/tagging.min.js"></script>

<%
	Set<QName> artefactTypes = new HashSet<QName>();
	Set<QName> infrastructureNodeTypes = new HashSet<QName>();
%>

<%
	for (Object obj : allSubResources) {
		ServiceTemplateId id = (ServiceTemplateId) obj;
		ServiceTemplateResource stRes = new ServiceTemplateResource(id);
		TServiceTemplate serviceTemplate = stRes.getServiceTemplate();
		if (serviceTemplate.getTags() != null) {
			int check = 0;
			QName artefactType = null;
			for (TTag tag : serviceTemplate.getTags().getTag()) {
				switch (tag.getName()) {
					case "xaasPackageNode" :
						check++;
						break;
					case "xaasPackageArtefactType" :
						check++;
						artefactType = QName.valueOf(tag.getValue());
						break;
					case "xaasPackageDeploymentArtefact" :
						check++;
						break;
					case "xaasPackageInfrastructure" :
						// optional tag, hence no check++
						infrastructureNodeTypes.add(QName.valueOf(tag.getValue()));
					default :
						break;
				}
			}
			if (check == 3) {
				artefactTypes.add(artefactType);
			}
		}
	}
%>

<div class="modal fade" id="createFromArtefactDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title">Create ServiceTemplate from Artefact</h4>
			</div>
			<div class="modal-body">
				<form id="createFromArtefactForm" enctype="multipart/form-data">
					<fieldset>
						<div class="form-group">
							<label for="artifactType" class="control-label">Type</label>
							<select id="artifactType" name="type" class="form-control">
								<c:forEach var="typeId" items="<%=artefactTypes%>">
									<option value="${typeId.toString()}">${typeId.toString()}</option>
								</c:forEach>
							</select>
						</div>
						<div class="form-group">
							<label for="createFromArtefactForm">Select Artefact:</label>
							<input
								id="createFromArtefactFormUpload" class="form-control"
								type="file" name="createFromArtefactForm" />
						</div>
						<div class="form-group">
							<label for="createFromArtefactForm">Tags:</label>
							<div id="createArtefactFormTags"></div>
						</div>
						<div class="form-group" id="nodeTypesDiv">
							<label for="artifactType">Node Types:</label> <select
								name="artifactType" class="form-control" id="nodeTypes" multiple>
								<c:forEach var="t" items="${allNodeTypes}">
									<option value="${t.getQName()}">${t.getQName()}</option>
								</c:forEach>
							</select>
						</div>
						<c:if test="<%=!infrastructureNodeTypes.isEmpty()%>">
							<div class="form-group" id="infrastructureDiv">
							<label for="infrastructureNodeTypes">Infrastructure:</label>
							<select name="infrastructure" class="form-control" id="infrastructureNodeTypes">
								<option value="" selected>None</option>
								<c:forEach var="t" items="<%=infrastructureNodeTypes%>">
									<option value="${t.toString()}">${t.toString()}</option>
								</c:forEach>
							</select>
						</div>
						</c:if>
					</fieldset>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal"
					onclick="$('#createFromArtefactDiag').modal('hide');">Cancel</button>
				<button type="button" class="btn btn-primary" data-dismiss="modal"
					onclick="createTemplateFromArtefact();">Add</button>
			</div>
		</div>
	</div>
</div>

<script>
	var files;

	$('#createFromArtefactFormUpload').on('change', prepareUpload);

	// Grab the files and set them to our variable
	function prepareUpload(event) {
		files = event.target.files;
	}

	var options = {
		"no-duplicate" : true,
		"tag-box-class" : "tagging",
		"case-sensitive" : true
	}

	var taggin = $('#createArtefactFormTags').tagging(options);

	function createTemplateFromArtefact() {
		var artefactType = $('#artefactType').val();
		var tags = taggin[0].tagging("getTags");
		var nodeTypes = $('#nodeTypes').val();
		var infrastructureNodeType = $('#infrastructureNodeTypes').val();
		var filesForUpload = $('#createFromArtefactFormUpload')[0].files;

		// upload data
		var formData = new FormData();

		// the file
		for (var i = 0; i < filesForUpload.length; i++) {
			var file = filesForUpload[i];
			formData.append("file", file, file.name);
		}

		formData.append("artefactType", artefactType);

		if(infrastructureNodeType != "") {
			formData.append("infrastructureNodeType", infrastructureNodeType);
		}


		if (nodeTypes == null) {
			formData.append("nodeTypes", null);
		} else {
			formData.append("nodeTypes", nodeTypes);
		}

		if (tags.length == 0) {
			formData.append("tags", null);
		} else {
			formData.append("tags", tags);
		}

		$.ajax({
			url : "${url}",
			type : "POST",
			async : false,
			data : formData,
			processData : false,
			contentType : false,
			error : function(jqXHR, textStatus, errorThrown) {
				vShowError("Could not create ServiceTemplate from artefact: "
						+ errorThrown + "<br/>" + jqXHR.responseText);
			},
			success : function(id, textStatus, jqXHR) {
				// Data has been validated at the server
				// We can just add the local data

				var loc = jqXHR.getResponseHeader('Location');
				$('#createFromArtefactDiag').modal('hide');
				window.location = loc;
			}
		});

	}
</script>

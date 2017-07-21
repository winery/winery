<%--
/*******************************************************************************
 * Copyright (c) 2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Niko Stadelmaier - removal of select2 library
 *    Philipp Meyer - removal of select2 library
 *******************************************************************************/
--%>
<%@tag description="Dialog for adding an implementation / deployment artifact" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fup" tagdir="/WEB-INF/tags/common"%>

<%@attribute name="name" required="true" description="Implementation | Deployment"%>
<%@attribute name="repositoryURL" required="true" description="the URL of Winery's repository"%>
<%@attribute name="uiURL" required="true" description="the URL of Winery's repository"%>
<%@attribute name="onSuccessfulArtifactCreationFunction" required="true" description="javascript code to be executed when the artifact has been successfully created. Parameter: artifactInfo"%>
<%@attribute name="allArtifactTypes" required="true" type="java.util.Collection" description="All available artifact types"%>
<%@attribute name="allNamespaces" required="true" type="java.util.Collection" description="All known namespaces"%>
<%@attribute name="defaultNSForArtifactTemplate" required="true" description="the default namespace of the artifact template"%>

<%-- either URL or a function to be called for addition --%>
<%@attribute name="URL" required="true" description="the URL of the artifact collection. May also be a function returning the correct URL (used at the topology modeler). I.e., it is an expression being evaluated"%>

<%@attribute name="isDeploymentArtifact" required="true" type="java.lang.Boolean" description="Is this dialog used to create deployment artifacts?"%>
<%-- required if implementation artifact --%>
<%@attribute name="interfacesOfAssociatedType" type="java.util.List" %>

<script>
// TODO: check if allArtifactTypes is empty -> then an error message should be shown. Alternative: Add "Manage" button next to "artifact types"

function addArtifact() {
	if (highlightRequiredFields()) {
		vShowError("Please fill out required fields.");
		return;
	}

	var artifactTemplateCreationMode = $("input[name='artifactTemplateCreation']:checked").val();
	var autoCreateArtifactTemplate = (artifactTemplateCreationMode=="createArtifactTemplate");

	if (autoCreateArtifactTemplate && ($("#artifactTemplateNameIsValid:visible").hasClass("invalid"))) {
		vShowError("Please ensure that the artifact template QName is valid.");
		return;
	}


	/* begin: form serialization */

	var theForm = $('#add${name}ArtifactForm');

	// do not serialze hidden fields
	// theForm.find("select:hidden,input:hidden").attr("disabled", "disabled");
	// Because we use "select2", the user-visible select fields are divs. The "real" selects are hidden.
	// Therefore, we disable fields manually :)
	var disabledFields;
	if (artifactTemplateCreationMode == "skipArtifactTemplate") {
		disabledFields = ["artifactTemplateName", "artifactTemplateNS", "artifactTemplateToLink"];
	} else if (artifactTemplateCreationMode == "createArtifactTemplate") {
		disabledFields = ["artifactTemplateToLink"];
	} else if (artifactTemplateCreationMode == "linkArtifactTemplate") {
		disabledFields = ["artifactTemplateName",  "artifactTemplateNS", "artifactType"];
	} else {
		vShowError("Code not consistent with UI");
	}

	// make a clean form
	disabledFields.forEach(function(element) {
		$("#"+element).prop("disabled", "true");
	});
	$("input[name='artifactTemplateCreation']").attr("disabled", "disabled");

	// do not serialize choice directly, but ...
	// ... append "autoCreateArtifactTemplate=true" in case the artifact template should be auto created
	var data = theForm.serialize();
	if (autoCreateArtifactTemplate) {
		data = data + "&autoCreateArtifactTemplate=true";
	}

	// enable fields again
	disabledFields.forEach(function(element) {
		$("#"+element).removeAttr("disabled");
	});
	$("input[name='artifactTemplateCreation']").removeAttr("disabled");

	/* end: form serialization */

	<c:if test="${not isDeploymentArtifact}">
	var operationVal = $("#operationName").val();
	if ((operationVal) && (operationVal != "")) {
		var operationName = $("#operationName option:selected").text();
		// The operationname is prefixed with the namespace, because of "nextselect"
		// we have to undo that effect.
		// Therefore, we replace the complete operationName parameter

		var pos = data.indexOf("operationName=");
		var posNextParam = data.indexOf("&", pos);
		data = data.substr(0, pos) + "operationName=" + operationName + data.substr(posNextParam);
	}
	</c:if>

	// Because of the new API, we need to convert the data to a JSON-string.
	data = data.split('&');
	var jsonData = '{';
	for (var i = 0; i < data.length; i++) {
		var tmp = data[i].split("=");
		if (tmp[0] == 'artifactTemplateNS') {
		    tmp[0] = 'artifactTemplateNamespace';
		}
		jsonData += '"' + tmp[0] + '": "' + decodeURIComponent(tmp[1]) + '",';
	}
	jsonData = jsonData.substr(0, jsonData.length-1);
	jsonData += '}';

	console.log(jsonData);

	// We assume that the artifact type exists
	// i.e., that it was not deleted during loading of the dialog

	// The deployment artifact resource allows auto creation of the artifact template
	// We do not need to do that manually using a separate POST call
	// TODO: In a future version, this might be better have a clean way to create additional content for an artifact template
	// do the addCall
	$.ajax({
		url: ${URL},
		type: "POST",
		async: false,
		"data": jsonData,
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not create ${name} Artifact", jqXHR, errorThrown);
		},
		success: function(data, textStatus, jqXHR) {
		    vShowSuccess("Successfully added ${name} Artifact!", "Success");
			// prepare data for onSuccessfulArtifactCreationFunction
			// even though interaceName and operationName do not exist at DA, accessing it via jQuery works: then "undefined" is returned, which is OK
			var artifactInfo = {
				name: $("#artifactName").val(),
				interfaceName: $("#interfaceName").val(),
				operationName: $("#operationName option:selected").text()
			};
			if (artifactTemplateCreationMode == "skipArtifactTemplate") {
				// artifactTemplate remains unset as there is not artifactTemplate to be created
				artifactInfo.artifactType = $("#artifactType").val();
			} else if (artifactTemplateCreationMode == "createArtifactTemplate") {
				artifactInfo.artifactTemplateName = $("#artifactTemplateName").val();
				// FIXME: This is a quick hack - the name could have been changed at the server as it might contain invalid characters for an id
				// In other words, $("#artifactTemplateName").val() might not be the localName of the artifactTemplate
				artifactInfo.artifactTemplate = "{" + $("#artifactTemplateNS").val() + "}" + $("#artifactTemplateName").val();
				artifactInfo.artifactType = $("#artifactType").val();
			} else if (artifactTemplateCreationMode == "linkArtifactTemplate") {
				artifactInfo.artifactTemplateName = $("#artifactTemplateToLink option:selected").text();
				artifactInfo.artifactTemplate = $("#artifactTemplateToLink").val();
				// artifact type is a mandantory field
				// we have to ask the artifact template for the QName of its type and then use this data
				require(["winery-support-common"], function(wsc) {
					var nsAndId = wsc.getNamespaceAndLocalNameFromQName(artifactInfo.artifactTemplate);
					var url = makeArtifactTemplateURL("${repositoryURL}", nsAndId.namespace, nsAndId.localname);
					$.ajax({
						type: "GET",
						async: false,
						url: url + "?type",
						dataType: "text",
						error: function(jqXHR, textStatus, errorThrown) {
							vShowAJAXError("Could not get type of artifact template", jqXHR, errorThrown);
							return;
						},
						success: function(resData, textStatus, jqXHR) {
							// QName is directly returned
							artifactInfo.artifactType = resData;
						}
					});
				});
			} else {
				vShowError("Code not consistent with UI");
			}
			// now, artifactInfo is filled completly

			// the function can be called
			${onSuccessfulArtifactCreationFunction}(artifactInfo);

			$('#add${name}ArtifactDiag').modal('hide');
			vShowSuccess("Artifact added successfully");

			// Do not show add files modal because of the new backend API
			vShowNotification("To add files, please use the Winery for now.", "File upload");
			/*if (autoCreateArtifactTemplate) {
				var aritfactTemplateNS = $("#artifactTemplateNS").val();
				var artifactTemplateName = $("#artifactTemplateName").val();
				var artifactTemplateURL = makeArtifactTemplateURL("${repositoryURL}", aritfactTemplateNS, artifactTemplateName);
				$("#artifactTemplateNameAtUploadFiles").text(artifactTemplateName).attr("href", artifactTemplateURL);
				var url = artifactTemplateURL + "files/";
				$('#fileupload').fileupload('option', 'url', url);
				$("#addFilesToArtifactTemplate").modal('show');
			}*/
		}
	});

}
</script>

<c:if test="${not isDeploymentArtifact}">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/nextselect.js"></script>
	<script>
		var dependendSelects = {"#interfaceName": "#operationName"};
		var interfaceOpData = {
			"": {
				label : "(none)",
				"options" : []
			}<c:if test="${not empty interfacesOfAssociatedType}">,</c:if>
			<c:forEach var="t" items="${interfacesOfAssociatedType}">
				// no label necessary as this list is pre-filled
				"${t.name}": {
					"options" : [
						"",
						<c:forEach var="u" varStatus="loop" items="${t.operationsResouce.listOfAllEntityIdsAsList}">
							"${t.name}:${u}"<c:if test="${!loop.last}">,</c:if>
						</c:forEach>
					]
				},
			</c:forEach>
			<c:forEach var="t" varStatus="outerLoop" items="${interfacesOfAssociatedType}">
				<c:forEach var="u" varStatus="innerLoop" items="${t.operationsResouce.listOfAllEntityIdsAsList}">
				"${t.name}:${u}" : {
					"label": "${u}"
				}<c:if test="${!innerLoop.last or !outerLoop.last}">,</c:if>
				</c:forEach>
			</c:forEach>
		};
	</script>
</c:if>

<div class="modal fade" id="add${name}ArtifactDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Add ${name} Artifact</h4>
			</div>
			<div class="modal-body">
				<form id="add${name}ArtifactForm" enctype="multipart/form-data">
					<fieldset>

						<div class="form-group">
							<label>Name</label>
							<input class="form-control" name="artifactName" id="artifactName" type="text" required="required" autocomplete="on" />
						</div>

						<c:if test="${not isDeploymentArtifact}">
							<div class="form-group">
								<label for="interfaceName">Interface Name</label>
								<select name="interfaceName" id="interfaceName" class="form-control" onchange="updateListContent(this.value, '#operationName', dependendSelects, interfaceOpData);">
									<option value="" selected="selected">(none)</option>
									<c:forEach var="t" items="${interfacesOfAssociatedType}">
										<option value="${t.name}">${t.name}</option>
									</c:forEach>
								</select>
							</div>

							<div class="form-group">
								<label for="operationName">Operation Name</label>
								<select name="operationName" id="operationName" class="form-control">
									<%-- options filled by updateListContent defined by nextselect.js --%>
								</select>
							</div>
						</c:if>

						<h4>Artifact Template Creation</h4>
						<div class="radio">
							<label>
								<input type="radio" name="artifactTemplateCreation" value="createArtifactTemplate" checked="checked" id="createArtifactTemplateInput">Create Artifact Template</input>
							</label>
							<p class="help-block">Check if you want to upload <strong>new</strong> files, you do not want to reuse existing files and you do not point to an image library.</p>
						</div>
						<div class="radio">
							<label>
								<input type="radio" name="artifactTemplateCreation" value="linkArtifactTemplate">Link Artifact Template</input>
							</label>
							<p class="help-block">Check if you want to reuse existing files.</p>
						</div>
						<div class="radio">
							<label>
								<input type="radio" name="artifactTemplateCreation" value="skipArtifactTemplate">Do not create an artifact template</input>
							</label>
							<p class="help-block">Check if you want to point to an image library.</p>
						</div>
					</fieldset>
					<fieldset id="artifactTypeFieldset">
						<div class="form-group" id="artifactTypeDiv">
							<label for="artifactType">Artifact Type</label>
							<select name="artifactType" class="form-control" id="artifactType">
								<c:forEach var="t" items="${allArtifactTypes}">
									<option value="${t.toString()}">${t.localPart}</option>
								</c:forEach>
							</select>
						</div>
					</fieldset>
					<fieldset>

						<fup:artifacttemplateselection allNamespaces="${allNamespaces}" repositoryURL="${repositoryURL}" defaultNSForArtifactTemplate="${defaultNSForArtifactTemplate}"/>

						<div id="linkArtifactTemplate" class="form-group" style="display:none;">
							<label for="divArtifactTemplateToLink">Artifact Template</label>
							<div id="divArtifactTemplateToLink">
								<%-- filled by jQuery at openAdd${name}ArtifactDiag() --%>
								<select id=artifactTemplateToLink name="artifactTemplate" class="form-control" style="max-width: 90%">
								</select>
								<%-- URL is changed each time the selection is changed --%>
								<a href="#" target="_blank" class="btn btn-info btn-sm" id="viewArtifactTemplateToLink">view</a>
							</div>
						</div>
					</fieldset>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary" onclick="addArtifact();">Add</button>
			</div>
		</div>
	</div>
</div>


<script>
function openAdd${name}ArtifactDiag() {
	$.ajax({
		url: "${repositoryURL}/artifacttemplates/",
		dataType: "json",
		async: false,
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not fetch available artifact templates", jqXHR, errorThrown);
		},
		success: function(data, textStatus, jqXHR) {
			var select = $("#artifactTemplateToLink");
			select.empty();
			$.each(data, function(index, o) {
				var qname = "{" + this.namespace + "}" + this.id;
				var option = '<option value="' + qname + '">' + this.name + '</option>';
				select.append(option);
				if (index==0) {
					// first element
					// this is the selected element
					// we put it as href to the "view" button
					$("#viewArtifactTemplateToLink").attr("href", makeArtifactTemplateURL("${uiURL}", this.namespace, this.id));
				}
			});
			select.trigger("change");
			$('#add${name}ArtifactDiag').modal('show');
		}
	});
}
	<c:if test="${not isDeploymentArtifact}">
	// the dependend select cannot be a select2 until https://github.com/ivaynberg/select2/issues/1656 is resolved
	</c:if>

requirejs(['tmpl', 'jquery.ui.widget', 'jquery.fileupload', 'jquery.fileupload-ui'], function() {
	$('#fileupload').fileupload({
		"autoUpload": true
	});
});

$(function(){
	$("input[name='artifactTemplateCreation']").on("change", function(e) {
		var choice = $(e.target).attr("value");
		if (choice == "skipArtifactTemplate") {
			$(".createArtifactTemplate").hide();
			$("#linkArtifactTemplate").hide();
			$("#artifactTypeFieldset").removeAttr("disabled");
			$("#artifactTypeDiv").show();
		} else if (choice == "createArtifactTemplate") {
			$(".createArtifactTemplate").show();
			$("#linkArtifactTemplate").hide();
			$("#artifactTypeFieldset").removeAttr("disabled");
			$("#artifactTypeDiv").show();
			// one might be copy the template name to the artifact template name (if ($("#artifactTemplateName").val() == ""))
		} else if (choice == "linkArtifactTemplate") {
			$(".createArtifactTemplate").hide();
			$("#linkArtifactTemplate").show();
			$("#artifactTypeFieldset").attr("disabled", "disabled");
			$("#artifactTypeDiv").hide();
		} else {
			vShowError("Code not consistent with UI");
		};
	});

	$("#add${name}ArtifactDiag").on('shown.bs.modal', function() {
		$(this).find('form')[0].reset();
		// createArtifactTemplate is the default seeting for the form
		// reset the dialog to this choice
		$("#createArtifactTemplateInput").trigger("change");
	});

	$("#artifactName").typing({
		start: function(event, $elem) {
			if (syncDAnameWithATname) {
				require(["artifacttemplateselection"], function(ats) {
					ats.flagArtifactTemplateNameAsUpdating();
				});
			}
		},
		stop: function(event, $elem) {
			// value is copied at the "change keyup input" event at #artifactName
				require(["artifacttemplateselection"], function(ats) {
					ats.checkArtifactTemplateName();
				});
		}
	});

	$("#artifactName")
	// tip by http://solicitingfame.com/2011/11/09/jquery-keyup-vs-bind/
	.bind("change keyup input", function() {
		if (syncDAnameWithATname) {
			$("#artifactTemplateName").val(this.value);
		}
	})
	.on("focus", function() {
		syncDAnameWithATname = ($("#artifactTemplateName").is(":visible")) && (this.value == $("#artifactTemplateName").val());
	});

	$("#artifactTemplateToLink").on("change", function(evt) {
		if (evt.val) {
			// TODO: possibly use makeArtifactTemplateURL("${repositoryURL}", this.namespace, this.id)) here
			require(["winery-support-common"], function(w) {
				var fragment = w.getURLFragmentOutOfFullQName(evt.val);
				var url = "${uiURL}/artifacttemplates/" + fragment + "/";
				$("#viewArtifactTemplateToLink").attr("href", url);
			});
		}
	});
});
</script>


<%-- file uploading part --%>

<div class="modal fade" id="addFilesToArtifactTemplate">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Add files to artifact template <a id="artifactTemplateNameAtUploadFiles"></a></h4>
			</div>
			<div class="modal-body">
				<fup:jquery-file-upload-full loadexistingfiles="false"></fup:jquery-file-upload-full>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		<!-- addFilesToArtifactTemplate -->
		</div>
	</div>
</div>

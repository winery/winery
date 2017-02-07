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
 *    Tobias Binz - communication with the nested iframe
 *******************************************************************************/
--%>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="w"  tagdir="/WEB-INF/tags"%>
<%@taglib prefix="o"  tagdir="/WEB-INF/tags/common/orioneditor"%>
<%@taglib prefix="b"  tagdir="/WEB-INF/tags/servicetemplates/boundarydefinitions"%>
<%@taglib prefix="pol" tagdir="/WEB-INF/tags/common/policies" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions"%>
<%@taglib prefix="wr" uri="http://www.eclipse.org/winery/repository/functions"%>

<%@page import="org.eclipse.winery.common.ModelUtilities"%>

<pol:policydiag allPolicyTypes="${it.allPolicyTypes}" repositoryURL="${it.repositoryURL}" />

<b:browseForServiceTemplatePropertyReqOrCap definedPropertiesAsJSONString="${it.definedPropertiesAsJSONString}" />

<div class="modal fade" id="propertyMappingDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Add property mapping</h4>
			</div>
			<div class="modal-body">
				<form id="propertyMappingForm" enctype="multipart/form-data">
					<fieldset>
						<div class="form-group">
							<label for="serviceTemplatePropertyRef">Service Template Property</label>
							<div class="row">
								<div class="col-xs-10">
									<input name="serviceTemplatePropertyRef" id="serviceTemplatePropertyRef" class="form-control" type="text" required="required">
								</div>
								<div class="col-xs-2">
									<button type="button" class="btn btn-default btn-sm" onclick="browseForServiceTemplateProperty($('#serviceTemplatePropertyRef'));">Browse</button>
								</div>
							</div>
						</div>

						<div class="form-group">
							<label for="targetObjectRef">Target: Node Template, Requirement, Capability, or Relationship Template</label>
							<div class="row">
								<div class="col-xs-4">
									<input name="targetObjectRef" id="targetObjectRef" class="form-control" type="text" required="required">
								</div>
								<div class="col-xs-2">
									<button type="button" class="btn btn-default btn-sm" onclick="browseForTemplateAndProperty();">Browse</button>
								</div>
							</div>
						</div>

						<div class="form-group">
							<label for="targetPropertyRef">Target Property</label>
							<input name="targetPropertyRef" id="targetPropertyRef" class="form-control" type="text" required="required"/>
						</div>
					</fieldset>
				</form>
			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button id="addPropertyMapping"    type="button" class="btn btn-primary" onclick="addPropertyMapping()">Add</button>
				<button id="updatePropertyMapping" type="button" class="btn btn-primary" onclick="updatePropertyMapping()">Update</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="propertyConstraintDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Add property constraint</h4>
			</div>
			<div class="modal-body">
				<form id="propertyMappingForm" enctype="multipart/form-data">
					<fieldset>
						<div class="form-group">
							<label for="serviceTemplatePropertyRef">Service Template Property</label>
							<div class="row">
								<div class="col-xs-10">
									<input name="serviceTemplatePropertyRef" id="serviceTemplatePropertyRefForConstraint" class="form-control" type="text" />
								</div>
								<div class="col-xs-2">
									<button type="button" class="btn btn-default btn-sm" onclick="browseForServiceTemplateProperty($('#serviceTemplatePropertyRefForConstraint'));">Browse</button>
								</div>
							</div>
						</div>

						<w:typeswithshortnameasselect label="Constraint Type" selectname="constraintType" type="constrainttype" typesWithShortNames="${it.constraintTypes}"/>

						<div class="form-group">
							... constraint fragment ...
						</div>
					</fieldset>
				</form>
			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button id="addPropertyConstraint"    type="button" class="btn btn-primary" onclick="addPropertyMapping()">Add</button>
				<button id="updatePropertyConstraint" type="button" class="btn btn-primary" onclick="updatePropertyMapping()">Update</button>
			</div>
		</div>
	</div>
</div>



<div id="alltabswithcontent">
<ul class="nav nav-tabs" id="myTab">
	<li class="active"><a href="#properties">Properties</a></li>
	<li><a href="#propertymappings">Property Mappings</a></li>
	<li><a href="#propertyconstraints">Property Constraints</a></li>
	<li><a href="#requirements">Requirements</a></li>
	<li><a href="#capabilities">Capabilities</a></li>
	<li><a href="#policies">Policies</a></li>
	<li><a href="#interfaces">Interfaces</a></li>
	<li><a href="#xml">XML</a></li>
</ul>

<div class="tab-content">

	<div class="tab-pane active" id="properties">
		<%-- reloadAfterSuccess is necessary as the XMLtree has to be changed --%>
		<o:orioneditorarea areaid="XMLtextarea" url="boundarydefinitions/properties" reloadAfterSuccess="true">${it.definedPropertiesAsEscapedHTML}</o:orioneditorarea>
	</div>

	<div class="tab-pane" id="propertymappings">
		<c:choose>
			<c:when test="${empty it.definedPropertiesAsEscapedHTML}">
			<p>No properties available. Thus, no properties can be mapped. Please define properties.</p>
			</c:when>
			<c:otherwise>
				<button id="deleteRequirement" class="rightbutton btn btn-xs btn-danger" onclick="deleteOnServerAndInTable(propertyMappingsTableInfo, 'Property Mapping', 'boundarydefinitions/propertymappings/');">Remove</button>
				<button class="rightbutton btn btn-xs btn-info" onclick="openAddPropertyMappingDiag();">Add</button>
				<button class="rightbutton btn btn-xs btn-primary" onclick="openUpdatePropertyMappingDiag();">Edit</button>
				<table id="propertyMappingsTable">
					<thead>
						<tr>
							<th>Service Template Property</th>
							<th>Target</th>
							<th>Target Property</th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${not empty it.defs.properties and not empty it.defs.properties.propertyMappings and not empty it.defs.properties.propertyMappings.propertyMapping}">
							<c:forEach items="${it.defs.properties.propertyMappings.propertyMapping}" var="propertyMapping">
								<tr>
									<td>${propertyMapping.serviceTemplatePropertyRef}</td>
									<td>${propertyMapping.targetObjectRef.id}</td> <%-- .name cannot be used as it is not an Id. Future work: Store the id in a seperate field and show the name to the user --%>
									<td>${propertyMapping.targetPropertyRef}</td>
								</tr>
							</c:forEach>
						</c:if>
					</tbody>
				</table>
			</c:otherwise>
		</c:choose>
	</div>

	<%-- TODO: provide this as .tag. The property constraint resource should also be provided as tag --%>
	<div class="tab-pane" id="propertyconstraints">
		<button class="rightbutton btn btn-xs btn-danger" onclick="deleteOnServerAndInTable(propertyConstraintsTableInfo, 'Property Constraint', 'propertyconstraints/');">Remove</button>
		<button class="rightbutton btn btn-xs btn-info" onclick="openAddPropertyConstraintDiag();">Add</button>
		<button class="rightbutton btn btn-xs btn-primary" onclick="openUpdatePropertyConstraintDiag();">Edit</button>
		<table id="propertyconstraintstable">
			<thead>
				<tr>
					<th>(internal id)</th>
					<th>Service Template Property</th>
					<th>Constraint Type</th>
					<th>Constraint</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>example</td>
					<td>/demo</td>
					<td>http://www.example.com/accessrestrictions</td>
					<td>(not yet implemented)</td>
				</tr>
			</tbody>
		</table>
	</div>

	<div class="tab-pane" id="requirements">
		<button class="rightbutton btn btn-xs btn-danger" onclick="deleteOnServerAndInTable(requirementsTableInfo, 'Requirement', 'boundarydefinitions/requirements/');">Remove</button>
		<button class="rightbutton btn btn-xs btn-info" onclick="openReqEditor(false);">Add</button>
		<button class="rightbutton btn btn-xs btn-primary" onclick="openReqEditor(true);">Edit</button>
		<table id="requirementstable">
			<thead>
				<tr>
					<th>(Id)</th><%-- of the boundary requirement, also used as the id of this element--%>
					<th>Name</th>
					<th>Reference</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${it.defs.requirements.requirement}">
					<tr>
						<td>${wr:determineIdUsingHashCode(item)}</td>
						<td>${item.name}</td>
						<td>${item.ref.id}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>

	<div class="tab-pane" id="capabilities">
		<%-- mirrored from requirements --%>
		<button class="rightbutton btn btn-xs btn-danger" onclick="deleteOnServerAndInTable(capabilitiesTableInfo, 'Capability', 'boundarydefinitions/capabilities/');">Remove</button>
		<button class="rightbutton btn btn-xs btn-info" onclick="openCapEditor(false);">Add</button>
		<button class="rightbutton btn btn-xs btn-primary" onclick="openCapEditor(true);">Edit</button>
		<table id="capabilitiestable">
			<thead>
				<tr>
					<th>(Id)</th><%-- of the boundary requirement, also used as the id of this element--%>
					<th>Name</th>
					<th>Reference</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${it.defs.capabilities.capability}">
					<tr>
						<td>${wr:determineIdUsingHashCode(item)}</td>
						<td>${item.name}</td>
						<td>${item.ref.id}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>

	<div class="tab-pane" id="policies">
		<pol:policies list="${it.defs.policies.policy}" repositoryURL="${it.repositoryURL}" />
		<br />
		<button class="btn btn-default btn-sm btn-primary" onclick="savePolicies();">Save</button>
	</div>
	<script>
		// work around for topology modeler's policy usage
		$(".addnewpolicy").show();

		// required for the policy addition dialog
		winery.repositoryURL = "${it.repositoryURL}";

		function savePolicies() {
			require(["winery-support-common", "XMLWriter"], function(wsc) {
				var xmlw = new XMLWriter("utf-8");
				xmlw.writeStartDocument();

				wsc.writeCollectionDefinedByATextArea(xmlw,
					$("div.policiesContainer").children("div.content").children("div.policy"),
					"Policies");

				xmlw.writeEndDocument();

				var data = xmlw.flush();

				$.ajax({
					url: "boundarydefinitions/policies",
					data: data,
					type: "PUT",
					contentType: "application/xml",
				}).done(function (result) {
					vShowSuccess("Sucessfully saved policies");
				}).fail(function(jqXHR, textStatus, errorThrown) {
					vShowAJAXError("Could not save policies", jqXHR, errorThrown);
				});
			});
		}
	</script>

	<b:browseForX XLong="node template" XShort="NodeTemplate"/>
	<script>
	/**
	 * called from browseForX
	 */
	function setNodeTemplateRef() {
		var newRef = $("#NodeTemplateReferenceField").val();
		$("#nodeTemplateRefForOperation").val(newRef);
		storeNewReference(newRef);
		updateTargetInterfaceAndOperation(true);
		$("#browseForNodeTemplateDiag").modal("hide");
	}

	function browseForNodeTemplate() {
		$("#NodeTemplateReferenceField").val($("#nodeTemplateRefForOperation").val());
		$("#browseForNodeTemplateDiag").modal("show");
	}
	</script>

	<b:browseForX XLong="relationship template" XShort="RelationshipTemplate"/>
	<script>
	/**
	 * called from browseForX
	 */
	function setRelationshipTemplateRef() {
		var newRef = $("#RelationshipTemplateReferenceField").val();
		$("#relationshipTemplateRefForOperation").val(newRef);
		storeNewReference(newRef);
		updateTargetInterfaceAndOperation(false);
		$("#browseForRelationshipTemplateDiag").modal("hide");
	}

	function browseForRelationshipTemplate() {
		$("#RelationshipTemplateReferenceField").val($("#relationshipTemplateRefForOperation").val());
		$("#browseForRelationshipTemplateDiag").modal("show");
	}
	</script>

	<script>
	function browseForPlan() {
		vShowError("not yet implemented.");
	}
	</script>

	<div class="tab-pane" id="interfaces">
		<h4>Provided Interface and Operation</h4>

		<form class="form-horizontal">
			<div class="form-group">
				<label for="interface" class="col-sm-1 control-label">Interface</label>
				<div class="col-sm-8">
					<input id="interface" class="form-control" placeholder="NCName or URI">
				</div>
				<button type="button" class="btn btn-danger btn-sm col-sm-1" onclick="deleteCurrentlySelectedInterface();">Delete</button>
			</div>
			<div class="form-group">
				<label for="operation" class="col-sm-1 control-label">Operation</label>
				<div class="col-sm-8">
					<input id="operation" class="form-control" placeholder="NCName">
				</div>
				<button type="button" class="btn btn-danger btn-sm col-sm-1" onclick="deleteCurrentlySelectedOperation();">Delete</button>
			</div>
		</form>

		<h4>Target</h4>

		<form id="reftargettypeForm">
			<label>
				<input id="nodeRadio" type="radio" name="reftargettype" value="node" checked="checked">
				Node Template
			</label>
			<label>
				<input id="relationshipRadio" type="radio" name="reftargettype" value="relationship">
				Relationship Template
			</label>
			<label>
				<input id="planRadio" type="radio" name="reftargettype" value="plan">
				Plan
			</label>
		</form>

		<form class="form-horizontal">
			<div class="form-group" id="selectNodeTemplateDiv">
				<label for="nodeTemplateRefForOperation" class="col-sm-1 control-label">Reference</label>
				<div class="col-sm-8">
					<input id="nodeTemplateRefForOperation" class="form-control">
				</div>
				<button type="button" class="btn btn-default btn-sm col-sm-1" onclick="browseForNodeTemplate();">Browse</button>
			</div>

			<div class="form-group" id="selectRelationshipTemplateDiv" style="display:none;">
				<label for="relationshipTemplateRefForOperation" class="col-sm-1 control-label">Reference</label>
				<div class="col-sm-8">
					<input id="relationshipTemplateRefForOperation" class="form-control">
				</div>
				<button type="button" class="btn btn-default btn-sm col-sm-1" onclick="browseForRelationshipTemplate();">Browse</button>
			</div>

			<div class="form-group" id="selectPlanDiv" style="display:none;">
				<label for="planRefForOperation" class="col-sm-1 control-label">Reference</label>
				<div class="col-sm-8">
					<c:choose>
						<c:when test="${empty it.listOfAllPlans}">
							<p>No plans available>
						</c:when>
						<c:otherwise>
							<a id="planRefForOperation" href="#"></a>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</form>

		<h4>Target Interface and Operation</h4>
		<form class="form-horizontal">
			<div class="form-group" id="selectInterfaceNameDiv">
				<label for="TargetInterface" class="col-sm-1 control-label">Interface</label>
				<div class="col-sm-8">
					<input id="TargetInterface" class="form-control">
				</div>
			</div>

			<div class="form-group" id="selectOperationNameDiv">
				<label for="TargetOperation" class="col-sm-1 control-label">Operation</label>
				<div class="col-sm-8">
					<input id="TargetOperation" class="form-control">
				</div>
			</div>
		</form>

		<div id="notApplicableDiv" style="display:none;">
			<p>not applicable in the case of plans</p>
		</div>

	</div>

	<script>
	// initialize interface selection
	require(["winery-support-common"], function(wsc) {
		wsc.fetchSelect2DataAndInitSelect2("interface", "boundarydefinitions/interfaces/?select2", function() {
			$("#interface").on("change", function() {
				updateOperationsField();
			});
			if ($("#interface").select2("val") != "") {
				updateOperationsField();
			}
		}, true);
	});

	function getInterfacesAndInterfaceURLs() {
		var iface = $("#interface").select2("val");
		iface = encodeID(iface);

		var ifacesURL = "boundarydefinitions/interfaces/";
		var ifaceURL = ifacesURL + iface + "/";
		return {
			ifacesURL: ifacesURL,
			ifaceURL: ifaceURL
		}
	}

	function getOperationsAndOperationURLs() {
		var ifaceURL = getInterfacesAndInterfaceURLs().ifaceURL;
		var operation = $("#operation").select2("val");
		operation = encodeID(operation);

		var operationsURL = ifaceURL + "exportedoperations/";
		var operationURL = operationsURL + operation + "/";
		return {
			operationsURL: operationsURL,
			operationURL: operationURL
		}
	}

	/**
	 * Updates the field of the exported operation
	 */
	function updateOperationsField() {
		var iface = $("#interface").select2("val");
		var urls = getInterfacesAndInterfaceURLs();

		$.ajax({
			url: urls.ifaceURL,
			type: "HEAD",
			async: false,
			error: function(jqXHR, textStatus, errorThrown) {
				if (jqXHR.status == 404) {
					// everything allright -> user entered a new interface
					$.ajax({
						url: urls.ifacesURL,
						async: false,
						type: "POST",
						data: JSON.stringify({name: iface}),
						contentType: "application/json"
					}).done(function (result) {
						vShowSuccess("Sucessfully created interface");
					}).fail(function(jqXHR, textStatus, errorThrown) {
						vShowAJAXError("Could not create interface", jqXHR, errorThrown);
					});
				} else {
					vShowAJAXError("Could not check for interface existance", jqXHR, errorThrown);
				}
			}
		});

		var url = urls.ifaceURL + "exportedoperations/?select2";
		require("winery-support-common").fetchSelect2DataAndInitSelect2("operation", url, function(){
			updateTarget();
		}, true);
	}

	$("#operation").on("change", function() {
		var operation = $("#operation").select2("val");
		var urls = getOperationsAndOperationURLs();
		$.ajax({
			url: urls.operationURL,
			type: "HEAD",
			async: false,
			error: function(jqXHR, textStatus, errorThrown) {
				if (jqXHR.status == 404) {
					// everything allright -> user entered a new operation
					$.ajax({
						url: urls.operationsURL,
						async: false,
						type: "POST",
						data: JSON.stringify({name: operation}),
						contentType: "application/json"
					}).done(function (result) {
						vShowSuccess("Sucessfully created operation");
					}).fail(function(jqXHR, textStatus, errorThrown) {
						vShowAJAXError("Could not create interface", jqXHR, errorThrown);
					});
				} else {
					vShowAJAXError("Could not check for interface operation", jqXHR, errorThrown);
				}
			}
		});

		updateTarget();
	})

	/**
	 * Updates the content at "Target": Reference and Target Interface/Operation
	 */
	function updateTarget() {
		var operation = $("#operation").select2("val");
		if (operation != "") {
			var urls = getOperationsAndOperationURLs();
			// At the beginning of the usage, there is no operation selected
			// Just fill the data if an operation is chosen
			var url = urls.operationURL;

			// store URL for later use - this avoids global JavaScript variables
			$("#operation").data("url", url);

			$.ajax({
				url: url,
				dataType: "json"
			}).done(function (data) {
				if ((data.type == "NodeOperation") || (data.type == "RelationshipOperation")) {
					if (data.type == "NodeOperation") {
						$("#nodeRadio").prop("checked", true);
						$("#nodeTemplateRefForOperation").val(data.ref);
						updateTargetInterfaceAndOperation(true, data.interfacename, data.operationname);
					} else {
						$("#relationshipRadio").prop("checked", true);
						$("#relationshipTemplateRefForOperation").val(data.ref);
						updateTargetInterfaceAndOperation(false, data.interfacename, data.operationname);
					}
				} else if (data.type == "Plan") {
					$("#planRadio").prop("checked", true);
					$("#planRefForOperation").editable("setValue", data.ref);
				} else if (data.type == null) {
					//  nothing set yet; set node as type
					putType("NodeOperation");
					$("#nodeRadio").prop("checked", true);
					// no reference is set if no type is set -> clear field
					$("#nodeTemplateRefForOperation").val("");
				} else {
					vShowError("Unexpected type '" + data.type + "'.");
					return;
				}
				adaptVisibilityOfDivsAccordingToReferenceType();
			}).fail(function(jqXHR, textStatus, errorThrown) {
				vShowAJAXError("Could not fetch data of exported operation from " + url, jqXHR, errorThrown);
			});
		}

	}

	function adaptVisibilityOfDivsAccordingToReferenceType() {
		var checked = $('input[name=reftargettype]:checked').val();
		if (checked == "node") {
			$("#selectNodeTemplateDiv").show();
			$("#selectRelationshipTemplateDiv").hide();
			$("#selectPlanDiv").hide();
			$("#selectInterfaceNameDiv").show();
			$("#selectOperationNameDiv").show();
			$("#notApplicableDiv").hide();
		} else if (checked == "relationship") {
			$("#selectNodeTemplateDiv").hide();
			$("#selectRelationshipTemplateDiv").show();
			$("#selectPlanDiv").hide();
			$("#selectInterfaceNameDiv").show();
			$("#selectOperationNameDiv").show();
			$("#notApplicableDiv").hide();
		} else if (checked == "plan") {
			$("#selectNodeTemplateDiv").hide();
			$("#selectRelationshipTemplateDiv").hide();
			$("#selectPlanDiv").show();
			$("#selectInterfaceNameDiv").hide();
			$("#selectOperationNameDiv").hide();
			$("#notApplicableDiv").show();
		} else {
			vShowError("UI in inconsistent state: wrong branch in adaptVisibilityOfDivsAccordingToReferenceType");
		}
	}
	</script>

	<div class="tab-pane" id="xml">
		<o:orioneditorarea areaid="XML" url="boundarydefinitions/" reloadAfterSuccess="true">${it.boundaryDefinitionsAsXMLStringEncoded}</o:orioneditorarea>
	</div>
</div>
</div>

<script>
$('#myTab a').click(function (e) {
	e.preventDefault();
	$(this).tab('show');
});

function openPropertyConstraintEditor() {
	vShowError("not yet implemented");
	// TODO: fill diag with the values of the table
	// $('#addPropertyConstraintDiag').modal('show');
}


var propertyConstraintsTableInfo = {
	id: '#propertyconstraintstable'
};
var requirementsTableInfo = {
		id: '#requirementstable'
};
var capabilitiesTableInfo = {
		id: '#capabilitiestable'
};
var interfacesTableInfo = {
		id: '#interfacestable'
};
var propertyMappingsTableInfo = {
		id: '#propertyMappingsTable'
	};

require(["winery-support"], function(ws) {
	var firstColumnIsHidden = {
			aoColumnDefs: [
						{ "bSearchable": false, "bVisible": false, "aTargets": [ 0 ] }
			]
		};

	ws.initTable(propertyConstraintsTableInfo, firstColumnIsHidden);
	ws.initTable(requirementsTableInfo, firstColumnIsHidden);
	ws.initTable(capabilitiesTableInfo, firstColumnIsHidden);

	ws.initTable(interfacesTableInfo);
	ws.initTable(propertyMappingsTableInfo);
});

<%-- === BEGIN: Property Constraints === --%>

function openUpdatePropertyConstraintDiag() {
	if (propertyConstraintsTableInfo.selectedRow) {
		require(["winery-support"], function(ws) {
			if (ws.isEmptyTable(propertyConstraintsTableInfo)) {
				vShowError("No property constraints available");
				return;
			}

			$("#addPropertyConstraint").hide();
			$("#updatePropertyConstraint").show();

			// put value in fields
			var children = $(propertyMappingsTableInfo.selectedRow).children("td");
			$("#serviceTemplatePropertyRefForConstraint").val($(children[1]).text());
			$("#constraintType").val($(children[2]).text());
			// TODO: value of the constraint

			$('#propertyConstraintDiag').modal('show');

			vShowError("Not yet implemented.");
		});
	} else {
		vShowError("No property constraint selected");
	}
}

function openAddPropertyConstraintDiag() {
	$("#addPropertyConstraint").show();
	$("#updatePropertyConstraint").hide();

	// reset all fields
	$("#serviceTemplatePropertyRefForConstraint").val("");
	// TODO: value of the constraint

	$('#propertyConstraintDiag').modal('show');

	vShowError("Not yet implemented.");
}

<%-- === END: Property Constraints === --%>


<%-- === BEGIN: Property Mapping === --%>

function openUpdatePropertyMappingDiag() {
	if (propertyMappingsTableInfo.selectedRow) {
		require(["winery-support"], function(ws) {
			if (ws.isEmptyTable(propertyMappingsTableInfo)) {
				vShowError("No property mappings available");
				return;
			}

			$("#addPropertyMapping").hide();
			$("#updatePropertyMapping").show();

			// put value in fields
			var children = $(propertyMappingsTableInfo.selectedRow).children("td");
			$("#serviceTemplatePropertyRef").val($(children[0]).text());
			$("#targetObjectRef").val($(children[1]).text());
			$("#targetPropertyRef").val($(children[2]).text());

			$('#propertyMappingDiag').modal('show');
		});
	} else {
		vShowError("No property mapping selected");
	}
}

function openAddPropertyMappingDiag() {
	$("#addPropertyMapping").show();
	$("#updatePropertyMapping").hide();

	// reset all fields
	$("#serviceTemplatePropertyRef").val("");
	$("#targetObjectRef").val("");
	$("#targetPropertyRef").val("");

	$('#propertyMappingDiag').modal('show');
}

function addOrUpdatePropertyMapping(add) {
	var serviceTemplatePropertyRef = $("#serviceTemplatePropertyRef").val();
	var targetObjectRef = $("#targetObjectRef").val();
	var targetPropertyRef = $("#targetPropertyRef").val();

	$.ajax({
		url: "boundarydefinitions/propertymappings",
		type: "POST",
		async: false,
		data: {
			serviceTemplatePropertyRef: serviceTemplatePropertyRef,
			targetObjectRef: targetObjectRef,
			targetPropertyRef: targetPropertyRef
		},
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not POST property mapping", jqXHR, errorThrown);
		},
		success: function(data, textSTatus, jqXHR) {
			// put value from fields into table
			if (add) {
				propertyMappingsTableInfo.table.fnAddData([serviceTemplatePropertyRef, targetObjectRef, targetPropertyRef]);
			} else {
				var children = $(propertyMappingsTableInfo.selectedRow).children("td");
				$(children[0]).text(serviceTemplatePropertyRef);
				$(children[1]).text(targetObjectRef);
				$(children[2]).text(targetPropertyRef);
			}

			$('#propertyMappingDiag').modal('hide');

			vShowSuccess("Successfully posted property mapping.");
		}
	});
}

function addPropertyMapping() {
	addOrUpdatePropertyMapping(true);
}

function updatePropertyMapping() {
	addOrUpdatePropertyMapping(false);
}

<%-- === END: Property Mapping === --%>


//window.addEventListener('message', function(event){var v = document.getElementById('%s');if(v){v=v.firstChild; if(v && v.contentWindow === event.source){%s}}})
window.addEventListener('message', function(event) {
	if (event.data) {
		if (event.data.targetObjectRef) {
			// current API expects both fields
			$(".newObjectRef").val(event.data.targetObjectRef);
			$("#newObjectPropertyRef").val(event.data.targetPropertyRef);
		} else if (event.data.reqRef) {
			$("#ReqReferenceField").val(event.data.reqRef);
		} else if (event.data.capRef) {
			$("#CapReferenceField").val(event.data.capRef);
		} else if (event.data.targetRelationshipTemplateRef) {
			$("#RelationshipTemplateReferenceField").val(event.data.targetRelationshipTemplateRef);
		}
	}
});

function storeNewReference(ref) {
	var url = $("#operation").data("url") + "/ref";
	$.ajax({
		url: url,
		data: ref,
		type: "PUT",
		contentType: "text/plain",
	}).done(function (result) {
		vShowSuccess("Sucessfully saved reference");
	}).fail(function(jqXHR, textStatus, errorThrown) {
		vShowAJAXError("Could not set reference", jqXHR, errorThrown);
	});
}

<c:if test="${not empty it.listOfAllPlans}">
$("#planRefForOperation").editable({
	type: "select",
	source: [
			<c:forEach var="item" items="${it.listOfAllPlans}">
			{value: "${item.id}",
			  text: "${item.text}"},
			</c:forEach>
		]
}).on("save", function(e, params) {
	var newRef = params.newValue;
	storeNewReference(newRef)
});
</c:if>

function putType(type) {
	var url = $("#operation").data("url") + "/type";
	$.ajax({
		url: url,
		data: type,
		type: "PUT",
		contentType: "text/plain",
	}).done(function (result) {
		vShowSuccess("Sucessfully changed reference type");
		adaptVisibilityOfDivsAccordingToReferenceType();
	}).fail(function(jqXHR, textStatus, errorThrown) {
		vShowAJAXError("Could not set new reference type", jqXHR, errorThrown);
	});
}

function getInterfaceDataFromURL(ifaceURL) {
	var select2data = null;
	$.ajax({
		url: ifaceURL + "?select2",
		async: false,
		type: "GET",
		dataType: "json",
		success: function (data) {
			select2data = data;
		},
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not get interface data", jqXHR, errorThrown);
		}
	});
	return select2data;
}

function getDataForUpdateTargetOperation(url, showError) {
	var select2data = null;
	$.ajax({
		url: url + "?select2",
		async: false,
		type: "GET",
		dataType: "json",
		success: function (data) {
			select2data = data;
		},
		error: function(jqXHR, textStatus, errorThrown) {
			if (showError) {
				vShowAJAXError("Could not get operation data", jqXHR, errorThrown);
			}
		}
	});
	return select2data;
}

function updateTargetOperation(operationNameToSelect) {
	var ifaceURL = $("#TargetInterface").data("url2");
	var select2data = null;
	var operationsURL;

	// we don't store the interface-URL at #TargetInterface, but do a quick hack here
	if (ifaceURL != null) {
		operationsURL = ifaceURL + "/" + encodeID($("#TargetInterface").val()) + "/operations/";
		select2data = getDataForUpdateTargetOperation(operationsURL, false);
	}

	// either only one URL or nothing found at second URL --> try first URL
	if (select2data == null) {
		ifaceURL = $("#TargetInterface").data("url1");
		operationsURL = ifaceURL + "/" + encodeID($("#TargetInterface").val()) + "/operations/";
		select2data = getDataForUpdateTargetOperation(operationsURL, true);
	}

	if (select2data != null) {
		$("#TargetOperation").select2({
			data:select2data
		});
		var valueToSelect = null;
		if ((typeof operationNameToSelect !== "undefined") && (operationNameToSelect != null)) {
			valueToSelect = operationNameToSelect;
		} else {
			// called without existing data --> insert pseudo data and store it at the backend to ensure consistency
			if (select2data.length > 0) {
				valueToSelect = select2data[0].id;
				storeTargetOperation(valueToSelect);
			}
		}
		if (valueToSelect != null) {
			$("#TargetOperation").select2('val', valueToSelect);
		}
	}

}

function updateTargetInterfaceAndOperationFromInterfaceURL(ifaceURL, interfaceNameToSelect, operationNameToSelect) {
	var select2data;
	if (typeof ifaceURL === "string") {
		select2data = getInterfaceDataFromURL(ifaceURL);
		$("#TargetInterface").data("url1", ifaceURL);
		$("#TargetInterface").data("url2", null);
	} else {
		// TODO: instead of hacking with two urls (especially at other places in the code), we should prefix the entries with S| at the source interface and T| at the target interface
		select2data = getInterfaceDataFromURL(ifaceURL[0]);
		var select2data2 =  getInterfaceDataFromURL(ifaceURL[1]);
		$.merge(select2data, select2data2);
		$("#TargetInterface").data("url1", ifaceURL[0]);
		$("#TargetInterface").data("url2", ifaceURL[1]);
	}
	$("#TargetInterface").select2({data:select2data});

	var valueToSelect = null;
	if ((typeof interfaceNameToSelect !== "undefined") && (interfaceNameToSelect != null)) {
		valueToSelect = interfaceNameToSelect;
	} else {
		// called without existing data --> insert pseudo data and store it at the backend to ensure consistency
		if (select2data.length > 0) {
			valueToSelect = select2data[0].id;
			storeTargetInterface(valueToSelect);
		}
	}
	if (valueToSelect != null) {
		$("#TargetInterface").select2('val', valueToSelect);
	}

	updateTargetOperation(operationNameToSelect);
}

function updateTargetInterfaceAndOperation(isNodeTemplate, interfaceNameToSelect, operationNameToSelect) {
	var url = "topologytemplate/";
	url = url + (isNodeTemplate ? "nodetemplates" : "relationshiptemplates" + "/");
	var templateId = isNodeTemplate ? $("#nodeTemplateRefForOperation").val() : $("#relationshipTemplateRefForOperation").val();
	if (templateId == "") {
		// if no template is provided, we just return
		return;
	}
	url = url + "/" + templateId + "/type";
	$.ajax({
		url: url,
		type: "GET",
		dataType: "text"
	}).done(function (typeQName) {
		require(["winery-support-common"], function(wsc) {
			var urlDetermination = isNodeTemplate ? wsc.makeNodeTypeURLFromQName : wsc.makeRelationshipTypeURLFromQName;
			var typeURL = urlDetermination("${it.repositoryURL}", typeQName)
			if (isNodeTemplate) {
				updateTargetInterfaceAndOperationFromInterfaceURL(typeURL + "/interfaces", interfaceNameToSelect, operationNameToSelect);
			} else {
				updateTargetInterfaceAndOperationFromInterfaceURL([typeURL + "/sourceinterfaces/", typeURL + "/targetinterfaces/"], interfaceNameToSelect, operationNameToSelect);
			}
		});
	}).fail(function(jqXHR, textStatus, errorThrown) {
		vShowAJAXError("Could not get type of node / relationship", jqXHR, errorThrown);
	});

}

$("#nodeRadio").on("change", function() {
	putType("NodeOperation");
	updateTargetInterfaceAndOperation(true);
});
$("#relationshipRadio").on("change", function() {
	putType("RelationshipOperation");
	updateTargetInterfaceAndOperation(false);
});
$("#planRadio").on("change", function() {
	putType("Plan");
});

$("#nodeTemplateRefForOperation").on("change", function() {
	updateTargetInterfaceAndOperation(true);
});

$("#relationshipTemplateRefForOperation").on("change", function() {
	updateTargetInterfaceAndOperation(false);
});

function storeTargetInterface(val) {
	val = val || $("#TargetInterface").select2("val");
	var url = $("#operation").data("url") + "interfacename";
	 // when selecting a new operation, both targetinterface and targetoperation are updated; this leads to race conditions at the backend and one chang would be lost. Therefore, we do the calls synchronously
	$.ajax({
		url: url,
		data: val,
		type: "PUT",
		contentType: "text/plain",
		async:false
	}).done(function (result) {
		vShowSuccess("Sucessfully saved interface name");
	}).fail(function(jqXHR, textStatus, errorThrown) {
		vShowAJAXError("Could not set interface name", jqXHR, errorThrown);
	});
}

function storeTargetOperation(val) {
	val = val || $("#TargetOperation").select2("val");
	var url = $("#operation").data("url") + "operationname";
	// when selecting a new operation, both targetinterface and targetoperation are updated; this leads to race conditions at the backend and one chang would be lost. Therefore, we do the calls synchronously
	$.ajax({
		url: url,
		data: val,
		type: "PUT",
		contentType: "text/plain",
		async:false
	}).done(function (result) {
		vShowSuccess("Sucessfully saved operation name");
	}).fail(function(jqXHR, textStatus, errorThrown) {
		vShowAJAXError("Could not set operation name", jqXHR, errorThrown);
	});
}

$("#TargetInterface").on("change", function() {
	storeTargetInterface();
	updateTargetOperation();
});

$("#TargetOperation").on("change", function() {
	storeTargetOperation();
});

function deleteCurrentlySelectedInterface() {
	vConfirmYesNo("Do you really want to delete this interface?", function() {
		var url = getInterfacesAndInterfaceURLs().ifaceURL;
		$.ajax({
			url: url,
			type: "DELETE"
		}).done(function () {
			require(["winery-support-common"], function (wsc) {
				wsc.removeItemFromSelect2Field($("#interface"), $("#interface").select2("data").id);
				vShowSuccess("Successfully deleted interface");
			});
		}).fail(function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not delete interface", jqXHR, errorThrown);
		});
	});
}

function deleteCurrentlySelectedOperation() {
	vConfirmYesNo("Do you really want to delete this operation?", function() {
		var url = getOperationsAndOperationURLs().operationURL;
		$.ajax({
			url: url,
			type: "DELETE"
		}).done(function () {
			require(["winery-support-common"], function (wsc) {
				wsc.removeItemFromSelect2Field($("#operation"), $("#operation").select2("data").id);
				vShowSuccess("Successfully deleted operation");
			});
		}).fail(function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not delete operation", jqXHR, errorThrown);
		});
	});
}

</script>

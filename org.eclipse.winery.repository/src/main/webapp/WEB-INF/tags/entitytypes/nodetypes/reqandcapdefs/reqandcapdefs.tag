<%--
/*******************************************************************************
 * Copyright (c) 2012-2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Yves Schubert - switch to bootstrap 3, integration with spinnerwithinphty
 *******************************************************************************/
--%>
<%@tag description="Models Requirement and Capability Definitions" pageEncoding="UTF-8"%>

<%@attribute name="labelForSingleItem" required="true" %>
<%@attribute name="url" required="true"%>
<%@attribute name="allSubResources" required="true" type="java.util.List" description="All available req-/cap-defs" %>
<%@attribute name="allTypes" required="true" type="java.util.Collection" description="All available types of req-/cap-def" %>
<%@attribute name="typeClass" required="true" type="java.lang.Class" description="The class of the type" %>

<%@taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct"  tagdir="/WEB-INF/tags/common" %>
<%@taglib prefix="con" tagdir="/WEB-INF/tags/constraints" %>
<%@taglib prefix="o"   tagdir="/WEB-INF/tags/common/orioneditor"%>
<%@taglib prefix="t"   tagdir="/WEB-INF/tags" %>
<%@taglib prefix="w"   uri="http://www.eclipse.org/winery/repository/functions"%>
<%@taglib prefix="wc"  uri="http://www.eclipse.org/winery/functions"%>

<button class="rightbutton btn btn-danger btn-xs" type="button" onclick="deleteOnServerAndInTable(reqCapTableInfo, '${labelForSingleItem}', '${url}');">Remove</button>
<button class="rightbutton btn btn-primary btn-xs" type="button" onclick="$('#addReqOrCapDefDiag').modal('show');">Add</button>

<div class="modal fade" id="addReqOrCapDefDiag">
<div class="modal-dialog">
<div class="modal-content" style="width:660px;">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h4 class="modal-title">Add ${labelForSingleItem}</h4>
	</div>
	<div class="modal-body">
		<form id="addReqOrCapDefForm" enctype="multipart/form-data"><fieldset>
			<div class="form-group">
				<label for="reqorcapname">Name</label>
				<input class="form-control" name="name" id="reqorcapname" type="text" required="required" />
			</div>
			<ct:QNameChooser allQNames="${allTypes}" idOfSelectField="type" labelOfSelectField="Type" />
			<ct:spinnerwithinphty label="Lower Bound" id="lowerbound" min="0" value="1" />
			<ct:spinnerwithinphty label="Upper Bound" id="upperbound" min="1" value="1" withinphty="true" />
		</fieldset></form>
	</div>
	<div class="modal-footer">
		<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
		<button type="button" class="btn btn-primary" onclick="createReqOrCapDef();">Add</button>
	</div>
</div>
</div>
</div>

<table id="reqorcapdefs">

<thead>
	<tr>
		<th>name</th>
		<th>type</th>
		<th>lower bound</th>
		<th>upper bound</th>
		<th>constraints</th>
	</tr>
</thead>

<tbody>

<c:forEach var="r" items="${allSubResources}">
	<tr>
		<td>${r.def.name}</td>
		<td>${wc:qname2href(pageContext.request.contextPath, typeClass, r.type)}</td>
		<td>${w:renderMinInstances(r.def.lowerBound)}</td>
		<td>${w:renderMaxInstances(r.def.upperBound)}</td>
		<td><button class="btn btn-xs" onclick="editConstraints('${r.def.name}');">Constraints...</button></td>
	</tr>
</c:forEach>

</tbody>

</table>

<%-- Editing a set of constraints --%>

<div class="modal fade" id="constraints-dialog">
<div class="modal-dialog">
<div class="modal-content">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h4 class="modal-title">Constraints</h4>
	</div>
	<div class="modal-body">
		<div id="noconstraintsexisting" style="display:none;">No constraints defined</div>
		<ol id="constraintlist" style="display:none;">
		</ol>
	</div>
	<div class="modal-footer">
		<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		<button type="button" class="btn btn-primary" onclick="editConstraint();">Add new</button>
	</div>
</div>
</div>
</div>

<script>
function deleteConstraint(id) {
	$.ajax({
		url: "${url}" + currentReqCapName + "/constraints/" + id,
		method: "DELETE",
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not delete constraint", jqXHR, errorThrown);
		},
		success: function(data, textStatus, jqXHR) {
			$("#"+id).remove();

			// quick hack
			var constraintListIsEmpty = $("#constraintlist").children().length == 0;
			if (constraintListIsEmpty) {
				// last item was removed -> show empty message
				$("#noconstraintsexisting").show();
				$("#constraintlist").hide();
			}
		}
	});
}

function getLiForConstraint(id) {
	return '<li id="' + id + '"><span onclick="editConstraint(\'' + id + '\');">constraint</span> <button class="btn btn-danger btn-xs" style="margin-left:10px;" onclick="deleteConstraint(\'' + id + '\')">Delete</button></li>';
}
/**
 * @param id the id of the constraint
 */
function addConstraintToList(id) {
	var constraintList = $("#constraintlist");

	var li = getLiForConstraint(id);
	constraintList.append(li);

	// ensure that "no constraints existing" is hidden and the list is shown
	$("#noconstraintsexisting").hide();
	constraintList.show();
}

var currentReqCapName;

function editConstraints(reqCapName) {
	currentReqCapName = reqCapName;
	$.ajax({
		url: "${url}" + currentReqCapName + "/constraints/",
		dataType: "json",
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not fetch constraints data", jqXHR, errorThrown);
		},
		success: function(data, textStatus, jqXHR) {
			if (data.length == 0) {
				$("#noconstraintsexisting").show();
				$("#constraintlist").hide();
			} else {
				$("#noconstraintsexisting").hide();
				var constraintList = $("#constraintlist");
				constraintList.empty();
				$(data).each(function(i,id) {
					addConstraintToList(id);
				});
				$("#constraintlist").show();
			};
			$("#constraints-dialog").modal("show");
		}
	});
}
</script>


<%-- Editing a single constraint --%>

<con:constraint />

<script>
var currentConstraintId;

/**
 * @param constraintId (optional) If not given, enable editing of a newly created constraint
 */
function editConstraint(constraintId) {
	currentConstraintId = constraintId;

	// Adjust Create/Update button
	if (currentConstraintId === undefined) {
		$("#createConstraintBtn").show();
		$("#updateConstraintBtn").hide();

		$("#createConstraintBtn").off("click");
		$("#createConstraintBtn").on("click", function() {
			getXMLOfConstraint(function(xmlString) {
				$.ajax({
					type: "POST",
					url: "${url}" + currentReqCapName + "/constraints/",
					contentType: "text/xml",
					async: true,
					data: xmlString,
					dataType: "text",
					error: function(jqXHR, textStatus, errorThrown) {
						vShowAJAXError("Could not add constraint", jqXHR, errorThrown);
					},
					success: function(resData, textStatus, jqXHR) {
						addConstraintToList(resData);
						$("#constraint-dialog").modal("hide");
					}
				});
			});
		});
	} else {
		// updating an existing constraint
		$("#createConstraintBtn").hide();
		$("#updateConstraintBtn").show();

		$("#updateConstraintBtn").off("click");
		$("#updateConstraintBtn").on("click", function() {
			getXMLOfConstraint(function(xmlString) {
				$.ajax({
					type: "PUT",
					url: "${url}" + currentReqCapName + "/constraints/" + currentConstraintId + "/",
					contentType: "text/xml",
					async: true,
					data: xmlString,
					dataType: "text",
					error: function(jqXHR, textStatus, errorThrown) {
						vShowAJAXError("Could not update constraint", jqXHR, errorThrown);
					},
					success: function(newId, textStatus, jqXHR) {
						$("#constraint-dialog").modal("hide");
						var newLi = getLiForConstraint(newId);
						var oldLi = $("#" + currentConstraintId);
						oldLi.before(newLi);
						oldLi.remove();
					}
				});
			});
		});
	};

	// fill textarea
	if (currentConstraintId === undefined) {
		$("#constraint-dialog").modal("show");
		// setting content only works if dialog is fully shown
		window.setTimeout(function() {
			window.winery.orionareas["constrainttextarea"].editor.setText($("#emptyconstraint").val());
		}, window.winery.BOOTSTRAP_ANIMATION_DURATION);
	} else {
		$.ajax({
			type: "GET",
			url: "${url}" + currentReqCapName + "/constraints/" + currentConstraintId + "/",
			dataType: "xml",
			async: true,
			error: function(jqXHR, textStatus, errorThrown) {
				vShowAJAXError("Could get constraint information", jqXHR, errorThrown);
			},
			success: function(xmlData, textStatus, jqXHR) {
				// xmlDoc contains an XML document and not just a string
				// We let jQuery parse the XML as we need to parse the type attribute

				// *move* type information to input field
				var type = xmlData.documentElement.getAttribute("constraintType");
				$("#typenameinput").val(type);
				xmlData.documentElement.removeAttribute("constraintType");

				$("#constraint-dialog").modal("show");

				// the XML document cannot be put directly as content. It has to be converted to a String
				// TODO: add nice formatting
				var xmlString = (new XMLSerializer()).serializeToString(xmlData);
				window.setTimeout(function() {
					window.winery.orionareas["constrainttextarea"].editor.setText(xmlString);
				}, window.winery.BOOTSTRAP_ANIMATION_DURATION);
			}
		});
	}
}
</script>


<script>
// TODO: this variable is available after switching tabs.
// One could cache this information without requiring reloading all the content
var reqCapTableInfo = {
	id : '#reqorcapdefs'
};

require(["winery-support"], function(ws) {
	ws.initTable(reqCapTableInfo);
});

function createReqOrCapDef() {
	if (highlightRequiredFields()) {
		vShowError("Please fill out all required fields.");
		return;
	}

	var data = $('#addReqOrCapDefForm').serialize();

	// replace &inphty; by TOSCA's "unbounded"
	data = data.replace("∞", "unbounded");
	// %E2%88%9E is the HTML encoding of &inphty;
	data = data.replace("%E2%88%9E", "unbounded");

	$.ajax({
		url: "${url}",
		type: "POST",
		async: false,
		data: data,
		error: function(jqXHR, textStatus, errorThrown) {
			vShowError("Could not add ${labelForSingleItem}: " + errorThrown + "<br/>" + jqXHR.responseText);
		},
		success: function(data, textStatus, jqXHR) {
			// Data has been validated at the server
			// We can just add the local data
			var name = $('#reqorcapname').val();
			var type = $('#type').select2("data").text; // TODO: make href to be consistent with other lines
			var lbound = $('#lowerbound').val();
			var ubound = $('#upperbound').val();
			var constraints = "<button class=\"btn btn-xs\" onclick=\"editConstraints('" + name + "');\">Constraints...</button>";
			var dataToAdd = [name, type, lbound, ubound, constraints];
			reqCapTableInfo.table.fnAddData(dataToAdd);
			$('#addReqOrCapDefDiag').modal('hide');
		}
	});
}

</script>

<%--
/*******************************************************************************
 * Copyright (c) 2012-2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Niko Stadelmaier - removal of select2 library
 *******************************************************************************/
--%>
<%@tag description="Dialog to change a req or cap. Offers function showEditDiagFor${shortName}(id)" pageEncoding="UTF-8"%>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="nt" tagdir="/WEB-INF/tags/common/templates/nodetemplates" %>
<%@taglib prefix="o"  tagdir="/WEB-INF/tags/common/orioneditor"%>
<%@taglib prefix="w"  tagdir="/WEB-INF/tags"%>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions"%>

<%@attribute name="headerLabel" required="true"%>
<%@attribute name="shortName" required="true" description="Used for diag id, function name suffix, Req|Cap"%>
<%@attribute name="requirementOrCapability" required="true" description="requirement|capability"%>
<%@attribute name="cssClassPrefix" required="true"%>
<%@attribute name="allTypes" required="true" type="java.util.Collection" description="Collection&lt;QName&gt; of all available types" %>
<%@attribute name="clazz" required="true" type="java.lang.Class" description="TRequirement.class or TCapability.class" %>
<%@attribute name="repositoryURL" required="true" %>

<div class="modal fade" id="AddOrUpdate${shortName}Diag">
	<div class="modal-dialog">
		<div class="modal-content" style="width:660px;">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title"><span id="headerAddOrUpdate"></span> ${headerLabel}</h4>
			</div>
			<div class="modal-body">
				<form id="add${shortName}Form" enctype="multipart/form-data">
					<fieldset>
						<w:idInput inputFieldId="${shortName}Id"/>

						<div class="form-group">
							<label for="${shortName}NameChooser" class="control-label">Definition Name:</label>
							<select  id="${shortName}NameChooser" class="form-control" type="text" required="required"> </select>
						</div>

						<div class="form-group">
							<label for="${shortName}TypeDisplay" class="control-label">${shortName} Type:</label>
							<input  id="${shortName}TypeDisplay" class="form-control" type="text" required="required" disabled="disabled"/>
						</div>

						<div id="${shortName}PropertiesContainer">
						</div>
					</fieldset>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<button type="button" id="add${shortName}btn" class="btn btn-primary" onclick="addOrUpdate${shortName}(false);">Add</button>
				<button type="button" id="delete${shortName}btn" class="btn btn-danger" onclick="deleteCurrent${shortName}();">Delete</button>
				<button type="button" id="update${shortName}btn" class="btn btn-primary" onclick="addOrUpdate${shortName}(true);">Change</button>
			</div>
		</div>
	</div>
</div>

<script>

/*
 * The following variables are declared twice due to double inclusion of this .tag flie
 * Due to JavaScript magic, it works nevertheless
 */
var selectedNodeTemplateForReqCapAddition;

//global variable set by editPropertiesXML and read by save${shortName}Edits
var nodeTemplateEditedReqOrCap;


function deleteCurrent${shortName}() {
	nodeTemplateEditedReqOrCap.remove();
	$("#AddOrUpdate${shortName}Diag").modal("hide");
}

function update${shortName}PropertiesContainerWithClone(propertiesContainerToClone) {
	var clone = propertiesContainerToClone.clone();
	$("#${shortName}PropertiesContainer").empty().append(clone);
	clone.find(".KVPropertyValue").editable({mode: "inline"});
}

function update${shortName}PropertiesFromSelectedType() {
	var data = $("#${shortName}NameChooser :selected");
	var name = data.text();
	var type = data.val();

	// fill in type
	// TODO: use qname2href and store QName in data-qname for later consumption -- possibly qname2href should always store the qname in data-qname
	$("#${shortName}TypeDisplay").val(type);

	// fill in properties (derived from type)
	var propertiesContainer= $(".skelettonPropertyEditorFor${shortName} > span").filter(function(){
	    return $(this).text() === "" + type;
	}).parent().children("div");
	update${shortName}PropertiesContainerWithClone(propertiesContainer);
}

$("#${shortName}NameChooser").on("change", function(e) {
	update${shortName}PropertiesFromSelectedType();
});

/**
 * Called when a req/cap should be added or updated
 * Update mode is triggered if reqOrCapIdtoUpdate is given
 *
 * @param nodeTemplateId the node template id to add a req/cap to. undefined in update mode
 * @param reqOrCapIdtoUpdate
 */
function showAddOrUpdateDiagFor${shortName}(nodeTemplateId, reqOrCapIdToUpdate) {
	var update = (typeof reqOrCapIdToUpdate !== "undefined");

	if (update) {
		nodeTemplateEditedReqOrCap = $("#" + reqOrCapIdToUpdate);
		// in update mode, nodeTemplateId is not provided, we have to search for the right shape
		selectedNodeTemplateForReqCapAddition = nodeTemplateEditedReqOrCap.closest(".NodeTemplateShape");
	} else {
		selectedNodeTemplateForReqCapAddition = $("#" + nodeTemplateId);
	}

	require(["winery-support-common"], function(wsc) {
		var typeQName = selectedNodeTemplateForReqCapAddition.children("div.headerContainer").children("span.typeQName").text();
		var urlFragment = wsc.getURLFragmentOutOfFullQName(typeQName);
		var url = "${repositoryURL}/nodetypes/" + urlFragment + "/${requirementOrCapability}definitions/";
		$.ajax({
			url: url,
			dataType: "json"
		}).fail(function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not fetch ${requirementOrCapability} definitions", jqXHR, errorThrown);
		}).done(function(data) {
			// now, we have all available requirement definitions
			// we have to ask each of it for the type
			// we use the type as key for the option and the name as displayed text
			// select2 perfectly handles duplicate keys

			var select2Data = [];

			$.each(data, function(i,e) {
				var rqDefURL = url + e.id + "/type";
				$.ajax({
					url: rqDefURL,
					async: false,
					dataType: "text"
				}).fail(function(jqXHR, textStatus, errorThrown) {
					vShowAJAXError("Could not fetch type for " + e.id, jqXHR, errorThrown);
				}).done(function(data) {
					var item = {
						id: data,
						text: e.id
					};
					select2Data.push(item);
				});
			});
			$("#${shortName}NameChooser").empty();
			<%--$("#${shortName}NameChooser").append("<option selected disabled>Select Name</option>");--%>
			$.each(select2Data, function(index, element){
				$("#${shortName}NameChooser").append("<option value='" + element.id + "'>" + element.text + "</option>")
			});

			if (update) {
				$("#add${shortName}btn").hide();
				$("#update${shortName}btn").show();
				$("#delete${shortName}btn").show();
				$("#headerAddOrUpdate").text("Change");

				// collect existing data in variables
				var id = nodeTemplateEditedReqOrCap.children(".id").text();
				var name = nodeTemplateEditedReqOrCap.children(".name").text();
				var type = nodeTemplateEditedReqOrCap.children(".type").children("a").data("qname");
				var propertiesContainer = nodeTemplateEditedReqOrCap.children(".propertiesContainer");
				// update displays

				// id
				$("#${shortName}Id").val(id);

				// name
				// we use the type as key at NameChooser. We hope that there are no duplicates. Otherwise, update won't work.
				$("#${shortName}NameChooser").val(type);
				// make consistency check
				var data = {id: $("#${shortName}NameChooser :selected").val(), text: $("#${shortName}NameChooser :selected").text()};
				if (data == null) {
					vShowError("type " + type + " could not be selected.")
				} else if (name != (data.text)) {
					vShowError("There are two names for different types. That case is not handled in the UI.");
				}

				// type
				$("#${shortName}TypeDisplay").val(type);

				// properties
				update${shortName}PropertiesContainerWithClone(propertiesContainer);
			} else {
				$("#add${shortName}btn").show();
				$("#update${shortName}btn").hide();
				$("#delete${shortName}btn").hide();
				$("#headerAddOrUpdate").text("Add");

				// QUICK HACK if dialog has been shown before -> show properties of selected type
				if ($("#${shortName}NameChooser :selected").val() != []) {
					<%--update${shortName}PropertiesFromSelectedType();--%>
				}
			}

			$("#AddOrUpdate${shortName}Diag").modal("show");
		});
	});
}

/**
 * Called at click on button "Add" or "Change"
 */
function addOrUpdate${shortName}(update) {
	if (highlightRequiredFields()) {
		vShowError("Please fill in all required fields");
		return;
	}
	require(["tmpl"], function(tmpl) {
		// Generate skeletton div
		var sel2data = { id: $("#${shortName}NameChooser :selected").val(), text: $("#${shortName}NameChooser :selected").text()};
		var data = {
			id: $("#${shortName}Id").val(),
			name: sel2data.text,
			type: sel2data.id
		};
		// tmpl-${shortName} is defined in reqsorcaps.tag
		var div = tmpl("tmpl-${shortName}", data);

		// Add the div to the node template
		if (update) {
			nodeTemplateEditedReqOrCap.replaceWith(div);
		} else {
			selectedNodeTemplateForReqCapAddition.children(".${cssClassPrefix}Container").children(".content").children(".addnewreqorcap").before(div);
		}

		// Put properties at the right place
		$("#toBeReplacedByProperties").replaceWith($("#${shortName}PropertiesContainer").children());

		$("#AddOrUpdate${shortName}Diag").modal("hide");
	});
}

</script>

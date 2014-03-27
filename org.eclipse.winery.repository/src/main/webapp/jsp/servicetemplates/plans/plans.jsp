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
 *    Yves Schubert - switch to bootstrap 3
 *******************************************************************************/
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" tagdir="/WEB-INF/tags/parameters" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<script>

var embeddedPlansTableInfo = {
	id: '#embeddedPlansTable'
};

var linkedPlansTableInfo = {
		id: '#linkedPlansTable'
};

$(function() {
	require(["winery-support"], function(ws) {
		ws.initTable(embeddedPlansTableInfo, {
			"aoColumns": [
							{ "bVisible": false, "bSearchable": false}, // ID column
							{ "sTitle": "Precondition" },
							{ "sTitle": "Name" },
							{ "sTitle": "Type" },
							{ "sTitle": "Language" }
						],
			"aaData" : ${it.embeddedPlansTableData}
		});

		ws.initTable(linkedPlansTableInfo, {
			"aoColumns": [
							{ "bVisible": false, "bSearchable": false}, // ID column
							{ "sTitle": "Precondition" },
							{ "sTitle": "Name" },
							{ "sTitle": "Type" },
							{ "sTitle": "Language" },
							{ "sTitle": "Reference" }
						],
			"aaData" : ${it.linkedPlansTableData}
		});
	});
});

function editIOParameters() {
	if (embeddedPlansTableInfo.selectedRow) {
		require(["winery-support"], function(ws) {
			if (ws.isEmptyTable(embeddedPlansTableInfo)) {
				vShowError("No plans available");
				return;
			}
			updateInputAndOutputParameters(getPlanURL());
			$("#editParametersDiag").modal("show");
		});
	} else {
		vShowError("No plan selected");
	}
}
	function createPlan(data) {
		if (highlightRequiredFields()) {
			vShowError("Please fill out all required fields");
			return;
		}
		data.submit();
	}

	function getPlanURL() {
		var id = embeddedPlansTableInfo.table.fnGetData(embeddedPlansTableInfo.selectedRow, 0);
		return "plans/" + encodeURIComponent(id) + "/";
	}

	function openPlanEditor() {
		if (embeddedPlansTableInfo.selectedRow) {
			var isEmptyTable = embeddedPlansTableInfo.table.children("tbody").children("tr").first().children("td").hasClass("dataTables_empty");
			if (isEmptyTable) {
				vShowError("No plans available");
				return;
			}
			window.open(getPlanURL() + "?edit", "_blank");
		} else {
			vShowError("No plan selected");
		}
	}

	function letUserChooseAPlan() {
		$('#planFileInput').trigger('click');
		$('#planChooseBtn').focus();
	}

	requirejs(["jquery.fileupload"], function(){
		$('#addPlanForm').fileupload().bind("fileuploadadd", function(e, data) {
			$.each(data.files, function (index, file) {
				$("#planFileText").val(file.name);
			});
			$("#addPlanBtn").off("click");
			$("#addPlanBtn").on("click", function() {
				createPlan(data);
			});
		}).bind("fileuploadstart", function(e) {
			$("#addPlanBtn").button("loading");
		}).bind('fileuploadfail', function(e, data) {
			vShowAJAXError("Could not add plan", data.jqXHR, data.errorThrown);
			$("#addPlanBtn").button("reset");
		}).bind('fileuploaddone', function(e, data) {
			vShowSuccess("Plan created successfully");

			// reset the add button
			$("#addPlanBtn").button("reset");
			// do not allow submission of the old files on a click if the dialog is opened another time
			$("#addPlanBtn").off("click");

			embeddedPlansTableInfo.table.fnAddData(data.result.tableData);

			$('#addPlanDiag').modal('hide');
		});
	});
</script>

<div class="modal fade" id="addPlanDiag">
	<div class="modal-dialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h4 class="modal-title">Add Plan</h4>
		</div>
		<div class="modal-body">
			<form id="addPlanForm" enctype="multipart/form-data" action="plans/" method="post">
				<div class="form-group">
					<label class="control-label">Name</label>
					<input name="planName" type="text" class="form-control" required="required">
				</div>

				<t:typeswithshortnameasselect label="Type" type="plantype" selectname="planType" typesWithShortNames="${it.planTypes}">
				</t:typeswithshortnameasselect>

				<t:typeswithshortnameasselect label="Language" type="planlanguage" selectname="planLanguage" typesWithShortNames="${it.planLanguages}">
				</t:typeswithshortnameasselect>

				<div class="form-group">
					<label class="control-label" for="planFileDiv">Archive</label>
					<div style="display: block; width: 100%" id="planFileDiv">
						<input id="planFileInput" name="file" type="file" style="display:none">
						<input name="fileText" id="planFileText" type="text" class="form-control" style="width:300px; display:inline;" onclick="letUserChooseAPlan();" required="required">
						<button type="button" id="planChooseBtn" class="btn btn-default btn-xs" onclick="letUserChooseAPlan();">Choose</button>
					</div>
				</div>
			</form>
		</div>
		<div class="modal-footer">
			<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			<button type="button" class="btn btn-primary" data-loading-text="Uploading..." id="addPlanBtn">Add</button>
		</div>
	</div>
	</div>
</div>

<p:parametersJS></p:parametersJS>

<div class="modal fade" id="editParametersDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Edit Parameters</h4>
			</div>
			<div class="modal-body">
				<p:parametersInput baseURL="getPlanURL()"></p:parametersInput>
				<p:parametersOutput baseURL="getPlanURL()"></p:parametersOutput>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>

	<div id="managementPlans">
		<h4>Embedded Plans</h4>
		<button class="rightbutton btn btn-xs btn-danger" onclick="deleteOnServerAndInTable(embeddedPlansTableInfo, 'Plan', 'plans/', 0, 2);">Remove</button>
		<button class="rightbutton btn btn-xs btn-info" onclick="$('#addPlanDiag').modal('show');">Add</button>
		<button class="rightbutton btn btn-xs btn-default" onclick="editIOParameters();">I/O Parameters</button>
		<button class="rightbutton btn btn-xs btn-primary" onclick="openPlanEditor();">Edit</button>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="embeddedPlansTable"></table>

		<br /><br />
		<h4>Linked Plans</h4>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="linkedPlansTable"></table>
	</div>

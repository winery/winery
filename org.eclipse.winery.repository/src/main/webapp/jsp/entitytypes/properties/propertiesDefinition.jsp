<%--
/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation, maintainance
 *    Yves Schubert - switch to bootstrap 3
 *******************************************************************************/
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="ct" tagdir="/WEB-INF/tags/common"%>
<%@taglib prefix="w" uri="http://www.eclipse.org/winery/repository/functions"%>

<%-- createResource of winery-support.js could be used. However, currently selects are not supported --%>
<div class="modal fade" id="addPropertyDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Add Property</h4>
			</div>
			<div class="modal-body">
				<form id="addPropertyForm">
					<div class="form-group">
						<label class="control-label" for="propName">Name</label>
						<input name="key" class="form-control" id="propName" type="text" />
					</div>

					<div class="form-group">
						<label class="control-label" for="propType">Type</label>
						<select name="type" class="form-control" id="propType">
							<c:forEach var="t" items="${it.availablePropertyTypes}">
								<option value="${t}">${t}</option>
							</c:forEach>
						</select>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary" onclick="createProperty();">Add</button>
			</div>
		</div>
	</div>
</div>

<script>
function noneClicked() {
	disableKVproperties();
	clearXSDElementSelection();
	clearXSDTypeSelection();
	$.ajax({
		url:  "propertiesdefinition/",
		type: 'DELETE',
		async: true,
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not remove properties definition", jqXHR, errorThrown);
		}
	});
}

function clearXSDElementSelection() {
	$("#xsdelement").editable('setValue', "", true);
}

function clearXSDTypeSelection() {
	$("#xsdtype").editable('setValue', "", true);
}

$(function(){
	$("#xsdelement").editable({
		type: "select",
		url: "post/",
		pk: 1,
		source: ${w:allXSDElementDefinitionsForTypeAheadSelection()}
	});
	$("#xsdelement").on("click", function(e){
		$("#xsdelementradio").prop("checked", true);
	});

	$("#xsdtype").editable({
		type: "select",
		source: ${w:allXSDTypeDefinitionsForTypeAheadSelection()}
	});
	$("#xsdtype").on("click", function(e){
		$("#xsdtyperadio").prop("checked", true);
	});

	/* make UI more nice: enable click on label */
	$("#textnone").on("click", function(e){
		$("#nopropdef").prop("checked", true);
		noneClicked();
	});
	$("#textxmlelement").on("click", function(e){
		$("#xsdelementradio").prop("checked", true);
		disableKVproperties();
		clearXSDTypeSelection();
	});
	$("#textxmltype").on("click", function(e){
		$("#xsdtyperadio").prop("checked", true);
		disableKVproperties();
		clearXSDElementSelection();
	});
	$("#textcustomkv").on("click", function(e){
		$("#customkv").prop("checked", true);
		updateKVpropertiesVisibility();
		setKVPropertiesOnServer();
		clearXSDElementSelection();
		clearXSDTypeSelection();
	});

	$('#kvPropsTabs a').click(function (e) {
		e.preventDefault();
		$(this).tab('show');
	});

	$("#addPropertyDiag").on("shown.bs.modal", function() {
		$("#propName").focus();
	});
});
</script>

<p>
	<%-- TODO: if clicked on the "label" of the input field (i.e., the content of the input tag), the input should be selected. This is not the default at HTML5 - see http://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_input_type_radio --%>

	<input id="nopropdef" type="radio" name="kind" value="none" <c:if test="${not it.isWineryKeyValueProperties and empty it.entityType.propertiesDefinition.element and empty it.entityType.propertiesDefinition.type}">checked="checked"</c:if>><span class="cursorpointer" id="textnone">(none)</span></input>
	<br/>

	<input id="xsdelementradio" type="radio" name="kind" value="element" <c:if test="${not empty it.entityType.propertiesDefinition.element}">checked="checked"</c:if>><span class="cursorpointer" id="textxmlelement">XML element</span></input>
	<a href="#" id="xsdelement" data-url="propertiesdefinition/" data-send="always" data-title="Select XSD Element" data-value="${it.entityType.propertiesDefinition.element}"><c:if test="${not empty it.entityType.propertiesDefinition.element}">${it.entityType.propertiesDefinition.element.localPart}</c:if></a>
	<br/>

	<input id="xsdtyperadio" type="radio" name="kind" value="type" <c:if test="${not empty it.entityType.propertiesDefinition.type}">checked="checked"</c:if>><span class="cursorpointer" id="textxmltype">XML type</span></input>
	<a href="#" id="xsdtype" data-url="propertiesdefinition/" data-send="always" data-title="Select XSD Type" data-value="${it.entityType.propertiesDefinition.type}"><c:if test="${not empty it.entityType.propertiesDefinition.type}">${it.entityType.propertiesDefinition.type.localPart}</c:if></a>
	<br/>


	<input id="customkv" type="radio" name="kind" value="KV" <c:if test="${it.isWineryKeyValueProperties and not it.isWineryKeyValuePropertiesDerivedFromXSD}">checked="checked"</c:if>><span class="cursorpointer" id="textcustomkv">Custom key/value pairs</span></input>
</p>

<div id="Properties" style="display:none; margin-left:20px;">
	<ul class="nav nav-tabs" id="kvPropsTabs">
		<li class="active"><a href="#kvProps">Properties</a></li>
		<li><a href="#wrapper">Wrapping</a></li>
	</ul>

	<div class="tab-content">
		<div class="tab-pane active" id="kvProps">

			<button class="rightbutton btn btn-danger btn-xs" type="button" onclick="deleteOnServerAndInTable(propertiesTableInfo, 'Property', 'propertiesdefinition/winery/list/');">Remove</button>
			<button class="rightbutton btn btn-primary btn-xs" type="button" onclick="$('#addPropertyDiag').modal('show');">Add</button>

			<table cellpadding="0" cellspacing="0" border="0" class="display" id="propertiesTable">
				<thead>
					<tr>
						<th>Name</th>
						<th>Type</th>
					</tr>
				</thead>
				<tbody>
					<c:if test="${it.isWineryKeyValueProperties}">
						<c:forEach var="t" items="${it.propertyDefinitionKVList}">
							<tr>
								<td>${t.key}</td>
								<%-- FIXME: t.type is the short type, but we need the full type. Currently, there is no way to get the full type for a short type --%>
								<td>${t.type}</td>
							</tr>
						</c:forEach>
					</c:if>
				</tbody>
			</table>
		</div>

		<div class="tab-pane" id="wrapper">
			<form id="wrapperelementform" enctype="multipart/form-data">
				<fieldset>
					<div style="width:400px;">
						<div class="form-group">
							<label for="wrapperelement_name">Name of Wrapper Element</label>
							<a href="#" class="form-control" id="wrapperelement_name" data-url="propertiesdefinition/winery/elementname" data-send="always" data-title="Local Name" data-type="text" data-value="${it.elementName}"></a>
						</div>
						<t:namespaceChooser idOfInput="wrapperelement_ns" selected="${it.namespace}" allNamespaces="${w:allNamespaces()}"></t:namespaceChooser>
					</div>
				</fieldset>
			</form>
		</div>
	</div>
</div>

<script>
function disableKVproperties() {
	$("#Properties").hide();
}

function enableKVproperties() {
	$("#Properties").show();
}

function updateKVpropertiesVisibility() {
	if ($("input[name='kind']:checked").val() == "KV") {
		enableKVproperties();
	} else {
		disableKVproperties();
	}
}

function setKVPropertiesOnServer() {
	$.ajax({
		url: "propertiesdefinition/winery/",
		type: "POST",
		async: true,
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could change to custom key/value pairs", jqXHR, errorThrown);
		}
	});
}

$(function() {
	// put change function on all inputs to get notified of any change by the user
	$("input[name='kind']").on("change", function(e) {
		// we do not POST something to the server as only concrete values really trigger a change on server side
		var target = e.currentTarget.value;
		if (target == "none") {
			noneClicked();
		} else if (target == "element") {
			disableKVproperties();
			clearXSDTypeSelection();
		} else if (target == "type") {
			disableKVproperties();
			clearXSDElementSelection();
		} else if (target == "KV") {
			<c:if test="${not it.isWineryKeyValuePropertiesDerivedFromXSD}">
			<%-- only empty the k/v properties if not derived from XSD--%>
			setKVPropertiesOnServer();
			</c:if>
			clearXSDElementSelection();
			clearXSDTypeSelection();
			enableKVproperties();
		} else {
			vShowError("UI not consistent to code");
		}
	});

	// initialization - display the custom box to enter k/vs only if KV is selected
	updateKVpropertiesVisibility();

	$("#wrapperelement_name").editable({
		ajaxOptions: {
			type: 'put'
		},
		params: function(params) {
			// adjust params according to Winery's expectations
			delete params.pk;
			params.name = params.value;
			delete params.value;
			return params;
		}
	}).on("save", function(e, params) {
		vShowSuccess("Successfully updated local name of wrapper element");
	});

	$("#wrapperelement_ns").on("change", function(e) {
		$.ajax({
			url: "propertiesdefinition/winery/namespace",
			type: "PUT",
			async: true,
			contentType: "text/plain",
			processData: false,
			data: e.val,
			error: function(jqXHR, textStatus, errorThrown) {
				vShowAJAXError("Could not update namespace", jqXHR, errorThrown);
			},
			success: function(data, textStatus, jqXHR) {
				vShowSuccess("Successfully updated namespace");
			}
		});
	});
});

var propertiesTableInfo = {
	id: '#propertiesTable'
};

require(["winery-support"], function(ws) {
	ws.initTable(propertiesTableInfo);
});

function createProperty() {
	var data = {
		key: $("#propName").val(),
		type: $('#propType :selected').text()
	}
	$.ajax({
		url: "propertiesdefinition/winery/list/",
		type: "POST",
		async: true,
		contentType: "application/json",
		processData: false,
		data: JSON.stringify(data),
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not add property", jqXHR, errorThrown);
		},
		success: function(data, textStatus, jqXHR) {
			var name = $('#propName').val();
			var type = $('#propType :selected').text();
			var dataToAdd = [name, type];
			propertiesTableInfo.table.fnAddData(dataToAdd);
			vShowSuccess("Property successfully added");
			$('#addPropertyDiag').modal('hide');
		}
	});
}
</script>


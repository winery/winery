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
<%@tag description="Input and Output parameters handling. Used at interface/operation and plan" pageEncoding="UTF-8"%>

<%@attribute name="afterLoad" description="JavaScript code to be executed after successfully loading/initialization"%>

<script>
var inputParametersTableInfo = {
	id: '#inputparameterstab'
};

var outputParametersTableInfo = {
	id: '#outputparameterstab'
};

$(function() {
	// for options see http://datatables.net/usage/options#sDom
	var opts = {
				"sDom": '<"H"r>t<"F"ip>',
				"iDisplayLength" : 3
			};

	require(["winery-support"], function(ws) {
		ws.initTable(inputParametersTableInfo, opts);
		$(inputParametersTableInfo.id).click(function(event) {
			if (inputParametersTableInfo.selectedRow) {
				// something has been selected
				$("#removeInParBtn").removeAttr("disabled");
			} else {
				// row has been deselected
				$("#removeInParBtn").attr("disabled", "disabled");
			}
		});

		ws.initTable(outputParametersTableInfo, opts, function() {
			${afterLoad}
		});
		$(outputParametersTableInfo.id).click(function(event) {
			if (outputParametersTableInfo.selectedRow) {
				// something has been selected
				$("#removeOutParBtn").removeAttr("disabled");
			} else {
				// row has been deselected
				$("#removeOutParBtn").attr("disabled", "disabled");
			}
		});
	});

});

function afterInputParameterCreation(serializedArray, resData, textStatus, jqXHR) {
	var required;
	if (serializedArray.length == 2) {
		required = "no";
	} else {
		required = serializedArray[2].value;
	}
	addRowToParameterstable(inputParametersTableInfo, serializedArray[0].value, serializedArray[1].value, required);
}

function afterOutputParameterCreation(serializedArray, resData, textStatus, jqXHR) {
	var required;
	if (serializedArray.length == 2) {
		required = "no";
	} else {
		required = serializedArray[2].value;
	}
	addRowToParameterstable(outputParametersTableInfo, serializedArray[0].value, serializedArray[1].value, required);
}

function createInputParameter(baseURL) {
	var url = baseURL + "inputparameters/";
	createResource('Input Parameter', [
			{'label': 'Name', 'name':'name'},
			{
				label: 'Type',
				name: 'type',
				hint:'TOSCA v1.0 does not specify any type system here. The content of this field is a string. The concrete semantics is left open. The convension is to use the xsd prefix for XML Schema basic types.'
			},
			{'label':'Required', 'name':'required', 'type': 'checkbox'}
		],
		url,
		afterInputParameterCreation);
}

function createOutputParameter(baseURL) {
	var url = baseURL + "outputparameters/";
	createResource('Output Parameter', [
			{'label': 'Name', 'name':'name'},
			{
				label: 'Type',
				name: 'type',
				hint:'TOSCA v1.0 does not specify any type system here. The content of this field is a string. The concrete semantics is left open. The convension is to use the xsd prefix for XML Schema basic types.'
			},
			{'label':'Required', 'name':'required', 'type': 'checkbox'}
		],
		url,
		afterOutputParameterCreation);
}

function addRowToParameterstable(tableInfo, name, type, required) {
	var checked;
	required = required.toLowerCase();
	if ((required == "yes") || (required == "on")) {
		checked = ' checked="checked"';
	} else {
		checked = "";
	}
	var checkbox = '<input type="checkbox"' + checked + ' disabled="disabled"></input>';
	var addData = [name, type, checkbox];
	tableInfo.table.fnAddData(addData);
}

function deleteInputParameter() {
	deleteOnServerAndInTable(inputParametersTableInfo, "Input Parameter", getOperationURL() + "inputparameters/");
}

function deleteOutputParameter() {
	deleteOnServerAndInTable(outputParametersTableInfo, "Output Parameter", getOperationURL() + "outputparameters/");
}

/**
 * only called if operation is selected
 *
 * @param url: the URL to query the parameters data
 * @param inOrOut: "In"|"Out"
 */
function updateParameters(url, tableInfo, inOrOut) {
	$.ajax({
		"url": url,
		dataType: "JSON",
		success: function(data, textStatus, jqXHR) {
			tableInfo.table.fnClearTable();
			$.each(data, function(number, item) {
				addRowToParameterstable(tableInfo, item.name, item.type, item.required);
			});
			$("#add" + inOrOut + "ParBtn").removeAttr("disabled");

			// remove button should always be disalbed as it gets enabled only after clicking a row in the table
			$("#remove" + inOrOut + "ParBtn").attr("disabled", "disabled");
		}
	});
}

function updateInputAndOutputParameters(baseURL) {
	var url = baseURL + "inputparameters/";
	updateParameters(url, inputParametersTableInfo, "In");
	url = baseURL + "outputparameters/";
	updateParameters(url, outputParametersTableInfo, "Out");
}

</script>

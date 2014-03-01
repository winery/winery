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

function addResourceInstance() {
	if (highlightRequiredFields()) {
		vShowError("Please fill in all required fields");
		return;
	}

	var dataToSend = $('#createResourceForm').serialize();
	var cr = $('#createResource');
	$.ajax({
		type: "POST",
		async: false,
		"data": dataToSend,
		"url": cr.data("url"),
		dataType: "text",
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not create resource", jqXHR, errorThrown);
			cr.modal("hide");
		},
		success: function(resData, textStatus, jqXHR) {
			cr.data("onSuccess")($('#createResourceForm').serializeArray(), resData, textStatus, jqXHR);
			cr.modal('hide');
		}
	});
}

/**
 * This function creates a dialog, where the user can add key/value pairs.
 * These pairs are then sent to the given URL via POST.
 *
 * REQUIRES <script id="template-createresource" type="text/x-tmpl">
 * Currently placed in header.jsp
 *
 * @param nameOfResource the name of the resource to add
 * @param fields array of label/name/type/hint/checked values to use for the field and to pass in the AJAX call. (optional) type is in "text"/"checkbox"/... -- the values allowed for "type" attributes of <input> fields. Currently, all fields are required.
 * @param url the URL to use. The URL is uses as unique ID. If a dialog is requested to be open with a URL and the previous dialog had the same URL, the previous dialog is opened
 * @param onSuccess: function(serializedArray, data, textStatus, jqXHR) to call if adding has been successful. "serializedArray" contains the value of $('#formid').serializeArray()
 */
function createResource(nameOfResource, fields, url, onSuccess) {
	var cr = $('#createResource');
	if (cr.length == 1) {
		if (cr.data("url") == url) {
			// the same dialog has been created before. Reuse it
			cr.modal("show");
			return;
		} else {
			// remove the dialog and thus enable the creation of a new one
			cr.remove();
		}
	}

	var data  = {
		nameOfResource: nameOfResource,
		fields: fields
	};
	require(["tmpl"], function(tmpl) {
		var div = tmpl("template-createresource", data);

		$("body").append(div);
		cr = $('#createResource');
		cr.on("shown.bs.modal", function() {
			$("#createResourceForm > fieldset > div:first-child > input").focus();
		});

		cr.modal('show');
		cr.data("url", url);
		cr.data("onSuccess", onSuccess);
	});
}

/**
 *
 * @param selection jQuery selection object (<selection>)
 * @param value the value of the text to add
 * @param text the text to add
 */
function addSortedSelectionItem(selection, value, text) {
	var option = selection.children("option:first-child");
	while ((option.length == 1) && (option.text() < text)) {
		option = option.next();
	}
	var toAppend = '<option value="' + value + '" selected="selected">' + text + '</option>';
	if (option.length == 0) {
		selection.append(toAppend);
	} else {
		option.before(toAppend);
	}
}

/**** begin: for datatable ****/

/**
 * Uses selected row as information for deleting on server (and on success deleting in table)
 *
 * the id of the thing to delete is read from the first column of the table
 *
 * @param tableInfo: info object about table
 * @param nameOfThingToDelete: used at messages
 * @param baseURL: used to form URL by baseURL+<name of thing>
 * @param idColumn: (optional) column to look for the id. If not provided, look in the first column
 * @param nameColumn: (optional) column to look for a name. If not provided, the id is used
 * @param namespaceColumn: (optional) column to look for a namespace. If not provided, do not use any nameespace information
 * @param withoutConfirmation (optional) if given, the resource is deleted without any confirmation
*/
function deleteOnServerAndInTable(tableInfo, nameOfThingToDelete, baseURL, idColumn, nameColumn, namespaceColumn, withoutConfirmation) {
	if (tableInfo.selectedRow == null) {
		vShowError("No row selected.");
	} else {
		idColumn = idColumn || 0; // default: first column indicates identifier
		var id = tableInfo.table.fnGetData(tableInfo.selectedRow, idColumn);
		var name;
		if (typeof nameColumn === "undefined") {
			name = id;
		} else  {
			name = tableInfo.table.fnGetData(tableInfo.selectedRow, nameColumn);
		}

		var url = baseURL;
		if (typeof namespaceColumn !== "undefined") {
			var namespace = tableInfo.table.fnGetData(tableInfo.selectedRow, namespaceColumn);
			namespace = encodeID(namespace);
			url = url + namespace + '/';
		}
		// append the id
		// we could add a "/" to be compatible with Jersey's URL rewriting
		// However, that prevents deleting a thing being a leaf in the URL (e.g. a namespace)
		url = url + encodeID(id);

		// defined in winery-common.js
		deleteResource(nameOfThingToDelete + " " + name, url,
			function(data, textSTatus, jqXHR) {
				tableInfo.table.fnDeleteRow(tableInfo.selectedRow);
				tableInfo.selectedRow = null;
				tableInfo.selectedTr = null;
			}, false, false, withoutConfirmation
		);
	}
}

/**** end: for datatable ****/

/**
 * Uploads the content of given form to given url
 *
 * @param form specifies the form to read data from
 * @param url specifies the URL to send the data to
 * @param onSuccess: function(XMLHttpRequest) to handle result
 */
function uploadFile(form, url, onSuccess) {
	var xhr = new XMLHttpRequest();
	var fd = new FormData(form);
	xhr.onreadystatechange = function(e) {
		if (this.readyState == 4) {
			if ((xhr.status != 200) && (xhr.status != 201)) {
				alert("Upload error occurred: " + xhr.status);
			} else {
				onSuccess(xhr);
			}
		}
	};
	xhr.open('post', url, true);
	xhr.send(fd);
}

/**
 * PUTs given value to the server in the BODY
 *
 * @param thing the thing to send. used as URL and in the error messages
 */
function updateValue(thing, value) {
	$.ajax({
		type: "PUT",
		async: false,
		url: thing,
		"data": value,
		dataType: "text",
		processData: false, // leads to a send in the body
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not set " + thing, jqXHR, errorThrown);
		},
		success: function() {
			vShowSuccess("Successfully updated " + thing);
		}
	});
}

/**
 * Puts the color to visualappearance/{id}
 *
 * Required by visualappearance.jsp (node type and relation ship type)
 *
 * @param id
 */
function putColor(id, hex) {
	var dataToSend = {
		"color" : hex
	};
	$.ajax({
		type : "PUT",
		async : false,
		url : "visualappearance/" + id,
		"data" : dataToSend,
		dataType : "text",
		error : function(jqXHR, textStatus, errorThrown) {
			vShowError("Could not set color " + errorThrown);
		},
		success: function(data, textStatus, jqXHR) {
			vShowSuccess("Successfully updated color");
		}
	});
}


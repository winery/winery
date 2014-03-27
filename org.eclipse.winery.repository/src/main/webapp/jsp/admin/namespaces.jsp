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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eclipse.org/winery/repository/functions" prefix="w" %>
<script>
var namespacePrefixesTableInfo = {
	id : '#namespacePrefixesTable'
};

$(function() {
	require(["winery-support"], function(ws) {
		ws.initTable(namespacePrefixesTableInfo);
	});
});

function addNSprefix() {
	$.ajax({
		url: "${pageContext.request.contextPath}/admin/namespaces/",
		type: "POST",
		async: false,
		data: $('#addNamespacePrefixForm').serialize(),
		error: function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not add namespace prefix", jqXHR, errorThrown);
		},
		success: function(data, textSTatus, jqXHR) {
			namespacePrefixesTableInfo.table.fnAddData([$('#nsPrefixAdded').val(), $('#namespaceAdded').val()]);
			$('#addNamespacePrefixDiag').modal('hide');
			vShowSuccess("Successfully added namespace prefix.");
		}
	});
}

</script>

<div class="modal fade" id="addNamespacePrefixDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Add namespace prefix</h4>
			</div>
			<div class="modal-body">
				<form id="addNamespacePrefixForm" enctype="multipart/form-data">
					<fieldset>
						<div class="form-group">
							<label for="nsPrefixAdded">Prefix</label>
							<input name="nsPrefix" id="nsPrefixAdded" class="form-control" type="text" />
						</div>

						<div class="form-group">
							<label for="namespaceAdded">Namespace</label>
							<input name="namespace" id="namespaceAdded" class="form-control" type="text" />
						</div>
					</fieldset>
				</form>
			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button type="button" class="btn btn-primary" onclick="addNSprefix()">Add</button>
			</div>
		</div>
	</div>
</div>

<div id="namespaces">
	<div class="listheading">
		<label>Defined Prefixes for Namespaces</label>
		<button class="rightbutton btn btn-danger btn-xs" type="button" onclick="deleteOnServerAndInTable(namespacePrefixesTableInfo, 'namespace', 'namespaces/', 1);">Remove</button>
		<button class="rightbutton btn btn-primary btn-xs" type="button" onclick="$('#addNamespacePrefixDiag').modal('show')">Add</button>
	</div>
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="namespacePrefixesTable">
		<thead>
			<tr>
				<th>Prefix</th>
				<th>Namespace</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="ns" items="${it.namespacesForJSP}">
				<tr>
					<td class="prefix">${w:getPrefix(ns.decoded)}</td>
					<td>${ns.decoded}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<script>
$(document).on("click", "td.prefix",
	vCreateTdClickFunction(
		"${pageContext.request.contextPath}/admin/namespaces/",
		"nsPrefix",
		"namespace"));
</script>

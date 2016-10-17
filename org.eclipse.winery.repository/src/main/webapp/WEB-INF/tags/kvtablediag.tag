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
 *    Kálmán Képes - initial API and implementation and/or initial documentation
 *******************************************************************************/
--%>
<%@tag description="Generic Key-Value Table and Dialog to use in JSPs" pageEncoding="UTF-8"%>


<%@attribute name="labelForSingleItem" required="true" %>
<%@attribute name="url" required="true"%>
<%@attribute name="allSubResources" required="true" type="java.util.List" description="All available tags" %>
<%@attribute name="typeClass" required="true" type="java.lang.Class" description="The class of the type" %>

<%@taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct"  tagdir="/WEB-INF/tags/common" %>
<%@taglib prefix="con" tagdir="/WEB-INF/tags/constraints" %>
<%@taglib prefix="o"   tagdir="/WEB-INF/tags/common/orioneditor"%>
<%@taglib prefix="t"   tagdir="/WEB-INF/tags" %>
<%@taglib prefix="w"   uri="http://www.eclipse.org/winery/repository/functions"%>
<%@taglib prefix="wc"  uri="http://www.eclipse.org/winery/functions"%>

<button class="rightbutton btn btn-danger btn-xs" type="button" onclick="deleteOnServerAndInTable(tagsTableInfo, '${labelForSingleItem}', '${url}');">Remove</button>
<button class="rightbutton btn btn-primary btn-xs" type="button" onclick="$('#addTagDiag').modal('show');">Add</button>

<div class="modal fade" id="addTagDiag">
<div class="modal-dialog">
<div class="modal-content" style="width:660px;">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h4 class="modal-title">Add ${labelForSingleItem}</h4>
	</div>
	<div class="modal-body">
		<form id="addTagForm" enctype="multipart/form-data"><fieldset>
			<div class="form-group">
				<label for="tagname">Name</label>
				<input class="form-control" name="name" id="tagname" type="text" required="required" />
			</div>
			<div class="form-group">
				<label for="tagvalue">Value</label>
				<input class="form-control" name="value" id="tagvalue" type="text" required="required" />
			</div>
		</fieldset></form>
	</div>
	<div class="modal-footer">
		<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
		<button type="button" class="btn btn-primary" onclick="createTagDef();">Add</button>
	</div>
</div>
</div>
</div>

<table id="kvtablediag">

<thead>
	<tr>
		<th>id</th>
		<th>name</th>
		<th>value</th>
	</tr>
</thead>

<tbody>

<c:forEach var="r" items="${allSubResources}">
	<tr>
		<td>${r.id}</td>
		<td>${r.name}</td>
		<td>${r.value}</td>
	</tr>
</c:forEach>

</tbody>

</table>

<script>
// TODO: this variable is available after switching tabs.
// One could cache this information without requiring reloading all the content
var tagsTableInfo = {
	id : '#kvtablediag'
};

require(["winery-support"], function(ws) {
	ws.initTable(tagsTableInfo);
});

function createTagDef() {
	if (highlightRequiredFields()) {
		vShowError("Please fill out all required fields.");
		return;
	}

	var data = $('#addTagForm').serialize();

	$.ajax({
		url: "${url}",
		type: "POST",
		async: false,
		data: data,
		error: function(jqXHR, textStatus, errorThrown) {
			vShowError("Could not add ${labelForSingleItem}: " + errorThrown + "<br/>" + jqXHR.responseText);
		},
		success: function(id, textStatus, jqXHR) {
			// Data has been validated at the server
			// We can just add the local data
			var name = $('#tagname').val();
			var value = $('#tagvalue').val();
			var dataToAdd = [id, name, value];
			console.log(dataToAdd);
			tagsTableInfo.table.fnAddData(dataToAdd);
			$('#addTagDiag').modal('hide');
		}
	});
}

</script>
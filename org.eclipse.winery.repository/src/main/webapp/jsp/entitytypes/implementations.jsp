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
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:addComponentInstance
	label="${it.typeStr} Implementation"
	URL="${pageContext.request.contextPath}/${it.type}implementations/"
	onSuccess="implementationsTableInfo.table.fnAddData([$(\"#addComponentInstanceForm :input[name='namespace']\").val(), $(\"#addComponentInstanceForm :input[name='name']\").val()]);"
	type="${it.typeId.QName}"
	openinnewwindow="false"
	/>

<script>
var implementationsTableInfo = {
	id: '#implementationsTable'
};

require(["winery-support"], function(ws) {
	ws.initTable(implementationsTableInfo, {
		"aoColumns": [
						{ "sTitle": "namespace" },
						{ "sTitle": "name" }
					],
		"aaData" : ${it.implementationsTableData}
	});
});

	function openImplementationEditor() {
		var namespace = implementationsTableInfo.table.fnGetData(implementationsTableInfo.selectedRow,0);
		var id = implementationsTableInfo.table.fnGetData(implementationsTableInfo.selectedRow,1);
		window.open("${pageContext.request.contextPath}/${it.type}implementations/" + encodeID(namespace) + "/" + encodeID(id), "_self");
	}

</script>

<p>
This page shows implementations available for this type.
Go to <a href="${pageContext.request.contextPath}/other/">Other Elements</a> to get an overview on all implementations stored in this repository.
</p>

	<div id="implementations">

		<button class="rightbutton btn btn-danger btn-xs" type="button" onclick="deleteOnServerAndInTable(implementationsTableInfo, '${it.typeStr} Implementation', '${pageContext.request.contextPath}/${it.type}implementations/', 1, 1, 0);">Remove</button>
		<button class="rightbutton btn btn-primary btn-xs" type="button" onclick="openNewCIdiag();">Add</button>
		<button class="rightbutton btn btn-default btn-xs" type="button" onclick="openImplementationEditor();">Edit</button>

		<table cellpadding="0" cellspacing="0" border="0" class="display" id="implementationsTable"></table>
	</div>

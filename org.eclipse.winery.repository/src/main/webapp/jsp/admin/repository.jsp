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
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<h4>General Repository Commands</h4>
<div>
	<a href="repository/?dump" class="btn btn-primary">Dump Repository</a>
	<button class="btn btn-danger" onclick="clearRepository();" id="btnclearrepository" data-loading-text="Deleting...">Clear Repository</button>
	<button class="btn btn-default" onclick="$('#upRepoZipDiag').modal('show');">Import Repository</button>
</div>

<t:simpleSingleFileUpload
	title="Upload Repository Content"
	text="Repository dump file"
	URL="repository/"
	type="POST"
	id="upRepoZip"
	accept="application/zip" />

<script>
function clearRepository() {
	deleteResource('the complete repository', 'repository/',
			function() {$("#btnclearrepository").button("reset");},
			function() {$("#btnclearrepository").button("reset");},
			function() {$("#btnclearrepository").button("loading");}
	);
}
</script>

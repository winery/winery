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

<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage windowtitle="Test" selected="Admin" cssClass="admin">

<input id="fileupload" type="file" name="files[]" multiple
    data-url="/path/to/upload/handler.json"
    data-sequential-uploads="true"
    data-form-data='{"script": "true"}'>

<script>
var fu = $("#fileupload");
requirejs(["jquery.fileupload"], function() {
    fu.fileupload({autoUpload:true});
});
</script>


</t:genericpage>

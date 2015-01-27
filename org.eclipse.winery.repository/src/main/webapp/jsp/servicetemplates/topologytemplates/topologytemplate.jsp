<%--
/*******************************************************************************
 * Copyright (c) 2012-2013,2015 University of Stuttgart.
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
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<div>
	<a class="btn btn-primary" id="newtab" style="cursor:pointer;" href="${it.location}" target="_blank" >Open Editor</a>
	<a class="btn btn-info" href="topologytemplate/?view" target="_blank" >Open View</a>
<%
if (org.eclipse.winery.repository.Prefs.INSTANCE.isPlanBuilderAvailable()) {
%>
	<script>
	function generateBuildPlan() {
		$.ajax({
			url: 'topologytemplate/',
			// targeting method triggerGenerateBuildPlan in TopologyTemplateResource.java
			dataType: "text"
		}).fail(function(jqXHR, textStatus, errorThrown) {
			vShowAJAXError("Could not trigger plan generation.", jqXHR, errorThrown);
		}).done(function(data, textStatus, jqXHR) {
			// data contains the URL of the current converter status
			// the URL of the generated plan is NOT sent
			var resultText = "Successfully triggered plan generation.<br />";
			resultText += 'Current status of the conversion may be found at <a href="' + data + '">' + data + '</a>.';
			vShowSuccess(resultText);
		});
	}
	</script>
	<button class="btn btn-default" onclick="generateBuildPlan();">Generate Build Plan</button>
<%
}
%>
	<br>
	<br>
	<div id="loading" class="topologyTemplatePreviewSizing" style="position:absolute; background-color: white; z-index:5;">Loading preview...</div>
	<iframe id="topologyTemplatePreview" class="topologyTemplatePreviewSizing" src="topologytemplate/?view=small" onload="$('#loading').hide(1000);"></iframe>
</div>

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
 *    Uwe Breitenbücher - initial API and implementation and/or initial documentation
 *    Oliver Kopp - improvements to fit updated index.jsp
 *    Yves Schubert - switch to bootstrap 3
 *******************************************************************************/
--%>

<%@tag language="java" pageEncoding="UTF-8" description="Renders the properies of one node tempate on the right"%>

<%@attribute name="repositoryURL" required="true" type="java.lang.String" description="The repository URL"%>
<%@attribute name="uiURL" required="true" type="java.lang.String" description="The UI URL"%>

<%@taglib prefix="ct" tagdir="/WEB-INF/tags/common" %>


<link rel="stylesheet" href="css/propertiesview.css" />

<div id="NTPropertiesView" class="propertiesView" style="display: none;">

	<div id="nodeTemplateInformationSection">
		<%--
			If this is layouted strangely, maybe a <form> wrapper has to be added
			Be aware that nested buttons then trigger a submission of the form (-> ct:spinnerwithinphty)
		--%>
		<fieldset>
			<div class="form-group">
				<label for="nodetemplateid">Id</label>
				<input id="nodetemplateid" disabled="disabled" class="form-control"></input>
			</div>
			<div class="form-group">
				<label for="nodetemplatename" class="control-label">Name</label>
				<input id="nodetemplatename" name="name" class="form-control"/>
			</div>
			<div class="form-group">
				<label for="nodetemplateType">Type</label>
				<%-- filled by fillInformationSection --%>
				<a id="nodetemplateType" target="_blank" href="#" class="form-control"></a>
			</div>
			<ct:spinnerwithinphty min="0" width="10" changedfunction="minInstancesChanged" label="min" id="minInstances" />
			<ct:spinnerwithinphty min="1" width="10" changedfunction="maxInstancesChanged" label="max" id="maxInstances" withinphty="true" />
		</fieldset>
	</div>

</div>

<script>
	function minInstancesChanged(event, ui) {
		var val;
		if (ui === undefined) {
			val = $("#minInstances").val();
		} else {
			val = ui.value;
		}
		ntMin.html(val);
	}

	function maxInstancesChanged(event, ui) {
		var val;
		if (ui === undefined) {
			val = $("#maxInstances").val();
		} else {
			val = ui.value;
		}
		ntMax.html(val);
	}

	// the name input field of the properties section
	var nameInput = $("#nodetemplatename");

	// the min/max fields of the currently selected node template
	var ntMin;
	var ntMax;

	function fillInformationSection(nodeTemplate) {
		require(["winery-support-common"], function(wsc) {
			// currently doesn't help for a delayed update
			//informationSection.slideDown();

			$("#nodetemplateid").val(nodeTemplate.attr("id"));

			var headerContainer = nodeTemplate.children("div.headerContainer");

			// copy name
			var nameField = headerContainer.children("div.name");
			var name = nameField.text();
			nameInput.val(name);

			// copy type
			var typeQName = headerContainer.children("span.typeQName").text();
			var href = wsc.makeNodeTypeURLFromQName("${uiURL}", typeQName);
			var type = headerContainer.children("div.type").text();
			$("#nodetemplateType").attr("href", href).text(type);

			// we could use jQuery-typing, but it is not possible to replace key events there
			nameInput.off("keyup");
			nameInput.on("keyup", function() {
				nameField.text($(this).val());
			});

			// handling of min and max
			ntMin = nodeTemplate.children(".headerContainer").children(".minMaxInstances").children(".minInstances");
			$("#minInstances").val(ntMin.text());
			ntMax = nodeTemplate.children(".headerContainer").children(".minMaxInstances").children(".maxInstances");
			$("#maxInstances").val(ntMax.text());
		});
	}

	function showViewOnTheRight() {
		$("#NTPropertiesView").fadeIn();
	}

	function hideViewOnTheRight() {
		$("#NTPropertiesView").fadeOut();
	}

$(function() {
	winery.events.register(
		winery.events.name.SELECTION_CHANGED,
		function() {
			// min/max instances do not lost focus if other shape is clicked
			// workaround
			if ($("#minInstances").is(":focus")) {
				minInstancesChanged();
			}
			if ($("#maxInstances").is(":focus")) {
				maxInstancesChanged();
			}
			var nodeTemplate = $("div.NodeTemplateShape.selected");
			var numSelected = nodeTemplate.length;
			if (numSelected == 1) {
				fillInformationSection(nodeTemplate);
				showViewOnTheRight();
			} else {
				hideViewOnTheRight();
			}
		}
	);
});
</script>

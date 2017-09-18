<%--
/*******************************************************************************
 * Copyright (c) 2012-2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
--%>
<%@tag language="java" pageEncoding="UTF-8" description="Renders the properies of one relationship tempate on the right"%>

<%@attribute name="relationshipTypes" required="true" type="java.util.Collection" %>
<%@attribute name="repositoryURL" required="true" type="java.lang.String" description="The repository URL"%>

<link rel="stylesheet" href="css/propertiesview.css" />

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="props" tagdir="/WEB-INF/tags/common/templates" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions" %>

<div id="RTPropertiesView" class="propertiesView" style="display: none;">

	<div id="relationshipTemplateInformationSection">
		<fieldset>
			<div class="form-group">
				<label for="relationshiptemplateid">Id</label>
				<input id="relationshiptemplateid" disabled="disabled" class="form-control"></input>
			</div>
			<div class="form-group">
				<label for="relationshiptemplatename" class="control-label">Name</label>
				<a href="#" id="relationshiptemplatename" data-title="Name" data-type="text" class="form-control"></a>
			</div>
			<div class="form-group">
				<label for="relationshipType">Type</label>
				<%-- filled by showRTViewOnTheRight --%>
				<a id="relationshipType" target="_blank" href="#" class="form-control"></a>
			</div>
			<div class="form-group">
				<label for="RTreq" class="control-label">Requirement</label>
				<select id="RTreq" class="form-control">
				</select>
			</div>
			<div class="form-group">
				<label for="RTcap" class="control-label">Capability</label>
				<select id="RTcap" class="form-control">
				</select>
			</div>
		</fieldset>
	</div>

</div>

<script>

	var currentlySelectedConn = null;

	/**
	 * Fills the requirement and capabilities dropdowns with the available reqs and caps (which are defined at the source/target node template)
	 *
	 * @param conn the connection itself
	 * @param dataField = "req"|"cap"
	 * @param sourceDivClass = requirementsContainer | capabilitiesContainer
	 */
	function fillReqOrCap(conn, dataField, nodetemplateId, sourceDivClass, targetSelect) {
		var nt = $("#" + nodetemplateId);
		var reqsOrCaps = nt.children("." + sourceDivClass).children(".content").children(".reqorcap");
		var connReqCap = winery.connections[conn.id][dataField];

		targetSelect.empty();

		var optData = {
			value: "__NONE__",
			text: "(none)"
		};
		if (!connReqCap) {
			selected: true
		}
		require(["tmpl"], function(tmpl) {
			var newOption = tmpl("tmpl-option", optData);
			targetSelect.append(newOption);

			reqsOrCaps.each(function(i,e) {
				optData.value = $(e).children(".id").children("span.id").text();
				optData.text = $(e).children(".name").children("span.name").text();
				optData.selected = (optData.value == connReqCap);
				newOption = tmpl("tmpl-option", optData);
				targetSelect.append(newOption);
			});
		});

		targetSelect.off("change");
		targetSelect.on("change", function(e) {
			var val = targetSelect.val();
			if (val == "__NONE__") {
				delete(winery.connections[conn.id][dataField]);
			} else {
				winery.connections[conn.id][dataField] = val;
			}
		});
	}

	function fillType(nsAndLocalName) {
		require(["winery-support-common"], function(wsc) {
			var href = wsc.makeRelationshipTypeURLFromNSAndLocalName("${repositoryURL}", nsAndLocalName);
			// localname is always the name of the relationship type because the specification requires a "name" attribute only and does not foresee an "id" attribute
			$("#relationshipType").attr("href", href).text(nsAndLocalName.localname);
		})
	}

	function displayProperties(connData) {
		$("#RTPropertiesView").append(connData.propertiesContainer);
	}

	/**
	 * @param conn the jsPlumb connection
	 */
	function showRTViewOnTheRight(conn) {
		currentlySelectedConn = conn;

		$("#RTPropertiesView").fadeIn();

		$("#relationshiptemplateid").val(winery.connections[conn.id].id);
		$("#relationshiptemplatename").editable('setValue', winery.connections[conn.id].name);
		fillReqOrCap(conn, "req", conn.sourceId, "requirementsContainer", $("#RTreq"));
		fillReqOrCap(conn, "cap", conn.targetId, "capabilitiesContainer", $("#RTcap"));
		fillType(winery.connections[conn.id].nsAndLocalName);
		displayProperties(winery.connections[conn.id]);
	}

	function hideRTViewOnTheRight() {
		if (currentlySelectedConn == null) {
			// nothing to do if no relationship template is selected
			return;
		}

		$("#RTPropertiesView").fadeOut();

		// user will see some flickering here, but we don't want to set timers -> could lead to race conditions
		$("#skelettonContainerForRelationshipTemplates").append(winery.connections[currentlySelectedConn.id].propertiesContainer);
		currentlySelectedConn = null;
	}

	function storeUpdatedName(newName) {
		currentlySelectedConn.name = newName;
	}

	$(function() {
		$("#relationshiptemplatename").editable({
			success: function(response, newValue) {
				currentlySelectedConn.name = newValue;
			}
		});
	});

	function unselectAllConnections() {
		jsPlumb.select().each(function(connection) {
			connection.removeType("selected");
		});
	}

	winery.events.register(
		winery.events.name.SELECTION_CHANGED,
		function() {
			var nodeTemplate = $("div.NodeTemplateShape.selected");
			var numSelected = nodeTemplate.length;
			if (numSelected != 0) {
				// if node templates are selected, no RT properties should be shown
				hideRTViewOnTheRight();

				unselectAllConnections();
			}
		}
	);

</script>

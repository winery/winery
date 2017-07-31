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
 *    Oliver Kopp - improvements
 *******************************************************************************/
--%>
<%@tag language="java" pageEncoding="UTF-8" description="Renders the palette on the left"%>

<%@attribute name="repositoryURL" required="true" type="java.lang.String"%>
<%@attribute name="uiURL" required="true" type="java.lang.String"%>
<%@attribute name="client" required="true" description="IWineryRepository" type="org.eclipse.winery.common.interfaces.IWineryRepository"%>
<%@attribute name="relationshipTypes" description="the known relationship types" required="true" type="java.util.Collection"%>

<%@tag import="javax.xml.namespace.QName" %>
<%@tag import="java.util.Collection"%>
<%@tag import="java.util.UUID"%>
<%@tag import="java.util.List"%>
<%@tag import="org.eclipse.winery.common.interfaces.IWineryRepository" %>
<%@tag import="org.eclipse.winery.model.tosca.TNodeType"%>
<%@tag import="org.eclipse.winery.common.Util" %>

<%@taglib prefix="nt" tagdir="/WEB-INF/tags/common/templates/nodetemplates" %>

<link rel="stylesheet" href="css/palette.css" />

<div id="palette">

<div id="paletteLabel">
Palette
</div>

<%
	Collection<TNodeType> allNodeTypes = client.getAllTypes(TNodeType.class);
	if (allNodeTypes.isEmpty()) {
%>
		<script>
			vShowError("No node types exist. Please add node types in the repository.");
		</script>
	<%
	}
	for (TNodeType nodeType: allNodeTypes) {
		if (nodeType.getName() == null) {
			System.err.println("Invalid nodetype in ns " + nodeType.getTargetNamespace());
			continue;
		}
%>
		<div class="paletteEntry">
			<div class="iconContainer">
				<img class="icon" onerror="var that=this; require(['winery-common-topologyrendering'], function(wct){wct.imageError(that);});" src="<%= repositoryURL %>/nodetypes/<%= Util.DoubleURLencode(nodeType.getTargetNamespace()) %>/<%=Util.DoubleURLencode(nodeType.getName())%>/visualappearance/50x50" />
			</div>
			<div class="typeContainer">
				<div class="typeContainerMiddle">
					<div class="typeContainerInner">
					<%= nodeType.getName() %>
					</div>
				</div>
			</div>

			<div class="hidden">
				<nt:nodeTemplateRenderer
					uiURL="${uiURL}"
					repositoryURL="${repositoryURL}"
					client="${client}"
					relationshipTypes="${relationshipTypes}"
					nodeTypeQName="<%=new QName(nodeType.getTargetNamespace(), nodeType.getName())%>"
					nodeType="<%=nodeType%>" />
			</div>
		</div>

<%
	}
%>

</div>


<script>

	//$("#palette").css("width","20px");
	//$("div.paletteEntry").hide();

	$("#palette").click (function() {
		showPalette();
		winery.events.fire(winery.events.name.command.UNSELECT_ALL_NODETEMPLATES);
	});

	function showPalette() {
		// reset width to original CSS width
		$("#palette").removeClass("shrunk");
		// show all palette entries
		$("div.paletteEntry").show();
		$("#paletteLabel").hide();
	}

	function hidePalette() {
		$("#palette").addClass("shrunk");
		// hide all palette entries
		$("div.paletteEntry").hide();
		$("#paletteLabel").show();
	}

	$(function() {
		$( "div.paletteEntry" ).draggable({
			cursor: "move",
			cursorAt: { top: 40, left: 112 },
			helper: function( event ) {
				var newObj = $(this).find("div.NodeTemplateShape").clone();
				newObj.removeClass("hidden");
				newObj.css("z-index", "2000");
				newObj.find ("div.endpointContainer").remove();

				// Ensure that obj is appended to drawingarea and not to palette
				// Consequence: the dragged object is always under the cursor and not paintet with an offset equal to the scrollheight
				$("#drawingarea").append(newObj);

				return newObj;
			},
			start: function( event, ui ) {
				winery.events.fire(winery.events.name.command.UNSELECT_ALL_NODETEMPLATES);
				// The palette is kept visible after a drag start,
				// therefore no action
				// hidePalette();
			},
			appendTo: '#drawingarea'
		});


	$( "div#drawingarea" ).droppable({
		accept: function(d) {
			if (d.hasClass("paletteEntry")) {
				return true;
			}
		},
		drop: function( event, ui ) {

			var palEntry = ui.draggable;
			var templateCode = palEntry.find("div.NodeTemplateShape").clone().wrap("<div></div>").parent().html();

			var newObj = $(templateCode);

			newObj.removeClass("ui-draggable");
			newObj.removeClass("ui-droppable");
			newObj.removeClass("hidden");

			// generate and set id
			var type = newObj.find("div.type.nodetemplate").text();
			var id = type;
			// we cannot use the id as the initial name, because we want to preserve special characters in the name, but not in the id.
			var name = type;

			// quick hack to make id valid
			// currently, only spaces and dots cause problems
			id = id.replace(" ", "_");
			id = id.replace(".", "_");

			if ($("#" + id).length != 0) {
				var count = 2;
				var idprefix = id + "_";
				do {
					id = idprefix + count;
					count++;
				} while ($("#" + id).length != 0);
				// also adjust name
				name = name + "_" + count;
			}
			newObj.attr("id", id);
			newObj.children("div.headerContainer").children("div.id").text(id);

			// initial name has been generated based on the id
			newObj.children("div.headerContainer").children("div.name").text(name);

			// fix main.css -> #editorArea -> margin-top: 45px;
			var top = Math.max(event.pageY-45, 0);

			// drag cursor is at 112/40
			// fix that
			top = Math.max(top-40, 0);
			var left = Math.max(event.pageX-112, 0);

			newObj.css("top", top);
			newObj.css("left", left);

			newObj.addClass("selected");

			// insert into sheet
			newObj.appendTo( $( "div#drawingarea" ) );

			// initialization works only for displayed objects
			require(["winery-common-topologyrendering"], function(wct) {
				wct.initNodeTemplate(newObj, true);

				// handle menus
				winery.events.fire(winery.events.name.SELECTION_CHANGED);
			});
		}
	})


});

</script>

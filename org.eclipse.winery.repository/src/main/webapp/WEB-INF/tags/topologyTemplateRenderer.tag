<%--
/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - skeletton for topology rendering
 *    Oliver Kopp - converted to .tag and integrated in the repository
 *******************************************************************************/
--%>
<%@tag description="Renders a toplogytemplate. This tag is used to render a topology template readonly. The topoology modeler does the rendering on itself." pageEncoding="UTF-8" %>

<%@tag import="java.lang.Math"%>
<%@tag import="java.util.ArrayList"%>
<%@tag import="java.util.Collection"%>
<%@tag import="org.eclipse.winery.common.ModelUtilities"%>
<%@tag import="org.eclipse.winery.model.tosca.TEntityTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeType"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>
<%@tag import="org.eclipse.winery.repository.Utils"%>

<%@attribute name="topology" required="true" description="the topology template to be rendered" type="org.eclipse.winery.model.tosca.TTopologyTemplate" %>
<%@attribute name="repositoryURL" required="true" %>
<%@attribute name="uiURL" required="true" %>
<%@attribute name="client" required="true" type="org.eclipse.winery.common.interfaces.IWineryRepository" %>
<%@attribute name="fullscreen" required="false" type="java.lang.Boolean" %>
<%@attribute name="additonalCSS" required="false"%>
<%@attribute name="autoLayoutOnLoad" required="false" type="java.lang.Boolean" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tmpl" tagdir="/WEB-INF/tags/common/templates" %>
<%@taglib prefix="nt"   tagdir="/WEB-INF/tags/common/templates/nodetemplates" %>
<%@taglib prefix="w" uri="http://www.eclipse.org/winery/repository/functions"%>

<%-- required for vShowError --%>
<script type="text/javascript" src="${w:topologyModelerURI()}/components/pnotify/jquery.pnotify.js"></script>
<script type="text/javascript" src="${w:topologyModelerURI()}/js/winery-common.js"></script>

<%-- required for vShowError --%>
<link type="text/css" href="${w:topologyModelerURI()}/components/pnotify/jquery.pnotify.default.css" media="all" rel="stylesheet" />
<link type="text/css" href="${w:topologyModelerURI()}/components/pnotify/jquery.pnotify.default.icons.css" media="all" rel="stylesheet" />

<%-- winery-common.css also contains definitions for properties --%>
<link type="text/css" href="${w:topologyModelerURI()}/css/winery-common.css" rel="stylesheet" />
<link type="text/css" href="${w:topologyModelerURI()}/css/topologytemplatecontent.css" rel="stylesheet" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/topologyTemplateRenderer.css" />
<c:if test="${not empty fullscreen}"><link rel="stylesheet" href="${pageContext.request.contextPath}/css/topologyTemplateRendererFullscreen.css" /></c:if>
<c:if test="${not empty additonalCSS}"><link rel="stylesheet" href="${additonalCSS}" /></c:if>

<%
	Collection<TRelationshipType> relationshipTypes = client.getAllTypes(TRelationshipType.class);

	// quick hack
	// better would be to collect all types used in the curren topoloy template
	Collection<TNodeType> nodeTypes = client.getAllTypes(TNodeType.class);
%>

<tmpl:CSSForTypes nodeTypes="<%=nodeTypes%>" relationshipTypes="<%=relationshipTypes%>"/>

<script>
// required by winery-common-topologyrendering
if (typeof winery === "undefined") winery = {}
if (typeof winery.connections === "undefined") winery.connections = {}

//enable caching. This disables appending of "?_=xy" at requests
jQuery.ajaxSetup({cache:true});

//configuration for pnotify
require(["jquery", "pnotify"], function() {
	$.pnotify.defaults.styling = "bootstrap3";
});
</script>

<%
	// used for the position of the NodeTemplates
	int topCounter = 0;
%>
<script>
function doLayout() {
	var editor = $("#editorArea");
	var nodeTemplates = editor.find(".NodeTemplateShape");
	require(["winery-sugiyamaLayouter"], function(layouter) {
		layouter.layout(nodeTemplates);
	});
}
</script>
<div class="topbar">
	<div class="topbarbuttons">
		<button class="btn btn-default" onclick="doLayout();">Layout</button>
		<tmpl:toggleButtons />
	</div>
</div>
<%-- div #editorArea required for layouter --%>
<div id="editorArea">
<div id="templateDrawingArea">

<tmpl:defineCreateConnectorEndpointsFunction relationshipTypes="<%=relationshipTypes%>"/>

<%
	// can be used later to call a doLayout()
	boolean somethingWithoutPosition = false;

	Collection<TRelationshipTemplate> relationshipTemplates = new ArrayList<>();
	Collection<TNodeTemplate> nodeTemplates = new ArrayList<>();

	// the minimum x/y coordinates.
	// used to move the content to the top left corner
	int minTop = Integer.MAX_VALUE;
	int minLeft = Integer.MAX_VALUE;

	for (TEntityTemplate entity: topology.getNodeTemplateOrRelationshipTemplate()) {
		if (entity instanceof TNodeTemplate) {
			TNodeTemplate nodeTemplate = (TNodeTemplate) entity;
			nodeTemplates.add(nodeTemplate);

			// determine minTop and minLeft
			String top = ModelUtilities.getTop(nodeTemplate);
			if (top != null) {
				int intTop = Utils.convertStringToInt(top);
				if (intTop != 0) {
					minTop = Math.min(minTop, intTop);
				}
			}

			String left = ModelUtilities.getLeft(nodeTemplate);
			if (left != null) {
				int intLeft = Utils.convertStringToInt(left);
				if (intLeft != 0) {
					minLeft = Math.min(minLeft, intLeft);
				}
			}

		} else {
			assert(entity instanceof TRelationshipTemplate);
			relationshipTemplates.add((TRelationshipTemplate) entity);
		}
	}

	for (TNodeTemplate nodeTemplate: nodeTemplates) {
		// assuming the topology can be displayed as a stack, else call doLayout() afterwards
		topCounter = topCounter + 150;

		String left = ModelUtilities.getLeft(nodeTemplate);
		if (left == null) {
			left = "0";
			somethingWithoutPosition = true;
		} else {
			// calulate offset
			// we could hash the coordinate in the loop before
			// but that would obfuscate the code and currently, we don't have speed issues here
			left = Integer.toString(Utils.convertStringToInt(left) - minLeft);
		}
		String top = ModelUtilities.getTop(nodeTemplate);
		if (top == null) {
			top = Integer.toString(topCounter);
			somethingWithoutPosition = true;
		} else {
			// calulate offset
			top = Integer.toString(Utils.convertStringToInt(top) - minTop);
		}
%>
		<nt:nodeTemplateRenderer top="<%=top%>" left="<%=left%>" nodeTemplate="<%=nodeTemplate%>" repositoryURL="${repositoryURL}" uiURL="<%=uiURL%>" client="<%=client%>" relationshipTypes="<%=relationshipTypes%>" topologyModelerURI="${w:topologyModelerURI()}/" />
<%
	}
	if (somethingWithoutPosition) {
		autoLayoutOnLoad = true;
	}
%>

<script>
function onDoneRendering() {
	<c:if test="${autoLayoutOnLoad}">
	doLayout();
	</c:if>

	// copied from index.jsp -> togglePrintView

	// move labels 10 px up
	// we have to do it here as jsPlumb currently paints the label on the line instead of above of it
	// See https://groups.google.com/d/msg/jsplumb/zdyAdWcRta0/K6F2MrHBH1AJ
	$(".relationshipTypeLabel").each(function(i, e) {
		var pos = $(e).offset();
		pos.top = pos.top - 10;
		$(e).offset(pos);
	});

	// The user can pass an additional script to the topologyTemplateResource via the script query parameter
	// In that script, he can define the function wineryViewExternalScriptOnLoad which is called here
	if (typeof wineryViewExternalScriptOnLoad === "function") {
		wineryViewExternalScriptOnLoad();
	}
}
</script>
<tmpl:registerConnectionTypesAndConnectNodeTemplates repositoryURL="${repositoryURL}" relationshipTypes="<%=relationshipTypes%>" relationshipTemplates="<%=relationshipTemplates%>" ondone="onDoneRendering();" readOnly="true"/>
</div>
</div>

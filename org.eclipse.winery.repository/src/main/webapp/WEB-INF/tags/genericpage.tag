<%--
/*******************************************************************************
 * Copyright (c) 2012-2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Yves Schubert - switch to bootstrap 3
 *    Lukas Balzer, Nicole Keppler - switch to bootstrap-touchspin
 *******************************************************************************/
--%>
<%@tag description="Global Wrapper" pageEncoding="UTF-8"%><!DOCTYPE html>

<%@attribute name="windowtitle" required="true" description="String to be used as window title"%>
<%@attribute name="selected" required="true"%>
<%@attribute name="cssClass" required="true"%>

<%@attribute name="libs" fragment="true" %>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="ct" tagdir="/WEB-INF/tags/common"%>
<%@taglib prefix="t"  tagdir="/WEB-INF/tags"%>
<%@taglib prefix="w"  uri="http://www.eclipse.org/winery/repository/functions"%>

<%@tag import="org.eclipse.winery.repository.Prefs" %>

<!-- This is Winery ${project.version}  -->

<html>
<head>
	<title>${windowtitle}</title>
	<meta name="application-name" content="Winery" />
	<meta charset="UTF-8">
	<link rel="icon" href="${w:topologyModelerURI()}/favicon.png" type="image/png">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/components/bootstrap/dist/css/bootstrap.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/components/bootstrap/dist/css/bootstrap-theme.css" />

	<%-- CSS to style the file input field as button and adjust the Bootstrap progress bars --%>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/components/blueimp-file-upload/css/jquery.fileupload.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/components/blueimp-file-upload/css/jquery.fileupload-ui.css" />
	<link type="text/css" href="${pageContext.request.contextPath}/components/datatables/media/css/jquery.dataTables.css" rel="stylesheet" />
	<link type="text/css" href="${pageContext.request.contextPath}/css/winery-repository.css" rel="Stylesheet" />
	<link type="text/css" href="${pageContext.request.contextPath}/components/pnotify/jquery.pnotify.default.css" media="all" rel="stylesheet" />
	<link type="text/css" href="${pageContext.request.contextPath}/components/pnotify/jquery.pnotify.default.icons.css" media="all" rel="stylesheet" />
	<link type="text/css" href="${pageContext.request.contextPath}/components/select2/select2.css" media="all" rel="stylesheet" />
	<link type="text/css" href="${pageContext.request.contextPath}/components/select2/select2-bootstrap.css" media="all" rel="stylesheet" />

	<link type="text/css" href="${pageContext.request.contextPath}/components/bootstrap3-wysihtml5-bower/dist/bootstrap3-wysihtml5.css" media="all" rel="stylesheet" />
	<link type="text/css" href="${pageContext.request.contextPath}/components/x-editable/dist/bootstrap3-editable/css/bootstrap-editable.css" media="all" rel="stylesheet" />

	<link rel="stylesheet" href="${pageContext.request.contextPath}/components/bootstrap-touchspin/src/jquery.bootstrap-touchspin.css" media="all"/>

	<link rel="stylesheet" href="${pageContext.request.contextPath}/components/xmltree/xmltree.css" />

	<link rel="stylesheet" type="text/css" href="http://eclipse.org/orion/editor/releases/6.0/built-editor.css"/>

	<link type="text/css" href="${w:topologyModelerURI()}/css/winery-common.css" rel="stylesheet" />

	<script type='text/javascript' src='${pageContext.request.contextPath}/components/requirejs/require.js'></script>
	<script>
		require.config({
			baseUrl: "${pageContext.request.contextPath}/js",
			paths: {
				"artifacttemplateselection": "${w:topologyModelerURI()}/js/artifacttemplateselection",
				"winery-sugiyamaLayouter": "${w:topologyModelerURI()}/js/winery-sugiyamaLayouter",

				"datatables": "../components/datatables/media/js/jquery.dataTables",
				"jquery": "../components/jquery/jquery",

				"jquery.fileupload": "../components/blueimp-file-upload/js/jquery.fileupload",
				"jquery.fileupload-ui": "../components/blueimp-file-upload/js/jquery.fileupload-ui",
				"jquery.fileupload-process": "../components/blueimp-file-upload/js/jquery.fileupload-process",
				"jquery.ui.widget": "../components/blueimp-file-upload/js/vendor/jquery.ui.widget",

				// required for jsplumb
				"jquery.ui": "../3rdparty/jquery-ui/js/jquery-ui",

				"jsplumb": "../components/jsPlumb/dist/js/jquery.jsPlumb-1.5.4",

				"keyboardjs": "../components/KeyboardJS/keyboard",

				"orioneditor": "http://eclipse.org/orion/editor/releases/6.0/built-editor-amd",

				"pnotify": "../components/pnotify/jquery.pnotify",

				"select2": "../components/select2/select2",

				"tmpl": "../components/blueimp-tmpl/js/tmpl",

				"URIjs": '../components/uri.js/src',

				"xmltree": "../components/xmltree/xmltree",

				"XMLWriter": "../components/XMLWriter/XMLWriter"
			}
		});
	</script>

	<script type='text/javascript' src='${pageContext.request.contextPath}/components/jquery/jquery.js'></script>
	<script type='text/javascript' src='${pageContext.request.contextPath}/components/bootstrap/dist/js/bootstrap.js'></script>

	<script type="text/javascript" src="${pageContext.request.contextPath}/components/jquery-typing/plugin/jquery.typing-0.3.2.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/components/select2/select2.js"></script>

	<script type="text/javascript" src="${pageContext.request.contextPath}/components/wysihtml5/dist/wysihtml5-0.3.0.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/components/handlebars/handlebars.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/components/bootstrap3-wysihtml5-bower/dist/bootstrap3-wysihtml5.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/components/x-editable/dist/bootstrap3-editable/js/bootstrap-editable.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/components/x-editable/dist/inputs-ext/wysihtml5/wysihtml5.js"></script>

	<script type='text/javascript' src='${pageContext.request.contextPath}/components/bootstrap-touchspin/dist/jquery.bootstrap-touchspin.js'></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/winery-support-non-AMD.js"></script>
	<script type="text/javascript" src="${w:topologyModelerURI()}/js/winery-common.js"></script>

	<script>
	// all x-editable popups should be placed in a way to fit "perfectly" on the screen
	$.fn.editable.defaults.placement = "auto";

	//configuration for pnotify
	require(["jquery", "pnotify"], function() {
		$.pnotify.defaults.styling = "bootstrap3";
	});
	</script>
</head>

<body>

<jsp:include page="/jsp/shared/dialogs.jsp" />

<script>
//enable caching. This disables appending of "?_=xy" at requests
jQuery.ajaxSetup({cache:true});

// prevent form submission on pressing "enter"
// In the topology modeler, this phenomen does not occurr.
// The reason why all forms are submitted on enter pressing is unknown.
$(document).on("keypress", "input", function(e) {
	var key = (e.keyCode || e.which);
	if (key == 13) {
		// enter pressed
		// press tab instead
		// The following does not work
		// TODO: include jQuery.tabbable plugin (http://stackoverflow.com/a/18740738/873282 / https://github.com/marklagendijk/jQuery.tabbable)
		$(e.currentTarget).trigger({
			type: 'keydown',
			which: 9
		});
		return false;
	}
});

$(function() {
	var scrolling = $(document).height() > $(window).height();
	if (scrolling) {
		// add CSS fix to prevent flickering
		$("#mainContainer").addClass("overflown");
	} else {
		$("#mainContainer").addClass("notoverflown");
	}
});
</script>

<t:about />

<div id="mainContainer">
	<div id="header">
		<div id="showabout">
			<button type="button" class="btn btn-default btn-xs" onClick="showAbout();">about</button>
		</div>
		<c:set var="warning" value="<%=Prefs.INSTANCE.getProperties().get(\"warning\")%>" />
		<c:if test="${not empty warning}">
		<div id="warning">
			${warning}
		</div>
		</c:if>

		<div id="mainMenuContainer">

			<%-- String values come from ComponentKind.toString() --%>

			<a class="styledTabMenuButton <c:if test="${selected eq 'ServiceTemplate'}">selected</c:if>" href="${pageContext.request.contextPath}/servicetemplates/">
				<div class="left"></div>
				<div class="center">Service Templates</div>
				<div class="right"></div>
			</a>

			<%-- TopologyTemplates: top level topology templates only in "pro" mode <a href="${pageContext.request.contextPath}/topologytemplates/">Topology Templates</a>  --%><%!  %>

			<a class="styledTabMenuButton <c:if test="${selected eq 'NodeType'}">selected</c:if>" href="${pageContext.request.contextPath}/nodetypes/">
				<div class="left"></div>
				<div class="center">Node Types</div>
				<div class="right"></div>
			</a>

			<a class="styledTabMenuButton <c:if test="${selected eq 'RelationshipType'}">selected</c:if>" href="${pageContext.request.contextPath}/relationshiptypes/">
				<div class="left"></div>
				<div class="center">Relationship Types</div>
				<div class="right"></div>
			</a>


			<%-- include all other TOSCA Elements into admin --%>
			<%-- We need to call it "Elements" instead of "components" as PRD01 on line 334 calls these "elements" --%>

			<c:choose>

				<c:when test="${selected eq 'ArtifactTemplate'}">
					<c:set var="otherSelected" value="selected" />
					<c:set var="otherLabel" value="Other Elements: Artifact Templates" />
				</c:when>

				<c:when test="${selected eq 'ArtifactType'}">
					<c:set var="otherSelected" value="selected" />
					<c:set var="otherLabel" value="Other Elements: Artifact Types" />
				</c:when>

				<c:when test="${selected eq 'CapabilityType'}">
					<c:set var="otherSelected" value="selected" />
					<c:set var="otherLabel" value="Other Elements: Capability Types" />
				</c:when>

				<c:when test="${selected eq 'NodeTypeImplementation'}">
					<c:set var="otherSelected" value="selected" />
					<c:set var="otherLabel" value="Other Elements: Node Type Implementations" />
				</c:when>

				<c:when test="${selected eq 'PolicyTemplate'}">
					<c:set var="otherSelected" value="selected" />
					<c:set var="otherLabel" value="Other Elements: Policy Templates" />
				</c:when>

				<c:when test="${selected eq 'PolicyType'}">
					<c:set var="otherSelected" value="selected" />
					<c:set var="otherLabel" value="Other Elements: Policy Types" />
				</c:when>

				<c:when test="${selected eq 'RelationshipTypeImplementation'}">
					<c:set var="otherSelected" value="selected" />
					<c:set var="otherLabel" value="Other Elements: Relationship Type Implementations" />
				</c:when>

				<c:when test="${selected eq 'RequirementType'}">
					<c:set var="otherSelected" value="selected" />
					<c:set var="otherLabel" value="Other Elements: Requirement Types" />
				</c:when>

				<c:when test="${selected eq 'XSDImport'}">
					<c:set var="otherSelected" value="selected" />
					<c:set var="otherLabel" value="Other Elements: Imports: XSD" />
				</c:when>


				<c:when test="${selected eq 'OtherElements'}">
					<c:set var="otherSelected" value="selected" />
					<c:set var="otherLabel" value="Other Elements" />
				</c:when>


				<c:otherwise>
					<c:set var="otherSelected" value="" />
					<c:set var="otherLabel" value="Other Elements" />
				</c:otherwise>

			</c:choose>

			<a class="styledTabMenuButton ${otherSelected}" href="${pageContext.request.contextPath}/other/">
				<div class="left"></div>
				<div class="center">${otherLabel}</div>
				<div class="right"></div>
			</a>

			<a class="styledTabMenuButton <c:if test="${selected eq 'admin'}">selected</c:if>" href="${pageContext.request.contextPath}/admin/">
				<div class="left"></div>
				<div class="center">Administration</div>
				<div class="right"></div>
			</a>

		</div>
	</div>

<script id="template-createresource" type="text/x-tmpl">
<div class="modal fade" id="createResource">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">{%=o.nameOfResource%}</h4>
			</div>
			<div class="modal-body">
				<form id="createResourceForm" enctype="multipart/form-data">
					<fieldset>
						{% for (var i=0, field; field=o.fields[i]; i++) { %}
							{%
							if (field.type === undefined) {
								field.type = "text";
							}
							if (field.type == 'checkbox' || field.type == 'radio') {
							%}
								<div class="form-group">
									<label>
										<input
											style="margin: 0 5px;"
											name="{%=field.name%}"
											type="{%=field.type%}"
											required="required"
											{% if (field.checked) { %}checked="checked"{% } %}
											autocomplete="off">
										{%=field.label%}
									</label>
							{% } else { %}
								<div class="form-group">
									<label for="addedtypeinput">{%=field.label%}</label>
									<input id="addedtypeinput" class="form-control" name="{%=field.name%}" type="{%=field.type%}" required="required" autocomplete="off" />
							{% } %}
							{% if (field.hint) { %}
									<span class="help-block">{%=field.hint%}</span>
							{% } %}
								</div>
						{% } %}
					</fieldset>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary" data-dismiss="modal" onclick="addResourceInstance();">Add</button>
			</div>
		</div>
	</div>
</div>
</script>

<div id="mainContent">


<jsp:invoke fragment="libs"></jsp:invoke>

<div class="${cssClass}">
<jsp:doBody/>
</div>

</div>
</div>

</body>
</html>


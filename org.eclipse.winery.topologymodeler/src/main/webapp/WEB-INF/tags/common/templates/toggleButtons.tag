<%--
/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Karoline Saatkamp - adapted for TargetLocation
 *******************************************************************************/
--%>
<%@tag description="Toggle buttons for visual appearance" pageEncoding="UTF-8"%>
<script>
var JQUERY_ANIMATION_DURATION = 400;

function doShowOrHide(elements, showThem) {
	if (elements.length == 0) {
		// e.g., no properties defined
		return;
	}
	if (showThem) {
		elements.slideDown();
	} else {
		elements.slideUp();
	}
	window.setTimeout(function() {
		jsPlumb.repaint(elements.parent());
	}, JQUERY_ANIMATION_DURATION);
}

function showIds(cb) {
	var elements;
	if ($("div.NodeTemplateShape.selected").size() > 0) {
		elements = $("div.NodeTemplateShape.selected div.id.nodetemplate");
	} else {
		elements = $("div.NodeTemplateShape:visible div.id.nodetemplate");
	}

	if ($(cb).hasClass("active")) {
		elements.fadeOut();
	} else {
		elements.fadeIn();
	}
	// no repaint required as no nodes are moved
	// window.setTimeout(jsPlumb.repaintEverything, JQUERY_ANIMATION_DURATION);
}

/**
 * Toogles visiblity of both node types and relationship types
 */
function showTypes(showThem) {
	// types at node templates
	var elements;
	var typesOfRelationshipTemplates;
	if ($("div.NodeTemplateShape.selected").size() > 0) {
		elements = $("div.NodeTemplateShape.selected div.type.nodetemplate");
		// TODO: We should put into typesOfRelationshipTemplates all type divs of relationshiptemplates connecting highlighted node templates
		//       This should be done when doing the multiselect
		//       And there should be a second if similar to the node templates for relationship templates
		typesOfRelationshipTemplates = $(".todo");
	} else {
		elements = $("div.NodeTemplateShape:visible div.type.nodetemplate");
		// TODO: we should check for a single relationship template being selected
		typesOfRelationshipTemplates = $(".relationshipTypeLabel");
	}

	if (showThem) {
		elements.fadeIn();
		typesOfRelationshipTemplates.fadeIn();
	} else {
		elements.fadeOut();
		typesOfRelationshipTemplates.fadeOut();
	}

	// no repaint required as no nodes are moved
	// window.setTimeout(jsPlumb.repaintEverything, JQUERY_ANIMATION_DURATION);
}

function showOrHideProperties(showThem) {
	var elements;
	if ($("div.NodeTemplateShape.selected").size() > 0) {
		elements = $("div.NodeTemplateShape.selected > div.propertiesContainer");
	} else {
		elements = $("div.NodeTemplateShape:visible > div.propertiesContainer");
	}
	doShowOrHide(elements, showThem);
}


function showOrHideDeploymentArtifacts(showThem) {
	var elements;
	if ($("div.NodeTemplateShape.selected").size() > 0) {
		elements = $("div.NodeTemplateShape:visible.selected > div.deploymentArtifactsContainer");
	} else {
		elements = $("div.NodeTemplateShape:visible > div.deploymentArtifactsContainer");
	}
	doShowOrHide(elements, showThem);
}

function showOrHideReqCaps(showThem) {
	var elements;
	if ($("div.NodeTemplateShape.selected").size() > 0) {
		elements = $("div.NodeTemplateShape:visible.selected > div.requirementsContainer, div.NodeTemplateShape:visible.selected > div.capabilitiesContainer");
	} else {
		elements = $("div.NodeTemplateShape:visible > div.requirementsContainer, div.NodeTemplateShape:visible > div.capabilitiesContainer");
	}
	doShowOrHide(elements, showThem);
}

function showOrHidePolicies(showThem) {
	var elements;
	if ($("div.NodeTemplateShape.selected").size() > 0) {
		elements = $("div.NodeTemplateShape:visible.selected > div.policiesContainer");
	} else {
		elements = $("div.NodeTemplateShape:visible > div.policiesContainer");
	}
	doShowOrHide(elements, showThem);
}

function showOrHideTargetLocations(showThem) {
	var elements;
	if ($("div.NodeTemplateShape.selected").size() > 0) {
		elements = $("div.NodeTemplateShape:visible.selected > div.targetLocationContainer");
	} else {
		elements = $("div.NodeTemplateShape:visible > div.targetLocationContainer");
	}
	doShowOrHide(elements, showThem);
}
</script>

<div class="btn-group" data-toggle="buttons-checkbox" id="toggleButtons">
	<button class="btn btn-default" id="toggleIdVisibility" onclick="showIds(this);">Ids</button>
	<button class="btn active" id="toggleTypeVisibility" onclick="showTypes(!$(this).hasClass('active'));">Types</button>
	<button class="btn btn-default" id="togglePropertiesVisibility" onclick="showOrHideProperties(!$(this).hasClass('active'));">Properties</button>
	<button class="btn btn-default" id="toggleDeploymentArtifactsVisibility" onclick="showOrHideDeploymentArtifacts(!$(this).hasClass('active'));">Deployment Artifacts</button>
	<button class="btn btn-default" id="toggleReqCapsVisibility" onclick="showOrHideReqCaps(!$(this).hasClass('active'));">Requirements &amp; Capabilities</button>
	<button class="btn btn-default" id="PoliciesVisibility" onclick="showOrHidePolicies(!$(this).hasClass('active'));">Policies</button>
	<button class="btn btn-default" id="TargetLocationsVisibility" onclick="showOrHideTargetLocations(!$(this).hasClass('active'));">Target Locations</button>
</div>


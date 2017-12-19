/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Yves Schubert - switch to bootstrap 3
 *    Karoline Saatkamp - add split functionality
 *******************************************************************************/

/**
 * Duplicate implementation of winrey-common.js: getNamespaceAndLocalNameFromQName
 *
 * @param qnameStr A QNAme string in the form {namespace}localName
 * @returns {{namespace: string, localName: string}}
 */
function getQName(qnameStr) {
	var pos = qnameStr.indexOf("}");
	var namespace = qnameStr.substring(1, pos);
	var localName = qnameStr.substring(pos+1);
	var res = {
			"namespace": namespace,
			"localName": localName
	};
	return res;
}

/**
 * @param attributeName an attribute with a value in the form prefix:localname
 * @param xmlElement a DOM element (offering the method lookupNamespaceURI)
 * @return { ns: namespace, id: id }
 */
function getNSAndId(attributeName, xmlElement) {
	var attributeValue = xmlElement.getAttribute(attributeName);
	var i = attributeValue.indexOf(":");
	var prefix = attributeValue.substring(0, i);
	var localName = attributeValue.substring(i+1);
	var ns = xmlElement.lookupNamespaceURI(prefix);
	var res = {
		ns : ns,
		id: localName
	};
	return res;
}

/**
 * @param el a href element
 * @param pathComponent the path element "artifacttemplates" or "artifacttypes"
 * @param attributeName the name of the attribute to read from the given xmlElement
 * @param xmlElement used to resolve a namespace prefix to a full namespace URI
 */
function addHref(el, pathComponent, attributeName, xmlElement) {
	var nsAndId = getNSAndId(attributeName, xmlElement);
	var loc = winery.uiURL + "/" + pathComponent + "/" + encodeID(nsAndId.ns) + "/" + encodeID(nsAndId.id);
	el.attr("href", loc);
}

var currentlySelectedDeploymentArtifactDiv;

/**
 * Sets global variables currentlySelectedNodeTemplate and currentlySelectedDeploymentArtifactDiv
 */
function showDeploymentArtifactInformation(nodeTemplateId, deploymentArtifactName) {
	currentlySelectedNodeTemplate = nodeTemplateId;
	var daDiv = $("#" + nodeTemplateId).children("div.deploymentArtifactsContainer").children("div.content").children("div.deploymentArtifact").children("div.name:contains(" + deploymentArtifactName + ")").parent();
	currentlySelectedDeploymentArtifactDiv = daDiv;
	var xml = daDiv.children("textarea").val();

	// get values to display directly from the "UI" instead of parsing the XML and asking the server for appropriate names
	var daArtifactTemplateName = daDiv.children("div.artifactTemplate").text();
	var daArtifactTypeName = daDiv.children("div.artifactType").text();

	// determine URLs
	require(["winery-support-common"], function(wsc) {
		xmlDoc = wsc.getDocument(xml);
		da = xmlDoc.firstChild;

		$("#DAname").text(deploymentArtifactName);

		$("#DAArtifactType").text(daArtifactTypeName);
		addHref($("#DAArtifactType"), "artifacttypes", "artifactType", da);

		var at = $("#DAArtifactTemplate");
		if (daArtifactTemplateName != "") {
			at.text(daArtifactTemplateName);
			addHref(at, "artifacttemplates", "artifactRef", da);
		} else {
			at.text("No template associated");
			at.removeAttr("href");
		}

		$("#DAXML").val(xml);

		$("#DeploymentArtifactInfo").modal("show");
	});
}

/**
 * Adds the given data to the deployment artifacts table of the currently active node template
 *
 * @param xmlAsDOM XML DOM document, TDeploymentArtifact. Produced by org.eclipse.winery.resources.artifacts.GenericArtifactsResource.onPost(String, String, String, String, String, String, String, String)
 * @param xmlAsString
 */
function addDeploymentArtifact(xmlAsDOM, xmlAsString) {
	var da = xmlAsDOM.firstChild;
	var daName = da.getAttribute("name");

	// we do NOT extract artifactType / artifactTemplate from the XML, but use the user input
	// showDeploymentArtifactInformation will extract its data directly from the XML without querying some input at the other HTML elements
	var daArtifactTemplateName = $("#artifactTemplateName").val();
	var daArtifactTypeName = $("#artifactType option:selected").text();

	// add information to node template shape
	var daData = {
		nodeTemplateId : currentlySelectedNodeTemplate,
		name : daName,
		xml : xmlAsString,
		artifactTypeName: daArtifactTypeName
	};
	if (daArtifactTemplateName != "") {
		daData.artifactTemplateName = daArtifactTemplateName;
	}
	addDeploymentArtifactInfoToNodeTemplate(daData);
}

function addDeploymentArtifactInfoToNodeTemplate(daData) {
	require(["tmpl"], function(tmpl){
		var data = tmpl("tmpl-deploymentArtifact", daData);
		var element = $("#" + currentlySelectedNodeTemplate).children(".deploymentArtifactsContainer").children(".content").children(".addDA:first");
		element.before(data);
	});
}

/**
 * This function directly accesses the fields of the dialog, because the return value of the server is XML and we do not want to parse XML
 *
 * @param artifactInfo = {name, interfaceName (may be undefined), operationName  (may be undefined), artifactTemplate (QName, may be undefined), artifactType}
 */
function artifactAddedSuccessfully(artifactInfo) {
	var typeNsAndId = getNamespaceAndLocalNameFromQName(artifactInfo.artifactType);
	var artifactTemplateNSAndId;
	if (artifactInfo.artifactTemplate) {
		artifactTemplateNSAndId = getNamespaceAndLocalNameFromQName(artifactInfo.artifactTemplate);
	} else {
		artifactTemplateNSAndId = undefined;
	}

	var daData = {
		nodeTemplateId : currentlySelectedNodeTemplate,
		name : artifactInfo.name,
		artifactTypeName: typeNsAndId.localname,
		artifactTypeNSAndId: typeNsAndId,
		artifactTemplateName: artifactInfo.artifactTemplateName,
		artifactTemplateNSAndId: artifactTemplateNSAndId
	};
	require(["tmpl"], function(tmpl){
		daData.xml = tmpl("tmpl-deploymentArtifactXML", daData);
		addDeploymentArtifactInfoToNodeTemplate(daData);
	});
}

// variables used for creation of deployment artifacts
var artifactTemplateAutoCreationEnabled = true;
var syncDAnameWithATname;

// introduced by the handling of deployment and implementation artifacts
// holds the ID only (!)
var currentlySelectedNodeTemplate;

/**
 * FIXME: this function is not updated to the the new dialog design and not included any more
 *
 * It should be used if the checkbox for at creation changes its checked status or if the at name is not valid
 *
 */
function updateArtifactTemplateCreationEnablement(value) {
	// remove field highlights
	// (currently, no intelligent removal and addition is made)
	$("#artifactName").removeClass("highlight");
	$("#artifactTemplateName").removeClass("highlight");

	if (value) {
		// enable it
		artifactTemplateAutoCreationEnabled = true;
		$("#artifactTemplateName").removeAttr("disabled");
		$("#artifactTemplateNS").removeAttr("disabled");
		$("#createWithoutFilesBtn").attr("disabled", "disabled");
		$("#createWithFilesBtn").removeAttr("disabled");
	} else {
		// disable it
		artifactTemplateAutoCreationEnabled = false;
		$("#artifactTemplateName").attr("disabled", "disabled");
		$("#artifactTemplateNS").attr("disabled", "disabled");
		$("#createWithoutFilesBtn").removeAttr("disabled");
		$("#createWithFilesBtn").attr("disabled", "disabled");
	}
}

function isShownNodeTemplateShapeChangeBoxes(shape) {
	return (shape.find(".endpointContainer").is(":visible"));
}

/**
 * @param shape jQuery object
 */
function showNodeTemplateShapeChangeBoxes(shape) {
	shape.find(".addDA").show();
	shape.children(".endpointContainer").show();
	shape.find(".addnewreqorcap").show();
	shape.find(".addnewpolicy").show();
}

/**
 * @param shape jQuery object
 */
function hideNodeTemplateShapeChangeBoxes(shape) {
	shape.find(".addDA").hide();
	shape.children(".endpointContainer").hide();
	shape.find(".addnewreqorcap").hide();
	shape.find(".addnewpolicy").hide();
}

// indicates if a connection is currently drawn
// used to decide whether the node template boxes should be displayed
var isInConnectionMode = false;

function wineryMoveSelectedNodeTemplateShapes(dX, dY) {
	var shapes = $("div.NodeTemplateShape.selected");
	hideNodeTemplateShapeChangeBoxes(shapes);
	shapes.each(function(i, nodeTemplate) {
		nodeTemplate = $(nodeTemplate);
		var offset = nodeTemplate.offset();
		offset.left += dX;
		offset.top += dY;
		nodeTemplate.offset(offset);
	});
	jsPlumb.repaint(shapes);
}


/**
 * Simple eventing framework
 *
 * use
 * winery.events.register(name, function) to register on an event
 * and
 * winery.events.fire(name) to fire all registered functions
 */

winery = {};
winery.events = {
	_events : {},

	/**
	 * Registers a function
	 *
	 * @return the registered function
	 */
	register : function(eventName, f) {
		if (!winery.events._events[eventName]) {
			winery.events._events[eventName] = {};
		}
		winery.events._events[eventName][f] = f;
		return f;
	},

	/**
	 * Fires all functions associated with the given event name
	 */
	fire : function(eventName) {
		if (winery.events._events[eventName]) {
			$.each(winery.events._events[eventName], function(index, value) {
				value();
			});
		}
		return true;
	}
};

/**
 * Determines whether a key combo is allowed.
 *
 * For instance, when a modal dialog is opened or a input is selected, DEL should not delete node template shapes
 */
function keyComboAllowed() {
	return ((!$(document.activeElement).is("input")) && ($("div.modal:visible").size() == 0));
}

function keyComboAllowedAndNodeTemplatesSelected() {
	return (keyComboAllowed() && ($("div.NodeTemplateShape.selected").size() != 0));
}




/* list of event names */
winery.events.name = {};
winery.events.name.command = {};

winery.events.name.command.IMPORT_TOPOLOGY = "importTopology";

winery.events.name.SELECTION_CHANGED = "selectionchanged";
winery.events.name.command.SELECT_ALL_NODETEMPLATES = "selectAllNodeTemplates";
winery.events.name.command.UNSELECT_ALL_NODETEMPLATES = "unselectAllNodeTemplates";
winery.events.name.command.DELETE_SELECTION = "deleteSelection";
winery.events.name.VISUALIZE = "visualize";

winery.events.name.command.MOVE_DOWN = "moveDown";
winery.events.name.command.MOVE_UP = "moveUp";
winery.events.name.command.MOVE_LEFT = "moveLeft";
winery.events.name.command.MOVE_RIGHT = "moveRight";

winery.events.name.command.SAVE = "save";
winery.events.name.command.SPLIT = "split";
winery.events.name.command.MATCH = "match";
winery.events.name.command.PATTERN_DETECTION = "detectPattern";
winery.events.name.command.VISUALIZE_PATTERNS = "visualizePatterns";
winery.events.name.command.HIGHLIGHT_PATTERN = "highlightPattern";

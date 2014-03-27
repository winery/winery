/*******************************************************************************
 * Copyright (c) 2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation
 *    Tobias Binz - communication with the parent window
 *******************************************************************************/

 /**
  * Called from the renderer as soon as the whole topology is loaded
  */
 function wineryViewExternalScriptOnLoad() {

	function getIdOfNodeTemplateShape(element) {
		var nodeTemplate = element.closest("div.NodeTemplateShape");
		var id = nodeTemplate.children("div.headerContainer").children("div.id").text();
		return id;
	}

	jsPlumb.bind("ready", function() {
		jsPlumb.bind("click", function(conn, originalEvent) {
			var id = winery.connections[conn.id].id;
			var message = {
				targetRelationshipTemplateRef: id
			}
			sendMessage(message);
		});
	});


	$("div.NodeTemplateShape").on("click", function(e) {
		var id = getIdOfNodeTemplateShape($(e.target));
		// send id and empty property as no property has been clicked
		var message = {
			targetObjectRef: id,
			targetPropertyRef: ""
		};
		sendMessage(message);

		return false;
	});

	$("tr.KVProperty").on("click", function(e) {
		var trKVProperty = $(e.target).closest("tr.KVProperty");
		var key = trKVProperty.children("td").children("span.KVPropertyKey").text();

		var content = trKVProperty.closest("div.content");
		var elementName = content.children("span.elementName").text();

		// form namespace-unaware XPath
		var xpath = "/*[local-name()='" + elementName + "']/*[local-name()='" + key + "']";

		var message = {
			targetPropertyRef: xpath,
			targetObjectRef: getIdOfNodeTemplateShape(trKVProperty)
		};
		sendMessage(message);

		// do not trigger click on NodeTemplateShape -> we included both values in the message
		return false;
	});

	$("div.requirements").on("click", function(e) {
		var reqorcap = $(e.target).closest("div.requirements");
		var id = reqorcap.children("div.id").text();

		var message = {
			reqRef: id
		};
		sendMessage(message);

		return false;
	});

	$("div.capabilities").on("click", function(e) {
		var reqorcap = $(e.target).closest("div.capabilities");
		var id = reqorcap.children("div.id").text();

		var message = {
			capRef: id
		};
		sendMessage(message);

		return false;
	});

 }

function sendMessage(message) {
	window.parent.postMessage(message, "*");
}

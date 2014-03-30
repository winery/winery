/*******************************************************************************
 * Copyright (c) 2012-2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/

/**
 * This file contains supporting functions for the topoplogy modeler
 */
define(
	// although XMLWriter ist not an AMD module, requirejs does not complain when loading it
	["winery-support-common", "XMLWriter"],
	function (w) {
		// has to be consistent with {@link org.eclipse.winery.common.constants.Namespaces}
		var TOSCA_NAMESPACE = "http://docs.oasis-open.org/tosca/ns/2011/12";
		var TOSCA_WINERY_EXTENSIONS_NAMESPACE ="http://www.opentosca.org/winery/extensions/tosca/2013/02/12";

		var topologyTemplateURL;

		var module = {
			save: save,
			setTopologyTemplateURL: function(url) {
				topologyTemplateURL = url;
			},
			getTopologyTemplateAsXML: getTopologyTemplateAsXML,

			TOSCA_NAMESPACE: TOSCA_NAMESPACE,
			TOSCA_WINERY_EXTENSIONS_NAMESPACE: TOSCA_WINERY_EXTENSIONS_NAMESPACE
		};
		return module;

		function writeReqOrCaps(elements, xmlw, globalWrapperElementName, singleElementWrapperName) {
			if (elements.length != 0) {
				xmlw.writeStartElement(globalWrapperElementName);

				$.each(elements, function(i,e) {
					xmlw.writeStartElement(singleElementWrapperName);
					e = $(e);
					xmlw.writeAttributeString("id",   e.children(".id").text());
					xmlw.writeAttributeString("name", e.children(".name").text());
					writeType(xmlw, e.children(".type").children("a").data("qname"));
					savePropertiesFromDivToXMLWriter(e.children("div.propertiesContainer"), xmlw);
					xmlw.writeEndElement();
				});

				xmlw.writeEndElement();
			}

		}

		/**
		 * "doSave"
		 */
		function save() {
			$("#saveBtn").button('loading');

			$.ajax({
				url: topologyTemplateURL,
				type: "PUT",
				contentType: 'text/xml',
				data: getTopologyTemplateAsXML(false),
				success: function(data, textStatus, jqXHR) {
					$("#saveBtn").button('reset');
					vShowSuccess("successfully saved.");
				},
				error: function(jqXHR, textStatus, errorThrown) {
					$("#saveBtn").button('reset');
					vShowAJAXError("Could not save", jqXHR, errorThrown);
				}
			});
		}
		
		/**
		 * Creates an XML String of the modelled topology template.
		 */
		function getTopologyTemplateAsXML(needsDefinitionsTag) {

			var xmlw = new XMLWriter("utf-8");
			xmlw.writeStartDocument();

			if (needsDefinitionsTag) {
				xmlw.writeStartElement("Definitions");
				xmlw.writeAttributeString("xmlns", TOSCA_NAMESPACE);
				xmlw.writeAttributeString("xmlns:winery", TOSCA_WINERY_EXTENSIONS_NAMESPACE);
                
				xmlw.writeStartElement("ServiceTemplate");
				xmlw.writeAttributeString("xmlns", TOSCA_NAMESPACE);
				xmlw.writeAttributeString("xmlns:winery", TOSCA_WINERY_EXTENSIONS_NAMESPACE);
			}
						xmlw.writeStartElement("TopologyTemplate");
			xmlw.writeAttributeString("xmlns", TOSCA_NAMESPACE);
			xmlw.writeAttributeString("xmlns:winery", TOSCA_WINERY_EXTENSIONS_NAMESPACE);
			$("div.NodeTemplateShape").not(".hidden").each (function() {
				xmlw.writeStartElement("NodeTemplate");

				var id = $(this).attr("id");

				var headerContainer = $(this).children("div.headerContainer");
				var name = headerContainer.children("div.name").text();
				var typeQNameStr = headerContainer.children("span.typeQName").text();
				var minmaxdiv = headerContainer.children("div.minMaxInstances");
				var min = minmaxdiv.children("span.minInstances").text();
				var max = minmaxdiv.children("span.maxInstances").text();
				if (max == "∞") {
					max = "unbounded";
				}
				var x = $(this).css("left");
				x = x.substring(0, x.indexOf("px"));
				var y = $(this).css("top");
				y = y.substring(0, y.indexOf("px"));

				xmlw.writeAttributeString("id", id);
				if (name != "") {
					xmlw.writeAttributeString("name", name);
				}
				writeType(xmlw, typeQNameStr);
				if (min != "") {
					xmlw.writeAttributeString("minInstances", min);
				}
				if (max != "") {
					xmlw.writeAttributeString("maxInstances", max);
				}
				xmlw.writeAttributeString("winery:x", x);
				xmlw.writeAttributeString("winery:y", y);

				/** Properties **/
				savePropertiesFromDivToXMLWriter($(this).children("div.propertiesContainer"), xmlw);

				/** Requirements **/
				writeReqOrCaps(
					$(this).children("div.requirementsContainer").children("div.content").children("div.reqorcap"),
					xmlw,
					"Requirements",
					"Requirement");

				/** Capabilities **/
				writeReqOrCaps(
					$(this).children("div.capabilitiesContainer").children("div.content").children("div.reqorcap"),
					xmlw,
					"Capabilities",
					"Capability");

				/** Policies **/
				w.writeCollectionDefinedByATextArea(xmlw,
						$(this).children("div.policiesContainer").children("div.content").children("div.policy"),
						"Policies");

				/** Deployment Artifacts **/
				// DAs do not store all data in the HTML, a global JavaScript variable is also used
				var das = $(this).children("div.deploymentArtifactsContainer").children("div.content").children("div.deploymentArtifact");
				if (das.length != 0) {
					xmlw.writeStartElement("DeploymentArtifacts");
					das.each(function(i,e) {
						// the textarea contains a valid deployment artifact xml
						var xml = $(e).children("textarea").val();
						xmlw.writeXML(xml);
					});
					xmlw.writeEndElement();
				}

				// End: Nodetemplate
				xmlw.writeEndElement();
			});
			jsPlumb.select().each(function(connection) {
				xmlw.writeStartElement("RelationshipTemplate");
				var id = connection.id;
				var typeQNameStr = connection.getType()[0];

				var connData = winery.connections[id];
				if (!connData) {
					vShowError("Error in the internal data structure: Id " + id + " not found");
					return;
				}

				xmlw.writeAttributeString("id", connData.id);
				if (connData.name != "") {
					xmlw.writeAttributeString("name", connData.name);
				}
				writeType(xmlw, typeQNameStr);

				if (typeof connData.propertiesContainer !== "undefined") {
					savePropertiesFromDivToXMLWriter(connData.propertiesContainer, xmlw);
				}

				xmlw.writeStartElement("SourceElement");
				if (connData.req) {
					// conn starts at a requirement
					xmlw.writeAttributeString("ref", connData.req);
				} else {
					// conn starts at a node template
					xmlw.writeAttributeString("ref", connection.sourceId);
				}
				xmlw.writeEndElement();
				xmlw.writeStartElement("TargetElement");
				if (connData.cap) {
					// conn ends at a capability
					xmlw.writeAttributeString("ref", connData.cap);
				} else {
					// conn ends at a node template
					xmlw.writeAttributeString("ref", connection.targetId);
				}
				xmlw.writeEndElement();

				xmlw.writeEndElement();
			});
			
			if (needsDefinitionsTag) {
				xmlw.writeEndElement();
				xmlw.writeEndElement();
			}
			
			xmlw.writeEndDocument();

			return xmlw.flush();
		}

		function writeQNameAttribute(w, nsPrefix, qnameStr) {
			var qname = getQName(qnameStr);
			w.writeAttributeString("xmlns:" + nsPrefix, qname.namespace);
			w.writeAttributeString("type", nsPrefix + ":" + qname.localName);
		}

		function writeType(w, typeQNameStr) {
			writeQNameAttribute(w, "ty", typeQNameStr);
		}

	}
);


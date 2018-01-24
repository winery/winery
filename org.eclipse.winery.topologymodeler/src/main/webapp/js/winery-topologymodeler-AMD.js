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
 *    Karoline Saatkamp - add split functionality
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
		var patternId;

		return {
			openChooseTopologyToImportDiag: openChooseTopologyToImportDiag,
			importTopology: importTopology,
			save: save,
			split: split,
			detectPattern: detectPattern,
			visualizePatterns: visualizePatterns,
			match: match,
			setTopologyTemplateURL: function (url) {
				topologyTemplateURL = url;
			},
			highlightPattern: highlightPattern,
			getTopologyTemplateAsXML: getTopologyTemplateAsXML,

			TOSCA_NAMESPACE: TOSCA_NAMESPACE,
			TOSCA_WINERY_EXTENSIONS_NAMESPACE: TOSCA_WINERY_EXTENSIONS_NAMESPACE
		};

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

		function openChooseTopologyToImportDiag() {
			$("#chooseTopologyToImportDiag").modal("show");
		}

		function importTopology(urlPrefix, serviceTemplateQName) {
			$("#importButon").button("loading");
			$.ajax({
				url: topologyTemplateURL + "merge",
				type: "POST",
				contentType: 'text/plain',
				data: serviceTemplateQName,
				success: function(data, textStatus, jqXHR) {
					$("#importButon").button("reset");
					vShowSuccess("successfully saved. Reloading page...");
					window.location.reload(true);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					$("#importButon").button("reset");
					vShowAJAXError("Could not import", jqXHR, errorThrown);
				}
			});

			// currently does not work as we do not support rendering based on JSON data

			// var topologyTemplateURL = urlPrefix + w.getURLFragmentOutOfFullQName(serviceTemplateQName) + "/" + "topologytemplate/";
			// $.getJSON(topologyTemplateURL, function(topologyTemplate) {
			// 	console.log(topologyTemplate);
			// });
		}

		/**
		 * "doSave"
		 */
		function save() {
			$("#saveBtn").button("loading");

			$.ajax({
				url: topologyTemplateURL,
				type: "PUT",
				contentType: 'text/xml',
				data: getTopologyTemplateAsXML(false),
				success: function(data, textStatus, jqXHR) {
					$("#saveBtn").button("reset");
					vShowSuccess("successfully saved.");
				},
				error: function(jqXHR, textStatus, errorThrown) {
					$("#saveBtn").button("reset");
					vShowAJAXError("Could not save", jqXHR, errorThrown);
				}
			});
		}

		/**
		 * "doSplit"
		 */
		function split() {
			$("#splitBtn").button("loading");

			$.ajax({
				url: topologyTemplateURL + 'split',
				type: "POST",
				success: function(data, textStatus, jqXHR) {
					$("#splitBtn").button("reset");
					var location = jqXHR.getResponseHeader("Location");
					vShowSuccess("Successfully split. <a target=\"_blank\" href=\"" + location + "\">Open split service template</a>");
				},
				error: function(jqXHR, textStatus, errorThrown) {
					$("#splitBtn").button("reset");
					vShowAJAXError("Could not split", jqXHR, errorThrown);
				}
			});
		}

		/**
		 * "doMatch"
		 */
		function match() {
			$("#matchBtn").button("loading");

			$.ajax({
				url: topologyTemplateURL + 'match',
				type: "POST",
				success: function(data, textStatus, jqXHR) {
					$("#matchBtn").button("reset");
					var location = jqXHR.getResponseHeader("Location");
					vShowSuccess("Successfully matched. <a target=\"_blank\" href=\"" + location + "\">Open matched service template</a>");
				},
				error: function(jqXHR, textStatus, errorThrown) {
					$("#matchBtn").button("reset");
					vShowAJAXError("Could not match", jqXHR, errorThrown);
				}
			});
		}


		/**
		 * "detectPattern"
		 */
		function detectPattern() {
			$("#patterndetectionBtn").button("loading");

			$.ajax({
				url: topologyTemplateURL + "patterndetection",
				type: "POST",
				success: function(data, textStatus, jqXHR) {
					$("#patterndetectionBtn").button("reset");
					//var location = jqXHR.getResponseHeader("Location");
					vShowSuccess("Algorithm was successful.\n" + data);

				},
				error: function(jqXHR, textStatus, errorThrown) {
					$("#patterndetectionBtn").button("reset");
					vShowAJAXError("Could not detect Patterns");
				}
			});
		}

		/**
		 * "visualizePatterns"
		 */
		function visualizePatterns() {
			$("#visualizePatternBtn").button("loading");

			$.ajax({
				url: topologyTemplateURL + "visualizepatterns",
				type: "POST",
				success: function(data, textStatus, jqXHR) {
					$("#visualizePatternBtn").button("reset");
					addPatternButtons();

					function addPatternButtons() {
						var lines = data.split('\n');
						for (var i = 0; i < lines.length-1; i++) {
							var tmp = lines[i].split(':');
							var pattern = tmp[0];
							$("#patternList").append('<li><a href="#" onclick="highlightPattern(this.id)" id="' + pattern + '">'+pattern+'</a></li>');
						}
					};
				},
				error: function(jqXHR, textStatus, errorThrown) {
					$("#visualizePatternBtn").button("reset");
					vShowAJAXError("Could not visualize Patterns");
				}
			});
		}

		function highlightPattern(patternId) {
			console.log("PatternId: " + patternId);
			$("div.NodeTemplateShape.detected").each(function () {
				$(this).removeClass("detected");
			});
			$.ajax({
				url: topologyTemplateURL + "visualizepatterns",
				type: "POST",
				success: function(data, textStatus, jqXHR) {
					var lines = data.split('\n');
					for (var i = 0; i < lines.length - 1; i++) {
						var tmp = lines[i].split(':');
						var patternName = tmp[0];
						if (patternName === patternId) {
							var nodes = tmp[1].split(',');
							for (var j = 0; j < nodes.length; j++) {
								$("div.NodeTemplateShape").each(function () {
									if (nodes[j] === $(this).attr("id")) {
										$(this).addClass("detected");
									}
								});
							}
						}
					}
				},
				error: function(jqXHR, textStatus, errorThrown) {
					$("#visualizePatternBtn").button("reset");
					vShowAJAXError("Could not visualize Patterns");
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
				var targetLocation = $(this).find("div.targetLocationContainer > div.content > .thetargetlocation").editable("getValue").undefined;
				if (targetLocation !== "") {
					xmlw.writeAttributeString("winery:location", targetLocation);
				}

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


/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
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
 * This file contains supporting functions for the rendering a topology template
 */
define(
	["jsplumb", "winery-support-common"],
	function (globdefa, wsc) {
		var readOnly = false;

		var module = {
			initNodeTemplate: initNodeTemplate,
			handleConnectionCreated: handleConnectionCreated,
			imageError: imageError,
			setReadOnly: setReadOnly
		};

		return module;

		/**
		 * @param nodeTemplateShape the set of node template shapes to initialize
		 * @param makeDraggable true if the nodeTemplates should be made draggable
		 */
		function initNodeTemplate(nodeTemplateShapeSet, makeDraggable) {
			if (makeDraggable) {
				jsPlumb.draggable(nodeTemplateShapeSet);
			}
			jsPlumb.makeTarget(nodeTemplateShapeSet, {
				anchor:"Continuous",
				endpoint:"Blank"
			});

			// this function is defined in index.jsp via jsp functions
			// as it depends on the available relationship types
			createConnectorEndpoints(nodeTemplateShapeSet);

			nodeTemplateShapeSet.addClass("layoutableComponent");

			nodeTemplateShapeSet.each(function(idx, s) {
				var shape = $(s);

				var id = shape.attr("id");

				// KV Properties
				var props = shape.children(".propertiesContainer")
					.children(".content")
					.children("table")
					.children("tbody");
				if (!readOnly) {
					props.find(".KVPropertyValue").editable();
				}

				// Deployment Artifacts
				var fu = shape.children(".deploymentArtifactsContainer")
					.children(".content")
					.children(".addnewartifacttemplate")
					.children(".fileupload");
				fu.attr("data-url", "nodetemplates/" + wsc.encodeId(id) + "/deploymentartifacts/");
			});

		//	nodeTemplateShapeSet.children(".deploymentArtifactsContainer").children(".content").children(".deploymentArtifact").each(function(index, e) {
		//      addnewfileoverlay could be added here
		//		$(this).
		//	});
		}

		/**
		 * Handles the creation of connections by jsPlumb
		 *
		 * Also called if connection is created during loading
		 */
		function handleConnectionCreated(data) {
			// might be called directly from here or by the event
			// if called by jsPlumb infrastructure, we have to get rid of the surrounding element
			var conn;
			if (data.connection) {
				conn = data.connection;
			} else {
				conn = data;
			}
			winery.debugConnData = conn;

			var id = conn.id;
			winery.connections[id] = {
				// we store the id to have a default for the id
				id: id,
				// and use it also as starting point of a name
				name: id,
				// we do NOT copy the plain type here
				// type is stored in the connection
				// type: .getType()[0]
				// BUT: we copy the detailed ns and id
				nsAndLocalName: wsc.getNamespaceAndLocalNameFromQName(conn.getType()[0])
			};
			putToolTipInfo(conn);

			// we have to manually show and hide the tooltips as Bootstrap's tooltip plugin does not work after a connection was highlighted.
			conn.bind("mouseenter", function(conn,e) {
				putToolTipInfo(conn);
			});
			conn.bind("mouseexit", function(conn,e) {
				$("div.tooltip").remove();
				// we have to replace the tooltip as
				putToolTipInfo(conn);
			});
		}

		function putToolTipInfo(conn) {
			// add tooltip showing the relationship type
			var svgElement = $(conn.canvas);
			// the title attribute is shown in the tooltip
			// set the relationship type as tooltip
			// we show the localname only
			var nsAndLocalName = winery.connections[conn.id].nsAndLocalName;
			// Vino4TOSCA: type in brackets
			var title = "(" + nsAndLocalName.localname + ")";
			svgElement.tooltip({title: title});
		}

		/**
		 * Removes the image from the display. Used at images which could not be loaded
		 *
		 * Used via {@code <img onerror="imageError(this);" ... />}
		 */
		function imageError(image) {
			image.onError="";
			image.style.visibility = "hidden";
		}

		function setReadOnly() {
			readOnly = true;
		}
	}
);
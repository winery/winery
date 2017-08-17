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
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
--%>
<%@tag description="Has to be included once before ussage of properties.tag. Used in topology modeler and in properties.jsp of entitytemplates" pageEncoding="UTF-8"%>

<%@tag import="org.eclipse.winery.common.constants.Namespaces" %>

<%@taglib prefix="o"  tagdir="/WEB-INF/tags/common/orioneditor"%>

<div class="modal fade" id="PropertyXMLDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Property</h4>
			</div>
			<div class="modal-body">
				<!--  embed the XML editor without a save button. We provide the save button by ourselves  -->
				<o:orioneditorarea areaid="PropertyXML" withoutsavebutton="true"/>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary" onclick="savePropertiesXMLChanges();">Save</button>
			</div>
		</div>
	</div>
</div>

<script>
// global variable set by editPropertiesXML and read by savePropertiesXMLChanges
var nodeTemplateIdsTextAreaPropertiesXMLEditing;

function editPropertiesXML(nodeTemplateId) {
	// code mirror does not update content if field not fully shown
	// therefore, we hook in into the "shown" event
	$("#PropertyXMLDiag").off("shown.bs.modal");
	$("#PropertyXMLDiag").on("shown.bs.modal", function() {
		nodeTemplateIdsTextAreaPropertiesXMLEditing = $("#" + nodeTemplateId).children(".propertiesContainer").children(".content").children("textarea");
		var val = nodeTemplateIdsTextAreaPropertiesXMLEditing.val();
		window.winery.orionareas["PropertyXML"].editor.setText(val);
		window.winery.orionareas["PropertyXML"].fixEditorHeight();
	});
	$("#PropertyXMLDiag").modal("show");
	nodeTemplateIdSelectedForPropertiesXMLEditing = nodeTemplateId;
}

function savePropertiesXMLChanges() {
	var val = window.winery.orionareas["PropertyXML"].editor.getText();
	nodeTemplateIdsTextAreaPropertiesXMLEditing.text(val);
	$("#PropertyXMLDiag").modal("hide");
}

/**
 * @param properties - the properties div
 * @param w - an XMLWriter
 */
function savePropertiesFromDivToXMLWriter(properties, w, writeNamespaceDeclaration) {
	if (properties.length != 0) {
		// properties exist
		var contentDiv = properties.children("div.content");
		var xmlProperties = contentDiv.children("textarea.properties_xml");
		if (xmlProperties.length == 0) {
			// K/V properties -> winery special: XSD defined at node type

			var elementName = contentDiv.children("span.elementName").text();
			var namespace = contentDiv.children("span.namespace").text();

			w.writeStartElement("Properties");
			if (writeNamespaceDeclaration){
				w.writeAttributeString("xmlns", "<%=Namespaces.TOSCA_NAMESPACE%>");
			}
			w.writeStartElement(elementName);
			w.writeAttributeString("xmlns", namespace);
			properties.children("div.content").children("table").children("tbody").children("tr").each(function() {
				var keyEl = $(this).children("td:first-child");
				var key = keyEl.text();
				var valueEl = keyEl.next().children("a");
				var value;
				if (valueEl.hasClass("editable-empty")) {
					value = "";
				} else {
					value = valueEl.text();
					value = value.replace(/\]\]>/g, "]]]]><![CDATA[>");
					value = "<![CDATA[" + value + "]]>";
				}
				w.writeStartElement(key);
				w.writeString(value);
				w.writeEndElement();
			});
			w.writeEndElement();
			// close "Properties"
			w.writeEndElement();
		} else {
			// just put the content of the XML field as child of the current node template
			// The sourrinding tag "properties" is included in the XML field
			var data = xmlProperties.text();
			w.writeXML(data);
		}
	}

}
</script>

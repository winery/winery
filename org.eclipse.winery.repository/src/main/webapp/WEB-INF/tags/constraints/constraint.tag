<%--
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
--%>
<%@tag description="Models editing a single constraint" pageEncoding="UTF-8"%>

<%@taglib prefix="o"  tagdir="/WEB-INF/tags/common/orioneditor"%>
<%@taglib prefix="t"  tagdir="/WEB-INF/tags"%>


<div class="modal fade" id="constraint-dialog">
	<div class="modal-dialog">
		<div class="modal-content" style="width:660px;">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Edit Constraint</h4>
			</div>
			<div class="modal-body">

			<form>
				<fieldset>
					<t:typeswithshortnameasselect label="Type" type="constrainttype" selectname="typenameinput" typesWithShortNames="<%=org.eclipse.winery.repository.resources.admin.types.ConstraintTypesManager.INSTANCE.getTypes()%>">
					</t:typeswithshortnameasselect>

					<o:orioneditorarea areaid="constrainttextarea" />
				</fieldset>
			</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				<%-- click functionality is set by editConstraint() --%>
				<button type="button" class="btn btn-primary" id="createConstraintBtn">Create</button>
				<button type="button" class="btn btn-primary" id="updateConstraintBtn">Update</button>
			</div>
		</div>
	</div>
</div>

<%--
Quick hack to get the representation of an empty constraint
TODO: A resource should provide the empty representation (?!)

The alternative is not to use the complete XML as the specification only allows the content of the XML wrapper to be modified.
If we use that method, this textarea is not required, but the sending of an existing constraint has to be modified to send only lines 2 to n-1, i.e., send without the wrapping lines.
This is not possible if the stored constraint is empty, then there are 2 lines only.
--%>
<textarea class="hidden" id="emptyconstraint">
<%=org.eclipse.winery.repository.Utils.getXMLAsString(new org.eclipse.winery.model.tosca.TConstraint())%>
</textarea>

<script>
/**
 * Shows an error if XML is invalid, calls onSuccess otherwise
 *
 * @param onSuccess Function called with XML String containging the constraint
 */
function getXMLOfConstraint(onSuccess) {
	// we have to weave in the type into the XML
	// The editor presents the complete XML as this is easier to implement
	// in comparison to select lines 3 to n-1 of the XML and then create a TConstraints element

	require(["winery-support-common"], function(wsc) {
		var txt = window.winery.orionareas["constrainttextarea"].editor.getText();
		var xmlDoc = wsc.checkXMLValidityAndShowErrorIfInvalid(txt);

		var type = document.createAttribute('constraintType');
		type.nodeValue = $("#typenameinput").val();
		xmlDoc.documentElement.attributes.setNamedItem(type);
		var xmlString = (new XMLSerializer()).serializeToString(xmlDoc);

		onSuccess(xmlString);
	});
}
</script>

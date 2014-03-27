<%--
/*******************************************************************************
 * Copyright (c) 2014 University of Stuttgart.
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

<%@tag pageEncoding="UTF-8"%>

<%@attribute name="XShort" description="The X to browse for. Short form. E.g., Req, Cap, ..." required="true" %>
<%@attribute name="XLong" description="The X to browse for. Long form. E.g., Requirement, Capability, ..." required="true" %>


<%-- Browse for property --%>
<div class="modal fade z1051" id="browseFor${XShort}Diag">
	<div class="modal-dialog" style="width:1000px;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Choose a ${XLong}</h4>
			</div>

			<div class="modal-body">
				<p class="text-info">Please click on the desired ${XLong}</p>
				<iframe id="topologyTemplatePreview" class="topologyTemplatePreviewSizing" src="topologytemplate/?view=${XShort}Selection&script=${pageContext.request.contextPath}/js/boundaryDefinitionsXSelection.js"></iframe>
				<form>
					<fieldset>
						<div class="form-group">
							<label for="${XShort}RefeferenceField">Reference to the ${XLong} in the topology template</label>
							<input type="text" id="${XShort}ReferenceField" class="form-control newObjectRef" />
						</div>
					</fieldset>
				</form>
			</div>

			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button type="button" class="btn btn-primary" onclick="set${XShort}Ref();">Set</button>
			</div>
		</div>
	</div>
</div>

<%--
/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
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
<%@tag description="Defines the javascript function createConnectorEndpoints globally. Quick hack to avoid huge hacking at the repository" pageEncoding="UTF-8"%>

<%@tag import="java.util.Collection"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>
<%@tag import="org.eclipse.winery.common.Util"%>

<%@attribute name="relationshipTypes" type="java.util.Collection" required="true" %>

<script>
function createConnectorEndpoints(nodeTemplateShapeSet) {
<%
	for (TRelationshipType relationshipType: (Collection<TRelationshipType>) relationshipTypes) {
%>
		nodeTemplateShapeSet.find(".<%=Util.makeCSSName(relationshipType.getTargetNamespace(), relationshipType.getName()) %>").each(function(i,e) {
			var p = $(e).parent();
			var grandparent = $(p).parent();

			jsPlumb.makeSource($(e), {
				parent:grandparent,
				anchor:"Continuous",
				connectionType: "{<%=relationshipType.getTargetNamespace()%>}<%=relationshipType.getName()%>",
				endpoint:"Blank"
			});
		});
<%
	}
%>
}
</script>

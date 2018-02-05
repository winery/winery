<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright (c) 2013 Contributors to the Eclipse Foundation
  ~
  ~ See the NOTICE file(s) distributed with this work for additional
  ~ information regarding copyright ownership.
  ~
  ~ This program and the accompanying materials are made available under the
  ~ terms of the Eclipse Public License 2.0 which is available at
  ~ http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
  ~ which is available at https://www.apache.org/licenses/LICENSE-2.0.
  ~
  ~ SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>
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

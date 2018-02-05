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
<%@tag description="Generates style element for node types and relationship types" pageEncoding="UTF-8" %>

<%@attribute name="nodeTypes" required="true" type="java.util.Collection" %>
<%@attribute name="relationshipTypes" required="true" type="java.util.Collection" %>

<%@tag import="java.util.Collection"%>
<%@tag import="javax.xml.namespace.QName"%>
<%@tag import="org.eclipse.winery.model.tosca.utils.ModelUtilities"%>
<%@tag import="org.eclipse.winery.common.Util"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeType"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>

<style>
<%
	for (TNodeType nt: (Collection<TNodeType>) nodeTypes) {
		String borderColor = ModelUtilities.getBorderColor(nt);
		String cssName = Util.makeCSSName(nt.getTargetNamespace(), nt.getName());
%>
		div.NodeTemplateShape.<%=cssName%> {
			border-color: <%=borderColor%>;
		}
<%
	}

	// relationship types CSS
	for (TRelationshipType rt: (Collection<TRelationshipType>) relationshipTypes) {
		String color = ModelUtilities.getColor(rt);
		QName qname = new QName(rt.getTargetNamespace(), rt.getName());
		String cssName = Util.makeCSSName(qname) + "_box";
%>
		div.<%=cssName%> {
			background: <%=color%>;
		}
<%
	}
%>
</style>

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
<%@tag description="Generates style element for node types and relationship types" pageEncoding="UTF-8" %>

<%@attribute name="nodeTypes" required="true" type="java.util.Collection" %>
<%@attribute name="relationshipTypes" required="true" type="java.util.Collection" %>

<%@tag import="java.util.Collection"%>
<%@tag import="javax.xml.namespace.QName"%>
<%@tag import="org.eclipse.winery.common.ModelUtilities"%>
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

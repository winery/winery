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
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.eclipse.org/winery/repository/functions" prefix="w" %>

<%@page import="org.eclipse.winery.repository.resources.SubMenuData"%>

<%
java.util.List<SubMenuData> subMenus = new java.util.ArrayList<>();

SubMenuData data;

data = new SubMenuData("#implementationartifacts", "Implementation Artifacts");
subMenus.add(data);
%>

<t:componentinstancewithNameDerivedFromAbstractFinal cssClass="relationshipTypeImplementation" selected="RelationshipTypeImplementation" subMenus="<%=subMenus%>" implementationFor="${w:nodeTypeQName2href(it.type)}">
</t:componentinstancewithNameDerivedFromAbstractFinal>

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
<%@page import="org.eclipse.winery.repository.resources.SubMenuData"%>

<%
java.util.List<SubMenuData> subMenus = new java.util.ArrayList<>();

SubMenuData data;

data = new SubMenuData("#namespaces", "Namespaces");
subMenus.add(data);

data = new SubMenuData("#repository", "Repository");
subMenus.add(data);

data = new SubMenuData("#planlanguages", "Plan Languages");
subMenus.add(data);

data = new SubMenuData("#plantypes", "Plan Types");
subMenus.add(data);

data = new SubMenuData("#constrainttypes", "Constraint Types");
subMenus.add(data);
%>

<%-- TODO: do not use componentinstance, but introduce a layer inbetween componentinstance.tag and genericpage.tag --%>

<t:componentinstance windowtitle="Admin" cssClass="mainContentContainer admin" selected="admin" subMenus="<%=subMenus%>">
</t:componentinstance>

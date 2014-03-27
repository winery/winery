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
<%@page import="org.eclipse.winery.repository.resources.SubMenuData"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<!-- add submenus after the submenus defined for the type -->
<%
java.util.List<SubMenuData> subMenus = new java.util.ArrayList<SubMenuData>(5);

SubMenuData data;

data = new SubMenuData("#topologytemplate", "Topology Template");
subMenus.add(data);

data = new SubMenuData("#plans", "Plans");
subMenus.add(data);

data = new SubMenuData("#selfserviceportal", "Self-service Portal");
subMenus.add(data);

data = new SubMenuData("#boundarydefinitions", "Boundary Definitions");
subMenus.add(data);

//Tags are currently not implemented -> Don't confuse users by showing the tab
//has to be enabled again, when tags are implemented
//data = new SubMenuData("#tags", "Tags");
//subMenus.add(data);
%>

<t:componentinstance cssClass="serviceTemplate" selected="ServiceTemplate" subMenus="<%=subMenus%>">
</t:componentinstance>

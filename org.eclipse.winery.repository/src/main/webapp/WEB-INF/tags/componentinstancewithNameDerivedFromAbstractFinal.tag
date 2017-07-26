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
<%@tag import="org.eclipse.winery.repository.resources.SubMenuData"%>
<%@tag description="Wrapper for component instances with name, derived from, abstract, final (equivalent to AbstractComponentInstanceWithName..." pageEncoding="UTF-8"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@tag import="java.util.ArrayList"%>

<!-- for the header -->
<%@attribute name="selected" required="true"%>
<%@attribute name="cssClass" required="true"%>
<%@attribute name="image" required="false"%>
<%@attribute name="libs" fragment="true" %>
<%@attribute name="implementationFor" %>
<%@attribute name="twolines" required="false" description="if set, two lines are required for the tabs"%>

<%@attribute name="subMenus" required="false" type="java.util.List" description="list of SubMenuData objects stating the content of the submenus. The first submenu is used as default page. Subpage #xml must not be included, it is added automatically."%>

<!-- add submenus after the submenus defined for the type -->
<%

if (subMenus == null) {
    subMenus = new ArrayList(1);
}

SubMenuData data;

data = new SubMenuData("#inheritance", "Inheritance");
subMenus.add(data);
%>

<t:componentinstancewithName cssClass="${cssClass}" selected="${selected}" subMenus="<%=subMenus%>" image="${image}" libs="${libs}" implementationFor="${implementationFor}" twolines="${twolines}">
</t:componentinstancewithName>

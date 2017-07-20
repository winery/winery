<%
/*******************************************************************************
 * Copyright (c) 2013 Pascal Hirmer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - initial API and implementation
 *******************************************************************************/

/**
 * This JSP file adds Node and RelationshipTemplates to a topology XML String using the JAXBHelper class.
 * After the java method has finished, the completed topology XML String is returned.
 */
%>
<%@page import="org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.JAXBHelper"%>

<%
    String topologyXML = JAXBHelper.addTemplatesToTopology(request.getParameter("topology"), request.getParameter("allChoices"), request.getParameter("selectedNodeTemplates"), request.getParameter("selectedRelationshipTemplates"));
%>

<%=topologyXML%>
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

<%
    /**
     * This JSP file adds Node and RelationshipTemplates to a topology XML String using the JAXBHelper class.
     * After the java method has finished, the completed topology XML String is returned.
     */
%>
<%@page import="org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.JAXBHelper" %>

<%
    String topologyXML = JAXBHelper.addTemplatesToTopology(request.getParameter("topology"), request.getParameter("allChoices"), request.getParameter("selectedNodeTemplates"), request.getParameter("selectedRelationshipTemplates"));
%>

<%=topologyXML%>

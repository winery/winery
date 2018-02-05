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
     * This JSP saves a topology template to the repository using the RESTHelper class.
     * It is called when there are several topology solutions which shall be saved in different locations.
     */
%>
<%@page import="org.eclipse.winery.model.tosca.Definitions" %>
<%@page import="org.eclipse.winery.model.tosca.TServiceTemplate" %>
<%@page import="org.eclipse.winery.model.tosca.TTopologyTemplate" %>
<%@page import="org.eclipse.winery.topologymodeler.addons.topologycompleter.helper.RESTHelper" %>
<%@page import="javax.xml.bind.JAXBContext" %>
<%@page import="javax.xml.bind.Unmarshaller" %>
<%@page import="java.io.StringReader" %>

<%
    String xmlString = request.getParameter("topology");
    String templateURL = request.getParameter("templateURL");
    String repositoryURL = request.getParameter("repositoryURL");

    // initiate JaxB context
    JAXBContext context;
    context = JAXBContext.newInstance(Definitions.class);
    StringReader reader = new StringReader(xmlString);

    // unmarshall the topology XML string
    Unmarshaller um = context.createUnmarshaller();
    Definitions jaxBDefinitions = (Definitions) um.unmarshal(reader);
    TServiceTemplate st = (TServiceTemplate) jaxBDefinitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);
    TTopologyTemplate toBeSaved = st.getTopologyTemplate();

    // depending on the selected save method (overwrite or create new) the save method is called
    if (request.getParameter("overwriteTopology").equals("true")) {
        RESTHelper.saveCompleteTopology(toBeSaved, templateURL, true, "", "", repositoryURL);
    } else {
        RESTHelper.saveCompleteTopology(toBeSaved, templateURL, false, request.getParameter("topologyName"), request.getParameter("topologyNamespace"), repositoryURL);
    }
%>

<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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
<%@tag
    description="Rendering for properties. A separate CSS has to be provided to style the content. Thus, this tag is reusable both in the topology modeler and in the management UI. Requires global javaScript function editPropertiesXML(visualElementId)"
    pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="propertiesDefinition" required="true"
             type="org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition"
             description="The TOSCA-conforming properties definition. May be null." %>
<%@attribute name="wpd" required="true" type="org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition"
             description="Winery's K/V properties definition. May be null" %>
<%@attribute name="template" required="true" type="org.eclipse.winery.model.tosca.TEntityTemplate"
             description="The template to display properties. Has to be null in case of the palette mode of the topology modeler" %>
<%@attribute name="pathToImages" required="true"
             description="The path (URI path) to the image/ url, where xml.png is available. Has to end with '/'" %>

<%@tag import="org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKV" %>
<%@tag import="org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKVList" %>
<%@tag import="org.eclipse.winery.model.tosca.utils.ModelUtilities" %>
<%@ tag import="java.util.Map" %>

<%
    if ((propertiesDefinition != null) || (wpd != null)) {
// properties exist
%>
<div class="propertiesContainer">
    <div class="header">Properties</div>
    <div class="content">
        <%
            if (wpd == null) {
                // no winery's special properties definition, but "normal" TOSCA properties definition

                if (propertiesDefinition.getType() != null) {
        %>
        <span class="properties_type">XSD Type: <%=propertiesDefinition.getType()%></span>
        <%
        } else {
        %>
        <span class="properties_element">XSD Element: <%=propertiesDefinition.getElement()%></span>
        <%
            }
        %>
        <textarea class="properties_xml"><%
            if (template != null) {
        %><%=org.eclipse.winery.common.Util.getXMLAsString(org.eclipse.winery.model.tosca.TEntityTemplate.Properties.class, template.getProperties())%><%
            }
        %></textarea>
        <%-- We have to do use $(this).parent().parent().parent().attr('id') instead of <%=visualElementId%> as on drag'n'drop from the palette, this binding is NOT changed, but the Id changes --> the user does NOT want to edit the properties from the palette entry, but from the node template --%>
        <button class="btn btn-default" onclick="editPropertiesXML($(this).parent().parent().parent().attr('id'));">
            <img src="${pathToImages}xml.png"></img>View
        </button>
        <%
        } else {
            // Winery special mode
            Map<String, String> props;
            if (template == null) {
                // setting null only because of dump compiler.
                // We never read props if in paletteMode
                props = null;
            } else {
                props = ModelUtilities.getPropertiesKV(template);
            }
        %>
        <%-- stores wrapper element name and namespace to ease serialization--%>
        <span class="elementName"><%=wpd.getElementName()%></span>
        <span class="namespace"><%=wpd.getNamespace()%></span>
        <table>
            <%
                PropertyDefinitionKVList list = wpd.getPropertyDefinitionKVList();
                if (list != null) {
                    // iterate on all defined properties
                    for (PropertyDefinitionKV propdef : list) {
                        String key = propdef.getKey();
                        String value;
                        if (template == null || props == null) {
                            value = "";
                        } else {
                            // assign value, but change "null" to "" if no property is defined
                            if ((value = props.get(key)) == null) {
                                value = "";
                            }
                        }
            %>
            <tr class="KVProperty">
                <td><span class="<%= key %> KVPropertyKey"><%= key %></span></td>
                <td><a class="KVPropertyValue" href="#" data-type="text" data-title="Enter <%= key %>"><%=value %></a></td>
            </tr>
            <%
                    }
                }
            %>
        </table>
        <%
            }
        %>
    </div>
</div>
<%
    }
%>

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
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="org.eclipse.winery.model.tosca.TEntityTemplate"%>
<%@page import="org.eclipse.winery.model.tosca.TEntityType"%>
<%@page import="org.eclipse.winery.common.ModelUtilities"%>
<%@page import="org.eclipse.winery.repository.Utils"%>

<%@taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="props" tagdir="/WEB-INF/tags/common/templates" %>
<%@taglib prefix="w"     uri="http://www.eclipse.org/winery/repository/functions"%>
<%@taglib prefix="wc"    uri="http://www.eclipse.org/winery/functions" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/components/XMLWriter/XMLWriter.js"></script>

<style>
    div.header {
        display: none;
    }
    span.elementName {
        display: none;
    }
    span.namespace {
        display: none;
    }
</style>

<props:propertiesBasic></props:propertiesBasic>

<c:set var="type" value="${w:typeForTemplate(it.template)}" />

<div id="containerForPropertiesContainer">
<div> <%-- This div is required by props:properties to be consistent with a node template. This mirrors div class="content" --%>
<props:properties
    propertiesDefinition="${type.propertiesDefinition}"
    wpd="${wc:winerysPropertiesDefinition(type)}"
    template="${it.template}"
    pathToImages="${w:topologyModelerURI()}/images/">
</props:properties>
</div>
</div>

<c:choose>
    <c:when test="${not empty type.propertiesDefinition or not empty wc:winerysPropertiesDefinition(type)}">
        <button id="propsSaveBtn" data-loading-text="Saving..." type="button" class="btn btn-primary btn-sm" onclick="saveProperties();">Save</button>
    </c:when>
    <c:otherwise>
        The type does not have a &ldquo;properties definition&rdquo;.
    </c:otherwise>
</c:choose>

<script>
$(".KVPropertyValue").editable();

// similar to topology modeler's index.jsp save() function
function saveProperties() {
    $("#propsSaveBtn").button('loading');
    var w = new XMLWriter("utf-8");
    w.writeStartDocument();
    var divContainer = $("#containerForPropertiesContainer");
    savePropertiesFromDivToXMLWriter(divContainer.children("div").children(".propertiesContainer"), w, true);
    w.writeEndDocument();

    $.ajax({
        url: "properties/",
        type: "PUT",
        contentType: 'text/xml',
        data: w.flush(),
        success: function(data, textStatus, jqXHR) {
            $("#propsSaveBtn").button('reset');
            vShowSuccess("successfully saved.");
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $("#propsSaveBtn").button('reset');
            vShowAJAXError("Could not save", errorThrown, jqXHR.responseText);
        }
    });

}
</script>

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

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="ct" tagdir="/WEB-INF/tags/common" %>

<c:choose>
    <c:when test="${empty it.requirementType.requiredCapabilityType}">
        <c:set var="selected" value="(none)" />
    </c:when>
    <c:otherwise>
        <c:set var="selected" value="${it.requirementType.requiredCapabilityType}" />
    </c:otherwise>
</c:choose>

<ct:QNameChooser allQNames="${it.allCapabilityTypes}" idOfSelectField="requiredCapabilityType" labelOfSelectField="" includeNONE="true" selected="${selected}"/>

<script>
$("#requiredCapabilityType").on("change", function(e) {
    var val = $("#requiredCapabilityType").val();
    if (val == "(none)") {
        // remove required capability type assignment
        $.ajax({
            url: 'requiredcapabilitytype',
            type: "DELETE"
        }).fail(function(jqXHR, textStatus, errorThrown) {
            vShowAJAXError("Could not remove required capability type assignment.", jqXHR, errorThrown);
        }).done(function(data, textStatus, jqXHR) {
            vShowSuccess("Successfully updated required capability type assignment.");
        });
    } else {
        // put new capability type
        $.ajax({
            url: 'requiredcapabilitytype',
            data: val,
            contentType: "text/plain",
            type: "PUT"
        }).fail(function(jqXHR, textStatus, errorThrown) {
            vShowAJAXError("Could not update required capability type assignment.", jqXHR, errorThrown);
        }).done(function(data, textStatus, jqXHR) {
            vShowSuccess("Successfully updated required capability type assignment.");
        });
    }
});
</script>
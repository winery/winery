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
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="w"  uri="http://www.eclipse.org/winery/repository/functions" %>

<%-- TODO: source (external documentation) and lang attributes not yet supported --%>

<c:if test="${empty it}">
    <div class="form-group">
        <div class="form-control" id="documentation"></div>
    </div>
    <script>
    $("#documentation").editable({
        type: "wysihtml5",
        send: "always",
        success: function(response, newValue) {
            $.ajax({
                type: "POST",
                async: false,
                "data": newValue,
                "url": "documentation/",
                dataType: "text",
                contentType: "text/html",
                error: function(jqXHR, textStatus, errorThrown) {
                    vShowAJAXError("Could not create documentation", jqXHR, errorThrown);
                },
                success: function(resData, textStatus, jqXHR) {
                    vShowSuccess("Successfully created the documentation");
                    doTheTabSelection();
                }
            });
        }
    });
    </script>
</c:if>


<c:if test="${not empty it}">
    <script>var documentationURL = {};</script>
    <c:forEach var="documentation" items="${it}" varStatus="status">
        <div class="form-group">
            <%-- we only print a heading if we have multiple documentations. Otherwise, the documentation itself should be displayed --%>
            <c:if test="${fn:length(it) > 1}"><label class="label-form">Documentation ${status.index}</label></c:if>
            <div class="form-control" id="documentation${status.index}">
                <c:forEach items="${documentation.content}" var="content">${content}</c:forEach>
            </div>
        </div>
        <c:set var="text" value="${w:XML(documentation)}"></c:set><!--  required to calculate the hashCode -->
        <c:set var="hash" value="<%=pageContext.getAttribute(\"text\").hashCode()%>"></c:set>
        <script>
        documentationURL[${status.index}] = "documentation/${hash}";
        $("#documentation${status.index}").editable({
            type: "wysihtml5",
            send: "always",
            success: function(response, newValue) {
                $.ajax({
                    type: "PUT",
                    async: false,
                    "data": newValue,
                    "url": documentationURL[${status.index}],
                    dataType: "text",
                    contentType: "text/html",
                    error: function(jqXHR, textStatus, errorThrown) {
                        vShowAJAXError("Could not update documentation", jqXHR, errorThrown);
                    },
                    success: function(resData, textStatus, jqXHR) {
                        vShowSuccess("Successfully updated the documentation");
                        documentationURL[${status.index}] = "documentation/" + resData;
                    }
                });
            }
        });
        </script>
    </c:forEach>
</c:if>

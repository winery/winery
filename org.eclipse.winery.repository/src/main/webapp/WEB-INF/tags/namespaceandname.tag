<%--
/*******************************************************************************
 * Copyright (c) 2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *      Lukas Harzenetter - initial API and implementation
 *      Nicole Keppler - Bugfixes
 *******************************************************************************/
--%>
<%@ tag description="displays all generic items as a list" pageEncoding="UTF-8"%>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="v"  uri="http://www.eclipse.org/winery/repository/functions" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions" %>

<c:forEach var="t" items="${it.componentInstanceIds}">
    <c:choose>
        <c:when test="${it.namespace != null}">
            <c:choose>
                <c:when test="${it.namespace == t.namespace}">
                    <c:set var="uri" value="./${t.xmlId.encoded}"></c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="uri" value="null"></c:set>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <c:set var="uri" value="./${t.namespace.encoded}/${t.xmlId.encoded}"></c:set>
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${uri != 'null'}">
            <%-- even though the id is an invalid XML, it is used for a simple implementation on a click on the graphical rendering to trigger opening the editor --%>
            <div class="entityContainer ${it.CSSclass}" id="${uri}/">
                <div class="left">
                    <c:if test="${it.type eq 'NodeType'}">
                        <a href="./${uri}/?edit">
                            <img src='./${uri}/visualappearance/50x50' style='margin-top: 21px; margin-left: 30px; height: 40px; width: 40px;' />
                        </a>
                    </c:if>
                </div>
                <div class="center">
                    <div class="informationContainer">
                        <div class="name">
                                ${wc:escapeHtml4(t.xmlId.decoded)}
                        </div>
                        <div class="namespace" alt="${wc:escapeHtml4(t.namespace.decoded)}">
                                ${wc:escapeHtml4(t.namespace.decoded)}
                        </div>
                    </div>
                    <div class="buttonContainer">
                        <a href="${uri}/?csar" class="exportButton"></a>
                        <a href="${uri}/?edit" class="editButton"></a>
                            <%-- we need double encoding of the URL as the passing to javascript: decodes the given string once --%>
                        <a href="javascript:deleteCI('${wc:escapeHtml4(t.xmlId.decoded)}', '${v:URLencode(uri)}/');" class="deleteButton" onclick="element = $(this).parent().parent().parent();"></a>
                    </div>
                </div>
                <div class="right"></div>
            </div>
        </c:when>
    </c:choose>
</c:forEach>


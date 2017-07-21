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
<%@tag description="Offers choice for valid endings" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@attribute name="shortName" required="true" description="source|target"%>
<%@attribute name="currentSelection" required="false"%>
<%@attribute name="possibleValidEndings" type="java.util.Collection" %>


<select name="valid${shortName}" onchange="updateValue('valid${shortName}', this.options[this.selectedIndex].value);">
    <c:choose>
        <c:when test="${empty currentSelection}">
            <option value="" selected="selected">(all)</option>
        </c:when>
        <c:otherwise>
            <option value="">(all)</option>
        </c:otherwise>
    </c:choose>
    <c:forEach var="typeId" items="${possibleValidEndings}">
        <c:choose>
            <c:when test="${currentSelection eq typeId.QName}">
                <c:set var="selected" value=" selected=\"selected\"" />
            </c:when>
            <c:otherwise>
                <c:set var="selected" value="" />
            </c:otherwise>
        </c:choose>
        <option value="${typeId.QName}"${selected}>${typeId.xmlId.decoded} (${typeId.namespace.decoded})</option>
    </c:forEach>
</select>

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
 *      Nicole Keppler - Bugfixes, added showing number of components in each namespace
 *******************************************************************************/
--%>
<%@tag description="displays all namespaces in a list" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions" %>
<%@taglib prefix="v"  uri="http://www.eclipse.org/winery/repository/functions" %>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="w"  uri="http://www.eclipse.org/winery/repository/functions"%>
<%@taglib prefix="t"  tagdir="/WEB-INF/tags" %>

<c:forEach var="d" items="${w:countOfInstancesInEachNamespace(it.TOSCAComponentId)}">
    <div class="entityContainer ${it.CSSclass}" id="${v:URLencode(d.namespace.encoded)}">
        <div class="left">
            <c:if test="${it.type eq 'NodeType'}">
                <a href="./${v:URLencode(d.namespace.encoded)}/"></a>
            </c:if>
        </div>
        <div class="center">
                <div class="informationContainer">
                    <div class="namespace" alt="${d.namespace}">
                        <div class="namespaceOnly" data-original-title="${d.namespace}">
                        	${d.namespace}
                        </div>
                        <div class="badge numberOfComponentInstances">
                            ${d.count}
                        </div>
                     </div>
                </div>

            <div class="buttonContainer" >
                    <%-- we need double encoding of the URL as the passing to javascript: decodes the given string once --%>
                <a type="button" class="deleteButton" onclick="deleteNamespace('${d.namespace}', '${v:URLencode(d.namespace.encoded)}')"></a>
            </div>
        </div>
        <div class="right"></div>
    </div>
</c:forEach>

<script>
    function deleteNamespace(namespace, url){
        var onSuccess = function () {
            url = url.replace(/[%]/g, '\\%').replace(/[.]/g, '\\.');
            $("#"+url).remove();
        };

        deleteResource(namespace, url, onSuccess);
    }

    $(".namespaceOnly").tooltip({
		classes: {
			"ui-tooltip": "namespaceTooltip"
		}
	});
</script>

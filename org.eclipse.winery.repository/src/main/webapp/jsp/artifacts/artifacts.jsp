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
 *    Yves Schubert - switch to bootstrap 3
 *******************************************************************************/
--%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="v"  uri="http://www.eclipse.org/winery/repository/functions"%>
<%@taglib prefix="ct" tagdir="/WEB-INF/tags/common" %>

<%--
Parameter
isDeploymentArtifact: true/false
--%>

<script>
var artifactsTableInfo = {
    id : '#artifactsTable'
};

<c:choose>
    <c:when test="${it.isDeploymentArtifacts}">
        <c:set var="URL" value="deploymentartifacts/" />
        <c:set var="name" value="Deployment" />
        <c:set var="interfacesOfAssociatedType" value="<%=null%>" />
    </c:when>
    <c:otherwise>
        <c:set var="URL" value="implementationartifacts/" />
        <c:set var="name" value="Implementation" />
        <c:set var="interfacesOfAssociatedType" value="${it.interfacesOfAssociatedType}" />
    </c:otherwise>
</c:choose>


$(function() {
    require(["winery-support"], function(ws) {
        ws.initTable(artifactsTableInfo);
    });
    <jsp:include page="/jsp/setupTriggerRemoveByDELKey.jsp" />
});

/**
 * This function directly accesses the fields of the dialog, because the return value of the server is XML and we do not want to parse XML
 *
 * @param artifactInfo = {name, interfaceName (may be undefined), operationName  (may be undefined), artifactTemplate (QName, may be undefined), artifactTemplateName (may be undefined), artifactType}
 */
function artifactAddedSuccessfully(artifactInfo) {
    require(["winery-support-common"], function(wsc) {
        var data = [artifactInfo.name<c:if test="${not it.isDeploymentArtifacts}">, artifactInfo.interfaceName, artifactInfo.operationName</c:if>];

        // artifactTemplate
        var link = "";
        if (artifactInfo.artifactTemplate) {
            var nsAndId = wsc.getNamespaceAndLocalNameFromQName(artifactInfo.artifactTemplate);
            link = '<a href="';
            link = link + makeArtifactTemplateURL("${pageContext.request.contextPath}", nsAndId.namespace, nsAndId.localname);
            link = link + '">';
            link = link + artifactInfo.name;
            link = link + "</a>";
        }
        // table field has to be filled even if no artifact template has been created
        data.push(link);

        // artifactType
        var href = wsc.makeArtifactTypeURLFromQName("${pageContext.request.contextPath}", artifactInfo.artifactType);
        link = '<a href="';
        link = link + href;
        link = link + '">';
        link = link + wsc.getNamespaceAndLocalNameFromQName(artifactInfo.artifactType).localname;
        link = link + "</a>";
        data.push(link);

        // artifactSpecificContent is emtpy as we do not allow addition of it in the UI
        data.push("");

        artifactsTableInfo.table.fnAddData(data);
    });
}

</script>

<ct:artifactcreationdialog
    onSuccessfulArtifactCreationFunction="artifactAddedSuccessfully"
    isDeploymentArtifact="${it.isDeploymentArtifacts}"
    interfacesOfAssociatedType="${interfacesOfAssociatedType}"
    name="${name}"
    allArtifactTypes="${it.allArtifactTypes}"
    URL="'${URL}'"
    repositoryURL="${pageContext.request.contextPath}"
    allNamespaces="${v:allNamespaces()}"
    defaultNSForArtifactTemplate="TODO"
/>

<div id="artifacts">
    <div class="listheading">
        <label>Available ${name} Artifacts</label>
        <button class="rightbutton btn btn-danger btn-xs" type="button" onclick="deleteOnServerAndInTable(artifactsTableInfo, '${name} Artifact', '${URL}');">Remove</button>
        <button class="rightbutton btn btn-primary btn-xs" type="button" onclick="openAdd${name}ArtifactDiag();">Add</button>
    </div>
    <table cellpadding="0" cellspacing="0" border="0" class="display" id="artifactsTable">
        <thead>
            <tr>
                <th>Name</th>
                <c:if test="${not it.isDeploymentArtifacts}">
                <th>Interface Name</th>
                <th>Operation Name</th>
                </c:if>
                <th>Artifact Template</th>
                <th>Artifact Type</th>
                <th>Specific Content</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="a" items="${it.allArtifactResources}">
                <tr>
                    <td>${a.a.name}</td>
                    <c:if test="${not it.isDeploymentArtifacts}">
                    <td>${a.a.interfaceName}</td>
                    <td>${a.a.operationName}</td>
                    </c:if>
                    <td><c:if test="${not empty a.a.artifactRef}"><a href="${pageContext.request.contextPath}/artifacttemplates/${v:URLencodeQName(a.a.artifactRef)}/">${a.a.artifactRef.localPart}</a></c:if></td>
                    <td><a href="${pageContext.request.contextPath}/artifacttypes/${v:URLencodeQName(a.a.artifactType)}">${a.a.artifactType.localPart}</a></td>
                    <td><c:if test="${not empty a.a.any}">
                        <!--  TODO: convert to bootstrap <a href="javascript: $.msgBox({title: 'Artifact Specific Content', content: '${v:doubleEscapeHTMLAndThenConvertNL2BR(a.a.any)}', type: 'info'});">show</a> -->
                        (exists)
                    </c:if></td>

                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<%--
/*******************************************************************************
 * Copyright (c) 2012-2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Lukas Harzenetter, Philipp Meyer - show only a limited number of items & pagination
 *    Lukas Harzenetter, Nicole Keppler - functionality of "add new"-button for namespaceonly and showAllItems
 *******************************************************************************/
--%>
<%@page import="org.eclipse.winery.repository.Utils"%>
<%@page import="org.eclipse.winery.repository.backend.BackendUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="v"  uri="http://www.eclipse.org/winery/repository/functions" %>
<%@taglib prefix="t"  tagdir="/WEB-INF/tags" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- In English, one can usually form a plural by adding an "s". Therefore, we resue the label to form the window title --%>
<t:genericpage windowtitle="${it.label}s" selected="${it.type}" cssClass="${it.CSSclass}">

<c:choose>
<c:when test="${empty pageContext.request.contextPath}">
<c:set var="URL" value="/" />
</c:when>
<c:otherwise>
<c:set var="URL" value="${pageContext.request.contextPath}/" />
</c:otherwise>
</c:choose>
<t:simpleSingleFileUpload
    title="Upload CSAR"
    text="CSAR file"
    URL="${URL}"
    type="POST"
    id="upCSAR"
    accept="application/zip,.csar"/>

    <c:choose>
        <c:when test="${it.showAllItems}">
            <t:addComponentInstance
                    label="${it.label}"
                    typeSelectorData="${it.typeSelectorData}"
                    URL='..'
            />
        </c:when>
        <c:otherwise>
            <t:addComponentInstance
                    label="${it.label}"
                    typeSelectorData="${it.typeSelectorData}"
            />
        </c:otherwise>
    </c:choose>



<div class="middle" id="ccontainer">
    <br />

    <table cellpadding=0 cellspacing=0 style="margin-top: 0; margin-left: 30px;">
        <tr>
            <td valign="top" style="padding-top: 25px; width: 680px;">
                <div id="searchBoxContainer">
                    <input id="searchBox" onkeyup="search(true)"/>
                </div>

                <c:choose>
                    <c:when test="${it.showAllItems or fn:length(it.componentInstanceIds) < 50}">
                        <t:namespaceandname></t:namespaceandname>
                    </c:when>
                    <c:otherwise>
                        <t:namespaceonly></t:namespaceonly>
                    </c:otherwise>
                </c:choose>

            </td>
            <td id="gcprightcolumn" valign="top">
                <div id="overviewtopshadow"></div>
                <div id="overviewbottomshadow"></div>
            </td>
            <td valign="top">
                <div class="btn-group-vertical" id="buttonList">
                    <button type="button" class="btn btn-default" onclick="openNewCIdiag();">Add new</button>
                    <button type="button" class="btn btn-default" onclick="importCSAR();">Import CSAR</button>
                    <c:if test="${it.type eq 'ServiceTemplate'}">
                        <button type="button" class="btn btn-default" onclick="$('#createFromArtifactDiag').modal('show');">Create from Artifact</button>
                    </c:if>
                    <c:choose>
                        <c:when test="${it.showAllItems}">
                            <a href="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}/${fn:toLowerCase(it.type)}s/" type="button" class="btn btn-default" value="">Show namespaces</a>
                        </c:when>
                        <c:when test="${fn:length(it.componentInstanceIds) < 50}"></c:when>
                        <c:otherwise>
                            <a href="./?full=true" type="button" class="btn btn-default" value="">Show all items</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <div id="paginator">
                    <select id="pageSize" onchange="paginateContainer(1)">
                        <option selected value="10">10</option>
                        <option value="25">25</option>
                        <option value="50">50</option>
                    </select>
                    <input type="button" class="btn bnt-default" id="previousPage" disabled="disabled" onclick="nextPage(false)" value="<"/>
                    <div id="pages">
                    </div>
                    <input type="button" class="btn bnt-default" id="nextPage" onclick="nextPage(true)" value=">"/>
                </div>
            </td>
        </tr>
    </table>
</div>

<c:if test="${it.type eq 'ServiceTemplate'}">
    <t:createFromArtifactDialog allSubResources="${it.componentInstanceIds}" allNodeTypes="<%=Utils.getAllNodeTypeResources()%>"/>
</c:if>

<script>
    $(function () {
        paginateContainer(1);
    });

    /**
     * enables searching for name and namespace
     *
     * @param startPagination boolean which specifies if the first page should be shown or not
     */
    function search(startPagination) {
        var searchString = $("#searchBox").val();
        searchString = searchString.toLowerCase();

        $(".entityContainer").each (function() {
            var name = $(this).find(".informationContainer > .name").text();
            var namespace = $(this).find(".informationContainer > .namespace").text();

            var t = name + namespace;
            t = t.toLowerCase();

            if (t.indexOf(searchString) == -1) {
                $(this).hide();
            } else {
                $(this).show();
            }

        });

        if (startPagination){
            paginateContainer(1);
        }
    }

    /**
     * paginates all container
     *
     * @param pageSelected integer containing the page to be shown
     */
    function paginateContainer(pageSelected) {
        var container = $(".entityContainer");
        var pageSize = $("#pageSize option:selected").val();
        var index = 0;

        if(container.length <= 10){
            $("#paginator").hide();
        }

        if ($('#searchBox').val() == "") {
            // standard procedure
            container.each(function () {
                if ((Math.floor(index++ / pageSize) + 1) === pageSelected) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            });
        } else {
            // This is needed for the pagination to work correctly when using the search. The reason is, that all
            // container matching the search, need to be visible in order to paginate them correctly.
            search(false);

            container = container.filter(":visible");

            container.each(function () {
                if ((Math.floor(index++ / pageSize) + 1) != pageSelected) {
                    $(this).hide();
                }
            });
        }

        // set up page navigation buttons
        var pages = $("#pages");
        var pageCount = Math.ceil(container.length / pageSize);

        pages.empty();

        var pagesToShow = [];

        if (pageCount > 4) {
            switch (pageSelected){
                case 1:
                case 2:
                    pagesToShow = [1, 2, 3, "...", pageCount];
                    break;
                case 3:
                    pagesToShow = [1, 2, 3, 4, "...", pageCount];
                    break;
                case pageCount -2:
                    pagesToShow = [1, "...", pageCount -3, pageCount-2, pageCount -1, pageCount];
                    break;
                case pageCount-1:
                case pageCount:
                    pagesToShow = [1, "...", pageCount-2, pageCount -1, pageCount];
                    break;
                default:
                    pagesToShow = [1, "...", pageSelected -1, pageSelected, pageSelected + 1, "...", pageCount];
                    break;
            }
        } else {
            for (var j = 0; j < pageCount; j++) {
                pagesToShow[j] = j + 1;
            }
        }

        for (var i = 0; i < pagesToShow.length; i++) {
            var selected = "";
            if (pagesToShow[i] === pageSelected) {
                selected = 'disabled="disabled"';
            }

            if (typeof pagesToShow[i] === "string") {
                pages.append($('<em class="pageRepresentative">' +  pagesToShow[i] + '</em>'))
            } else {
                pages.append(
                        $('<input type="button"' + selected + ' class="btn btn-default pageSelector" value="' +
                                pagesToShow[i] + '" onclick="paginateContainer(' + pagesToShow[i] + ')"/>')
                );
            }

        }

        // disable previous/next buttons accordingly
        if (pageSelected >= pageCount) {
            $("#nextPage").prop("disabled", true);
        } else {
            $("#nextPage").prop("disabled", false);
        }

        if (pageSelected <= 1) {
            $("#previousPage").prop("disabled", true);
        } else {
            $("#previousPage").prop("disabled", false);
        }
    }

    /**
     * manages navigation through pages for previous/next arrow
     *
     * @param next boolean which specifies whether the next or previous page is requested. TRUE if next, FALSE if previous
     */
    function nextPage(next) {
        var page = parseInt($(".pageSelector").filter(":disabled").val());

        if (next) {
            paginateContainer(page +1, true);
        } else {
            paginateContainer(page -1, true);
        }
    }

function entityContainerClicked(e) {
    var target = $(e.target);
    if (target.is("a")) {
        // do nothing as a nested a element is clicked
    } else {
        var ec = target.parents("div.entityContainer");
        var url = ec.attr('id');
        if (e.ctrlKey) {
            // emulate browser's default behavior to open a new tab
            window.open(url);
        } else {
            window.location = url;
        }
    }
}

$("div.entityContainer").on("click", entityContainerClicked);

/**
 * deletes given component instance
 * uses global variable "element", which stores the DOM element to delete upon successful deletion
 */
function deleteCI(name, URL) {
    deleteResource(name, URL, function() {
        element.remove();
    });
}

function importCSAR() {
    $('#upCSARDiag').modal('show');
}

// If export button is clicked with "CTRL", the plain XML is shown, not the CSAR
// We use "on" with filters instead as new elements could be added when pressing "Add new" (in the future)
// contained code is the same as the code of the CSAR button at the topology modeler (see index.jsp)
$(document).on("click", ".exportButton", function(evt) {
    var url = $(this).attr("href");
    if (evt.ctrlKey) {
        url = url.replace(/csar$/, "definitions");
    }
    window.open(url);
    return false;
});

<%-- Special feature in the case of the service template --%>
<c:if test="${it.type eq 'ServiceTemplate'}">
//If edit button is clicked with "CTRL", the topology modeler is opened, not the service template editor
//We use "on" with filters instead as new elements could be added when pressing "Add new" (in the future)
$(document).on("click", ".editButton", function(evt) {
    var url = $(this).attr("href");
    if (evt.ctrlKey) {
        url = url.replace(/\?edit$/, "topologytemplate/?edit");
        // open in new tab
        var newWin = window.open(url);
        // focussing the new window does not work in Chrome
        newWin.focus();
    } else {
        // normal behavior
        window.location = url;
    }
    evt.preventDefault();
});
</c:if>

$(".exportButton").tooltip({
    placement: 'bottom',
    html: true,
    title: "Export CSAR.<br/>Hold CTRL key to export XML only."
});
$(".editButton").tooltip({
    placement: 'bottom',
    html: true,
    title: <c:if test="${it.type eq 'ServiceTemplate'}">"Edit.<br/>Hold CTRL key to directly open the topology modeler."</c:if><c:if test="${not (it.type eq 'ServiceTemplate')}">"Edit"</c:if>
});
$(".deleteButton").tooltip({
    placement: 'bottom',
    title: "Delete"
});
</script>

</t:genericpage>

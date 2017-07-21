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
 *    Yves Schubert - port to bootstrap 3
 *******************************************************************************/
--%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t"  tagdir="/WEB-INF/tags" %>
<%@taglib prefix="ct" tagdir="/WEB-INF/tags/common" %>
<%@taglib prefix="p"  tagdir="/WEB-INF/tags/parameters" %>
<%@taglib prefix="w"  uri="http://www.eclipse.org/winery/repository/functions"%>

<%@page import="org.eclipse.winery.common.Util" %>
<%@page import="org.eclipse.winery.repository.Constants" %>

<link type="text/css" href="${pageContext.request.contextPath}/components/bootstrap-switch/build/css/bootstrap3/bootstrap-switch.css" rel="stylesheet" />
<script type="text/javascript" src="${pageContext.request.contextPath}/components/bootstrap-switch/build/js/bootstrap-switch.js"></script>

<%-- include basic parameters support --%>
<p:parametersJS afterLoad="interfaceSelectionChanged();"></p:parametersJS>

<script>
<%final String URI_LIFECYCLE_INTERFACE = "http://www.example.com/interfaces/lifecycle";%>
var URI_LIFECYCLE_INTERFACE = "<%=URI_LIFECYCLE_INTERFACE%>";

/**
 * @param noUpdate if given, then the selection is not updated. This is required when Winery itself creates interfaces without user intervention. For instance, that is the case at the lifecycle interface
 */
function afterInterfaceCreation(serializedArray, resData, textStatus, jqXHR, noUpdate) {
    var text = serializedArray[0].value;
    if (text == URI_LIFECYCLE_INTERFACE) {
        // lifecycle interface has been generated
        // disable button to generate lifecycle interface
        $("#generatelifecycleifacetn").attr("disabled", "disabled");
    }
    addSortedSelectionItem($("#interfaces"), text, text);
    if (!noUpdate) {
        interfaceSelectionChanged();
    }
}

function afterOperationCreation(serializedArray, resData, textStatus, jqXHR) {
    var text = serializedArray[0].value;
    addSortedSelectionItem($("#operations"), text, text);
    operationSelectionChanged();
}

function getIfaceURL() {
    var iface = $("#interfaces").find(":selected").val();
    return "${it.urlPrefix}interfaces/" + encodeID(iface) + "/";
}

function getOperationURL() {
    var op = $("#operations").find(":selected").val();
    return getIfaceURL() + "operations/" + encodeID(op) + "/";
}

function createInterface() {
    createResource('Interface', [{'label': 'Name', 'name':'interfaceName'}], '${it.urlPrefix}interfaces/', afterInterfaceCreation);
}

function createOperation() {
    var url = getIfaceURL() + "operations/";
    createResource('Operation', [{'label':'Name', 'name':'name'}], url, afterOperationCreation);
}

function disableAllOperationButtons() {
    $("#addOpBtn").attr("disabled", "disabled");
    $("#removeOpBtn").attr("disabled", "disabled");
}

function disableAllInputButtons() {
    $("#addInParBtn").attr("disabled", "disabled");
    $("#removeInParBtn").attr("disabled", "disabled");
}

function disableAllOutputButtons() {
    $("#addOutParBtn").attr("disabled", "disabled");
    $("#removeOutParBtn").attr("disabled", "disabled");
}

function interfaceSelectionChanged(noupdate) {
    var iface = $("#interfaces").find(":selected");
    if (iface.length == 0) {
        // nothing selected
        $("#operations").empty();
        inputParametersTableInfo.table.fnClearTable();
        outputParametersTableInfo.table.fnClearTable();
        disableAllOperationButtons();
        disableAllInputButtons();
        disableAllOutputButtons();
        $("#removeIfBtn").attr("disabled", "disabled");
        $("#generateiabtn").attr("disabled", "disabled");
    } else {
        $.ajax({
            "url": getIfaceURL() + "operations/",
            dataType: "JSON",
            success: function(data, textStatus, jqXHR) {
                var operations = $("#operations");
                operations.empty();
                $.each(data, function(number, item) {
                    var selected;
                    if (number == 0) {
                        selected  = ' selected="selected"';
                    } else {
                        selected = "";
                    }
                    operations.append('<option value="' + item + '"' + selected + '>' + item + '</option>');
                });
                operationSelectionChanged();
                $("#removeIfBtn").removeAttr("disabled");
                $("#generateiabtn").removeAttr("disabled");
            }
        });
    }
}

function operationSelectionChanged() {
    if ($("#operations").find(":selected").length == 0) {
        inputParametersTableInfo.table.fnClearTable();
        outputParametersTableInfo.table.fnClearTable();
        disableAllInputButtons();
        disableAllOutputButtons();
        $("#removeOpBtn").attr("disabled", "disabled");
        if ($("#interfaces").children("option").length == 0) {
            // no interfaces available
            $("#addOpBtn").attr("disabled", "disabled");
        } else {
            $("#addOpBtn").removeAttr("disabled");
        }
    } else {
        updateInputAndOutputParameters(getOperationURL());
        $("#addOpBtn").removeAttr("disabled");
        $("#removeOpBtn").removeAttr("disabled");
    }
}


function determineNextItemToSelect(currentItem) {
    var nextToSelect;
    nextToSelect = currentItem.next();
    if (nextToSelect.length == 0) {
        nextToSelect = currentItem.prev();
        // even if nothing found, the following code works:
        // X.attr("selected", "selected") does not throw any error if X is empty
    }
    return nextToSelect;
}

function deleteThing(thingId, thingName, urlDetermination, changedFunction) {
    var thing = $("#" + thingId).find(":selected");
    if (thing.length == 0) {
        vShowError("UI in wrong state");
    } else {
        deleteResource(
            thingName + " " + thing.text(),
            urlDetermination(),
            function() {
                var nextToSelect = determineNextItemToSelect(thing);
                thing.remove();
                nextToSelect.attr("selected", "selected");
                changedFunction();
            }
        );
    }
}

function deleteInterface() {
    var iface = $("#interfaces").find(":selected").text();

    // if the lifecycle interface is going to be removed,
    // enable the button to generate the lifecycle interface again
    var changedFunction;
    if (iface == URI_LIFECYCLE_INTERFACE) {
        changedFunction = function() {
            $("#generatelifecycleifacetn").removeAttr("disabled");
            interfaceSelectionChanged();
        }
    } else {
        changedFunction = interfaceSelectionChanged;
    }

    deleteThing("interfaces", "Interface", getIfaceURL, changedFunction);
}

function deleteOperation() {
    deleteThing("operations", "Operation", getOperationURL, operationSelectionChanged);
}

function createArtifactTemplate() {
    // we use the quick way at implementationartifacts/ to auto-generate an artifact template
    // see org.eclipse.winery.repository.resources.artifacts.GenericArtifactsResource<ArtifactResource, ArtifactT> for the REST-interface description;

    var url = getTypeImplementationURL() + "implementationartifacts/";
    var data = $("#artifactTemplateNS, #artifactTemplateName, #autoCreateArtifactTemplate, #artifactType, #autoGenerateIA, #interfaces, #javapackage").serialize();

    // we use the artifact template name as artifact name
    var artifactName = $("#artifactTemplateName").serialize();
    artifactName = "artifact" + artifactName.substring(16);
    data = data + "&" + artifactName;

    $.ajax({
        url: url,
        data: data,
        type: "POST"
    }).fail(function(jqXHR, textStatus, errorThrown) {
        vShowAJAXError("Could not create artifact template", jqXHR, errorThrown);
        $("#generateiabtn").button("reset");
    }).done(function(data, textStatus, jqXHR) {
        var location = jqXHR.getResponseHeader("location");

        vShowSuccess('Successfully generated IA. It is currently downloaded and available <a href="' + location + '/../../">here</a>');

        // open the downloaded file
        window.open(location, "_blank");

        $("#generateiamodal").modal("hide");
    });
}

function generateIA() {
    $("#generateiabtn").button("loading");

    // create type implementation if necessary
    var typeImplementationHasToBeCreated = $("#nodeTypeImplCreationSwitch").bootstrapSwitch('status'); // "status" instead of "state" (!)
    if (typeImplementationHasToBeCreated) {
        var url = "${pageContext.request.contextPath}/${it.relationshipTypeOrNodeTypeURLFragment}implementations/";
        var data = $("#nodetypeimplementationName, #nodetypeimplementationNS, #qnameOfType").serialize();
        $.ajax({
            url: url,
            type: "POST",
            data: data
        }).fail(function(jqXHR, textStatus, errorThrown) {
            vShowAJAXError("Could not create type implementation", jqXHR, errorThrown);
            $("#generateiabtn").button("reset");
        }).done(function() {
            createArtifactTemplate();
        });
    } else {
        createArtifactTemplate();
    }
}

$(function() {
    $("#generateiamodal").on("show.bs.modal", function() {
        // check whether the required artifact type exists
        $.ajax({
            url: "${pageContext.request.contextPath}/artifacttypes/<%=Util.URLencode(Constants.NAMESPACE_ARTIFACTTYPE_WAR)%>/<%=Util.URLencode(Constants.LOCALNAME_ARTIFACTTYPE_WAR)%>/",
            type: "HEAD"
        }).fail(function(jqXHR, textStatus, errorThrown) {
            if (jqXHR.status == 404) {
                vShowError("WAR artifact type does not exist. Please create artifact type WAR");
                $("#generateiabtn").attr("disabled", "disabled");
            } else {
                vShowAJAXError("Could not check for existance of WAR artifact type", jqXHR, errorThrown);
            }
        }).success(function() {
            $("#generateiabtn").removeAttr("disabled");
        });

        // dialog is reset at each show
        $("#generateiabtn").button("reset");

        // set java package
        $("#javapackage").val("${w:namespaceToJavaPackage(it.namespace)}");

        // set node type implementation name
        $("#nodetypeimplementationName").val("${it.name}_impl");

        // reset node type implementation namespace
        $("#nodetypeimplementationNS").val("${it.namespace}").attr("selected", "selected");

        // the default type impl could exist
        checkNodeTypeImplName();

        // reset artifact template namespace
        $("#artifactTemplateNS").val("${it.namespace}").attr("selected", "selected");

        require(["artifacttemplateselection", "winery-support"], function(ats, ws) {
            // set artifact template name
            var iface = $("#interfaces").find(":selected");
            var initialATName = "${it.name}_" + ws.makeNCName(iface.text()) + "_IA";
            $("#artifactTemplateName").val(initialATName);

            // check if artifact template is a valid name
            ats.checkArtifactTemplateName();
        });
    });

    $("#nodetypeimplementationNS").on("blur", checkNodeTypeImplName).on("change", checkNodeTypeImplName).on("focus", flagNodeTypeImplAsUpdating);
    $("#nodetypeimplementationName").typing({
        start: function(event, $elem) {
            flagNodeTypeImplAsUpdating();
        },
        stop: function(event, $elem) {
            checkNodeTypeImplName();
        }
    });

});

<%-- adapted from artifacttemplateselection.tag --%>

function getTypeImplementationURL() {
    var ns = $("#nodetypeimplementationNS").val();
    var name = $("#nodetypeimplementationName").val();
    return "${pageContext.request.contextPath}/${it.relationshipTypeOrNodeTypeURLFragment}implementations/" + encodeID(ns) + "/" + encodeID(name) + "/";
}

function checkNodeTypeImplName() {
    var name = $("#nodetypeimplementationName").val();
    if (name == "") {
        var valid = false;
        var invalidReason = "No name provided";
        // TODO: setNodeTypeImplNameValidityStatus(valid, invalidReason);
    } else {
        url = getTypeImplementationURL();
        $.ajax(url, {
            type: 'HEAD',
            dataType: 'html',
            error: function(jqXHR, textStatus, errorThrown) {
                if (jqXHR.status == 404) {
                    // node type implementation does not exist
                    $("#nodeTypeImplCreationSwitch").bootstrapSwitch('setState', true);
                } else {
                    vShowAJAXError("Could not check for type implementation existance", jqXHR, errorThrown);
                    // Alternative: setValidityStatus(false, textStatus);
                }
            },
            success: function(data, textStatus, jqXHR) {
                // node type implementation exists
                $("#nodeTypeImplCreationSwitch").bootstrapSwitch('setState', false);
            }
        });
    }
}

function flagNodeTypeImplAsUpdating() {
    // not yet implemented
}

function generateLifeCycleInterface() {
    var data = "interfaceName=" + encodeURIComponent(URI_LIFECYCLE_INTERFACE);
    $.ajax({
        url: '${it.urlPrefix}interfaces/',
        data: data,
        type: "POST"
    }).fail(function(jqXHR, textStatus, errorThrown) {
        vShowAJAXError("Could not create interface", jqXHR, errorThrown);
    }).done(function(data, textStatus, jqXHR) {
        var serializedArray = [{value:URI_LIFECYCLE_INTERFACE}];
        afterInterfaceCreation(serializedArray, data, textStatus, jqXHR, true);

        var operations = ["install", "configure", "start", "stop", "uninstall"];
        var errorOccurred = false;

        $(operations).each(function(i, operationName) {
            // we have to go through one-by-one to keep the order of operations
            // no parallel creation possible
            // Therefore also "async: false" at the AJAX call

            var data = "name=" + operationName;

            var url = getIfaceURL() + "operations/";
            $.ajax({
                url: url,
                data: data,
                type: "POST",
                async: false
            }).fail(function(jqXHR, textStatus, errorThrown) {
                vShowAJAXError("Could not create operation " + operationName, jqXHR, errorThrown);
                errorOccurred = true;
            }).done(function(data, textStatus, jqXHR) {
                serializedArray = [{value:operationName}];
                afterOperationCreation(serializedArray, data, textStatus, jqXHR);
            });
        });

        if (!errorOccurred) {
            vShowSuccess('Successfully generated lifecycle interface');
        }
    });
}

</script>

<div id="generateiamodal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Generate Implementation Artifact</h4>
            </div>
            <div class="modal-body">
                <form role="form">
                    <div class="form-group">
                        <label for="javapackage">Java Package</label>
                        <input class="form-control" id="javapackage" name="javapackage" placeholder="Enter java package name" required="required">
                    </div>

                    <div class="form-group-grouping typeimplementation">
                        <div class="form-group">
                            <label for="nodetypeimplementationName">${it.relationshipTypeOrNodeType} Implementation Name</label>
                            <input required="required" class="form-control" id="nodetypeimplementationName" name="name" placeholder="Enter name for node type implementation" pattern="[\i-[:]][\c-[:]]*"><!-- name is an NCName -->
                        </div>

                        <t:namespaceChooser nameOfInput="namespace" idOfInput="nodetypeimplementationNS" allNamespaces="${w:allNamespaces()}" selected="${it.namespace}"></t:namespaceChooser>

                        <div id="nodeTypeImplCreationSwitch" class="make-switch" data-on-label="will be created" data-off-label="will be reused" style="height:30px; width:250px;">
                            <input type="checkbox" disabled="disabled" checked="checked">
                        </div>

                        <input type="hidden" name="type" id="qnameOfType" value="${it.typeQName}">
                    </div>

                    <p class="text-warning">There is no check for the name of the implementation artifact. The artifact template name will be reused as implementation artifact name without any further check.</p>

                    <ct:artifacttemplateselection repositoryURL="${pageContext.request.contextPath}" defaultNSForArtifactTemplate="${it.namespace}" allNamespaces="${w:allNamespaces()}"></ct:artifacttemplateselection>
                    <input type="hidden" name="autoCreateArtifactTemplate" value="true" id="autoCreateArtifactTemplate">

                    <div class="form-group">
                        <label for="artifacttype">Artifact Type</label>
                        <a class="form-control" target="_blank" href="${pageContext.request.contextPath}/artifacttypes/<%=Util.URLencode(Constants.NAMESPACE_ARTIFACTTYPE_WAR)%>/<%=Util.URLencode(Constants.LOCALNAME_ARTIFACTTYPE_WAR)%>/">WAR</a>
                        <input type="hidden" name="artifactType" id="artifactType" value="{<%=Constants.NAMESPACE_ARTIFACTTYPE_WAR%>}<%=Constants.LOCALNAME_ARTIFACTTYPE_WAR%>">
                    </div>

                    <input type="hidden" name="autoGenerateIA" id="autoGenerateIA" value="true">
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" id="generateiabtn" onclick="generateIA();" data-loading-text="Generating...">Generate</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


<div><!--  we do not use bootstrap's "container" as the container leads to an overflow -->
    <div class="col-xs-4 bordered">
        <div class="listheading">
            <button class="rightbutton btn btn-danger btn-xs" type="button" onclick="deleteInterface();" disabled="disabled" id="removeIfBtn">Remove</button>
            <button class="rightbutton btn btn-primary btn-xs" type="button" onclick="createInterface();" id="addIfBtn">Add</button>
            <label>Interfaces</label>
        </div>
        <select class="listcontent" id="interfaces" size="18" onchange="interfaceSelectionChanged();" name="interfaceName">
        <c:set var="generatelifcecycleifactebtnDisabled" value=""></c:set>
        <c:set var="URI_LIFECYCLE_INTERFACE" value="<%=URI_LIFECYCLE_INTERFACE%>"></c:set>
        <!-- ${URI_LIFECYCLE_INTERFACE} -->
        <c:forEach var="iface" varStatus="count" items="${it.listOfAllEntityIdsAsList}">
            <c:set var="selected" value=""></c:set>
            <c:if test="${count.index == 0}">
                <c:set var="selected" value=" selected=\"selected\""></c:set>
            </c:if>
            <c:if test="${iface == URI_LIFECYCLE_INTERFACE}">
                <c:set var="generatelifcecycleifactebtnDisabled" value=" disabled=\"disabled\""></c:set>
            </c:if>
            <option value="${iface}"${selected}>${iface}</option>
        </c:forEach>
        </select>
        <button id="generateiabtnOpenModal" class="btn btn-default btn-xs" data-toggle="modal" data-target="#generateiamodal">Generate Implementation Artifact</button>
        <button id="generatelifecycleifacetn" class="btn btn-default btn-xs" onclick="generateLifeCycleInterface();"${generatelifcecycleifactebtnDisabled}>Generate lifecycle interface</button>
    </div>

    <div class="col-xs-4 middlebox bordered">
        <div class="listheading">
            <button class="rightbutton btn btn-danger btn-xs" type="button" onclick="deleteOperation();" id="removeOpBtn">Remove</button>
            <button class="rightbutton btn btn-primary btn-xs" type="button" onclick="createOperation();" id="addOpBtn">Add</button>
            <label>Operations</label>
        </div>
        <select class="listcontent" id="operations" size="18" onchange="operationSelectionChanged();">
        </select>
    </div>

    <div class="col-xs-4 bordered">
        <p:parametersInput baseURL="getOperationURL()"></p:parametersInput>
        <br /><br />
        <p:parametersOutput baseURL="getOperationURL()"></p:parametersOutput>
    </div>
</div>

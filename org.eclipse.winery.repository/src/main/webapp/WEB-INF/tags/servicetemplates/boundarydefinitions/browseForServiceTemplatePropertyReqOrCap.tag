<%--
/*******************************************************************************
 * Copyright (c) 2013-2014 University of Stuttgart.
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

<%@tag pageEncoding="UTF-8"%>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="bd" tagdir="/WEB-INF/tags/servicetemplates/boundarydefinitions" %>

<%@attribute name="definedPropertiesAsJSONString" required="true" %>

<div class="modal fade z1051" id="browseForServiceTemplatePropertyDiag" >
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Choose Property of Service Template</h4>
            </div>

            <div class="modal-body">
                <p class="text-info">Please click on a node to select the element</p>
                <div id='propertymappingstree'></div>
                <form>
                    <fieldset>
                        <div class="form-group">
                            <label for="newServiceTemplatePropertyRef">Reference to the property of the Service Template</label>
                            <input type="text" id="newServiceTemplatePropertyRef" class="form-control" />
                        </div>
                    </fieldset>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" onclick="setServiceTemplatePropertyRef();">Set</button>
            </div>
        </div>
    </div>
</div>

<%-- Browse for property --%>
<%--
The following cannot be used as we return TWO things: the template and the property
<bd:browseForX XShort="Property" XLong="Node Template, Relationship Template, or directly a property" />
--%>
<div class="modal fade z1051" id="browseForTemplateProperty">
    <div class="modal-dialog" style="width:1000px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Choose Node Template, Relationship Template, or directly a property</h4>
            </div>

            <div class="modal-body">
                <p class="text-info">Please click on the desired element</p>
                <iframe id="topologyTemplatePreview" class="topologyTemplatePreviewSizing" src="topologytemplate/?view=propertySelection&script=${pageContext.request.contextPath}/js/boundaryDefinitionsXSelection.js"></iframe>
                <form>
                    <fieldset>
                        <div class="form-group">
                            <label for="newObjectRef">Reference to the object in the topology template</label>
                            <input type="text" id="newObjectRef" class="form-control newObjectRef" /> <%-- newObjectRef required as  --%>
                        </div>
                        <div class="form-group">
                            <label for="newObjectPropertyRef">Reference to the object's property</label>
                            <input type="text" id="newObjectPropertyRef" class="form-control" />
                        </div>
                    </fieldset>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" onclick="setTemplateAndTemplatePropertyRef();">Set</button>
            </div>
        </div>
    </div>
</div>

<bd:browseForReqOrCap label="Requirement" reqOrCap="Req" requirementsOrCapabilities="requirements"/>
<bd:browseForReqOrCap label="Capability"  reqOrCap="Cap" requirementsOrCapabilities="capabilities"/>

<script>
//global variable to hold the reference to the input field where the selection of the service template property should be written into
//Requried as both a property mapping and a property constraint refer to a property of the service template.
var fieldToWriteSelectedServiceTemplateProperty;

function setServiceTemplatePropertyRef() {
    fieldToWriteSelectedServiceTemplateProperty.val($("#newServiceTemplatePropertyRef").val());
    $("#browseForServiceTemplatePropertyDiag").modal("hide");
}

function browseForServiceTemplateProperty(field) {
    fieldToWriteSelectedServiceTemplateProperty = field;
    $("#newServiceTemplatePropertyRef").val(field.val());
    $("#browseForServiceTemplatePropertyDiag").modal("show");
}


/**
 * Opens topology and lets user select a node template, relationship template,
 */
function browseForTemplateAndProperty() {
    $("#newObjectRef").val($("#targetObjectRef").val());
    $("#newObjectPropertyRef").val($("#targetPropertyRef").val());
    $("#browseForTemplateProperty").modal("show");
}

function setTemplateAndTemplatePropertyRef() {
    $("#targetObjectRef").val($("#newObjectRef").val());
    // always copy over targetPropertyRef, even if it's empty
    $("#targetPropertyRef").val($("#newObjectPropertyRef").val());

    $("#browseForTemplateProperty").modal("hide");
}

<c:if test="${not empty definedPropertiesAsJSONString}">
// initialize the xmltree of the service template properties
// xmltree has to be inialized once and not more than once
// Therefore, we put it here and not in some shown events
require(["xmltree"], function(xmltree) {
    new xmltree({
        xml: '${definedPropertiesAsJSONString}',
        container: '#propertymappingstree',
        startCollapsed: false,
        clickCallback: serviceTemplatePropertyClicked
    });
});
</c:if>

function serviceTemplatePropertyClicked(li, xpath, event) {
    require(["winery-support"], function(ws) {
        var pathFragmentRegExp = ws.QName_RegExp + "(.*)";
        var pathFragmentPattern = new RegExp(pathFragmentRegExp);

        // Transform the XPath to an XPath being namespace unaware
        // This is required as the OpenTOSCA container does not implement XPath processing in a namespace-aware manner
        var fragments = xpath.split("/");
        var path = [];
        $(fragments).each(function(i,e) {
            var res = pathFragmentPattern.exec(e);
            if (res != null) {
                if (typeof res[1] !== undefined) {
                    e = "*[local-name()='" + res[3] + "']" + res[5];
                }
            }
            path.push(e);
        });
        xpath = path.join("/");

        $("#newServiceTemplatePropertyRef").val(xpath);
    });
}

/* communication with the iframe is at boundarydefinitions.jsp as we also need it for "Interfaces" */

</script>
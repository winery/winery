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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="o" tagdir="/WEB-INF/tags/common/orioneditor"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="w" uri="http://www.eclipse.org/winery/repository/functions"%>

<%-- Upload functionality inspired by plans.jsp. That code could be generalized somehow in a .tag file --%>

<ul class="nav nav-tabs" id="myTab">
    <li class="active"><a href="#description">Description</a></li>
    <li><a href="#images">Images</a></li>
    <li><a href="#options">Options</a></li>
    <li><a href="#xml" id="showXMLTab">XML</a></li>
</ul>

<div class="tab-content">

    <div class="tab-pane active" id="description">
        <div class="form-group">
            <label class="label-form">Name</label>
            <a href="#" class="form-control" data-send="always" id="displayName" data-url="selfserviceportal/displayname" data-tile="Enter Display Name">${it.application.displayName}</a>
        </div>

        <div class="form-group">
            <label class="label-form">Description</label>
            <div class="form-control" id="applicationDescriptionDiv">${it.application.description}</div>
        </div>
    </div>

    <div class="tab-pane" id="images">

        <t:imageUpload
        label="Icon"
        URL="selfserviceportal/icon.jpg"
        id="upIcon"
        width="16px"
        accept="image/*"/>

        <t:imageUpload
        label="Preview"
        URL="selfserviceportal/image.jpg"
        id="upImage"
        width="100px"
        accept="image/*"/>

    </div>

    <div class="tab-pane" id="options">
        <button class="rightbutton btn btn-xs btn-danger" name="remove" onclick="deleteOnServerAndInTable(optionsTableInfo, 'Option', 'selfserviceportal/options/', 0, 1);">Remove</button>
        <button class="rightbutton btn btn-xs btn-info" name="add" onclick="$('#addOptionDiag').modal('show');">Add</button>
        <!--  <button class="rightbutton btn btn-xs btn-default" name="edit" onclick="openOptionEditor();">Edit</button> -->

        <table id="optionsTable">
            <thead>
                <tr>
                    <th>Id</th>
                    <th>Name</th>
                    <th>Icon</th>
                    <th>Plan Service Name</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${not empty it.application}">
                    <c:forEach var="option" items="${it.application.options.option}">
                        <tr>
                            <td>${option.id}</td>
                            <td>${option.name}</td>
                            <td><img src="selfserviceportal/options/${w:URLencode(option.id)}/icon.jpg" style="width:50px;"></td>
                            <td>${option.planServiceName}</td>
                        </tr>
                    </c:forEach>
                </c:if>
            </tbody>
        </table>
    </div>

    <div class="tab-pane" id="xml">
        <o:orioneditorarea areaid="XML" url="selfserviceportal/" reloadAfterSuccess="true">${it.applicationAsXMLStringEncoded}</o:orioneditorarea>
    </div>

</div>

<script>
function letUserChooseAFile() {
    $('#fileInput').trigger('click');
    $('#chooseBtn').focus();
}

$('#showXMLTab').on('shown.bs.tab', function (e) {
    window.winery.orionareas['XML'].fixEditorHeight();
});
</script>

<div class="modal fade" id="addOptionDiag">
    <div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h4 class="modal-title">Add Option</h4>
        </div>
        <div class="modal-body">
            <form id="addOptionForm" enctype="multipart/form-data" action="selfserviceportal/options/" method="post">
                <div class="form-group">
                    <label class="control-label">Name</label>
                    <input name="name" type="text" class="form-control" required="required">
                </div>

                <div class="form-group">
                    <label class="control-label">Description</label>
                    <textarea id="optionDescription" name="description" class="form-control" required="required"></textarea>
                </div>

                <div class="form-group">
                    <label class="control-label" for="iconDiv">Icon</label>
                    <div style="display: block; width: 100%" id="iconDiv">
                        <input id="fileInput" name="file" type="file" style="display:none" accept="image/*">
                        <input name="fileText" id="fileText" type="text" class="form-control" style="width:300px; display:inline;" onclick="letUserChooseAFile();" required="required">
                        <button type="button" id="chooseBtn" class="btn btn-default btn-xs" onclick="letUserChooseAFile();">Choose</button>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label">Plan Service Name</label>
                    <input name="planServiceName" type="text" class="form-control" required="required">
                </div>

                <div class="form-group">
                    <label class="control-label">Plan Input Message</label>
                    <textarea name="planInputMessage" class="form-control" required="required" rows="20">&lt;soapenv:Envelope xmlns:soapenv=&quot;http://schemas.xmlsoap.org/soap/envelope/&quot;&gt;
&lt;soapenv:Header/&gt;
&lt;soapenv:Body&gt;
&lt;/soapenv:Body&gt;
&lt;/soapenv:Envelope&gt;</textarea>
                </div>

            </form>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-primary" data-loading-text="Uploading..." id="addOptionBtn">Add</button>
        </div>
    </div>
    </div>
</div>


<script>
$("#displayName").editable({
    ajaxOptions: {type: "PUT"},
    success: function() {
        vShowSuccess("Successfully updated display name");
    },
    error: function(response) {
        vShowError("Could not update display name: " + response.status + " " + response.responseText);
    }
});

$("#applicationDescriptionDiv").editable({
    type: "wysihtml5",
    send: "always",
    url: "selfserviceportal/description",
    ajaxOptions: {type: "PUT"},
    success: function() {
        vShowSuccess("Successfully updated description");
    },
    error: function(response) {
        vShowError("Could not update description: " + response.status + " " + response.responseText);
    }
});

$("#optionDescription").wysihtml5();

var optionsTableInfo = {
    id: '#optionsTable'
};

$('#myTab a').click(function (e) {
    e.preventDefault();
    $(this).tab('show');
});

$(function() {
    // initialize table and hide first column
    require(["winery-support"], function(ws) {
        ws.initTable(optionsTableInfo, {
            "aoColumnDefs": [
                { "bSearchable": false, "bVisible": false, "aTargets": [ 0 ] }
            ]
        });
    });

    $("#addOptionDiag").on("hidden.bs.modal", function() {
        // we currently do not send data back from the server
        // we emulate the AJAX refresh by a reaload
        doTheTabSelection(function() {
            $('#myTab a[href="#options"]').tab('show');
        });
    });
});

function createOption(data) {
    if (highlightRequiredFields()) {
        vShowError("Please fill out all required fields");
        return;
    }
    data.submit();
}

requirejs(["jquery.fileupload"], function(){
    $('#addOptionForm').fileupload({
        // dropping should only be available in the addOptionDialog. This, however, does not work correctly
        dropZone: $("#addOptionDiag")
    }).bind("fileuploadadd", function(e, data) {
        $.each(data.files, function (index, file) {
            $("#fileText").val(file.name);
        });
        $("#addOptionBtn").off("click");
        $("#addOptionBtn").on("click", function() {
            createOption(data);
        });
    }).bind("fileuploadstart", function(e) {
        $("#addOptionBtn").button("loading");
    }).bind('fileuploadfail', function(e, data) {
        vShowAJAXError("Could not add option", data.jqXHR, data.errorThrown);
        $("#addOptionBtn").button("reset");
    }).bind('fileuploaddone', function(e, data) {
        vShowSuccess("Option created successfully.");

        // reset the add button
        $("#addOptionBtn").button("reset");
        // do not allow submission of the old files on a click if the dialog is opened another time
        $("#addOptionBtn").off("click");

        // TODO: add data
        //embeddedPlansTableInfo.table.fnAddData(data.result.tableData);
        // current workaround: event on hidden.bs.modal

        $('#addOptionDiag').modal('hide');
    });
});

</script>

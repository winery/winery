<%--
/*******************************************************************************
 * Copyright (c) 2014 University of Stuttgart.
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

<%@attribute name="label" description="Requirement|Capability" required="true" %>
<%@attribute name="requirementsOrCapabilities" description="requirements|capabilities" required="true" %>
<%@attribute name="reqOrCap" description="requirement|capability" required="true" %>

<%@taglib prefix="b"  tagdir="/WEB-INF/tags/servicetemplates/boundarydefinitions"%>

<b:browseForX XShort="${reqOrCap}" XLong="${label}" />

<div class="modal fade" id="${reqOrCap}Diag">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title"><span id="addOrUpdate${reqOrCap}Span"></span> ${label}</h4>
            </div>
            <div class="modal-body">
                <form>
                    <fieldset>
                        <div class="form-group">
                            <label for="${reqOrCap}Name">Name</label>
                            <div>
                                <input name="${reqOrCap}Name" id="${reqOrCap}Name" class="form-control" type="text">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="${reqOrCap}RefDiv">${label}</label>
                            <div id="${reqOrCap}RefDiv" class="row">
                                <div class="col-xs-10">
                                    <input id="X${reqOrCap}Ref" class="form-control" type="text"> <%-- The input id is prefixed with "X" as "requirementRef" alone does not work --%>
                                </div>
                                <div class="col-xs-2">
                                    <button type="button" class="btn btn-default btn-sm" onclick="browseFor${reqOrCap}($('#XReqRef'));">Browse</button>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button id="add${reqOrCap}"    type="button" class="btn btn-primary" onclick="addorUpdate${reqOrCap}(false);">Add</button>
                <button id="delete${reqOrCap}" type="button" class="btn btn-danger" onclick="delete${reqOrCap}t();">Delete</button>
                <button id="update${reqOrCap}" type="button" class="btn btn-primary" onclick="addorUpdate${reqOrCap}(true);">Update</button>
            </div>
        </div>
    </div>
</div>


<script>
/**
 * Triggered by the browse button at requirements. In the dialog Add/Change Requirement
 */
function browseFor${reqOrCap}(field) {
    $("#${reqOrCap}ReferenceField").val($("#X${reqOrCap}Ref").val());
    $("#browseFor${reqOrCap}Diag").modal("show");
}

/**
 * Called by click on "Set" button at the browseForReqDiag
 */
function set${reqOrCap}Ref() {
    $("#X${reqOrCap}Ref").val($("#${reqOrCap}ReferenceField").val());
    $("#browseFor${reqOrCap}Diag").modal("hide");
}

/**
 * Called from the modal after the user clicks "Add" or "Delete"
 */
function addorUpdate${reqOrCap}(update) {
    var data = {
        name: $("#${reqOrCap}Name").val(),
        ref: $("#X${reqOrCap}Ref").val()
    }

    $.ajax({
        url: "boundarydefinitions/${requirementsOrCapabilities}/",
        data: data,
        type: "POST"
    }).fail(function(jqXHR, textStatus, errorThrown) {
        vShowAJAXError("Could not add ${label}", jqXHR, errorThrown);
    }).done(function(id) {
        // data is the new id
        var tableRow = [id, data.name, data.ref];
        ${requirementsOrCapabilities}TableInfo.table.fnAddData(tableRow);

        if (update) {
            // update is implemented as delete + recreate
            // after successfull creation, we can delete
            deleteOnServerAndInTable(${requirementsOrCapabilities}TableInfo, '${label}', 'boundarydefinitions/${requirementsOrCapabilities}/', undefined, undefined, undefined, true);
            // TODO: we should hook into onSuccess/onError, but currently these are not exposed from deleteResource to deleteOnServerAndInTable
            vShowSuccess("Successfully updated ${label}.");
        } else {
            vShowSuccess("Successfully added ${label}.");
        }


        $('#${reqOrCap}Diag').modal('hide');
    });
};

function deleteRequirement() {
    $('#${reqOrCap}Diag').modal('hide');
    $("#delete${reqOrCap}").click();
}

/**
 * Called from the buttons in the table if the user clicks "Add" or "Edit"
 */
function open${reqOrCap}Editor(update) {
    if (update) {
        if (${requirementsOrCapabilities}TableInfo.selectedRow) {
            require(["winery-support"], function(ws) {
                if (ws.isEmptyTable(${requirementsOrCapabilities}TableInfo)) {
                    vShowError("No ${requirementsOrCapabilities} available");
                    return;
                }

                var data = ${requirementsOrCapabilities}TableInfo.table.fnGetData(${requirementsOrCapabilities}TableInfo.table.selectedRow);
                // we don't require the id as deleteOnServerAndInTable automatically deletes the selectedRow
                $("#${reqOrCap}Name").val(data[0][1]);
                $("#X${reqOrCap}Ref").val(data[0][2]);

                $("#add${reqOrCap}").hide();
                $("#update${reqOrCap}").show();
                $("#delete${reqOrCap}").show();

                $("#addOrUpdate${reqOrCap}Span").text("Change");
                $('#${reqOrCap}Diag').modal('show');
            });
        } else {
            vShowError("No ${label} selected");
        }
    } else {
        // create a new req/cap
        $("#add${reqOrCap}").show();
        $("#update${reqOrCap}").hide();
        $("#delete${reqOrCap}").hide();
        $("#addOrUpdate${reqOrCap}Span").text("Add");
        $('#${reqOrCap}Diag').modal('show');
    }
}

</script>

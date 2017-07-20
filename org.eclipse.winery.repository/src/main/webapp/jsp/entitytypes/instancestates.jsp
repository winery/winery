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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal fade" id="addStateDiag">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Add State</h4>
            </div>
            <div class="modal-body">
                <form id="addPropertyForm" enctype="multipart/form-data" method="post">
                    <div class="row">
                        <label>
                            State: <input name="state" id="state" type="text" />
                        </label>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="createState();">Add</button>
            </div>
        </div>
    </div>
</div>

<div id="Properties">
    <button class="rightbutton btn btn-danger btn-xs" type="button" onclick="deleteOnServerAndInTable(propertiesTableInfo, 'InstanceState', 'instancestates/');">Remove</button>
    <button class="rightbutton btn btn-primary btn-xs" type="button" onclick="$('#addStateDiag').modal('show');">Add</button>

    <table cellpadding="0" cellspacing="0" border="0" class="display" id="propertiesTable">
        <thead>
            <tr>
                <th>State</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="t" items="${it.instanceStates}">
                <tr>
                    <td>${t}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<script>
var propertiesTableInfo = {
        id: '#propertiesTable'
};

require(["winery-support"], function(ws) {
    ws.initTable(propertiesTableInfo);
});

function createState() {
    $.ajax({
        url: "instancestates/",
        type: "POST",
        async: false,
        data: $('#addPropertyForm').serialize(),
        error: function(jqXHR, textStatus, errorThrown) {
            vShowError("Could not add instancestate: " + errorThrown + "<br/>" + jqXHR.responseText);
        },
        success: function(data, textStatus, jqXHR) {
            var dataToAdd = [$('#state').val()];
            propertiesTableInfo.table.fnAddData(dataToAdd);
            $('#addStateDiag').modal('hide');
        }
    });
}
</script>

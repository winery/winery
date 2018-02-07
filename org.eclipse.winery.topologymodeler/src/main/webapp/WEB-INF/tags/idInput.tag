<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright (c) 2014 Contributors to the Eclipse Foundation
  ~
  ~ See the NOTICE file(s) distributed with this work for additional
  ~ information regarding copyright ownership.
  ~
  ~ This program and the accompanying materials are made available under the
  ~ terms of the Eclipse Public License 2.0 which is available at
  ~ http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
  ~ which is available at https://www.apache.org/licenses/LICENSE-2.0.
  ~
  ~ SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>
<%@tag
    description="input field for an id unique in the target namespace. Current implementation: Unique in the topology modeler"
    pageEncoding="UTF-8" %>

<%@attribute name="inputFieldId" required="true" description="The name and id of the input field" %>

<div class="form-group" id="${inputFieldId}Group">
    <label for="${inputFieldId}" class="control-label">Id:</label>
    <input id="${inputFieldId}" class="form-control" name="${inputFieldId}" type="text" required="required"/>
</div>

<script>
    $("#${inputFieldId}").typing({
        stop: function (evt, elem) {
            // check for existinance in the current model
            // TODO: global check using the backend
            var isSuccess;
            try {
                var val = elem.val();
                isSuccess = (val != "") && ($("#" + elem.val()).length == 0);
            } catch (err) {
                // all syntax errors are invalid inputs
                isSuccess = false;
            }
            var newClass = (isSuccess ? "has-success" : "has-error");
            var div = elem.parent();
            if (!div.hasClass(newClass)) {
                div.removeClass("has-error").removeClass("has-success").addClass(newClass);
            }
        }
    });
</script>

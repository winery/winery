<%--
/*******************************************************************************
 * Copyright (c) 2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
--%>
<%@tag description="input field for an id unique in the target namespace. Current implementation: Unique in the topology modeler" pageEncoding="UTF-8"%>

<%@attribute name="inputFieldId" required="true" description="The name and id of the input field"%>

<div class="form-group" id="${inputFieldId}Group">
	<label for="${inputFieldId}" class="control-label">Id:</label>
	<input id="${inputFieldId}" class="form-control" name="${inputFieldId}" type="text" required="required" />
</div>

<script>
$("#${inputFieldId}").typing({
	stop: function(evt, elem) {
		// check for existinance in the current model
		// TODO: global check using the backend
		var isSuccess;
		try {
			var val = elem.val();
			isSuccess = (val != "") && ($("#" + elem.val()).length == 0);
		} catch(err) {
			// all syntax errors are invalid inputs
			isSuccess = false;
		}
		var newClass = (isSuccess? "has-success" : "has-error");
		var div = elem.parent();
		if (!div.hasClass(newClass)) {
			div.removeClass("has-error").removeClass("has-success").addClass(newClass);
		}
	}
});
</script>

<%--
/*******************************************************************************
 * Copyright (c) 2012-2014 University of Stuttgart.
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
<%@tag description="Renders pairs of types with shortname as select element" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@attribute name="label" required="true"%>
<%@attribute name="selectname" required="true" description="Used as Name and as Id"%>
<%@attribute name="typesWithShortNames" required="true" type="java.util.Collection"%>
<%@attribute name="type" required="true" description="The type of all types. E.g., planlanguage"%>

<div class="form-group">
<label for="${selectname}">${label}</label>

<div style="display: block; width: 100%">
	<select name="${selectname}" id="${selectname}" style="width:300px;">
		<c:forEach var="t" items="${typesWithShortNames}">
			<option value="${t.type}">${t.shortName}</option>
		</c:forEach>
	</select>
	<button type="button" class="btn btn-info btn-xs" onclick="updateTypesWithShortNames();">Refresh</button>
	<a href="${pageContext.request.contextPath}/admin/#${type}s" class="btn btn-info btn-xs" target="_blank">Manage</a>
</div>
</div>

<script>
function updateTypesWithShortNames() {
	vShowNotification('not yet implemented')
	/* Implementation idea:
		* get on ...${type}s resource with app/json and ?select2 - this is the direct select2 data
		* replace select element with input: select2 cannot update an input element
	*/
}

</script>

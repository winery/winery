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
<%@tag description="Input or Output parameters" pageEncoding="UTF-8"%>

<%@attribute name="label" required="true" %>
<%@attribute name="inOrOut" required="true" %>
<%@attribute name="tableId" required="true" %>
<%@attribute name="baseURL" required="true" description="JavaScript expression for determining the baseURL"%>

<div class="row">
	<div class="row listheading">
		<button class="rightbutton btn btn-danger btn-xs" type="button" onclick="delete${inOrOut}putParameter(${baseURL});" id="remove${inOrOut}ParBtn">Remove</button>
		<button class="rightbutton btn btn-primary btn-xs" type="button" onclick="create${inOrOut}putParameter(${baseURL});" id="add${inOrOut}ParBtn">Add</button>
		<label>${label}</label>
	</div>
	<table class="row" id="${tableId}">
		<thead>
			<tr>
			<th>Name</th>
			<th>Type</th>
			<th>Required</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

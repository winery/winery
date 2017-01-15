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
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="form-group">
	<label class="control-label" for="form-control">Abstract</label>
	<select class="form-control" style="width:100px;" id="isAbstract" onchange="updateValue('abstract', this.options[this.selectedIndex].value);">
		<option value="yes"<c:if test="${it.isAbstract=='yes'}"> selected="selected"</c:if>>yes</option>
		<option value="no"<c:if test="${it.isAbstract=='no'}"> selected="selected"</c:if>>no</option>
	</select>
</div>

<div class="form-group">
	<label class="control-label">Final</label>
	<select class="form-control" style="width:100px;" id="isFinal" onchange="updateValue('final', this.options[this.selectedIndex].value);">
		<option value="yes"<c:if test="${it.isFinal=='yes'}"> selected="selected"</c:if>>yes</option>
		<option value="no"<c:if test="${it.isFinal=='no'}"> selected="selected"</c:if>>no</option>
	</select>
</div>

<div class="form-group">
	<label class="control-label">Derived from</label>
	<div style="display: block; width: 100%">
		<select class="form-control" style="width:600px; display:inline; margin-right: 10px;" id="derivedFrom" onchange="updateValue('derivedFrom', this.options[this.selectedIndex].value);">
			<option value=""<c:if test="${empty it.derivedFrom}"> selected="selected"</c:if>>(none)</option>
			<c:forEach items="${it.possibleSuperTypes}" var="type">
				<c:set var="value" value="${type.QName}" />
				<option value="${value}"<c:if test="${value==it.derivedFrom}"> selected="selected"</c:if>>${type.xmlId.decoded}</option>
			</c:forEach>
		</select>
		<button type="button" class="btn btn-info btn-sm" onclick="openSuperType();">Open</button>
	</div>
</div>

<script>
function openSuperType() {
	require(["winery-support-common"], function(w) {
		var qname = $("#derivedFrom").val();
		var fragment = w.getURLFragmentOutOfFullQName(qname);
		window.location = "../../" + fragment + "/";
	});
}
</script>

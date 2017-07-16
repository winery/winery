<%--
/*******************************************************************************
 * Copyright (c) 2012-2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *	  Niko Stadelmaier - removal of select2 library
 *******************************************************************************/
--%>
<%@tag description="Dialog parts for choosing a QName" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions"%>

<%@attribute name="allQNames" required="true" type="java.util.Collection" description="Collection&lt;QName&gt; of all available QNames" %>
<%@attribute name="includeNONE" required="false" type="java.lang.Boolean" description="Should (none) be included as option?"%>
<%@attribute name="selected" required="false" description="The initial value to select"%>
<%@attribute name="labelOfSelectField" required="true"%>
<%@attribute name="idOfSelectField" required="true"%>

<div class="form-group">
	<c:if test="${not empty labelOfSelectField}"><label for="${idOfSelectField}" class="control-label">${labelOfSelectField}:</label></c:if>
	<select id="${idOfSelectField}" name="${idOfSelectField}" class="form-control">
		<c:if test="${includeNONE}"><option value="(none)">(none)</option></c:if>
		<c:forEach var="namespaceEntry" items="${wc:convertQNameListToNamespaceToLocalNameList(allQNames)}">
			<optgroup label="${namespaceEntry.key}">
				<c:forEach var="localName" items="${namespaceEntry.value}">
					<option value="{${namespaceEntry.key}}${localName}">${localName}</option>
				</c:forEach>
			</optgroup>
		</c:forEach>
	</select>
</div>

<script>
$(function(){
	<c:if test="${not empty selected}">
		$("#${idOfSelectField}").val(${selected});
	</c:if>
});
</script>

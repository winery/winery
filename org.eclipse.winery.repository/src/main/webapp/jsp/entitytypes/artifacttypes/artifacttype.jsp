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
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%--
TODO: implement update / subresource "file extension"
<div class="center">Associated File Extension</div>

	<div class="middle" id="ccontainer">
		<input name="fileextension" type="text" onblur="updateValue('fileextension', this.value)" <c:if test="${not empty it.associatedFileExtension}"> value="${it.associatedFileExtension}" </c:if> />
	</div>
--%>

<t:entitytype cssClass="artifactType" selected="ArtifactType">
</t:entitytype>

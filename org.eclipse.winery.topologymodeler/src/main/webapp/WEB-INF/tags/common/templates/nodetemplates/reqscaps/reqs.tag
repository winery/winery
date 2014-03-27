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
<%@tag description="Renders the list of requirements or capabilties" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="nt" tagdir="/WEB-INF/tags/common/templates/nodetemplates/reqscaps" %>

<%@attribute name="client" required="true" description="IWineryRepository" type="org.eclipse.winery.common.interfaces.IWineryRepository"%>
<%@attribute name="list" required="false" type="java.util.List"%>
<%@attribute name="repositoryURL" required="true" %>
<%@attribute name="pathToImages" required="true" description="The path (URI path) to the image/ url, where xml.png is available. Has to end with '/'"%>

<nt:reqsorcaps
	headerLabel="Requirements"
	cssClassPrefix="requirements"
	list="${list}"
	shortName="Req"
	TReqOrCapTypeClass="<%=org.eclipse.winery.model.tosca.TRequirementType.class%>"
	repositoryURL="${repositoryURL}"
	typeURLFragment="requirementtypes"
	pathToImages="${pathToImages}"
	client="${client}"
/>

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
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags/entitytypes/nodetypes/reqandcapdefs" %>

<t:reqandcapdefs allTypes="${it.allTypes}" allSubResources="${it.allEntityResources}" url="capabilitydefinitions/" labelForSingleItem="Capability Definition" typeClass="<%=org.eclipse.winery.model.tosca.TRequirementType.class%>"/>

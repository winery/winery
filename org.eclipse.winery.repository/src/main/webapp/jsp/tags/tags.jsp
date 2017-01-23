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
 *    Kálmán Képes
 *******************************************************************************/
--%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="w" uri="http://www.eclipse.org/winery/repository/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:kvtablediag allSubResources="${it.allEntityResources}" url="tags/" labelForSingleItem="Tag" typeClass="<%=org.eclipse.winery.model.tosca.TTag.class%>"/>

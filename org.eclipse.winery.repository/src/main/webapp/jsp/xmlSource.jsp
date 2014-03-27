<%--
/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
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

<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions" %>
<%@taglib prefix="o"  tagdir="/WEB-INF/tags/common/orioneditor"%>

<o:orioneditorarea areaid="XML">${wc:escapeHtml4(it.definitionsAsXMLString)}</o:orioneditorarea>

<p class="text-muted">Save leads to a synchronization with the other tabs</p>
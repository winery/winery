<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
  ~
  ~ See the NOTICE file(s) distributed with this work for additional
  ~ information regarding copyright ownership.
  ~
  ~ This program and the accompanying materials are made available under the
  ~ terms of the Eclipse Public License 2.0 which is available at
  ~ http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
  ~ which is available at https://www.apache.org/licenses/LICENSE-2.0.
  ~
  ~ SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>
<%@tag description="Dialog parts for name and type choosing" pageEncoding="UTF-8" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions" %>
<%@taglib prefix="w" tagdir="/WEB-INF/tags/common" %>


<%@attribute name="allTypes" required="true" type="java.util.Collection"
             description="Collection&lt;QName&gt; of all available types" %>
<%@attribute name="idPrefix" required="true"
             description="prefix used for name and type field. E.g., 'Req' becomes 'ReqType'." %>
<%@attribute name="hideIdField" required="false"
             description="if given, id field is not displayed. Quick hack to have this dialog reusable. Future versions might always show the id dialog and provide sync between name and id" %>

<c:if test="${not hideIdField}">
    <div class="form-group">
        <label for="${idPrefix}Id" class="control-label">Id:</label>
        <input id="${idPrefix}Id" class="form-control" name="${shortName}Name" type="text" required="required"
               disabled="disabled"/>
    </div>
</c:if>
<div class="form-group">
    <label for="${idPrefix}Name" class="control-label">Name:</label>
    <input id="${idPrefix}Name" class="form-control" name="${shortName}Name" type="text" required="required"/>
</div>

<w:QNameChooser allQNames="${allTypes}" idOfSelectField="${idPrefix}Type" labelOfSelectField="Type"/>

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
<%@tag description="form div to upload an icon" pageEncoding="UTF-8"%>

<%@attribute name="label" required="true" description="LAbel to be used. Also used as title of the dialog"%>
<%@attribute name="URL" required="true" description="URL to post to"%>
<%@attribute name="id" required="true" description="id to form basis for ...Diag: id of diag; ...Form: id of input field used for file upload; ...Img: Image to refresh"%>
<%@attribute name="accept" description="if not null/'': list of accepted MIME file types"%>
<%@attribute name="width" required="true" description="Width of the image to display"%>
<%@attribute name="resize" description="if not null/'': enables image resizing. Currently not supported"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:simpleSingleFileUpload
    title="Upload ${label}"
    text="File"
    additionalDropZone="#${id}FormGroup"
    URL="${URL}"
    type="PUT"
    id="${id}"
    accept="${accept}"/>

<div id="${id}FormGroup" class="form-group">
    <label for="${id}DisplayDiv">${label}</label>
    <div id="${id}DisplayDiv" style="width:100%">
        <div class="col-md-2">
            <a href="${URL}" target="_blank"><img id="${id}Img" style="width:${width};" src="${URL}" alt="n/a" /></a>
        </div>
        <button class="btn btn-default btn-xs" type="button" onClick="$('#${id}Diag').modal('show');">Upload...</button> or drop the image in this area.
    </div>
</div>

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
 *    Yves Schubert - switch to bootstrap 3
 *******************************************************************************/
--%>
<%@tag description="Global Wrapper" pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@attribute name="title" required="true" description="title of the dialog"%>
<%@attribute name="text" required="true" description="text to show before upload box"%>
<%@attribute name="URL" required="true" description="URL to post to"%>
<%@attribute name="type" required="true" description="PUT|POST"%>
<%@attribute name="additionalDropZone" required="false" description="jQuery selector for an additional dropzone"%>
<%@attribute name="id" required="true" description="id to form basis for ...Diag: id of diag; ...Form: id of input field used for file upload; ...Img: Image to refresh"%>
<%@attribute name="accept" description="if not null/'': list of accepted MIME file types"%>
<%@attribute name="resize" description="if not null/'': enables image resizing. Currently not supported"%>

<div class="modal fade" id="${id}Diag">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">${title}</h4>
            </div>
            <div class="modal-body">
                <form>
                    <fieldset>
                        <div class="form-group">
                            <label for="${id}Form">${text}:</label>
                            <input id="${id}Form" class="form-control" type="file" name="${id}Form" <c:if test="${!empty accept}">accept="${accept}"</c:if> />
                        </div>
                    </fieldset>
                    <p>You may also <strong>drop the file</strong> here.</p>
                    <p>The file is <strong>immediately</strong> uploaded without any confirmation.</p>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" data-loading-text="Uploading..." id="cancelfileuploadbtn">Cancel</button>
            </div>
        </div>
    </div>
</div>

<script>
/**
 * We cannot use jQuery's ",", because this leads to multiple uploads at a single drop.
 * Therefore, we introduced that function
 */
function bindFileUploadForSingleFileUpload(selector) {
    requirejs(["jquery.fileupload"], function(){
        $(selector).fileupload({
            dataType: 'json',
            url: '${URL}',
            type: '${type}',
            dropZone: $(selector),
            paramName: 'file',
            autoUpload: true
        }).bind("fileuploadstart", function(e) {
            $("#cancelfileuploadbtn").button("loading");
        }).bind('fileuploadfail', function(e, data) {
            vShowAJAXError("File upload failed", data.jqXHR, data.errorThrown);
            $("#cancelfileuploadbtn").button("reset");
        }).bind('fileuploaddone', function(e, data) {
            var text = "File uploaded successfully.";
            var responseText = data.jqXHR.responseText;
            if (responseText != "") {
                // we expect a JSON array
                var response = $.parseJSON(responseText);
                if (response.length == 0) {
                    // some JSON parsing error, just display the text itself
                    text = text + "<br /><br />With following issues, possibly wrong<br />" + responseText;
                } else if (response.length == 1) {
                    text = text + "<br /><br />With following issue<br />" + response[0];
                } else {
                    text = text + "<br /><br />With following issues, possibly wrong<br /><ul>";
                    $(response).each(function(i,e) {
                        text = text + "<li>" + e + "</li>";
                    });
                    text = text + "</ul>";
                }
            }
            vShowSuccess(text);
            $("#cancelfileuploadbtn").button("reset");
            $('#${id}Diag').modal('hide');

            // refresh the image
            var img = $('#${id}Img');
            if (img.length === 1) {
                var src = img.attr('src');
                var queryPos = src.indexOf('?');
                if (queryPos != -1) {
                    src = src.substring(0, queryPos);
                }
                img.attr('src', src + '?' + Math.random());
            }
        });
    });
}

$(function() {
    bindFileUploadForSingleFileUpload("#${id}Diag");
    <c:if test="${not empty additionalDropZone}">bindFileUploadForSingleFileUpload("${additionalDropZone}");</c:if>
});
</script>

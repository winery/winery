<%--
/*******************************************************************************
 * Copyright (c) 2013-2014 University of Stuttgart.
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
<%@tag description="Wrapper for an orion editing area" pageEncoding="UTF-8"%>

<%@attribute name="areaid" required="true" description="The id of the editing area."%>
<%@attribute name="withoutsavebutton" required="false"%>
<%@attribute name="initialtext" required="false" description="The value to put in the editor. Can be also passed as body of this tag"%>
<%@attribute name="url" required="false"%>
<%@attribute name="hidden" required="false" description="if not empty, the form is hidden"%>
<%@attribute name="method" required="false" description="the method to use. Defaults to PUT"%>

<%-- QUICK HACK to change the method from POST to PUT after saving an empty documentation the first time --%>
<%@attribute name="reloadAfterSuccess" required="false" description="Trigger a page reload after success (if true)"%>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions" %>

<div>
	<div id="${areaid}" class="orionxmleditordiv" <c:if test="{$not empty hidden}">style="display: none;"</c:if>><pre>${wc:escapeHtml4(initialtext)}<jsp:doBody/></pre></div>
	<c:if test="${empty withoutsavebutton}">
		<button class="btn btn-primary" type="button" onclick="window.winery.orionareas['${areaid}'].save(this);" data-loading-text="Saving...">Save</button>
	</c:if>
</div>

<script>
if (window.winery === undefined) {
	window.winery = {};
}
if (window.winery.orionareas === undefined) {
	window.winery.orionareas = {};
}
require(["orioneditor"], function(edit) {
	var config = {
		id: "${areaid}", // used for URL update
		reloadAfterSuccess : "${reloadAfterSuccess}",
		editor: edit({
			contentType: "application/xml",
			parent: "${areaid}"
			// todo: we can set the initial text by the parameter "contents"
		}),
		ajaxOptions : {
			contentType: "text/xml"
		},
		fixEditorHeight: function() {
			// fix the editor
			// orion puts "height:0px" -> we remove that
			$("#${areaid}").removeAttr("style");
			// due to the CSS style, the height is 300px
			// "just" adapt the editor to that size
			this.editor.resize();
		},
		save: function(button) {
			var btn = $(button); // also works if button is undefined
			btn.button("loading");

			var options = this.ajaxOptions;
			options.data = this.editor.getText();

			// ensure that "config" variable is initialized within the ajax call
			var config = this;
			// the following code does not use "this" anymore as the "this" in the function references to the jqXHR instead of config
			$.ajax(options).done(function( data, textStatus, jqXHR ) {
				if (data !== undefined) {
					// data contains the new id
					url = url.replace(config.id, data);
					conifg.id = data;
					config.ajaxOptions.url = url;
				}
				if (config.reloadAfterSuccess) {
					location.reload();
				} else {
					vShowSuccess("sucessfully saved");
					btn.button("reset");
				}
			}).fail(function(jqXHR, textStatus, errorThrown) {
				vShowAJAXError("Could not add update XML", jqXHR, errorThrown);
				btn.button("reset");
			});
		}
	};

	// now, editor is defined
	// we cannot "fix" the appearance as the editor height determination does not work on hidden fields

	// url is an optional parameter to the .tag
	if ("${url}" != "") {
		config.ajaxOptions.url = "${url}";
	}
	// method is an optional parameter to the .tag
	if ("${method}" == "") {
		config.ajaxOptions.type = "PUT";
	} else {
		config.ajaxOptions.type = "${method}";
	}

	// store the config in global variable
	window.winery.orionareas["${areaid}"] = config;
});
</script>

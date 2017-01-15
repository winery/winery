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
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<script type='text/javascript' src='${pageContext.request.contextPath}/components/raphael/raphael.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/components/colorwheel/javascripts/colorwheel.js'></script>

<ul class="nav nav-tabs" id="myTab">
	<li class="active"><a href="#icon">Icon</a></li>
	<li><a href="#colors">Colors</a></li>
	<li><a href="#arrow">Arrow</a></li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="icon">
		<br />
		<t:imageUpload
			label="Icon (16x16) used in palette"
			URL="visualappearance/16x16"
			id="upSmall"
			width="16px"
			resize="16"
			accept="image/*"/>
	</div>

	<div class="tab-pane" id="colors">
		<br />
		<form>
			<fieldset>
				<t:colorwheel label="Line Color" color="${it.color}" id="color" url="color" />
				<t:colorwheel label="Hover Color" color="${it.hoverColor}" id="hovercolor" url="hovercolor" />
			</fieldset>
		</form>
	</div>

	<div class="tab-pane" id="arrowDiv">
		<br />
		<form>
			<fieldset>
				<div class="form-group">
					<label for="arrow">Arrow appearance</label>
					<div style="width:100%" id="arrow">
						<div style="float:left; width:50px;">
							<!-- Same values as the beginning of the file names in src\main\webapp\images\relationshiptype -->
							<select id="dropDownSourceHead">
								<option value="none"></option>
								<option value="PlainArrow"></option>
								<option value="Diamond"></option>
								<!--  not yet supported
								<option value="simpleArrow"></option>
								<option value="doubleArrow"></option>
								<option value="circle"></option>
								<option value="square"></option> -->
							</select>
						</div>
						<div style="float:left; width:80px;">
							<select id="lineSelect">
								<option value="plain"></option>
								<option value="dotted"></option>
								<option value="dotted2"></option>
							</select>
						</div>
						<div style="float:left; width:50px;">
							<select id="dropDownTargetHead">
								<option value="none"></option>
								<option value="PlainArrow"></option>
								<option value="Diamond"></option>
								<!--  not yet supported
								<option value="simpleArrow"></option>
								<option value="doubleArrow"></option>
								<option value="circle"></option>
								<option value="square"></option> -->
							</select>
						</div>
					</div>
				</div>
			</fieldset>
		</form>
	</div>
</div>

<script>
$('#myTab a').click(function (e) {
	e.preventDefault();
	$(this).tab('show');
});

/**
 * @param sourceOrTarget "Source" or "Target"
 */
function formatArrow(config, sourceOrTarget) {
	var path = "${pageContext.request.contextPath}/images/relationshiptype/" + config.id + sourceOrTarget + ".png";
	return "<img src='" + path +"' />";
}

var globalAJAXParamsForSelect2VisualAppearance = {
	type  : "PUT",
	contentType : "text/plain",
	success : function() {
		vShowSuccess("Successfully updated arrow appearance");
	},
	error : function(jqXHR, textStatus, errorThrown) {
		vShowAJAXError("Could not supdate arrow appearance", jqXHR, errorThrown);
	}
}

/* source arrow head */

function formatArrowSource(config) {
	return formatArrow(config, "Source");
}

// set stored value
$("#dropDownSourceHead").val("${it.sourceArrowHead}")
// enable storage on change of element
.on("change", function(e) {
	params = globalAJAXParamsForSelect2VisualAppearance;
	params.url = "visualappearance/sourcearrowhead";
	params.data = e.val;
	$.ajax(params);
})
// make the selection box show arrows
.select2({
	formatResult: formatArrowSource,
	formatSelection: formatArrowSource,
	escapeMarkup: function(m) { return m; },
	minimumResultsForSearch: -1
});


/* line */
function formatLine(config) {
	var path = "${pageContext.request.contextPath}/images/relationshiptype/" + config.id + "Line.png";
	return "<img src='" + path +"' />";
}

//set stored value
$("#lineSelect").val("${it.dash}")
//enable storage on change of element
.on("change", function(e) {
	params = globalAJAXParamsForSelect2VisualAppearance;
	params.url = "visualappearance/dash";
	params.data = e.val;
	$.ajax(params);
})
//make the selection box show arrows
.select2({
	formatResult: formatLine,
	formatSelection: formatLine,
	escapeMarkup: function(m) { return m; },
	minimumResultsForSearch: -1
});



/* target arrow head */

function formatArrowTarget(config) {
	return formatArrow(config, "Target");
}

//set stored value
$("#dropDownTargetHead").val("${it.targetArrowHead}")
//enable storage on change of element
.on("change", function(e) {
	params = globalAJAXParamsForSelect2VisualAppearance;
	params.url = "visualappearance/targetarrowhead";
	params.data = e.val;
	$.ajax(params);
})
//make the selection box show arrows
.select2({
	formatResult: formatArrowTarget,
	formatSelection: formatArrowTarget,
	escapeMarkup: function(m) { return m; },
	minimumResultsForSearch: -1
});

</script>


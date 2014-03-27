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
<%@tag description="initializes a color picker. Uses global variables cp_currentColorPicker, cp_oldColor, cp_written" pageEncoding="UTF-8"%>

<%@attribute name="elementId" required="true" description="id of the element to convert to a color picker"%>
<%@attribute name="url" required="true" description="URL to put to"%>
<%@attribute name="color" required="true" description="the initial color"%>

<script>
var cp_currentColorPicker;
var cp_oldColor;
var cp_written;

$(function() {
	$('#${elementId}').ColorPicker({
		onChange : function(hsb, hex, rgb) {
			$('#${elementId}').css('background-color', '#' + hex);
		},
		color: '${color}',
		onShow: function (colpkr) {
			cp_currentColorPicker = $(colpkr).fadeIn(500);
			cp_oldColor = $("#${elementId}").css('background-color');
			cp_written = false;
			return false;
		},
		onHide: function (colpkr) {
			if (!cp_written) {
				$("#${elementId}").css('background-color', cp_oldColor);
			}
			$(colpkr).fadeOut(500);
			return false;
		},
		onSubmit: function(hsb, hex, rgb) {
			cp_currentColorPicker.fadeOut(500);
			cp_written = true;
			putColor('${url}', hex);
			return false;
		}
	});
});
</script>

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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script>
/**
 * Displays a nice message box with "yes" and "no"
 *
 * TODO: currently cannot be recursively called
 *
 * @param msg      Message to display
 * @param fnOnYes  function to be called if user presses "yes"
 * @param title    (optional) title of the dialog
 */
function vConfirmYesNo(msg, fnOnYes, title) {
	title = title || "Please confirm";
	$("#diagyesnotitle").text(title);
	$("#diagyesnomsg").text(msg);
	$("#diagyesnoyesbtn").off("click");
	$("#diagyesnoyesbtn").on("click", function() {
		var diag = $("#diagyesno");
		// quick hack to get fnOnYes() working -> use the hidden.bs.modal event
		diag.on("hidden.bs.modal", function() {
			fnOnYes();
			diag.off("hidden.bs.modal");
		});
		diag.modal("hide");
	});
	$("#diagyesno").modal("show");
}

$(function() {
	$("#diagyesno").on("shown.bs.modal", function() {
		$("#diagyesnoyesbtn").focus();
	});
});
</script>

<div class="modal fade z1051" id="diagyesno">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="diagyesnotitle"></h4>
			</div>
			<div class="modal-body">
				<p id="diagyesnomsg"></p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">No</button>
				<button id="diagyesnoyesbtn" type="button" class="btn btn-primary">Yes</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade z1060" id="diagmessage">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="diagmessagetitle"></h4>
			</div>
			<div class="modal-body" id="diagmessagemsg">
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
			</div>
		</div>
	</div>
</div>

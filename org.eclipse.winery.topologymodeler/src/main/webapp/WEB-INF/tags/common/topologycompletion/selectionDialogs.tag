<%
/*******************************************************************************
 * Copyright (c) 2013 Pascal Hirmer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - initial API and implementation
 *******************************************************************************/
%>
<%@tag language="java" pageEncoding="UTF-8" description="This tag is used to create DIVs for the selection dialogs."%>

<%@attribute name="repositoryURL" type="java.lang.String"%>
<%@attribute name="serviceTemplateName" type="java.lang.String"%>
<%@attribute name="topologyTemplateURL" type="java.lang.String"%>

<!--
	Topology Completion: chooseRelationshipTemplateDiag.
	This dialog serves the user selection of inserted RelationshipTemplates whenever there are several possibilities.
-->
<div class="modal fade" id="chooseRelationshipTemplateDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Topology Completion - Relationship Template Selection</h4>
			</div>
			<div class="modal-body"></div>
			<div class="modal-footer"></div>
		</div>
	</div>
</div>

<!--
	Topology Completion: chooseNodeTemplateDiag.
	This dialog serves the user selection of inserted Node and RelationshipTemplates when the user selects "Complete topology step-by-step".
-->
<div class="modal fade" id="chooseNodeTemplateDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Topology Completion - Step by Step</h4>
			</div>
			<div class="modal-body"></div>
			<div class="modal-footer">
				<button type="button" class="btn btn-danger" data-dismiss="modal">Cancel</button>
			</div>
		</div>
	</div>
</div>

<!--
	Topology Completion: chooseTopologyDiag.
	This dialog serves the user selection of completed topologies.
-->
<div class="modal fade" id="chooseTopologyDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Topology Completion - Choose possible solution </h4>
			</div>
			<div class="modal-body"></div>
			<div class="modal-footer">
				<button type="button" class="btn btn-danger" data-dismiss="modal">Cancel</button>
			</div>
		</div>
	</div>
</div>

<!--
	Topology Completion: enterCompletionInformationDiag.
	This dialog serves the input of information before completing a topology automatically.
-->
<div class="modal fade" id="enterCompletionInformationDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Topology Completion</h4>
			</div>
			<div class="modal-body">
				<p> Select Save Option: </p>
				<form id="enterCompletionInformationForm" enctype="multipart/form-data">
					<fieldset>
						<ul>
							<li>
								<input type="radio" id="overwriteTopology" name="overwriteTopology"
									   onclick="document.getElementById('topologyNamespace').disabled = true; document.getElementById('topologyName').disabled = true; document.getElementById('openInNewWindow').disabled = true;" checked>
								<label for="overwriteTopology">Overwrite Topology</label>
							</li>
							<li>
								<input type="radio" id="createNewTopology" name="overwriteTopology"
									   onclick="document.getElementById('topologyNamespace').disabled = false; document.getElementById('topologyName').disabled = false;document.getElementById('openInNewWindow').disabled = false;">
								<label for="createNewTopology">Create new Topology</label>
							</li>
							<li>
							  Name: <input id="topologyName" name="topologyName" disabled="disabled" type="text" size="30"
											 maxlength="30">
							</li>
							<li>
							  Namespace: <input id="topologyNamespace" name="topologyNamespace" disabled="disabled"
												 type="text" size="50" maxlength="60">
							</li>
							<li>
								<input id="openInNewWindow" name="openInNewWindow" type="checkbox" disabled="disabled"/>
								<label for="openInNewWindow">Open Topology in new Window</label>
							</li>
							<li>
								<input id="completionStyle" name="completionStyle" type="checkbox"/>
								<label for="completionStyle">Complete Topology Step-by-Step</label>
							</li>
						</ul>
					</fieldset>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn" data-dismiss="modal">Cancel</button>
				<button type="button" class="btn btn-primary btn-default" id="btnCompleteTopology" onclick="onClickCompleteTopology()">Complete Topology</button>
				<script>
					function onClickCompleteTopology() {
						var namespace = document.getElementById('topologyNamespace').value;
						var validURIregexp = new RegExp("([A-Za-z][A-Za-z0-9+\\-.]*):(?:(//)(?:((?:[A-Za-z0-9\\-._~!$&'()*+,;=:]|%[0-9A-Fa-f]{2})*)@)?((?:\\[(?:(?:(?:(?:[0-9A-Fa-f]{1,4}:){6}|::(?:[0-9A-Fa-f]{1,4}:){5}|(?:[0-9A-Fa-f]{1,4})?::(?:[0-9A-Fa-f]{1,4}:){4}|(?:(?:[0-9A-Fa-f]{1,4}:){0,1}[0-9A-Fa-f]{1,4})?::(?:[0-9A-Fa-f]{1,4}:){3}|(?:(?:[0-9A-Fa-f]{1,4}:){0,2}[0-9A-Fa-f]{1,4})?::(?:[0-9A-Fa-f]{1,4}:){2}|(?:(?:[0-9A-Fa-f]{1,4}:){0,3}[0-9A-Fa-f]{1,4})?::[0-9A-Fa-f]{1,4}:|(?:(?:[0-9A-Fa-f]{1,4}:){0,4}[0-9A-Fa-f]{1,4})?::)(?:[0-9A-Fa-f]{1,4}:[0-9A-Fa-f]{1,4}|(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))|(?:(?:[0-9A-Fa-f]{1,4}:){0,5}[0-9A-Fa-f]{1,4})?::[0-9A-Fa-f]{1,4}|(?:(?:[0-9A-Fa-f]{1,4}:){0,6}[0-9A-Fa-f]{1,4})?::)|[Vv][0-9A-Fa-f]+\\.[A-Za-z0-9\\-._~!$&'()*+,;=:]+)\\]|(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|(?:[A-Za-z0-9\\-._~!$&'()*+,;=]|%[0-9A-Fa-f]{2})*))(?::([0-9]*))?((?:/(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})*)*)|/((?:(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})+(?:/(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})*)*)?)|((?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})+(?:/(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})*)*)|)(?:\\?((?:[A-Za-z0-9\\-._~!$&'()*+,;=:@/?]|%[0-9A-Fa-f]{2})*))?(?:\#((?:[A-Za-z0-9\\-._~!$&'()*+,;=:@/?]|%[0-9A-Fa-f]{2})*))?");
						if (!document.getElementById('overwriteTopology').checked && document.getElementById('topologyName').value == "") {
							vShowError("Please enter a name for the new topology.");
						} else if (!document.getElementById('overwriteTopology').checked && (document.getElementById('topologyNamespace').value == "" || !validURIregexp.test(namespace))) {
							vShowError("Please enter a valid name space for the new topology.");
						}
						else {
							$('#enterCompletionInformationDiag').modal('hide');

							require(["winery-topologycompletion"], function(completer) {
								completer.complete(document.getElementById('overwriteTopology').checked,document.getElementById('openInNewWindow').checked,document.getElementById('topologyName').value, document.getElementById('topologyNamespace').value, document.getElementById('completionStyle').checked,
										"<%=repositoryURL%>", "<%=serviceTemplateName%>", "<%=topologyTemplateURL%>");
							});

						}
					}
				</script>
			</div>
		</div>
	</div>
</div>

<script>
	$(function() {
		var chooseRelationshipTemplateDiag = $('#chooseRelationshipTemplateDiag');

		chooseRelationshipTemplateDiag.on('show', function() {
			$(this).find('form')[0].reset();
		});

		var chooseNodeTemplateDiag = $('#chooseNodeTemplateDiag');

		chooseNodeTemplateDiag.on('show', function() {
			$(this).find('form')[0].reset();
		});

		chooseNodeTemplateDiag.on('hidden.bs.modal', function () {
			for (var i = 0; i < Connections.length; i++) {
				jsPlumb.detach(Connections[i]);
			}
			$(document.getElementById("nodeTemplateSelector")).remove();
		});

		var chooseTopologyDiag = $('#chooseTopologyDiag');

		chooseTopologyDiag.on('show', function() {
			$(this).find('form')[0].reset();
		});

		chooseTopologyDiag.on('hidden.bs.modal', function () {
			for (var i = 0; i < Connections.length; i++) {
				jsPlumb.detach(Connections[i]);
			}
			$(document.getElementById("topologyTemplateSelector")).remove();
		});

		var enterCompletionInformationDiag = $('#enterCompletionInformationDiag');

		enterCompletionInformationDiag.on('show', function() {
			$(this).find('form')[0].reset();
		});
	});

	/**
	 * This function is invoked when the button "Complete Topology" is
	 * selected. It will open a dialog to enter necessary information for the
	 * completion.
	 */
	function completeTopology() {
		// show the dialog to enter information for the topology completion
		enterCompletionInformationDiag.modal("show");
	}
</script>

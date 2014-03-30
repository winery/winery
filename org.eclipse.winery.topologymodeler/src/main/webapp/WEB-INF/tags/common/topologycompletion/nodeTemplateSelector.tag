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
<%@tag language="java" pageEncoding="UTF-8" description="This tag is used to render Node and Relationship Templates for selection in a dialog."%>

<%-- attributes for the NodeTemplate selection --%>
<%@attribute name="templateURL" type="java.lang.String"%>
<%@attribute name="topologyName" type="java.lang.String"%>
<%@attribute name="topologyNamespace" type="java.lang.String"%>
<%@attribute name="repositoryURL" type="java.lang.String" %>
<%@attribute name="stName" type="java.lang.String" %>
<%@attribute name="choices" type="java.util.Map<org.eclipse.winery.model.tosca.TNodeTemplate, java.util.Map<org.eclipse.winery.model.tosca.TNodeTemplate, java.util.List<org.eclipse.winery.model.tosca.TEntityTemplate>>>"%>

<%@tag import="java.util.ArrayList"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="java.util.List"%>
<%@tag import="java.util.Map"%>
<%@tag import="java.util.UUID"%>
<%@tag import="javax.xml.namespace.QName"%>
<%@tag import="org.eclipse.winery.model.tosca.TEntityTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>
<%@tag import="org.eclipse.winery.repository.client.WineryRepositoryClientFactory"%>
<%@tag import="org.eclipse.winery.repository.client.IWineryRepositoryClient"%>
<%@tag import="org.eclipse.winery.common.Util"%>

<%@taglib prefix="ntrq" tagdir="/WEB-INF/tags/common/templates/nodetemplates/reqscaps" %>
<%@taglib prefix="nt"   tagdir="/WEB-INF/tags/common/templates/nodetemplates" %>

<div id="nodeTemplateSelector">
	<p> There are several possible Node Templates to be inserted. <br> Please select your desired NodeTemplate: </p>

	<%
		// the pixel distance between the displayed NodeTemplates
		final int NODE_TEMPLATE_DISTANCE = 150;

		IWineryRepositoryClient client = WineryRepositoryClientFactory.getWineryRepositoryClient();
		client.addRepository(repositoryURL);

		// instantiate  variables
		Map<String, String> idMap = new HashMap<String, String>();
		List<TRelationshipTemplate> possibleConnections = new ArrayList<TRelationshipTemplate>();
		String sourceId = "";
		String randomId = "";
		String id = "";

		// a counter used for an ID
		int counter = 0;

		// used for the position of the NodeTemplate in the EditorArea
		int topCounter = 0;
	%>
	<script>
		// array to collect the created IDs
		IDs = new Array();

		// save all created connections in an array to be able to detach them after the selection
		Connections = new Array();
	</script>
	<%
		// render a topology for every choice to be displayed in the dialog
		for (TNodeTemplate nt: choices.keySet()) {

			Map<TNodeTemplate, List<TEntityTemplate>> entityTemplates = choices.get(nt);

			for (TNodeTemplate choice: entityTemplates.keySet()) {
				id = "choice" + Integer.toString(counter);

				%>
				<div id="proposalEditorArea">
				<div id="proposaldrawingarea">
				<%

				topCounter = 0;
			%>
			<nt:nodeTemplateRenderer client="<%=client%>" relationshipTypes="<%=client.getAllTypes(TRelationshipType.class)%>" repositoryURL='<%=repositoryURL%>' nodeTemplate="<%=nt%>" top="<%=Integer.toString(topCounter)%>" left='<%="0"%>'/>
			<script>

				//Map IDs here. ID mapping is necessary to avoid conflict with the modelled NodeTemplates in the background.
				<%
					randomId = UUID.randomUUID().toString();
				%>
					document.getElementById("<%=nt.getId()%>").id = "<%=randomId%>";
					IDs.push("<%=randomId%>");
				<%
					idMap.put(nt.getId(), randomId);
				%>
			</script>
			<%

			topCounter = topCounter + NODE_TEMPLATE_DISTANCE;
			%>
			<!-- use the nodeTemplateRenderer tag to render NodeTemplates-->
			<nt:nodeTemplateRenderer client="<%=client%>" relationshipTypes="<%=client.getAllTypes(TRelationshipType.class)%>" repositoryURL='<%=repositoryURL%>' nodeTemplate="<%=choice%>" top="<%=Integer.toString(topCounter)%>" left='<%="0"%>'/>
			<script>
			    //Map IDs here
				<%
					randomId = UUID.randomUUID().toString();
				%>
					document.getElementById("<%=choice.getId()%>").id = "<%=randomId%>";
					IDs.push("<%=randomId%>");
				<%
					idMap.put(choice.getId(), randomId);
				%>
			</script>
			</div>
			</div>
			<% if (entityTemplates.get(choice).size() > 1) { %>
				<p> There are several possible Relationship Templates to connect the Node Templates <%=nt.getName()%> and <%=choice.getName()%>. Please choose at least one connection: </p>
			<%}
			for (TEntityTemplate rtChoice: entityTemplates.get(choice)) {

				TRelationshipTemplate connector = (TRelationshipTemplate) rtChoice;
				if (entityTemplates.get(choice).size() > 1) {
				%>
					<input checked="checked" id="<%=connector.getName()%>" name="<%=connector.getName()%>" type="checkbox" value="<%=connector.getName()%>">	<%=connector.getName()%> <br/>
				<%
				}
				sourceId = ((TNodeTemplate) connector.getSourceElement().getRef()).getId();
				String targetId = ((TNodeTemplate) connector.getTargetElement().getRef()).getId();
				QName type = connector.getType();
				String visualSourceId = idMap.get(sourceId);
				String visualTargetId = idMap.get(targetId);
				%>
				<script>
					// connect the rendered NodeTemplates
					require(["winery-common-topologyrendering"], function(wct) {
						wct.initNodeTemplate(jsPlumb.getSelector(".NodeTemplateShape:not('.hidden')"), true);
					});
					var c;
					require(["jsplumb"], function(_jsPlumb) {
						_jsPlumb.ready(function() {
							c = _jsPlumb.connect({
								source:"<%=visualSourceId%>",
								target:"<%=visualTargetId%>",
								endpoint:"Blank",
								type: "<%=type%>"
							});
							Connections.push(c);
						})
					});

				</script>
			<%}
		%>
		<br>
		<button type="button" class="btn btn-primary btn-default" data-dismiss="modal" id="<%=id%>" value='<%=choice.getName()%>' onclick="onSelected<%=choice.getName()%>()">Use Template: <%=choice.getName()%></button>
		<script>

			/**
			 * Handles a click on the "Select" button.
			 *
			 * This selection handler method is created for every NodeTemplate that can be chosen by the user.
			 * This is realized by inserting the unique names of the NodeTemplate choices in the method name via JSP scriptlet.
			 */
			function onSelected<%=choice.getName()%>() {

				SelectedRTs = new Array();

				for (var i = 0; i < Connections.length; i++) {
					jsPlumb.detach(Connections[i]);
				}

				<%
				if (entityTemplates.get(choice).size() == 1) {
				%>
					SelectedRTs.push("<%=((TRelationshipTemplate) entityTemplates.get(choice).get(0)).getName()%>");
				<%
				} else if (entityTemplates.get(choice).size() > 1) {
					for (TEntityTemplate rtChoice: entityTemplates.get(choice)) {
					TRelationshipTemplate connector = (TRelationshipTemplate) rtChoice;
					%>
					if (document.getElementById("<%=connector.getName()%>").checked) {
						SelectedRTs.push(document.getElementById("<%=connector.getName()%>").value);
					}
				<%
				}}
				%>
				SelectedItems = new Array();
				SelectedItems.push(document.getElementById("<%=id%>").value);

				if (SelectedItems.length == 0) {
					vShowError("Please selected at least one Relationship Template.");
				} else {
					// add the selected Templates to the topology and restart the completion
					var selectedNodeTemplates = JSON.stringify(SelectedItems);
					var selectedRelationshipTemplates = JSON.stringify(SelectedRTs);
					$.post("jsp/topologyCompletion/selectionHandler.jsp", {topology: topology, allChoices: choices, selectedNodeTemplates: selectedNodeTemplates, selectedRelationshipTemplates: selectedRelationshipTemplates},
						function(data){
							require(["winery-topologycompletion"], function(completer) {
								completer.restartCompletion(data, document.getElementById('overwriteTopology').checked,document.getElementById('openInNewWindow').checked,
									document.getElementById('topologyName').value, document.getElementById('topologyNamespace').value, true, "<%=stName%>",
									"<%=templateURL%>", "<%=repositoryURL%>");
							});
						}
					);
				}
			}
		</script>
		<%
		counter++;
		}
	}%>
	<br>
	<br>
	<br>
	<button type="button" class="btn btn-primary btn-default" data-dismiss="modal" id="cancel" onclick="onSelectedCancel()">Cancel Automatic Completion</button>
	<p><i> Press this button if you want to continue the completion manually.</i> </p>
	<script>
		// save topology and refresh the page
		function onSelectedCancel() {
			$.post("jsp/topologyCompletion/topologySaver.jsp", {topology: topology, templateURL: "<%=templateURL%>", repositoryURL: "<%=repositoryURL%>", topologyName: "<%=topologyName%>", topologyNamespace: "<%=topologyNamespace%>", overwriteTopology: "true"},
				function(callback){
					document.location.reload(true);
				}
			);
		}
	</script>
</div>
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

<%@tag language="java" pageEncoding="UTF-8" description="This tag is used to render Relationship Templates for selection in a dialog."%>

<%-- attributes for the topology selection --%>
<%@attribute name="templateURL" type="java.lang.String"%>
<%@attribute name="topologyName" type="java.lang.String"%>
<%@attribute name="topologyNamespace" type="java.lang.String"%>
<%@attribute name="repositoryURL" type="java.lang.String" %>
<%@attribute name="stName" type="java.lang.String" %>
<%@attribute name="choices" type="java.util.List<org.eclipse.winery.model.tosca.TEntityTemplate>"%>

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
<%@taglib prefix="tc"   tagdir="/WEB-INF/tags/common/topologycompletion"%>

<div id="relationshipTemplateSelector">
<p> There are several possible Relationship Templates for a connection. <br> Please select your desired connection(s): </p>
<script>
	// save all created connections in an array to be able to detach them after the selection
	Connections = new Array();
</script>
<%
	// the pixel distance between the displayed NodeTemplates
	final int NODE_TEMPLATE_DISTANCE = 150;

	IWineryRepositoryClient client = WineryRepositoryClientFactory.getWineryRepositoryClient();
	client.addRepository(repositoryURL);

	Map<String, String> idMap = new HashMap<String, String>();
	String sourceId = "";
	String id = "choice";

	// used for the position of the NodeTemplate in the EditorArea
	int topCounter = 0;

	List<TRelationshipTemplate> possibleConnections = new ArrayList<TRelationshipTemplate>();

	for (TEntityTemplate choice: choices) {
		if (choice instanceof TRelationshipTemplate) {
			possibleConnections.add((TRelationshipTemplate) choice);
		}
	}
	for (TRelationshipTemplate connector: possibleConnections) { %>
		<div id="proposalEditorArea">
		<div id="proposaldrawingarea">
		<div id="allRelationships">
		<%
		topCounter = 0;

		for (TEntityTemplate choice: choices) {
			if (choice instanceof TNodeTemplate) {
				TNodeTemplate nodeTemplate = (TNodeTemplate) choice;

				topCounter = topCounter + NODE_TEMPLATE_DISTANCE;
				%>
				<nt:nodeTemplateRenderer client="<%=client%>" relationshipTypes="<%=client.getAllTypes(TRelationshipType.class)%>" repositoryURL='<%=repositoryURL%>' nodeTemplate="<%=nodeTemplate%>" top="<%=Integer.toString(topCounter)%>" left='<%="0"%>'/>
				<script>
					//Map IDs here
					<%
						String randomId = UUID.randomUUID().toString();
					%>
						document.getElementById("<%=nodeTemplate.getId()%>").id = "<%=randomId%>";
					<%
						idMap.put(nodeTemplate.getId(), randomId);
					%>
				</script>
				<%
			}
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
		</div>
		</div>
		</div>
		<input id="<%=id%>" name="<%=id%>" type="checkbox" value="<%=connector.getName()%>"> <%=connector.getName()%> <br>

	<%}%>
	<button type="button" class="btn btn-primary btn-default" id="btnUseSelection" onclick="useRelationshipTemplateSelection()">Use Selection</button>
	<script>
		function useRelationshipTemplateSelection() {
			// add the selected RelationshipTemplates to the topology and restart the completion
			SelectedItems = new Array();
			for (var i= 0; i < document.getElementById("rtchoices").children[0].choice.length; i++) {
				if (document.getElementById("rtchoices").children[0].choice[i].checked == true) {
					SelectedItems.push(document.getElementById("rtchoices").children[0].choice[i].value);
				}
			}

			if (SelectedItems.length == 0) {
				vShowError("Please selected at least one Relationship Template.");
			} else {
				$('#chooseRelationshipTemplateDiag').modal('hide');
				var selectedRelationshipTemplates = JSON.stringify(SelectedItems);
				// add selected RelationshipTemplate(s) to the topology
				$.post("jsp/topologyCompletion/selectionHandler.jsp", {topology: topology, allChoices: choices, selectedRelationshipTemplates: selectedRelationshipTemplates},
					function(data) {
						require(["winery-topologycompletion"], function(completer) {
							completer.restartCompletion(data, document.getElementById('overwriteTopology').checked,document.getElementById('openInNewWindow').checked,
								topologyName, topologyNamespace, true, "<%=stName%>",
								"<%=templateURL%>", "<%=repositoryURL%>");
						});
					}
				);
			}
		}
	</script>
</div>

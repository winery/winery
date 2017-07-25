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

<%@tag language="java" pageEncoding="UTF-8" description="This tag is used to render Topology Templates for selection in a dialog."%>

<%-- attributes for the topology selection --%>
<%@attribute name="templateURL" type="java.lang.String"%>
<%@attribute name="topologyName" type="java.lang.String"%>
<%@attribute name="topologyNamespace" type="java.lang.String"%>
<%@attribute name="repositoryURL" type="java.lang.String" %>
<%@attribute name="uiURL" type="java.lang.String" %>
<%@attribute name="solutionTopologies" type="java.util.List<org.eclipse.winery.model.tosca.TTopologyTemplate>"%>

<%@tag import="java.io.StringWriter"%>
<%@tag import="java.util.HashMap"%>
<%@tag import="java.util.Map"%>
<%@tag import="java.util.List"%>
<%@tag import="java.util.UUID"%>
<%@tag import="javax.xml.bind.Marshaller"%>
<%@tag import="javax.xml.bind.JAXBContext"%>
<%@tag import="javax.xml.bind.JAXBException"%>
<%@tag import="javax.xml.namespace.QName"%>
<%@tag import="org.eclipse.winery.model.tosca.Definitions"%>
<%@tag import="org.eclipse.winery.model.tosca.TEntityTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>
<%@tag import="org.eclipse.winery.model.tosca.TServiceTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TTopologyTemplate"%>
<%@tag import="org.eclipse.winery.repository.client.WineryRepositoryClientFactory"%>
<%@tag import="org.eclipse.winery.repository.client.IWineryRepositoryClient"%>
<%@tag import="org.eclipse.winery.common.Util"%>

<%@taglib prefix="ntrq" tagdir="/WEB-INF/tags/common/templates/nodetemplates/reqscaps" %>
<%@taglib prefix="nt"   tagdir="/WEB-INF/tags/common/templates/nodetemplates"%>

<div id="topologyTemplateSelector">
<p> There are several possible topology solutions <br> Please select your desired topology: </p>
	<script>
		// array to collect the created IDs
		IDs = new Array();

		// save all created connections in an array to be able to detach them after the selection
		Connections = new Array();
	</script>
<%
	// the pixel distance between the displayed NodeTemplates
	final int NODE_TEMPLATE_DISTANCE = 150;

	List<TTopologyTemplate> topologyTemplateSelector = solutionTopologies;
	int i = 0;
	int counter = 0;
	Map<String, String> idMap;
	for (TTopologyTemplate choice: topologyTemplateSelector) {
		Definitions definitions = new Definitions();
		TServiceTemplate st = new TServiceTemplate();
		st.setTopologyTemplate(choice);
		definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(st);
		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Marshaller m = context.createMarshaller();
		StringWriter stringWriter = new StringWriter();

		m.marshal(definitions, stringWriter);
		int topCounter = 0;
		IWineryRepositoryClient client = WineryRepositoryClientFactory.getWineryRepositoryClient();
		client.addRepository(repositoryURL);
		String id = "solution" + Integer.toString(i);

		String sourceId = null;
		idMap = new HashMap<String, String>();

		%>
		<div id="proposalEditorArea">
		<div id="proposaldrawingarea">
		<div id="<%=counter%>">
		<script> IDs.push("<%=id%>"); </script>
		<%
			for (TEntityTemplate entity: choice.getNodeTemplateOrRelationshipTemplate()) {

				if (entity instanceof TNodeTemplate) {
					TNodeTemplate nodeTemplate = (TNodeTemplate) entity;

					%>
						<nt:nodeTemplateRenderer client="<%=client%>" relationshipTypes="<%=client.getAllTypes(TRelationshipType.class)%>" repositoryURL='<%=repositoryURL%>' uiURL="<%=uiURL%>" nodeTemplate="<%=nodeTemplate%>" top="<%=Integer.toString(topCounter)%>" left='<%="0"%>'/>

						<%
							String randomId = UUID.randomUUID().toString();
						%>
						<script>
							document.getElementById("<%=nodeTemplate.getId()%>").id = "<%=randomId%>";
						</script>
						<%
							topCounter = topCounter + NODE_TEMPLATE_DISTANCE;
							idMap.put(nodeTemplate.getId(), randomId);
						%>

					<%
				}
			}
			for (TEntityTemplate entity: choice.getNodeTemplateOrRelationshipTemplate()) {
				if (entity instanceof TRelationshipTemplate) {
					TRelationshipTemplate connector = (TRelationshipTemplate) entity;
					sourceId = ((TNodeTemplate) connector.getSourceElement().getRef()).getId();
					String visualSourceId = idMap.get(sourceId);
					String targetId = ((TNodeTemplate) connector.getTargetElement().getRef()).getId();
					String visualTargetId = idMap.get(targetId);
					QName type = connector.getType();
					%>
					<script type='text/javascript'>
						var c;
						require(["winery-common-topologyrendering"], function(wct) {
							wct.initNodeTemplate(jsPlumb.getSelector(".NodeTemplateShape:not('.hidden')"), true);
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
							wct.handleConnectionCreated(c);
						});
					</script>  <%
				}
			}
	%>
	</div>
	</div>
	</div>
	<br>
	<input name="<%=id%>" id="<%=id%>" type="checkbox" value='<%=stringWriter.toString()%>' onclick="onClick<%=id%>()"> Save this Topology &nbsp; &nbsp;
	<script>
		/**
		 * Handles a click on the "Save this Topology" checkbox.
		 */
		function onClick<%=id%>() {
			if (document.getElementById('<%=id%>').checked) {
				document.getElementById('<%=id + "overwrite"%>').disabled = false;
				document.getElementById('<%=id + "name"%>').disabled = false;
				document.getElementById('<%=id + "namespace"%>').disabled = false;
				document.getElementById('<%=id + "newWindow"%>').disabled = false;
			}
			else {
				document.getElementById('<%=id + "overwrite"%>').disabled = true;
				document.getElementById('<%=id + "name"%>').disabled = true;
				document.getElementById('<%=id + "namespace"%>').disabled = true;
				document.getElementById('<%=id + "newWindow"%>').disabled = true;
			}
		}
	</script>
	<input disabled="disabled" name='<%=id + "overwrite"%>' id='<%=id + "overwrite"%>' type="checkbox" onclick='onClick<%=id + "overwrite"%>()'> Overwrite current Topology &nbsp; &nbsp;
	<script>
		/**
		 * Handles a click on the "Overwrite current Topology" checkbox.
		 */
		function onClick<%=id + "overwrite"%>() {
			if (document.getElementById('<%=id + "overwrite"%>').checked) {
				document.getElementById('<%=id + "name"%>').disabled = true;
				document.getElementById('<%=id + "namespace"%>').disabled = true;
				document.getElementById('<%=id + "newWindow"%>').disabled = true;
			} else {
				document.getElementById('<%=id + "name"%>').disabled = false;
				document.getElementById('<%=id + "namespace"%>').disabled = false;
				document.getElementById('<%=id + "newWindow"%>').disabled = false;
			}
		}
	</script>
	<input disabled="disabled" name='<%=id + "newWindow"%>' id='<%=id + "newWindow"%>' type="checkbox"> Open in new Window <br> <br>
	<p>Name: <input disabled="disabled" id='<%=id + "name"%>' name='<%=id + "name"%>' value="<%=topologyName%>" type="text" size="30" maxlength="30"> </p>
	<p>Namespace: <input disabled="disabled" id='<%=id + "namespace"%>' value="<%=topologyNamespace%>" name='<%=id + "namespace"%>' type="text" size="50" maxlength="60"> </p>
	<%
	counter++;
	i++;
	}
%>
	<button type="button" id="save" class="btn btn-primary btn-default">Save Topologies</button>
	<script>
		$('#save').on('click', function() {

			for (var i = 0; i < IDs.length; i++) {
				if (document.getElementById(IDs[i]).checked) {

					var name = document.getElementById(IDs[i] + 'name').value;
					var namespace = document.getElementById(IDs[i] + 'namespace').value;
					var overwrite = document.getElementById(IDs[i] + 'overwrite').checked;
					var openInNewWindow = document.getElementById(IDs[i] + 'newWindow').checked;

					// check validity of the namespace
					var validURIregexp = new RegExp("([A-Za-z][A-Za-z0-9+\\-.]*):(?:(//)(?:((?:[A-Za-z0-9\\-._~!$&'()*+,;=:]|%[0-9A-Fa-f]{2})*)@)?((?:\\[(?:(?:(?:(?:[0-9A-Fa-f]{1,4}:){6}|::(?:[0-9A-Fa-f]{1,4}:){5}|(?:[0-9A-Fa-f]{1,4})?::(?:[0-9A-Fa-f]{1,4}:){4}|(?:(?:[0-9A-Fa-f]{1,4}:){0,1}[0-9A-Fa-f]{1,4})?::(?:[0-9A-Fa-f]{1,4}:){3}|(?:(?:[0-9A-Fa-f]{1,4}:){0,2}[0-9A-Fa-f]{1,4})?::(?:[0-9A-Fa-f]{1,4}:){2}|(?:(?:[0-9A-Fa-f]{1,4}:){0,3}[0-9A-Fa-f]{1,4})?::[0-9A-Fa-f]{1,4}:|(?:(?:[0-9A-Fa-f]{1,4}:){0,4}[0-9A-Fa-f]{1,4})?::)(?:[0-9A-Fa-f]{1,4}:[0-9A-Fa-f]{1,4}|(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))|(?:(?:[0-9A-Fa-f]{1,4}:){0,5}[0-9A-Fa-f]{1,4})?::[0-9A-Fa-f]{1,4}|(?:(?:[0-9A-Fa-f]{1,4}:){0,6}[0-9A-Fa-f]{1,4})?::)|[Vv][0-9A-Fa-f]+\\.[A-Za-z0-9\\-._~!$&'()*+,;=:]+)\\]|(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)|(?:[A-Za-z0-9\\-._~!$&'()*+,;=]|%[0-9A-Fa-f]{2})*))(?::([0-9]*))?((?:/(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})*)*)|/((?:(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})+(?:/(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})*)*)?)|((?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})+(?:/(?:[A-Za-z0-9\\-._~!$&'()*+,;=:@]|%[0-9A-Fa-f]{2})*)*)|)(?:\\?((?:[A-Za-z0-9\\-._~!$&'()*+,;=:@/?]|%[0-9A-Fa-f]{2})*))?(?:\#((?:[A-Za-z0-9\\-._~!$&'()*+,;=:@/?]|%[0-9A-Fa-f]{2})*))?");
					if (validURIregexp.test(namespace) || overwrite) {

						if (!overwrite) {
							// first create a new service template via AJAX call
							var dataToSend = "name=" + name + "&namespace=" + namespace;
							var url = "<%=repositoryURL%>" + "/servicetemplates/";
							$.ajax(
								{
									type: "POST",
									async: false,
									url: url,
									"data": dataToSend,
									dataType: "text",
									error: function(jqXHR, textStatus, errorThrown) {
										vShowAJAXError("Could not add Service Template.");
									}
							});
						}

						// now save the topology template
						$.post("jsp/topologyCompletion/topologySaver.jsp", {topology: document.getElementById(IDs[i]).value, templateURL: "<%=templateURL%>", repositoryURL: "<%=repositoryURL%>", topologyName: name, topologyNamespace: namespace, overwriteTopology: overwrite},
							function(data){
								if (openInNewWindow) {
									// a new topology has been created, open it in a new window
									var win=window.open('?repositoryURL=' + "<%=repositoryURL%>" + '&ns='+ namespace + '&id=' + name, '_blank');
									win.focus();
								} else if (overwrite) {
									// refresh page
									document.location.reload(true);
								}
								// close the dialog
								chooseTopologyDiag.modal("hide");
								vShowSuccess("Successfully Saved Topologies.")
							}
						);
					} else {
						vShowError("Please enter a valid namespace.");
					}
				}
			}
		});
	</script>
</div>


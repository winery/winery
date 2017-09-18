<%
/*******************************************************************************
 * Copyright (c) 2013 Pascal Hirmer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - initial API and implementation
 *******************************************************************************/

/**
 * This JSP calls the topology completion and handles the response.
 * It is called the event handler when "Complete Topology" is selected in the EnterCompletionInformationDiag.
 * There are several possible responses from the completion:
 *	 - the topology is complete: display a success message
 *   - the topology is complete, several solutions exist: display dialog to choose topology solution
 *	 - topology completion interrupted: the user has to chose inserted Node or Relationship Templates
 */
%>

<%@page import="java.io.StringWriter"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.bind.Marshaller"%>
<%@page import="javax.xml.bind.JAXBContext"%>
<%@page import="javax.xml.bind.JAXBException"%>
<%@page import="org.eclipse.winery.model.tosca.Definitions"%>
<%@page import="org.eclipse.winery.model.tosca.TEntityTemplate"%>
<%@page import="org.eclipse.winery.model.tosca.TNodeTemplate"%>
<%@page import="org.eclipse.winery.model.tosca.TServiceTemplate"%>
<%@page import="org.eclipse.winery.model.tosca.TTopologyTemplate"%>
<%@page import="org.eclipse.winery.topologymodeler.addons.topologycompleter.topologycompletion.CompletionInterface"%>

<%@taglib prefix="tc"   tagdir="/WEB-INF/tags/common/topologycompletion"%>

<%
	// parse Strings from the request to Boolean values
	boolean stepByStep = Boolean.parseBoolean(request.getParameter("stepByStep"));
	boolean restarted = Boolean.parseBoolean(request.getParameter("restarted"));
	boolean overwriteTopology = Boolean.parseBoolean(request.getParameter("overwriteTopology"));

	// call the topology completion component which will return a message if it was successful.
	CompletionInterface completionInterface = new CompletionInterface();
	String message = completionInterface.complete(request.getParameter("topology"), request.getParameter("stName"), request.getParameter("templateURL"), overwriteTopology, request.getParameter("topologyName"), request.getParameter("topologyNamespace"), request.getParameter("repositoryURL"), stepByStep, restarted);

	if (message.equals("success")) { %>
		<script> vShowSuccess('Completion Successful!'); </script>
	<%
	} else if (message.equals("topologyComplete") && !restarted) { %>
		<script>
			vShowSuccess('The topology is already complete.');
		</script>
	<%
	} else if (message.equals("failure")) {
		%>
		<p> <%=completionInterface.getErrorMessage()%> </p>
	<%} else if (message.equals("userInteraction")) {

			// a user interaction is necessary to choose RelationshipTemplates, receive
			// the current topology and the choices from the CompletionInterface
			// and display them via relationshipTemplateSelector.jsp

			TTopologyTemplate currentTopology = completionInterface.getCurrentTopology();
			List<TEntityTemplate> relationshipTemplateSelection = completionInterface.getRelationshipTemplateChoices();

			/////////////////////////////////////////////////////
			// Convert JAXB objects of the topology and the
			// Relationship Templates to be chosen to XML Strings
			/////////////////////////////////////////////////////

			Definitions definitions = new Definitions();
			TServiceTemplate serviceTemplate = new TServiceTemplate();

			serviceTemplate.setTopologyTemplate(currentTopology);
			definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(serviceTemplate);
			JAXBContext context = JAXBContext.newInstance(Definitions.class);
			Marshaller marshaller = context.createMarshaller();
			StringWriter currentTopologyString = new StringWriter();

			marshaller.marshal(definitions, currentTopologyString);

			TTopologyTemplate topologyTemplate = new TTopologyTemplate();

			// add all choices to a TopologyTemplate
			for (TEntityTemplate entityTemplate: relationshipTemplateSelection) {
				topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(entityTemplate);
			}

			// get the choices as XML
			definitions = new Definitions();
			serviceTemplate = new TServiceTemplate();
			serviceTemplate.setTopologyTemplate(topologyTemplate);
			definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(serviceTemplate);
			context = JAXBContext.newInstance(Definitions.class);
			StringWriter choicesAsXMLString = new StringWriter();

			marshaller.marshal(definitions, choicesAsXMLString);
		%>
		<script>
			var topology = "<%=currentTopologyString.toString()%>";
			var choices = "<%=choicesAsXMLString.toString()%>";
		</script>
		<!-- call the relationshipTemplateSelector tag to render the selection to graphic objects -->
		<tc:relationshipTemplateSelector choices='<%=relationshipTemplateSelection%>' templateURL='<%=request.getParameter("templateURL")%>' topologyName='<%=request.getParameter("topologyName")%>'
			topologyNamespace='<%=request.getParameter("topologyNamespace")%>' repositoryURL='<%=request.getParameter("repositoryURL")%>' stName='<%=request.getParameter("stName")%>' />
	<%} else if (message.equals("userTopologySelection")) {
		// there are several topology solutions. Receive the choices from the CompletionInterface
		// and display them via topologyTemplateSelector.tag
		List<TTopologyTemplate> topologyTemplateSelection = completionInterface.getTopologyTemplateChoices();
	%>
	<!-- call the topologyTemplateSelector tag to render the selection to graphic objects -->
	<tc:topologyTemplateSelector solutionTopologies='<%=topologyTemplateSelection%>' templateURL='<%=request.getParameter("templateURL")%>' topologyName='<%=request.getParameter("topologyName")%>'
		topologyNamespace='<%=request.getParameter("topologyNamespace")%>' repositoryURL='<%=request.getParameter("repositoryURL")%>' />
	<%
	} else if (message.equals("stepByStep")) {

		// the topology completion is processed step-by-step. The user has to choose inserted Node and RelationshipTemplates
		TTopologyTemplate currentTopology = completionInterface.getCurrentTopology();
		Map<TNodeTemplate, Map<TNodeTemplate, List<TEntityTemplate>>> nodeTemplateSelection = completionInterface.getNodeTemplateChoices();

		///////////////////////////////////////////////
		// Convert JAXB objects of the topology and the
		// Templates to be chosen to XML Strings
		///////////////////////////////////////////////

		Definitions definitions = new Definitions();
		TServiceTemplate serviceTemplate = new TServiceTemplate();
		serviceTemplate.setTopologyTemplate(currentTopology);
		definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(serviceTemplate);
		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Marshaller marshaller = context.createMarshaller();
		StringWriter currentTopologyString = new StringWriter();

		marshaller.marshal(definitions, currentTopologyString);

		// add all choices to a TopologyTemplate
		TTopologyTemplate topologyTemplate = new TTopologyTemplate();

		for (TNodeTemplate nodeTemplate: nodeTemplateSelection.keySet()) {
			Map<TNodeTemplate, List<TEntityTemplate>> entityTemplates = nodeTemplateSelection.get(nodeTemplate);

			for (TNodeTemplate entity: entityTemplates.keySet()) {
				topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(entity);
				topologyTemplate.getNodeTemplateOrRelationshipTemplate().addAll(entityTemplates.get(entity));
			}
			topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(nodeTemplate);
		}

		// get the choices as XML
		definitions = new Definitions();
		serviceTemplate = new TServiceTemplate();
		serviceTemplate.setTopologyTemplate(topologyTemplate);
		definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(serviceTemplate);

		StringWriter choicesAsXMLString = new StringWriter();

		marshaller.marshal(definitions, choicesAsXMLString);

		%>
		<script>
			var topology = '<%=currentTopologyString.toString()%>';
			var choices = '<%=choicesAsXMLString.toString()%>';
		</script>
		<!-- call the tc:nodeTemplateSelector tag to render the selection to graphic objects -->
		<tc:nodeTemplateSelector choices='<%=nodeTemplateSelection%>' templateURL='<%=request.getParameter("templateURL")%>' topologyName='<%=request.getParameter("topologyName")%>'
			topologyNamespace='<%=request.getParameter("topologyNamespace")%>' repositoryURL='<%=request.getParameter("repositoryURL")%>' stName='<%=request.getParameter("stName")%>' />
	<%}
%>

<script>
	var message = "<%=message%>";
</script>

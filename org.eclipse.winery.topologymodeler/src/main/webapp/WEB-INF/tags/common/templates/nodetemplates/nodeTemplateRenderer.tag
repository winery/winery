<%--
/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Uwe Breitenbücher - skeletton for node template shapes
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *	  Karoline Saatkamp - maintenance
 *******************************************************************************/
--%>
<%@tag language="java" pageEncoding="UTF-8" description="This tag is used for both real nodeTemplate node rendering and rendering of a 'template' used to create a nodeTemplateShape. The latter is called by palette.jsp. Therefore, this tag has to be more general."%>
<%-- Parameters --%>

<%-- template and palette --%>
<%@attribute name="client" required="true" description="IWineryRepository" type="org.eclipse.winery.common.interfaces.IWineryRepository"%>
<%@attribute name="repositoryURL" required="true" type="java.lang.String" description="The URL of winery's repository"%>
<%@attribute name="uiURL" required="true" type="java.lang.String" description="The URL of winery's UI"%>
<%@attribute name="topologyModelerURI" required="false" type="java.lang.String" description="The URL of winery topology modeler's URI - required for images/. Has to end with '/'. Can be left blank."%>
<%@attribute name="relationshipTypes" description="the known relationship types" required="true" type="java.util.Collection"%>

<%-- only for topology modeler --%>
<%@attribute name="nodeTemplate" type="org.eclipse.winery.model.tosca.TNodeTemplate"%>
<%@attribute name="top"%>
<%@attribute name="left"%>

<%-- only for palette.jsp --%>
<%@attribute name="nodeType" type="org.eclipse.winery.model.tosca.TNodeType" %>
<%@attribute name="nodeTypeQName" type="javax.xml.namespace.QName"%>

<%@tag import="java.util.Collection"%>
<%@tag import="java.util.Collections"%>
<%@tag import="java.util.List"%>
<%@tag import="java.util.UUID"%>
<%@tag import="javax.xml.namespace.QName"%>
<%@tag import="javax.xml.transform.OutputKeys"%>
<%@tag import="javax.xml.transform.Transformer"%>
<%@tag import="javax.xml.transform.TransformerFactory"%>
<%@tag import="org.eclipse.winery.common.ModelUtilities"%>
<%@tag import="org.eclipse.winery.common.Util"%>
<%@tag import="org.eclipse.winery.common.ids.definitions.ArtifactTemplateId"%>
<%@tag import="org.eclipse.winery.common.ids.definitions.ArtifactTypeId"%>
<%@tag import="org.eclipse.winery.model.tosca.TCapability"%>
<%@tag import="org.eclipse.winery.model.tosca.TDeploymentArtifact"%>
<%@tag import="org.eclipse.winery.model.tosca.TDeploymentArtifacts"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate.Capabilities"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate.Policies"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate.Requirements"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeType"%>
<%@tag import="org.eclipse.winery.model.tosca.TPolicy" %>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>
<%@tag import="org.eclipse.winery.model.tosca.TRequirement"%>
<%@tag import="org.apache.commons.lang3.StringUtils"%>

<%@taglib prefix="nt" tagdir="/WEB-INF/tags/common/templates/nodetemplates" %>
<%@taglib prefix="ntrq"  tagdir="/WEB-INF/tags/common/templates/nodetemplates/reqscaps" %>
<%@taglib prefix="pol"   tagdir="/WEB-INF/tags/common/policies" %>
<%@taglib prefix="props" tagdir="/WEB-INF/tags/common/templates" %>

<%
	String visualElementId;

	boolean paletteMode;
	if (nodeTemplate == null) {
		// we are in palette mode
		// --> we render a template to be inserted in the drawing area by drag'n'drop
		paletteMode = true;
		assert(nodeType != null);
		assert(nodeTypeQName != null);

		// these values are only pseudo values, they get all overwritten in drop function of palette.jsp
		visualElementId = UUID.randomUUID().toString();
		left = "0";
		top = "0";
	} else {
		// we render a real node template
		paletteMode = false;
		nodeTypeQName = nodeTemplate.getType();
		nodeType = client.getType(nodeTypeQName, TNodeType.class);
		if (nodeType == null) {
%>
			<script>vShowError("Could not get node type <%=nodeTypeQName%>");</script>
<%
			return;
		}

		visualElementId = nodeTemplate.getId();
	}

	String nodeTypeCSSName = Util.makeCSSName(nodeTypeQName);
%>

	<div class="NodeTemplateShape unselectable <%=nodeTypeCSSName%> <%if (paletteMode){%> hidden<%}%>" id="<%=visualElementId%>" style="left: <%=left%>px; top: <%=top%>px">
		<div class="headerContainer">
			<img class="icon" onerror="var that=this; require(['winery-common-topologyrendering'], function(wct){wct.imageError(that);});" src="<%=repositoryURL%>/nodetypes/<%=Util.DoubleURLencode(nodeTypeQName)%>/visualappearance/50x50" />
			<%
				String name;
				if (paletteMode) {
					name = ""; // will be changed on drop
				} else {
					name = nodeTemplate.getName();
					if (StringUtils.isEmpty(name)) {
						name = visualElementId;
					}
				}
			%>
			<div class="minMaxInstances">
				<span class="minInstances"><%
					if (!paletteMode) {
						%><%=Util.renderMinInstances(nodeTemplate.getMinInstances())%><%
					}
				%></span>
				<span class="maxInstances"><%
					if (!paletteMode) {
						%><%=Util.renderMaxInstances(nodeTemplate.getMaxInstances())%><%
					}
				%></span>
			</div>
			<div class="id nodetemplate"><%=visualElementId%></div>
			<div class="name nodetemplate"><%=name%></div>
			<div class="type nodetemplate"><%=Util.qname2hrefWithName(uiURL, TNodeType.class, nodeTypeQName, nodeType.getName())%></div>
			<span class="typeQName hidden"><%=nodeTypeQName%></span>
			<span class="typeNamespace hidden"><%=nodeTypeQName.getNamespaceURI()%></span>
		</div>
		<div class="endpointContainer">
		<%
			for (TRelationshipType relationshipType: (Collection<TRelationshipType>) relationshipTypes) {
		%>
			<div class="connectorEndpoint <%=Util.makeCSSName(relationshipType.getTargetNamespace(), relationshipType.getName())%>">
				<div class="connectorBox <%=Util.makeCSSName(relationshipType.getTargetNamespace(), relationshipType.getName())%>_box"></div>
				<div class="connectorLabel"><%=relationshipType.getName()%></div>
			</div>
		<%
			}
		%>
		</div>

		<div class="targetLocationContainer">
			<div class="header">
				Target Location
			</div>
			<div class="content">
				<a class="thetargetlocation" href="#" data-type="text" data-title="Enter the target location"><%=ModelUtilities.getTargetLabel(nodeTemplate).orElse("")%></a>
			</div>
		</div>

		<%-- Properties --%>
		<props:properties
			propertiesDefinition="<%=nodeType.getPropertiesDefinition()%>"
			wpd="<%=ModelUtilities.getWinerysPropertiesDefinition(nodeType)%>"
			template="<%=paletteMode ? null : nodeTemplate %>"
			pathToImages="${topologyModelerURI}images/" />

	<%-- Deployment Artifacts --%>

	<%
	List<TDeploymentArtifact> deploymentArtifacts;
	if (paletteMode) {
		deploymentArtifacts = Collections.emptyList();
	} else {
		TDeploymentArtifacts tDeploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
		if (tDeploymentArtifacts == null) {
			deploymentArtifacts = Collections.emptyList();
		} else {
			deploymentArtifacts = tDeploymentArtifacts.getDeploymentArtifact();
		}
	}
	// Render even if (deploymentArtifacts.isEmpty()), because user could add some with drag'n'drop

	// following is required to render artifact specific content
	TransformerFactory transFactory = TransformerFactory.newInstance();
	Transformer transformer = transFactory.newTransformer();
	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	%>

	<div class="deploymentArtifactsContainer">

		<div class="header">Deployment Artifacts</div>
		<div class="content">
			<%
			if (!paletteMode) {
				for (TDeploymentArtifact deploymentArtifact : deploymentArtifacts) {
					%>
					<div class="deploymentArtifact row" onclick="showDeploymentArtifactInformation('<%=visualElementId%>', '<%=deploymentArtifact.getName()%>');">
						<textarea class="hidden"><%=org.eclipse.winery.common.Util.getXMLAsString(org.eclipse.winery.model.tosca.TDeploymentArtifact.class, deploymentArtifact)%></textarea>
						<div class="col-xs-4 overflowhidden deploymentArtifact name"><%=deploymentArtifact.getName()%></div>
						<div class="col-xs-4 overflowhidden artifactTemplate"><%
							QName artifactRef;
							if ((artifactRef = deploymentArtifact.getArtifactRef()) != null) {
								ArtifactTemplateId atId = new ArtifactTemplateId(artifactRef);
								%><%=client.getName(atId)%><%
							}
						%></div>
						<div class="col-xs-4 overflowhidden artifactType"><%
						ArtifactTypeId atyId = new ArtifactTypeId(deploymentArtifact.getArtifactType());
						%><%=client.getName(atyId)%></div>
					</div>
					<%
				}
			}
			%>

			<div class="row addDA">
				<button class="btn btn-default btn-xs center-block addDA">Add new</button>
			</div>

			<div class="row addnewartifacttemplate">
				<div class="center-block">Drop to add new deployment artifact. Not yet implemented.</div>
			</div>
		</div>
	</div>

	<%-- Requirements and Capabilities --%>
	<%
	List<TRequirement> reqList;
	if (paletteMode) {
		reqList = null;
	} else {
		Requirements reqs = nodeTemplate.getRequirements();
		if (reqs == null) {
			reqList = null;
		} else {
			reqList = reqs.getRequirement();
		}
	}
	%>
	<ntrq:reqs list="<%=reqList%>" repositoryURL="${repositoryURL}" pathToImages="${topologyModelerURI}images/" client="${client}" />

	<%
	List<TCapability> capList;
	if (paletteMode) {
		capList = null;
	} else {
		Capabilities caps = nodeTemplate.getCapabilities();
		if (caps == null) {
			capList = null;
		} else {
			capList = caps.getCapability();
		}
	}
	%>
	<ntrq:caps list="<%=capList%>" repositoryURL="${repositoryURL}" pathToImages="${topologyModelerURI}images/" client="${client}"/>

	<%-- Policies --%>
	<%
	List<TPolicy> policyList;
	if (paletteMode) {
		policyList = null;
	} else {
		Policies policies = nodeTemplate.getPolicies();
		if (policies == null) {
			policyList = null;
		} else {
			policyList = policies.getPolicy();
		}
	}
	%>
	<pol:policies list="<%=policyList%>" repositoryURL="${repositoryURL}" />
</div>

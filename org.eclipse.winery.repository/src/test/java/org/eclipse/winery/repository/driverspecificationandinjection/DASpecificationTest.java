/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Karoline Saatkamp - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.driverspecificationandinjection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DASpecificationTest extends TestWithGitBackedRepository {

	@Test
	public void getArtifactTypeOfDA() throws Exception {
		setRevisionTo("af529e513388dc9358a8f700757d8dc59aba3a55");
		ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DASpecificationTest", false);
		TTopologyTemplate topologyTemplate = this.repository.getElement(id).getTopologyTemplate();

		TNodeTemplate nodeTemplateWithAbstractDA = topologyTemplate.getNodeTemplate("shetland_pony");

		TDeploymentArtifact deploymentArtifact = nodeTemplateWithAbstractDA.getDeploymentArtifacts().getDeploymentArtifact().get(0);
		QName artifactTypeQName = deploymentArtifact.getArtifactType();
		ArtifactTypeId artifactTypeId = new ArtifactTypeId(artifactTypeQName);

		TArtifactType artifactType = this.repository.getElement(artifactTypeId);

		assertEquals(artifactType.getTargetNamespace(), DASpecification.getArtifactTypeOfDA(nodeTemplateWithAbstractDA.getDeploymentArtifacts().getDeploymentArtifact().get(0)).getTargetNamespace());
		assertEquals(artifactType.getName(), DASpecification.getArtifactTypeOfDA(nodeTemplateWithAbstractDA.getDeploymentArtifacts().getDeploymentArtifact().get(0)).getName());
	}
	
	@Test
	public void getNodeTemplatesWithAbstractDAs() throws Exception {
		setRevisionTo("af529e513388dc9358a8f700757d8dc59aba3a55");
		ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DASpecificationTest", false);
		TTopologyTemplate topologyTemplate = this.repository.getElement(id).getTopologyTemplate();
		List<TNodeTemplate> nodeTemplateWithAbstractDA = new ArrayList<>();
		nodeTemplateWithAbstractDA.add(topologyTemplate.getNodeTemplate("shetland_pony"));
		
		List<TNodeTemplate> nodesWithAbstractDA = DASpecification.getNodeTemplatesWithAbstractDAs(topologyTemplate);
		
		assertEquals(nodeTemplateWithAbstractDA, nodesWithAbstractDA);
	}

	@Test
	public void getArtifactTypeHierarchy() throws Exception {
		setRevisionTo("af529e513388dc9358a8f700757d8dc59aba3a55");
		ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DASpecificationTest", false);
		TTopologyTemplate topologyTemplate = this.repository.getElement(id).getTopologyTemplate();
		
		TNodeTemplate nodeTemplate = topologyTemplate.getNodeTemplate("westernequipment");
		
		List<TArtifactType> artifactTypes = DASpecification.getArtifactTypeHierarchy(DASpecification.getArtifactTypeOfDA(nodeTemplate.getDeploymentArtifacts().getDeploymentArtifact().get(0)));
		List<String> artifactTypeNames = new ArrayList<>();
		artifactTypes.stream().forEach(at -> artifactTypeNames.add(at.getName()));
		
		assertEquals(2, artifactTypes.size());
		assertTrue(artifactTypeNames.contains("WesternEquipment_Pony"));
		assertTrue(artifactTypeNames.contains("PonyEquipment"));
	}

	@Test
	public void getNodesWithSuitableConcreteDAs() throws Exception {
		setRevisionTo("5f63267261584a513dd8a9b7960687cc3dda910a");
		ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DASpecificationTest", false);
		TTopologyTemplate topologyTemplate = this.repository.getElement(id).getTopologyTemplate();
		
		TNodeTemplate nodeTemplate = topologyTemplate.getNodeTemplate("ponycompetition");
		TNodeTemplate nodeTemplateWithAbstractDA = topologyTemplate.getNodeTemplate("shetland_pony");
		TDeploymentArtifact deploymentArtifact = nodeTemplateWithAbstractDA.getDeploymentArtifacts().getDeploymentArtifact().get(0);
		TNodeTemplate expectedNodeTemplate = topologyTemplate.getNodeTemplate("dressageequipment");
		
		TNodeTemplate actualNodeWithConcreteDA = DASpecification.getNodesWithSuitableConcreteDAs(nodeTemplate, deploymentArtifact, topologyTemplate);

		assertEquals(expectedNodeTemplate, actualNodeWithConcreteDA);
	}

	@Test
	public void getNodesWithSuitableConcreteDAAndTheDirectlyConnectedNode() throws Exception {
		setRevisionTo("5f63267261584a513dd8a9b7960687cc3dda910a");
		ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DASpecificationTest", false);
		TTopologyTemplate topologyTemplate = this.repository.getElement(id).getTopologyTemplate();
		
		TNodeTemplate nodeTemplateWithAbstractDA = topologyTemplate.getNodeTemplate("shetland_pony");
		TDeploymentArtifact deploymentArtifact = nodeTemplateWithAbstractDA.getDeploymentArtifacts().getDeploymentArtifact().get(0);
		TNodeTemplate nodeTemplateConcretDA1 = topologyTemplate.getNodeTemplate("dressageequipment");
		TRelationshipTemplate relationshipTemplate1 = topologyTemplate.getRelationshipTemplate("con_42");
		TNodeTemplate nodeTemplateConcretDA2 = topologyTemplate.getNodeTemplate("westernequipment");
		TRelationshipTemplate relationshipTemplate2 = topologyTemplate.getRelationshipTemplate("con_54");
		
		Map<TRelationshipTemplate, TNodeTemplate> concreteDAsAndConnectedNodes = new HashMap<>();
		concreteDAsAndConnectedNodes.put(relationshipTemplate1, nodeTemplateConcretDA1);
		concreteDAsAndConnectedNodes.put(relationshipTemplate2, nodeTemplateConcretDA2);

		Map<TRelationshipTemplate, TNodeTemplate> actualNodeWithConcreteDA = 
				DASpecification.getNodesWithSuitableConcreteDAAndTheDirectlyConnectedNode(nodeTemplateWithAbstractDA, deploymentArtifact, topologyTemplate);

		assertEquals(concreteDAsAndConnectedNodes, actualNodeWithConcreteDA);
	}
}

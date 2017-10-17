/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Karoline Saatkamp - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.driverspecificationandinjection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DriverInjectionTest extends TestWithGitBackedRepository {

	@Test
	public void injectDriver() throws Exception {
		setRevisionTo("d8ee55deecf37f5052d27807df691a7b70ec50f2");
		ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DASpecificationTest", false);
		TTopologyTemplate topologyTemplate = this.repository.getElement(id).getTopologyTemplate();
				
		TTopologyTemplate tTopologyTemplate = DriverInjection.injectDriver(topologyTemplate);

		TNodeTemplate nodeTemplateWithAbstractDA = tTopologyTemplate.getNodeTemplate("shetland_pony");
		List<TDeploymentArtifact> deploymentArtifacts = nodeTemplateWithAbstractDA.getDeploymentArtifacts().getDeploymentArtifact();
		List<String> deploymentArtifactNames = new ArrayList<>();
		deploymentArtifacts.stream().forEach(da -> deploymentArtifactNames.add(da.getName()));

		TRelationshipTemplate relationshipTemplate = tTopologyTemplate.getRelationshipTemplate("con_71");
		
		assertEquals(2, deploymentArtifacts.size());
		assertTrue(deploymentArtifactNames.contains("WesternEquipment_Pony"));
		assertTrue(deploymentArtifactNames.contains("DressageEquipment_Pony"));
		assertEquals("org.test.dressagedriver", ModelUtilities.getPropertiesKV(relationshipTemplate).get("Driver"));
	}
	
	@Test
	public void setDriverProperty() throws Exception {
		setRevisionTo("d8ee55deecf37f5052d27807df691a7b70ec50f2");
		ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DASpecificationTest", false);
		TTopologyTemplate topologyTemplate = this.repository.getElement(id).getTopologyTemplate();
		TRelationshipTemplate relationshipTemplate = topologyTemplate.getRelationshipTemplate("con_71");
		TDeploymentArtifact deploymentArtifact = topologyTemplate.getNodeTemplate("dressageequipment").getDeploymentArtifacts().getDeploymentArtifact().stream()
				.filter(da -> da.getName().equalsIgnoreCase("DressageEquipment_Pony")).findFirst().get();
		
		DriverInjection.setDriverProperty(relationshipTemplate, deploymentArtifact);
		
		assertEquals("org.test.dressagedriver", ModelUtilities.getPropertiesKV(relationshipTemplate).get("Driver"));
		
	}

}

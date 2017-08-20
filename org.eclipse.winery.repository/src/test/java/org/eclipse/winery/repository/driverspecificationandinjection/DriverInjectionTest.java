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
import java.util.List;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.PrefsTestEnabledGitBackedRepository;
import org.eclipse.winery.repository.WineryUsingHttpServer;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import org.eclipse.jetty.server.Server;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DriverInjectionTest {

	private static Git git;
	private static Server server;
	
	@BeforeClass
	public static void init() throws Exception {
		// enable git-backed repository
		PrefsTestEnabledGitBackedRepository prefsTestEnabledGitBackedRepository = new PrefsTestEnabledGitBackedRepository();
		git = prefsTestEnabledGitBackedRepository.git;
		server = WineryUsingHttpServer.createHttpServer(9080);
		server.start();
	}

	@AfterClass
	public static void shutdown() throws Exception {
		server.stop();
	}

	protected void setRevisionTo(String ref) throws GitAPIException {
		// TODO: newer JGit version: setForce(true)
		git.clean().setCleanDirectories(true).call();

		this.git.reset()
				.setMode(ResetCommand.ResetType.HARD)
				.setRef(ref)
				.call();
	}

	@Test
	public void injectDriver() throws Exception {
		setRevisionTo("d8ee55deecf37f5052d27807df691a7b70ec50f2");
		ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DASpecificationTest", false);
		ServiceTemplateResource serviceTemplateResource = new ServiceTemplateResource(id);
		TTopologyTemplate topologyTemplate = serviceTemplateResource.getServiceTemplate().getTopologyTemplate();
				
		TTopologyTemplate tTopologyTemplate = DriverInjection.injectDriver(topologyTemplate);

		TNodeTemplate nodeTemplateWithAbstractDA = tTopologyTemplate.getNodeTemplate("shetland_pony");
		List<TDeploymentArtifact> deploymentArtifacts = nodeTemplateWithAbstractDA.getDeploymentArtifacts().getDeploymentArtifact();
		List<String> deploymentArtifactNames = new ArrayList<>();
		deploymentArtifacts.stream().forEach(da -> deploymentArtifactNames.add(da.getName()));

		TRelationshipTemplate relationshipTemplate = tTopologyTemplate.getRelationshipTemplate("con_71");
		
		assertEquals(2, deploymentArtifacts.size());
		assertTrue(deploymentArtifactNames.contains("WesternEquipment_Pony"));
		assertTrue(deploymentArtifactNames.contains("DressageEquipment_Pony"));
		assertEquals("org.test.dressagedriver", ModelUtilities.getPropertiesKV(relationshipTemplate).getProperty("Driver"));
	}
	
	@Test
	public void setDriverProperty() throws Exception {
		setRevisionTo("d8ee55deecf37f5052d27807df691a7b70ec50f2");
		ServiceTemplateId id = new ServiceTemplateId("http://winery.opentosca.org/test/servicetemplates/ponyuniverse/daspecifier", "DASpecificationTest", false);
		ServiceTemplateResource serviceTemplateResource = new ServiceTemplateResource(id);
		TTopologyTemplate topologyTemplate = serviceTemplateResource.getServiceTemplate().getTopologyTemplate();
		TRelationshipTemplate relationshipTemplate = topologyTemplate.getRelationshipTemplate("con_71");
		TDeploymentArtifact deploymentArtifact = topologyTemplate.getNodeTemplate("dressageequipment").getDeploymentArtifacts().getDeploymentArtifact().stream()
				.filter(da -> da.getName().equalsIgnoreCase("DressageEquipment_Pony")).findFirst().get();
		
		DriverInjection.setDriverProperty(relationshipTemplate, deploymentArtifact);
		
		assertEquals("org.test.dressagedriver", ModelUtilities.getPropertiesKV(relationshipTemplate).getProperty("Driver"));
		
	}

}

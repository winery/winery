/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Karoline Saatkamp - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.winery.repository.driverspecificationandinjection;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;

public class DriverInjection {

	public static TTopologyTemplate injectDriver(TTopologyTemplate topologyTemplate) throws Exception {

		List<TNodeTemplate> nodeTemplatesWithAbstractDA = DASpecification.getNodeTemplatesWithAbstractDAs(topologyTemplate);

		for (TNodeTemplate nodeTemplateWithAbstractDA : nodeTemplatesWithAbstractDA) {
			List<TDeploymentArtifact> abstractDAsAttachedToNodeTemplate = nodeTemplateWithAbstractDA.getDeploymentArtifacts().getDeploymentArtifact().stream()
				.filter(da -> DASpecification.getArtifactTypeOfDA(da).getAbstract() == TBoolean.YES)
				.collect(Collectors.toList());
			for (TDeploymentArtifact abstractDA : abstractDAsAttachedToNodeTemplate) {
				Map<TRelationshipTemplate, TNodeTemplate> nodeTemplatesWithConcreteDA = DASpecification.getNodesWithSuitableConcreteDAAndTheDirectlyConnectedNode(nodeTemplateWithAbstractDA, abstractDA, topologyTemplate);

				if (nodeTemplatesWithConcreteDA != null) {
					for (TRelationshipTemplate relationshipTemplate : nodeTemplatesWithConcreteDA.keySet()) {
						TDeploymentArtifact concreteDeploymentArtifact = DASpecification.getSuitableConcreteDA(abstractDA, nodeTemplatesWithConcreteDA.get(relationshipTemplate));
						nodeTemplateWithAbstractDA.getDeploymentArtifacts().getDeploymentArtifact().add(concreteDeploymentArtifact);
						setDriverProperty(relationshipTemplate, concreteDeploymentArtifact);
					}
					//concrete DAs from the delivering Node Template must not be deleted. They are uploaded by the OpenTOSCA Container but not used.
					nodeTemplateWithAbstractDA.getDeploymentArtifacts().getDeploymentArtifact().remove(abstractDA);
				} else {
					throw new WineryRepositoryException("No concrete DA found for the abstract DA");
				}
			}
		}
		return topologyTemplate;
	}

	public static void setDriverProperty(TRelationshipTemplate relationshipTemplate, TDeploymentArtifact driverDeploymentArtifact) throws Exception {
		QName DAArtifactTemplateQName = driverDeploymentArtifact.getArtifactRef();
		ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId(DAArtifactTemplateQName);
		TArtifactTemplate artifactTemplate = RepositoryFactory.getRepository().getElement(artifactTemplateId);

		Properties artifactProperties = ModelUtilities.getPropertiesKV(artifactTemplate);
		Properties relationshipProperties = ModelUtilities.getPropertiesKV(relationshipTemplate);

		if ((artifactProperties != null) && (relationshipProperties != null) && artifactProperties.containsKey("Driver") && relationshipProperties.containsKey("Driver")) {
			relationshipProperties.setProperty("Driver", artifactProperties.getProperty("Driver"));
			RelationshipTypeId relationshipTypeId = new RelationshipTypeId(relationshipTemplate.getType());
			TRelationshipType relationshipType = RepositoryFactory.getRepository().getElement(relationshipTypeId);
			ModelUtilities.setPropertiesKV(ModelUtilities.getWinerysPropertiesDefinition(relationshipType), relationshipTemplate, relationshipProperties);
		} else {
			throw new WineryRepositoryException("No Property found to set to the driver classname");
		}
	}
}

/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

package org.eclipse.winery.repository.driverspecificationandinjection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.apache.commons.lang3.tuple.Pair;

public class DriverInjection {

    public static TTopologyTemplate injectDriver(TTopologyTemplate topologyTemplate) throws DriverInjectionException {

        List<TNodeTemplate> nodeTemplatesWithAbstractDA = DASpecification.getNodeTemplatesWithAbstractDAs(topologyTemplate);

        for (TNodeTemplate nodeTemplateWithAbstractDA : nodeTemplatesWithAbstractDA) {
            if (nodeTemplateWithAbstractDA.getDeploymentArtifacts() != null) {
                List<TDeploymentArtifact> abstractDAsAttachedToNodeTemplate = nodeTemplateWithAbstractDA.getDeploymentArtifacts().stream()
                    .filter(da -> DASpecification.getArtifactTypeOfDA(da).getAbstract())
                    .collect(Collectors.toList());
                for (TDeploymentArtifact abstractDA : abstractDAsAttachedToNodeTemplate) {
                    Set<Pair<TRelationshipTemplate, TNodeTemplate>> nodeTemplatesWithConcreteDA
                        = DASpecification.getNodesWithSuitableConcreteDAAndTheDirectlyConnectedNode(nodeTemplateWithAbstractDA, abstractDA, topologyTemplate);
                    if (!nodeTemplatesWithConcreteDA.isEmpty()) {
                        for (Pair<TRelationshipTemplate, TNodeTemplate> pair : nodeTemplatesWithConcreteDA) {
                            TRelationshipTemplate relationshipTemplate = pair.getLeft();
                            TNodeTemplate nodeTemplate = pair.getRight();
                            TDeploymentArtifact concreteDeploymentArtifact = DASpecification.getSuitableConcreteDA(abstractDA, nodeTemplate);

                            if (concreteDeploymentArtifact != null) {
                                nodeTemplateWithAbstractDA.getDeploymentArtifacts().add(concreteDeploymentArtifact);
                                setDriverProperty(relationshipTemplate, concreteDeploymentArtifact);
                            } else {
                                throw new DriverInjectionException("For the Node Template " + nodeTemplateWithAbstractDA.getId() + " no suitable Driver can be found. Please add a respective Messaging Middleware to proceed.");
                            }
                        }
                        // concrete DAs from the delivering Node Template must not be deleted. They are uploaded by the OpenTOSCA Container but not used.
                        nodeTemplateWithAbstractDA.getDeploymentArtifacts().remove(abstractDA);
                    } else {
                        throw new DriverInjectionException("For the Node Template " + nodeTemplateWithAbstractDA.getId() + " no suitable Driver can be found. Please add a respective Messaging Middleware to proceed.");
                    }
                }
            }
        }
        return topologyTemplate;
    }

    public static void setDriverProperty(TRelationshipTemplate relationshipTemplate, TDeploymentArtifact driverDeploymentArtifact) throws DriverInjectionException {
        QName DAArtifactTemplateQName = driverDeploymentArtifact.getArtifactRef();
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId(DAArtifactTemplateQName);
        TArtifactTemplate artifactTemplate = RepositoryFactory.getRepository().getElement(artifactTemplateId);

        Map<String, String> artifactProperties = ModelUtilities.getPropertiesKV(artifactTemplate);
        LinkedHashMap<String, String> relationshipProperties = ModelUtilities.getPropertiesKV(relationshipTemplate);

        if ((artifactProperties != null) && (relationshipProperties != null)
            && artifactProperties.containsKey("Driver") && relationshipProperties.containsKey("Driver")) {
            relationshipProperties.put("Driver", artifactProperties.get("Driver"));
            ModelUtilities.setPropertiesKV(relationshipTemplate, relationshipProperties);
        } else {
            throw new DriverInjectionException("No Property found to set to the driver classname");
        }
    }
}

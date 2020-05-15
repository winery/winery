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

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.*;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;

import javax.xml.namespace.QName;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        Map<String, String> artifactProperties = ModelUtilities.getPropertiesKV(artifactTemplate);
        LinkedHashMap<String, String> relationshipProperties = ModelUtilities.getPropertiesKV(relationshipTemplate);

        if ((artifactProperties != null) && (relationshipProperties != null) 
            && artifactProperties.containsKey("Driver") && relationshipProperties.containsKey("Driver")) {
            relationshipProperties.put("Driver", artifactProperties.get("Driver"));
            ModelUtilities.setPropertiesKV(relationshipTemplate, relationshipProperties);
        } else {
            throw new WineryRepositoryException("No Property found to set to the driver classname");
        }
    }
}

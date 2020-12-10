/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.converter.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.IRepository;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class TopologyTemplateUtils {

    public static void updateServiceTemplateWithResolvedDa(DefinitionsChildId entryId, IRepository repository, DefinitionsChildId oldArtifactTemplateId, DefinitionsChildId newArtifactTemplateId) throws IOException {
        TServiceTemplate serviceTemplate = repository.getElement(entryId);
        TArtifactTemplate newArtifactTemplate = repository.getElement(newArtifactTemplateId);
        TArtifactTemplate oldArtifactTemplate = repository.getElement(oldArtifactTemplateId);

        @NonNull List<TNodeTemplate> nodeTemplates = serviceTemplate.getTopologyTemplate().getNodeTemplates();

        for (TNodeTemplate nodeTemplate : nodeTemplates) {
            @Nullable TDeploymentArtifacts deploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
            if (deploymentArtifacts != null) {
                @NonNull List<TDeploymentArtifact> deplyArtifact = deploymentArtifacts.getDeploymentArtifact();
                for (TDeploymentArtifact deploymentArtifact : deplyArtifact) {
                    if (deploymentArtifact.getArtifactRef().getLocalPart().equals(oldArtifactTemplate.getName())) {
                        QName newSelfDeploymentArtifact = new QName(deploymentArtifact.getArtifactRef().getNamespaceURI(), deploymentArtifact.getArtifactRef().getLocalPart() + "-self", deploymentArtifact.getArtifactRef().getPrefix());
                        deploymentArtifact.setArtifactRef(newSelfDeploymentArtifact);
                        deploymentArtifact.setName(newArtifactTemplate.getName());
                        TDeploymentArtifacts newDeployMentArtifacts = new TDeploymentArtifacts();
                        newDeployMentArtifacts.getDeploymentArtifact().add(deploymentArtifact);
                        serviceTemplate.getTopologyTemplate().getNodeTemplate(nodeTemplate.getId()).setDeploymentArtifacts(newDeployMentArtifacts);
                        repository.setElement(entryId, serviceTemplate);
                    }
                }
            }
        }
    }

    public static void updateTopologyTemplate(DefinitionsChildId entryId, IRepository repository, DefinitionsChildId nodeTypeId, DefinitionsChildId newNodeTypeId) throws IOException {
        TServiceTemplate serviceTemplate = repository.getElement(entryId);
        TNodeType oldNodeType = repository.getElement(nodeTypeId);

        @Nullable TNodeTemplate tNodeTemplate = serviceTemplate.getTopologyTemplate().getNodeTemplate(oldNodeType.getIdFromIdOrNameField());

        TTopologyTemplate topologyTemplateCopy = serviceTemplate.getTopologyTemplate();

        @NonNull List<TRelationshipTemplate> relationshipTemplates = topologyTemplateCopy.getRelationshipTemplates();
        List<TRelationshipTemplate> toRemove = new ArrayList<>();

        List<TRelationshipTemplate> newIncomingRel = new ArrayList<>();

        for (TRelationshipTemplate relation : relationshipTemplates) {
            if (relation.getTargetElement().getRef().equals(tNodeTemplate)) {
                toRemove.add(relation);
                TRelationshipTemplate.SourceOrTargetElement targetElementNew = new TRelationshipTemplate.SourceOrTargetElement();
                targetElementNew.setRef(tNodeTemplate);
                relation.setTargetElement(targetElementNew);
                newIncomingRel.add(relation);
            } else if (relation.getSourceElement().getRef().equals(tNodeTemplate)) {
                toRemove.add(relation);
                TRelationshipTemplate.SourceOrTargetElement targetElementNew = new TRelationshipTemplate.SourceOrTargetElement();
                targetElementNew.setRef(tNodeTemplate);
                relation.setSourceElement(targetElementNew);
                newIncomingRel.add(relation);
            }
        }

        if (!newIncomingRel.isEmpty()) {
            relationshipTemplates.removeAll(toRemove);
            relationshipTemplates.addAll(newIncomingRel);
            topologyTemplateCopy.setRelationshipTemplates(relationshipTemplates);
        }

        topologyTemplateCopy.getNodeTemplateOrRelationshipTemplate().remove(tNodeTemplate);
        topologyTemplateCopy.getNodeTemplateOrRelationshipTemplate().add(tNodeTemplate);
        topologyTemplateCopy.setNodeTemplates(topologyTemplateCopy.getNodeTemplates());

        serviceTemplate.setTopologyTemplate(topologyTemplateCopy);
        repository.setElement(entryId, serviceTemplate);
    }
}

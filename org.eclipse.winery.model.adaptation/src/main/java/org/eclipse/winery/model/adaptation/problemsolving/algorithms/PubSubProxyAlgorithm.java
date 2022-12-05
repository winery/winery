/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.problemsolving.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.bouncycastle.math.raw.Mod;

public class PubSubProxyAlgorithm extends AbstractProxyAlgorithm {

    private static int newRelationshipIdCounter = 100;
    private static int IdCounter = 1;
    private static int newCapabilityCounter = 1;
    private static int newRequirementCounter = 1;

    @Override
    protected boolean insertProxy(TNodeTemplate sourceNode, TNodeTemplate targetNode, TRelationshipTemplate oldConnection, TTopologyTemplate topology) {
        IRepository repository = RepositoryFactory.getRepository();
        List<TPolicy> oldConnectionPolicies = oldConnection.getPolicies();

        TNodeType sourceNodeProxyType = repository.getElement(new NodeTypeId(OpenToscaBaseTypes.publisherProxy));
        TNodeTemplate sourceNodeProxy = ModelUtilities.instantiateNodeTemplate(sourceNodeProxyType);
        setNewCoordinates(sourceNode, sourceNodeProxy, 150, 0);
        topology.addNodeTemplate(sourceNodeProxy);

        TNodeType topicNodeType = repository.getElement(new NodeTypeId(OpenToscaBaseTypes.topic));
        TNodeTemplate topicNode = ModelUtilities.instantiateNodeTemplate(topicNodeType);
        setNewCoordinates(sourceNodeProxy, topicNode, 300, 0);
        topology.addNodeTemplate(topicNode);

        TNodeType targetNodeProxyType = repository.getElement(new NodeTypeId(OpenToscaBaseTypes.subscriberProxy));
        TNodeTemplate targetNodeProxy = ModelUtilities.instantiateNodeTemplate(targetNodeProxyType);
        setNewCoordinates(topicNode, targetNodeProxy, 300, 0);
        topology.addNodeTemplate(targetNodeProxy);

        setNewCoordinates(targetNodeProxy, targetNode, 300, 0);
        
        TRelationshipTemplate newRelationshipTemplate = ModelUtilities.createRelationshipTemplateAndAddToTopology(sourceNode, sourceNodeProxy,
            OpenToscaBaseTypes.httpConnectsTo, "httpconnectsTo", topology);
        topology.getRelationshipTemplate(newRelationshipTemplate.getId()).setPolicies(oldConnectionPolicies);
        newRelationshipTemplate = ModelUtilities.createRelationshipTemplateAndAddToTopology(sourceNodeProxy, topicNode,
            OpenToscaBaseTypes.topicConnectsTo, "topicconnectsTo", topology);
        topology.getRelationshipTemplate(newRelationshipTemplate.getId()).setPolicies(oldConnectionPolicies);
        newRelationshipTemplate = ModelUtilities.createRelationshipTemplateAndAddToTopology(targetNodeProxy, topicNode,
            OpenToscaBaseTypes.topicConnectsTo, "topicconnectsTo", topology);
        topology.getRelationshipTemplate(newRelationshipTemplate.getId()).setPolicies(oldConnectionPolicies);
        newRelationshipTemplate = ModelUtilities.createRelationshipTemplateAndAddToTopology(targetNodeProxy, targetNode,
            OpenToscaBaseTypes.httpConnectsTo, "httpconnectsTo", topology);
        topology.getRelationshipTemplate(newRelationshipTemplate.getId()).setPolicies(oldConnectionPolicies);
        
        Optional<String> participantSource = ModelUtilities.getParticipant(sourceNode);
        Optional<String> participantTarget = ModelUtilities.getParticipant(targetNode);
        
        if (participantSource.isPresent()) {
            ModelUtilities.setParticipant(sourceNodeProxy, participantSource.get());
        }
        if (participantTarget.isPresent()) {
            ModelUtilities.setParticipant(targetNodeProxy, participantTarget.get());
        }

        //Add Driver to the Proxies
        TArtifactTemplate artifactTemplate = RepositoryFactory.getRepository().getElement(new ArtifactTemplateId(OpenToscaBaseTypes.abstractJava11DriverTemplate));

        List<TDeploymentArtifact> sourceNodeProxyDAs = new ArrayList<>();
        if (sourceNodeProxy.getDeploymentArtifacts() != null && !sourceNodeProxy.getDeploymentArtifacts().isEmpty()) {
            sourceNodeProxyDAs.addAll(sourceNodeProxy.getDeploymentArtifacts());
        }
        sourceNodeProxyDAs.add(new TDeploymentArtifact
            .Builder(this.getUniqueDAID(sourceNodeProxy, "Driver"), artifactTemplate.getType())
            .setArtifactRef(OpenToscaBaseTypes.abstractPython3DriverTemplate).build());
        sourceNodeProxy.setDeploymentArtifacts(sourceNodeProxyDAs);
        

        List<TDeploymentArtifact> targetNodeProxyDAs = new ArrayList<>();
        if (targetNodeProxy.getDeploymentArtifacts() != null && !targetNodeProxy.getDeploymentArtifacts().isEmpty()) {
            targetNodeProxyDAs.addAll(targetNodeProxy.getDeploymentArtifacts());
        }
        targetNodeProxyDAs.add(new TDeploymentArtifact
            .Builder(this.getUniqueDAID(targetNodeProxy, "Driver"), artifactTemplate.getType())
            .setArtifactRef(OpenToscaBaseTypes.abstractJava11DriverTemplate).build());
        
        //TArtifactTemplate subscriberDA = RepositoryFactory.getRepository().getElement(new ArtifactTemplateId(OpenToscaBaseTypes.subscriberProxyDA));
        //targetNodeProxyDAs.add(new TDeploymentArtifact
        //    .Builder(this.getUniqueDAID(targetNodeProxy, "Subscriber_DA"), subscriberDA.getType())
        //    .setArtifactRef(OpenToscaBaseTypes.subscriberProxyDA).build());

        targetNodeProxy.setDeploymentArtifacts(targetNodeProxyDAs);
        
        List<TRelationshipTemplate> relationshipTemplates = topology.getRelationshipTemplates();
        relationshipTemplates.remove(oldConnection);
        topology.setRelationshipTemplates(relationshipTemplates);
        

        return true;
    }

    protected void setNewCoordinates(TNodeTemplate referenceNode, TNodeTemplate newNode, int newX, int newY) {
        int x = Integer.parseInt(referenceNode.getX()) + newX;
        newNode.setX(Integer.toString(x));

        int y = Integer.parseInt(referenceNode.getY()) + newY;
        newNode.setY(Integer.toString(y));
    }

    private String getUniqueReqID(TTopologyTemplate topologyTemplate) {
        String id = "req_";

        List<String> ids = new ArrayList<>();
        for (TNodeTemplate nt : topologyTemplate.getNodeTemplates()) {
            if (nt.getRequirements() != null) {
                nt.getRequirements().forEach(req -> ids.add(req.getId()));
            }
        }
        boolean uniqueID = false;
        while (!uniqueID) {
            if (!ids.contains(id + newRequirementCounter)) {
                id = id + newRequirementCounter;
                newRequirementCounter++;
                uniqueID = true;
            } else {
                newRequirementCounter++;
            }
        }

        return id;
    }

    private String getUniqueDAID(TNodeTemplate nodeTemplate, String name) {
        int counter = 1;
        String id = name;

        List<String> ids = new ArrayList<>();
        if (nodeTemplate.getDeploymentArtifacts() != null) {
            nodeTemplate.getDeploymentArtifacts().forEach(da -> ids.add(da.getName()));
        }
        if (!ids.contains(name)) {
            return id;
        }

        boolean uniqueID = false;
        while (!uniqueID) {
            if (!ids.contains(id + counter)) {
                id = id + counter;
                uniqueID = true;
            } else {
                counter++;
            }
        }

        return id;
    }
}

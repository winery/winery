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

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class PubSubProxyAlgorithm extends AbstractProxyAlgorithm {

    private static int newRelationshipIdCounter = 100;
    private static int IdCounter = 1;
    private static int newCapabilityCounter = 1;
    private static int newRequirementCounter = 1;

    @Override
    protected boolean insertProxy(TNodeTemplate sourceNode, TNodeTemplate targetNode, TRelationshipTemplate oldConnection, TTopologyTemplate topology) {
        topology.getNodeTemplateOrRelationshipTemplate().remove(oldConnection);

        TNodeTemplate sourceNodeProxy = new TNodeTemplate();
        sourceNodeProxy.setType(OpenToscaBaseTypes.publisherProxy);
        sourceNodeProxy.setName(OpenToscaBaseTypes.publisherProxy.getLocalPart());
        sourceNodeProxy.setId(sourceNode.getId() + "_proxy" + IdCounter++);
        setNewCoordinates(sourceNode, sourceNodeProxy, 150, 0);
        topology.addNodeTemplate(sourceNodeProxy);

        TNodeTemplate topicNode = new TNodeTemplate();
        topicNode.setType(OpenToscaBaseTypes.topic);
        topicNode.setName(OpenToscaBaseTypes.topic.getLocalPart());
        topicNode.setId(topicNode.getName() + "_new" + IdCounter++);
        setNewCoordinates(sourceNodeProxy, topicNode, 300, 0);
        topology.addNodeTemplate(topicNode);

        TNodeTemplate targetNodeProxy = new TNodeTemplate();
        targetNodeProxy.setType(OpenToscaBaseTypes.subscriberProxy);
        targetNodeProxy.setName(OpenToscaBaseTypes.subscriberProxy.getLocalPart());
        targetNodeProxy.setId(targetNode.getId() + "_proxy" + IdCounter++);
        setNewCoordinates(topicNode, targetNodeProxy, 300, 0);
        topology.addNodeTemplate(targetNodeProxy);

        setNewCoordinates(targetNodeProxy, targetNode, 300, 0);

        TNodeTemplate targetNodeProxyReturn = new TNodeTemplate();
        targetNodeProxyReturn.setType(OpenToscaBaseTypes.publisherProxy);
        targetNodeProxyReturn.setName(OpenToscaBaseTypes.publisherProxy.getLocalPart());
        targetNodeProxyReturn.setId(targetNode.getId() + "_proxy" + IdCounter++);
        setNewCoordinates(targetNode, targetNodeProxyReturn, 150, 100);
        topology.addNodeTemplate(targetNodeProxyReturn);

        TNodeTemplate topicNode2 = new TNodeTemplate();
        topicNode2.setType(OpenToscaBaseTypes.topic);
        topicNode2.setName(OpenToscaBaseTypes.topic.getLocalPart());
        topicNode2.setId(topicNode2.getName() + "_new" + IdCounter++);
        setNewCoordinates(sourceNodeProxy, topicNode2, 300, 100);
        topology.addNodeTemplate(topicNode2);

        TNodeTemplate sourceNodeProxyReturn = new TNodeTemplate();
        sourceNodeProxyReturn.setType(OpenToscaBaseTypes.subscriberProxy);
        sourceNodeProxyReturn.setName(OpenToscaBaseTypes.subscriberProxy.getLocalPart());
        sourceNodeProxyReturn.setId(sourceNode.getId() + "_proxy" + IdCounter++);
        setNewCoordinates(topicNode2, sourceNodeProxyReturn, 300, 100);
        topology.addNodeTemplate(sourceNodeProxyReturn);

        setNewCoordinates(targetNodeProxy, targetNode, 300, 0);

        ModelUtilities.createRelationshipTemplateAndAddToTopology(sourceNode, sourceNodeProxy,
            ToscaBaseTypes.connectsToRelationshipType, "connectsTo", topology);
        ModelUtilities.createRelationshipTemplateAndAddToTopology(sourceNodeProxy, topicNode,
            OpenToscaBaseTypes.topicConnectsTo, "connectsTo", topology);
        ModelUtilities.createRelationshipTemplateAndAddToTopology(targetNodeProxy, topicNode,
            OpenToscaBaseTypes.topicConnectsTo, "connectsTo", topology);
        ModelUtilities.createRelationshipTemplateAndAddToTopology(targetNodeProxy, targetNode,
            ToscaBaseTypes.connectsToRelationshipType, "connectsTo", topology);
        ModelUtilities.createRelationshipTemplateAndAddToTopology(sourceNodeProxyReturn, sourceNode,
            ToscaBaseTypes.connectsToRelationshipType, "connectsTo", topology);
        ModelUtilities.createRelationshipTemplateAndAddToTopology(sourceNodeProxyReturn, topicNode2,
            OpenToscaBaseTypes.topicConnectsTo, "connectsTo", topology);
        ModelUtilities.createRelationshipTemplateAndAddToTopology(targetNodeProxyReturn, topicNode2,
            OpenToscaBaseTypes.topicConnectsTo, "connectsTo", topology);
        ModelUtilities.createRelationshipTemplateAndAddToTopology(targetNode, targetNodeProxyReturn,
            ToscaBaseTypes.connectsToRelationshipType, "connectsTo", topology);

        //Add Requirements to the inserted Node Templates
        ModelUtilities.addRequirement(topicNode, OpenToscaBaseTypes.topicReqType, this.getUniqueReqID(topology));
        ModelUtilities.addRequirement(topicNode2, OpenToscaBaseTypes.topicReqType, this.getUniqueReqID(topology));
        ModelUtilities.addRequirement(sourceNodeProxy, OpenToscaBaseTypes.proxyReqType, this.getUniqueReqID(topology));
        ModelUtilities.addRequirement(sourceNodeProxyReturn, OpenToscaBaseTypes.proxyReqType, this.getUniqueReqID(topology));
        ModelUtilities.addRequirement(targetNodeProxy, OpenToscaBaseTypes.proxyReqType, this.getUniqueReqID(topology));
        ModelUtilities.addRequirement(targetNodeProxyReturn, OpenToscaBaseTypes.proxyReqType, this.getUniqueReqID(topology));

        //Add Driver to the Proxies
        TArtifactTemplate artifactTemplate = RepositoryFactory.getRepository().getElement(new ArtifactTemplateId(OpenToscaBaseTypes.abstractJava11DriverTemplate));

        List<TDeploymentArtifact> sourceNodeProxyDAs = new ArrayList<>();
        if (sourceNodeProxy.getDeploymentArtifacts() != null && !sourceNodeProxy.getDeploymentArtifacts().isEmpty()) {
            sourceNodeProxyDAs.addAll(sourceNodeProxy.getDeploymentArtifacts());
        }
        sourceNodeProxyDAs.add(new TDeploymentArtifact
            .Builder(this.getUniqueDAID(sourceNodeProxy, "Driver"), artifactTemplate.getType())
            .setArtifactRef(OpenToscaBaseTypes.abstractJava11DriverTemplate).build());
        sourceNodeProxy.setDeploymentArtifacts(sourceNodeProxyDAs);

        List<TDeploymentArtifact> sourceNodeProxyReturnDAs = new ArrayList<>();
        if (sourceNodeProxyReturn.getDeploymentArtifacts() != null && !sourceNodeProxyReturn.getDeploymentArtifacts().isEmpty()) {
            sourceNodeProxyReturnDAs.addAll(sourceNodeProxyReturn.getDeploymentArtifacts());
        }
        sourceNodeProxyReturnDAs.add(new TDeploymentArtifact
            .Builder(this.getUniqueDAID(sourceNodeProxyReturn, "Driver"), artifactTemplate.getType())
            .setArtifactRef(OpenToscaBaseTypes.abstractJava11DriverTemplate).build());
        sourceNodeProxyReturn.setDeploymentArtifacts(sourceNodeProxyReturnDAs);

        List<TDeploymentArtifact> targetNodeProxyDAs = new ArrayList<>();
        if (targetNodeProxy.getDeploymentArtifacts() != null && !targetNodeProxy.getDeploymentArtifacts().isEmpty()) {
            targetNodeProxyDAs.addAll(targetNodeProxy.getDeploymentArtifacts());
        }
        targetNodeProxyDAs.add(new TDeploymentArtifact
            .Builder(this.getUniqueDAID(targetNodeProxy, "Driver"), artifactTemplate.getType())
            .setArtifactRef(OpenToscaBaseTypes.abstractJava11DriverTemplate).build());
        targetNodeProxy.setDeploymentArtifacts(targetNodeProxyDAs);

        List<TDeploymentArtifact> targetNodeProxyReturnDAs = new ArrayList<>();
        if (targetNodeProxyReturn.getDeploymentArtifacts() != null && !targetNodeProxyReturn.getDeploymentArtifacts().isEmpty()) {
            targetNodeProxyReturnDAs.addAll(targetNodeProxyReturn.getDeploymentArtifacts());
        }
        targetNodeProxyReturnDAs.add(new TDeploymentArtifact
            .Builder(this.getUniqueDAID(targetNodeProxyReturn, "Driver"), artifactTemplate.getType())
            .setArtifactRef(OpenToscaBaseTypes.abstractJava11DriverTemplate).build());
        targetNodeProxyReturn.setDeploymentArtifacts(targetNodeProxyReturnDAs);

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

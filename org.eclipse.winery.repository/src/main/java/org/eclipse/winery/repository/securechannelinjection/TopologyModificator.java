/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.securechannelinjection;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.slf4j.LoggerFactory;

public class TopologyModificator {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TopologyModificator.class);

    private final IRepository repository = RepositoryFactory.getRepository();
    private final TServiceTemplate serviceTemplate;
    private final Map<String, TEntityTemplate> context;

    TopologyModificator(TServiceTemplate serviceTemplate, Map<String, TEntityTemplate> context) {
        this.serviceTemplate = serviceTemplate;
        this.context = context;
    }

    protected ServiceTemplateId modifyTopology(List<String> actions) throws IOException {

        for (String action : actions) {
            LOGGER.debug("Processing: " + action);
            String[] tokens = action.split("\\s+");
            switch (tokens[0]) {
                case "addNode":
                    addNode(tokens[1], tokens[2]);
                    break;
                case "deleteNode":
                    deleteNode((TNodeTemplate) context.get(tokens[1]));
                    break;
                case "replaceNode":
                    replaceNode((TNodeTemplate) context.get(tokens[1]), (TNodeTemplate) context.get(tokens[2]));
                    break;
                case "addRelationship":
                    addRelationship(tokens[1], (TNodeTemplate) context.get(tokens[2]), (TNodeTemplate) context.get(tokens[3]));
                    break;
                case "deleteRelationship":
                    deleteRelationship((TNodeTemplate) context.get(tokens[1]), (TNodeTemplate) context.get(tokens[2]));
                    break;
                default:
                    throw new SecureChannelException("Unknown action");
            }
        }
        LOGGER.debug("Successfully modified service template");
        serviceTemplate.setName("secured-" + serviceTemplate.getName());
        ServiceTemplateId newServiceId = new ServiceTemplateId(serviceTemplate.getTargetNamespace(), serviceTemplate.getName(), false);
        repository.setElement(newServiceId, serviceTemplate);
        LOGGER.debug("Saved service template as " + newServiceId.getQName().getLocalPart());
        return newServiceId;
    }

    /**
     * Adds a node template to the topology
     *
     * @param nodeType    The namespace and name of the node, separated by two colons ('::')
     * @param contextName The name under which the node template will be accessible via the context
     * @throws SecureChannelException If the node type does not exist
     */
    private void addNode(String nodeType, String contextName) throws SecureChannelException {
        LOGGER.debug("Adding node " + contextName);
        String namespace = nodeType.split("::")[0];
        String localPart = nodeType.split("::")[1];
        NodeTypeId nodeTypeId = new NodeTypeId(namespace, localPart, false);
        if (repository.exists(nodeTypeId)) {
            TNodeType tNodeType = repository.getElement(nodeTypeId);
            TNodeTemplate nodeTemplate = ModelUtilities.instantiateNodeTemplate(tNodeType);
            serviceTemplate.getTopologyTemplate().addNodeTemplate(nodeTemplate);
            context.put(contextName, nodeTemplate);
            LOGGER.debug("Added node to service template and context");
        } else {
            LOGGER.debug("Couldn't find nodeType");
            throw new SecureChannelException("Could not find node type to add");
        }
    }

    /**
     * Deletes a node template and all relationship templates pointing towards or away from this node template from the
     * topology.
     *
     * @param nodeTemplate The node template which is deleted
     */
    private void deleteNode(TNodeTemplate nodeTemplate) {
        LOGGER.debug("Deleting node " + nodeTemplate.getName());
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        topologyTemplate.getNodeTemplateOrRelationshipTemplate().remove(nodeTemplate);
        // Removes all relationships with nodeTemplate as source or target
        topologyTemplate.setRelationshipTemplates(topologyTemplate.getRelationshipTemplates().stream()
            .filter(rel -> !rel.getSourceElement().getRef().getId().equals(nodeTemplate.getId())
                && !rel.getTargetElement().getRef().getId().equals(nodeTemplate.getId()))
            .collect(Collectors.toList()));
    }

    /**
     * Replaces a node template with a different node template. All properties and artifacts are copied over to the new
     * node template.
     *
     * @param oldNode The node template that is going to be replaced
     * @param newNode The node template that is going to be inserted
     */
    private void replaceNode(TNodeTemplate oldNode, TNodeTemplate newNode) {
        LOGGER.debug("Replacing node " + oldNode.getId() + " by " + newNode.getId());
        LOGGER.debug("Copying all properties, deployment artifacts etc");
        newNode.setProperties(oldNode.getProperties());
        newNode.setDeploymentArtifacts(oldNode.getDeploymentArtifacts());
        newNode.setRequirements(oldNode.getRequirements());
        newNode.setCapabilities(oldNode.getCapabilities());
        newNode.setMinInstances(oldNode.getMinInstances());
        newNode.setMaxInstances(oldNode.getMaxInstances());
        newNode.setName(oldNode.getName());
        newNode.setPolicies(oldNode.getPolicies());
        serviceTemplate.getTopologyTemplate().addNodeTemplate(newNode);
        LOGGER.debug("Changing all relationships that contain the old node to the new node");
        serviceTemplate.getTopologyTemplate().getRelationshipTemplates().forEach(rel -> {
            if (rel.getSourceElement().getRef().getId().equals(oldNode.getId())) {
                TRelationshipTemplate.SourceOrTargetElement newSource = new TRelationshipTemplate.SourceOrTargetElement();
                newSource.setRef(newNode);
                rel.setSourceElement(newSource);
            } else if (rel.getTargetElement().getRef().getId().equals(oldNode.getId())) {
                TRelationshipTemplate.SourceOrTargetElement newTarget = new TRelationshipTemplate.SourceOrTargetElement();
                newTarget.setRef(newNode);
                rel.setTargetElement(newTarget);
            }
        });
        LOGGER.debug("Removing old node");
        serviceTemplate.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().remove(oldNode);
    }

    /**
     * Adds a relationship between two nodes
     *
     * @param relationshipType The namespace and name of the relationship
     * @param source           The source of the relationship
     * @param target           The target of the relationship
     * @throws SecureChannelException If the relationship type does not exist
     */
    private void addRelationship(String relationshipType, TNodeTemplate source, TNodeTemplate target) throws SecureChannelException {
        LOGGER.debug("Adding relationship " + relationshipType + ": " + source.getName() + " -> " + target.getName());
        String namespace = relationshipType.split("::")[0];
        String localPart = relationshipType.split("::")[1];
        RelationshipTypeId relationshipTypeId = new RelationshipTypeId(namespace, localPart, false);
        if (repository.exists(relationshipTypeId)) {
            TRelationshipType tRelationshipType = repository.getElement(relationshipTypeId);
            TRelationshipTemplate relationshipTemplate = ModelUtilities.instantiateRelationshipTemplate(tRelationshipType, source, target);
            serviceTemplate.getTopologyTemplate().addRelationshipTemplate(relationshipTemplate);
        } else {
            LOGGER.error("Couldn't find relationshipType");
            throw new SecureChannelException("Could not find relationship type to add");
        }
    }

    /**
     * Deletes a relationship from the topology based on the source and target.
     *
     * @param source The source of the relationship
     * @param target The target of the relationship
     * @throws SecureChannelException If no relationship between the source and target exists
     */
    private void deleteRelationship(TNodeTemplate source, TNodeTemplate target) throws SecureChannelException {
        LOGGER.debug("Deleting relationship " + source.getName() + " -> " + target.getName());
        TRelationshipTemplate relationship = serviceTemplate.getTopologyTemplate().getRelationshipTemplates().stream()
            .filter(rel -> rel.getSourceElement().getRef().getId().equals(source.getId()))
            .filter(rel -> rel.getTargetElement().getRef().getId().equals(target.getId()))
            .findAny().orElseThrow(() -> new SecureChannelException("Couldn't find relationship between nodes"));
        List<TRelationshipTemplate> newRelationshipTemplates = serviceTemplate.getTopologyTemplate().getRelationshipTemplates();
        newRelationshipTemplates.remove(relationship);
        serviceTemplate.getTopologyTemplate().setRelationshipTemplates(newRelationshipTemplates);
    }
}

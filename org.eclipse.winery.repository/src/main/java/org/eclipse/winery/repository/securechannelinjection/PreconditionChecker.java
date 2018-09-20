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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
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

public class PreconditionChecker {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(PreconditionChecker.class);
    private final IRepository repository = RepositoryFactory.getRepository();
    private final TServiceTemplate serviceTemplate;
    private final Map<String, TEntityTemplate> context;

    PreconditionChecker(TServiceTemplate serviceTemplate, Map<String, TEntityTemplate> context) {
        this.serviceTemplate = serviceTemplate;
        this.context = context;
    }

    protected boolean checkPreconditions(List<String> preconditions) {
        boolean fulfillsPreconditions = true;
        for (String precondition : preconditions) {
            LOGGER.debug("Precondition: " + precondition);
            String[] tokens = precondition.split("\\s+");
            switch (tokens[0]) {
                case "runsOnVm":
                    LOGGER.debug("Checking precondition runsOnVm.");
                    TNodeTemplate requiredNode = (TNodeTemplate) context.get(tokens[1]);
                    fulfillsPreconditions = runsOnVm(requiredNode, tokens[1]);
                    break;
                case "hasSecureVersion":
                    LOGGER.debug("Checking precondition hasSecureVersion");
                    TNodeTemplate insecureNode = (TNodeTemplate) context.get(tokens[1]);
                    TNodeType type = repository.getElement(new NodeTypeId(insecureNode.getTypeAsQName()));
                    fulfillsPreconditions = hasSecureVersion(type, tokens[1]);
                    break;
                default:
                    throw new SecureChannelException("Unknown precondition");
            }
            if (!fulfillsPreconditions) {
                break;
            }
        }

        return fulfillsPreconditions;
    }

    /**
     * Checks whether the node runs on a VM or not. All nodes representing a VM are required to extend from the abstract
     * node "VM_w1-wip1". If the node runs on a VM, the node template representing the VM is added to the context as
     * <code>sourceOrTarget + "Host"</code>.
     *
     * @param node           The node that is checked
     * @param sourceOrTarget A string representing whether the node is the source or target of the insecure
     *                       relationship. This is needed to put the host node into the context.
     * @return True if the node runs on a VM.
     */
    private boolean runsOnVm(TNodeTemplate node, String sourceOrTarget) {

        List<TNodeTemplate> hostedOnNodes;
        TNodeTemplate hostedNode = node;
        TNodeType hostedNodeType = repository.getElement(new NodeTypeId(hostedNode.getTypeAsQName()));

        while (!derivesFromVM(hostedNodeType)) {
            LOGGER.debug("Current node:" + hostedNode.getName());
            hostedOnNodes = getHostedOnNodeOfNodeTemplate(hostedNode);
            if (!hostedOnNodes.isEmpty()) {
                hostedNode = hostedOnNodes.get(0);
                hostedNodeType = repository.getElement(new NodeTypeId(hostedNode.getTypeAsQName()));
                LOGGER.debug("Name of the host type: " + hostedNodeType.getName());
            } else {
                // Reached the end of the hostedOn chain
                LOGGER.info("Didn't find any more hostedOn relationships");
                return false;
            }
        }

        System.out.println("Putting in host as " + sourceOrTarget + "Host");
        context.put(sourceOrTarget + "Host", hostedNode);
        return true;
    }

    /**
     * Checks whether a node type has a direct descendant that implements the secure feature. These descendants are
     * uniformly named the same as the node type + "-secure". If a secure version exists, it will be added as
     * <code>contextName + "-secure"</code>
     *
     * @param type        The type of the node that's checked
     * @param contextName The name of the node template in the context
     */
    private boolean hasSecureVersion(TNodeType type, String contextName) {
        LOGGER.debug("Entering hasSecureVersion");
        Optional<TNodeType> secureType = repository.getAllDefinitionsChildIds().stream()
            .filter(id -> id instanceof NodeTypeId).map(NodeTypeId.class::cast).map(repository::getElement)
            .filter(nodeType -> nodeType.getDerivedFrom() != null &&
                repository.getElement(new NodeTypeId(nodeType.getDerivedFrom().getType())).equals(type))
            .filter(nodeType -> {
                LOGGER.debug("Name of the node type: " + nodeType.getName());
                return nodeType.getName().contains(type.getName() + "-secure");
            }).findAny();
        if (secureType.isPresent()) {
            context.put(contextName + "-secure", ModelUtilities.instantiateNodeTemplate(secureType.get()));
            return true;
        }
        return false;
    }

    /**
     * Find all predecessors of a node template. the predecessor is the target of a hostedOn relationship to the
     * nodeTemplate
     *
     * @param nodeTemplate for which all predecessors should be found
     * @return list of predecessors
     */
    private List<TNodeTemplate> getHostedOnNodeOfNodeTemplate(TNodeTemplate nodeTemplate) {
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        List<TNodeTemplate> hostedOnNodeTemplates = new ArrayList<>();
        List<TRelationshipTemplate> outgoingRelationships = ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate);
        for (TRelationshipTemplate relationshipTemplate : outgoingRelationships) {
            TRelationshipType base = getBasisRelationshipType(relationshipTemplate.getType());
            LOGGER.debug("Base: " + base.getName());

            if (base.getName().equalsIgnoreCase("hostedOn")) {
                hostedOnNodeTemplates.add(ModelUtilities.getTargetNodeTemplateOfRelationshipTemplate(topologyTemplate, relationshipTemplate));
            }
        }
        return hostedOnNodeTemplates;
    }

    private TRelationshipType getBasisRelationshipType(QName relationshipTypeQName) {
        RelationshipTypeId parentRelationshipTypeId = new RelationshipTypeId(relationshipTypeQName);
        TRelationshipType parentRelationshipType = repository.getElement(parentRelationshipTypeId);
        TRelationshipType basisRelationshipType = parentRelationshipType;

        while (parentRelationshipType != null) {
            basisRelationshipType = parentRelationshipType;

            if (parentRelationshipType.getDerivedFrom() != null) {
                parentRelationshipTypeId = new RelationshipTypeId(parentRelationshipType.getDerivedFrom().getTypeRef());
                parentRelationshipType = repository.getElement(parentRelationshipTypeId);
            } else {
                parentRelationshipType = null;
            }
        }
        return basisRelationshipType;
    }

    /**
     * Checks whether a a node type derives from a VM node type
     *
     * @param toBeChecked The node type that is checked
     * @return True if the checked node type derives from a VM node type
     */
    private boolean derivesFromVM(TNodeType toBeChecked) {
        TNodeType node = toBeChecked;
        while (node != null) {
            if (node.getName().startsWith("VM")) {
                return true;
            }
            if (node.getDerivedFrom() != null) {
                node = repository.getElement(new NodeTypeId(node.getDerivedFrom().getTypeAsQName()));
            } else {
                node = null;
            }
        }
        return false;
    }
}

/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.enhance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.constants.OpenToscaInterfaces;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class EnhancementUtils {

    public static TTopologyTemplate determineStatefulComponents(TTopologyTemplate topology) {
        Map<QName, TNodeType> nodeTypes = RepositoryFactory.getRepository().getQNameToElementMapping(NodeTypeId.class);

        topology.getNodeTemplates().stream()
            .filter(nodeTemplate -> {
                TNodeType type = nodeTypes.get(nodeTemplate.getType());
                if (Objects.nonNull(type.getTags())) {
                    return type.getTags().getTag()
                        .stream()
                        .anyMatch(
                            tag -> "stateful".equals(tag.getName().toLowerCase())
                                || "isStateful".toLowerCase().equals(tag.getName().toLowerCase())
                        );
                }

                return false;
            })
            // avoid duplicate annotations
            .filter(node -> !ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.statefulComponentPolicyType))
            .forEach(node ->
                ModelUtilities.addPolicy(node, OpenToscaBaseTypes.statefulComponentPolicyType, "stateful")
            );

        return topology;
    }

    public static TopologyAndErrorList determineFreezableComponents(TTopologyTemplate topology) {
        Map<QName, TNodeType> nodeTypes = RepositoryFactory.getRepository().getQNameToElementMapping(NodeTypeId.class);

        ArrayList<String> errorList = new ArrayList<>();
        topology.getNodeTemplates().stream()
            // only iterate over all stateful components
            .filter(node -> ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.statefulComponentPolicyType))
            // avoid duplicate annotations
            .filter(node -> !ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.freezableComponentPolicyType))
            .forEach(node -> {
                TNodeType type = nodeTypes.get(node.getType());
                if (ModelUtilities.nodeTypeHasInterface(type, OpenToscaInterfaces.stateInterface)) {
                    ModelUtilities.addPolicy(node, OpenToscaBaseTypes.freezableComponentPolicyType, "freezable");
                } else {
                    TRelationshipTemplate relationshipTemplate;
                    boolean isFreezable = false;
                    do {
                        relationshipTemplate = getHostedOnRelationship(topology, node);

                        if (Objects.nonNull(relationshipTemplate)) {
                            TNodeTemplate host = (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
                            TNodeType hostType = nodeTypes.get(host.getType());
                            if (ModelUtilities.nodeTypeHasInterface(hostType, OpenToscaInterfaces.stateInterface)) {
                                ModelUtilities.addPolicy(host, OpenToscaBaseTypes.freezableComponentPolicyType, "freezable");
                                isFreezable = true;
                            }
                        }
                    } while (!isFreezable && Objects.nonNull(relationshipTemplate));

                    if (!isFreezable) {
                        errorList.add(node.getId());
                    }
                }
            });

        TopologyAndErrorList topologyAndErrorList = new TopologyAndErrorList();
        topologyAndErrorList.errorList = errorList;
        topologyAndErrorList.topologyTemplate = topology;

        return topologyAndErrorList;
    }

    public static TTopologyTemplate cleanFreezableComponents(TTopologyTemplate topology) {
        topology.getNodeTemplates().stream()
            // only iterate over all freezable components
            .filter(node -> ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.freezableComponentPolicyType))
            .forEach(node -> {
                TRelationshipTemplate hostedOnRelationship = getHostedOnRelationship(topology, node);
                while (Objects.nonNull(hostedOnRelationship)) {
                    TNodeTemplate host = (TNodeTemplate) hostedOnRelationship.getTargetElement().getRef();
                    if (ModelUtilities.containsPolicyType(host, OpenToscaBaseTypes.freezableComponentPolicyType)) {
                        node.getPolicies().getPolicy()
                            .removeIf(policy -> policy.getPolicyType().equals(OpenToscaBaseTypes.freezableComponentPolicyType));
                        hostedOnRelationship = null;
                    } else {
                        hostedOnRelationship = getHostedOnRelationship(topology, host);
                    }
                }
            });

        return topology;
    }

    private static TRelationshipTemplate getHostedOnRelationship(TTopologyTemplate topology, TNodeTemplate node) {
        Map<QName, TRelationshipType> relationshipTypes = RepositoryFactory.getRepository().getQNameToElementMapping(RelationshipTypeId.class);
        List<TRelationshipTemplate> outgoingRelationshipTemplates = ModelUtilities.getOutgoingRelationshipTemplates(topology, node);
        return outgoingRelationshipTemplates.stream()
            .filter(relation -> ModelUtilities.isOfType(ToscaBaseTypes.hostedOnRelationshipType, relation.getType(), relationshipTypes))
            .findFirst()
            .orElse(null);
    }
}

/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.splitting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class ProviderRepository {

    public static final ProviderRepository INSTANCE = new ProviderRepository();

    private static final String NS_NAME_START = "http://www.opentosca.org/providers/";

    /**
     * Pointing to a concrete node template has to be done by putting this node template into a separeate namespace <p>
     * The given targetLocation is appended to {@see NS_NAME_START} to gain the namespace. All NodeTemplates in this
     * namespace and all "lower" namespaces (e.g., starting with that string) are returned.
     *
     * @return All node templates available for the given targetLocation.
     */

    public List<TTopologyTemplate> getAllTopologyFragmentsForLocationAndOfferingCapability(String targetLocation, TRequirement requirement) {
        QName reqTypeQName = requirement.getType();
        RequirementTypeId reqTypeId = new RequirementTypeId(reqTypeQName);
        QName requiredCapabilityType = RepositoryFactory.getRepository().getElement(reqTypeId).getRequiredCapabilityType();

        return getAllTopologyFragmentsForLocation(targetLocation).stream()
            .filter(tf -> {
                Optional<TNodeTemplate> nodeTemplate = ModelUtilities.getAllNodeTemplates(tf).stream()
                    .filter(nt -> nt.getCapabilities() != null)
                    .filter(nt -> nt.getCapabilities().getCapability().stream()
                        .anyMatch(cap -> cap.getType().equals(requiredCapabilityType))
                    ).findAny();
                if (nodeTemplate.isPresent()) {
                    return true;
                } else {
                    return false;
                }
            })
            .collect(Collectors.toList());
    }

    /**
     * Get all fragments which fulfill all specified requirements from the given provider.
     *
     * @return the matching fragments if any exists, empty list else
     */
    public List<TTopologyTemplate> getTopologyFragments(String targetLocation, List<TRequirement> requirements) {
        if (targetLocation == null || requirements == null || requirements.isEmpty()) {
            return new ArrayList<>();
        }
        Map<TRequirement, List<TTopologyTemplate>> fragmentsForRequirement = new HashMap<>();
        List<TRequirement> mergedReqs = mergeByType(requirements);

        for (TRequirement requirement : mergedReqs) {
            List<TTopologyTemplate> fragments =
                getAllTopologyFragmentsForLocationAndOfferingCapability(targetLocation, requirement);
            // all requirements have to have at least one possible fragment
            if (fragments.isEmpty()) {
                return new ArrayList<>();
            } else {
                fragmentsForRequirement.put(requirement,
                    getAllTopologyFragmentsForLocationAndOfferingCapability(targetLocation, requirement));
            }
        }
        return getIntersection(fragmentsForRequirement.values());
    }

    /**
     * Only get fragments which contain one node template.
     */
    public List<TTopologyTemplate> getPaaSFragments(String targetLabel, List<TRequirement> requirements) {
        List<TTopologyTemplate> fragments = getTopologyFragments(targetLabel, requirements);
        fragments.removeIf(tt -> tt.getNodeTemplates().size() != 1);
        return fragments;
    }

    private List<TRequirement> mergeByType(List<TRequirement> requirements) {
        // valid because only requirements/capability types are considered for matching
        Map<QName, TRequirement> removeDuplicates = new HashMap<>();
        requirements.forEach(req -> removeDuplicates.put(req.getType(), req));
        return new ArrayList<>(removeDuplicates.values());
    }

    private List<TTopologyTemplate> getIntersection(Collection<List<TTopologyTemplate>> fragments) {
        // get fragments fulfilling all requirements
        Iterator<List<TTopologyTemplate>> iterator = fragments.iterator();
        Set<TTopologyTemplate> intersection = new HashSet<>(iterator.next());
        while (iterator.hasNext()) {
            intersection.retainAll(iterator.next());
        }
        return new ArrayList<>(intersection);
    }

    public List<TTopologyTemplate> getAllTopologyFragmentsForLocation(String targetLocation) {
        String namespaceStr;
        if ("*".equals(targetLocation)) {
            namespaceStr = NS_NAME_START;
        } else {
            namespaceStr = NS_NAME_START + targetLocation.toLowerCase();
        }

        return RepositoryFactory.getRepository().getAllDefinitionsChildIds(ServiceTemplateId.class).stream()
            // get all service templates in the namespace
            .filter(id -> id.getNamespace().getDecoded().toLowerCase().startsWith(namespaceStr))
            // get all contained node templates
            .flatMap(id -> {
                TTopologyTemplate topologyTemplate = RepositoryFactory.getRepository().getElement(id).getTopologyTemplate();
                List<TNodeTemplate> matchedNodeTemplates = topologyTemplate.getNodeTemplateOrRelationshipTemplate().stream()
                    .filter(t -> t instanceof TNodeTemplate)
                    .map(TNodeTemplate.class::cast)
                    .collect(Collectors.toList());

                matchedNodeTemplates.forEach(t -> ModelUtilities.setTargetLabel(t, id.getNamespace().getDecoded().replace(NS_NAME_START, "")));

                return getAllTopologyFragmentsFromServiceTemplate(topologyTemplate).stream();
            })
            .collect(Collectors.toList());
    }

    private List<TTopologyTemplate> getAllTopologyFragmentsFromServiceTemplate(TTopologyTemplate topologyTemplate) {

        List<TTopologyTemplate> topologyFragments = new ArrayList<>();

        Splitting helperFunctions = new Splitting();
        List<TNodeTemplate> nodeTemplatesWithoutIncomingRelationship = helperFunctions.getNodeTemplatesWithoutIncomingHostedOnRelationships(topologyTemplate);
        List<TNodeTemplate> visitedNodeTemplates = new ArrayList<>();

        //It can only be one topology fragment contained in the service template
        if (nodeTemplatesWithoutIncomingRelationship.size() == 1) {
            TDocumentation documentation = new TDocumentation();
            Optional<String> targetLabel = ModelUtilities.getTargetLabel(nodeTemplatesWithoutIncomingRelationship.get(0));
            String label;
            if (!targetLabel.isPresent()) {
                label = "unkown";
            } else {
                label = targetLabel.get();
            }
            documentation.getContent().add("Stack of Node Template " + nodeTemplatesWithoutIncomingRelationship.get(0).getId()
                + " from Provider Repository " + label);
            topologyTemplate.getDocumentation().add(documentation);
            topologyFragments.add(topologyTemplate);
        } else {
            for (TNodeTemplate nodeWithoutIncomingRel : nodeTemplatesWithoutIncomingRelationship) {
                if (!visitedNodeTemplates.contains(nodeWithoutIncomingRel)) {
                    TDocumentation documentation = new TDocumentation();
                    Optional<String> targetLabel = ModelUtilities.getTargetLabel(nodeWithoutIncomingRel);
                    String label;
                    if (!targetLabel.isPresent()) {
                        label = "unkown";
                    } else {
                        label = targetLabel.get();
                    }
                    documentation.getContent().add("Stack of Node Template " + nodeWithoutIncomingRel.getId()
                        + " from Provider Repository " + label);
                    TTopologyTemplate topologyFragment = new TTopologyTemplate.Builder()
                        .addDocumentation(documentation)
                        .build();
                    topologyFragment.getNodeTemplateOrRelationshipTemplate().addAll(breadthFirstSearch(nodeWithoutIncomingRel, topologyTemplate));
                    topologyFragments.add(topologyFragment);

                    topologyFragment.getNodeTemplateOrRelationshipTemplate().stream()
                        .filter(et -> et instanceof TNodeTemplate)
                        .map(TNodeTemplate.class::cast)
                        .forEach(nt -> visitedNodeTemplates.add(nt));
                }
            }
        }
        return topologyFragments;
    }

    private List<TEntityTemplate> breadthFirstSearch(TNodeTemplate nodeTemplate, TTopologyTemplate topologyTemplate) {
        List<TEntityTemplate> topologyFragmentElements = new ArrayList<>();
        topologyFragmentElements.add(nodeTemplate);
        List<TRelationshipTemplate> outgoingRelationships = ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate);

        for (TRelationshipTemplate outgoingRelationship : outgoingRelationships) {
            Object successor = outgoingRelationship.getTargetElement().getRef();
            if (successor instanceof TNodeTemplate) {
                topologyFragmentElements.add(outgoingRelationship);
                topologyFragmentElements.addAll(breadthFirstSearch((TNodeTemplate) successor, topologyTemplate));
            }
        }
        return topologyFragmentElements;
    }
}

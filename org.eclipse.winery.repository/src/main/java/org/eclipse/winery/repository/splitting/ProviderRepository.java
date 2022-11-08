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
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class ProviderRepository {

    public static final ProviderRepository INSTANCE = new ProviderRepository();

    private static final String NS_NAME_START = "http://opentosca.org/matching/providers/";

    /**
     * Pointing to a concrete node template has to be done by putting this node template into a separate namespace <p>
     * The given targetLocation is appended to {@see NS_NAME_START} to gain the namespace. All NodeTemplates in this
     * namespace and all "lower" namespaces (e.g., starting with that string) are returned.
     *
     * @return All node templates available for the given targetLocation.
     */

    public List<TServiceTemplate> getAllTopologyFragmentsForLocationAndOfferingCapability(String targetLocation, TRequirement requirement) {
        QName reqTypeQName = requirement.getType();
        RequirementTypeId reqTypeId = new RequirementTypeId(reqTypeQName);
        QName requiredCapabilityType = RepositoryFactory.getRepository().getElement(reqTypeId).getRequiredCapabilityType();

        return getAllTopologyFragmentsForLocation(targetLocation).stream()
            .filter(tf ->
                getNodesWithOpenCapabilities(tf.getTopologyTemplate()) != null)
            .filter(tf -> getNodesWithOpenCapabilities(tf.getTopologyTemplate()).stream()
                .anyMatch(nt -> nt.getCapabilities().stream()
                    .anyMatch(cap -> cap.getType().equals(requiredCapabilityType))
                )
            )
            .collect(Collectors.toList());
    }

    /**
     * Get all fragments which fulfill all specified requirements from the given provider.
     *
     * @return the matching fragments if any exists, empty list else
     */
    public List<TServiceTemplate> getTopologyFragments(String targetLocation, List<TRequirement> requirements) {
        if (targetLocation == null || requirements == null || requirements.isEmpty()) {
            return new ArrayList<>();
        }
        Map<TRequirement, List<TServiceTemplate>> fragmentsForRequirement = new HashMap<>();
        List<TRequirement> mergedReqs = mergeByType(requirements);

        for (TRequirement requirement : mergedReqs) {
            List<TServiceTemplate> fragments =
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
        List<TServiceTemplate> fragments = getTopologyFragments(targetLabel, requirements);

        return fragments.stream().filter(tt -> tt.getTopologyTemplate().getNodeTemplates().size() != 1).map(st -> st.getTopologyTemplate())
            .collect(Collectors.toList());
    }

    private List<TRequirement> mergeByType(List<TRequirement> requirements) {
        // valid because only requirements/capability types are considered for matching
        Map<QName, TRequirement> removeDuplicates = new HashMap<>();
        requirements.forEach(req -> removeDuplicates.put(req.getType(), req));
        return new ArrayList<>(removeDuplicates.values());
    }

    private List<TServiceTemplate> getIntersection(Collection<List<TServiceTemplate>> fragments) {
        // get fragments fulfilling all requirements
        Iterator<List<TServiceTemplate>> iterator = fragments.iterator();
        HashSet<TServiceTemplate> set = new HashSet<>();
        ArrayList<TServiceTemplate> result = new ArrayList<>();
        while (iterator.hasNext()) {
            set.retainAll(iterator.next());
        }
        result.addAll(set);
        return result;
    }

    public List<TServiceTemplate> getAllTopologyFragmentsForLocation(String targetLocation) {
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
            .map(id -> {
                TServiceTemplate serviceTemplate = RepositoryFactory.getRepository().getElement(id);
                TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
                if (topologyTemplate != null) {
                    List<TNodeTemplate> matchedNodeTemplates = topologyTemplate.getNodeTemplateOrRelationshipTemplate().stream()
                        .filter(t -> t instanceof TNodeTemplate)
                        .map(TNodeTemplate.class::cast)
                        .collect(Collectors.toList());

                    matchedNodeTemplates.forEach(t ->
                        ModelUtilities.setTargetLabel(t, id.getNamespace().getDecoded().replace(NS_NAME_START, ""))
                    );
                }

                return serviceTemplate;
            })
            .collect(Collectors.toList());
    }

    private List<TNodeTemplate> getNodesWithOpenCapabilities(TTopologyTemplate topologyTemplate) {

        return topologyTemplate.getNodeTemplates().stream()
            .filter(nt -> nt.getCapabilities() != null && !nt.getCapabilities().isEmpty())
            .filter(nt -> ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, nt).isEmpty())
            .collect(Collectors.toList());
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

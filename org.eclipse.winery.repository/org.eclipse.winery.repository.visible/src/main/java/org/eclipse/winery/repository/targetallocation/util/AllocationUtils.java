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

package org.eclipse.winery.repository.targetallocation.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

/**
 * Utility methods for the target allocation approach.
 */
public class AllocationUtils {

    public static long idCounter = 0;
    private static IRepository repository = RepositoryFactory.getRepository();

    /**
     * Calculates the permutations of the given lists.
     *
     * @param max the maximum count of permutations to produce
     */
    public static <T> List<List<T>> getPermutations(List<List<T>> possibilities, int max) {
        List<List<T>> results = new ArrayList<>();
        getPermutations(possibilities, results, 0, new ArrayList<>(), max);
        return results;
    }

    /**
     * Helper method for calculating permutations.
     */
    private static <T> void getPermutations(List<List<T>> possibilities, List<List<T>> results,
                                            int depth, List<T> current, int max) {
        if (results.size() > max - 1) {
            return;
        }
        if (depth >= possibilities.size()) {
            results.add(current);
            return;
        }
        for (T possibility : possibilities.get(depth)) {
            List<T> newCurrent = new ArrayList<>(current);
            newCurrent.add(possibility);
            getPermutations(possibilities, results, depth + 1, newCurrent, max);
        }
    }

    /**
     * Create topologies from the input topology where all top level nts have
     * the target labels specified by one permutation.
     *
     * @param topology     topology to clone for generating the new topologies
     * @param permutations possible target label permutations: each list contains all top level NTs
     *                     of the topology with set target labels
     */
    public static List<TopologyWrapper> generateTargetLabelTopologies(TopologyWrapper topology,
                                                                      List<List<PermutationHelper>> permutations) {
        List<TopologyWrapper> topologies = new ArrayList<>();

        for (List<PermutationHelper> permutation : permutations) {
            TopologyWrapper newTopology = new TopologyWrapper(deepcopy(topology.getTopology()));
            Map<String, TNodeTemplate> topLevelNtsByIds = newTopology.getTopLevelNtsByIds();

            for (PermutationHelper possibility : permutation) {
                String targetLabel = possibility.getTargetLabel();
                for (TNodeTemplate correspondingNT : possibility.getCorrespondingNTs()) {
                    TNodeTemplate nodeTemplate = topLevelNtsByIds.get(correspondingNT.getId());
                    ModelUtilities.setTargetLabel(nodeTemplate, targetLabel);
                }
            }
            topologies.add(newTopology);
        }
        return topologies;
    }

    public static List<TPolicyTemplate> getPolicyTemplates(TNodeTemplate nodeTemplate) {
        List<TPolicyTemplate> policyTemplates = new ArrayList<>();

        if (nodeTemplate.getPolicies() != null) {
            List<TPolicy> tPolicies = nodeTemplate.getPolicies().getPolicy();
            for (TPolicy tPolicy : tPolicies) {
                policyTemplates.add(toPolicyTemplate(tPolicy));
            }
        }
        return policyTemplates;
    }

    public static TPolicyTemplate toPolicyTemplate(TPolicy policy) {
        PolicyTemplateId id = new PolicyTemplateId(policy.getPolicyRef());
        return repository.getElement(id);
    }

    /**
     * Still no complete copy of all TOSCA constructs. Cloned so that !original.equals(clone).
     */
    public static TTopologyTemplate deepcopy(TTopologyTemplate topologyTemplate) {
        return deepcopy(topologyTemplate, true);
    }

    /**
     * Still no complete copy of all TOSCA constructs.
     */
    public static TTopologyTemplate deepcopy(TTopologyTemplate topologyTemplate, boolean changeNames) {
        TTopologyTemplate clone = new TTopologyTemplate();
        Map<String, TNodeTemplate> clonedNTsByIds = new HashMap<>();

        for (TNodeTemplate nodeTemplate : topologyTemplate.getNodeTemplates()) {
            TNodeTemplate clonedNT = clone(nodeTemplate, changeNames);
            clone.addNodeTemplate(clonedNT);
            clonedNTsByIds.put(clonedNT.getId(), clonedNT);
        }

        for (TRelationshipTemplate relationshipTemplate : topologyTemplate.getRelationshipTemplates()) {
            TRelationshipTemplate clonedRT = clone(relationshipTemplate, changeNames);
            // source
            if (relationshipTemplate.getSourceElement().getRef() instanceof TNodeTemplate) {
                clonedRT.setSourceNodeTemplate(clonedNTsByIds.get(relationshipTemplate.getSourceElement().getRef().getId()));
            } else {
                TRelationshipTemplate.SourceOrTargetElement source = new TRelationshipTemplate.SourceOrTargetElement();
                source.setRef(relationshipTemplate.getSourceElement().getRef());
                clonedRT.setSourceElement(source);
            }
            // target
            if (relationshipTemplate.getTargetElement().getRef() instanceof TNodeTemplate) {
                clonedRT.setTargetNodeTemplate(clonedNTsByIds.get(relationshipTemplate.getTargetElement().getRef().getId()));
            } else {
                TRelationshipTemplate.SourceOrTargetElement target = new TRelationshipTemplate.SourceOrTargetElement();
                target.setRef(relationshipTemplate.getTargetElement().getRef());
                clonedRT.setTargetElement(target);
            }
            clone.addRelationshipTemplate(clonedRT);
        }
        return clone;
    }

    /**
     * Clone the NodeTemplate and change the name of the clone if requested, so that !original.equals(clone).
     *
     * @param nodeTemplate the NodeTemplate to clone
     * @param changeNames <code>true</code> if name shall be changed by adding a number suffix, <code>false</code> 
     *                    otherwise
     * @return the cloned NodeTemplate
     */
    public static TNodeTemplate clone(TNodeTemplate nodeTemplate, boolean changeNames) {
        TNodeTemplate cloned = BackendUtils.clone(nodeTemplate);

        // name used in equals -> make unique to avoid equals bugs caused by cloning
        if (changeNames) {
            cloned.setName(cloned.getId() + idCounter++);
        }
        return cloned;
    }

    /**
     * Clone the RelationshipTemplate and change the name of the clone if requested, so that !original.equals(clone).
     *
     * @param relationshipTemplate the RelationshipTemplate to clone
     * @param changeNames          <code>true</code> if name shall be changed by adding a number suffix,
     *                             <code>false</code>
     *                             otherwise
     * @return the cloned RelationshipTemplate
     */
    public static TRelationshipTemplate clone(TRelationshipTemplate relationshipTemplate, boolean changeNames) {
        TRelationshipTemplate cloned = BackendUtils.clone(relationshipTemplate);

        // name used in equals -> make unique to avoid equals bugs caused by cloning
        if (changeNames) {
            cloned.setName(cloned.getId() + idCounter++);
        }
        return cloned;
    }
}

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

package org.eclipse.winery.repository.targetallocation.criteria.minhosts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.splitting.ProviderRepository;
import org.eclipse.winery.repository.targetallocation.util.AllocationUtils;
import org.eclipse.winery.repository.targetallocation.util.PermutationHelper;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

/**
 * Assignment of target labels for {@link MinHosts}.
 * Considers three different states: all predecessors of a node tempalte have target labels,
 * some (at least one with and one without) predecessors have target labels,
 * no predecessor has target labels.
 * Assigns target labels so that split is avoided and the amount of node templates of the topology is minimal.
 */
class TargetLabelAssignment {

    private enum PredecessorsTargetLabelState {
        ALL_PREDECESSORS_HAVE_TARGET_LABELS,
        SOME_PREDECESSORS_HAVE_TARGET_LABELS,
        NO_PREDECESSOR_HAS_TARGET_LABEL
    }

    private TopologyWrapper original;
    private Set<String> presentTargetLabels = new HashSet<>();
    private Map<TNodeTemplate, Set<String>> possibleTargetLabels = new HashMap<>();
    private List<TNodeTemplate> doneTopLevelHosts = new ArrayList<>();
    private List<List<TNodeTemplate>> assignedAsGroup = new ArrayList<>();
    private int outputCap;

    TargetLabelAssignment(TopologyWrapper topology, int outputCap) {
        this.original = topology;
        this.outputCap = outputCap;
    }

    protected List<TopologyWrapper> computeTargetLabelPermutations() {
        addMissingTargetLabels();
        List<List<PermutationHelper>> possibilities = new ArrayList<>();

        // permute node templates which were assigned target labels at the same time as group
        for (List<TNodeTemplate> group : assignedAsGroup) {
            Set<String> targetLabels = possibleTargetLabels.get(group.get(0));
            List<PermutationHelper> possibility = new ArrayList<>();
            for (String targetLabel : targetLabels) {
                possibility.add(new PermutationHelper(group, targetLabel));
            }
            possibilities.add(possibility);
        }
        List<TNodeTemplate> done = assignedAsGroup.stream().flatMap(List::stream).collect(Collectors.toList());

        // permute rest of node templates
        for (Map.Entry<TNodeTemplate, Set<String>> entry : possibleTargetLabels.entrySet()) {
            if (done.contains(entry.getKey()) || !original.isTopLevelNT(entry.getKey()) ||
                !original.getNodeTemplates().contains(entry.getKey())) {
                continue;
            }
            List<PermutationHelper> possibility = new ArrayList<>();
            for (String targetLabel : entry.getValue()) {
                possibility.add(new PermutationHelper(entry.getKey(), targetLabel));
            }
            possibilities.add(possibility);
        }
        List<List<PermutationHelper>> permutations = AllocationUtils.getPermutations(possibilities, outputCap);
        return AllocationUtils.generateTargetLabelTopologies(original, permutations);
    }

    private void addMissingTargetLabels() {
        for (TNodeTemplate nodeTemplate : original.getNodeTemplates()) {
            Optional<String> targetLabel = ModelUtilities.getTargetLabel(nodeTemplate);
            Set<String> targetLabels = new HashSet<>();
            if (targetLabel.isPresent()) {
                targetLabels.add(targetLabel.get());
                presentTargetLabels.add(targetLabel.get());
            }
            possibleTargetLabels.put(nodeTemplate, targetLabels);
        }
        addMissingTargetLabelsRecursive(original.getTopLevelHosts(), new TNodeTemplate());
    }

    private void addMissingTargetLabelsRecursive(List<TNodeTemplate> topLevelHosts, TNodeTemplate stop) {
        for (TNodeTemplate topLevelHost : topLevelHosts) {
            if (doneTopLevelHosts.contains(topLevelHost)) {
                continue;
            }

            // traverse topology until matching can be found
            TNodeTemplate previous = null;
            TNodeTemplate next = topLevelHost;
            while (next != null && !next.equals(stop)) {
                // resolve predecessors
                List<TNodeTemplate> predecessors = original.getHostedOnPredecessors(next);
                predecessors.remove(previous);
                for (TNodeTemplate predecessor : predecessors) {
                    if (!original.isTopLevelNT(predecessor) && possibleTargetLabels.get(predecessor).isEmpty()) {
                        List<TNodeTemplate> topLevelHostPredecessors =
                            original.getTransitiveTopLevelHostPredecessors(predecessor);
                        addMissingTargetLabelsRecursive(topLevelHostPredecessors, next);
                    }
                }

                // assign target labels based on the three cases
                if (isMatchable(next)) {
                    doneTopLevelHosts.addAll(original.getTransitiveTopLevelHostPredecessors(next));
                    break;
                }
                previous = next;
                next = original.getHostedOnSuccessor(next);
            }
        }
    }

    private boolean isMatchable(TNodeTemplate nodeTemplate) {
        PredecessorsTargetLabelState state = determinePredecessorsTargetLabelState(nodeTemplate);
        switch (state) {
            case ALL_PREDECESSORS_HAVE_TARGET_LABELS:
                return isMatchableAllPredecessorsHaveTargetLabel(nodeTemplate);
            case SOME_PREDECESSORS_HAVE_TARGET_LABELS:
                return isMatchableSomePredecessorsHaveTargetLabels(nodeTemplate);
            case NO_PREDECESSOR_HAS_TARGET_LABEL:
                return isMatchableNoPredecessorHasTargetLabel(nodeTemplate);
            default:
                return false;
        }
    }

    private boolean isMatchableAllPredecessorsHaveTargetLabel(TNodeTemplate nodeTemplate) {
        List<TNodeTemplate> predecessors = original.getHostedOnPredecessors(nodeTemplate);
        Map<String, List<TNodeTemplate>> targetLabelGroups = groupByTargetLabel(predecessors);

        // check each group if its matchable here
        Set<TNodeTemplate> matchingFound = new HashSet<>();
        for (Map.Entry<String, List<TNodeTemplate>> entry : targetLabelGroups.entrySet()) {
            List<TRequirement> reqs = original.getRequirements(entry.getValue());
            List<TTopologyTemplate> fragments = ProviderRepository.INSTANCE.getPaaSFragments(entry.getKey(), reqs);
            if (!fragments.isEmpty()) {
                matchingFound.addAll(entry.getValue());
            }
        }

        Set<TNodeTemplate> noMatchingFound = new HashSet<>(predecessors);
        noMatchingFound.removeAll(matchingFound);
        if (noMatchingFound.isEmpty()) {
            // all groups of target labels can be matched here
            original.removeNotNeededSuccessors(nodeTemplate);
            return true;
        } else {
            // at least one target label group can't be matched here
            Set<String> targetLabelsToPropagate = new HashSet<>();
            for (TNodeTemplate ntNoMatchFound : noMatchingFound) {
                targetLabelsToPropagate.addAll(possibleTargetLabels.get(ntNoMatchFound));
            }
            possibleTargetLabels.get(nodeTemplate).addAll(targetLabelsToPropagate);
            return false;
        }
    }

    private boolean isMatchableSomePredecessorsHaveTargetLabels(TNodeTemplate nodeTemplate) {
        List<TNodeTemplate> predecessors = original.getHostedOnPredecessors(nodeTemplate);
        Map<String, List<TNodeTemplate>> targetLabelGroups = groupByTargetLabel(predecessors);
        Set<String> predecessorTargetLabels = targetLabelGroups.keySet();
        List<TNodeTemplate> predecessorsWithOutLabel = predecessors.stream()
            .filter(pred -> possibleTargetLabels.get(pred).isEmpty()).collect(Collectors.toList());

        // try to match all nts without target labels together with one of the target label groups
        boolean allAssigned = false;
        for (String targetLabel : predecessorTargetLabels) {
            List<TRequirement> requirementsToFulfill = original.getRequirements(predecessorsWithOutLabel);
            requirementsToFulfill.addAll(original.getRequirements(targetLabelGroups.get(targetLabel)));
            List<TTopologyTemplate> fragments = ProviderRepository.INSTANCE.getPaaSFragments(targetLabel, requirementsToFulfill);

            if (!fragments.isEmpty()) {
                for (TNodeTemplate predWithoutLabel : predecessorsWithOutLabel) {
                    addTargetLabelsToTransitiveTopLevelNTPredecessors(predWithoutLabel, Collections.singleton(targetLabel));
                }
                allAssigned = true;
            }
        }

        List<TNodeTemplate> toGroup = predecessorsWithOutLabel.stream()
            .map(nt -> original.getTransitiveTopLevelPredecessors(nt))
            .flatMap(List::stream).collect(Collectors.toList());
        assignedAsGroup.add(toGroup);
        if (allAssigned) {
            // at least one target label found for all nts without target labels together
            original.removeNotNeededSuccessors(nodeTemplate);
            return true;
        } else {
            // no match possible here but present target labels have to be fulfilled -> add and propagate
            for (TNodeTemplate predWithoutLabel : predecessorsWithOutLabel) {
                addTargetLabelsToTransitiveTopLevelNTPredecessors(predWithoutLabel, predecessorTargetLabels);
            }
            possibleTargetLabels.get(nodeTemplate).addAll(predecessorTargetLabels);
            return false;
        }
    }

    private boolean isMatchableNoPredecessorHasTargetLabel(TNodeTemplate nodeTemplate) {
        Set<String> matchingTargetLabels = new HashSet<>();

        for (String targetLabel : presentTargetLabels) {
            List<TRequirement> predReqs = original.getPredecessorRequirements(nodeTemplate);
            List<TTopologyTemplate> fragments = ProviderRepository.INSTANCE.getPaaSFragments(targetLabel, predReqs);
            if (!fragments.isEmpty()) {
                matchingTargetLabels.add(targetLabel);
            }
        }

        if (matchingTargetLabels.isEmpty()) {
            return false;
        } else {
            List<TNodeTemplate> topLevelPredecessors = addTargetLabelsToTransitiveTopLevelNTPredecessors(nodeTemplate,
                matchingTargetLabels);
            assignedAsGroup.add(topLevelPredecessors);
            doneTopLevelHosts.addAll(original.getTransitiveTopLevelHostPredecessors(nodeTemplate));
            original.removeNotNeededSuccessors(nodeTemplate);
            return true;
        }
    }

    private PredecessorsTargetLabelState determinePredecessorsTargetLabelState(TNodeTemplate nodeTemplate) {
        List<TNodeTemplate> predecessors = original.getHostedOnPredecessors(nodeTemplate);
        boolean predWithTargetLabelExists = false;
        boolean predWithoutTargetLabelExists = false;

        for (TNodeTemplate predecessor : predecessors) {
            if (possibleTargetLabels.get(predecessor).isEmpty()) {
                predWithoutTargetLabelExists = true;
            } else {
                predWithTargetLabelExists = true;
            }
        }
        if (predWithoutTargetLabelExists && predWithTargetLabelExists) {
            return PredecessorsTargetLabelState.SOME_PREDECESSORS_HAVE_TARGET_LABELS;
        }
        if (predWithoutTargetLabelExists) {
            return PredecessorsTargetLabelState.NO_PREDECESSOR_HAS_TARGET_LABEL;
        }
        if (predWithTargetLabelExists) {
            return PredecessorsTargetLabelState.ALL_PREDECESSORS_HAVE_TARGET_LABELS;
        }
        return null;
    }

    /**
     * The same NT can be in multiple target label groups because of multiple possible target labels.
     */
    private Map<String, List<TNodeTemplate>> groupByTargetLabel(List<TNodeTemplate> nodeTemplates) {
        Map<String, List<TNodeTemplate>> targetLabelGroups = new HashMap<>();

        for (TNodeTemplate nodeTemplate : nodeTemplates) {
            for (String targetLabel : possibleTargetLabels.get(nodeTemplate)) {
                if (targetLabelGroups.get(targetLabel) == null) {
                    List<TNodeTemplate> nts = new ArrayList<>();
                    nts.add(nodeTemplate);
                    targetLabelGroups.put(targetLabel, nts);
                } else {
                    targetLabelGroups.get(targetLabel).add(nodeTemplate);
                }
            }
        }
        return targetLabelGroups;
    }

    private List<TNodeTemplate> addTargetLabelsToTransitiveTopLevelNTPredecessors(TNodeTemplate nodeTemplate,
                                                                                  Set<String> targetLabels) {
        List<TNodeTemplate> topLevelPredecessors = original.getTransitiveTopLevelPredecessors(nodeTemplate);
        for (TNodeTemplate topLevelPredecessor : topLevelPredecessors) {
            possibleTargetLabels.get(topLevelPredecessor).addAll(targetLabels);
        }
        return topLevelPredecessors;
    }
}

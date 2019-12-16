/********************************************************************************
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
 ********************************************************************************/

package org.eclipse.winery.repository.targetallocation.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.splitting.Splitting;

/**
 * Utility methods for operating on a TTopologyTemplate.
 */
public class TopologyWrapper {

    private TTopologyTemplate topologyTemplate;
    private Splitting splitting = new Splitting();

    public TopologyWrapper(TTopologyTemplate topologyTemplate) {
        this.topologyTemplate = topologyTemplate;
    }

    public List<TNodeTemplate> getHostedOnPredecessors(TNodeTemplate nodeTemplate) {
        return getHostedOns(getIncomingRTs(nodeTemplate)).stream()
            .map(this::getSourceNT)
            .collect(Collectors.toList());
    }

    /**
     * Assumption: components can only be hosted on one host.
     *
     * @return successor if existing, null else
     */
    public TNodeTemplate getHostedOnSuccessor(TNodeTemplate nodeTemplate) {
        List<TRelationshipTemplate> hostedOns = getHostedOns(getOutgoingRTs(nodeTemplate));
        if (hostedOns.size() >= 1) {
            return getTargetNT(hostedOns.get(0));
        }
        return null;
    }

    public List<TRelationshipTemplate> getHostedOns(List<TRelationshipTemplate> relationshipTemplates) {
        return relationshipTemplates.stream()
            .filter(this::isHostedOn)
            .collect(Collectors.toList());
    }

    public boolean isHostedOn(TRelationshipTemplate relationshipTemplate) {
        TRelationshipType type = splitting.getBasisRelationshipType(relationshipTemplate.getType());
        if (type.getName().equalsIgnoreCase("hostedOn")) {
            return true;
        }
        return type.getValidTarget() != null &&
            type.getValidTarget().getTypeRef().getLocalPart().equalsIgnoreCase("Container");
    }

    public List<TRelationshipTemplate> getConnectsTos(List<TRelationshipTemplate> relationshipTemplates) {
        return relationshipTemplates.stream()
            .filter(this::isConnectsTo)
            .collect(Collectors.toList());
    }

    public boolean isConnectsTo(TRelationshipTemplate relationshipTemplate) {
        TRelationshipType type = splitting.getBasisRelationshipType(relationshipTemplate.getType());
        if (type.getName().equalsIgnoreCase("connectsTo")) {
            return true;
        }
        return type.getValidTarget() != null &&
            type.getValidTarget().getTypeRef().getLocalPart().equalsIgnoreCase("Endpoint");
    }

    public List<TNodeTemplate> getTopLevelHosts() {
        return getNodeTemplates().stream()
            .filter(this::isTopLevelHost)
            .collect(Collectors.toList());
    }

    public List<TNodeTemplate> getTopLevelNTs() {
        return getNodeTemplates().stream()
            .filter(this::isTopLevelNT)
            .collect(Collectors.toList());
    }

    /**
     * @return all transitive predecessors without incoming hostedOns or
     * the input Node Template if it doesn't have incoming hostedOns
     */
    public List<TNodeTemplate> getTransitiveTopLevelPredecessors(TNodeTemplate nodeTemplate) {
        List<TNodeTemplate> results = new ArrayList<>();
        getTransitiveTopLevelPredecessors(nodeTemplate, results);
        return results;
    }

    private void getTransitiveTopLevelPredecessors(TNodeTemplate nodeTemplate, List<TNodeTemplate> results) {
        if (isTopLevelNT(nodeTemplate)) {
            results.add(nodeTemplate);
            return;
        }
        for (TNodeTemplate predecessor : getHostedOnPredecessors(nodeTemplate)) {
            getTransitiveTopLevelPredecessors(predecessor, results);
        }
    }

    /**
     * @return all transitive predecessors which predecessors have no incoming hostedOns
     * including the inout Node Template itself if it fits there characteristics
     */
    public List<TNodeTemplate> getTransitiveTopLevelHostPredecessors(TNodeTemplate nodeTemplate) {
        List<TNodeTemplate> results = new ArrayList<>();
        if (isTopLevelHost(nodeTemplate)) {
            results.add(nodeTemplate);
        }
        for (TNodeTemplate predecessor : getHostedOnPredecessors(nodeTemplate)) {
            getTransitiveTopLevelHostPredecessors(predecessor, results);
        }
        return results;
    }

    private void getTransitiveTopLevelHostPredecessors(TNodeTemplate nodeTemplate, List<TNodeTemplate> results) {
        if (isTopLevelHost(nodeTemplate)) {
            results.add(nodeTemplate);
            return;
        }
        for (TNodeTemplate predecessor : getHostedOnPredecessors(nodeTemplate)) {
            getTransitiveTopLevelHostPredecessors(predecessor, results);
        }
    }

    /**
     * Remove node templates which become obsolete because of the injection of a cloud provider service.
     * A node template is obsolete if it doesn't have predecessors.
     */
    public void removeNotNeededSuccessors(TNodeTemplate nodeTemplate) {
        TNodeTemplate successor = getHostedOnSuccessor(nodeTemplate);
        TNodeTemplate newSuccessor;
        removeAllRTs(getHostedOns(getOutgoingRTs(nodeTemplate)));

        while (successor != null && getHostedOnPredecessors(successor).isEmpty()) {
            newSuccessor = getHostedOnSuccessor(successor);
            removeNT(successor);
            removeAllRTs(getRelationshipTemplates(successor));
            successor = newSuccessor;
        }
    }

    /**
     * Remove all not needed nts starting from the ones provided.
     */
    public void removeNotNeededNTs(Set<String> originalTopLevelNTIds) {
        for (TNodeTemplate topLevelNT : getTopLevelNTs()) {
            if (!originalTopLevelNTIds.contains(topLevelNT.getId())) {
                removeNotNeededSuccessors(topLevelNT);
                removeNT(topLevelNT);
            }
        }
    }

    /**
     * Only top level NTs are considered.
     */
    public Set<String> getPresentTargetLabels() {
        return getTopLevelNTs().stream()
            .map(ModelUtilities::getTargetLabel)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
    }

    public List<TRequirement> getRequirements(TNodeTemplate nodeTemplate) {
        if (nodeTemplate.getRequirements() != null) {
            return nodeTemplate.getRequirements().getRequirement();
        }
        return new ArrayList<>();
    }

    public List<TRequirement> getRequirements(List<TNodeTemplate> nodeTemplates) {
        return nodeTemplates.stream()
            .map(this::getRequirements)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    /**
     * @return the requirements of the predecessors of the node template
     */
    public List<TRequirement> getPredecessorRequirements(TNodeTemplate nodeTemplate) {
        return getHostedOnPredecessors(nodeTemplate).stream()
            .map(this::getRequirements)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    public Map<String, TNodeTemplate> getTopLevelNtsByIds() {
        Map<String, TNodeTemplate> ntsByIds = new HashMap<>();
        for (TNodeTemplate nodeTemplate : getTopLevelNTs()) {
            ntsByIds.put(nodeTemplate.getId(), nodeTemplate);
        }
        return ntsByIds;
    }

    public Map<String, TNodeTemplate> getNTsByIds() {
        Map<String, TNodeTemplate> ntsByIds = new HashMap<>();
        for (TNodeTemplate nodeTemplate : getNodeTemplates()) {
            ntsByIds.put(nodeTemplate.getId(), nodeTemplate);
        }
        return ntsByIds;
    }

    /**
     * @return true if Node Template has no hostedOn predecessors, false else
     */
    public boolean isTopLevelNT(TNodeTemplate nodeTemplate) {
        return getHostedOnPredecessors(nodeTemplate).isEmpty();
    }

    /**
     * @return true if Node Template has no hostedOn successor, false else
     */
    public boolean isBottomLevelNT(TNodeTemplate nodeTemplate) {
        return getHostedOnSuccessor(nodeTemplate) == null;
    }

    /**
     * @return true if Node Template has a predecessor which is a top level NT, false else
     */
    public boolean isTopLevelHost(TNodeTemplate nodeTemplate) {
        for (TNodeTemplate predecessor : getHostedOnPredecessors(nodeTemplate)) {
            if (isTopLevelNT(predecessor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        List<String> nts = topologyTemplate.getNodeTemplates().stream().map(HasId::getId).collect(Collectors.toList());
        List<String> rts = topologyTemplate.getRelationshipTemplates().stream().map(rt ->
            "{" + getSourceNT(rt).getId() + "--" + rt.getType().getLocalPart() + "-->" + getTargetNT(rt).getId())
            .collect(Collectors.toList());
        return "NTs: " + nts + "RTs: " + rts;
    }

    public List<TRelationshipTemplate> getIncomingRTs(TNodeTemplate nodeTemplate) {
        return ModelUtilities.getIncomingRelationshipTemplates(topologyTemplate, nodeTemplate);
    }

    public List<TRelationshipTemplate> getOutgoingRTs(TNodeTemplate nodeTemplate) {
        return ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate);
    }

    public List<TRelationshipTemplate> getRelationshipTemplates() {
        return topologyTemplate.getRelationshipTemplates();
    }

    public List<TRelationshipTemplate> getRelationshipTemplates(TNodeTemplate nodeTemplate) {
        List<TRelationshipTemplate> allRTs = new ArrayList<>();
        allRTs.addAll(getIncomingRTs(nodeTemplate));
        allRTs.addAll(getOutgoingRTs(nodeTemplate));
        return allRTs;
    }

    public List<TNodeTemplate> getNodeTemplates() {
        return topologyTemplate.getNodeTemplates();
    }

    public boolean removeNT(TNodeTemplate nodeTemplate) {
        return topologyTemplate.getNodeTemplateOrRelationshipTemplate().remove(nodeTemplate);
    }

    public boolean removeAllNTs(Collection<TNodeTemplate> nodeTemplates) {
        return topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(nodeTemplates);
    }

    public boolean removeRT(TRelationshipTemplate relationshipTemplate) {
        return topologyTemplate.getNodeTemplateOrRelationshipTemplate().remove(relationshipTemplate);
    }

    public boolean removeAllRTs(Collection<TRelationshipTemplate> relationshipTemplates) {
        return topologyTemplate.getNodeTemplateOrRelationshipTemplate().removeAll(relationshipTemplates);
    }

    public TNodeTemplate getSourceNT(TRelationshipTemplate tRelationshipTemplate) {
        return ModelUtilities.getSourceNodeTemplateOfRelationshipTemplate(topologyTemplate, tRelationshipTemplate);
    }

    public TNodeTemplate getTargetNT(TRelationshipTemplate tRelationshipTemplate) {
        return ModelUtilities.getTargetNodeTemplateOfRelationshipTemplate(topologyTemplate, tRelationshipTemplate);
    }

    public TTopologyTemplate getTopology() {
        return this.topologyTemplate;
    }

    public void setTopology(TTopologyTemplate topologyTemplate) {
        this.topologyTemplate = topologyTemplate;
    }
}

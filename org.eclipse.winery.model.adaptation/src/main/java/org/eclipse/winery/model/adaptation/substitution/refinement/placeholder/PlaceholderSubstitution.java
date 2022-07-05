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

package org.eclipse.winery.model.adaptation.substitution.refinement.placeholder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.winery.model.adaptation.substitution.AbstractSubstitution;
import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementChooser;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaPropertyMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.jgrapht.GraphMapping;

public class PlaceholderSubstitution extends AbstractSubstitution {

    private ServiceTemplateId serviceTemplateId;
    private TTopologyTemplate topologyTemplate;
    private RefinementChooser refinementChooser;
    private String versionAppendix;
    private TTopologyTemplate subgraphDetector;

    public PlaceholderSubstitution(ServiceTemplateId serviceTemplateId, TTopologyTemplate subgraphDetector, RefinementChooser refinementChooser, String versionAppendix) {
        this.serviceTemplateId = serviceTemplateId;
        this.refinementChooser = refinementChooser;
        this.versionAppendix = versionAppendix;
        this.subgraphDetector = subgraphDetector;
        this.topologyTemplate = RepositoryFactory.getRepository().getElement(serviceTemplateId).getTopologyTemplate();
    }

    private boolean isApplicable(PlaceholderSubstitutionCandidate candidate) {
        
        return false;
    }

    public Map<String, String> applyRefinement(RefinementCandidate refinement, TTopologyTemplate topology) {
        return null;
    }

    public void substitutePlaceholders() {
        ToscaIsomorphismMatcher isomorphismMatcher = new ToscaIsomorphismMatcher();
        int[] id = new int[1];

        while (true) {
            //Detector is the subgraph of application-specific components of the origin topology
            ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(subgraphDetector);

            List<TServiceTemplate> serviceTemplateCandidates = getServiceTemplateCandidates();
            List<PlaceholderSubstitutionCandidate> matchingCandidates = new ArrayList<>();
            serviceTemplateCandidates.forEach(st -> {
                ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(st.getTopologyTemplate());
                IToscaMatcher matcher = new ToscaPropertyMatcher();
                Iterator<GraphMapping<ToscaNode, ToscaEdge>> matches = isomorphismMatcher.findMatches(detectorGraph, topologyGraph, matcher);

                matches.forEachRemaining(mapping -> {
                    PlaceholderSubstitutionCandidate candidate = new PlaceholderSubstitutionCandidate(st, mapping, detectorGraph, id[0]++);
                    if (isApplicable(candidate)) {
                        matchingCandidates.add(candidate);
                    }
                });
            });

            if (matchingCandidates.size() == 0) {
                break;
            }

            //TODO: Implement Refinement Chooser
            //RefinementCandidate refinement = this.refinementChooser.chooseRefinement(serviceTemplateCandidates, this.refinementServiceTemplateId, topology);

            //if (Objects.isNull(refinement)) {
            //    break;
            //}

            //applyRefinement(refinement, topology);
        }
    }

    private List<TServiceTemplate> getServiceTemplateCandidates() {
        return RepositoryFactory.getRepository().getAllDefinitionsChildIds(ServiceTemplateId.class)
            .stream()
            .filter(id -> !id.equals(this.serviceTemplateId))
            .map(id -> RepositoryFactory.getRepository().getElement(id))
            .collect(Collectors.toList());
    }

    private TNodeTemplate getPlaceholder(TNodeTemplate nodeTemplate) throws Exception {

        List<TNodeTemplate> hostedOnSuccessors = ModelUtilities.getHostedOnSuccessors(topologyTemplate, nodeTemplate.getId());
        if (hostedOnSuccessors.size() == 1) {
            if (hostedOnSuccessors.get(0).getType().getNamespaceURI().equals("http://opentosca/multiparticipant/placeholdertypes")) {
                return hostedOnSuccessors.get(0);
            } else {
                return getPlaceholder(hostedOnSuccessors.get(0));
            }
        } else {
            throw new Exception("No Placeholder Component detected.");
        }
    }

    private HostingStackCharacteristics getHostingStackCharacteristics(TTopologyTemplate topologyTemplate, TNodeTemplate nodeTemplate) {
        HostingStackCharacteristics hostingStackCharacteristics = new HostingStackCharacteristics(nodeTemplate);
        List<TNodeTemplate> hostedOnSuccessors = ModelUtilities.getHostedOnSuccessors(topologyTemplate, nodeTemplate);
        TNodeTemplate successor;
        while (!hostedOnSuccessors.isEmpty()) {
            successor = hostedOnSuccessors.get(0);
            LinkedHashMap<String, String> propertiesKV = ModelUtilities.getPropertiesKV(successor);
            if (!propertiesKV.isEmpty()) {
                hostingStackCharacteristics.addKVPropertyToProperties(propertiesKV.keySet());
            }
            if (!successor.getCapabilities().isEmpty()) {
                List<TCapability> capabilities = successor.getCapabilities();
                for (TCapability capability : capabilities) {
                    hostingStackCharacteristics.addCapability(capability.getType());
                }
            }
            hostedOnSuccessors = ModelUtilities.getHostedOnSuccessors(topologyTemplate, successor);
        }
        return hostingStackCharacteristics;
    }
}

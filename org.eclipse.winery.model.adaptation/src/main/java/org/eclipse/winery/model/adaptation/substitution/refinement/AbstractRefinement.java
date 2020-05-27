/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.substitution.refinement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.winery.model.ids.extensions.RefinementId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.adaptation.substitution.AbstractSubstitution;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.jgrapht.GraphMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRefinement extends AbstractSubstitution {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSubstitution.class);

    protected ServiceTemplateId refinementServiceTemplateId;

    private final List<OTRefinementModel> refinementModels;
    private final RefinementChooser refinementChooser;

    public AbstractRefinement(RefinementChooser refinementChooser, Class<? extends RefinementId> idClass, String versionAppendix) {
        this.refinementChooser = refinementChooser;
        this.versionAppendix = versionAppendix;
        this.refinementModels = this.repository.getAllDefinitionsChildIds(idClass)
            .stream()
            .map(repository::getElement)
            .collect(Collectors.toList());
    }

    public ServiceTemplateId refineServiceTemplate(ServiceTemplateId id) {
        refinementServiceTemplateId = this.getSubstitutionServiceTemplateId(id);
        TServiceTemplate element = this.repository.getElement(refinementServiceTemplateId);

        this.refineTopology(element.getTopologyTemplate());
        try {
            this.repository.setElement(refinementServiceTemplateId, element);
        } catch (IOException e) {
            LOGGER.error("Error while saving refined topology", e);
        }

        return refinementServiceTemplateId;
    }

    public void refineTopology(TTopologyTemplate topology) {
        ToscaIsomorphismMatcher isomorphismMatcher = new ToscaIsomorphismMatcher();
        int[] id = new int[1];

        while (getLoopCondition(topology)) {
            ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topology);

            List<RefinementCandidate> candidates = new ArrayList<>();
            this.refinementModels
                .forEach(prm -> {
                    ToscaGraph detectorGraph = ToscaTransformer.createTOSCAGraph(prm.getDetector());
                    IToscaMatcher matcher = getMatcher(prm);
                    Iterator<GraphMapping<ToscaNode, ToscaEdge>> matches = isomorphismMatcher.findMatches(detectorGraph, topologyGraph, matcher);

                    matches.forEachRemaining(mapping -> {
                        RefinementCandidate candidate = new RefinementCandidate(prm, mapping, detectorGraph, id[0]++);

                        if (isApplicable(candidate, topology)) {
                            candidates.add(candidate);
                        }
                    });
                });

            if (candidates.size() == 0) {
                break;
            }

            RefinementCandidate refinement = this.refinementChooser.chooseRefinement(candidates, this.refinementServiceTemplateId, topology);

            if (Objects.isNull(refinement)) {
                break;
            }

            applyRefinement(refinement, topology);
        }
    }

    public abstract boolean getLoopCondition(TTopologyTemplate topology);

    public abstract IToscaMatcher getMatcher(OTRefinementModel prm);

    public abstract boolean isApplicable(RefinementCandidate candidate, TTopologyTemplate topology);

    public abstract void applyRefinement(RefinementCandidate refinement, TTopologyTemplate topology);
}

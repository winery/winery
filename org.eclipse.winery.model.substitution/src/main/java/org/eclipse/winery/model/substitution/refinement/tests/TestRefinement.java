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

package org.eclipse.winery.model.substitution.refinement.tests;

import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.common.ids.definitions.TestRefinementModelId;
import org.eclipse.winery.model.substitution.refinement.AbstractRefinement;
import org.eclipse.winery.model.substitution.refinement.DefaultRefinementChooser;
import org.eclipse.winery.model.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.substitution.refinement.RefinementChooser;
import org.eclipse.winery.model.tosca.TRefinementModel;
import org.eclipse.winery.model.tosca.TRelationDirection;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.topologygraph.matching.IToscaMatcher;
import org.eclipse.winery.topologygraph.matching.ToscaTypeMatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRefinement extends AbstractRefinement {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRefinement.class);

    public TestRefinement(RefinementChooser refinementChooser) {
        super(refinementChooser, TestRefinementModelId.class, "mimicTest");
    }

    public TestRefinement() {
        this(new DefaultRefinementChooser());
    }

    @Override
    public boolean isApplicable(RefinementCandidate candidate, TTopologyTemplate topology) {
        // since we already meet all requirements during the sub-graph matching, this is always true
        return true;
    }

    @Override
    public void applyRefinement(RefinementCandidate refinement, TTopologyTemplate topology) {
        // import the refinement structure
        Map<String, String> idMapping = BackendUtils.mergeTopologyTemplateAinTopologyTemplateB(
            refinement.getRefinementModel().getRefinementTopology(),
            topology
        );

        // iterate over the refinement nodes and add the configured relations
        refinement.getRefinementModel().getRefinementTopology().getNodeTemplates()
            .forEach(nodeTemplate ->
                refinement.getRefinementModel().getRelationMappings().getRelationMapping().stream()
                    .filter(relationMapping -> relationMapping.getRefinementNode().getId().equals(nodeTemplate.getId()))
                    .forEach(relationMapping -> {
                        String relId = this.versionAppendix + "-" + relationMapping.getRelationType().getLocalPart();
                        int counter = 0;
                        while (Objects.nonNull(idMapping.get(relId))) {
                            relId += counter++;
                        }
                        TRelationshipTemplate relationshipTemplate = new TRelationshipTemplate();
                        relationshipTemplate.setType(relationMapping.getRelationType());
                        relationshipTemplate.setId(relId);

                        if (relationMapping.getDirection() == TRelationDirection.INGOING) {
                            String sourceId = idMapping.get(relationMapping.getDetectorNode().getId());
                            String targetId = idMapping.get(relationMapping.getRefinementNode().getId());
                            relationshipTemplate.setSourceNodeTemplate(topology.getNodeTemplate(sourceId));
                            relationshipTemplate.setTargetNodeTemplate(topology.getNodeTemplate(targetId));
                        } else {
                            String sourceId = idMapping.get(relationMapping.getRefinementNode().getId());
                            String targetId = idMapping.get(relationMapping.getDetectorNode().getId());
                            relationshipTemplate.setSourceNodeTemplate(topology.getNodeTemplate(sourceId));
                            relationshipTemplate.setTargetNodeTemplate(topology.getNodeTemplate(targetId));
                        }

                        topology.addRelationshipTemplate(relationshipTemplate);
                    })
            );
    }

    @Override
    public IToscaMatcher getMatcher(TRefinementModel prm) {
        return new ToscaTypeMatcher();
    }
}

/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologygraph.matching.patterndetection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMapping;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTPrmMapping;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;
import org.eclipse.winery.repository.targetallocation.util.AllocationUtils;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaEntity;
import org.eclipse.winery.topologygraph.model.ToscaNode;

import io.github.adr.embedded.ADR;
import org.jgrapht.GraphMapping;

public class PatternDetectionUtils {

    /**
     * To reuse the pattern refinement implementation for the pattern detection process, the detector is swapped with
     * the refinement.
     */
    @ADR(31)
    public static OTPatternRefinementModel swapDetectorWithRefinement(OTPatternRefinementModel prm) {
        TTopologyTemplate detector = prm.getDetector();
        prm.setDetector(prm.getRefinementTopology());
        prm.setRefinementTopology(detector);

        Stream.of(
            prm.getRelationMappings() == null ? Stream.empty() : prm.getRelationMappings().stream(),
            prm.getPermutationMappings() == null ? Stream.empty() : prm.getPermutationMappings().stream(),
            prm.getAttributeMappings() == null ? Stream.empty() : prm.getAttributeMappings().stream(),
            prm.getStayMappings() == null ? Stream.empty() : prm.getStayMappings().stream(),
            prm.getDeploymentArtifactMappings() == null ? Stream.empty() : prm.getDeploymentArtifactMappings().stream(),
            prm.getBehaviorPatternMappings() == null ? Stream.empty() : prm.getBehaviorPatternMappings().stream()
        ).flatMap(Function.identity())
            .map(OTPrmMapping.class::cast)
            .forEach(mapping -> {
                if (mapping instanceof OTAttributeMapping) {
                    OTAttributeMapping attributeMapping = (OTAttributeMapping) mapping;
                    swapDetectorWithRefinement(attributeMapping);
                } else {
                    swapDetectorWithRefinement(mapping);
                }
            });
        return prm;
    }

    public static OTPrmMapping swapDetectorWithRefinement(OTPrmMapping mapping) {
        TEntityTemplate detectorElement = mapping.getDetectorElement();
        mapping.setDetectorElement(mapping.getRefinementElement());
        mapping.setRefinementElement(detectorElement);
        return mapping;
    }

    public static OTAttributeMapping swapDetectorWithRefinement(OTAttributeMapping attributeMapping) {
        swapDetectorWithRefinement((OTPrmMapping) attributeMapping);
        String detectorProp = attributeMapping.getDetectorProperty();
        attributeMapping.setDetectorProperty(attributeMapping.getRefinementProperty());
        attributeMapping.setRefinementProperty(detectorProp);
        return attributeMapping;
    }

    public static TEntityTemplate getEntityCorrespondence(ToscaEntity entity, GraphMapping<ToscaNode, ToscaEdge> graphMapping) {
        if (entity instanceof ToscaNode) {
            return graphMapping.getVertexCorrespondence((ToscaNode) entity, false).getTemplate();
        } else {
            return graphMapping.getEdgeCorrespondence((ToscaEdge) entity, false).getTemplate();
        }
    }

    public static void toPdrms(List<OTRefinementModel> refinementModels) {
        refinementModels.removeIf(prm -> !(prm instanceof OTPatternRefinementModel)
            || !((OTPatternRefinementModel) prm).isPdrm());
        // for pattern detection the detector is the "refinement" and the refinement is the detector
        refinementModels.stream()
            .map(prm -> (OTPatternRefinementModel) prm)
            .forEach(PatternDetectionUtils::swapDetectorWithRefinement);
    }

    /**
     * This does not create a complete clone.
     */
    public static OTPatternRefinementModel clone(OTPatternRefinementModel other) {
        return new OTPatternRefinementModel(new OTPatternRefinementModel.Builder()
            .setName(other.getName())
            .setTargetNamespace(other.getTargetNamespace())
            .setDetector(other.getDetector())
            .setRelationMappings(cloneRelationMappings(other.getRelationMappings()))
            .setPermutationMappings(other.getPermutationMappings())
            .setRefinementStructure(AllocationUtils.deepcopy(other.getRefinementStructure(), false))
            .setAttributeMappings(cloneAttributeMappings(other.getAttributeMappings()))
            .setStayMappings(other.getStayMappings())
            .setDeploymentArtifactMappings(other.getDeploymentArtifactMappings())
            .setPermutationOptions(other.getPermutationOptions())
            .setComponentSets(other.getComponentSets())
            .setBehaviorPatternMappings(other.getBehaviorPatternMappings())
            .setIsPdrm(other.isPdrm())
        );
    }

    private static List<OTRelationMapping> cloneRelationMappings(List<OTRelationMapping> relationMappings) {
        if (relationMappings != null) {
            return relationMappings.stream()
                .map(mapping -> new OTRelationMapping(new OTRelationMapping.Builder()
                    .setDetectorElement(mapping.getDetectorElement())
                    .setRefinementElement(mapping.getRefinementElement())
                    .setDirection(mapping.getDirection())
                    .setRelationType(mapping.getRelationType())
                    .setValidSourceOrTarget(mapping.getValidSourceOrTarget())
                )).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private static List<OTAttributeMapping> cloneAttributeMappings(List<OTAttributeMapping> attributeMappings) {
        if (attributeMappings != null) {
            return new ArrayList<>(attributeMappings);
        }
        return new ArrayList<>();
    }
}

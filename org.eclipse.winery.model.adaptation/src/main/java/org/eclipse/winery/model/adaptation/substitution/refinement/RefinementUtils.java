/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.extensions.OTPrmMapping;
import org.eclipse.winery.model.tosca.extensions.OTRelationDirection;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.model.tosca.extensions.OTPermutationMapping;
import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;

public abstract class RefinementUtils {

    public static boolean isStayingRefinementElement(TEntityTemplate element, OTTopologyFragmentRefinementModel prm) {
        return getStayingRefinementElements(prm).stream()
            .anyMatch(stayingElement -> stayingElement.getId().equals(element.getId()));
    }

    public static List<TEntityTemplate> getStayingRefinementElements(OTTopologyFragmentRefinementModel prm) {
        return prm.getStayMappings() == null ? new ArrayList<>() :
            prm.getStayMappings().stream()
                .map(OTPrmMapping::getRefinementElement)
                .collect(Collectors.toList());
    }

    public static boolean isStayPlaceholder(TEntityTemplate element, OTTopologyFragmentRefinementModel prm) {
        return getStayPlaceholders(prm).stream()
            .anyMatch(stayingElement -> stayingElement.getId().equals(element.getId()));
    }

    public static List<TEntityTemplate> getStayPlaceholders(OTTopologyFragmentRefinementModel prm) {
        return prm.getStayMappings() == null ? new ArrayList<>() :
            prm.getStayMappings().stream()
                .map(OTPrmMapping::getDetectorElement)
                .collect(Collectors.toList());
    }

    public static boolean permutabilityMappingExistsForDetectorElement(TEntityTemplate entityTemplate, OTTopologyFragmentRefinementModel prm) {
        return prm.getPermutationMappings() != null &&
            prm.getPermutationMappings().stream()
                .anyMatch(permutationMap -> permutationMap.getDetectorElement().equals(entityTemplate));
    }

    public static boolean permutabilityMappingExistsForRefinementNode(TEntityTemplate entityTemplate, OTTopologyFragmentRefinementModel prm) {
        return prm.getPermutationMappings() != null &&
            prm.getPermutationMappings().stream()
                .anyMatch(permutationMap -> permutationMap.getRefinementElement().equals(entityTemplate));
    }

    public static boolean noMappingExistsForRefinementNodeExeptForGivenDetectorNode(TNodeTemplate detectorNode,
                                                                                    TEntityTemplate refinementNode,
                                                                                    OTTopologyFragmentRefinementModel refinementModel) {
        return !isStayPlaceholder(detectorNode, refinementModel) &&
            !permutabilityMappingExistsForRefinementNode(refinementNode, refinementModel) &&
            getAllContentMappingsForRefinementNodeWithoutDetectorNode(detectorNode, refinementNode, refinementModel).size() == 0;
    }

    public static boolean stayMappingExistsForRefinementNode(TEntityTemplate entityTemplate, OTTopologyFragmentRefinementModel prm) {
        return prm.getStayMappings() != null &&
            prm.getStayMappings().stream()
                .anyMatch(permutationMap -> permutationMap.getRefinementElement().equals(entityTemplate));
    }

    public static void addMutabilityMapping(TEntityTemplate detectorNode, TEntityTemplate refinementNode,
                                            OTTopologyFragmentRefinementModel prm) {
        if (prm.getPermutationMappings() == null || prm.getPermutationMappings().stream()
            .noneMatch(map -> map.getDetectorElement().getId().equals(detectorNode.getId())
                && map.getRefinementElement().getId().equals(refinementNode.getId()))) {
            prm.setPermutationMappings(
                addMapping(detectorNode, refinementNode, new OTPermutationMapping(new OTPermutationMapping.Builder()), prm.getPermutationMappings())
            );
        }
    }

    public static void addStayMapping(TNodeTemplate detectorNode, TEntityTemplate refinementNode,
                                      OTTopologyFragmentRefinementModel prm) {
        prm.setStayMappings(
            addMapping(detectorNode, refinementNode, new OTStayMapping(new OTStayMapping.Builder()), prm.getStayMappings())
        );
    }

    public static void addRelationMapping(TNodeTemplate detectorNode, TEntityTemplate refinementNode,
                                          QName relationType, OTRelationDirection direction, QName validSourOrTarget,
                                          OTTopologyFragmentRefinementModel prm) {
        OTRelationMapping mapping = new OTRelationMapping(new OTRelationMapping.Builder());
        prm.setRelationMappings(
            addMapping(detectorNode, refinementNode, mapping, prm.getRelationMappings())
        );

        mapping.setRelationType(relationType);
        mapping.setDirection(direction);
        mapping.setValidSourceOrTarget(validSourOrTarget);
    }

    public static void addDeploymentArtifactMapping(TNodeTemplate detectorNode, TEntityTemplate refinementNode,
                                                    QName artifactType, OTTopologyFragmentRefinementModel prm) {
        OTDeploymentArtifactMapping mapping = new OTDeploymentArtifactMapping(new OTDeploymentArtifactMapping.Builder());
        prm.setDeploymentArtifactMappings(
            addMapping(detectorNode, refinementNode, mapping, prm.getDeploymentArtifactMappings())
        );

        mapping.setArtifactType(artifactType);
    }

    private static <T extends OTPrmMapping> List<T> addMapping(TEntityTemplate detectorNode, TEntityTemplate refinementNode,
                                                               T mapping, List<T> mappings) {
        if (mappings == null) {
            mappings = new ArrayList<>();
        }

        mapping.setDetectorElement(detectorNode);
        mapping.setRefinementElement(refinementNode);
        mapping.setId(UUID.randomUUID().toString());
        mappings.add(mapping);

        return mappings;
    }

    static <T extends OTPrmMapping> List<T> getMappingsForDetectorNode(TNodeTemplate detectorNode,
                                                                       List<T> mappings) {
        return mappings == null ? new ArrayList<>() :
            mappings.stream()
                .filter(mapping -> mapping.getDetectorElement().equals(detectorNode))
                .collect(Collectors.toList());
    }

    public static List<OTPrmMapping> getAllMappingsForDetectorNode(TNodeTemplate detectorNode,
                                                                   OTTopologyFragmentRefinementModel refinementModel) {
        ArrayList<OTPrmMapping> mappings = new ArrayList<>();
        mappings.addAll(getMappingsForDetectorNode(detectorNode, refinementModel.getRelationMappings()));
        mappings.addAll(getMappingsForDetectorNode(detectorNode, refinementModel.getAttributeMappings()));
        mappings.addAll(getMappingsForDetectorNode(detectorNode, refinementModel.getDeploymentArtifactMappings()));
        mappings.addAll(getMappingsForDetectorNode(detectorNode, refinementModel.getStayMappings()));
        mappings.addAll(getMappingsForDetectorNode(detectorNode, refinementModel.getPermutationMappings()));
        return mappings;
    }

    static <T extends OTPrmMapping> List<T> getMappingsForRefinementNodeButNotFromDetectorNode(TNodeTemplate detectorNode,
                                                                                               TEntityTemplate refinementNode,
                                                                                               List<T> mappings) {
        return mappings == null ? new ArrayList<>() :
            mappings.stream()
                .filter(mapping -> refinementNode == null || mapping.getRefinementElement().getId().equals(refinementNode.getId()))
                .filter(mapping -> detectorNode == null || !mapping.getDetectorElement().getId().equals(detectorNode.getId()))
                .collect(Collectors.toList());
    }

    public static List<OTPrmMapping> getAllMappingsForRefinementNodeWithoutDetectorNode(TNodeTemplate detectorNode,
                                                                                        TEntityTemplate refinementNode,
                                                                                        OTTopologyFragmentRefinementModel refinementModel) {
        ArrayList<OTPrmMapping> mappings = new ArrayList<>();
        mappings.addAll(getMappingsForRefinementNodeButNotFromDetectorNode(detectorNode, refinementNode, refinementModel.getRelationMappings()));
        mappings.addAll(getMappingsForRefinementNodeButNotFromDetectorNode(detectorNode, refinementNode, refinementModel.getAttributeMappings()));
        mappings.addAll(getMappingsForRefinementNodeButNotFromDetectorNode(detectorNode, refinementNode, refinementModel.getDeploymentArtifactMappings()));
        mappings.addAll(getMappingsForRefinementNodeButNotFromDetectorNode(detectorNode, refinementNode, refinementModel.getStayMappings()));
        mappings.addAll(getMappingsForRefinementNodeButNotFromDetectorNode(detectorNode, refinementNode, refinementModel.getPermutationMappings()));
        return mappings;
    }

    public static List<OTPrmMapping> getAllMappingsForRefinementNode(TEntityTemplate refinementNode,
                                                                     OTTopologyFragmentRefinementModel refinementModel) {
        return getAllMappingsForRefinementNodeWithoutDetectorNode(null, refinementNode, refinementModel);
    }

    public static List<OTPrmMapping> getAllContentMappingsForRefinementNodeWithoutDetectorNode(TNodeTemplate detectorNode,
                                                                                               TEntityTemplate refinementNode,
                                                                                               OTTopologyFragmentRefinementModel refinementModel) {
        ArrayList<OTPrmMapping> mappings = new ArrayList<>();
        mappings.addAll(getMappingsForRefinementNodeButNotFromDetectorNode(detectorNode, refinementNode, refinementModel.getRelationMappings()));
        mappings.addAll(getMappingsForRefinementNodeButNotFromDetectorNode(detectorNode, refinementNode, refinementModel.getAttributeMappings()));
        mappings.addAll(getMappingsForRefinementNodeButNotFromDetectorNode(detectorNode, refinementNode, refinementModel.getDeploymentArtifactMappings()));
        return mappings;
    }

    public static List<OTPrmMapping> getStayAndPermutationMappings(OTTopologyFragmentRefinementModel refinementModel) {
        ArrayList<OTPrmMapping> mappings = new ArrayList<>();
        mappings.addAll(getMappingsForRefinementNodeButNotFromDetectorNode(null, null, refinementModel.getStayMappings()));
        mappings.addAll(getMappingsForRefinementNodeButNotFromDetectorNode(null, null, refinementModel.getPermutationMappings()));
        return mappings;
    }

    public static List<OTPrmMapping> getAllContentMappingsForRefinementNode(TEntityTemplate refinementNode,
                                                                            OTTopologyFragmentRefinementModel refinementModel) {
        return getAllContentMappingsForRefinementNodeWithoutDetectorNode(null, refinementNode, refinementModel);
    }

    public static boolean canRedirectRelation(OTRelationMapping relationMapping,
                                              TRelationshipTemplate relationship,
                                              Map<QName, TRelationshipType> relationshipTypes,
                                              Map<QName, TNodeType> nodeTypes) {
        return redirectRelation(relationMapping, relationship, null, null, relationshipTypes, nodeTypes);
    }

    public static boolean redirectRelation(OTRelationMapping relationMapping,
                                           TRelationshipTemplate relationship,
                                           TTopologyTemplate topology,
                                           Map<String, String> idMapping,
                                           Map<QName, TRelationshipType> relationshipTypes,
                                           Map<QName, TNodeType> nodeTypes) {
        if (ModelUtilities.isOfType(relationMapping.getRelationType(), relationship.getType(), relationshipTypes)) {
            if (relationMapping.getDirection() == OTRelationDirection.INGOING
                && (Objects.isNull(relationMapping.getValidSourceOrTarget())
                || ModelUtilities.isOfType(relationMapping.getValidSourceOrTarget(), relationship.getSourceElement().getRef().getType(), nodeTypes))
            ) {
                // change the source element to the new source defined in the relation mapping
                if (Objects.nonNull(idMapping)) {
                    String id = idMapping.get(relationMapping.getRefinementElement().getId());
                    relationship.setTargetNodeTemplate(topology.getNodeTemplate(id));
                }
                return true;
            } else if (Objects.isNull(relationMapping.getValidSourceOrTarget())
                || ModelUtilities.isOfType(relationMapping.getValidSourceOrTarget(), relationship.getTargetElement().getRef().getType(), nodeTypes)) {
                if (Objects.nonNull(idMapping)) {
                    String id = idMapping.get(relationMapping.getRefinementElement().getId());
                    relationship.setSourceNodeTemplate(topology.getNodeTemplate(id));
                }
                return true;
            }
        }
        return false;
    }
}

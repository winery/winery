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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.extensions.PatternRefinementModelId;
import org.eclipse.winery.model.ids.extensions.TopologyFragmentRefinementModelId;
import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTPermutationMapping;
import org.eclipse.winery.model.tosca.extensions.OTPrmMapping;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.model.tosca.extensions.OTStringList;
import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.model.version.VersionSupport;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.collections.impl.factory.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.model.adaptation.substitution.refinement.RefinementUtils.addMutabilityMapping;
import static org.eclipse.winery.model.adaptation.substitution.refinement.RefinementUtils.getAllContentMappingsForRefinementNodeWithoutDetectorNode;
import static org.eclipse.winery.model.adaptation.substitution.refinement.RefinementUtils.getAllMappingsForDetectorNode;
import static org.eclipse.winery.model.adaptation.substitution.refinement.RefinementUtils.getStayAndPermutationMappings;
import static org.eclipse.winery.model.adaptation.substitution.refinement.RefinementUtils.isStayPlaceholder;
import static org.eclipse.winery.model.adaptation.substitution.refinement.RefinementUtils.isStayingRefinementElement;
import static org.eclipse.winery.model.adaptation.substitution.refinement.RefinementUtils.noMappingExistsForRefinementNodeExeptForGivenDetectorNode;
import static org.eclipse.winery.model.adaptation.substitution.refinement.RefinementUtils.permutabilityMappingExistsForDetectorElement;
import static org.eclipse.winery.model.adaptation.substitution.refinement.RefinementUtils.permutabilityMappingExistsForRefinementNode;

public class PermutationGenerator {

    private static final Logger logger = LoggerFactory.getLogger(PermutationGenerator.class);

    protected final Map<QName, TRelationshipType> relationshipTypes = new HashMap<>();
    protected final Map<QName, TNodeType> nodeTypes = new HashMap<>();

    protected final String errorMessage = "Permutations cannot be determined automatically! Reason: {}.";
    protected String mutabilityErrorReason = "";

    public PermutationGenerator() {
        this.relationshipTypes.putAll(RepositoryFactory.getRepository().getQNameToElementMapping(RelationshipTypeId.class));
        this.nodeTypes.putAll(RepositoryFactory.getRepository().getQNameToElementMapping(NodeTypeId.class));
    }

    public boolean checkMutability(OTTopologyFragmentRefinementModel refinementModel) {
        logger.info("Starting mutability check of {}", refinementModel.getIdFromIdOrNameField());
        this.mutabilityErrorReason = "";

        List<TNodeTemplate> detectorNodeTemplates = refinementModel.getDetector().getNodeTemplates();
        Set<TNodeTemplate> mutableNodes = detectorNodeTemplates.stream()
            .filter(nodeTemplate -> !isStayPlaceholder(nodeTemplate, refinementModel))
            .collect(Collectors.toSet());

        List<OTStringList> permutationOptions = new ArrayList<>();
        refinementModel.setPermutationOptions(permutationOptions);
        Sets.powerSet(mutableNodes).stream()
            .filter(set -> !(
                set.size() == 0 || set.size() == mutableNodes.size()
            )).forEach(permutation -> permutationOptions.add(
            new OTStringList(
                permutation.stream()
                    .map(HasId::getId)
                    .collect(Collectors.toList())
            )
        ));

        refinementModel.setComponentSets(new ArrayList<>());

        for (TNodeTemplate detectorNode : detectorNodeTemplates) {
            getAllMappingsForDetectorNode(detectorNode, refinementModel).stream()
                .filter(mapping -> mapping.getRefinementElement() instanceof TNodeTemplate)
                .map(mapping -> (TNodeTemplate) mapping.getRefinementElement())
                .forEach(refinementNode ->
                    this.checkComponentMutability(refinementNode, detectorNode, refinementModel)
                );

            ModelUtilities.getIncomingRelationshipTemplates(refinementModel.getDetector(), detectorNode)
                .stream()
                .filter(relation -> refinementModel.getComponentSets().stream()
                    .noneMatch(setList -> setList.getValues().contains(relation.getTargetElement().getRef().getId())
                        && setList.getValues().contains(relation.getSourceElement().getRef().getId()))
                )
                .forEach(relation -> {
                    TNodeTemplate dependantNode = (TNodeTemplate) relation.getSourceElement().getRef();
                    if (refinementModel.getRelationMappings() != null) {
                        refinementModel.getRelationMappings().stream()
                            .filter(relMap -> relMap.getDetectorElement().equals(dependantNode))
                            .filter(relMap -> RefinementUtils.canRedirectRelation(relMap, relation, this.relationshipTypes, this.nodeTypes))
                            .findFirst()
                            .ifPresent(relMap ->
                                addMutabilityMapping(relMap.getDetectorElement(), relMap.getRefinementElement(), refinementModel)
                            );
                    }
                });
        }

        if (refinementModel.getPermutationMappings() == null) {
            this.mutabilityErrorReason = "No permutation mappings could be identified";
            logger.info(this.errorMessage, this.mutabilityErrorReason);
            return false;
        }

        List<String> unmappedDetectorNodes = refinementModel.getDetector().getNodeTemplates().stream()
            .filter(detectorNode -> !isStayPlaceholder(detectorNode, refinementModel))
            .filter(detectorNode -> !permutabilityMappingExistsForDetectorElement(detectorNode, refinementModel))
            .map(HasId::getId)
            .collect(Collectors.toList());

        if (unmappedDetectorNodes.size() > 0) {
            this.mutabilityErrorReason = "There are detector nodes which could not be mapped to a refinement node: "
                + String.join(", ", unmappedDetectorNodes);
            logger.info(this.errorMessage, this.mutabilityErrorReason);
            return false;
        }

        List<String> unmappedRefinementNodes = refinementModel.getRefinementStructure().getNodeTemplates().stream()
            .filter(refinementNode -> !isStayingRefinementElement(refinementNode, refinementModel))
            .filter(refinementNode -> !permutabilityMappingExistsForRefinementNode(refinementNode, refinementModel))
            .map(HasId::getId)
            .collect(Collectors.toList());

        if (unmappedRefinementNodes.size() > 0) {
            this.mutabilityErrorReason = "There are refinement nodes which could not be mapped to a detector node: "
                + String.join(", ", unmappedRefinementNodes);
            logger.info(this.errorMessage, this.mutabilityErrorReason);
            return false;
        }

        List<String> unmappableRelationIds = chekMutabilityOfDetectorRelations(refinementModel);

        if (unmappableRelationIds.size() > 0) {
            this.mutabilityErrorReason = "There are relations that cannot be redirected during the generation: "
                + String.join(", ", unmappableRelationIds);
            logger.info(this.errorMessage, this.mutabilityErrorReason);
            return false;
        }

        return true;
    }

    private void checkComponentMutability(TNodeTemplate refinementNode,
                                          TNodeTemplate detectorNode,
                                          OTTopologyFragmentRefinementModel refinementModel) {
        logger.info("Checking component mutability of detectorNode \"{}\" to refinementNode \"{}\"",
            detectorNode.getId(), refinementNode.getId());

        List<OTPrmMapping> mappingsWithoutDetectorNode =
            getAllContentMappingsForRefinementNodeWithoutDetectorNode(detectorNode, refinementNode, refinementModel);

        if (noMappingExistsForRefinementNodeExeptForGivenDetectorNode(detectorNode, refinementNode, refinementModel)) {
            logger.info("Adding MutabilityMapping between detector Node \"{}\" and refinement node \"{}\"",
                detectorNode.getId(), refinementNode.getId());
            addMutabilityMapping(detectorNode, refinementNode, refinementModel);
        } else if (mappingsWithoutDetectorNode.size() > 0) {
            // Determine the set of pattern which must be refined together as they define overlapping mappings
            ArrayList<String> patternSet = new ArrayList<>();
            patternSet.add(detectorNode.getId());

            mappingsWithoutDetectorNode.stream()
                .map(OTPrmMapping::getDetectorElement)
                .forEach(node -> patternSet.add(node.getId()));

            logger.info("Found pattern set of components: {}", String.join(",", patternSet));

            if (refinementModel.getComponentSets() == null) {
                refinementModel.setComponentSets(new ArrayList<>());
            }

            refinementModel.getPermutationOptions()
                .removeIf(permutationOption -> !(permutationOption.getValues().containsAll(patternSet)
                    || permutationOption.getValues().stream().noneMatch(patternSet::contains))
                );

            boolean added = false;
            for (OTStringList componentSet : refinementModel.getComponentSets()) {
                List<String> existingPatternSet = componentSet.getValues();
                if (existingPatternSet.stream().anyMatch(patternSet::contains)) {
                    added = true;
                    patternSet.forEach(id -> {
                        if (!existingPatternSet.contains(id)) {
                            existingPatternSet.add(id);
                        }
                    });
                    logger.info("Added pattern set to existing set: {}",
                        String.join(",", existingPatternSet));
                    break;
                }
            }

            if (!added) {
                refinementModel.getComponentSets().add(new OTStringList(patternSet));
            }
        }

        // Only check dependee if the current component can be mapped clearly to the current detector node
        if (mappingsWithoutDetectorNode.size() == 0) {
            // For all nodes the current refinement node is dependent on and which do not have any other dependants
            // or maps from different detector nodes, check their mutability.
            ModelUtilities.getOutgoingRelationshipTemplates(refinementModel.getRefinementTopology(), refinementNode)
                .stream()
                .map(element -> (TNodeTemplate) element.getTargetElement().getRef())
                .filter(dependee -> noMappingExistsForRefinementNodeExeptForGivenDetectorNode(detectorNode, dependee, refinementModel))
                .filter(dependee -> {
                        List<TRelationshipTemplate> incomingRelations = ModelUtilities.getIncomingRelationshipTemplates(
                            refinementModel.getRefinementTopology(), dependee)
                            .stream()
                            .filter(relation -> !relation.getSourceElement().getRef().getId().equals(refinementNode.getId()))
                            .collect(Collectors.toList());
                        return incomingRelations.isEmpty() || incomingRelations.stream()
                            .map(relationship -> (TNodeTemplate) relationship.getSourceElement().getRef())
                            .anyMatch(source -> noMappingExistsForRefinementNodeExeptForGivenDetectorNode(detectorNode, source, refinementModel));
                    }
                ).forEach(dependee -> this.checkComponentMutability(dependee, detectorNode, refinementModel));
        }
    }

    private List<String> chekMutabilityOfDetectorRelations(OTTopologyFragmentRefinementModel refinementModel) {
        List<String> unmappableRelationIds = new ArrayList<>();
        // If there are incoming relations that cannot be redirected during the generation of the permutations,
        // try to generate permutation maps.
        for (TNodeTemplate detectorNode : refinementModel.getDetector().getNodeTemplates()) {
            List<TRelationshipTemplate> unMappableRelations =
                ModelUtilities.getIncomingRelationshipTemplates(refinementModel.getDetector(), detectorNode).stream()
                    .filter(relation -> !permutabilityMappingExistsForDetectorElement(relation, refinementModel))
                    .collect(Collectors.toList());

            for (TRelationshipTemplate unmappable : unMappableRelations) {
                logger.info("Checking unmapped relation \"{}\"", unmappable.getId());
                // If the relation exists between two components in a component set we ignore it, as there is no
                // case in which the relation has to be redirected while creating a permutation.
                boolean unmappableRelationExists = refinementModel.getComponentSets().stream()
                    .noneMatch(componentSet ->
                        componentSet.getValues().containsAll(Arrays.asList(
                            unmappable.getSourceElement().getRef().getId(),
                            unmappable.getTargetElement().getRef().getId())));

                if (unmappableRelationExists) {
                    // If there is only one permutation mapping sourcing from the current detector node,
                    // the relation can be redirected directly to the corresponding refinement node.
                    // If there are multiple permutations mappings sourcing from the relation's source detector node,
                    // do crazy shit: If the corresponding refinement nodes of the relation's source have relations
                    // that point to the same node that can be mapped to the current detector node (except for
                    // relations among each other), the relation can be redirected to this node.
                    TNodeTemplate source = (TNodeTemplate) unmappable.getSourceElement().getRef();
                    List<TNodeTemplate> nodesTheSourceRefinesTo = getStayAndPermutationMappings(refinementModel).stream()
                        .filter(pm -> pm.getDetectorElement().getId().equals(source.getId()))
                        .map(OTPrmMapping::getRefinementElement)
                        .filter(element -> element instanceof TNodeTemplate)
                        .map(element -> (TNodeTemplate) element)
                        .distinct()
                        .collect(Collectors.toList());

                    Set<TNodeTemplate> nodesTheRefinementNodesAreDependingOn = new HashSet<>();
                    nodesTheSourceRefinesTo.forEach(node -> nodesTheRefinementNodesAreDependingOn.addAll(
                        ModelUtilities.getOutgoingRelationshipTemplates(refinementModel.getRefinementStructure(), node).stream()
                            .map(outgoing -> outgoing.getTargetElement().getRef())
                            .filter(target -> target instanceof TNodeTemplate)
                            .map(target -> (TNodeTemplate) target)
                            // As we are trying to redirect the relation between the detectorNode and the source,
                            // we can filter the other refinement nodes that are corresponding to other detector nodes.
                            .filter(target -> refinementModel.getPermutationMappings().stream().anyMatch(pm ->
                                    pm.getRefinementElement().getId().equals(target.getId()) &&
                                        pm.getDetectorElement().getId().equals(detectorNode.getId())
                                )
                            ).collect(Collectors.toList())
                    ));
                    // If the current detectorNode refines to multiple refinement nodes and the nodesTheSourceRefinesTo
                    // are depending on multiple of these refinement nodes, we cannot determine which one should be the
                    // respective target of the relation automatically.
                    if (nodesTheRefinementNodesAreDependingOn.size() == 1) {
                        TNodeTemplate target = nodesTheRefinementNodesAreDependingOn.iterator().next();
                        logger.info("Found possibility to redirect relation \"{}\" to refinement node \"{}\"",
                            unmappable.getId(), target.getId());
                        addMutabilityMapping(unmappable, target, refinementModel);
                        unmappableRelationExists = false;
                    }
                } else {
                    logger.info("Relation \"{}\" is part of a component set", unmappable.getId());
                }

                if (unmappableRelationExists) {
                    unmappableRelationIds.add(unmappable.getId());
                }
            }
        }

        return unmappableRelationIds;
    }

    public Map<String, OTTopologyFragmentRefinementModel> generatePermutations(OTTopologyFragmentRefinementModel refinementModel) {
        Map<String, OTTopologyFragmentRefinementModel> permutations = new HashMap<>();
        IRepository repository = RepositoryFactory.getRepository();

        if (!checkMutability(refinementModel)) {
            throw new RuntimeException("The refinement model cannot be permuted!");
        }

        QName refinementModelQName = new QName(refinementModel.getTargetNamespace(), refinementModel.getName());
        DefinitionsChildId refinementModelId = new TopologyFragmentRefinementModelId(refinementModelQName);
        if (refinementModel instanceof OTPatternRefinementModel) {
            refinementModelId = new PatternRefinementModelId(refinementModelQName);
        }

        for (OTStringList options : refinementModel.getPermutationOptions()) {
            String permutationName = VersionSupport.getNewComponentVersionId(refinementModelId,
                "permutation-" + String.join("-", options.getValues()).replaceAll("_", "-"));
            QName permutationQName = new QName(refinementModel.getTargetNamespace(), permutationName);

            DefinitionsChildId permutationModelId = new TopologyFragmentRefinementModelId(permutationQName);
            if (refinementModel instanceof OTPatternRefinementModel) {
                permutationModelId = new PatternRefinementModelId(permutationQName);
            }

            try {
                // To ensure that the permutationMaps are duplicated correctly, save the permutation first
                repository.setElement(refinementModelId, refinementModel);
                repository.setElement(permutationModelId, refinementModel);
            } catch (IOException e) {
                logger.error("Error while creating permutation!", e);
                break;
            }

            OTTopologyFragmentRefinementModel permutation = repository.getElement(permutationModelId);
            permutation.setName(permutationName);
            permutations.put(permutationName, permutation);

            Map<String, String> alreadyAdded = new HashMap<>();
            for (String option : options.getValues()) {
                permutation.getPermutationMappings().stream()
                    .filter(permutationMap -> permutationMap.getDetectorElement().getId().equals(option))
                    .map(OTPrmMapping::getRefinementElement)
                    .filter(refinementElement -> refinementElement instanceof TNodeTemplate)
                    .map(refinementElement -> (TNodeTemplate) refinementElement)
                    .forEach(refinementElement -> {
                        TNodeTemplate addedDetectorElement = alreadyAdded.containsKey(refinementElement.getId())
                            ? permutation.getDetector().getNodeTemplate(alreadyAdded.get(refinementElement.getId()))
                            : addNodeFromRefinementStructureToDetector(refinementElement, permutation, alreadyAdded);

                        // region outgoing relations of the currently permuted refinementElement
                        ModelUtilities.getOutgoingRelationshipTemplates(permutation.getRefinementStructure(), refinementElement)
                            .forEach(relation -> {
                                // Using the permutation maps defined in the original model as we remove them in the permutation
                                refinementModel.getPermutationMappings().stream()
                                    .filter(permutationMap -> permutationMap.getRefinementElement().getId()
                                        .equals(relation.getTargetElement().getRef().getId()))
                                    .filter(permutationMap -> permutationMap.getDetectorElement() instanceof TNodeTemplate)
                                    .forEach(permutationMap -> {
                                        // If the relation is among components which are mutated in this permutation, 
                                        // i.e., it corresponds to the same detector element or is part of the components
                                        // to be mutated, add it the component and create the relation.
                                        if (permutationMap.getDetectorElement().getId().equals(option)
                                            || options.getValues().contains(permutationMap.getDetectorElement().getId())) {
                                            String alreadyAddedElement = alreadyAdded.get(relation.getTargetElement().getRef().getId());
                                            TNodeTemplate target = alreadyAddedElement == null
                                                ? addNodeFromRefinementStructureToDetector((TNodeTemplate) relation.getTargetElement().getRef(), permutation, alreadyAdded)
                                                : permutation.getDetector().getNodeTemplate(alreadyAddedElement);
                                            ModelUtilities.createRelationshipTemplateAndAddToTopology(
                                                addedDetectorElement, target, relation.getType(), permutation.getDetector());
                                        } else if (!options.getValues().contains(permutationMap.getDetectorElement().getId())) {
                                            // Else if the target is part of the detector, add the relation between the
                                            // added element and the detector element.
                                            // No need to check instance of again, as we filter them in line 383.
                                            TNodeTemplate target = (TNodeTemplate) permutationMap.getDetectorElement();
                                            ModelUtilities.createRelationshipTemplateAndAddToTopology(addedDetectorElement, target, relation.getType(), permutation.getDetector());
                                        }
                                    });
                            });
                        //endregion

                        // region handle ingoing relations in the detector
                        for (TRelationshipTemplate relation : permutation.getDetector().getRelationshipTemplates()) {
                            if (relation.getTargetElement().getRef().getId().equals(option)) {
                                Optional<OTPermutationMapping> relationTarget = permutation.getPermutationMappings().stream()
                                    .filter(permutationMap -> permutationMap.getDetectorElement().getId().equals(relation.getId()))
                                    .filter(permutationMap -> permutationMap.getRefinementElement().getId().equals(refinementElement.getId()))
                                    .findFirst();

                                long refinementEquivalents = permutation.getPermutationMappings().stream()
                                    .filter(permutationMap -> permutationMap.getDetectorElement().getId().equals(option))
                                    .map(OTPrmMapping::getRefinementElement)
                                    .distinct()
                                    .count();
                                if (relationTarget.isPresent() || refinementEquivalents == 1) {
                                    ModelUtilities.createRelationshipTemplateAndAddToTopology(
                                        (TNodeTemplate) relation.getSourceElement().getRef(),
                                        addedDetectorElement,
                                        relation.getType(),
                                        permutation.getDetector()
                                    );
                                }
                            }
                        }
                        // endregion
                    });

                // region remove permuted
                if (permutation.getAttributeMappings() != null) {
                    permutation.getRelationMappings()
                        .removeIf(map -> map.getDetectorElement().getId().equals(option));
                }
                if (permutation.getAttributeMappings() != null) {
                    permutation.getAttributeMappings()
                        .removeIf(map -> map.getDetectorElement().getId().equals(option));
                }
                if (permutation.getDeploymentArtifactMappings() != null) {
                    permutation.getDeploymentArtifactMappings()
                        .removeIf(map -> map.getDetectorElement().getId().equals(option));
                }
                permutation.getPermutationMappings()
                    .removeIf(permMap -> permMap.getDetectorElement().getId().equals(option) ||
                        permMap.getDetectorElement() instanceof TRelationshipTemplate &&
                            (((TRelationshipTemplate) permMap.getDetectorElement()).getSourceElement().getRef().getId().equals(option)
                                || ((TRelationshipTemplate) permMap.getDetectorElement()).getTargetElement().getRef().getId().equals(option)));

                permutation.getDetector().getNodeTemplateOrRelationshipTemplate()
                    .removeIf(template -> template instanceof TRelationshipTemplate &&
                        (((TRelationshipTemplate) template).getSourceElement().getRef().getId().equals(option)
                            || ((TRelationshipTemplate) template).getTargetElement().getRef().getId().equals(option))
                        || template.getId().equals(option)
                    );
                // endregion
            }

            try {
                RepositoryFactory.getRepository().setElement(permutationModelId, permutation);
            } catch (IOException e) {
                logger.error("Error while saving permutation!", e);
                break;
            }
        }

        return permutations;
    }

    public String getMutabilityErrorReason() {
        return mutabilityErrorReason;
    }

    private TNodeTemplate addNodeFromRefinementStructureToDetector(TNodeTemplate refinementElement,
                                                                   OTTopologyFragmentRefinementModel permutation,
                                                                   Map<String, String> alreadyAdded) {
        TNodeTemplate clone = SerializationUtils.clone(refinementElement);
        ModelUtilities.generateNewIdOfTemplate(clone, permutation.getDetector());
        permutation.getDetector().addNodeTemplate(clone);
        alreadyAdded.put(refinementElement.getId(), clone.getId());

        if (permutation.getStayMappings() == null) {
            permutation.setStayMappings(new ArrayList<>());
        }
        permutation.getStayMappings().add(new OTStayMapping(clone, refinementElement));

        return clone;
    }
}

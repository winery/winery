/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.enhance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.constants.OpenToscaInterfaces;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKVList;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.NamespaceManager;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.NamespaceProperties;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class exposes utility functions which enhance a given topology. It also provides some semantic utilities as,
 * e.g., returning the hostedOn relation of a NodeTemplate.
 */
public class EnhancementUtils {

    static final String GENERATED_NS_SUFFIX = "/generated";
    private static final Logger logger = LoggerFactory.getLogger(EnhancementUtils.class);

    // region ******************** Freeze and Defrost ********************
    public static TTopologyTemplate determineStatefulComponents(TTopologyTemplate topology) {
        Map<QName, TNodeType> nodeTypes = RepositoryFactory.getRepository().getQNameToElementMapping(NodeTypeId.class);

        topology.getNodeTemplates().stream()
            .filter(nodeTemplate -> {
                TNodeType type = nodeTypes.get(nodeTemplate.getType());
                if (Objects.nonNull(type.getTags())) {
                    return type.getTags().getTag()
                        .stream()
                        .anyMatch(
                            tag -> "stateful".equals(tag.getName().toLowerCase())
                                || "isStateful".toLowerCase().equals(tag.getName().toLowerCase())
                        );
                }

                return false;
            })
            // avoid duplicate annotations
            .filter(node -> !ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.statefulComponentPolicyType))
            .forEach(node ->
                ModelUtilities.addPolicy(node, OpenToscaBaseTypes.statefulComponentPolicyType, "stateful")
            );

        return topology;
    }

    public static TopologyAndErrorList determineFreezableComponents(TTopologyTemplate topology) {
        Map<QName, TNodeType> nodeTypes = RepositoryFactory.getRepository().getQNameToElementMapping(NodeTypeId.class);

        ArrayList<String> errorList = new ArrayList<>();
        topology.getNodeTemplates().stream()
            // only iterate over all stateful components
            .filter(node -> ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.statefulComponentPolicyType))
            // avoid duplicate annotations
            .filter(node -> !ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.freezableComponentPolicyType))
            .forEach(node -> {
                TNodeType type = nodeTypes.get(node.getType());
                if (ModelUtilities.nodeTypeHasInterface(type, OpenToscaInterfaces.stateInterface)) {
                    ModelUtilities.addPolicy(node, OpenToscaBaseTypes.freezableComponentPolicyType, "freezable");
                } else {
                    TRelationshipTemplate relationshipTemplate;
                    boolean isFreezable = false;
                    do {
                        relationshipTemplate = getHostedOnRelationship(topology, node);

                        if (Objects.nonNull(relationshipTemplate)) {
                            TNodeTemplate host = (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
                            TNodeType hostType = nodeTypes.get(host.getType());
                            if (ModelUtilities.nodeTypeHasInterface(hostType, OpenToscaInterfaces.stateInterface)) {
                                ModelUtilities.addPolicy(host, OpenToscaBaseTypes.freezableComponentPolicyType, "freezable");
                                isFreezable = true;
                            }
                        }
                    } while (!isFreezable && Objects.nonNull(relationshipTemplate));

                    if (!isFreezable) {
                        errorList.add(node.getId());
                    }
                }
            });

        TopologyAndErrorList topologyAndErrorList = new TopologyAndErrorList();
        topologyAndErrorList.errorList = errorList;
        topologyAndErrorList.topologyTemplate = topology;

        return topologyAndErrorList;
    }

    public static TTopologyTemplate cleanFreezableComponents(TTopologyTemplate topology) {
        topology.getNodeTemplates().stream()
            // only iterate over all freezable components
            .filter(node -> ModelUtilities.containsPolicyType(node, OpenToscaBaseTypes.freezableComponentPolicyType))
            .forEach(node -> {
                TRelationshipTemplate hostedOnRelationship = getHostedOnRelationship(topology, node);
                while (Objects.nonNull(hostedOnRelationship)) {
                    TNodeTemplate host = (TNodeTemplate) hostedOnRelationship.getTargetElement().getRef();
                    if (ModelUtilities.containsPolicyType(host, OpenToscaBaseTypes.freezableComponentPolicyType)) {
                        node.getPolicies().getPolicy()
                            .removeIf(policy -> policy.getPolicyType().equals(OpenToscaBaseTypes.freezableComponentPolicyType));
                        hostedOnRelationship = null;
                    } else {
                        hostedOnRelationship = getHostedOnRelationship(topology, host);
                    }
                }
            });

        return topology;
    }
    // endregion

    /**
     * This method returns the <em>hostedOn</em> RelationshipTemplate (see {@link ToscaBaseTypes#hostedOnRelationshipType})
     * of the given NodeTemplate in the given Topology. Note: It is assumed that there is only <b>one</b> hostedOn
     * relation.
     *
     * @param topology The topology in which the given node is in.
     * @param node     The node to return the hostedOn relation.
     */
    private static TRelationshipTemplate getHostedOnRelationship(TTopologyTemplate topology, TNodeTemplate node) {
        Map<QName, TRelationshipType> relationshipTypes = RepositoryFactory.getRepository().getQNameToElementMapping(RelationshipTypeId.class);
        List<TRelationshipTemplate> outgoingRelationshipTemplates = ModelUtilities.getOutgoingRelationshipTemplates(topology, node);
        return outgoingRelationshipTemplates.stream()
            .filter(relation -> ModelUtilities.isOfType(ToscaBaseTypes.hostedOnRelationshipType, relation.getType(), relationshipTypes))
            .findFirst()
            .orElse(null);
    }

    private static String generateNewGeneratedNamespace(QName qName) {
        NamespaceManager namespaceManager = RepositoryFactory.getRepository().getNamespaceManager();
        String namespace = qName.getNamespaceURI().concat(GENERATED_NS_SUFFIX);
        NamespaceProperties namespaceProperties = namespaceManager.getNamespaceProperties(namespace);
        namespaceProperties.setGeneratedNamespace(true);
        namespaceManager.setNamespaceProperties(namespace, namespaceProperties);
        return namespace;
    }

    private static void addAllDAsAndIAsToImplementation(TNodeTypeImplementation target, TNodeTypeImplementation source) {
        if (Objects.nonNull(source.getDeploymentArtifacts())) {
            target.getDeploymentArtifacts().getDeploymentArtifact().addAll(
                source.getDeploymentArtifacts().getDeploymentArtifact()
            );
        }

        if (Objects.nonNull(source.getImplementationArtifacts())) {
            target.getImplementationArtifacts().getImplementationArtifact().addAll(
                source.getImplementationArtifacts().getImplementationArtifact()
            );
        }
    }

    // region ******************** Add Management Features ******************** 

    /**
     * Gathers all feature NodeTypes available for the given topology.
     * <p>
     * Note: If feature NodeTypes are used in the topology, they cannot be enhanced with more features.
     * </p>
     *
     * @param topology The topology to update.
     */
    public static Map<String, Map<QName, String>> getAvailableFeaturesForTopology(TTopologyTemplate topology) {
        IRepository repository = RepositoryFactory.getRepository();

        Map<String, Map<QName, String>> availableFeatures = new HashMap<>();
        Map<QName, TNodeType> nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);

        topology.getNodeTemplates().forEach(node -> {
            Map<TNodeType, String> featureChildren = ModelUtilities.getAvailableFeaturesOfType(node.getType(), nodeTypes);
            Map<QName, String> applicableFeatures = new HashMap<>();

            // Check requirements
            featureChildren.forEach((featureType, value) -> {
                if (Objects.nonNull(featureType.getRequirementDefinitions())) {
                    List<TRequirementDefinition> requirements = featureType.getRequirementDefinitions().getRequirementDefinition().stream()
                        .filter(req -> req.getRequirementType().equals(OpenToscaBaseTypes.managementFeatureRequirement))
                        .collect(Collectors.toList());

                    requirements.forEach(req -> {
                        boolean applicable = ModelUtilities.getHostedOnSuccessors(topology, node).stream()
                            .anyMatch(hosts -> {
                                WineryVersion reqVersion = VersionUtils.getVersion(req.getName());
                                String reqName = VersionUtils.getNameWithoutVersion(req.getName());

                                String type = hosts.getTypeAsQName().getLocalPart();
                                if (VersionUtils.getNameWithoutVersion(type).equals(reqName)) {
                                    return reqVersion.getComponentVersion().isEmpty()
                                        || reqVersion.getComponentVersion().equals(VersionUtils.getVersion(type).getComponentVersion());
                                }

                                return false;
                            });

                        if (applicable) {
                            applicableFeatures.put(featureType.getQName(), value);
                        }
                    });
                } else {
                    applicableFeatures.put(featureType.getQName(), value);
                }
            });

            if (featureChildren.size() > 0) {
                availableFeatures.put(node.getId(), applicableFeatures);
            }
        });

        return availableFeatures;
    }

    /**
     * This method applies selected features to the given topology. Hereby the <code>featureMap</code> as generated by
     * {@link EnhancementUtils#getAvailableFeaturesForTopology(TTopologyTemplate)} is expected. However, the list may
     * differ from the originally generated one, since a user may want to have only a specific feature, i.e., all of the
     * specified features in the given list are applied.
     *
     * @param topology   The topology, the features will be applied to. It must be the same topology which was passed to
     *                   the {@link EnhancementUtils#getAvailableFeaturesForTopology(TTopologyTemplate)}.
     * @param featureMap The list of features to apply to the topology.
     * @return The updated topology in which all matching NodeTypes will be replaced with the corresponding feature
     * NodeTypes.
     */
    public static TTopologyTemplate applyFeaturesForTopology(TTopologyTemplate topology, Map<String, Map<QName, String>> featureMap) {
        topology.getNodeTemplates().stream()
            .filter(nodeTemplate -> Objects.nonNull(featureMap.get(nodeTemplate.getId())))
            .forEach(nodeTemplate -> {
                TNodeType generatedNodeType = createFeatureNodeType(nodeTemplate, featureMap.get(nodeTemplate.getId()));
                nodeTemplate.setType(generatedNodeType.getQName());
                if (Objects.nonNull(generatedNodeType.getWinerysPropertiesDefinition())) {
                    PropertyDefinitionKVList definedProperties = generatedNodeType.getWinerysPropertiesDefinition()
                        .getPropertyDefinitionKVList();

                    final @NonNull LinkedHashMap<String, String> kvProperties = ModelUtilities.getPropertiesKV(nodeTemplate) == null
                        ? new LinkedHashMap<>()
                        : ModelUtilities.getPropertiesKV(nodeTemplate);
                    if (kvProperties.isEmpty()) {
                        definedProperties.stream().map(PropertyDefinitionKV::getKey)
                            .forEach(k -> kvProperties.put(k, ""));
                        definedProperties.forEach(propertyDefinition -> kvProperties.put(propertyDefinition.getKey(), ""));
                    } else {
                        definedProperties.forEach(propertyDefinition -> {
                            if (Objects.isNull(kvProperties.get(propertyDefinition.getKey()))) {
                                kvProperties.put(propertyDefinition.getKey(), "");
                            }
                        });
                    }
                    // TODO:
                    // We need to set new Properties because the {@link TEntityTemplate#setProperties} is implemented
                    // badly and does not add new properties. Due to time constraints we do it that way for now.
                    if (!kvProperties.isEmpty()) {
                        ModelUtilities.setPropertiesKV(nodeTemplate, kvProperties);
                    }
                }
            });

        return topology;
    }

    /**
     * This method merges the Basic-NodeType of the given nodeTemplate with the selected Feature-NodeTypes and generates
     * respective implementations.
     *
     * @param nodeTemplate The NodeTemplate that is updated with the selected features.
     * @param featureTypes The list of selected features as generated by {@link EnhancementUtils#getAvailableFeaturesForTopology(TTopologyTemplate)}.
     * @return The mapping of the generated merged NodeType and the QName of the NodeType it replaces.
     */
    public static TNodeType createFeatureNodeType(TNodeTemplate nodeTemplate, Map<QName, String> featureTypes) {
        IRepository repository = RepositoryFactory.getRepository();
        Map<QName, TNodeType> nodeTypes = repository.getQNameToElementMapping(NodeTypeId.class);
        Map<QName, TNodeTypeImplementation> nodeTypeImplementations = repository.getQNameToElementMapping(NodeTypeImplementationId.class);

        StringBuilder featureNames = new StringBuilder();
        featureTypes.values().forEach(featureName -> {
            if (!featureNames.toString().isEmpty()) {
                featureNames.append("-");
            }
            featureNames.append(featureName.replaceAll("\\s", "_"));
        });

        // merge type
        String namespace = generateNewGeneratedNamespace(nodeTemplate.getType());
        TNodeType featureEnrichedNodeType = nodeTypes.get(nodeTemplate.getType());
        featureEnrichedNodeType.setTargetNamespace(namespace);
        featureEnrichedNodeType.setName(
            nodeTemplate.getType().getLocalPart() + "-" + nodeTemplate.getId() + "-" + featureNames.toString()
                + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + "1"
        );

        // prepare Properties
        if (Objects.isNull(featureEnrichedNodeType.getWinerysPropertiesDefinition())) {
            WinerysPropertiesDefinition props = new WinerysPropertiesDefinition();
            props.setPropertyDefinitionKVList(new PropertyDefinitionKVList());
            ModelUtilities.replaceWinerysPropertiesDefinition(featureEnrichedNodeType, props);
        }
        List<PropertyDefinitionKV> baseProperties = featureEnrichedNodeType.getWinerysPropertiesDefinition()
            .getPropertyDefinitionKVList()
            .getPropertyDefinitionKVs();

        // prepare Interfaces
        if (Objects.isNull(featureEnrichedNodeType.getInterfaces())) {
            featureEnrichedNodeType.setInterfaces(new TInterfaces());
        }

        List<TInterface> baseInterfaces = featureEnrichedNodeType.getInterfaces().getInterface();

        // merge impl accordingly
        TNodeTypeImplementation generatedImplementation = new TNodeTypeImplementation();
        generatedImplementation.setNodeType(featureEnrichedNodeType.getQName());
        generatedImplementation.setName(
            featureEnrichedNodeType.getName() + "_Impl"
                + WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + "1"
        );

        // ensure that the lists are initialized
        generatedImplementation.setImplementationArtifacts(new TImplementationArtifacts());
        generatedImplementation.setDeploymentArtifacts(new TDeploymentArtifacts());

        Collection<NodeTypeImplementationId> baseTypeImplementations =
            repository.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, nodeTemplate.getType());

        if (baseTypeImplementations.size() > 0) {
            for (NodeTypeImplementationId id : baseTypeImplementations) {
                if (Objects.isNull(generatedImplementation.getTargetNamespace())) {
                    generatedImplementation.setTargetNamespace(generateNewGeneratedNamespace(id.getQName()));
                }
                addAllDAsAndIAsToImplementation(generatedImplementation, nodeTypeImplementations.get(id.getQName()));
            }
        } else {
            // This should never be the case. However, we implement it as a valid fallback. 
            generatedImplementation.setTargetNamespace(
                namespace.replace("nodetypes", "nodetypeimplementations")
            );
        }

        featureTypes.keySet().forEach(featureTypeQName -> {
            TNodeType nodeType = nodeTypes.get(featureTypeQName);

            // merge Properties
            if (Objects.nonNull(nodeType.getWinerysPropertiesDefinition())) {
                PropertyDefinitionKVList kvList = nodeType.getWinerysPropertiesDefinition().getPropertyDefinitionKVList();
                if (Objects.nonNull(kvList) && !kvList.isEmpty()) {
                    for (PropertyDefinitionKV kv : kvList) {
                        boolean listContainsProperty = baseProperties.stream()
                            .anyMatch(property -> property.getKey().equals(kv.getKey()));
                        if (!listContainsProperty) {
                            baseProperties.add(kv);
                        }
                    }
                }
            }

            // merge Interfaces
            if (Objects.nonNull(nodeType.getInterfaces()) && !nodeType.getInterfaces().getInterface().isEmpty()) {
                baseInterfaces.addAll(nodeType.getInterfaces().getInterface());
            }

            // merge implementations
            repository.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, featureTypeQName)
                .forEach(id -> {
                    addAllDAsAndIAsToImplementation(generatedImplementation, nodeTypeImplementations.get(id.getQName()));
                });
        });

        // In the case that neither the basic type, nor the feature types define properties,
        // remove them from the type to ensure a compliant XML.
        if (Objects.nonNull(featureEnrichedNodeType.getWinerysPropertiesDefinition())
            && Objects.nonNull(featureEnrichedNodeType.getWinerysPropertiesDefinition().getPropertyDefinitionKVList())
            && featureEnrichedNodeType.getWinerysPropertiesDefinition().getPropertyDefinitionKVList().isEmpty()) {
            ModelUtilities.removeWinerysPropertiesDefinition(featureEnrichedNodeType);
        }

        try {
            repository.setElement(new NodeTypeId(featureEnrichedNodeType.getQName()), featureEnrichedNodeType);
            repository.setElement(new NodeTypeImplementationId(generatedImplementation.getQName()), generatedImplementation);
        } catch (IOException e) {
            logger.error("Error while saving generated definitions.", e);
        }

        return featureEnrichedNodeType;
    }
    // endregion
}

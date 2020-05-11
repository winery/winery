/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.Constants;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.InterfaceTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TAppliesTo;
import org.eclipse.winery.model.tosca.TArtifact;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifacts;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.kvproperties.AttributeDefinition;
import org.eclipse.winery.model.tosca.kvproperties.AttributeDefinitionList;
import org.eclipse.winery.model.tosca.kvproperties.ConstraintClauseKVList;
import org.eclipse.winery.model.tosca.kvproperties.ParameterDefinition;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.yaml.TArtifactDefinition;
import org.eclipse.winery.model.tosca.yaml.TArtifactType;
import org.eclipse.winery.model.tosca.yaml.TAttributeDefinition;
import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.yaml.TCapabilityType;
import org.eclipse.winery.model.tosca.yaml.TConstraintClause;
import org.eclipse.winery.model.tosca.yaml.TImplementation;
import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceType;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TNodeType;
import org.eclipse.winery.model.tosca.yaml.TOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.TParameterDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyType;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignmentOrDefinition;
import org.eclipse.winery.model.tosca.yaml.TPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipAssignment;
import org.eclipse.winery.model.tosca.yaml.TRelationshipDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.yaml.TRelationshipType;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.TSubstitutionMappings;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.support.Defaults;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.TMapImportDefinition;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementDefinition;
import org.eclipse.winery.repository.backend.filebased.YamlRepository;
import org.eclipse.winery.repository.converter.support.Namespaces;
import org.eclipse.winery.repository.converter.support.ValueConverter;
import org.eclipse.winery.repository.converter.support.xml.TypeConverter;
import org.eclipse.winery.repository.export.YamlExporter;

import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class X2YConverter {
    public final static Logger LOGGER = LoggerFactory.getLogger(X2YConverter.class);

    private final YamlRepository repository;
//    private final Path path;

    private HashBiMap<String, String> prefixNamespace;
    private Map<DefinitionsChildId, Definitions> importDefinitions;

    public X2YConverter(YamlRepository repository) {
        this.repository = repository;
        this.prefixNamespace = new HashBiMap<>();
        this.importDefinitions = new LinkedHashMap<>();
    }

    @NonNull
    public TServiceTemplate convert(Definitions node) {
        return convert(node, false);
    }

    /**
     * Converts TOSCA XML Definitions to TOSCA YAML ServiceTemplates
     */
    @NonNull
    public TServiceTemplate convert(Definitions node, boolean convertImports) {
        LOGGER.debug("Convert TServiceTemplate: {}", node.getIdFromIdOrNameField());

        TServiceTemplate.Builder builder = new TServiceTemplate.Builder(Defaults.TOSCA_DEFINITIONS_VERSION)
            .setDescription(convertDocumentation(node.getElement().getDocumentation()))
            .setArtifactTypes(convert(node.getArtifactTypes()))
            .setCapabilityTypes(convert(node.getCapabilityTypes()))
            .setRelationshipTypes(convert(node.getRelationshipTypes()))
            .setNodeTypes(convert(node.getNodeTypes()))
            .setPolicyTypes(convert(node.getPolicyTypes()))
            .setInterfaceTypes(convert(node.getInterfaceTypes()));

        if (node.getServiceTemplates().size() == 1) {
            builder.setTopologyTemplate(convert(node.getServiceTemplates().get(0)));
            builder.addMetadata("targetNamespace", node.getTargetNamespace());
        }

        if (convertImports) {
            builder.setImports(convertImports());
        }

        return builder.build();
    }

    public List<TMapImportDefinition> convertImports() {
        List<TMapImportDefinition> imports = new ArrayList<>();
        TMapImportDefinition tMapImportDefinition = new TMapImportDefinition();
        for (Map.Entry<DefinitionsChildId, Definitions> importDefinition : importDefinitions.entrySet()) {
            TImportDefinition tImportDefinition = new TImportDefinition.Builder(YamlExporter.getDefinitionsName(repository, importDefinition.getKey()).concat(Constants.SUFFIX_TOSCA_DEFINITIONS))
                .setNamespacePrefix(getNamespacePrefix(importDefinition.getKey().getQName().getNamespaceURI()))
                .setNamespaceUri(importDefinition.getKey().getQName().getNamespaceURI())
                .build();
            tMapImportDefinition.put(importDefinition.getKey().getQName().getLocalPart(), tImportDefinition);
        }
        if (!tMapImportDefinition.isEmpty()) {
            imports.add(tMapImportDefinition);
            return imports;
        } else {
            return null;
        }
    }

    public Map<String, TPropertyAssignment> convert(TEntityTemplate.Properties node) {
        if (Objects.isNull(node)) return null;
        Map<String, String> propertiesKV = node.getKVProperties();
        if (Objects.isNull(propertiesKV)) return null;
        return propertiesKV.entrySet().stream()
            .map(entry ->
                new LinkedHashMap.SimpleEntry<>(
                    String.valueOf(entry.getKey()),
                    new TPropertyAssignment.Builder()
                        .setValue(ValueConverter.INSTANCE.convert(entry.getValue()))
                        .build()
                )
            )
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
    }

    public TTopologyTemplateDefinition convert(org.eclipse.winery.model.tosca.TServiceTemplate node) {
        // TODO substitution mappings are currently not converted
        if (Objects.isNull(node)) return null;
        TTopologyTemplate topologyTemplate = node.getTopologyTemplate();
        if (Objects.isNull(topologyTemplate)) return null;
        return new TTopologyTemplateDefinition.Builder()
            .setDescription(convertDocumentation(topologyTemplate.getDocumentation()))
            .setNodeTemplates(convert(topologyTemplate.getNodeTemplates(), topologyTemplate.getRelationshipTemplates()))
            .setRelationshipTemplates(convert(topologyTemplate.getRelationshipTemplates()))
            .setPolicies(convert(topologyTemplate.getPolicies()))
            .setInputs(convert(topologyTemplate.getInputs()))
            .setOutputs(convert(topologyTemplate.getOutputs()))
            .build();
    }

    public Map<String, TNodeTemplate> convert(List<org.eclipse.winery.model.tosca.TNodeTemplate> nodes, List<org.eclipse.winery.model.tosca.TRelationshipTemplate> rTs) {
        if (Objects.isNull(nodes)) return null;
        return nodes.stream()
            .filter(Objects::nonNull)
            .flatMap(entry -> convert(entry, Optional.ofNullable(rTs).orElse(new ArrayList<>())).entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NonNull
    public Map<String, TNodeTemplate> convert(org.eclipse.winery.model.tosca.TNodeTemplate node, @NonNull List<org.eclipse.winery.model.tosca.TRelationshipTemplate> rTs) {
        if (Objects.isNull(node)) {
            return new LinkedHashMap<>();
        }
        Metadata meta = new Metadata();
        if (Objects.nonNull(node.getX()) && Objects.nonNull(node.getY())) {
            meta.add(org.eclipse.winery.repository.converter.support.Defaults.X_COORD, node.getX());
            meta.add(org.eclipse.winery.repository.converter.support.Defaults.Y_COORD, node.getY());
        }

        if (Objects.nonNull(node.getName())) {
            meta.add(org.eclipse.winery.repository.converter.support.Defaults.DISPLAY_NAME, node.getName());
        }

        return Collections.singletonMap(
            node.getIdFromIdOrNameField(),
            new TNodeTemplate.Builder(
                convert(
                    node.getType(),
                    new NodeTypeId(node.getType())
                ))
                .setProperties(convert(node.getProperties()))
                .setMetadata(meta)
                .setRequirements(convert(node.getRequirements()))
                .setCapabilities(convert(node.getCapabilities()))
                .setArtifacts(convert(node.getArtifacts()))
                .build()
        );
    }

    @NonNull
    public Map<String, TRelationshipTemplate> convert(org.eclipse.winery.model.tosca.TRelationshipTemplate node) {
        if (Objects.isNull(node)) return new LinkedHashMap<>();
        return Collections.singletonMap(
            node.getIdFromIdOrNameField(),
            new TRelationshipTemplate.Builder(convert(node.getType(), new RelationshipTypeId(node.getType())))
                .setProperties(convert(node.getProperties()))
                .build()
        );
    }

    public <T extends org.eclipse.winery.model.tosca.yaml.TEntityType.Builder<T>> T convert(TEntityType node, T builder, Class<? extends TEntityType> clazz) {
        TBoolean isFinal = node.getFinal();
        TBoolean isAbstract = node.getAbstract();

        // ensure that the targetNamespace is always set
        if (Objects.isNull(node.getTargetNamespace()) || node.getTargetNamespace().isEmpty()) {
            String id = node.getIdFromIdOrNameField();
            node.setTargetNamespace(id.substring(0, id.lastIndexOf(".")));
        }

        return builder
            .setDerivedFrom(convert(node.getDerivedFrom(), clazz))
            .setMetadata(convert(node.getTags()))
            .addMetadata("targetNamespace", node.getTargetNamespace())
            .addMetadata("abstract", isAbstract.equals(TBoolean.YES) ? "true" : "false")
            .addMetadata("final", isFinal.equals(TBoolean.YES) ? "true" : "false")
            .setProperties(convert(node, node.getPropertiesDefinition()))
            .setAttributes(convert(node, node.getAttributeDefinitions()))
            .setDescription(convertDocumentation(node.getDocumentation()));
    }

    public Map<String, TPropertyDefinition> convert(TEntityType type, TEntityType.PropertiesDefinition node) {
        // TODO convert properties beside simple winery properties
        WinerysPropertiesDefinition properties = type.getWinerysPropertiesDefinition();
        if (Objects.isNull(properties) ||
            Objects.isNull(properties.getPropertyDefinitionKVList()) ||
            properties.getPropertyDefinitionKVList().isEmpty()) return null;
        return properties.getPropertyDefinitionKVList().stream()
            .collect(Collectors.toMap(
                PropertyDefinitionKV::getKey,
                entry -> new TPropertyDefinition.Builder(convertType(entry.getType()))
                    .setRequired(entry.isRequired())
                    .setDefault(entry.getDefaultValue())
                    .setDescription(entry.getDescription())
                    .addConstraints(convert(entry.getConstraints()))
                    .build()
            ));
    }

    public Map<String, TAttributeDefinition> convert(TEntityType node, @Nullable AttributeDefinitionList attributes) {
        if (Objects.isNull(node) || Objects.isNull(attributes)) return new HashMap<>();
        return attributes.stream().collect(Collectors.toMap(
            AttributeDefinition::getKey,
            entry -> new TAttributeDefinition.Builder(entry.getType())
                .setDescription(entry.getDescription())
                .setDefault(entry.getDefaultValue())
                .build()
        ));
    }

    public List<TConstraintClause> convert(ConstraintClauseKVList constraints) {
        if (Objects.isNull(constraints)) return null;

        List<TConstraintClause> list = new ArrayList<>();
        constraints.forEach(entry -> {
            TConstraintClause clause = new TConstraintClause();
            clause.setKey(entry.getKey());
            clause.setValue(entry.getValue());
            clause.setList(entry.getList());
            list.add(clause);
        });
        return list;
    }

    public Map<String, TArtifactType> convert(org.eclipse.winery.model.tosca.TArtifactType node) {
        TArtifactType.Builder builder = new TArtifactType.Builder()
            .setMimeType(node.getMimeType())
            .setFileExt(node.getFileExtensions());
        String nodeFullName = this.getFullName(node);
        return Collections.singletonMap(
            nodeFullName,
            convert(node, builder, org.eclipse.winery.model.tosca.TArtifactType.class).build()
        );
    }

    public Map<String, TNodeType> convert(org.eclipse.winery.model.tosca.TNodeType node) {
        if (Objects.isNull(node)) return null;
        // TNodeTypeImplementation impl = getNodeTypeImplementation(new QName(node.getTargetNamespace(), node.getName()));

        String nodeFullName = this.getFullName(node);

        return Collections.singletonMap(
            nodeFullName,
            convert(node, new TNodeType.Builder(), org.eclipse.winery.model.tosca.TNodeType.class)
                .setRequirements(convert(node.getRequirementDefinitions()))
                .setCapabilities(convert(node.getCapabilityDefinitions()))
                .setInterfaces(convert(node.getInterfaceDefinitions()))
                .setArtifacts(convert(node.getArtifacts()))
                // .setArtifacts(convert(node.getArtifacts().getArtifact()))
                .build()
        );
    }

    public Map<String, TArtifactDefinition> convert(TNodeTypeImplementation node, Map<String, TArtifactDefinition> artifacts) {
        if (Objects.isNull(node)) return null;
        String suffix = "@" + node.getNodeType().getLocalPart() + "@" + "nodetypes";
        return Stream.of(convert(node.getDeploymentArtifacts(), artifacts), convert(node.getImplementationArtifacts(), artifacts))
            .filter(Objects::nonNull)
            .flatMap(entry -> entry.entrySet().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

//    public Map<String, TRelationshipType> convert(org.eclipse.winery.model.tosca.TRelationshipType node) {
//        if (Objects.isNull(node)) return null;
//        // TODO Use TRelationshipTypeImplementation artifacts
//        TRelationshipTypeImplementation impl = getRelationshipTypeImplementation(new QName(node.getTargetNamespace(), node.getName()));
//        return Collections.singletonMap(
//            node.getIdFromIdOrNameField(),
//            convert(node, new TRelationshipType.Builder(), org.eclipse.winery.model.tosca.TRelationshipType.class)
//                .addInterfaces(convert(node.getSourceInterfaces(), impl))
//                .addInterfaces(convert(node.getTargetInterfaces(), impl))
//                .build()
//        );
//    }

    public Map<String, TRelationshipType> convert(org.eclipse.winery.model.tosca.TRelationshipType node) {
        if (Objects.isNull(node)) return null;
        String nodeFullName = this.getFullName(node);
        return Collections.singletonMap(
            nodeFullName,
            convert(node, new TRelationshipType.Builder(), org.eclipse.winery.model.tosca.TRelationshipType.class)
                .addInterfaces(convert(node.getInterfaceDefinitions()))
                .addValidTargetTypes(convertTargets(node.getValidSource(), node.getValidTarget()))
                .build()
        );
    }

    private List<QName> convertTargets(org.eclipse.winery.model.tosca.TRelationshipType.ValidSource validSource, org.eclipse.winery.model.tosca.TRelationshipType.ValidTarget validTarget) {
        if (validSource != null && validTarget != null) {
            List<QName> output = new ArrayList<>();
            output.add(new QName(validSource.getTypeRef().getNamespaceURI(), validSource.getTypeRef().getLocalPart()));
            output.add(new QName(validTarget.getTypeRef().getNamespaceURI(), validSource.getTypeRef().getLocalPart()));
            return output;
        }
        return null;
    }

    public Map<String, TInterfaceDefinition> convert(TInterfaces node, TRelationshipTypeImplementation implementation) {
        if (Objects.isNull(node)) return null;
        return node.getInterface().stream()
            .filter(Objects::nonNull)
            .map(entry -> convert(
                entry,
                Optional.ofNullable(implementation.getImplementationArtifacts()).orElse(new TImplementationArtifacts())
                    .getImplementationArtifact().stream()
                    .filter(impl -> Objects.nonNull(impl) && impl.getInterfaceName().equals(entry.getName()))
                    .collect(Collectors.toList())
                )
            )
            .flatMap(entry -> entry.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NonNull
    public Map<String, TCapabilityType> convert(org.eclipse.winery.model.tosca.TCapabilityType node) {
        if (Objects.isNull(node)) return new LinkedHashMap<>();
        String nodeFullName = this.getFullName(node);
        return Collections.singletonMap(
            nodeFullName,
            convert(node, new TCapabilityType.Builder(), org.eclipse.winery.model.tosca.TCapabilityType.class)
                .addValidSourceTypes(node.getValidNodeTypes())
                .build()
        );
    }

    @NonNull
    public Map<String, TPolicyType> convert(org.eclipse.winery.model.tosca.TPolicyType node) {
        if (Objects.isNull(node)) return new LinkedHashMap<>();
        TPolicyType.Builder builder = new TPolicyType.Builder();

        if (node.getAppliesTo() != null) {
            builder = builder.setTargets(node
                .getAppliesTo()
                .getNodeTypeReference()
                .stream()
                .map(TAppliesTo.NodeTypeReference::getTypeRef)
                .collect(Collectors.toList()));
        }
        String nodeFullName = this.getFullName(node);
        return Collections.singletonMap(
            nodeFullName,
            convert(node, builder, org.eclipse.winery.model.tosca.TPolicyType.class)
                .build()
        );
    }

    public TSubstitutionMappings convert(TBoundaryDefinitions node) {
        if (Objects.isNull(node)) return null;
        return new TSubstitutionMappings.Builder()
            // TODO Convert Boundary definitions
            .build();
    }

    @NonNull
    public Map<String, TPolicyDefinition> convert(TPolicy node) {
        if (Objects.isNull(node)) return new LinkedHashMap<>();
        return Collections.singletonMap(
            node.getName(),
            new TPolicyDefinition.Builder(convert(node.getPolicyType(), new PolicyTypeId(node.getPolicyType())))
                .setProperties(convert(node.getProperties()))
                .setTargets(node.getTargets())
                .build()
        );
    }

    /**
     * Converts TOSCA XML list of Documentations to TOSCA YAML Description of type string
     */
    public String convertDocumentation(@NonNull List<TDocumentation> doc) {
        return doc.stream()
            .map(TDocumentation::getContent)
            .flatMap(List::stream)
            .map(Object::toString)
            .collect(Collectors.joining("\n"));
    }

    public QName convert(TEntityType.DerivedFrom node, Class<? extends TEntityType> clazz) {
        if (Objects.isNull(node)) return null;
        DefinitionsChildId id;
        if (clazz.equals(org.eclipse.winery.model.tosca.TNodeType.class)) {
            id = new NodeTypeId(node.getTypeRef());
        } else if (clazz.equals(org.eclipse.winery.model.tosca.TRelationshipType.class)) {
            id = new RelationshipTypeId(node.getTypeRef());
        } else if (clazz.equals(TRequirementType.class)) {
            id = new RequirementTypeId(node.getTypeRef());
        } else if (clazz.equals(org.eclipse.winery.model.tosca.TCapabilityType.class)) {
            id = new CapabilityTypeId(node.getTypeRef());
        } else if (clazz.equals(org.eclipse.winery.model.tosca.TArtifactType.class)) {
            id = new ArtifactTypeId(node.getTypeRef());
        } else if (clazz.equals(org.eclipse.winery.model.tosca.TInterfaceType.class)) {
            id = new InterfaceTypeId(node.getTypeRef());
        } else {
            id = new PolicyTypeId(node.getTypeRef());
        }
        return getQName(
            id,
            node.getTypeRef().getNamespaceURI(),
            node.getTypeRef().getLocalPart());
    }

    public Metadata convert(TTags node) {
        if (Objects.isNull(node)) return null;
        return node.getTag().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                TTag::getName,
                TTag::getValue,
                (a, b) -> a + "|" + b,
                Metadata::new));
    }

    @Deprecated
    public Map<String, TInterfaceDefinition> convert(TInterface node) {
        if (Objects.isNull(node)) return new LinkedHashMap<>();
        return Collections.singletonMap(
            node.getName(),
            new TInterfaceDefinition.Builder<>()
                .setOperations(convertOperations(node.getOperation()))
                .build()
        );
    }

    @Deprecated
    public Map<String, TOperationDefinition> convertOperations(List<TOperation> nodes) {
        if (Objects.isNull(nodes)) return null;
        return nodes.stream()
            .filter(Objects::nonNull)
            .flatMap(node -> convert(node)
                .entrySet().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NonNull
    @Deprecated
    public Map<String, TOperationDefinition> convert(TOperation node) {
        if (Objects.isNull(node)) return new LinkedHashMap<>();
        return Collections.singletonMap(
            node.getName(),
            new TOperationDefinition.Builder().build()
        );
    }

    @Deprecated
    public Map<String, TInterfaceDefinition> convert(TInterfaces node, TNodeTypeImplementation implementation) {
        if (Objects.isNull(node)) return null;
        return node.getInterface().stream()
            .filter(Objects::nonNull)
            .map(entry -> convert(
                entry,
                Optional.ofNullable(implementation.getImplementationArtifacts()).orElse(new TImplementationArtifacts())
                    .getImplementationArtifact().stream()
                    .filter(impl -> Objects.nonNull(impl) && impl.getInterfaceName().equals(entry.getName()))
                    .collect(Collectors.toList())
                )
            )
            .flatMap(entry -> entry.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NonNull
    @Deprecated
    public Map<String, TInterfaceDefinition> convert(TInterface node, @NonNull List<TImplementationArtifact> impl) {
        if (Objects.isNull(node)) return new LinkedHashMap<>();
        return Collections.singletonMap(node.getName(), new TInterfaceDefinition.Builder<>().build());
    }

    public Map<String, TOperationDefinition> convertOperations(List<TOperation> nodes, @NonNull List<TImplementationArtifact> impl) {
        if (Objects.isNull(nodes)) return null;
        return nodes.stream()
            .filter(Objects::nonNull)
            .flatMap(node -> convert(
                node,
                impl.stream()
                    .filter(entry -> Objects.nonNull(entry)
                        && Objects.nonNull(entry.getOperationName())
                        && entry.getOperationName().equals(node.getName()))
                    .collect(Collectors.toList())
            ).entrySet().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NonNull
    @Deprecated
    public Map<String, TOperationDefinition> convert(TOperation node, List<TImplementationArtifact> impl) {
        if (Objects.isNull(node)) return new LinkedHashMap<>();
        return Collections.singletonMap(
            node.getName(),
            new TOperationDefinition.Builder().build()
        );
    }

    @Deprecated
    public TServiceTemplate convertNodeTypeImplementation(TServiceTemplate type, TNodeTypeImplementation node) {
        if (Objects.isNull(node)) return null;
        TNodeType nodeType = type.getNodeTypes().entrySet().iterator().next().getValue();
        nodeType.setArtifacts(convert(node, nodeType.getArtifacts()));
        nodeType.setInterfaces(convertInterfaces(nodeType.getInterfaces(), node.getImplementationArtifacts()));
        type.getNodeTypes().entrySet().iterator().next().setValue(nodeType);
        type.setImports(addNewImports(type.getImports()));
        return type;
    }

    @Deprecated
    public TServiceTemplate convertRelationshipTypeImplementation(TServiceTemplate type, TRelationshipTypeImplementation node) {
        if (Objects.isNull(node)) return null;
        TRelationshipType relationshipType = type.getRelationshipTypes().entrySet().iterator().next().getValue();
        // relationshipType.setInterfaces(convertRelationshipInterfaces(relationshipType.getInterfaces(), node.getImplementationArtifacts()));
        type.getRelationshipTypes().entrySet().iterator().next().setValue(relationshipType);
        return type;
    }

    private List<TMapImportDefinition> addNewImports(List<TMapImportDefinition> imports) {
        List<TMapImportDefinition> newImportsList = convertImports();
        if (newImportsList.isEmpty()) {
            return imports;
        }
        if (imports.isEmpty()) {
            return newImportsList;
        }
        TMapImportDefinition newImports = newImportsList.get(0);
        TMapImportDefinition existingImports = imports.get(0);
        for (Map.Entry<String, TImportDefinition> newImport : newImports.entrySet()) {
            Boolean found = false;
            for (Map.Entry<String, TImportDefinition> existingImport : existingImports.entrySet()) {
                if (newImport.getKey().equalsIgnoreCase(existingImport.getKey()) && newImport.getValue().equals(existingImport.getValue())) {
                    found = true;
                }
            }
            if (!found) {
                existingImports.put(newImport.getKey(), newImport.getValue());
            }
        }
        imports.set(0, newImports);
        return imports;
    }

    @Deprecated
    private Map<String, TInterfaceDefinition> convertInterfaces(Map<String, TInterfaceDefinition> interfaces, TImplementationArtifacts implementationArtifacts) {
        if (implementationArtifacts == null) {
            return interfaces;
        }
        List<TImplementationArtifacts.ImplementationArtifact> listImplArt = implementationArtifacts.getImplementationArtifact();
        for (TImplementationArtifacts.ImplementationArtifact implementationArtifact : listImplArt) {
            TInterfaceDefinition selectedInterface = interfaces.get(implementationArtifact.getInterfaceName());
            if (selectedInterface != null) {
                TOperationDefinition operation = selectedInterface.getOperations().get(implementationArtifact.getOperationName());
                // operation.setImplementation(convertImplementation(implementationArtifact, operation.getImplementation()));
            }
        }
        return interfaces;
    }

    public List<TMapRequirementDefinition> convert(org.eclipse.winery.model.tosca.TNodeType.RequirementDefinitions node) {
        if (Objects.isNull(node)) return null;
        return node.getRequirementDefinition().stream()
            .filter(Objects::nonNull)
            .map(this::convert)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public Map<String, TArtifactDefinition> convert(TDeploymentArtifacts node) {
        if (Objects.isNull(node)) return null;
        return node.getDeploymentArtifact().stream()
            .filter(Objects::nonNull)
            .map(ia -> new LinkedHashMap.SimpleEntry<>(ia.getArtifactRef().getLocalPart(), convertArtifactReference(new QName(ia.getArtifactRef().getNamespaceURI(), ia.getArtifactRef().getLocalPart()))))
            .filter(Objects::nonNull)
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, TArtifactDefinition> convert(TDeploymentArtifacts node, Map<String, TArtifactDefinition> artifacts) {
        if (Objects.isNull(node)) return null;
        Map<String, TArtifactDefinition> output = new LinkedHashMap<>();
        for (TDeploymentArtifact deploymentArtifact : node.getDeploymentArtifact()) {
            if (artifacts.containsKey(deploymentArtifact.getArtifactRef().getLocalPart())) {
                output.put(deploymentArtifact.getArtifactRef().getLocalPart(), artifacts.get(deploymentArtifact.getArtifactRef().getLocalPart()));
            } else {
                output.put(deploymentArtifact.getArtifactRef().getLocalPart(), convertArtifactReference(deploymentArtifact.getArtifactRef()));
            }
        }

        return output;
    }

    public Map<String, TArtifactDefinition> convert(TImplementationArtifacts node, Map<String, TArtifactDefinition> artifacts) {
        if (Objects.isNull(node)) return null;
        Map<String, TArtifactDefinition> output = new LinkedHashMap<>();
        for (TImplementationArtifact implementationArtifact : node.getImplementationArtifact()) {
            if (artifacts.containsKey(implementationArtifact.getArtifactRef().getLocalPart())) {
                output.put(implementationArtifact.getArtifactRef().getLocalPart(), artifacts.get(implementationArtifact.getArtifactRef().getLocalPart()));
            } else {
                output.put(implementationArtifact.getArtifactRef().getLocalPart(), convertArtifactReference(implementationArtifact.getArtifactRef()));
            }
        }
        return output;
    }

    public TArtifactDefinition convertArtifactReference(QName ref) {
        if (Objects.isNull(ref)) return null;
        return convert(new ArtifactTemplateId(ref));
    }

    public TArtifactDefinition convert(ArtifactTemplateId id) {
        TArtifactTemplate node = repository.getElement(id);
//        List<String> files = Optional.ofNullable(repository.getContainedFiles(new ArtifactTemplateFilesDirectoryId(id)))
//            .orElse(new TreeSet<>())
//            .stream()
//            .map(ref -> {
//                try {
//                    InputStream inputStream = repository.newInputStream(ref);
//                    Path path = this.path.resolve(id.getGroup())
//                        .resolve(id.getNamespace().getEncoded())
//                        .resolve(node.getIdFromIdOrNameField())
//                        .resolve(ref.getFileName());
//                    if (!path.toFile().exists()) {
//                        //noinspection ResultOfMethodCallIgnored
//                        path.getParent().toFile().mkdirs();
//                        Files.copy(inputStream, path);
//                    }
//                    return this.path.relativize(path).toString();
//                } catch (IOException e) {
//                    LOGGER.error("Failed to copy Artifact file", e);
//                    return null;
//                }
//            })
//            .filter(Objects::nonNull)
//            .collect(Collectors.toList());
        return convertArtifactTemplate(node);
    }

    @Deprecated
    public TArtifactDefinition convertArtifactTemplate(TArtifactTemplate node) {
        List<String> files = new ArrayList<>();
        TArtifactTemplate.ArtifactReferences artifactReferences = node.getArtifactReferences();
        if (artifactReferences != null) {
            List<TArtifactReference> artifactReferenceList = artifactReferences.getArtifactReference();
            if (artifactReferenceList != null) {
                for (TArtifactReference artifactReference : artifactReferenceList) {
                    files.add(artifactReference.getReference());
                }
            }
        }
        if (Objects.isNull(node) || Objects.isNull(node.getType()))
            return null;
        return new TArtifactDefinition.Builder(getQName(
            new ArtifactTypeId(node.getType()),
            node.getType().getNamespaceURI(),
            node.getType().getLocalPart()
        ), files.size() > 0 ? files.get(0) : null)
            .build();
    }

    public TMapRequirementDefinition convert(org.eclipse.winery.model.tosca.TRequirementDefinition node) {
        if (Objects.isNull(node))
            return null;
        TRequirementDefinition.Builder builder = new TRequirementDefinition.Builder(node.getCapability())
            .setDescription(convertDocumentation(node.getDocumentation()))
            .setOccurrences(node.getLowerBound(), node.getUpperBound())
            .setNode(node.getNode());

        if (node.getRelationship() != null) {
            TRelationshipDefinition.Builder relationshipDefBuilder = new TRelationshipDefinition.Builder(node.getRelationship());
            builder = builder.setRelationship(relationshipDefBuilder.build());
        }

        return new TMapRequirementDefinition().setMap(
            Collections.singletonMap(
                node.getName(),
                builder.build()
            ));
    }

    public QName convert(@NonNull TRequirementType node) {
        return getQName(
            new CapabilityTypeId(node.getRequiredCapabilityType()),
            node.getRequiredCapabilityType().getNamespaceURI(),
            node.getRequiredCapabilityType().getLocalPart()
        );
    }

    public Map<String, TCapabilityDefinition> convert(org.eclipse.winery.model.tosca.TNodeType.CapabilityDefinitions node) {
        if (Objects.isNull(node) || node.getCapabilityDefinition().isEmpty()) return null;
        return node.getCapabilityDefinition().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                org.eclipse.winery.model.tosca.TCapabilityDefinition::getName,
                this::convert
            ));
    }

    public TCapabilityDefinition convert(org.eclipse.winery.model.tosca.TCapabilityDefinition node) {
        return new TCapabilityDefinition.Builder(
            convert(
                node.getCapabilityType(),
                new CapabilityTypeId(node.getCapabilityType())
            ))
            .setValidSourceTypes(node.getValidSourceTypes())
            .setDescription(convertDocumentation(node.getDocumentation()))
            .setOccurrences(node.getLowerBound(), node.getUpperBound())
            .build();
    }

    public QName convert(QName node, DefinitionsChildId id) {
        if (Objects.isNull(node)) return null;
        return getQName(
            id,
            node.getNamespaceURI(),
            node.getLocalPart()
        );
    }

    public Map<String, TInterfaceDefinition> convert(TInterfaces node, String type) {
        if (Objects.isNull(node)) return null;
        return node.getInterface().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                TInterface::getName,
                entry -> new TInterfaceDefinition.Builder()
                    .setType(new QName(type))
                    .addOperations(convertOperations(entry.getOperation(), new ArrayList<>()))
                    .build()
            ));
    }

    public Map<String, TInterfaceDefinition> convert(TInterfaces node) {
        if (Objects.isNull(node)) return null;
        return node.getInterface().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                TInterface::getName,
                entry -> new TInterfaceDefinition.Builder()
                    .addOperations(convertOperations(entry.getOperation(), new ArrayList<>()))
                    .build()
            ));
    }

    public Map<String, TPropertyAssignmentOrDefinition> convert(TOperation.InputParameters node) {
        if (Objects.isNull(node)) return null;
        return node.getInputParameter().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                TParameter::getName,
                entry -> new TPropertyDefinition.Builder(convertType(entry.getType()))
                    .setRequired(convert(entry.getRequired()))
                    .build()
            ));
    }

    public Map<String, TPropertyAssignmentOrDefinition> convert(TOperation.OutputParameters node) {
        if (Objects.isNull(node)) return null;
        return node.getOutputParameter().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                TParameter::getName,
                entry -> new TPropertyDefinition.Builder(convertType(entry.getType()))
                    .setRequired(convert(entry.getRequired()))
                    .build()
            ));
    }

    public boolean convert(TBoolean node) {
        return Objects.nonNull(node) && node.equals(TBoolean.YES);
    }

    private QName convertType(String type) {
        return TypeConverter.INSTANCE.convert(type);
    }

    public Map<String, TArtifactDefinition> convert(TArtifacts node) {
        if (Objects.isNull(node))
            return null;

        if (Objects.isNull(node.getArtifact()))
            return new HashMap<>();

        return node.getArtifact().stream()
            .filter(Objects::nonNull)
            .map(this::convert)
            .filter(Objects::nonNull)
            .flatMap(map -> map.entrySet().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, TCapabilityAssignment> convert(org.eclipse.winery.model.tosca.TNodeTemplate.Capabilities node) {
        if (Objects.isNull(node)) return null;
        return node.getCapability().stream()
            .filter(Objects::nonNull)
            .map(this::convert)
            .filter(Objects::nonNull)
            .flatMap(map -> map.entrySet().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, TArtifactDefinition> convert(TArtifact node) {
        if (Objects.isNull(node)) return null;

        return Collections.singletonMap(
            node.getName(),
            new TArtifactDefinition.Builder(this.convert(node.getType(), new ArtifactTypeId(node.getType())), node.getFile())
                .setDescription(node.getDescription())
                .setDeployPath(node.getDeployPath())
                .build()
        );
    }

    public Map<String, TInterfaceType> convert(org.eclipse.winery.model.tosca.TInterfaceType node) {
        if (Objects.isNull(node)) return null;

        Map<String, TOperationDefinition> ops = new HashMap<>();
        node.getOperations().forEach((key, value) -> ops.putAll(convert(value)));

        return Collections.singletonMap(
            node.getName(),
            new TInterfaceType.Builder()
                .setDescription(node.getDescription())
                .setOperations(ops)
                .setDerivedFrom(convert(node.getDerivedFrom(), node.getClass()))
                .build()
        );
    }

    public Map<String, TCapabilityAssignment> convert(TCapability node) {
        if (Objects.isNull(node)) return null;

        // skip empty capability assignments
        if (node.getProperties() == null || node.getProperties().getKVProperties() == null || node.getProperties().getKVProperties().size() == 0) {
            return null;
        }

        return Collections.singletonMap(
            node.getName(),
            new TCapabilityAssignment.Builder()
                .setProperties(convert(node.getProperties()))
                .build()
        );
    }

    public List<TMapRequirementAssignment> convert(org.eclipse.winery.model.tosca.TNodeTemplate.Requirements node) {
        if (Objects.isNull(node)) return null;
        return node.getRequirement().stream()
            .filter(Objects::nonNull)
            .map(this::convert)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public TMapRequirementAssignment convert(TRequirement node) {
        if (Objects.isNull(node)) return null;
        TRequirementAssignment.Builder builder = new TRequirementAssignment.Builder();
        // here we assume the assignment to include template names only (no types!)
        // todo make s more generic TRequirement conversion
        // todo allow changing occurrences in TRequirement in topology modeler
        // todo allow passing relationship assignment parameters

        if (node.getCapability() != null) {
            builder = builder.setCapability(QName.valueOf(node.getCapability()));
        }

        if (node.getNode() != null) {
            builder = builder.setNode(QName.valueOf(node.getNode()));
        }

        if (node.getRelationship() != null) {
            builder = builder.setRelationship(new TRelationshipAssignment.Builder(QName.valueOf(node.getRelationship())).build());
        }

        return new TMapRequirementAssignment().setMap(Collections.singletonMap(
            node.getName(),
            builder.build()
        ));
    }

    private Map<String, TPolicyDefinition> convert(TPolicies node) {
        if (Objects.isNull(node)) return null;
        return node.getPolicy().stream()
            .filter(Objects::nonNull)
            .map(this::convert)
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private <T, K> Map<String, K> convert(List<T> nodes) {
        if (Objects.isNull(nodes)) return null;
        return nodes.stream()
            .filter(Objects::nonNull)
            .flatMap(node -> {
                if (node instanceof org.eclipse.winery.model.tosca.TRelationshipTemplate) {
                    return convert((org.eclipse.winery.model.tosca.TRelationshipTemplate) node).entrySet().stream();
                } else if (node instanceof org.eclipse.winery.model.tosca.TArtifactType) {
                    return convert((org.eclipse.winery.model.tosca.TArtifactType) node).entrySet().stream();
                } else if (node instanceof org.eclipse.winery.model.tosca.TCapabilityType) {
                    return convert((org.eclipse.winery.model.tosca.TCapabilityType) node).entrySet().stream();
                } else if (node instanceof org.eclipse.winery.model.tosca.TRelationshipType) {
                    return convert((org.eclipse.winery.model.tosca.TRelationshipType) node).entrySet().stream();
                } else if (node instanceof org.eclipse.winery.model.tosca.TNodeType) {
                    return convert((org.eclipse.winery.model.tosca.TNodeType) node).entrySet().stream();
                } else if (node instanceof org.eclipse.winery.model.tosca.TPolicyType) {
                    return convert((org.eclipse.winery.model.tosca.TPolicyType) node).entrySet().stream();
                } else if (node instanceof TPolicy) {
                    return convert((TPolicy) node).entrySet().stream();
                } else if (node instanceof ParameterDefinition) {
                    return convert((ParameterDefinition) node).entrySet().stream();
                } else if (node instanceof org.eclipse.winery.model.tosca.TInterfaceDefinition) {
                    return convert((org.eclipse.winery.model.tosca.TInterfaceDefinition) node).entrySet().stream();
                } else if (node instanceof org.eclipse.winery.model.tosca.TOperationDefinition) {
                    return convert((org.eclipse.winery.model.tosca.TOperationDefinition) node).entrySet().stream();
                } else if (node instanceof org.eclipse.winery.model.tosca.TArtifact) {
                    return convert((org.eclipse.winery.model.tosca.TArtifact) node).entrySet().stream();
                } else if (node instanceof org.eclipse.winery.model.tosca.TInterfaceType) {
                    return convert((org.eclipse.winery.model.tosca.TInterfaceType) node).entrySet().stream();
                }
                throw new AssertionError();
            })
            .peek(entry -> LOGGER.debug("entry: {}", entry))
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> (K) entry.getValue()));
    }

    private Map<String, TParameterDefinition> convert(ParameterDefinition node) {
        if (Objects.isNull(node)) return new HashMap<>();
        return Collections.singletonMap(
            node.getKey(),
            new TParameterDefinition.Builder()
                .setType(node.getType())
                .setDescription(node.getDescription())
                .setRequired(node.getRequired())
                .setDefault(node.getDefaultValue())
                .setValue(node.getValue())
                .build()
        );
    }

    private Map<String, TInterfaceDefinition> convert(org.eclipse.winery.model.tosca.TInterfaceDefinition node) {
        if (Objects.isNull(node)) return new HashMap<>();
        return Collections.singletonMap(
            node.getName(),
            new TInterfaceDefinition.Builder<>()
                .setType(node.getType())
                .setInputs(convert(node.getInputs()))
                .setOperations(convert(node.getOperations()))
                .build());
    }

    private Map<String, TOperationDefinition> convert(org.eclipse.winery.model.tosca.TOperationDefinition node) {
        if (Objects.isNull(node)) return new HashMap<>();
        return Collections.singletonMap(
            node.getName(),
            new TOperationDefinition.Builder()
                .setDescription(node.getDescription())
                .setInputs(convert(node.getInputs()))
                .setOutputs(convert(node.getOutputs()))
                .setImplementation(convert(node.getImplementation()))
                .build());
    }

    @Nullable
    private TImplementation convert(org.eclipse.winery.model.tosca.TImplementation node) {
        if (Objects.isNull(node)) return null;
        TImplementation implementation = new TImplementation();
        implementation.setPrimaryArtifactName(node.getPrimary());
        implementation.setDependencyArtifactNames(node.getDependencies());
        implementation.setOperationHost(node.getOperationHost());
        implementation.setTimeout(node.getTimeout());
        return implementation;
    }

    private String getNamespacePrefix(String uri) {
        if (!prefixNamespace.containsValue(uri)) {
            String prefix = repository.getNamespaceManager().getPrefix(uri);
            if ("tosca".equals(prefix) && !uri.equals(Namespaces.TOSCA_NS)) prefix = prefix.concat("_xml");
            prefixNamespace.put(prefix, uri);
        }
        return prefixNamespace.inverse().get(uri);
    }

    private QName getQName(DefinitionsChildId id, String namespaceURI, String localPart) {
        setImportDefinition(id);
        return new QName(
            namespaceURI,
            localPart,
            this.getNamespacePrefix(namespaceURI)
        );
    }

    private void setImportDefinition(DefinitionsChildId id) {
        this.importDefinitions.put(
            id, repository.getDefinitions(id)
        );
    }

    private TNodeTypeImplementation getNodeTypeImplementation(QName nodeType) {
        return repository.getAllDefinitionsChildIds(NodeTypeImplementationId.class)
            .stream()
            .map(repository::getElement)
            .filter(entry -> entry.getNodeType().equals(nodeType))
            .findAny().orElse(new TNodeTypeImplementation());
    }

    private TRelationshipTypeImplementation getRelationshipTypeImplementation(QName relationshipType) {
        return repository.getAllDefinitionsChildIds(RelationshipTypeImplementationId.class)
            .stream()
            .map(repository::getElement)
            .filter(entry -> entry.getRelationshipType().equals(relationshipType))
            .findAny().orElse(new TRelationshipTypeImplementation());
    }

    private String getFullName(org.eclipse.winery.model.tosca.TEntityType node) {
        String nodeFullName = node.getIdFromIdOrNameField();
        if (node.getTargetNamespace() != null && !nodeFullName.contains(node.getTargetNamespace())) {
            nodeFullName = node.getTargetNamespace().concat(".").concat(node.getIdFromIdOrNameField());
        }
        return nodeFullName;
    }
}

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
package org.eclipse.winery.repository.yaml.converter;

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
import org.eclipse.winery.model.converter.support.Namespaces;
import org.eclipse.winery.model.converter.support.xml.TypeConverter;
import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.model.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.InterfaceTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.PolicyTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.tosca.TAppliesTo;
import org.eclipse.winery.model.tosca.TArtifact;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TArtifactType;
import org.eclipse.winery.model.tosca.TArtifacts;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TDataType;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TGroupDefinition;
import org.eclipse.winery.model.tosca.TGroupType;
import org.eclipse.winery.model.tosca.TImplementation;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.TInterfaceType;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TOperationDefinition;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipTypeImplementation;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.model.tosca.TSchema;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.kvproperties.AttributeDefinition;
import org.eclipse.winery.model.tosca.extensions.kvproperties.ConstraintClauseKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.ParameterDefinition;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.extensions.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.yaml.YTArtifactDefinition;
import org.eclipse.winery.model.tosca.yaml.YTArtifactType;
import org.eclipse.winery.model.tosca.yaml.YTAttributeDefinition;
import org.eclipse.winery.model.tosca.yaml.YTCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.YTCapabilityDefinition;
import org.eclipse.winery.model.tosca.yaml.YTCapabilityType;
import org.eclipse.winery.model.tosca.yaml.YTConstraintClause;
import org.eclipse.winery.model.tosca.yaml.YTDataType;
import org.eclipse.winery.model.tosca.yaml.YTEntityType;
import org.eclipse.winery.model.tosca.yaml.YTGroupDefinition;
import org.eclipse.winery.model.tosca.yaml.YTGroupType;
import org.eclipse.winery.model.tosca.yaml.YTImplementation;
import org.eclipse.winery.model.tosca.yaml.YTImportDefinition;
import org.eclipse.winery.model.tosca.yaml.YTInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.YTInterfaceType;
import org.eclipse.winery.model.tosca.yaml.YTNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.YTNodeType;
import org.eclipse.winery.model.tosca.yaml.YTOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.YTParameterDefinition;
import org.eclipse.winery.model.tosca.yaml.YTPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.YTPolicyType;
import org.eclipse.winery.model.tosca.yaml.YTPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.YTPropertyAssignmentOrDefinition;
import org.eclipse.winery.model.tosca.yaml.YTPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipAssignment;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipDefinition;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipTemplate;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipType;
import org.eclipse.winery.model.tosca.yaml.YTRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.YTRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.YTSchemaDefinition;
import org.eclipse.winery.model.tosca.yaml.YTServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.YTSubstitutionMappings;
import org.eclipse.winery.model.tosca.yaml.YTTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.support.Defaults;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.YTMapImportDefinition;
import org.eclipse.winery.model.tosca.yaml.support.YTMapRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.support.YTMapRequirementDefinition;
import org.eclipse.winery.repository.yaml.YamlRepository;
import org.eclipse.winery.repository.yaml.export.YamlExporter;

import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FromCanonical {
    public final static Logger LOGGER = LoggerFactory.getLogger(FromCanonical.class);

    private final YamlRepository repository;

    private HashBiMap<String, String> prefixNamespace;
    private Map<DefinitionsChildId, TDefinitions> importDefinitions;

    public FromCanonical(YamlRepository repository) {
        this.repository = repository;
        this.prefixNamespace = new HashBiMap<>();
        this.importDefinitions = new LinkedHashMap<>();
    }

    @NonNull
    public YTServiceTemplate convert(TDefinitions node) {
        return convert(node, false);
    }

    /**
     * Converts canonical TDefinitions to a TOSCA YAML ServiceTemplate
     */
    @NonNull
    public YTServiceTemplate convert(TDefinitions node, boolean convertImports) {
        LOGGER.debug("Convert TServiceTemplate: {}", node.getIdFromIdOrNameField());

        YTServiceTemplate.Builder builder = new YTServiceTemplate.Builder(Defaults.TOSCA_DEFINITIONS_VERSION)
            .setDescription(convertDocumentation(node.getElement().getDocumentation()))
            .setArtifactTypes(convert(node.getArtifactTypes()))
            .setCapabilityTypes(convert(node.getCapabilityTypes()))
            .setRelationshipTypes(convert(node.getRelationshipTypes()))
            .setNodeTypes(convert(node.getNodeTypes()))
            .setPolicyTypes(convert(node.getPolicyTypes()))
            .setInterfaceTypes(convert(node.getInterfaceTypes()))
            .setDataTypes(convert(node.getDataTypes()))
            .setGroupTypes(convert(node.getGroupTypes()));

        if (node.getServiceTemplates().size() == 1) {
            builder.setTopologyTemplate(convert(node.getServiceTemplates().get(0)));
            builder.addMetadata("targetNamespace", node.getTargetNamespace());
        }

        if (convertImports) {
            List<YTMapImportDefinition> imports = convertImports();
            YTMapImportDefinition existingImports = prepareExistingImports(node.getImportDefinitions());
            if (Objects.nonNull(imports)) {
                imports.stream().findFirst().ifPresent(def -> def.putAll(existingImports));
            } else if (!existingImports.isEmpty()) {
                imports = new ArrayList<>();
                imports.add(existingImports);
            }
            builder.setImports(imports);
        }

        return builder.build();
    }

    private YTMapImportDefinition prepareExistingImports(Map<String, QName> importDefinitions) {
        YTMapImportDefinition tMapImportDefinition = new YTMapImportDefinition();
        importDefinitions.forEach((key, value) -> {
            YTImportDefinition tImportDefinition =
                new YTImportDefinition.Builder(key)
                    .setNamespacePrefix(getNamespacePrefix(value.getNamespaceURI()))
                    .setNamespaceUri(value.getNamespaceURI())
                    .build();
            tMapImportDefinition.put(value.getLocalPart(), tImportDefinition);
        });

        return tMapImportDefinition;
    }

    public List<YTMapImportDefinition> convertImports() {
        List<YTMapImportDefinition> imports = new ArrayList<>();
        YTMapImportDefinition tMapImportDefinition = new YTMapImportDefinition();
        for (Map.Entry<DefinitionsChildId, TDefinitions> importDefinition : importDefinitions.entrySet()) {
            YTImportDefinition tImportDefinition =
                new YTImportDefinition.Builder(YamlExporter.getDefinitionsName(repository, importDefinition.getKey())
                    .concat(Constants.SUFFIX_TOSCA_DEFINITIONS))
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

    public Map<String, YTPropertyAssignment> convert(TEntityTemplate.Properties node) {
        if (Objects.isNull(node)) {
            return null;
        }
        if (node instanceof TEntityTemplate.YamlProperties) {
            Map<String, Object> propertiesKV = ((TEntityTemplate.YamlProperties) node).getProperties();
            Map<String, YTPropertyAssignment> assignments = propertiesKV.entrySet().stream()
                .map(entry ->
                    new LinkedHashMap.SimpleEntry<>(
                        String.valueOf(entry.getKey()),
                        PropertyConverter.convert(entry.getValue())
                    )
                )
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
                ));
            return assignments;
        }
        // FIXME deal with converting WineryKVProperties and XmlProperties
        return null;
    }

    public YTTopologyTemplateDefinition convert(TServiceTemplate node) {
        // substitution mappings are an extension feature and currently not supported for YAML
        if (Objects.isNull(node)) {
            return null;
        }
        TTopologyTemplate topologyTemplate = node.getTopologyTemplate();
        if (Objects.isNull(topologyTemplate)) {
            return null;
        }
        YTTopologyTemplateDefinition.Builder builder = new YTTopologyTemplateDefinition.Builder()
            .setDescription(convertDocumentation(topologyTemplate.getDocumentation()))
            .setNodeTemplates(convert(topologyTemplate.getNodeTemplates(), topologyTemplate.getRelationshipTemplates()))
            .setRelationshipTemplates(convert(topologyTemplate.getRelationshipTemplates()))
            .setPolicies(convert(topologyTemplate.getPolicies()))
            .setGroups(convert(topologyTemplate.getGroups()));
        if (topologyTemplate.getInputs() != null) {
            builder.setInputs(convert(topologyTemplate.getInputs()));
        }
        if (topologyTemplate.getOutputs() != null) {
            builder.setOutputs(convert(topologyTemplate.getOutputs()));
        }
        return builder.build();
    }

    public Map<String, YTNodeTemplate> convert(List<TNodeTemplate> nodes, List<TRelationshipTemplate> rTs) {
        if (Objects.isNull(nodes)) {
            return null;
        }
        return nodes.stream()
            .filter(Objects::nonNull)
            .flatMap(entry -> convert(entry, Optional.ofNullable(rTs).orElse(new ArrayList<>())).entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NonNull
    public Map<String, YTNodeTemplate> convert(TNodeTemplate node, @NonNull List<TRelationshipTemplate> rTs) {
        if (Objects.isNull(node)) {
            return new LinkedHashMap<>();
        }
        Metadata meta = new Metadata();
        if (Objects.nonNull(node.getX()) && Objects.nonNull(node.getY())) {
            meta.add(org.eclipse.winery.model.converter.support.Defaults.X_COORD, node.getX());
            meta.add(org.eclipse.winery.model.converter.support.Defaults.Y_COORD, node.getY());
        }

        if (Objects.nonNull(node.getName())) {
            meta.add(org.eclipse.winery.model.converter.support.Defaults.DISPLAY_NAME, node.getName());
        }

        return Collections.singletonMap(
            node.getIdFromIdOrNameField(),
            new YTNodeTemplate.Builder(
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
    public Map<String, YTRelationshipTemplate> convert(TRelationshipTemplate node) {
        if (Objects.isNull(node)) {
            return new LinkedHashMap<>();
        }
        return Collections.singletonMap(
            node.getIdFromIdOrNameField(),
            new YTRelationshipTemplate.Builder(convert(node.getType(), new RelationshipTypeId(node.getType())))
                .setProperties(convert(node.getProperties()))
                .build()
        );
    }

    public <T extends YTEntityType.Builder<T>> T convert(TEntityType node, T builder, Class<? extends TEntityType> clazz) {
        // ensure that the targetNamespace is always set
        if (Objects.isNull(node.getTargetNamespace()) || node.getTargetNamespace().isEmpty()) {
            String id = node.getIdFromIdOrNameField();
            node.setTargetNamespace(id.substring(0, id.lastIndexOf(".")));
        }

        builder
            .setDerivedFrom(convert(node.getDerivedFrom(), clazz))
            .setMetadata(convert(node.getTags()))
            .addMetadata("targetNamespace", node.getTargetNamespace())
            .addMetadata("abstract", node.getAbstract() ? "true" : "false")
            .addMetadata("final", node.getFinal() ? "true" : "false")
            .setAttributes(convertAttributes(node, node.getAttributeDefinitions()))
            .setDescription(convertDocumentation(node.getDocumentation()));

        if (node.getProperties() != null) {
            if (node.getProperties() instanceof TEntityType.YamlPropertiesDefinition) {
                builder.setProperties(convertYamlProperties((TEntityType.YamlPropertiesDefinition) node.getProperties()));
            } else if (node.getProperties() instanceof WinerysPropertiesDefinition) {
                builder.setProperties(convertWinerysProperties((WinerysPropertiesDefinition) node.getProperties()));
            } else {
                LOGGER.warn("Attempting to convert XML-based properties definition on type {} to YAML definitions", node.getQName());
            }
        }

        return builder;
    }

    private Map<String, YTPropertyDefinition> convertWinerysProperties(WinerysPropertiesDefinition properties) {
        return properties.getPropertyDefinitions().stream()
            .collect(Collectors.toMap(
                PropertyDefinitionKV::getKey,
                entry -> new YTPropertyDefinition.Builder(convertType(entry.getType()))
                    .setRequired(entry.isRequired())
                    .setDefault(entry.getDefaultValue())
                    .setDescription(entry.getDescription())
                    .addConstraints(convertConstraints(entry.getConstraints()))
                    .build()
            ));
    }

    private Map<String, YTPropertyDefinition> convertYamlProperties(TEntityType.YamlPropertiesDefinition properties) {
        return properties.getProperties().stream()
            .collect(Collectors.toMap(
                TEntityType.YamlPropertyDefinition::getName,
                this::convert
            ));
    }

    private YTPropertyDefinition convert(TEntityType.YamlPropertyDefinition canonical) {
        YTPropertyDefinition.Builder builder = new YTPropertyDefinition.Builder(canonical.getType());
        builder.setConstraints(convertConstraints(canonical.getConstraints()));
        builder.setDefault(canonical.getDefaultValue());
        builder.setDescription(canonical.getDescription());
        if (canonical.getEntrySchema() != null) {
            builder.setEntrySchema(convert(canonical.getEntrySchema()));
        }
        if (canonical.getKeySchema() != null) {
            builder.setKeySchema(convert(canonical.getKeySchema()));
        }
        builder.setRequired(canonical.getRequired());
        builder.setStatus(canonical.getStatus().toString());
        return builder.build();
    }

    @Nullable
    private YTSchemaDefinition convert(@Nullable TSchema canonical) {
        if (canonical == null) {
            return null;
        }
        YTSchemaDefinition.Builder builder = new YTSchemaDefinition.Builder(canonical.getType());
        builder.setDescription(canonical.getDescription());
        builder.setConstraints(convertConstraints(canonical.getConstraints()));
        builder.setKeySchema(convert(canonical.getKeySchema()));
        builder.setEntrySchema(convert(canonical.getEntrySchema()));
        return builder.build();
    }

    public Map<String, YTAttributeDefinition> convertAttributes(TEntityType node, @Nullable List<AttributeDefinition> attributes) {
        if (Objects.isNull(node) || Objects.isNull(attributes)) {
            return new HashMap<>();
        }
        return attributes.stream().collect(Collectors.toMap(
            AttributeDefinition::getKey,
            entry -> new YTAttributeDefinition.Builder(entry.getType())
                .setDescription(entry.getDescription())
                .setDefault(entry.getDefaultValue())
                .build()
        ));
    }

    public List<YTConstraintClause> convertConstraints(List<ConstraintClauseKV> constraints) {
        if (Objects.isNull(constraints)) {
            return null;
        }

        List<YTConstraintClause> list = new ArrayList<>();
        constraints.forEach(entry -> {
            YTConstraintClause.Builder builder = new YTConstraintClause.Builder();
            builder.setKey(entry.getKey());
            builder.setValue(entry.getValue());
            builder.setList(entry.getList());
            list.add(builder.build());
        });
        return list;
    }

    public Map<String, YTArtifactType> convert(TArtifactType node) {
        YTArtifactType.Builder builder = new YTArtifactType.Builder()
            .setMimeType(node.getMimeType())
            .setFileExt(node.getFileExtensions());
        String nodeFullName = this.getFullName(node);
        return Collections.singletonMap(
            nodeFullName,
            convert(node, builder, TArtifactType.class).build()
        );
    }

    public Map<String, YTNodeType> convert(TNodeType node) {
        if (Objects.isNull(node)) {
            return null;
        }

        String nodeFullName = this.getFullName(node);

        return Collections.singletonMap(
            nodeFullName,
            convert(node, new YTNodeType.Builder(), TNodeType.class)
                .setRequirements(convert(node.getRequirementDefinitions()))
                .setCapabilities(convert(node.getCapabilityDefinitions()))
                .setInterfaces(convert(node.getInterfaceDefinitions()))
                .setArtifacts(convert(node.getArtifacts()))
                .build()
        );
    }

    public Map<String, YTArtifactDefinition> convert(TNodeTypeImplementation node, Map<String, YTArtifactDefinition> artifacts) {
        if (Objects.isNull(node)) {
            return null;
        }
        String suffix = "@" + node.getNodeType().getLocalPart() + "@" + "nodetypes";
        return Stream.of(convert(node.getDeploymentArtifacts(), artifacts), convert(node.getImplementationArtifacts(), artifacts))
            .filter(Objects::nonNull)
            .flatMap(entry -> entry.entrySet().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, YTRelationshipType> convert(TRelationshipType node) {
        if (Objects.isNull(node)) {
            return null;
        }
        String nodeFullName = this.getFullName(node);
        return Collections.singletonMap(
            nodeFullName,
            convert(node, new YTRelationshipType.Builder(), TRelationshipType.class)
                .addInterfaces(convert(node.getInterfaceDefinitions()))
                .setValidTargetTypes(node.getValidTargetList())
                .build()
        );
    }

    public Map<String, YTInterfaceDefinition> convert(TInterfaces node, TRelationshipTypeImplementation implementation) {
        if (Objects.isNull(node)) {
            return null;
        }
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
    public Map<String, YTCapabilityType> convert(TCapabilityType node) {
        if (Objects.isNull(node)) return new LinkedHashMap<>();
        String nodeFullName = this.getFullName(node);
        return Collections.singletonMap(
            nodeFullName,
            convert(node, new YTCapabilityType.Builder(), TCapabilityType.class)
                .addValidSourceTypes(node.getValidNodeTypes())
                .build()
        );
    }

    @NonNull
    public Map<String, YTPolicyType> convert(TPolicyType node) {
        if (Objects.isNull(node)) {
            return new LinkedHashMap<>();
        }
        YTPolicyType.Builder builder = new YTPolicyType.Builder();

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
            convert(node, builder, TPolicyType.class)
                .build()
        );
    }

    @NonNull
    public Map<String, YTGroupType> convert(TGroupType node) {
        if (Objects.isNull(node)) {
            return new LinkedHashMap<>();
        }
        YTGroupType.Builder builder = new YTGroupType.Builder()
            .setMembers(node.getMembers());
        String nodeFullName = getFullName(node);
        return Collections.singletonMap(
            nodeFullName,
            convert(node, builder, TGroupType.class).build()
        );
    }

    public YTSubstitutionMappings convert(TBoundaryDefinitions node) {
        if (Objects.isNull(node)) {
            return null;
        }
        return new YTSubstitutionMappings.Builder()
            // TODO Convert Boundary definitions
            .build();
    }

    @NonNull
    public Map<String, YTPolicyDefinition> convert(TPolicy node) {
        if (Objects.isNull(node)) {
            return new LinkedHashMap<>();
        }
        return Collections.singletonMap(
            node.getName(),
            new YTPolicyDefinition.Builder(convert(node.getPolicyType(), new PolicyTypeId(node.getPolicyType())))
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
        if (Objects.isNull(node)) {
            return null;
        }
        DefinitionsChildId id;
        if (clazz.equals(TNodeType.class)) {
            id = new NodeTypeId(node.getTypeRef());
        } else if (clazz.equals(TRelationshipType.class)) {
            id = new RelationshipTypeId(node.getTypeRef());
        } else if (clazz.equals(TRequirementType.class)) {
            id = new RequirementTypeId(node.getTypeRef());
        } else if (clazz.equals(TCapabilityType.class)) {
            id = new CapabilityTypeId(node.getTypeRef());
        } else if (clazz.equals(TArtifactType.class)) {
            id = new ArtifactTypeId(node.getTypeRef());
        } else if (clazz.equals(TInterfaceType.class)) {
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
        if (Objects.isNull(node)) {
            return null;
        }
        return node.getTag().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                TTag::getName,
                TTag::getValue,
                (a, b) -> a + "|" + b,
                Metadata::new));
    }

    @Deprecated
    public Map<String, YTInterfaceDefinition> convert(TInterface node) {
        if (Objects.isNull(node)) {
            return new LinkedHashMap<>();
        }
        return Collections.singletonMap(
            node.getName(),
            new YTInterfaceDefinition.Builder<>()
                .setOperations(convertOperations(node.getOperation()))
                .build()
        );
    }

    @Deprecated
    public Map<String, YTOperationDefinition> convertOperations(List<TOperation> nodes) {
        if (Objects.isNull(nodes)) {
            return null;
        }
        return nodes.stream()
            .filter(Objects::nonNull)
            .flatMap(node -> convert(node)
                .entrySet().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NonNull
    @Deprecated
    public Map<String, YTOperationDefinition> convert(TOperation node) {
        if (Objects.isNull(node)) {
            return new LinkedHashMap<>();
        }
        return Collections.singletonMap(
            node.getName(),
            new YTOperationDefinition.Builder().build()
        );
    }

    @Deprecated
    public Map<String, YTInterfaceDefinition> convert(TInterfaces node, TNodeTypeImplementation implementation) {
        if (Objects.isNull(node)) {
            return null;
        }
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
    public Map<String, YTInterfaceDefinition> convert(TInterface node, @NonNull List<TImplementationArtifact> impl) {
        if (Objects.isNull(node)) {
            return new LinkedHashMap<>();
        }
        return Collections.singletonMap(node.getName(), new YTInterfaceDefinition.Builder<>().build());
    }

    public Map<String, YTOperationDefinition> convertOperations(List<TOperation> nodes, @NonNull List<TImplementationArtifact> impl) {
        if (Objects.isNull(nodes)) {
            return null;
        }
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
    public Map<String, YTOperationDefinition> convert(TOperation node, List<TImplementationArtifact> impl) {
        if (Objects.isNull(node)) {
            return new LinkedHashMap<>();
        }
        return Collections.singletonMap(
            node.getName(),
            new YTOperationDefinition.Builder().build()
        );
    }

    @Deprecated
    public YTServiceTemplate convertNodeTypeImplementation(YTServiceTemplate type, TNodeTypeImplementation node) {
        if (Objects.isNull(node)) {
            return null;
        }
        YTNodeType nodeType = type.getNodeTypes().entrySet().iterator().next().getValue();
        nodeType.setArtifacts(convert(node, nodeType.getArtifacts()));
        nodeType.setInterfaces(convertInterfaces(nodeType.getInterfaces(), node.getImplementationArtifacts()));
        type.getNodeTypes().entrySet().iterator().next().setValue(nodeType);
        type.setImports(addNewImports(type.getImports()));
        return type;
    }

    @Deprecated
    public YTServiceTemplate convertRelationshipTypeImplementation(YTServiceTemplate type, TRelationshipTypeImplementation node) {
        if (Objects.isNull(node)) {
            return null;
        }
        YTRelationshipType relationshipType = type.getRelationshipTypes().entrySet().iterator().next().getValue();
        // relationshipType.setInterfaces(convertRelationshipInterfaces(relationshipType.getInterfaces(), node.getImplementationArtifacts()));
        type.getRelationshipTypes().entrySet().iterator().next().setValue(relationshipType);
        return type;
    }

    private List<YTMapImportDefinition> addNewImports(List<YTMapImportDefinition> imports) {
        List<YTMapImportDefinition> newImportsList = convertImports();
        if (newImportsList.isEmpty()) {
            return imports;
        }
        if (imports.isEmpty()) {
            return newImportsList;
        }
        YTMapImportDefinition newImports = newImportsList.get(0);
        YTMapImportDefinition existingImports = imports.get(0);
        for (Map.Entry<String, YTImportDefinition> newImport : newImports.entrySet()) {
            boolean found = false;
            for (Map.Entry<String, YTImportDefinition> existingImport : existingImports.entrySet()) {
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
    private Map<String, YTInterfaceDefinition> convertInterfaces(Map<String, YTInterfaceDefinition> interfaces, TImplementationArtifacts implementationArtifacts) {
        if (implementationArtifacts == null) {
            return interfaces;
        }
        List<TImplementationArtifacts.ImplementationArtifact> listImplArt = implementationArtifacts.getImplementationArtifact();
        for (TImplementationArtifacts.ImplementationArtifact implementationArtifact : listImplArt) {
            YTInterfaceDefinition selectedInterface = interfaces.get(implementationArtifact.getInterfaceName());
            if (selectedInterface != null) {
                YTOperationDefinition operation = selectedInterface.getOperations().get(implementationArtifact.getOperationName());
                // operation.setImplementation(convertImplementation(implementationArtifact, operation.getImplementation()));
            }
        }
        return interfaces;
    }

    public List<YTMapRequirementDefinition> convert(TNodeType.RequirementDefinitions node) {
        if (Objects.isNull(node)) {
            return null;
        }
        return node.getRequirementDefinition().stream()
            .filter(Objects::nonNull)
            .map(this::convert)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public Map<String, YTArtifactDefinition> convert(TDeploymentArtifacts node) {
        if (Objects.isNull(node)) {
            return null;
        }
        return node.getDeploymentArtifact().stream()
            .filter(Objects::nonNull)
            .map(ia -> new LinkedHashMap.SimpleEntry<>(ia.getArtifactRef().getLocalPart(), convertArtifactReference(new QName(ia.getArtifactRef().getNamespaceURI(), ia.getArtifactRef().getLocalPart()))))
            .filter(Objects::nonNull)
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, YTArtifactDefinition> convert(TDeploymentArtifacts node, Map<String, YTArtifactDefinition> artifacts) {
        if (Objects.isNull(node)) {
            return null;
        }
        Map<String, YTArtifactDefinition> output = new LinkedHashMap<>();
        for (TDeploymentArtifact deploymentArtifact : node.getDeploymentArtifact()) {
            if (artifacts.containsKey(deploymentArtifact.getArtifactRef().getLocalPart())) {
                output.put(deploymentArtifact.getArtifactRef().getLocalPart(), artifacts.get(deploymentArtifact.getArtifactRef().getLocalPart()));
            } else {
                output.put(deploymentArtifact.getArtifactRef().getLocalPart(), convertArtifactReference(deploymentArtifact.getArtifactRef()));
            }
        }

        return output;
    }

    public Map<String, YTArtifactDefinition> convert(TImplementationArtifacts node, Map<String, YTArtifactDefinition> artifacts) {
        if (Objects.isNull(node)) {
            return null;
        }
        Map<String, YTArtifactDefinition> output = new LinkedHashMap<>();
        for (TImplementationArtifact implementationArtifact : node.getImplementationArtifact()) {
            if (artifacts.containsKey(implementationArtifact.getArtifactRef().getLocalPart())) {
                output.put(implementationArtifact.getArtifactRef().getLocalPart(), artifacts.get(implementationArtifact.getArtifactRef().getLocalPart()));
            } else {
                output.put(implementationArtifact.getArtifactRef().getLocalPart(), convertArtifactReference(implementationArtifact.getArtifactRef()));
            }
        }
        return output;
    }

    public YTArtifactDefinition convertArtifactReference(QName ref) {
        if (Objects.isNull(ref)) {
            return null;
        }
        return convert(new ArtifactTemplateId(ref));
    }

    public YTArtifactDefinition convert(ArtifactTemplateId id) {
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
    public YTArtifactDefinition convertArtifactTemplate(TArtifactTemplate node) {
        if (Objects.isNull(node) || Objects.isNull(node.getType())) {
            return null;
        }
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
        return new YTArtifactDefinition.Builder(getQName(
            new ArtifactTypeId(node.getType()),
            node.getType().getNamespaceURI(),
            node.getType().getLocalPart()
        ), files.size() > 0 ? files.get(0) : null)
            .build();
    }

    public YTMapRequirementDefinition convert(TRequirementDefinition node) {
        if (Objects.isNull(node)) {
            return null;
        }
        YTRequirementDefinition.Builder builder = new YTRequirementDefinition.Builder(node.getCapability())
            .setDescription(convertDocumentation(node.getDocumentation()))
            .setOccurrences(node.getLowerBound(), node.getUpperBound())
            .setNode(node.getNode());

        if (node.getRelationship() != null) {
            YTRelationshipDefinition.Builder relationshipDefBuilder = new YTRelationshipDefinition.Builder(node.getRelationship());
            builder = builder.setRelationship(relationshipDefBuilder.build());
        }

        return new YTMapRequirementDefinition().setMap(
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

    public Map<String, YTCapabilityDefinition> convert(TNodeType.CapabilityDefinitions node) {
        if (Objects.isNull(node) || node.getCapabilityDefinition().isEmpty()) {
            return null;
        }
        return node.getCapabilityDefinition().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(TCapabilityDefinition::getName, this::convert));
    }

    public YTCapabilityDefinition convert(TCapabilityDefinition node) {
        return new YTCapabilityDefinition.Builder(convert(node.getCapabilityType(),
            new CapabilityTypeId(node.getCapabilityType())))
            .setValidSourceTypes(node.getValidSourceTypes())
            .setDescription(convertDocumentation(node.getDocumentation()))
            .setOccurrences(node.getLowerBound(), node.getUpperBound())
            .build();
    }

    public QName convert(QName node, DefinitionsChildId id) {
        if (Objects.isNull(node)) {
            return null;
        }
        return getQName(
            id,
            node.getNamespaceURI(),
            node.getLocalPart()
        );
    }

    public Map<String, YTInterfaceDefinition> convert(TInterfaces node, String type) {
        if (Objects.isNull(node)) {
            return null;
        }
        return node.getInterface().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                TInterface::getName,
                entry -> new YTInterfaceDefinition.Builder()
                    .setType(new QName(type))
                    .addOperations(convertOperations(entry.getOperation(), new ArrayList<>()))
                    .build()
            ));
    }

    public Map<String, YTInterfaceDefinition> convert(TInterfaces node) {
        if (Objects.isNull(node)) {
            return null;
        }
        return node.getInterface().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                TInterface::getName,
                entry -> new YTInterfaceDefinition.Builder()
                    .addOperations(convertOperations(entry.getOperation(), new ArrayList<>()))
                    .build()
            ));
    }

    public Map<String, YTPropertyAssignmentOrDefinition> convert(TOperation.InputParameters node) {
        if (Objects.isNull(node)) {
            return null;
        }
        return node.getInputParameter().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                TParameter::getName,
                entry -> new YTPropertyDefinition.Builder(convertType(entry.getType()))
                    .setRequired(entry.getRequired())
                    .build()
            ));
    }

    public Map<String, YTPropertyAssignmentOrDefinition> convert(TOperation.OutputParameters node) {
        if (Objects.isNull(node)) {
            return null;
        }
        return node.getOutputParameter().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                TParameter::getName,
                entry -> new YTPropertyDefinition.Builder(convertType(entry.getType()))
                    .setRequired(entry.getRequired())
                    .build()
            ));
    }

    private QName convertType(String type) {
        return TypeConverter.INSTANCE.convert(type);
    }

    public Map<String, YTArtifactDefinition> convert(TArtifacts node) {
        if (Objects.isNull(node)) {
            return null;
        }

        if (Objects.isNull(node.getArtifact())) {
            return new HashMap<>();
        }

        return node.getArtifact().stream()
            .filter(Objects::nonNull)
            .map(this::convert)
            .filter(Objects::nonNull)
            .flatMap(map -> map.entrySet().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, YTCapabilityAssignment> convert(TNodeTemplate.Capabilities node) {
        if (Objects.isNull(node)) {
            return null;
        }
        return node.getCapability().stream()
            .filter(Objects::nonNull)
            .map(this::convert)
            .filter(Objects::nonNull)
            .flatMap(map -> map.entrySet().stream())
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, YTArtifactDefinition> convert(TArtifact node) {
        if (Objects.isNull(node)) {
            return null;
        }

        return Collections.singletonMap(
            node.getName(),
            new YTArtifactDefinition.Builder(this.convert(node.getType(), new ArtifactTypeId(node.getType())), node.getFile())
                .setDescription(node.getDescription())
                .setDeployPath(node.getDeployPath())
                .build()
        );
    }

    public Map<String, YTInterfaceType> convert(TInterfaceType node) {
        if (Objects.isNull(node)) {
            return null;
        }

        Map<String, YTOperationDefinition> ops = new HashMap<>();
        node.getOperations().forEach((key, value) -> ops.putAll(convert(value)));

        return Collections.singletonMap(
            node.getName(),
            new YTInterfaceType.Builder()
                .setDescription(node.getDescription())
                .setOperations(ops)
                .setDerivedFrom(convert(node.getDerivedFrom(), node.getClass()))
                .build()
        );
    }

    public Map<String, YTCapabilityAssignment> convert(TCapability node) {
        if (Objects.isNull(node)) {
            return null;
        }

        // skip empty capability assignments
        if (node.getProperties() == null) {
            return null;
        }

        return Collections.singletonMap(
            node.getName(),
            new YTCapabilityAssignment.Builder()
                .setProperties(convert(node.getProperties()))
                .build()
        );
    }

    public List<YTMapRequirementAssignment> convert(TNodeTemplate.Requirements node) {
        if (Objects.isNull(node)) {
            return null;
        }
        return node.getRequirement().stream()
            .filter(Objects::nonNull)
            .map(this::convert)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public YTMapRequirementAssignment convert(TRequirement node) {
        if (Objects.isNull(node)) {
            return null;
        }
        YTRequirementAssignment.Builder builder = new YTRequirementAssignment.Builder();
        // here we assume the assignment to include template names only (no types!)
        // todo make a more generic TRequirement conversion
        // todo allow changing occurrences in TRequirement in topology modeler
        // todo allow passing relationship assignment parameters

        if (node.getCapability() != null) {
            builder = builder.setCapability(QName.valueOf(node.getCapability()));
        }

        if (node.getNode() != null) {
            builder = builder.setNode(QName.valueOf(node.getNode()));
        }

        if (node.getRelationship() != null) {
            builder = builder.setRelationship(new YTRelationshipAssignment.Builder(QName.valueOf(node.getRelationship())).build());
        }

        return new YTMapRequirementAssignment().setMap(Collections.singletonMap(
            node.getName(),
            builder.build()
        ));
    }

    private Map<String, YTPolicyDefinition> convert(TPolicies node) {
        if (Objects.isNull(node)) {
            return null;
        }
        return node.getPolicy().stream()
            .filter(Objects::nonNull)
            .map(this::convert)
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private <T, K> Map<String, K> convert(List<T> nodes) {
        if (Objects.isNull(nodes)) {
            return null;
        }
        return nodes.stream()
            .filter(Objects::nonNull)
            .flatMap(node -> {
                if (node instanceof TRelationshipTemplate) {
                    return convert((TRelationshipTemplate) node).entrySet().stream();
                } else if (node instanceof TArtifactType) {
                    return convert((TArtifactType) node).entrySet().stream();
                } else if (node instanceof TCapabilityType) {
                    return convert((TCapabilityType) node).entrySet().stream();
                } else if (node instanceof TRelationshipType) {
                    return convert((TRelationshipType) node).entrySet().stream();
                } else if (node instanceof TNodeType) {
                    return convert((TNodeType) node).entrySet().stream();
                } else if (node instanceof TPolicyType) {
                    return convert((TPolicyType) node).entrySet().stream();
                } else if (node instanceof TPolicy) {
                    return convert((TPolicy) node).entrySet().stream();
                } else if (node instanceof ParameterDefinition) {
                    return convert((ParameterDefinition) node).entrySet().stream();
                } else if (node instanceof TInterfaceDefinition) {
                    return convert((TInterfaceDefinition) node).entrySet().stream();
                } else if (node instanceof TOperationDefinition) {
                    return convert((TOperationDefinition) node).entrySet().stream();
                } else if (node instanceof TArtifact) {
                    return convert((TArtifact) node).entrySet().stream();
                } else if (node instanceof TInterfaceType) {
                    return convert((TInterfaceType) node).entrySet().stream();
                } else if (node instanceof TDataType) {
                    return convert((TDataType) node).entrySet().stream();
                } else if (node instanceof TGroupDefinition) {
                    return convert((TGroupDefinition) node).entrySet().stream();
                } else if (node instanceof TGroupType) {
                    return convert((TGroupType) node).entrySet().stream();
                }
                throw new AssertionError();
            })
            .peek(entry -> LOGGER.debug("entry: {}", entry))
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> (K) entry.getValue()));
    }

    private Map<String, YTDataType> convert(TDataType node) {
        if (Objects.isNull(node)) {
            return new HashMap<>();
        }
        String nodeFullName = this.getFullName(node);        
        YTDataType.Builder builder = convert(node, new YTDataType.Builder(), TDataType.class);
        return Collections.singletonMap(
            nodeFullName,
            builder.setConstraints(convertConstraints(node.getConstraints())).build()
        );
    }

    private Map<String, YTParameterDefinition> convert(ParameterDefinition node) {
        if (Objects.isNull(node)) {
            return new HashMap<>();
        }
        return Collections.singletonMap(
            node.getKey(),
            new YTParameterDefinition.Builder()
                .setType(node.getType())
                .setDescription(node.getDescription())
                .setRequired(node.getRequired())
                .setDefault(node.getDefaultValue())
                .setValue(node.getValue())
                .build()
        );
    }

    private Map<String, YTInterfaceDefinition> convert(TInterfaceDefinition node) {
        if (Objects.isNull(node)) {
            return new HashMap<>();
        }
        return Collections.singletonMap(
            node.getName(),
            new YTInterfaceDefinition.Builder<>()
                .setType(node.getType())
                .setInputs(convert(node.getInputs()))
                .setOperations(convert(node.getOperations()))
                .build());
    }

    private Map<String, YTOperationDefinition> convert(TOperationDefinition node) {
        if (Objects.isNull(node)) {
            return new HashMap<>();
        }
        return Collections.singletonMap(
            node.getName(),
            new YTOperationDefinition.Builder()
                .setDescription(node.getDescription())
                .setInputs(convert(node.getInputs()))
                .setOutputs(convert(node.getOutputs()))
                .setImplementation(convert(node.getImplementation()))
                .build());
    }

    private Map<String, YTGroupDefinition> convert(TGroupDefinition node) {
        if (Objects.isNull(node)) {
            return new HashMap<>();
        }

        // type fallback
        if (node.getType() == null) {
            node.setType(QName.valueOf("{tosca.groups}Root"));
        }

        return Collections.singletonMap(
            node.getName(),
            new YTGroupDefinition.Builder(node.getType())
                .setDescription(node.getDescription())
                .setMembers(node.getMembers())
                .setProperties(convert(node.getProperties()))
                .build());
    }

    @Nullable
    private YTImplementation convert(TImplementation node) {
        if (Objects.isNull(node)) {
            return null;
        }
        YTImplementation.Builder builder = new YTImplementation.Builder();
        builder.setPrimaryArtifactName(node.getPrimary());
        builder.setDependencyArtifactNames(node.getDependencies());
        builder.setOperationHost(node.getOperationHost());
        builder.setTimeout(node.getTimeout());
        return builder.build();
    }

    private String getNamespacePrefix(String uri) {
        if (!prefixNamespace.containsValue(uri)) {
            String prefix = repository.getNamespaceManager().getPrefix(uri);
            if ("tosca".equals(prefix) && !uri.equals(Namespaces.TOSCA_YAML_NS)) prefix = prefix.concat("_xml");
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

    private String getFullName(TEntityType node) {
        String nodeFullName = node.getIdFromIdOrNameField();
        if (node.getTargetNamespace() != null && !nodeFullName.contains(node.getTargetNamespace())) {
            nodeFullName = node.getTargetNamespace().concat(".").concat(node.getIdFromIdOrNameField());
        }
        return nodeFullName;
    }

    private static class PropertyConverter {
        private static Object convert(String value) {
            if (value.startsWith("get_input")) {
                return String.format("{ %s }", value.trim());
            }
            return value;
        }

        public static YTPropertyAssignment convert(Object value) {
            YTPropertyAssignment.Builder builder = new YTPropertyAssignment.Builder();
            if (value instanceof String) {
                builder.setValue(convert((String) value));
                return builder.build();
            }
            if (value instanceof Map) {
                builder.setValue(
                    ((Map<String, Object>) value).entrySet().stream()
                        .map(entry ->
                            new LinkedHashMap.SimpleEntry<>(
                                String.valueOf(entry.getKey()),
                                convert(entry.getValue()))
                        )
                        .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                        ))
                );
                return builder.build();
            }
            if (value instanceof List) {
                builder.setValue(
                    ((List<Object>) value).stream()
                        .map(entry -> convert(entry))
                        .collect(Collectors.toList())
                );
                return builder.build();
            }
            // value is some kind of object that's not a collection, which we DO NOT TOUCH!
            builder.setValue(value);
            return builder.build();
        }
    }
}

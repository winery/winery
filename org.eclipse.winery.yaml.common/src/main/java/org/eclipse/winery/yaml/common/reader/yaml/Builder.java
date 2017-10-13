/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.common.reader.yaml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.TArtifactDefinition;
import org.eclipse.winery.model.tosca.yaml.TArtifactType;
import org.eclipse.winery.model.tosca.yaml.TAttributeAssignment;
import org.eclipse.winery.model.tosca.yaml.TAttributeDefinition;
import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.yaml.TCapabilityType;
import org.eclipse.winery.model.tosca.yaml.TConstraintClause;
import org.eclipse.winery.model.tosca.yaml.TDataType;
import org.eclipse.winery.model.tosca.yaml.TEntityType;
import org.eclipse.winery.model.tosca.yaml.TEntrySchema;
import org.eclipse.winery.model.tosca.yaml.TGroupDefinition;
import org.eclipse.winery.model.tosca.yaml.TGroupType;
import org.eclipse.winery.model.tosca.yaml.TImplementation;
import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceAssignment;
import org.eclipse.winery.model.tosca.yaml.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceType;
import org.eclipse.winery.model.tosca.yaml.TNodeFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TNodeType;
import org.eclipse.winery.model.tosca.yaml.TOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.TParameterDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyType;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignmentOrDefinition;
import org.eclipse.winery.model.tosca.yaml.TPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.TPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipAssignment;
import org.eclipse.winery.model.tosca.yaml.TRelationshipDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.yaml.TRelationshipType;
import org.eclipse.winery.model.tosca.yaml.TRepositoryDefinition;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.TStatusValue;
import org.eclipse.winery.model.tosca.yaml.TSubstitutionMappings;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.TVersion;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.TListString;
import org.eclipse.winery.model.tosca.yaml.support.TMapImportDefinition;
import org.eclipse.winery.model.tosca.yaml.support.TMapObject;
import org.eclipse.winery.model.tosca.yaml.support.TMapPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.tosca.datatypes.Credential;
import org.eclipse.winery.yaml.common.Defaults;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.exception.UnrecognizedFieldException;
import org.eclipse.winery.yaml.common.validator.FieldValidator;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Builder {
    public static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

    private final String namespace;
    private List<String> exceptionMessages;
    private Map<String, String> prefix2Namespace;
    private FieldValidator validator;

    public Builder(String namespace) {
        this.namespace = namespace;
        this.validator = new FieldValidator();
        this.exceptionMessages = new ArrayList<>();
    }

    private void initPrefix2Namespace(Object object) {
        this.prefix2Namespace = new LinkedHashMap<>();
        this.prefix2Namespace.put("tosca", Namespaces.TOSCA_NS);

        if (!Objects.nonNull(object)) return;

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        for (Map<String, Object> map : list) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!(entry.getValue() instanceof String)) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> importDefinition = (Map<String, Object>) entry.getValue();
                    if (importDefinition != null) {
                        String namespacePrefix = (String) importDefinition.get("namespace_prefix");
                        String namespaceUri = (String) importDefinition.get("namespace_uri");
                        if (namespacePrefix != null && namespaceUri != null) {
                            this.prefix2Namespace.put(namespacePrefix, namespaceUri);
                        }
                    }
                }
            }
        }
    }

    private <T> boolean validate(Class<T> clazz, Object object) {
        if (object instanceof Map) {
            if (!clazz.equals(TInterfaceType.class)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> fields = (Map<String, Object>) object;
                this.exceptionMessages.addAll(validator.validate(clazz, fields));
            }
            return true;
        } else {
            // TODO exception msg: Because instance (=object) is nonNull and not a map
            return false;
        }
    }

    @Nullable
    public TServiceTemplate buildServiceTemplate(Object object) throws UnrecognizedFieldException {
        if (!Objects.nonNull(object) || !validate(TServiceTemplate.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        // build map between prefix and namespaces
        initPrefix2Namespace(map.get("imports"));

        TServiceTemplate.Builder builder = new TServiceTemplate.Builder(
            map.get("tosca_definitions_version").toString()
        ).setMetadata(buildMetadata(map.get("metadata")))
            .setDescription(buildDescription(map.get("description")))
            .setDslDefinitions(buildDslDefinitions(map.get("dsl_definitions")))
            .setRepositories(buildRepositories(map.get("repositories")))
            .setImports(buildImports(map.get("imports")))
            .setArtifactTypes(buildArtifactTypes(map.get("artifact_types")))
            .setDataTypes(buildDataTypes(map.get("data_types")))
            .setCapabilityTypes(buildCapabilityTypes(map.get("capability_types")))
            .setInterfaceTypes(buildInterfaceTypes(map.get("interface_types")))
            .setRelationshipTypes(buildRelationshipTypes(map.get("relationship_types")))
            .setNodeTypes(buildNodeTypes(map.get("node_types")))
            .setGroupTypes(buildGroupTypes(map.get("group_types")))
            .setPolicyTypes(buildPolicyTypes(map.get("policy_types")))
            .setTopologyTemplate(buildTopologyTemplate(map.get("topology_template")));
        if (!this.exceptionMessages.isEmpty()) throw new UnrecognizedFieldException(this.exceptionMessages);
        return builder.build();
    }

    @Nullable
    public TTopologyTemplateDefinition buildTopologyTemplate(Object object) {
        if (!Objects.nonNull(object) || !validate(TTopologyTemplateDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TTopologyTemplateDefinition.Builder()
            .setDescription(buildDescription(map.get("description")))
            .setInputs(buildMapParameterDefinition(map.get("inputs")))
            .setNodeTemplates((buildNodeTemplates(map.get("node_templates"))))
            .setRelationshipTemplates(buildRelationshipTemplates(map.get("relationship_templates")))
            .setGroups(buildGroupDefinitions(map.get("groups")))
            .setPolicies(buildMapPolicyDefinition(map.get("policies")))
            .setOutputs(buildMapParameterDefinition(map.get("outputs")))
            .setSubstitutionMappings(buildSubstitutionMappings(map.get("substitution_mappings")))
            .build();
    }

    @Nullable
    public Metadata buildMetadata(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> tmp = (Map<String, Object>) object;
        Metadata metadata = new Metadata();
        tmp.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .forEach(entry -> {
                metadata.put(entry.getKey(), entry.getValue().toString());
                if (entry.getValue() instanceof Date)
                    metadata.put(entry.getKey(), new SimpleDateFormat("yyyy-MM-dd").format(entry.getValue()));
            });
        return metadata;
    }

    @Nullable
    public String buildDescription(Object object) {
        if (!Objects.nonNull(object)) return null;
        return object.toString();
    }

    @Nullable
    public Map<String, Object> buildDslDefinitions(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Nullable
    public Map<String, TRepositoryDefinition> buildRepositories(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildRepositoryDefinition(entry.getValue())));
    }

    @Nullable
    public TRepositoryDefinition buildRepositoryDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        if (object instanceof String) return new TRepositoryDefinition.Builder((String) object).build();
        if (!validate(TRepositoryDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRepositoryDefinition.Builder((String) map.get("url"))
            .setDescription(buildDescription(map.get("description")))
            .setCredential(buildCredential(map.get("credential")))
            .build();
    }

    @Nullable
    public Credential buildCredential(Object object) {
        if (!Objects.nonNull(object) || !validate(Credential.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        @SuppressWarnings("unchecked")
        Map<String, String> keys = (Map<String, String>) map.get("keys");
        return new Credential.Builder((String) map.get("token_type"))
            .setProtocol((String) map.get("protocol"))
            .setToken((String) map.get("token"))
            .setKeys(keys)
            .build();
    }

    @Nullable
    public List<TMapImportDefinition> buildImports(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        return list.stream()
            .filter(Objects::nonNull)
            .flatMap(map -> map.entrySet().stream())
            .filter(Objects::nonNull)
            .map(entry -> buildMapImportDefinition(entry.getKey(), entry.getValue()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    public TMapImportDefinition buildMapImportDefinition(String key, Object object) {
        if (!Objects.nonNull(object)) return null;
        TMapImportDefinition mapImportDefinition = new TMapImportDefinition();
        mapImportDefinition.put(key, buildImportDefinition(object));
        return mapImportDefinition;
    }

    @Nullable
    public TImportDefinition buildImportDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        if (object instanceof String) return new TImportDefinition.Builder((String) object).build();
        if (!validate(TImportDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TImportDefinition.Builder(map.get("file").toString())
            .setRepository(buildQName((String) map.get("repository")))
            .setNamespaceUri((String) map.get("namespace_uri"))
            .setNamespacePrefix((String) map.get("namespace_prefix"))
            .build();
    }

    @Nullable
    private QName buildQName(String name) {
        if (name == null) return null;
        if (name.matches("\\{.+}.+")) {
            String namespace = name.substring(name.indexOf("{") + 1, name.lastIndexOf("}"));
            String localPart = name.substring(name.lastIndexOf("}") + 1);
            return new QName(namespace, localPart);
        } else if (name.contains(":")) {
            Integer pos = name.indexOf(":");
            String prefix = name.substring(0, pos);
            String localPart = name.substring(pos + 1, name.length());
            if (!prefix2Namespace.containsKey(prefix)) {
                this.exceptionMessages.add("Prefix \"" + prefix + "\" for name \"" + localPart + "\" is not defined!");
            }
            return new QName(prefix2Namespace.get(prefix), localPart, prefix);
        } else if (Defaults.TOSCA_NORMATIVE_NAMES.contains(name)) {
            return new QName(Namespaces.TOSCA_NS, name, "tosca");
        } else if (Defaults.YAML_TYPES.contains(name)) {
            return new QName(Namespaces.YAML_NS, name, "yaml");
        } else {
            return new QName(namespace, name, "");
        }
    }

    @Nullable
    public Map<String, TArtifactType> buildArtifactTypes(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildArtifactType(entry.getValue())));
    }

    @Nullable
    public TArtifactType buildArtifactType(Object object) {
        if (!Objects.nonNull(object) || !validate(TArtifactType.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, TArtifactType.class, new TArtifactType.Builder())
            .setMimeType((String) map.get("mime_type"))
            .setFileExt(buildListString(map.get("file_ext")))
            .build();
    }

    @NonNull
    public <T extends TEntityType.Builder<T>> T buildEntityType(Object object, Class<?> child, T builder) {
        if (!Objects.nonNull(object) || !validate(child, object)) return builder;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return builder
            .setDescription(buildDescription(map.get("description")))
            .setVersion(buildVersion(map.get("version")))
            .setDerivedFrom(buildQName((String) map.get("derived_from")))
            .setProperties(buildProperties(map.get("properties")))
            .setAttributes(buildAttributes(map.get("attributes")))
            .setMetadata(buildMetadata(map.get("metadata")));
    }

    @Nullable
    public TVersion buildVersion(Object object) {
        return !Objects.nonNull(object) ? null : new TVersion((String) object);
    }

    @Nullable
    public Map<String, TPropertyDefinition> buildProperties(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> buildPropertyDefinition(entry.getValue(), TPropertyDefinition.class)
            ));
    }

    @Nullable
    public TPropertyDefinition buildPropertyDefinition(Object object, Class<?> child) {
        if (!Objects.nonNull(object) || !validate(child, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TPropertyDefinition.Builder(buildQName((String) map.get("type")))
            .setDescription(buildDescription(map.get("description")))
            .setRequired(buildRequired(map.get("required")))
            .setDefault(map.get("default"))
            .setStatus(buildStatus(map.get("status")))
            .setConstraints(buildConstraints(map.get("constraints")))
            .setEntrySchema(buildEntrySchema(map.get("entry_schema")))
            .build();
    }

    @Nullable
    public Boolean buildRequired(Object object) {
        if (!Objects.nonNull(object)) return null;
        if (object instanceof String) return "true".equals(object) ? Boolean.TRUE : Boolean.FALSE;
        return object instanceof Boolean ? (Boolean) object : Boolean.FALSE;
    }

    @Nullable
    public TStatusValue buildStatus(Object object) {
        if (!Objects.nonNull(object)) return null;
        String status = (String) object;
        switch (status) {
            case "supported":
                return TStatusValue.supported;
            case "unsupported":
                return TStatusValue.unsupported;
            case "experimental":
                return TStatusValue.experimental;
            case "deprecated":
                return TStatusValue.deprecated;
            default:
                assert ("supported".equals(status) ||
                    "unsupported".equals(status) ||
                    "experimental".equals(status) ||
                    "deprecated".equals(status));
                return null;
        }
    }

    @Nullable
    public List<TConstraintClause> buildConstraints(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        return list.stream()
            .filter(Objects::nonNull)
            .flatMap(map -> map.entrySet().stream())
            .map(entry -> buildConstraintClause(entry.getKey(), entry.getValue()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    public TConstraintClause buildConstraintClause(String key, Object object) {
        if (!Objects.nonNull(object)) return null;
        TConstraintClause constraintClause = new TConstraintClause();
        switch (key) {
            case "equal":
                constraintClause.setEqual(object);
                break;
            case "greater_than":
                constraintClause.setGreaterOrEqual(object);
                break;
            case "greater_or_equal":
                constraintClause.setGreaterOrEqual(object);
                break;
            case "less_than":
                constraintClause.setLessThan(object);
                break;
            case "less_or_equal":
                constraintClause.setLessOrEqual(object);
                break;
            case "in_range":
                constraintClause.setInRange(buildListObject(object));
                break;
            case "valid_values":
                constraintClause.setValidValues(buildListObject(object));
                break;
            case "length":
                constraintClause.setLength(object);
                break;
            case "min_length":
                constraintClause.setMinLength(object);
                break;
            case "max_length":
                constraintClause.setMaxLength(object);
                break;
            case "pattern":
                constraintClause.setPattern(object);
                break;
            default:
        }
        return constraintClause;
    }

    @Nullable
    public List<Object> buildListObject(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        List<Object> result = (List<Object>) object;
        return result;
    }

    @Nullable
    public TEntrySchema buildEntrySchema(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TEntrySchema.Builder()
            .setType(buildQName((String) map.get("type")))
            .setDescription(buildDescription(map.get("description")))
            .setConstraints(buildConstraints(map.get("constraints")))
            .build();
    }

    @Nullable
    public Map<String, TAttributeDefinition> buildAttributes(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildAttributeDefinition(entry.getValue())));
    }

    @Nullable
    public TAttributeDefinition buildAttributeDefinition(Object object) {
        if (!Objects.nonNull(object) || !validate(TAttributeDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TAttributeDefinition.Builder(buildQName((String) map.get("type")))
            .setDescription(buildDescription(map.get("description")))
            .setDefault(map.get("default"))
            .setStatus(buildStatus(map.get("status")))
            .setEntrySchema(buildEntrySchema(map.get("entry_schema")))
            .build();
    }

    @Nullable
    public List<String> buildListString(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        List<String> tmp = ((List<Object>) object).stream()
            .filter(Objects::nonNull)
            .map(Object::toString)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return tmp;
    }

    @Nullable
    public Map<String, TDataType> buildDataTypes(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildDataType(entry.getValue())));
    }

    @Nullable
    public TDataType buildDataType(Object object) {
        if (!Objects.nonNull(object) || !validate(TDataType.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, TDataType.class, new TDataType.Builder())
            .setConstraints(buildConstraints(map.get("constraints")))
            .build();
    }

    @Nullable
    public Map<String, TCapabilityType> buildCapabilityTypes(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildCapabilityType(entry.getValue())));
    }

    @Nullable
    public TCapabilityType buildCapabilityType(Object object) {
        if (!Objects.nonNull(object) || !validate(TCapabilityType.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, TCapabilityType.class, new TCapabilityType.Builder())
            .setValidSourceTypes(buildListQName(buildListString(map.get("valid_source_types"))))
            .build();
    }

    @Nullable
    public List<QName> buildListQName(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        return list.stream().map(this::buildQName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    public Map<String, TInterfaceType> buildInterfaceTypes(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildInterfaceType(entry.getValue())));
    }

    @Nullable
    public TInterfaceType buildInterfaceType(Object object) {
        if (!Objects.nonNull(object) || !validate(TInterfaceType.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        Set<String> keys = Stream.of("inputs", "description", "version", "derived_from",
            "properties", "attributes", "metadata").collect(Collectors.toSet());
        return buildEntityType(object, TInterfaceType.class, new TInterfaceType.Builder())
            .setInputs(buildProperties(map.get("inputs")))
            .setOperations(map.entrySet().stream()
                .filter(Objects::nonNull)
                .filter(entry -> !keys.contains(entry.getKey()))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> buildOperationDefinition(entry.getValue(), "TInterfaceType"))
                ))
            .build();
    }

    @Nullable
    public TOperationDefinition buildOperationDefinition(Object object, String context) {
        if (!Objects.nonNull(object) || !validate(TOperationDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TOperationDefinition.Builder()
            .setDescription(buildDescription(map.get("description")))
            .setInputs(buildPropertyAssignmentOrDefinition(map.get("inputs"), context))
            .setOutputs(buildPropertyAssignmentOrDefinition(map.get("outputs"), context))
            .setImplementation(buildImplementation(map.get("implementation")))
            .build();
    }

    @Nullable
    public Map<String, TPropertyAssignmentOrDefinition> buildPropertyAssignmentOrDefinition(Object object, String context) {
        if (!Objects.nonNull(object)) return null;
        if ("TNodeType".equals(context) ||
            "TRelationshipType".equals(context) ||
            "TGroupType".equals(context) ||
            "TInterfaceType".equals(context)) {
            return buildProperties(object).entrySet().stream()
                .filter(this::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
            return buildMapPropertyAssignment(object).entrySet().stream()
                .filter(this::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    @Nullable
    public Map<String, TPropertyAssignment> buildMapPropertyAssignment(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildPropertyAssignment(entry.getValue())));
    }

    @Nullable
    public TPropertyAssignment buildPropertyAssignment(Object object) {
        if (!Objects.nonNull(object)) return null;
        return new TPropertyAssignment.Builder()
            .setValue(object)
            .build();
    }

    @Nullable
    public TImplementation buildImplementation(Object object) {
        if (!Objects.nonNull(object)) return null;
        if (object instanceof String) return new TImplementation(buildQName((String) object));
        if (!validate(TImplementation.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TImplementation.Builder(buildQName((String) map.get("primary")))
            .setDependencies(buildListQName(buildListString(map.get("dependencies"))))
            .build();
    }

    @Nullable
    public Map<String, TRelationshipType> buildRelationshipTypes(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildRelationshipType(entry.getValue())));
    }

    @Nullable
    public TRelationshipType buildRelationshipType(Object object) {
        if (!Objects.nonNull(object) || !validate(TRelationshipType.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, TRelationshipType.class, new TRelationshipType.Builder())
            .setValidTargetTypes(buildListQName(buildListString(map.get("valid_target_types"))))
            .setInterfaces(buildMapInterfaceDefinition(map.get("interfaces"), "TRelationshipType"))
            .build();
    }

    @Nullable
    public Map<String, TInterfaceDefinition> buildMapInterfaceDefinition(Object object, String context) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildInterfaceDefinition(entry.getValue(), context)));
    }

    @Nullable
    public TInterfaceDefinition buildInterfaceDefinition(Object object, String context) {
        if (!Objects.nonNull(object) || !validate(TInterfaceType.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        Set<String> keys = Stream.of("inputs", "type").collect(Collectors.toSet());
        return new TInterfaceDefinition.Builder()
            .setType(buildQName((String) map.get("type")))
            .setInputs(buildPropertyAssignmentOrDefinition(map.get("inputs"), context))
            .setOperations(
                map.entrySet().stream()
                    .filter(Objects::nonNull)
                    .filter(entry -> !keys.contains(entry.getKey()))
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> buildOperationDefinition(entry.getValue(), context))
                    )
            )
            .build();
    }

    @Nullable
    public Map<String, TNodeType> buildNodeTypes(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildNodeType(entry.getValue())));
    }

    @Nullable
    public TNodeType buildNodeType(Object object) {
        if (!Objects.nonNull(object) || !validate(TNodeType.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, TNodeType.class, new TNodeType.Builder())
            .setRequirements(buildListMapRequirementDefinition(map.get("requirements")))
            .setCapabilities(buildMapCapabilityDefinition(map.get("capabilities")))
            .setInterfaces(buildMapInterfaceDefinition(map.get("interfaces"), "TNodeType"))
            .setArtifacts(buildMapArtifactDefinition(map.get("artifacts")))
            .build();
    }

    @Nullable
    public List<TMapRequirementDefinition> buildListMapRequirementDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        return list.stream()
            .filter(Objects::nonNull)
            .flatMap(entry -> entry.entrySet().stream())
            .filter(Objects::nonNull)
            .map(entry -> buildMapRequirementDefinition(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    @Nullable
    public TMapRequirementDefinition buildMapRequirementDefinition(String key, Object object) {
        if (!Objects.nonNull(object)) return null;
        TMapRequirementDefinition result = new TMapRequirementDefinition();
        put(result, key, buildRequirementDefinition(object));
        return result;
    }

    @Nullable
    public TRequirementDefinition buildRequirementDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        if (object instanceof String) return new TRequirementDefinition.Builder(buildQName((String) object)).build();
        if (!validate(TRequirementDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRequirementDefinition.Builder(buildQName((String) map.get("capability")))
            .setNode(buildQName((String) map.get("node")))
            .setRelationship(buildRelationshipDefinition(map.get("relationship")))
            .setOccurrences(buildListString(map.get("occurrences")))
            .build();
    }

    @Nullable
    public TRelationshipDefinition buildRelationshipDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        if (object instanceof String) return new TRelationshipDefinition.Builder(buildQName((String) object)).build();
        if (!validate(TRelationshipDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRelationshipDefinition.Builder(buildQName((String) map.get("type")))
            .setInterfaces(buildMapInterfaceDefinition(map.get("interfaces"), "TRelationshipDefinition"))
            .build();
    }

    @Nullable
    public Map<String, TCapabilityDefinition> buildMapCapabilityDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildCapabilityDefinition(entry.getValue())));
    }

    private <T> void put(Map<String, T> map, String key, T value) {
        if (Objects.nonNull(map) && Objects.nonNull(key) && Objects.nonNull(value)) map.put(key, value);
    }

    private <T> boolean nonNull(Map.Entry<String, T> entry) {
        return Objects.nonNull(entry) && Objects.nonNull(entry.getKey()) && Objects.nonNull(entry.getValue());
    }

    @Nullable
    public TCapabilityDefinition buildCapabilityDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        if (object instanceof String) return new TCapabilityDefinition.Builder(buildQName((String) object)).build();
        if (!validate(TCapabilityDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TCapabilityDefinition.Builder(buildQName((String) map.get("type")))
            .setDescription(buildDescription(map.get("description")))
            .setOccurrences(buildListString(map.get("occurrences")))
            .setValidSourceTypes(buildListQName(buildListString(map.get("valid_source_types"))))
            .setProperties(buildProperties(map.get("properties")))
            .setAttributes(buildAttributes(map.get("attributes")))
            .build();
    }

    @Nullable
    public Map<String, TArtifactDefinition> buildMapArtifactDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildArtifactDefinition(entry.getValue())));
    }

    @Nullable
    public TArtifactDefinition buildArtifactDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        if (object instanceof String) {
            String file = (String) object;
            // TODO infer artifact type and mime type from file URI
            String type = file.substring(file.lastIndexOf("."), file.length());
            return new TArtifactDefinition.Builder(buildQName(type), new ArrayList<>(Collections.singleton(file))).build();
        }
        if (!validate(TArtifactDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;

        List<String> files;
        if (map.get("file") instanceof String) {
            files = new ArrayList<>(Collections.singleton((String) map.get("file")));
        } else if (map.get("files") instanceof List) {
            // TODO capability check
            files = buildListString(map.get("files"));
        } else {
            files = null;
            assert false;
        }
        return new TArtifactDefinition.Builder(buildQName((String) map.get("type")), files)
            .setRepository((String) map.get("repository"))
            .setDescription(buildDescription(map.get("description")))
            .setDeployPath((String) map.get("deploy_path"))
            .setProperties(buildMapPropertyAssignment(map.get("properties")))
            .build();
    }

    @Nullable
    public Map<String, TGroupType> buildGroupTypes(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildGroupType(entry.getValue())));
    }

    @Nullable
    public TGroupType buildGroupType(Object object) {
        if (!Objects.nonNull(object) || !validate(TGroupType.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, TGroupType.class, new TGroupType.Builder())
            .setMembers(buildListQName(buildListString(map.get("members"))))
            .setRequirements(buildListMapRequirementDefinition(map.get("requirements")))
            .setCapabilities(buildMapCapabilityDefinition(map.get("capabilities")))
            .setInterfaces(buildMapInterfaceDefinition(map.get("interfaces"), "TGroupType"))
            .build();
    }

    @Nullable
    public Map<String, TPolicyType> buildPolicyTypes(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildPolicyType(entry.getValue())));
    }

    @Nullable
    public TPolicyType buildPolicyType(Object object) {
        if (!Objects.nonNull(object) || !validate(TPolicyType.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, TPolicyType.class, new TPolicyType.Builder())
            .setTargets(buildListQName(buildListString(map.get("targets"))))
            .setTriggers(map.get("triggers"))
            .build();
    }

    @Nullable
    public Map<String, TParameterDefinition> buildMapParameterDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildParameterDefinition(entry.getValue())));
    }

    @Nullable
    public TParameterDefinition buildParameterDefinition(Object object) {
        if (!Objects.nonNull(object) || !validate(TParameterDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TParameterDefinition.Builder(buildPropertyDefinition(object, TParameterDefinition.class))
            .setValue(map.get("value"))
            .build();
    }

    @Nullable
    public Map<String, TNodeTemplate> buildNodeTemplates(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildNodeTemplate(entry.getValue())));
    }

    @Nullable
    public TNodeTemplate buildNodeTemplate(Object object) {
        if (!Objects.nonNull(object) || !validate(TNodeTemplate.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TNodeTemplate.Builder(buildQName((String) map.get("type")))
            .setDescription(buildDescription(map.get("description")))
            .setMetadata(buildMetadata(map.get("metadata")))
            .setDirectives(buildListString(map.get("directives")))
            .setProperties(buildMapPropertyAssignment(map.get("properties")))
            .setAttributes(buildMapAttributeAssignment(map.get("attributes")))
            .setRequirements(buildListMapRequirementAssignment(map.get("requirements")))
            .setCapabilities(buildMapCapabilityAssignment(map.get("capabilities")))
            .setInterfaces(buildMapInterfaceDefinition(map.get("interfaces"), "TNodeTemplate"))
            .setArtifacts(buildMapArtifactDefinition(map.get("artifacts")))
            .setNodeFilter(buildNodeFilterDefinition(map.get("node_filter")))
            .setCopy(buildQName((String) map.get("copy")))
            .build();
    }

    @Nullable
    public Map<String, TAttributeAssignment> buildMapAttributeAssignment(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildAttributeAssignment(entry.getValue())));
    }

    @Nullable
    public TAttributeAssignment buildAttributeAssignment(Object object) {
        if (!Objects.nonNull(object) || !validate(TAttributeAssignment.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TAttributeAssignment.Builder()
            .setDescription(buildDescription(map.get("description")))
            .setValue(map.get("value"))
            .build();
    }

    @Nullable
    public List<TMapRequirementAssignment> buildListMapRequirementAssignment(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        return list.stream()
            .filter(Objects::nonNull)
            .flatMap(entry -> entry.entrySet().stream())
            .filter(Objects::nonNull)
            .map(entry -> buildMapRequirementAssignment(entry.getKey(), entry.getValue()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    public TMapRequirementAssignment buildMapRequirementAssignment(String key, Object object) {
        if (!Objects.nonNull(object)) return null;
        TMapRequirementAssignment result = new TMapRequirementAssignment();
        put(result, key, buildRequirementAssignment(object));
        return result;
    }

    @Nullable
    public TRequirementAssignment buildRequirementAssignment(Object object) {
        if (!Objects.nonNull(object)) return null;
        if (object instanceof String) return new TRequirementAssignment(buildQName((String) object));
        if (!validate(TRequirementAssignment.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRequirementAssignment.Builder()
            .setCapability(buildQName((String) map.get("capability")))
            .setNode(buildQName((String) map.get("node")))
            .setRelationship(buildRelationshipAssignment(map.get("relationship")))
            .setNodeFilter(buildNodeFilterDefinition(map.get("node_filter")))
            .setOccurrences(buildListString(map.get("occurrences")))
            .build();
    }

    @Nullable
    public TRelationshipAssignment buildRelationshipAssignment(Object object) {
        if (!Objects.nonNull(object)) return null;
        if (object instanceof String) return new TRelationshipAssignment.Builder(buildQName((String) object)).build();
        if (!validate(TRelationshipAssignment.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRelationshipAssignment.Builder(buildQName((String) map.get("type")))
            .setProperties(buildMapPropertyAssignment(map.get("properties")))
            .setInterfaces(buildMapInterfaceAssignment(map.get("interfaces")))
            .build();
    }

    @Nullable
    public Map<String, TInterfaceAssignment> buildMapInterfaceAssignment(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildInterfaceAssignment(entry.getValue())));
    }

    @Nullable
    public TInterfaceAssignment buildInterfaceAssignment(Object object) {
        if (!Objects.nonNull(object) || !validate(TInterfaceAssignment.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        Set<String> keys = Stream.of("type", "inputs").collect(Collectors.toSet());
        TInterfaceAssignment.Builder builder = new TInterfaceAssignment.Builder();
        builder.setType(buildQName((String) map.get("type")))
            .setInputs(buildPropertyAssignmentOrDefinition(map.get("inputs"), "TInterfaceAssignment"))
            .setOperations(
                map.entrySet().stream()
                    .filter(Objects::nonNull)
                    .filter(entry -> !keys.contains(entry.getKey()))
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> buildOperationDefinition(entry.getValue(), "TInterfaceAssignment")
                    ))
            );
        return builder.build();
    }

    @Nullable
    public TNodeFilterDefinition buildNodeFilterDefinition(Object object) {
        if (!Objects.nonNull(object) || !validate(TNodeFilterDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TNodeFilterDefinition.Builder()
            .setProperties(buildListMapPropertyFilterDefinition(map.get("properties")))
            .setCapabilities(buildListMapObjectValue(map.get("capabilities")))
            .build();
    }

    @Nullable
    public List<TMapPropertyFilterDefinition> buildListMapPropertyFilterDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        return list.stream()
            .filter(Objects::nonNull)
            .flatMap(entry -> entry.entrySet().stream())
            .filter(Objects::nonNull)
            .map(entry -> buildMapPropertyDefinition(entry.getKey(), entry.getValue()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    public TMapPropertyFilterDefinition buildMapPropertyDefinition(String key, Object object) {
        if (!Objects.nonNull(object)) return null;
        TMapPropertyFilterDefinition result = new TMapPropertyFilterDefinition();
        put(result, key, buildPropertyFilterDefinition(object));
        return result;
    }

    @Nullable
    public TPropertyFilterDefinition buildPropertyFilterDefinition(Object object) {
        if (!Objects.nonNull(object) || !validate(TPropertyFilterDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TPropertyFilterDefinition.Builder()
            .setConstraints(buildConstraints(map.get("constraints")))
            .build();
    }

    @Nullable
    public List<TMapObject> buildListMapObjectValue(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        return list.stream()
            .filter(Objects::nonNull)
            .flatMap(map -> map.entrySet().stream())
            .filter(Objects::nonNull)
            .map(entry -> buildMapObjectValue(entry.getKey(), entry.getValue()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    public TMapObject buildMapObjectValue(String key, Object object) {
        if (!Objects.nonNull(object)) return null;
        TMapObject result = new TMapObject();
        put(result, key, object);
        return result;
    }

    @Nullable
    public Map<String, TCapabilityAssignment> buildMapCapabilityAssignment(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildCapabilityAssignment(entry.getValue())));
    }

    @Nullable
    public TCapabilityAssignment buildCapabilityAssignment(Object object) {
        if (!Objects.nonNull(object) || !validate(TCapabilityAssignment.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TCapabilityAssignment.Builder()
            .setProperties(buildMapPropertyAssignment(map.get("properties")))
            .setAttributes(buildMapAttributeAssignment(map.get("attributes")))
            .build();
    }

    @Nullable
    public Map<String, TRelationshipTemplate> buildRelationshipTemplates(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildRelationshipTemplate(entry.getValue())));
    }

    @Nullable
    public TRelationshipTemplate buildRelationshipTemplate(Object object) {
        if (!Objects.nonNull(object) || !validate(TRelationshipTemplate.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRelationshipTemplate.Builder(buildQName((String) map.get("type")))
            .setDescription(buildDescription(map.get("description")))
            .setMetadata(buildMetadata(map.get("metadata")))
            .setProperties(buildMapPropertyAssignment(map.get("properties")))
            .setAttributes(buildMapAttributeAssignment(map.get("attributes")))
            .setInterfaces(buildMapInterfaceDefinition(map.get("interfaces"), "TRelationshipTemplate"))
            .setCopy(buildQName((String) map.get("copy")))
            .build();
    }

    @Nullable
    public Map<String, TGroupDefinition> buildGroupDefinitions(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildGroupDefinition(entry.getValue())));
    }

    @Nullable
    public TGroupDefinition buildGroupDefinition(Object object) {
        if (!Objects.nonNull(object) || !validate(TGroupDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TGroupDefinition.Builder(buildQName((String) map.get("type")))
            .setDescription(buildDescription(map.get("description")))
            .setMetadata(buildMetadata(map.get("metadata")))
            .setProperties(buildMapPropertyAssignment(map.get("properties")))
            .setMembers(buildListQName(buildListString(map.get("members"))))
            .setInterfaces(buildMapInterfaceDefinition(map.get("interfaces"), "TGroupDefinition"))
            .build();
    }

    @Nullable
    public Map<String, TPolicyDefinition> buildMapPolicyDefinition(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildPolicyDefinition(entry.getValue())));
    }

    @Nullable
    public TPolicyDefinition buildPolicyDefinition(Object object) {
        if (!Objects.nonNull(object) || !validate(TPolicyDefinition.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TPolicyDefinition.Builder(buildQName((String) map.get("type")))
            .setDescription(buildDescription(map.get("description")))
            .setMetadata(buildMetadata(map.get("metadata")))
            .setProperties(buildMapPropertyAssignment(map.get("properties")))
            .setTargets(buildListQName(buildListString(map.get("targets"))))
            .build();
    }

    @Nullable
    public TSubstitutionMappings buildSubstitutionMappings(Object object) {
        if (!Objects.nonNull(object) || !validate(TSubstitutionMappings.class, object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TSubstitutionMappings.Builder()
            .setNodeType(buildQName((String) map.get("node_type")))
            .setCapabilities(buildMapStringList(map.get("capabilities")))
            .setRequirements(buildMapStringList(map.get("requirements")))
            .build();
    }

    @Nullable
    public Map<String, TListString> buildMapStringList(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(this::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> buildStringList(entry.getValue())));
    }

    @Nullable
    public TListString buildStringList(Object object) {
        if (!Objects.nonNull(object)) return null;
        @SuppressWarnings("unchecked")
        List<String> tmp = (List<String>) object;
        TListString stringList = new TListString();
        stringList.addAll(tmp);
        return stringList;
    }
}

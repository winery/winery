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

package org.eclipse.winery.repository.converter.reader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.model.converter.support.Namespaces;
import org.eclipse.winery.model.converter.support.exception.InvalidToscaSyntax;
import org.eclipse.winery.model.converter.support.exception.MultiException;
import org.eclipse.winery.model.tosca.yaml.YTArtifactDefinition;
import org.eclipse.winery.model.tosca.yaml.YTArtifactType;
import org.eclipse.winery.model.tosca.yaml.YTAttributeAssignment;
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
import org.eclipse.winery.model.tosca.yaml.YTInterfaceAssignment;
import org.eclipse.winery.model.tosca.yaml.YTInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.YTInterfaceType;
import org.eclipse.winery.model.tosca.yaml.YTNodeFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.YTNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.YTNodeType;
import org.eclipse.winery.model.tosca.yaml.YTOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.YTParameterDefinition;
import org.eclipse.winery.model.tosca.yaml.YTPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.YTPolicyType;
import org.eclipse.winery.model.tosca.yaml.YTPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.YTPropertyAssignmentOrDefinition;
import org.eclipse.winery.model.tosca.yaml.YTPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.YTPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipAssignment;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipDefinition;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipTemplate;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipType;
import org.eclipse.winery.model.tosca.yaml.YTRepositoryDefinition;
import org.eclipse.winery.model.tosca.yaml.YTRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.YTRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.YTSchemaDefinition;
import org.eclipse.winery.model.tosca.yaml.YTServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.YTStatusValue;
import org.eclipse.winery.model.tosca.yaml.YTSubstitutionMappings;
import org.eclipse.winery.model.tosca.yaml.YTTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.YTVersion;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.YTListString;
import org.eclipse.winery.model.tosca.yaml.support.YTMapImportDefinition;
import org.eclipse.winery.model.tosca.yaml.support.YTMapObject;
import org.eclipse.winery.model.tosca.yaml.support.YTMapPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.support.YTMapPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.support.YTMapRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.support.YTMapRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.tosca.datatypes.Credential;
import org.eclipse.winery.repository.converter.validator.FieldValidator;

import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YamlBuilder {
    public static final Logger LOGGER = LoggerFactory.getLogger(YamlBuilder.class);

    private final String namespace;
    private MultiException exception;
    private Map<String, String> prefix2Namespace;
    private FieldValidator validator;

    public YamlBuilder(String namespace) {
        this.namespace = namespace;
        this.validator = new FieldValidator();
        this.exception = new MultiException();
    }

    private void initPrefix2Namespace(Object object) {
        this.prefix2Namespace = new LinkedHashMap<>();
        this.prefix2Namespace.put("tosca", Namespaces.TOSCA_YAML_NS);

        if (Objects.isNull(object)) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        for (Map<String, Object> map : list) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!(entry.getValue() instanceof String)) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> importDefinition = (Map<String, Object>) entry.getValue();
                    if (importDefinition != null) {
                        String namespacePrefix = stringValue(importDefinition.get("namespace_prefix"));
                        String namespaceUri = stringValue(importDefinition.get("namespace_uri"));
                        if (namespacePrefix != null && namespaceUri != null) {
                            this.prefix2Namespace.put(namespacePrefix, namespaceUri);
                        }
                    }
                }
            }
        }
    }

    private <T, K> boolean validate(Class<T> clazz, Object object, Parameter<K> parameter) {
        if (object instanceof Map) {
            if (!clazz.equals(YTInterfaceType.class)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> fields = (Map<String, Object>) object;
                this.exception.add(validator.validate(clazz, fields, parameter));
            }
            return true;
        } else if (object instanceof String) {
            switch (clazz.getSimpleName()) {
                case "TAttributeAssignment":
                case "TRequirementAssignment":
                case "TOperationDefinition":
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Nullable
    public YTServiceTemplate buildServiceTemplate(Object object) throws MultiException {
        if (Objects.isNull(object) || !validate(YTServiceTemplate.class, object, new Parameter<>().addContext("service_template"))) {
            return null;
        }
        Parameter<Object> parameter = new Parameter<>();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        // build map between prefix and namespaces
        initPrefix2Namespace(map.get("imports"));

        YTServiceTemplate.Builder builder = new YTServiceTemplate.Builder(stringValue(
            map.getOrDefault("tosca_definitions_version", "")
        )).setMetadata(buildMetadata(map.get("metadata")))
            .setDescription(buildDescription(map.get("description")))
            .setDslDefinitions(buildMap(map.get("dsl_definitions"),
                parameter.copy().addContext("dsl_definition").setBuilderOO((obj, p) -> obj)
            ))
            .setRepositories(buildMap(map, "repositories", this::buildRepositoryDefinition, parameter))
            .setImports(buildList(map, "imports", this::buildMapImportDefinition, parameter))
            .setArtifactTypes(buildMap(map, "artifact_types", this::buildArtifactType, parameter))
            .setDataTypes(buildMap(map, "data_types", this::buildDataType, parameter))
            .setCapabilityTypes(buildMap(map, "capability_types", this::buildCapabilityType, parameter))
            .setInterfaceTypes(buildMap(map, "interface_types", this::buildInterfaceType, parameter))
            .setRelationshipTypes(buildMap(map, "relationship_types", this::buildRelationshipType, parameter))
            .setNodeTypes(buildMap(map, "node_types", this::buildNodeType, parameter))
            .setGroupTypes(buildMap(map, "group_types", this::buildGroupType, parameter))
            .setPolicyTypes(buildMap(map, "policy_types", this::buildPolicyType, parameter))
            .setTopologyTemplate(buildTopologyTemplate(map.get("topology_template"),
                parameter.copy().addContext("topology_template")
            ));
        if (this.exception.hasException()) {
            throw this.exception;
        }
        return builder.build();
    }

    @Nullable
    public YTTopologyTemplateDefinition buildTopologyTemplate(Object object, Parameter<Object> parameter) {
        if (Objects.isNull(object) || !validate(YTTopologyTemplateDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTTopologyTemplateDefinition.Builder()
            .setDescription(buildDescription(map.get("description")))
            .setInputs(buildMap(map, "inputs", this::buildParameterDefinition, parameter))
            .setNodeTemplates(buildMap(map, "node_templates", this::buildNodeTemplate, parameter))
            .setRelationshipTemplates(buildMap(map, "relationship_templates", this::buildRelationshipTemplate, parameter))
            .setGroups(buildMap(map, "groups", this::buildGroupDefinition, parameter))
            .setPolicies(buildListMap(map, "policies", this::buildPolicyDefinition, parameter))
            .setOutputs(buildMap(map, "outputs", this::buildParameterDefinition, parameter))
            .setSubstitutionMappings(buildSubstitutionMappings(map.get("substitution_mappings"),
                parameter.copy().addContext("substitution_mappings")
            ))
            .build();
    }

    @Nullable
    public Metadata buildMetadata(Object object) {
        if (Objects.isNull(object)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> tmp = (Map<String, Object>) object;
        Metadata metadata = new Metadata();
        tmp.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getValue()))
            .forEach(entry -> {
                metadata.put(entry.getKey(), stringValue(entry.getValue()));
                if (entry.getValue() instanceof Date) {
                    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSZ");
                    date.setTimeZone(TimeZone.getTimeZone("UTC"));
                    metadata.put(entry.getKey(), date.format(entry.getValue()));
                }
            });
        return metadata;
    }

    @Nullable
    public String buildDescription(Object object) {
        if (Objects.isNull(object)) {
            return "";
        }
        return stringValue(object);
    }

    @Nullable
    public YTRepositoryDefinition buildRepositoryDefinition(Object object, Parameter<YTRepositoryDefinition> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String) {
            return new YTRepositoryDefinition.Builder(stringValue(object)).build();
        }
        if (!validate(YTRepositoryDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTRepositoryDefinition.Builder(stringValue(map.get("url")))
            .setDescription(buildDescription(map.get("description")))
            .setCredential(buildCredential(map.get("credential"),
                new Parameter<Credential>(parameter.getContext()).addContext("credential")
            ))
            .build();
    }

    @Nullable
    public Credential buildCredential(Object object, Parameter<Credential> parameter) {

        if (Objects.isNull(object)) {
            return null;
        }
        if (!validate(Credential.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        @SuppressWarnings("unchecked")
        Map<String, String> keys = (Map<String, String>) map.get("keys");
        return new Credential.Builder(stringValue(map.get("token_type")))
            .setProtocol(stringValue(map.get("protocol")))
            .setToken(stringValue(map.get("token")))
            .setUser(stringValue(map.get("user")))
            .setKeys(keys)
            .build();
    }

    @Nullable
    public YTMapImportDefinition buildMapImportDefinition(Object object, Parameter<YTMapImportDefinition> parameter) {
        YTMapImportDefinition mapImportDefinition = new YTMapImportDefinition();
        mapImportDefinition.put(stringValue(parameter.getValue()), buildImportDefinition(object,
            new Parameter<>(parameter.getContext())
        ));
        return mapImportDefinition;
    }

    @Nullable
    public YTImportDefinition buildImportDefinition(Object object, Parameter<YTImportDefinition> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String) {
            return new YTImportDefinition.Builder(stringValue(object)).build();
        }
        if (!validate(YTImportDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTImportDefinition.Builder(stringValue(map.get("file")))
            .setRepository(buildQName(stringValue(map.get("repository"))))
            .setNamespaceUri(stringValue(map.get("namespace_uri")))
            .setNamespacePrefix(stringValue(map.get("namespace_prefix")))
            .build();
    }

    @Nullable
    private QName buildQName(String name) {
        if (Objects.isNull(name)) {
            return null;
        }
        // TODO decide on namespace rules w.r.t Simple Profile spec
        // current solution: use dotted notation for namespaces as in RADON particles

        // ignore version when splitting the full name
        String n = VersionUtils.getNameWithoutVersion(name);
        int separatorPos = n.lastIndexOf(".");
        if (separatorPos != -1) {
            String namespace = name.substring(0, separatorPos);
            String localPart = name.substring(separatorPos + 1);
            return new QName(namespace, localPart);
        }

        return new QName(null, name);
    }

    @Nullable
    public YTArtifactType buildArtifactType(Object object, Parameter<YTArtifactType> parameter) {
        if (Objects.isNull(object) || !validate(YTArtifactType.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object,
            new Parameter<YTArtifactType.Builder>(parameter.getContext())
                .setBuilder(new YTArtifactType.Builder())
                .setClazz(YTArtifactType.class))
            .setMimeType(stringValue(map.get("mime_type")))
            .setFileExt(buildListString(map.get("file_ext"),
                new Parameter<List<String>>(parameter.getContext()).addContext("file_ext")
            ))
            .build();
    }

    @NonNull
    public <T extends YTEntityType.Builder<T>> T buildEntityType(Object object, Parameter<T> parameter) {
        if (Objects.isNull(object) || !validate(parameter.getClazz(), object, parameter)) {
            return parameter.getBuilder();
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return parameter.getBuilder()
            .setDescription(buildDescription(map.get("description")))
            .setVersion(buildVersion(map.get("version")))
            .setDerivedFrom(buildQName(stringValue(map.get("derived_from"))))
            .setProperties(buildMap(map, "properties", this::buildPropertyDefinition, YTPropertyDefinition.class, parameter))
            .setAttributes(buildMap(map, "attributes", this::buildAttributeDefinition, parameter))
            .setMetadata(buildMetadata(map.get("metadata")));
    }

    @Nullable
    public YTVersion buildVersion(Object object) {
        return Objects.isNull(object) ? null : new YTVersion.Builder().setVersion(stringValue(object)).build();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> YTPropertyDefinition buildPropertyDefinition(Object object, Parameter<T> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (!validate(parameter.getClazz(), object, parameter)) {
            return null;
        }
        Map<String, Object> map = (Map<String, Object>) object;
        String type = stringValue(map.get("type"));
        if (type == null) {
            type = "string";
        }
        return new YTPropertyDefinition.Builder(buildQName(type))
            .setDescription(buildDescription(map.get("description")))
            .setRequired(buildRequired(map.get("required")))
            .setDefault(map.get("default"))
            .setStatus(buildStatus(map.get("status")))
            .addConstraints(buildList(map, "constraints", this::buildConstraintClause, parameter))
            .setEntrySchema(buildSchemaDefinition(map.get("entry_schema"),
                new Parameter<YTSchemaDefinition>(parameter.getContext()).addContext("entry_schema")
            ))
            .setKeySchema(buildSchemaDefinition(map.get("key_schema"),
                new Parameter<YTSchemaDefinition>(parameter.getContext()).addContext("key_schema")))
            .build();
    }

    @Nullable
    public Boolean buildRequired(Object object) {
        if (Objects.isNull(object)) {
            return true;
        }
        if (object instanceof String) {
            return "true".equals(object) ? Boolean.TRUE : Boolean.FALSE;
        }
        return object instanceof Boolean ? (Boolean) object : Boolean.FALSE;
    }

    @Nullable
    public YTStatusValue buildStatus(Object object) {
        String status = stringValue(object);
        if (Objects.isNull(status)) {
            return null;
        }
        switch (status) {
            case "supported":
                return YTStatusValue.supported;
            case "unsupported":
                return YTStatusValue.unsupported;
            case "experimental":
                return YTStatusValue.experimental;
            case "deprecated":
                return YTStatusValue.deprecated;
            default:
                assert ("supported".equals(status) ||
                    "unsupported".equals(status) ||
                    "experimental".equals(status) ||
                    "deprecated".equals(status));
                return null;
        }
    }

    @Nullable
    public YTConstraintClause buildConstraintClause(Object object, Parameter<YTConstraintClause> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        YTConstraintClause.Builder builder = new YTConstraintClause.Builder();
        builder.setKey(parameter.getValue());
        switch (parameter.getValue()) {
            case "in_range":
                builder.setList(buildListString(object,
                    new Parameter<List<String>>(parameter.getContext()).addContext("in_range")));
                break;
            case "valid_values":
                builder.setList(buildListString(object,
                    new Parameter<List<String>>(parameter.getContext()).addContext("valid_values")));
                break;
            default:
                builder.setKey(parameter.getValue());
                builder.setValue(stringValue(object));
        }
        return builder.build();
    }

    @Nullable
    public List<Object> buildListObject(Object object) {
        if (Objects.isNull(object)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<Object> result = (List<Object>) object;
        return result;
    }

    @Nullable
    public YTSchemaDefinition buildSchemaDefinition(Object object, Parameter<YTSchemaDefinition> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String) {
            return new YTSchemaDefinition.Builder(buildQName(stringValue(object))).build();
        } else {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) object;
            return new YTSchemaDefinition.Builder(buildQName(stringValue(map.get("type"))))
                .setDescription(buildDescription(map.get("description")))
                .setConstraints(buildList(map, "constraints", this::buildConstraintClause, parameter))
                .setEntrySchema(buildSchemaDefinition(map.get("entry_schema"),
                    new Parameter<YTSchemaDefinition>(parameter.getContext()).addContext("entry_schema")))
                .setKeySchema(buildSchemaDefinition(map.get("key_schema"),
                    new Parameter<YTSchemaDefinition>(parameter.getContext()).addContext("key_schema")))
                .build();
        }
    }

    @Nullable
    public <T> YTAttributeDefinition buildAttributeDefinition(Object object, Parameter<T> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (!validate(YTAttributeDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTAttributeDefinition.Builder(buildQName(stringValue(map.get("type"))))
            .setDescription(buildDescription(map.get("description")))
            .setDefault(map.get("default"))
            .setStatus(buildStatus(map.get("status")))
            .setEntrySchema(buildSchemaDefinition(map.get("entry_schema"),
                new Parameter<YTSchemaDefinition>(parameter.getContext()).addContext("entry_schema")
            ))
            .setKeySchema(buildSchemaDefinition(map.get("key_schema"),
                new Parameter<YTSchemaDefinition>(parameter.getContext()).addContext("key_schema")))
            .build();
    }

    @Nullable
    public List<String> buildListString(Object object, Parameter<List<String>> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (!(object instanceof List)) {
            exception.add(new InvalidToscaSyntax(
                "The value '{}' is invalid. Only arrays of form '[a,b]' are allowed",
                object
            ).setContext(new ArrayList<>(parameter.getContext())));
            return new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<String> tmp = ((List<Object>) object).stream()
            .map(this::stringValue)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return tmp;
    }

    @Nullable
    public YTDataType buildDataType(Object object, Parameter<YTDataType> parameter) {
        if (Objects.isNull(object) || !validate(YTDataType.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object,
            new Parameter<YTDataType.Builder>(parameter.getContext())
                .setBuilder(new YTDataType.Builder())
                .setClazz(YTDataType.class))
            .setConstraints(buildList(map, "constraints", this::buildConstraintClause, parameter))
            .build();
    }

    @Nullable
    public YTCapabilityType buildCapabilityType(Object object, Parameter<YTCapabilityType> parameter) {
        if (Objects.isNull(object) || !validate(YTCapabilityType.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object,
            new Parameter<YTCapabilityType.Builder>(parameter.getContext())
                .setBuilder(new YTCapabilityType.Builder())
                .setClazz(YTCapabilityType.class))
            .setValidSourceTypes(buildListQName(buildListString(map.get("valid_source_types"),
                new Parameter<List<String>>(parameter.getContext()).addContext("valid_source_types")
            )))
            .build();
    }

    @Nullable
    public List<QName> buildListQName(List<String> list) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return null;
        }
        return list.stream().map(this::buildQName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    public YTInterfaceType buildInterfaceType(Object object, Parameter<YTInterfaceType> parameter) {
        if (Objects.isNull(object) || !validate(YTInterfaceType.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object,
            new Parameter<YTInterfaceType.Builder>(parameter.getContext())
                .setBuilder(new YTInterfaceType.Builder())
                .setClazz(YTInterfaceType.class))
            .setInputs(buildMap(map, "inputs", this::buildPropertyDefinition,
                YTPropertyDefinition.class, parameter))
            .setOperations(buildMap(object,
                new Parameter<YTOperationDefinition>(parameter.getContext()).addContext("(operations)")
                    .setValue("TInterfaceType")
                    .setBuilderOO(this::buildOperationDefinition)
                    .setFilter(this::filterInterfaceTypeOperation)
            ))
            .setDerivedFrom(buildQName(stringValue(map.get("derived_from"))))
            .build();
    }

    private Boolean filterInterfaceTypeOperation(Map.Entry<String, Object> entry) {
        if (Objects.isNull(entry.getKey())) {
            return false;
        }
        Set<String> keys = Stream.of("inputs", "description", "version", "derived_from",
            "properties", "attributes", "metadata").collect(Collectors.toSet());
        return !keys.contains(entry.getKey());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public YTOperationDefinition buildOperationDefinition(Object object, Parameter<YTOperationDefinition> parameter) {
        if (Objects.isNull(object) || !validate(YTOperationDefinition.class, object, parameter)) {
            return null;
        }
        if (!(object instanceof Map)) {
            return null;
        }
        Map<String, Object> map = (Map<String, Object>) object;
        String description = buildDescription(map.get("description"));
        Map<String, YTParameterDefinition> inputs = buildParameterDefinitions(map.get("inputs"),
            new Parameter<>(parameter.getContext()).addContext("inputs").setValue(parameter.getValue())
        );
        Map<String, YTParameterDefinition> outputs = buildParameterDefinitions(map.get("outputs"),
            new Parameter<>(parameter.getContext()).addContext("outputs").setValue(parameter.getValue())
        );
        YTImplementation implementation = buildImplementation(map.get("implementation"),
            new Parameter<YTImplementation>(parameter.getContext()).addContext("implementation")
        );
        return new YTOperationDefinition.Builder()
            .setDescription(description)
            .setInputs(inputs)
            .setOutputs(outputs)
            .setImplementation(implementation)
            .build();
    }

    @Nullable
    public Map<String, YTParameterDefinition> buildParameterDefinitions(Object object, Parameter<Object> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        String context = stringValue(parameter.getValue());
        if ("TNodeType".equals(context) ||
            "TRelationshipType".equals(context) ||
            "TGroupType".equals(context) ||
            "TInterfaceType".equals(context)) {
            return buildMap(object, new Parameter<YTParameterDefinition>(parameter.getContext())
                .setClazz(YTParameterDefinition.class)
                .setBuilderOO(this::buildParameterDefinition));
        } else {
            return buildMap(object, new Parameter<YTParameterDefinition>(parameter.getContext())
                .setBuilderOO(this::buildParameterAssignment));
        }
    }

    @Nullable
    public Map<String, YTPropertyAssignmentOrDefinition> buildPropertyAssignmentOrDefinition(Object object, Parameter<Object> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        String context = stringValue(parameter.getValue());
        if ("TNodeType".equals(context) ||
            "TRelationshipType".equals(context) ||
            "TGroupType".equals(context) ||
            "TInterfaceType".equals(context)) {
            return buildMap(object, new Parameter<YTPropertyAssignmentOrDefinition>(parameter.getContext())
                .setClazz(YTPropertyDefinition.class)
                .setBuilderOO(this::buildPropertyDefinition));
        } else {
            return buildMap(object, new Parameter<YTPropertyAssignmentOrDefinition>(parameter.getContext())
                .setBuilderOO(this::buildPropertyAssignment));
        }
    }

    @Nullable
    public <T> YTPropertyAssignment buildPropertyAssignment(Object object, Parameter<T> parameter) {
        return new YTPropertyAssignment.Builder()
            .setValue(object)
            .build();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public YTImplementation buildImplementation(Object object, Parameter<YTImplementation> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String) {
            return new YTImplementation(stringValue(object));
        }
        if (object instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) object;
            YTImplementation.Builder builder = new YTImplementation.Builder();
            builder.setPrimaryArtifactName(stringValue(map.get("primary")));
            builder.setDependencyArtifactNames(buildListString(map.get("dependencies"),
                new Parameter<List<String>>(parameter.getContext()).addContext("dependencies")
            ));
            builder.setOperationHost(stringValue(map.get("operation_host")));
            String timeout = stringValue(map.get("timeout"));
            builder.setTimeout(timeout == null ? null : Integer.valueOf(timeout));
            return builder.build();
        }
        return null;
    }

    @Nullable
    public YTRelationshipType buildRelationshipType(Object object, Parameter<YTRelationshipType> parameter) {
        if (Objects.isNull(object) || !validate(YTRelationshipType.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, new Parameter<YTRelationshipType.Builder>(parameter.getContext())
            .setBuilder(new YTRelationshipType.Builder())
            .setClazz(YTRelationshipType.class))
            .setValidTargetTypes(buildListQName(buildListString(map.get("valid_target_types"),
                new Parameter<List<String>>(parameter.getContext()).addContext("valid_target_types")
            )))
            .setInterfaces(buildMap(map.get("interfaces"),
                new Parameter<YTInterfaceDefinition>(parameter.getContext()).addContext("interfaces")
                    .setValue("TRelationshipType")
                    .setBuilderOO(this::buildInterfaceDefinition)
            ))
            .build();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public YTInterfaceDefinition buildInterfaceDefinition(Object object, Parameter<YTInterfaceDefinition> parameter) {
        if (Objects.isNull(object) || !validate(YTInterfaceType.class, object, parameter)) {
            return null;
        }
        Map<String, Object> map = (Map<String, Object>) object;
        YTInterfaceDefinition.Builder<?> output = new YTInterfaceDefinition.Builder<>()
            .setType(buildQName(stringValue(map.get("type"))))
            .setInputs(buildParameterDefinitions(map.get("inputs"),
                new Parameter<>(parameter.getContext()).addContext("inputs")
                    .setValue(parameter.getValue())
            ));
        Map<String, YTOperationDefinition> operations = buildMap(map.get("operations"),
            new Parameter<YTOperationDefinition>(parameter.getContext())
                .setValue(parameter.getValue())
                .addContext("(operation)")
                .setBuilderOO(this::buildOperationDefinition)
                .setFilter(this::filterInterfaceAssignmentOperation)
        );
        output.setOperations(operations);
        return output.build();
    }

    @Nullable
    public YTNodeType buildNodeType(Object object, Parameter<YTNodeType> parameter) {
        if (Objects.isNull(object) || !validate(YTNodeType.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, new Parameter<YTNodeType.Builder>(parameter.getContext())
            .setBuilder(new YTNodeType.Builder())
            .setClazz(YTNodeType.class))
            .setRequirements(buildList(map, "requirements", this::buildMapRequirementDefinition, parameter))
            .setCapabilities(buildMap(map, "capabilities", this::buildCapabilityDefinition, parameter))
            .setInterfaces(buildMap(map.get("interfaces"),
                new Parameter<YTInterfaceDefinition>(parameter.getContext()).addContext("interfaces")
                    .setValue("TNodeType")
                    .setBuilderOO(this::buildInterfaceDefinition)
            ))
            .setArtifacts(buildMap(map, "artifacts", this::buildArtifactDefinition, parameter))
            .build();
    }

    @Nullable
    public YTMapRequirementDefinition buildMapRequirementDefinition(Object object, Parameter<YTMapRequirementDefinition> parameter) {
        YTMapRequirementDefinition result = new YTMapRequirementDefinition();
        put(result, parameter.getValue(), buildRequirementDefinition(object, new Parameter<>(parameter.getContext())));
        return result;
    }

    @Nullable
    public YTRequirementDefinition buildRequirementDefinition(Object object, Parameter<YTRequirementDefinition> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String) {
            return new YTRequirementDefinition.Builder(buildQName(stringValue(object))).build();
        }
        if (!validate(YTRequirementDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTRequirementDefinition.Builder(buildQName(stringValue(map.get("capability"))))
            .setNode(buildQName(stringValue(map.get("node"))))
            .setRelationship(buildRelationshipDefinition(map.get("relationship"),
                new Parameter<YTRelationshipDefinition>(parameter.getContext()).addContext("relationship")
            ))
            .setOccurrences(buildListString(map.get("occurrences"),
                new Parameter<List<String>>(parameter.getContext()).addContext("occurrences")
            ))
            .build();
    }

    @Nullable
    public YTRelationshipDefinition buildRelationshipDefinition(Object object, Parameter<YTRelationshipDefinition> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String) {
            return new YTRelationshipDefinition.Builder(buildQName(stringValue(object))).build();
        }
        if (!validate(YTRelationshipDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTRelationshipDefinition.Builder(buildQName(stringValue(map.get("type"))))
            .setInterfaces(buildMap(map.get("interfaces"),
                new Parameter<YTInterfaceDefinition>(parameter.getContext()).addContext("interfaces")
                    .setValue("TRelationshipDefinition")
                    .setBuilderOO(this::buildInterfaceDefinition)
            ))
            .build();
    }

    @Nullable
    public YTCapabilityDefinition buildCapabilityDefinition(Object object, Parameter<YTCapabilityDefinition> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String) {
            return new YTCapabilityDefinition.Builder(buildQName(stringValue(object))).build();
        }
        if (!validate(YTCapabilityDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTCapabilityDefinition.Builder(buildQName(stringValue(map.get("type"))))
            .setDescription(buildDescription(map.get("description")))
            .setOccurrences(buildListString(map.get("occurrences"),
                new Parameter<List<String>>(parameter.getContext()).addContext("occurrences")
            ))
            .setValidSourceTypes(buildListQName(buildListString(map.get("valid_source_types"),
                new Parameter<List<String>>(parameter.getContext()).addContext("valid_source_types")
            )))
            .setProperties(buildMap(map.get("properties"),
                new Parameter<YTPropertyDefinition>(parameter.getContext()).addContext("properties")
                    .setClazz(YTPropertyDefinition.class)
                    .setBuilderOO(this::buildPropertyDefinition)
            ))
            .setAttributes(buildMap(map, "attributes", this::buildAttributeDefinition, parameter))
            .build();
    }

    @Nullable
    public YTArtifactDefinition buildArtifactDefinition(Object object, Parameter<YTArtifactDefinition> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String) {
            String file = stringValue(object);
            if (Objects.isNull(file)) {
                return null;
            }
            // TODO infer artifact type and mime type from file URI
            String type = file.substring(file.lastIndexOf("."), file.length());
            return new YTArtifactDefinition.Builder(buildQName(type), file).build();
        }
        if (!validate(YTArtifactDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;

        String file;
        if (map.get("file") instanceof String) {
            file = stringValue(map.get("file"));
        } else {
            file = null;
            assert false;
        }
        return new YTArtifactDefinition.Builder(buildQName(stringValue(map.get("type"))), file)
            .setRepository(stringValue(map.get("repository")))
            .setDescription(buildDescription(map.get("description")))
            .setDeployPath(stringValue(map.get("deploy_path")))
            .setProperties(buildMap(map.get("properties"),
                new Parameter<YTPropertyAssignment>().addContext("properties")
                    .setBuilderOO(this::buildPropertyAssignment)
            ))
            .build();
    }

    @Nullable
    public YTGroupType buildGroupType(Object object, Parameter<YTGroupType> parameter) {
        if (Objects.isNull(object) || !validate(YTGroupType.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, new Parameter<YTGroupType.Builder>(parameter.getContext())
            .setBuilder(new YTGroupType.Builder())
            .setClazz(YTGroupType.class))
            .setMembers(buildListQName(buildListString(map.get("members"),
                new Parameter<List<String>>(parameter.getContext()).addContext("members")
            )))
            .build();
    }

    @Nullable
    public YTPolicyType buildPolicyType(Object object, Parameter<YTPolicyType> parameter) {
        if (Objects.isNull(object) || !validate(YTPolicyType.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, new Parameter<YTPolicyType.Builder>(parameter.getContext())
            .setBuilder(new YTPolicyType.Builder())
            .setClazz(YTPolicyType.class))
            .setTargets(buildListQName(buildListString(map.get("targets"),
                new Parameter<List<String>>(parameter.getContext()).addContext("targets")
            )))
            .setTriggers(map.get("triggers"))
            .build();
    }

    @Nullable
    public YTParameterDefinition buildParameterDefinition(Object object, Parameter<YTParameterDefinition> parameter) {
        if (Objects.isNull(object) || !validate(YTParameterDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        String type = stringValue(map.get("type"));
        if (type == null) {
            type = "string";
        }
        return new YTParameterDefinition.Builder()
            .setType(buildQName(type))
            .setDescription(buildDescription(map.get("description")))
            .setRequired(buildRequired(map.get("required")))
            .setDefault(map.get("default"))
            .setStatus(buildStatus(map.get("status")))
            .setConstraints(buildList(map, "constraints", this::buildConstraintClause, parameter))
            .setEntrySchema(buildSchemaDefinition(map.get("entry_schema"),
                new Parameter<YTSchemaDefinition>(parameter.getContext()).addContext("entry_schema")
            ))
            .setKeySchema(buildSchemaDefinition(map.get("key_schema"),
                new Parameter<YTSchemaDefinition>(parameter.getContext()).addContext("key_schema")))
            .setValue(map.get("value"))
            .build();
    }

    @Nullable
    public YTParameterDefinition buildParameterAssignment(Object object, Parameter<YTParameterDefinition> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        return new YTParameterDefinition.Builder()
            .setValue(object)
            .build();
    }

    @Nullable
    public YTNodeTemplate buildNodeTemplate(Object object, Parameter<YTNodeTemplate> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (!validate(YTNodeTemplate.class, object, parameter)) {
            LOGGER.info("Validation failed when trying to deserialize NodeTemplate");
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTNodeTemplate.Builder(buildQName(stringValue(map.get("type"))))
            .setDescription(buildDescription(map.get("description")))
            .setMetadata(buildMetadata(map.get("metadata")))
            .setDirectives(buildListString(map.get("directives"),
                new Parameter<List<String>>(parameter.getContext()).addContext("directives")
            ))
            .setProperties(buildMap(map, "properties", this::buildPropertyAssignment, parameter))
            .setAttributes(buildMap(map, "attributes", this::buildAttributeAssignment, parameter))
            .setRequirements(buildList(map, "requirements", this::buildMapRequirementAssignment, parameter))
            .setCapabilities(buildMap(map, "capabilities", this::buildCapabilityAssignment, parameter))
            .setInterfaces(buildMap(map.get("interfaces"),
                new Parameter<YTInterfaceAssignment>(parameter.getContext()).addContext("interfaces")
                    .setValue("TNodeTemplate")
                    .setBuilderOO(this::buildInterfaceAssignment)
            ))
            .setArtifacts(buildMap(map, "artifacts", this::buildArtifactDefinition, parameter))
            .setNodeFilter(buildNodeFilterDefinition(map.get("node_filter"),
                new Parameter<YTNodeFilterDefinition>(parameter.getContext()).addContext("node_filter")
            ))
            .setCopy(buildQName(stringValue(map.get("copy"))))
            .build();
    }

    @Nullable
    public YTAttributeAssignment buildAttributeAssignment(Object object, Parameter<YTAttributeAssignment> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (!(object instanceof Map)) {
            // Attribute assignment with simple value
            return new YTAttributeAssignment.Builder().setValue(object).build();
        } else if (!((Map) object).containsKey("value")) {
            // Attribute assignment with <attribute_value_expression>
            return new YTAttributeAssignment.Builder().setValue(object).build();
        } else if (((Map) object).containsKey("value") && validate(YTAttributeAssignment.class, object, parameter)) {
            // Attribute assignment with extended notation
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) object;
            return new YTAttributeAssignment.Builder()
                .setDescription(buildDescription(map.get("description")))
                .setValue(map.get("value"))
                .build();
        } else {
            return null;
        }
    }

    @Nullable
    public YTMapRequirementAssignment buildMapRequirementAssignment(Object object, Parameter<YTMapRequirementAssignment> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        YTMapRequirementAssignment result = new YTMapRequirementAssignment();
        put(result, stringValue(parameter.getValue()), buildRequirementAssignment(object,
            new Parameter<>(parameter.getContext())
        ));
        return result;
    }

    @Nullable
    public YTRequirementAssignment buildRequirementAssignment(Object object, Parameter<YTRequirementAssignment> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String) {
            return new YTRequirementAssignment.Builder().setNode(buildQName(stringValue(object))).build();
        }
        if (!validate(YTRequirementAssignment.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTRequirementAssignment.Builder()
            .setCapability(buildQName(stringValue(map.get("capability"))))
            .setNode(buildQName(stringValue(map.get("node"))))
            .setRelationship(buildRelationshipAssignment(map.get("relationship"),
                new Parameter<YTRelationshipAssignment>(parameter.getContext()).addContext("relationship")
            ))
            .setNodeFilter(buildNodeFilterDefinition(map.get("node_filter"),
                new Parameter<YTNodeFilterDefinition>(parameter.getContext()).addContext("node_filter")
            ))
            .setOccurrences(buildListString(map.get("occurrences"),
                new Parameter<List<String>>(parameter.getContext()).addContext("occurrences")
            ))
            .build();
    }

    @Nullable
    public YTRelationshipAssignment buildRelationshipAssignment(Object object, Parameter<YTRelationshipAssignment> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String) {
            return new YTRelationshipAssignment.Builder(buildQName(stringValue(object))).build();
        }
        if (!validate(YTRelationshipAssignment.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTRelationshipAssignment.Builder(buildQName(stringValue(map.get("type"))))
            .setProperties(buildMap(map, "properties", this::buildPropertyAssignment, parameter))
            .setInterfaces(buildMap(map, "interfaces", this::buildInterfaceAssignment, parameter))
            .build();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public YTInterfaceAssignment buildInterfaceAssignment(Object object, Parameter<YTInterfaceAssignment> parameter) {
        if (Objects.isNull(object) || !validate(YTInterfaceAssignment.class, object, parameter)) {
            return null;
        }
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTInterfaceAssignment.Builder()
            .setType(buildQName(stringValue(map.get("type"))))
            .setInputs(buildParameterDefinitions(map.get("inputs"),
                new Parameter<>(parameter.getContext())
                    .setValue("TInterfaceAssignment")
            ))
            .setOperations(buildMap(map.get("operations"),
                new Parameter<YTOperationDefinition>(parameter.getContext()).addContext("(operations)")
                    .setBuilderOO(this::buildOperationDefinition)
                    // .setFilter(this::filterInterfaceAssignmentOperation)
                    .setValue("TInterfaceAssignment")
            ))
            .build();
    }

    private Boolean filterInterfaceAssignmentOperation(Map.Entry<String, Object> entry) {
        if (Objects.isNull(entry.getKey())) {
            return false;
        }
        Set<String> keys = Stream.of("type", "inputs").collect(Collectors.toSet());
        return !keys.contains(entry.getKey());
    }

    @Nullable
    public YTNodeFilterDefinition buildNodeFilterDefinition(Object object, Parameter<YTNodeFilterDefinition> parameter) {
        if (Objects.isNull(object) || !validate(YTNodeFilterDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTNodeFilterDefinition.Builder()
            .setProperties(buildList(map, "properties", this::buildMapPropertyDefinition, parameter))
            .setCapabilities(buildList(map, "capabilities", this::buildMapObjectValue, parameter))
            .build();
    }

    @Nullable
    public YTMapPropertyFilterDefinition buildMapPropertyDefinition(Object object, Parameter<YTMapPropertyFilterDefinition> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        YTMapPropertyFilterDefinition result = new YTMapPropertyFilterDefinition();
        put(result, stringValue(parameter.getValue()), buildPropertyFilterDefinition(object,
            new Parameter<>(parameter.getContext())));
        return result;
    }

    @Nullable
    public YTPropertyFilterDefinition buildPropertyFilterDefinition(Object object, Parameter<YTPropertyFilterDefinition> parameter) {
        if (Objects.isNull(object) || !validate(YTPropertyFilterDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTPropertyFilterDefinition.Builder()
            .setConstraints(buildList(map, "constraints", this::buildConstraintClause, parameter))
            .build();
    }

    @Nullable
    public YTMapObject buildMapObjectValue(Object object, Parameter<YTMapObject> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        YTMapObject result = new YTMapObject();
        put(result, stringValue(parameter.getValue()), object);
        return result;
    }

    @Nullable
    public YTCapabilityAssignment buildCapabilityAssignment(Object object, Parameter<YTCapabilityAssignment> parameter) {
        if (Objects.isNull(object) || !validate(YTCapabilityAssignment.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTCapabilityAssignment.Builder()
            .setProperties(buildMap(map, "properties", this::buildPropertyAssignment, parameter))
            .setAttributes(buildMap(map, "attributes", this::buildAttributeAssignment, parameter))
            .build();
    }

    @Nullable
    public YTRelationshipTemplate buildRelationshipTemplate(Object object, Parameter<YTRelationshipTemplate> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (!validate(YTRelationshipTemplate.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTRelationshipTemplate.Builder(buildQName(stringValue(map.get("type"))))
            .setDescription(buildDescription(map.get("description")))
            .setMetadata(buildMetadata(map.get("metadata")))
            .setProperties(buildMap(map, "properties", this::buildPropertyAssignment, parameter))
            .setAttributes(buildMap(map, "attributes", this::buildAttributeAssignment, parameter))
            .setInterfaces(buildMap(map.get("interfaces"),
                new Parameter<YTInterfaceDefinition>(parameter.getContext()).addContext("interfaces")
                    .setValue("TRelationshipTemplate")
                    .setBuilderOO(this::buildInterfaceDefinition)
            ))
            .setCopy(buildQName(stringValue(map.get("copy"))))
            .build();
    }

    @Nullable
    public YTGroupDefinition buildGroupDefinition(Object object, Parameter<YTGroupDefinition> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (!validate(YTGroupDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTGroupDefinition.Builder(buildQName(stringValue(map.get("type"))))
            .setDescription(buildDescription(map.get("description")))
            .setMetadata(buildMetadata(map.get("metadata")))
            .setProperties(buildMap(map, "properties", this::buildPropertyAssignment, parameter))
            .setMembers(buildListQName(buildListString(map.get("members"),
                new Parameter<List<String>>(parameter.getContext()).addContext("members")
            )))
            .build();
    }

    @Nullable
    public YTMapPolicyDefinition buildMapPolicyDefinition(Object object, Parameter<YTMapPolicyDefinition> parameter) {
        YTMapPolicyDefinition result = new YTMapPolicyDefinition();
        put(result, parameter.getValue(), buildPolicyDefinition(object, new Parameter<>(parameter.getContext())));
        return result;
    }

    @Nullable
    public YTPolicyDefinition buildPolicyDefinition(Object object, Parameter<YTPolicyDefinition> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (!validate(YTPolicyDefinition.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTPolicyDefinition.Builder(buildQName(stringValue(map.get("type"))))
            .setDescription(buildDescription(map.get("description")))
            .setMetadata(buildMetadata(map.get("metadata")))
            .setProperties(buildMap(map, "properties", this::buildPropertyAssignment, parameter))
            .setTargets(buildListQName(buildListString(map.get("targets"),
                new Parameter<List<String>>(parameter.getContext()).addContext("targets")
            )))
            .build();
    }

    @Nullable
    public YTSubstitutionMappings buildSubstitutionMappings(Object object, Parameter<Object> parameter) {
        if (Objects.isNull(object) || !validate(YTSubstitutionMappings.class, object, parameter)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new YTSubstitutionMappings.Builder()
            .setNodeType(buildQName(stringValue(map.get("node_type"))))
            .setCapabilities(buildMap(map, "capabilities", this::buildStringList, parameter))
            .setRequirements(buildMap(map, "requirements", this::buildStringList, parameter))
            .build();
    }

    @Nullable
    public YTListString buildStringList(Object object, Parameter<YTListString> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<String> tmp = (List<String>) object;
        YTListString stringList = new YTListString();
        stringList.addAll(tmp);
        return stringList;
    }

    @Nullable
    private String stringValue(@Nullable Object object) {
        if (Objects.isNull(object)) {
            return null;
        }
        return String.valueOf(object);
    }

    private String stringValue(@Nullable Object object, String defaultValue) {
        if (Objects.isNull(object)) {
            return defaultValue;
        }
        return String.valueOf(object);
    }

    private <T> void put(Map<String, T> map, String key, T value) {
        if (Objects.nonNull(map) && Objects.nonNull(key) && Objects.nonNull(value)) {
            map.put(key, value);
        }
    }

    private <T> boolean nonNull(Map.Entry<String, T> entry) {
        return Objects.nonNull(entry) && Objects.nonNull(entry.getKey()) && Objects.nonNull(entry.getValue());
    }

    private <T> boolean nonNull(Pair<String, T> pair) {
        return Objects.nonNull(pair) && Objects.nonNull(pair.getOne()) && Objects.nonNull(pair.getTwo());
    }

    private <T, K> Map<String, T> buildListMap(Map<String, Object> map, String key,
                                               BiFunction<Object, Parameter<T>, T> function, Parameter<K> parameter) {
        return buildListMap(map.get(key),
            new Parameter<T>(parameter.getContext()).addContext(key)
                .setBuilderOO(function)
        );
    }

    private <T> Map<String, T> buildListMap(Object object, Parameter<T> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (!(object instanceof List)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        return list.stream()
            .filter(Objects::nonNull)
            .flatMap(map -> map.entrySet().stream())
            .filter(Objects::nonNull)
            .map(entry -> Tuples.pair(entry.getKey(), parameter.getBuilderOO().apply(entry.getValue(),
                new Parameter<T>(parameter.getContext())
                    .setClazz(parameter.getClazz())
                    .setValue(entry.getKey())))
            )
            .collect(Collectors.toMap(Pair::getOne, Pair::getTwo));
    }

    private <T, K> Map<String, T> buildMap(Map<String, Object> map, String key,
                                           BiFunction<Object, Parameter<T>, T> function, Parameter<K> parameter) {
        return buildMap(map.get(key),
            new Parameter<T>(parameter.getContext()).addContext(key)
                .setBuilderOO(function)
        );
    }

    private <T, K> Map<String, T> buildMap(Map<String, Object> map, String key,
                                           BiFunction<Object, Parameter<T>, T> function,
                                           Class<?> clazz, Parameter<K> parameter) {
        return buildMap(map.get(key),
            new Parameter<T>(parameter.getContext()).addContext(key)
                .setClazz(clazz)
                .setBuilderOO(function)
        );
    }

    private <T> Map<String, T> buildMap(Object object, Parameter<T> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        //if (Objects.isNull(parameter.getFilter())) parameter.setFilter(this::nonNull);
        Map<String, T> output = buildStream(object, parameter)
            .map(entry -> {
                return Tuples.pair(entry.getKey(), parameter.getBuilderOO().apply(
                    entry.getValue(),
                    new Parameter<T>(parameter.getContext()).addContext(entry.getKey())
                        .setClazz(parameter.getClazz())
                        .setValue(parameter.getValue())
                    )
                );
            })
            .filter(this::nonNull)
            .collect(Collectors.toMap(Pair::getOne, Pair::getTwo));
        return output;
    }

    private <T> Stream<Map.Entry<String, Object>> buildStream(Object object, Parameter<T> parameter) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return map.entrySet().stream()
            .filter(Optional.ofNullable(parameter.getFilter()).orElse(entry -> true));
    }

    private <T> List<T> buildList(Map<String, Object> map, String key, BiFunction<Object, Parameter<T>, T> function, Parameter<?> parameter) {
        return buildList(map.get(key),
            new Parameter<T>(parameter.getContext()).addContext(key)
                .setBuilderOO(function)
        );
    }

    private <T> List<T> buildList(Object object, Parameter<T> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>) object;
        return list.stream()
            .filter(Objects::nonNull)
            .flatMap(map -> map.entrySet().stream())
            .filter(Objects::nonNull)
            .map(entry -> parameter.getBuilderOO().apply(entry.getValue(),
                new Parameter<T>(parameter.getContext())
                    .setValue(entry.getKey())
            ))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static class Parameter<T> {
        private Set<String> context;
        private String value;
        private Class<?> clazz;
        private T builder;

        private Predicate<Map.Entry<String, Object>> filter;
        private BiFunction<Object, Parameter<T>, @Nullable T> builderOO;

        public Parameter() {
            context = new LinkedHashSet<>();
        }

        public Parameter(Set<String> context) {
            this.context = new LinkedHashSet<>(context);
        }

        public Set<String> getContext() {
            return context;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public Parameter<T> setClazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public T getBuilder() {
            return builder;
        }

        public Parameter<T> setBuilder(T builder) {
            this.builder = builder;
            return this;
        }

        public Predicate<Map.Entry<String, Object>> getFilter() {
            return filter;
        }

        public Parameter<T> setFilter(Predicate<Map.Entry<String, Object>> filter) {
            this.filter = filter;
            return this;
        }

        public BiFunction<Object, Parameter<T>, T> getBuilderOO() {
            return builderOO;
        }

        public Parameter<T> setBuilderOO(BiFunction<Object, Parameter<T>, T> builderOO) {
            this.builderOO = builderOO;
            return this;
        }

        public String getValue() {
            return value;
        }

        public Parameter<T> setValue(String value) {
            this.value = value;
            return this;
        }

        public Parameter<T> copy() {
            return new Parameter<>(this.context);
        }

        public Parameter<T> addContext(String value) {
            context.add(value);
            return this;
        }

        @Override
        public String toString() {
            return context.stream().collect(Collectors.joining(":"));
        }
    }
}

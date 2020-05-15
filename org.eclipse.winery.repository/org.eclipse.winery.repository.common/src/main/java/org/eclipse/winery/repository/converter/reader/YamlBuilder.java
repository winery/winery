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
import org.eclipse.winery.model.tosca.yaml.support.TMapPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.support.TMapPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.tosca.datatypes.Credential;
import org.eclipse.winery.model.converter.support.exception.InvalidToscaSyntax;
import org.eclipse.winery.model.converter.support.exception.MultiException;
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
        this.prefix2Namespace.put("tosca", Namespaces.TOSCA_NS);

        if (Objects.isNull(object)) return;

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
            if (!clazz.equals(TInterfaceType.class)) {
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
    public TServiceTemplate buildServiceTemplate(Object object) throws MultiException {
        if (Objects.isNull(object) || !validate(TServiceTemplate.class, object, new Parameter<>().addContext("service_template")))
            return null;
        Parameter<Object> parameter = new Parameter<>();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        // build map between prefix and namespaces
        initPrefix2Namespace(map.get("imports"));

        TServiceTemplate.Builder builder = new TServiceTemplate.Builder(stringValue(
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
        if (this.exception.hasException()) throw this.exception;
        return builder.build();
    }

    @Nullable
    public TTopologyTemplateDefinition buildTopologyTemplate(Object object, Parameter<Object> parameter) {
        if (Objects.isNull(object) || !validate(TTopologyTemplateDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TTopologyTemplateDefinition.Builder()
            .setDescription(buildDescription(map.get("description")))
            .setInputs(buildMap(map, "inputs", this::buildParameterDefinition, parameter))
            .setNodeTemplates(buildMap(map, "node_templates", this::buildNodeTemplate, parameter))
            .setRelationshipTemplates(buildMap(map, "relationship_templates", this::buildRelationshipTemplate, parameter))
            .setGroups(buildMap(map, "groups", this::buildGroupDefinition, parameter))
            .setPolicies(buildMap(map, "policies", this::buildPolicyDefinition, parameter))
            .setOutputs(buildMap(map, "outputs", this::buildParameterDefinition, parameter))
            .setSubstitutionMappings(buildSubstitutionMappings(map.get("substitution_mappings"),
                parameter.copy().addContext("substitution_mappings")
            ))
            .build();
    }

    @Nullable
    public Metadata buildMetadata(Object object) {
        if (Objects.isNull(object)) return null;
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
        if (Objects.isNull(object)) return "";
        return stringValue(object);
    }

    @Nullable
    public TRepositoryDefinition buildRepositoryDefinition(Object object, Parameter<TRepositoryDefinition> parameter) {
        if (Objects.isNull(object)) return new TRepositoryDefinition();
        if (object instanceof String) return new TRepositoryDefinition.Builder(stringValue(object)).build();
        if (!validate(TRepositoryDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRepositoryDefinition.Builder(stringValue(map.get("url")))
            .setDescription(buildDescription(map.get("description")))
            .setCredential(buildCredential(map.get("credential"),
                new Parameter<Credential>(parameter.getContext()).addContext("credential")
            ))
            .build();
    }

    @Nullable
    public Credential buildCredential(Object object, Parameter<Credential> parameter) {

        if (Objects.isNull(object)) return null;
        if (!validate(Credential.class, object, parameter)) return null;
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
    public TMapImportDefinition buildMapImportDefinition(Object object, Parameter<TMapImportDefinition> parameter) {
        TMapImportDefinition mapImportDefinition = new TMapImportDefinition();
        mapImportDefinition.put(stringValue(parameter.getValue()), buildImportDefinition(object,
            new Parameter<>(parameter.getContext())
        ));
        return mapImportDefinition;
    }

    @Nullable
    public TImportDefinition buildImportDefinition(Object object, Parameter<TImportDefinition> parameter) {
        if (Objects.isNull(object)) return new TImportDefinition();
        if (object instanceof String) return new TImportDefinition.Builder(stringValue(object)).build();
        if (!validate(TImportDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TImportDefinition.Builder(stringValue(map.get("file")))
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
    public TArtifactType buildArtifactType(Object object, Parameter<TArtifactType> parameter) {
        if (Objects.isNull(object) || !validate(TArtifactType.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object,
            new Parameter<TArtifactType.Builder>(parameter.getContext())
                .setBuilder(new TArtifactType.Builder())
                .setClazz(TArtifactType.class))
            .setMimeType(stringValue(map.get("mime_type")))
            .setFileExt(buildListString(map.get("file_ext"),
                new Parameter<List<String>>(parameter.getContext()).addContext("file_ext")
            ))
            .build();
    }

    @NonNull
    public <T extends TEntityType.Builder<T>> T buildEntityType(Object object, Parameter<T> parameter) {
        if (Objects.isNull(object) || !validate(parameter.getClazz(), object, parameter)) return parameter.getBuilder();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return parameter.getBuilder()
            .setDescription(buildDescription(map.get("description")))
            .setVersion(buildVersion(map.get("version")))
            .setDerivedFrom(buildQName(stringValue(map.get("derived_from"))))
            .setProperties(buildMap(map, "properties", this::buildPropertyDefinition, TPropertyDefinition.class, parameter))
            .setAttributes(buildMap(map, "attributes", this::buildAttributeDefinition, parameter))
            .setMetadata(buildMetadata(map.get("metadata")));
    }

    @Nullable
    public TVersion buildVersion(Object object) {
        return Objects.isNull(object) ? null : new TVersion(stringValue(object));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> TPropertyDefinition buildPropertyDefinition(Object object, Parameter<T> parameter) {
        if (Objects.isNull(object)) return new TPropertyDefinition();
        if (!validate(parameter.getClazz(), object, parameter)) return null;
        Map<String, Object> map = (Map<String, Object>) object;
        String type = stringValue(map.get("type"));
        if (type == null) type = "string";
        return new TPropertyDefinition.Builder<>(buildQName(type))
            .setDescription(buildDescription(map.get("description")))
            .setRequired(buildRequired(map.get("required")))
            .setDefault(map.get("default"))
            .setStatus(buildStatus(map.get("status")))
            .addConstraints(buildList(map, "constraints", this::buildConstraintClause, parameter))
            .setEntrySchema(buildEntrySchema(map.get("entry_schema"),
                new Parameter<TEntrySchema>(parameter.getContext()).addContext("entry_schema")
            ))
            .build();
    }

    @Nullable
    public Boolean buildRequired(Object object) {
        if (Objects.isNull(object)) return true;
        if (object instanceof String) return "true".equals(object) ? Boolean.TRUE : Boolean.FALSE;
        return object instanceof Boolean ? (Boolean) object : Boolean.FALSE;
    }

    @Nullable
    public TStatusValue buildStatus(Object object) {
        String status = stringValue(object);
        if (Objects.isNull(status)) return null;
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
    public TConstraintClause buildConstraintClause(Object object, Parameter<TConstraintClause> parameter) {
        if (Objects.isNull(object)) return null;
        TConstraintClause constraintClause = new TConstraintClause();
        constraintClause.setKey(parameter.getValue());
        switch (parameter.getValue()) {
            case "in_range":
                constraintClause.setList(buildListString(object,
                    new Parameter<List<String>>(parameter.getContext()).addContext("in_range")));
                break;
            case "valid_values":
                constraintClause.setList(buildListString(object,
                    new Parameter<List<String>>(parameter.getContext()).addContext("valid_values")));
                break;
            default:
                constraintClause.setKey(parameter.getValue());
                constraintClause.setValue(stringValue(object));
        }
        return constraintClause;
    }

    @Nullable
    public List<Object> buildListObject(Object object) {
        if (Objects.isNull(object)) return null;
        @SuppressWarnings("unchecked")
        List<Object> result = (List<Object>) object;
        return result;
    }

    @Nullable
    public TEntrySchema buildEntrySchema(Object object, Parameter<TEntrySchema> parameter) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String) {
            return new TEntrySchema.Builder().setType(buildQName(stringValue(object))).build();
        } else {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) object;
            return new TEntrySchema.Builder()
                .setType(buildQName(stringValue(map.get("type"))))
                .setDescription(buildDescription(map.get("description")))
                .setConstraints(buildList(map, "constraints", this::buildConstraintClause, parameter))
                .build();
        }
    }

    @Nullable
    public <T> TAttributeDefinition buildAttributeDefinition(Object object, Parameter<T> parameter) {
        if (Objects.isNull(object)) return new TAttributeDefinition();
        if (!validate(TAttributeDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TAttributeDefinition.Builder(buildQName(stringValue(map.get("type"))))
            .setDescription(buildDescription(map.get("description")))
            .setDefault(map.get("default"))
            .setStatus(buildStatus(map.get("status")))
            .setEntrySchema(buildEntrySchema(map.get("entry_schema"),
                new Parameter<TEntrySchema>(parameter.getContext()).addContext("entry_schema")
            ))
            .build();
    }

    @Nullable
    public List<String> buildListString(Object object, Parameter<List<String>> parameter) {
        if (Objects.isNull(object)) return null;
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
    public TDataType buildDataType(Object object, Parameter<TDataType> parameter) {
        if (Objects.isNull(object) || !validate(TDataType.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object,
            new Parameter<TDataType.Builder>(parameter.getContext())
                .setBuilder(new TDataType.Builder())
                .setClazz(TDataType.class))
            .setConstraints(buildList(map, "constraints", this::buildConstraintClause, parameter))
            .build();
    }

    @Nullable
    public TCapabilityType buildCapabilityType(Object object, Parameter<TCapabilityType> parameter) {
        if (Objects.isNull(object) || !validate(TCapabilityType.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object,
            new Parameter<TCapabilityType.Builder>(parameter.getContext())
                .setBuilder(new TCapabilityType.Builder())
                .setClazz(TCapabilityType.class))
            .setValidSourceTypes(buildListQName(buildListString(map.get("valid_source_types"),
                new Parameter<List<String>>(parameter.getContext()).addContext("valid_source_types")
            )))
            .build();
    }

    @Nullable
    public List<QName> buildListQName(List<String> list) {
        if (Objects.isNull(list) || list.isEmpty()) return null;
        return list.stream().map(this::buildQName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    public TInterfaceType buildInterfaceType(Object object, Parameter<TInterfaceType> parameter) {
        if (Objects.isNull(object) || !validate(TInterfaceType.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object,
            new Parameter<TInterfaceType.Builder>(parameter.getContext())
                .setBuilder(new TInterfaceType.Builder())
                .setClazz(TInterfaceType.class))
            .setInputs(buildMap(map, "inputs", this::buildPropertyDefinition,
                TPropertyDefinition.class, parameter))
            .setOperations(buildMap(object,
                new Parameter<TOperationDefinition>(parameter.getContext()).addContext("(operations)")
                    .setValue("TInterfaceType")
                    .setBuilderOO(this::buildOperationDefinition)
                    .setFilter(this::filterInterfaceTypeOperation)
            ))
            .setDerivedFrom(buildQName(stringValue(map.get("derived_from"))))
            .build();
    }

    private Boolean filterInterfaceTypeOperation(Map.Entry<String, Object> entry) {
        if (Objects.isNull(entry.getKey())) return false;
        Set<String> keys = Stream.of("inputs", "description", "version", "derived_from",
            "properties", "attributes", "metadata").collect(Collectors.toSet());
        return !keys.contains(entry.getKey());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public TOperationDefinition buildOperationDefinition(Object object, Parameter<TOperationDefinition> parameter) {
        if (Objects.isNull(object) || !validate(TOperationDefinition.class, object, parameter)) return null;
        if (object instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) object;
            String description = buildDescription(map.get("description"));
            Map<String, TParameterDefinition> inputs = buildParameterDefinitions(map.get("inputs"),
                new Parameter<>(parameter.getContext()).addContext("inputs").setValue(parameter.getValue())
            );
            Map<String, TParameterDefinition> outputs = buildParameterDefinitions(map.get("outputs"),
                new Parameter<>(parameter.getContext()).addContext("outputs").setValue(parameter.getValue())
            );
            TImplementation implementation = buildImplementation(map.get("implementation"),
                new Parameter<TImplementation>(parameter.getContext()).addContext("implementation")
            );
            return new TOperationDefinition.Builder()
                .setDescription(description)
                .setInputs(inputs)
                .setOutputs(outputs)
                .setImplementation(implementation)
                .build();
        }
        return new TOperationDefinition();
    }

    @Nullable
    public Map<String, TParameterDefinition> buildParameterDefinitions(Object object, Parameter<Object> parameter) {
        if (Objects.isNull(object)) return null;
        String context = stringValue(parameter.getValue());
        if ("TNodeType".equals(context) ||
            "TRelationshipType".equals(context) ||
            "TGroupType".equals(context) ||
            "TInterfaceType".equals(context)) {
            return buildMap(object, new Parameter<TParameterDefinition>(parameter.getContext())
                .setClazz(TParameterDefinition.class)
                .setBuilderOO(this::buildParameterDefinition));
        } else {
            return buildMap(object, new Parameter<TParameterDefinition>(parameter.getContext())
                .setBuilderOO(this::buildParameterAssignment));
        }
    }

    @Nullable
    public Map<String, TPropertyAssignmentOrDefinition> buildPropertyAssignmentOrDefinition(Object object, Parameter<Object> parameter) {
        if (Objects.isNull(object)) return null;
        String context = stringValue(parameter.getValue());
        if ("TNodeType".equals(context) ||
            "TRelationshipType".equals(context) ||
            "TGroupType".equals(context) ||
            "TInterfaceType".equals(context)) {
            return buildMap(object, new Parameter<TPropertyAssignmentOrDefinition>(parameter.getContext())
                .setClazz(TPropertyDefinition.class)
                .setBuilderOO(this::buildPropertyDefinition));
        } else {
            return buildMap(object, new Parameter<TPropertyAssignmentOrDefinition>(parameter.getContext())
                .setBuilderOO(this::buildPropertyAssignment));
        }
    }

    @Nullable
    public <T> TPropertyAssignment buildPropertyAssignment(Object object, Parameter<T> parameter) {
        return new TPropertyAssignment.Builder()
            .setValue(object)
            .build();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public TImplementation buildImplementation(Object object, Parameter<TImplementation> parameter) {
        if (Objects.isNull(object)) return null;
        if (object instanceof String) return new TImplementation(stringValue(object));
        if (object instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) object;
            TImplementation implementation = new TImplementation();
            implementation.setPrimaryArtifactName(stringValue(map.get("primary")));
            implementation.setDependencyArtifactNames(buildListString(map.get("dependencies"),
                new Parameter<List<String>>(parameter.getContext()).addContext("dependencies")
            ));
            implementation.setOperationHost(stringValue(map.get("operation_host")));
            String timeout = stringValue(map.get("timeout"));
            implementation.setTimeout(timeout == null ? null : Integer.valueOf(timeout));
            return implementation;
        }
        return null;
    }

    @Nullable
    public TRelationshipType buildRelationshipType(Object object, Parameter<TRelationshipType> parameter) {
        if (Objects.isNull(object) || !validate(TRelationshipType.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, new Parameter<TRelationshipType.Builder>(parameter.getContext())
            .setBuilder(new TRelationshipType.Builder())
            .setClazz(TRelationshipType.class))
            .setValidTargetTypes(buildListQName(buildListString(map.get("valid_target_types"),
                new Parameter<List<String>>(parameter.getContext()).addContext("valid_target_types")
            )))
            .setInterfaces(buildMap(map.get("interfaces"),
                new Parameter<TInterfaceDefinition>(parameter.getContext()).addContext("interfaces")
                    .setValue("TRelationshipType")
                    .setBuilderOO(this::buildInterfaceDefinition)
            ))
            .build();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public TInterfaceDefinition buildInterfaceDefinition(Object object, Parameter<TInterfaceDefinition> parameter) {
        if (Objects.isNull(object) || !validate(TInterfaceType.class, object, parameter)) return null;
        Map<String, Object> map = (Map<String, Object>) object;
        TInterfaceDefinition.Builder<?> output = new TInterfaceDefinition.Builder<>()
            .setType(buildQName(stringValue(map.get("type"))))
            .setInputs(buildParameterDefinitions(map.get("inputs"),
                new Parameter<>(parameter.getContext()).addContext("inputs")
                    .setValue(parameter.getValue())
            ));
        Map<String, TOperationDefinition> operations = buildMap(map.get("operations"),
            new Parameter<TOperationDefinition>(parameter.getContext())
                .setValue(parameter.getValue())
                .addContext("(operation)")
                .setBuilderOO(this::buildOperationDefinition)
                .setFilter(this::filterInterfaceAssignmentOperation)
        );
        output.setOperations(operations);
        return output.build();
    }

    @Nullable
    public TNodeType buildNodeType(Object object, Parameter<TNodeType> parameter) {
        if (Objects.isNull(object) || !validate(TNodeType.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, new Parameter<TNodeType.Builder>(parameter.getContext())
            .setBuilder(new TNodeType.Builder())
            .setClazz(TNodeType.class))
            .setRequirements(buildList(map, "requirements", this::buildMapRequirementDefinition, parameter))
            .setCapabilities(buildMap(map, "capabilities", this::buildCapabilityDefinition, parameter))
            .setInterfaces(buildMap(map.get("interfaces"),
                new Parameter<TInterfaceDefinition>(parameter.getContext()).addContext("interfaces")
                    .setValue("TNodeType")
                    .setBuilderOO(this::buildInterfaceDefinition)
            ))
            .setArtifacts(buildMap(map, "artifacts", this::buildArtifactDefinition, parameter))
            .build();
    }

    @Nullable
    public TMapRequirementDefinition buildMapRequirementDefinition(Object object, Parameter<TMapRequirementDefinition> parameter) {
        TMapRequirementDefinition result = new TMapRequirementDefinition();
        put(result, parameter.getValue(), buildRequirementDefinition(object, new Parameter<>(parameter.getContext())));
        return result;
    }

    @Nullable
    public TRequirementDefinition buildRequirementDefinition(Object object, Parameter<TRequirementDefinition> parameter) {
        if (Objects.isNull(object)) return new TRequirementDefinition();
        if (object instanceof String)
            return new TRequirementDefinition.Builder(buildQName(stringValue(object))).build();
        if (!validate(TRequirementDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRequirementDefinition.Builder(buildQName(stringValue(map.get("capability"))))
            .setNode(buildQName(stringValue(map.get("node"))))
            .setRelationship(buildRelationshipDefinition(map.get("relationship"),
                new Parameter<TRelationshipDefinition>(parameter.getContext()).addContext("relationship")
            ))
            .setOccurrences(buildListString(map.get("occurrences"),
                new Parameter<List<String>>(parameter.getContext()).addContext("occurrences")
            ))
            .build();
    }

    @Nullable
    public TRelationshipDefinition buildRelationshipDefinition(Object object, Parameter<TRelationshipDefinition> parameter) {
        if (Objects.isNull(object)) return new TRelationshipDefinition();
        if (object instanceof String)
            return new TRelationshipDefinition.Builder(buildQName(stringValue(object))).build();
        if (!validate(TRelationshipDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRelationshipDefinition.Builder(buildQName(stringValue(map.get("type"))))
            .setInterfaces(buildMap(map.get("interfaces"),
                new Parameter<TInterfaceDefinition>(parameter.getContext()).addContext("interfaces")
                    .setValue("TRelationshipDefinition")
                    .setBuilderOO(this::buildInterfaceDefinition)
            ))
            .build();
    }

    @Nullable
    public TCapabilityDefinition buildCapabilityDefinition(Object object, Parameter<TCapabilityDefinition> parameter) {
        if (Objects.isNull(object)) return new TCapabilityDefinition();
        if (object instanceof String)
            return new TCapabilityDefinition.Builder(buildQName(stringValue(object))).build();
        if (!validate(TCapabilityDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TCapabilityDefinition.Builder(buildQName(stringValue(map.get("type"))))
            .setDescription(buildDescription(map.get("description")))
            .setOccurrences(buildListString(map.get("occurrences"),
                new Parameter<List<String>>(parameter.getContext()).addContext("occurrences")
            ))
            .setValidSourceTypes(buildListQName(buildListString(map.get("valid_source_types"),
                new Parameter<List<String>>(parameter.getContext()).addContext("valid_source_types")
            )))
            .setProperties(buildMap(map.get("properties"),
                new Parameter<TPropertyDefinition>(parameter.getContext()).addContext("properties")
                    .setClazz(TPropertyDefinition.class)
                    .setBuilderOO(this::buildPropertyDefinition)
            ))
            .setAttributes(buildMap(map, "attributes", this::buildAttributeDefinition, parameter))
            .build();
    }

    @Nullable
    public TArtifactDefinition buildArtifactDefinition(Object object, Parameter<TArtifactDefinition> parameter) {
        if (Objects.isNull(object)) return new TArtifactDefinition();
        if (object instanceof String) {
            String file = stringValue(object);
            if (Objects.isNull(file)) return null;
            // TODO infer artifact type and mime type from file URI
            String type = file.substring(file.lastIndexOf("."), file.length());
            return new TArtifactDefinition.Builder(buildQName(type), file).build();
        }
        if (!validate(TArtifactDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;

        String file;
        if (map.get("file") instanceof String) {
            file = stringValue(map.get("file"));
        } else {
            file = null;
            assert false;
        }
        return new TArtifactDefinition.Builder(buildQName(stringValue(map.get("type"))), file)
            .setRepository(stringValue(map.get("repository")))
            .setDescription(buildDescription(map.get("description")))
            .setDeployPath(stringValue(map.get("deploy_path")))
            .setProperties(buildMap(map.get("properties"),
                new Parameter<TPropertyAssignment>().addContext("properties")
                    .setBuilderOO(this::buildPropertyAssignment)
            ))
            .build();
    }

    @Nullable
    public TGroupType buildGroupType(Object object, Parameter<TGroupType> parameter) {
        if (Objects.isNull(object) || !validate(TGroupType.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, new Parameter<TGroupType.Builder>(parameter.getContext())
            .setBuilder(new TGroupType.Builder())
            .setClazz(TGroupType.class))
            .setMembers(buildListQName(buildListString(map.get("members"),
                new Parameter<List<String>>(parameter.getContext()).addContext("members")
            )))
            .setRequirements(buildList(map, "requirements", this::buildMapRequirementDefinition, parameter))
            .setCapabilities(buildMap(map, "capabilities", this::buildCapabilityDefinition, parameter))
            .setInterfaces(buildMap(map.get("interfaces"),
                new Parameter<TInterfaceDefinition>(parameter.getContext())
                    .setValue("TGroupType")
                    .setBuilderOO(this::buildInterfaceDefinition)
            ))
            .build();
    }

    @Nullable
    public TPolicyType buildPolicyType(Object object, Parameter<TPolicyType> parameter) {
        if (Objects.isNull(object) || !validate(TPolicyType.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return buildEntityType(object, new Parameter<TPolicyType.Builder>(parameter.getContext())
            .setBuilder(new TPolicyType.Builder())
            .setClazz(TPolicyType.class))
            .setTargets(buildListQName(buildListString(map.get("targets"),
                new Parameter<List<String>>(parameter.getContext()).addContext("targets")
            )))
            .setTriggers(map.get("triggers"))
            .build();
    }

    @Nullable
    public TParameterDefinition buildParameterDefinition(Object object, Parameter<TParameterDefinition> parameter) {
        if (Objects.isNull(object) || !validate(TParameterDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        String type = stringValue(map.get("type"));
        if (type == null) type = "string";
        return new TParameterDefinition.Builder()
            .setType(buildQName(type))
            .setDescription(buildDescription(map.get("description")))
            .setRequired(buildRequired(map.get("required")))
            .setDefault(map.get("default"))
            .setStatus(buildStatus(map.get("status")))
            .setConstraints(buildList(map, "constraints", this::buildConstraintClause, parameter))
            .setEntrySchema(buildEntrySchema(map.get("entry_schema"),
                new Parameter<TEntrySchema>(parameter.getContext()).addContext("entry_schema")
            ))
            .setValue(map.get("value"))
            .build();
    }

    @Nullable
    public TParameterDefinition buildParameterAssignment(Object object, Parameter<TParameterDefinition> parameter) {
        if (Objects.isNull(object)) return null;
        return new TParameterDefinition.Builder()
            .setValue(object)
            .build();
    }

    @Nullable
    public TNodeTemplate buildNodeTemplate(Object object, Parameter<TNodeTemplate> parameter) {
        if (Objects.isNull(object)) return new TNodeTemplate();
        if (!validate(TNodeTemplate.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TNodeTemplate.Builder(buildQName(stringValue(map.get("type"))))
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
                new Parameter<TInterfaceAssignment>(parameter.getContext()).addContext("interfaces")
                    .setValue("TNodeTemplate")
                    .setBuilderOO(this::buildInterfaceAssignment)
            ))
            .setArtifacts(buildMap(map, "artifacts", this::buildArtifactDefinition, parameter))
            .setNodeFilter(buildNodeFilterDefinition(map.get("node_filter"),
                new Parameter<TNodeFilterDefinition>(parameter.getContext()).addContext("node_filter")
            ))
            .setCopy(buildQName(stringValue(map.get("copy"))))
            .build();
    }

    @Nullable
    public TAttributeAssignment buildAttributeAssignment(Object object, Parameter<TAttributeAssignment> parameter) {
        if (Objects.isNull(object)) return null;
        if (!(object instanceof Map)) {
            // Attribute assignment with simple value
            return new TAttributeAssignment.Builder().setValue(object).build();
        } else if (!((Map) object).containsKey("value")) {
            // Attribute assignment with <attribute_value_expression>
            return new TAttributeAssignment.Builder().setValue(object).build();
        } else if (((Map) object).containsKey("value") && validate(TAttributeAssignment.class, object, parameter)) {
            // Attribute assignment with extended notation
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) object;
            return new TAttributeAssignment.Builder()
                .setDescription(buildDescription(map.get("description")))
                .setValue(map.get("value"))
                .build();
        } else {
            return null;
        }
    }

    @Nullable
    public TMapRequirementAssignment buildMapRequirementAssignment(Object object, Parameter<TMapRequirementAssignment> parameter) {
        if (Objects.isNull(object)) return null;
        TMapRequirementAssignment result = new TMapRequirementAssignment();
        put(result, stringValue(parameter.getValue()), buildRequirementAssignment(object,
            new Parameter<>(parameter.getContext())
        ));
        return result;
    }

    @Nullable
    public TRequirementAssignment buildRequirementAssignment(Object object, Parameter<TRequirementAssignment> parameter) {
        if (Objects.isNull(object)) return null;
        if (object instanceof String) return new TRequirementAssignment(buildQName(stringValue(object)));
        if (!validate(TRequirementAssignment.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRequirementAssignment.Builder()
            .setCapability(buildQName(stringValue(map.get("capability"))))
            .setNode(buildQName(stringValue(map.get("node"))))
            .setRelationship(buildRelationshipAssignment(map.get("relationship"),
                new Parameter<TRelationshipAssignment>(parameter.getContext()).addContext("relationship")
            ))
            .setNodeFilter(buildNodeFilterDefinition(map.get("node_filter"),
                new Parameter<TNodeFilterDefinition>(parameter.getContext()).addContext("node_filter")
            ))
            .setOccurrences(buildListString(map.get("occurrences"),
                new Parameter<List<String>>(parameter.getContext()).addContext("occurrences")
            ))
            .build();
    }

    @Nullable
    public TRelationshipAssignment buildRelationshipAssignment(Object object, Parameter<TRelationshipAssignment> parameter) {
        if (Objects.isNull(object)) return null;
        if (object instanceof String)
            return new TRelationshipAssignment.Builder(buildQName(stringValue(object))).build();
        if (!validate(TRelationshipAssignment.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRelationshipAssignment.Builder(buildQName(stringValue(map.get("type"))))
            .setProperties(buildMap(map, "properties", this::buildPropertyAssignment, parameter))
            .setInterfaces(buildMap(map, "interfaces", this::buildInterfaceAssignment, parameter))
            .build();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public TInterfaceAssignment buildInterfaceAssignment(Object object, Parameter<TInterfaceAssignment> parameter) {
        if (Objects.isNull(object) || !validate(TInterfaceAssignment.class, object, parameter)) return null;
        Map<String, Object> map = (Map<String, Object>) object;
        return new TInterfaceAssignment.Builder()
            .setType(buildQName(stringValue(map.get("type"))))
            .setInputs(buildParameterDefinitions(map.get("inputs"),
                new Parameter<>(parameter.getContext())
                    .setValue("TInterfaceAssignment")
            ))
            .setOperations(buildMap(map.get("operations"),
                new Parameter<TOperationDefinition>(parameter.getContext()).addContext("(operations)")
                    .setBuilderOO(this::buildOperationDefinition)
                    // .setFilter(this::filterInterfaceAssignmentOperation)
                    .setValue("TInterfaceAssignment")
            ))
            .build();
    }

    private Boolean filterInterfaceAssignmentOperation(Map.Entry<String, Object> entry) {
        if (Objects.isNull(entry.getKey())) return false;
        Set<String> keys = Stream.of("type", "inputs").collect(Collectors.toSet());
        return !keys.contains(entry.getKey());
    }

    @Nullable
    public TNodeFilterDefinition buildNodeFilterDefinition(Object object, Parameter<TNodeFilterDefinition> parameter) {
        if (Objects.isNull(object) || !validate(TNodeFilterDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TNodeFilterDefinition.Builder()
            .setProperties(buildList(map, "properties", this::buildMapPropertyDefinition, parameter))
            .setCapabilities(buildList(map, "capabilities", this::buildMapObjectValue, parameter))
            .build();
    }

    @Nullable
    public TMapPropertyFilterDefinition buildMapPropertyDefinition(Object object, Parameter<TMapPropertyFilterDefinition> parameter) {
        if (Objects.isNull(object)) return null;
        TMapPropertyFilterDefinition result = new TMapPropertyFilterDefinition();
        put(result, stringValue(parameter.getValue()), buildPropertyFilterDefinition(object,
            new Parameter<>(parameter.getContext())));
        return result;
    }

    @Nullable
    public TPropertyFilterDefinition buildPropertyFilterDefinition(Object object, Parameter<TPropertyFilterDefinition> parameter) {
        if (Objects.isNull(object) || !validate(TPropertyFilterDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TPropertyFilterDefinition.Builder()
            .setConstraints(buildList(map, "constraints", this::buildConstraintClause, parameter))
            .build();
    }

    @Nullable
    public TMapObject buildMapObjectValue(Object object, Parameter<TMapObject> parameter) {
        if (Objects.isNull(object)) return null;
        TMapObject result = new TMapObject();
        put(result, stringValue(parameter.getValue()), object);
        return result;
    }

    @Nullable
    public TCapabilityAssignment buildCapabilityAssignment(Object object, Parameter<TCapabilityAssignment> parameter) {
        if (Objects.isNull(object) || !validate(TCapabilityAssignment.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TCapabilityAssignment.Builder()
            .setProperties(buildMap(map, "properties", this::buildPropertyAssignment, parameter))
            .setAttributes(buildMap(map, "attributes", this::buildAttributeAssignment, parameter))
            .build();
    }

    @Nullable
    public TRelationshipTemplate buildRelationshipTemplate(Object object, Parameter<TRelationshipTemplate> parameter) {
        if (Objects.isNull(object)) return new TRelationshipTemplate();
        if (!validate(TRelationshipTemplate.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TRelationshipTemplate.Builder(buildQName(stringValue(map.get("type"))))
            .setDescription(buildDescription(map.get("description")))
            .setMetadata(buildMetadata(map.get("metadata")))
            .setProperties(buildMap(map, "properties", this::buildPropertyAssignment, parameter))
            .setAttributes(buildMap(map, "attributes", this::buildAttributeAssignment, parameter))
            .setInterfaces(buildMap(map.get("interfaces"),
                new Parameter<TInterfaceDefinition>(parameter.getContext()).addContext("interfaces")
                    .setValue("TRelationshipTemplate")
                    .setBuilderOO(this::buildInterfaceDefinition)
            ))
            .setCopy(buildQName(stringValue(map.get("copy"))))
            .build();
    }

    @Nullable
    public TGroupDefinition buildGroupDefinition(Object object, Parameter<TGroupDefinition> parameter) {
        if (Objects.isNull(object)) return new TGroupDefinition();
        if (!validate(TGroupDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TGroupDefinition.Builder(buildQName(stringValue(map.get("type"))))
            .setDescription(buildDescription(map.get("description")))
            .setMetadata(buildMetadata(map.get("metadata")))
            .setProperties(buildMap(map, "properties", this::buildPropertyAssignment, parameter))
            .setMembers(buildListQName(buildListString(map.get("members"),
                new Parameter<List<String>>(parameter.getContext()).addContext("members")
            )))
            .setInterfaces(buildMap(map, "interfaces", this::buildInterfaceDefinition, parameter.setValue("TGroupDefinition")
            ))
            .build();
    }

    @Nullable
    public TMapPolicyDefinition buildMapPolicyDefinition(Object object, Parameter<TMapPolicyDefinition> parameter) {
        TMapPolicyDefinition result = new TMapPolicyDefinition();
        put(result, parameter.getValue(), buildPolicyDefinition(object, new Parameter<>(parameter.getContext())));
        return result;
    }

    @Nullable
    public TPolicyDefinition buildPolicyDefinition(Object object, Parameter<TPolicyDefinition> parameter) {
        if (Objects.isNull(object)) return new TPolicyDefinition();
        if (!validate(TPolicyDefinition.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TPolicyDefinition.Builder(buildQName(stringValue(map.get("type"))))
            .setDescription(buildDescription(map.get("description")))
            .setMetadata(buildMetadata(map.get("metadata")))
            .setProperties(buildMap(map, "properties", this::buildPropertyAssignment, parameter))
            .setTargets(buildListQName(buildListString(map.get("targets"),
                new Parameter<List<String>>(parameter.getContext()).addContext("targets")
            )))
            .build();
    }

    @Nullable
    public TSubstitutionMappings buildSubstitutionMappings(Object object, Parameter<Object> parameter) {
        if (Objects.isNull(object) || !validate(TSubstitutionMappings.class, object, parameter)) return null;
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) object;
        return new TSubstitutionMappings.Builder()
            .setNodeType(buildQName(stringValue(map.get("node_type"))))
            .setCapabilities(buildMap(map, "capabilities", this::buildStringList, parameter))
            .setRequirements(buildMap(map, "requirements", this::buildStringList, parameter))
            .build();
    }

    @Nullable
    public TListString buildStringList(Object object, Parameter<TListString> parameter) {
        if (Objects.isNull(object)) return null;
        @SuppressWarnings("unchecked")
        List<String> tmp = (List<String>) object;
        TListString stringList = new TListString();
        stringList.addAll(tmp);
        return stringList;
    }

    @Nullable
    private String stringValue(@Nullable Object object) {
        if (Objects.isNull(object)) return null;
        return String.valueOf(object);
    }

    private String stringValue(@Nullable Object object, String defaultValue) {
        if (Objects.isNull(object)) return defaultValue;
        return String.valueOf(object);
    }

    private <T> void put(Map<String, T> map, String key, T value) {
        if (Objects.nonNull(map) && Objects.nonNull(key) && Objects.nonNull(value)) map.put(key, value);
    }

    private <T> boolean nonNull(Map.Entry<String, T> entry) {
        return Objects.nonNull(entry) && Objects.nonNull(entry.getKey()) && Objects.nonNull(entry.getValue());
    }

    private <T> boolean nonNull(Pair<String, T> pair) {
        return Objects.nonNull(pair) && Objects.nonNull(pair.getOne()) && Objects.nonNull(pair.getTwo());
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
        if (Objects.isNull(object)) return null;
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
//            .filter(this::nonNull)
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
        if (Objects.isNull(object)) return null;
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
        private BiFunction<Object, Parameter<T>, T> builderOO;

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

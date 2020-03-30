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
package org.eclipse.winery.repository.yaml.converter.support;

import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.jdt.annotation.NonNull;

import org.eclipse.winery.model.converter.support.Namespaces;
import org.eclipse.winery.model.converter.support.exception.MultiException;
import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.tosca.yaml.*;
import org.eclipse.winery.model.tosca.yaml.support.TMapImportDefinition;
import org.eclipse.winery.repository.converter.reader.YamlReader;
import org.eclipse.winery.repository.converter.validator.support.ExceptionVisitor;
import org.eclipse.winery.repository.converter.validator.support.Result;
import org.eclipse.winery.repository.converter.writer.WriterUtils;

import org.w3c.dom.Document;

public class SchemaVisitor extends ExceptionVisitor<Result, Parameter> {

    private Map<QName, Map<String, QName>> propertyDefinition;
    private Map<String, List<Map.Entry<String, TImportDefinition>>> imports;
    private Map<String, TDataType> data_types;
    private List<String> localDatatypeNames;
    private Map<String, SchemaBuilder> schemaBuilders;
    private Map<String, List<String>> schemaTypes;

    public SchemaVisitor() {
        this.reset();
    }

    public void reset() {
        this.propertyDefinition = new LinkedHashMap<>();
        this.imports = new LinkedHashMap<>();
        this.data_types = new LinkedHashMap<>();
        this.schemaBuilders = new LinkedHashMap<>();
        this.schemaTypes = new LinkedHashMap<>();

        this.localDatatypeNames = new ArrayList<>();
    }

    @NonNull
    public Map<QName, Map<String, QName>> getPropertyDefinition() {
        return propertyDefinition;
    }

    private void addImport(String namespace, Map.Entry<String, TImportDefinition> importDefinition) {
        if (imports.containsKey(namespace)) {
            imports.get(namespace).add(importDefinition);
        } else {
            imports.put(namespace, new ArrayList<>(Collections.singleton(importDefinition)));
        }
    }

    private void addSchemaTypes(String namespace, String typeName) {
        if (this.schemaTypes.containsKey(namespace)) {
            this.schemaTypes.get(namespace).add(typeName);
        } else {
            this.schemaTypes.put(namespace, new ArrayList<>(Collections.singleton(typeName)));
        }
    }

    public Map<QName, Map<String, QName>> visit(TServiceTemplate node, Path path, Path outpath, String namespace) {
        for (TMapImportDefinition map : node.getImports()) {
            for (Map.Entry<String, TImportDefinition> entry : map.entrySet()) {
                YamlReader reader = new YamlReader();
                try {
                    TServiceTemplate serviceTemplate = reader.parse(entry.getValue(), path, entry.getValue().getNamespaceUri());
                    visit(serviceTemplate, new Parameter(outpath, entry.getValue().getNamespaceUri()).setBuildSchema(false));
                } catch (MultiException e) {
                    setException(e);
                }
            }
        }
        this.visit(node, new Parameter(outpath, namespace));
        return propertyDefinition;
    }

    @Override
    public Result visit(TServiceTemplate node, Parameter parameter) {

        Map<String, TDataType> tmpDataTypes = this.data_types;
        this.data_types = node.getDataTypes();
        for (TMapImportDefinition map : node.getImports()) {
            for (Map.Entry<String, TImportDefinition> entry : map.entrySet()) {
                addImport(entry.getValue().getNamespaceUri(), new AbstractMap.SimpleEntry<>(entry));
            }
        }

        // init localDataTypesList
        node.getDataTypes().forEach((key, value) -> this.localDatatypeNames.add(key));

        // Default: parameter.datatype is not set -> visit all datatype nodes
        QName type = parameter.getDatatype();
        if (type == null) {
            for (Map.Entry<String, TDataType> entry : node.getDataTypes().entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().accept(this, parameter.copy().addContext("datatypes", entry.getKey()));
                }
            }

            visitChildes(node, parameter);
        }
        // Optimized: parameter.datatype is set and defined in this service template 
        else if (type.getNamespaceURI().equals(parameter.getNamespace())
            && this.localDatatypeNames.contains(type.getLocalPart())) {
            node.getDataTypes().get(parameter.getDatatype().getLocalPart())
                .accept(this, parameter.copy().addContext("datatypes", parameter.getDatatype().getLocalPart()));
        }

        if (parameter.getBuildSchema()) {
            for (Map.Entry<String, SchemaBuilder> entry : schemaBuilders.entrySet()) {
                Document document = entry.getValue().build();
                WriterUtils.saveType(document, parameter.getPath(), parameter.getNamespace(), entry.getValue().getNamespace());
            }
        }

        this.data_types = tmpDataTypes;
        return null;
    }

    public String getRelativeFileName(String namespace) {
        return EncodingUtil.URLencode(namespace) + ".xsd";
    }

    @Override
    public Result visit(TDataType node, Parameter parameter) {
        SchemaBuilder builder;

        if (this.schemaBuilders.containsKey(parameter.getNamespace())) {
            builder = this.schemaBuilders.get(parameter.getNamespace());
        } else {
            builder = new SchemaBuilder(parameter.getNamespace());
            this.schemaBuilders.put(parameter.getNamespace(), builder);
        }

        Map<String, QName> buildPlan = new LinkedHashMap<>();

        for (Map.Entry<String, TPropertyDefinition> entry : node.getProperties().entrySet()) {
            QName type = entry.getValue().getType();
            buildPlan.put(entry.getKey(), entry.getValue().getType());

            // Add default YAML types 
            if (type.getNamespaceURI().equals(Namespaces.YAML_NS)) {
                builder.addElements(entry.getKey(), entry.getValue());
            }
            // if parameter.datatype is not defined and property type is defined in this schema -> add
            else if (parameter.getDatatype() == null
                && type.getNamespaceURI().equals(parameter.getNamespace())
                && this.localDatatypeNames.contains(type.getLocalPart())) {
                builder.addElements(entry.getKey(), entry.getValue());
            }
            // if parameter.datatype is defined and property type is not in this service template
            else if (!type.getNamespaceURI().equals(parameter.getNamespace())
                || !this.localDatatypeNames.contains(type.getLocalPart())) {
                for (Map.Entry<String, TImportDefinition> importDefinition : imports.get(type.getNamespaceURI())) {
                    try {
                        YamlReader reader = new YamlReader();
                        TServiceTemplate serviceTemplate = reader.parse(importDefinition.getValue(), parameter.getPath(), importDefinition.getValue().getNamespaceUri());
                        visit(serviceTemplate,
                            new Parameter(parameter.getPath(), type.getNamespaceURI())
                                .setDatatype(type).setBuildSchema(false));

                        // TODO getAbsoluteFilePath
                        builder.addImports(importDefinition.getValue().getNamespaceUri(), getRelativeFileName(importDefinition.getValue().getNamespaceUri()));
                        builder.addElements(entry.getKey(), entry.getValue());
                    } catch (MultiException e) {
                        setException(e);
                    }
                }
            }
            // if parameter.datatype is defined and property type is in this service template but not read 
            else if (!(this.schemaTypes.containsKey(parameter.getNamespace())
                && this.schemaTypes.get(parameter.getNamespace()).contains(type.getLocalPart()))) {
                this.data_types.get(type.getLocalPart()).accept(this, parameter.copy().addContext(type.getLocalPart()).setDatatype(type));
                builder.addElements(entry.getKey(), entry.getValue());
            }
        }

        this.addSchemaTypes(parameter.getNamespace(), parameter.getKey());
        this.propertyDefinition.put(new QName(parameter.getNamespace(), parameter.getKey()), buildPlan);
        if (parameter.getBuildComplexType()) {
            builder.buildComplexType(parameter.getKey(), false);
        }
        return null;
    }

    @Override
    public Result visit(TEntityType node, Parameter parameter) {
        if (node instanceof TDataType || node.getProperties().isEmpty()) {
            return null;
        }
        // BuildPlan for assignments
        Map<String, QName> plan = new LinkedHashMap<>();
        String name = parameter.getKey() + "_Properties";

        SchemaBuilder builder = new SchemaBuilder(parameter.getNamespace());

        Map<String, String> imports = new LinkedHashMap<>();

        for (Map.Entry<String, TPropertyDefinition> entry : node.getProperties().entrySet()) {
            builder.addElements(entry.getKey(), entry.getValue());
            QName type = entry.getValue().getType();
            if (type.getNamespaceURI() != null && !type.getNamespaceURI().equals(Namespaces.YAML_NS)) {
                imports.put(type.getNamespaceURI(), type.getLocalPart());
            }
            plan.put(entry.getKey(), entry.getValue().getType());
        }
        imports.forEach((key, value) -> builder.addImports(key, getRelativeFileName(key)));
        Document document = builder.buildComplexType(name, true).build();

        WriterUtils.saveType(document, parameter.getPath(), parameter.getNamespace(), name);
        // FIXME this needs access to a repository
        WriterUtils.storeTypes(null, parameter.getPath(), name, parameter.getNamespace(), name);

        this.propertyDefinition.put(new QName(parameter.getNamespace(), name), plan);
        return null;
    }

    /**
     * Visit all childes of ServiceTemplates except from metadata, repositories, imports, data_types
     */
    private void visitChildes(TServiceTemplate node, Parameter parameter) {
        node.getArtifactTypes().entrySet().stream()
            .filter(entry -> Objects.nonNull(entry) && Objects.nonNull(entry.getValue()))
            .forEach(entry ->
                entry.getValue().accept(this, parameter.copy().addContext("artifact_types", entry.getKey()))
            );
        node.getCapabilityTypes().entrySet().stream()
            .filter(entry -> Objects.nonNull(entry) && Objects.nonNull(entry.getValue()))
            .forEach(entry ->
                entry.getValue().accept(this, parameter.copy().addContext("capability_types", entry.getKey()))
            );
        node.getInterfaceTypes().entrySet().stream()
            .filter(entry -> Objects.nonNull(entry) && Objects.nonNull(entry.getValue()))
            .forEach(entry ->
                entry.getValue().accept(this, parameter.copy().addContext("interface_types", entry.getKey()))
            );
        node.getRelationshipTypes().entrySet().stream()
            .filter(entry -> Objects.nonNull(entry) && Objects.nonNull(entry.getValue()))
            .forEach(entry ->
                entry.getValue().accept(this, parameter.copy().addContext("relationship_types", entry.getKey()))
            );
        node.getNodeTypes().entrySet().stream()
            .filter(entry -> Objects.nonNull(entry) && Objects.nonNull(entry.getValue()))
            .forEach(entry ->
                entry.getValue().accept(this, parameter.copy().addContext("node_types", entry.getKey()))
            );
        node.getGroupTypes().entrySet().stream()
            .filter(entry -> Objects.nonNull(entry) && Objects.nonNull(entry.getValue()))
            .forEach(entry ->
                entry.getValue().accept(this, parameter.copy().addContext("group_types", entry.getKey()))
            );
        node.getPolicyTypes().entrySet().stream()
            .filter(entry -> Objects.nonNull(entry) && Objects.nonNull(entry.getValue()))
            .forEach(entry ->
                entry.getValue().accept(this, parameter.copy().addContext("policy_types", entry.getKey()))
            );
        if (Objects.nonNull(node.getTopologyTemplate())) {
            node.getTopologyTemplate().accept(this, parameter.copy().addContext("topology_template"));
        }
    }
}

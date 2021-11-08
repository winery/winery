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
package org.eclipse.winery.repository.yaml.converter.support.visitors;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.YTArtifactType;
import org.eclipse.winery.model.tosca.yaml.YTEntityType;
import org.eclipse.winery.model.tosca.yaml.YTImportDefinition;
import org.eclipse.winery.model.tosca.yaml.YTServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.support.YTMapImportDefinition;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.converter.support.Namespaces;
import org.eclipse.winery.model.converter.support.exception.MultiException;
import org.eclipse.winery.repository.converter.reader.YamlReader;
import org.eclipse.winery.repository.converter.validator.support.ExceptionVisitor;

public class ReferenceVisitor extends ExceptionVisitor<ReferenceVisitor.Result, ReferenceVisitor.Parameter> {

    private final YTServiceTemplate serviceTemplate;
    private final String namespace;
    private final YamlReader reader;
    private final Path path;

    private Map<YTImportDefinition, ReferenceVisitor> visitors;
    private Map<YTImportDefinition, YTServiceTemplate> serviceTemplates;

    public ReferenceVisitor(YTServiceTemplate serviceTemplate, String namespace, Path path) {
        this.serviceTemplate = serviceTemplate;
        this.namespace = namespace;
        this.reader = new YamlReader();
        this.path = path;
        this.visitors = new LinkedHashMap<>();
        this.serviceTemplates = new LinkedHashMap<>();
    }

    public Result getTypes(QName reference, String entityClassName) {
        return visit(serviceTemplate, new Parameter(reference, entityClassName));
    }

    @Override
    public Result visit(YTImportDefinition node, Parameter parameter) {
        if (node.getNamespaceUri() == null && !parameter.reference.getNamespaceURI().equals(Namespaces.DEFAULT_YAML_NS)) {
            return null;
        }

        String namespace = node.getNamespaceUri() == null ? Namespaces.DEFAULT_YAML_NS : node.getNamespaceUri();
        if (!this.visitors.containsKey(node)) {
            try {
                this.serviceTemplates.put(node, reader.readImportDefinition(node, path, namespace));
            } catch (MultiException e) {
                setException(e);
            }
            this.visitors.put(node, new ReferenceVisitor(this.serviceTemplates.get(node), namespace, path));
        }

        return this.visitors.get(node).visit(this.serviceTemplates.get(node), parameter);
    }

    @Override
    public Result visit(YTEntityType node, Parameter parameter) {
        if (node.getDerivedFrom() != null) {
            return serviceTemplate.accept(this, new Parameter(node.getDerivedFrom(), parameter.entityClass)).copy(node, node.getDerivedFrom());
        }
        return new Result(node);
    }

    @Override
    public Result visit(YTServiceTemplate node, Parameter parameter) {
        Result result;
        if (parameter.reference.getNamespaceURI().equals(this.namespace)
            && "TArtifactType".equals(parameter.entityClass)
            && node.getArtifactTypes().containsKey(parameter.reference.getLocalPart())) {
            return node.getArtifactTypes().get(parameter.reference.getLocalPart()).accept(this, parameter.copy());
        }

        for (YTMapImportDefinition map : node.getImports()) {
            for (Map.Entry<String, YTImportDefinition> entry : map.entrySet()) {
                result = entry.getValue().accept(this, parameter);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    @Override
    public Result visit(YTArtifactType node, Parameter parameter) {
        return null;
    }

    public static class Result extends AbstractResult<Result> {
        private List<Object> objects;
        private List<QName> names;

        public Result(Object object) {
            this.objects = new ArrayList<>();
            this.names = new ArrayList<>();
            if (object != null) {
                this.objects.add(object);
            }
        }

        public List<Object> getObjects() {
            return objects;
        }

        public List<QName> getNames() {
            return names;
        }

        public Result copy(Object object, QName name) {
            this.objects.add(object);
            this.names.add(name);
            return this;
        }

        @Override
        public Result add(Result result) {
            // No collecting (deep search)
            return this;
        }
    }

    public static class Parameter extends AbstractParameter<Parameter> {
        private final QName reference;
        private final String entityClass;

        public Parameter(QName reference, String entityName) {
            this.reference = reference;
            this.entityClass = entityName;
        }

        @Override
        public Parameter copy() {
            Parameter parameter = new Parameter(this.reference, this.entityClass);
            parameter.getContext().addAll(this.getContext());
            return parameter;
        }

        @Override
        public Parameter self() {
            return this;
        }
    }
}

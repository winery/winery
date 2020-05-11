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
package org.eclipse.winery.repository.converter.support.validator;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.TArtifactType;
import org.eclipse.winery.model.tosca.yaml.TAttributeDefinition;
import org.eclipse.winery.model.tosca.yaml.TCapabilityType;
import org.eclipse.winery.model.tosca.yaml.TDataType;
import org.eclipse.winery.model.tosca.yaml.TEntityType;
import org.eclipse.winery.model.tosca.yaml.TEntrySchema;
import org.eclipse.winery.model.tosca.yaml.TGroupDefinition;
import org.eclipse.winery.model.tosca.yaml.TGroupType;
import org.eclipse.winery.model.tosca.yaml.TInterfaceType;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TNodeType;
import org.eclipse.winery.model.tosca.yaml.TPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyType;
import org.eclipse.winery.model.tosca.yaml.TPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.yaml.TRelationshipType;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.converter.support.Defaults;
import org.eclipse.winery.repository.converter.support.Namespaces;
import org.eclipse.winery.repository.converter.support.exception.InvalidDefinition;
import org.eclipse.winery.repository.converter.support.exception.InvalidTypeExtend;
import org.eclipse.winery.repository.converter.support.exception.MultiException;
import org.eclipse.winery.repository.converter.support.exception.UndefinedType;
import org.eclipse.winery.repository.converter.support.validator.support.ExceptionVisitor;
import org.eclipse.winery.repository.converter.support.validator.support.Parameter;
import org.eclipse.winery.repository.converter.support.validator.support.Result;

public class TypeValidator extends ExceptionVisitor<Result, Parameter> {
    private TypeVisitor typeVisitor;

    public TypeValidator(Path path, String namespace) {
        this.typeVisitor = new TypeVisitor(namespace, path);
        this.typeVisitor.addDataTypes(Defaults.YAML_TYPES, Namespaces.YAML_NS);
        this.typeVisitor.addDataTypes(Defaults.TOSCA_TYPES, Namespaces.TOSCA_NS);
    }

    public void validate(TServiceTemplate serviceTemplate) throws MultiException {
        serviceTemplate.accept(typeVisitor, new Parameter());
        if (typeVisitor.hasExceptions()) {
            this.setException(this.typeVisitor.getException());
        }

        serviceTemplate.accept(this, new Parameter());
        if (this.hasExceptions()) {
            throw this.getException();
        }
    }

    /**
     * Validates that a map of lists contains a list mapped to the namespace uri of a QName and the list contains the
     * local name of the QName
     */
    private void validateTypeIsDefined(QName type, Map<String, List<String>> map, Parameter parameter) {
        if (!(map.containsKey(type.getNamespaceURI()) && map.get(type.getNamespaceURI()).contains(type.getLocalPart()))) {
            setException(new UndefinedType(
                "Type '{}' is undefined\nKnown types: {}",
                type,
                map
            ).setContext(parameter.getContext()));
        }
    }

    private void setInvalidDefinition(Parameter parameter) {
        setException(new InvalidDefinition(
            "Type '{}:type' is required",
            parameter.getKey()
        ).setContext(parameter.getContext()));
    }

    private void validateEntityType(TEntityType node, Parameter parameter, Map<String, List<String>> types) {
        if (Objects.nonNull(node.getDerivedFrom())) {
            validateTypeIsDefined(node.getDerivedFrom(), types, parameter.copy().addContext("derived_from"));
        }
    }

    private void validatePropertyOrAttributeDefinition(QName type, TEntrySchema entrySchema, Parameter parameter) {
        if (Objects.isNull(type)) {
            setInvalidDefinition(parameter);
        } else {
            validateTypeIsDefined(type, typeVisitor.getDataTypes(), parameter.copy().addContext("type"));

            if (type.getLocalPart().equals("list") || type.getLocalPart().equals("map")) {
                if (Objects.isNull(entrySchema)) {
                    setException(new InvalidDefinition(
                        "EntrySchema '{}:entry_schema' is required for '{}:type: {}'",
                        parameter.getKey(),
                        parameter.getKey(),
                        type.getLocalPart()
                    ).setContext(parameter.getContext()));
                } else if (Objects.isNull(entrySchema.getType())) {
                    setInvalidDefinition(parameter.copy().addContext("entry_schema"));
                } else {
                    validateTypeIsDefined(
                        entrySchema.getType(),
                        typeVisitor.getDataTypes(),
                        parameter.copy().addContext("entry_schema").addContext("type")
                    );
                }
            }
        }
    }

    @Override
    public Result visit(TArtifactType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getArtifactTypes());
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TCapabilityType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getCapabilityTypes());

        for (QName source : node.getValidSourceTypes()) {
            validateTypeIsDefined(source, typeVisitor.getNodeTypes(), parameter.copy().addContext("valid_source_types"));
        }

        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TDataType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getDataTypes());

        // Extend a native DataType should fail
        if (Objects.nonNull(node.getDerivedFrom()) &&
            (Defaults.YAML_TYPES.contains(node.getDerivedFrom().getLocalPart()) ||
                Defaults.TOSCA_TYPES.contains(node.getDerivedFrom().getLocalPart())
            ) &&
            !node.getProperties().isEmpty()) {
            setException(new InvalidTypeExtend(
                    "The native data type '{}' cannot be extended with properties!",
                    parameter.getKey()
                ).setContext(parameter.getContext())
            );
        }

        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TGroupType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getGroupTypes());
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TInterfaceType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getInterfaceTypes());
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TRelationshipType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getRelationshipTypes());
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TNodeType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getNodeTypes());
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TPolicyType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getPolicyTypes());
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TNodeTemplate node, Parameter parameter) {
        if (Objects.isNull(node.getType())) {
            setInvalidDefinition(parameter);
        } else {
            validateTypeIsDefined(node.getType(), typeVisitor.getNodeTypes(), parameter.copy().addContext("type"));
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TRelationshipTemplate node, Parameter parameter) {
        if (Objects.isNull(node.getType())) {
            setInvalidDefinition(parameter);
        } else {
            validateTypeIsDefined(node.getType(), typeVisitor.getRelationshipTypes(), parameter.copy().addContext("type"));
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TGroupDefinition node, Parameter parameter) {
        if (Objects.isNull(node.getType())) {
            setInvalidDefinition(parameter);
        } else {
            validateTypeIsDefined(node.getType(), typeVisitor.getGroupTypes(), parameter.copy().addContext("type"));
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TPolicyDefinition node, Parameter parameter) {
        if (Objects.isNull(node.getType())) {
            setInvalidDefinition(parameter);
        } else {
            validateTypeIsDefined(node.getType(), typeVisitor.getPolicyTypes(), parameter.copy().addContext("type"));
        }
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TPropertyDefinition node, Parameter parameter) {
        validatePropertyOrAttributeDefinition(node.getType(), node.getEntrySchema(), parameter);
        return super.visit(node, parameter);
    }

    @Override
    public Result visit(TAttributeDefinition node, Parameter parameter) {
        validatePropertyOrAttributeDefinition(node.getType(), node.getEntrySchema(), parameter);
        return super.visit(node, parameter);
    }
}

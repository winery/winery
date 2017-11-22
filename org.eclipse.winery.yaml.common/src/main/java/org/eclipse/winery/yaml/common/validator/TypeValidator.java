/********************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.common.validator;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.TArtifactType;
import org.eclipse.winery.model.tosca.yaml.TAttributeDefinition;
import org.eclipse.winery.model.tosca.yaml.TCapabilityType;
import org.eclipse.winery.model.tosca.yaml.TDataType;
import org.eclipse.winery.model.tosca.yaml.TEntityType;
import org.eclipse.winery.model.tosca.yaml.TEntrySchema;
import org.eclipse.winery.model.tosca.yaml.TGroupType;
import org.eclipse.winery.model.tosca.yaml.TInterfaceType;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TNodeType;
import org.eclipse.winery.model.tosca.yaml.TPolicyType;
import org.eclipse.winery.model.tosca.yaml.TPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipType;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.Defaults;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.exception.InvalidNativeTypeExtend;
import org.eclipse.winery.yaml.common.exception.InvalidParentType;
import org.eclipse.winery.yaml.common.exception.MissingNodeType;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.exception.UnknownCapabilitySourceType;
import org.eclipse.winery.yaml.common.exception.UnknownDataType;
import org.eclipse.winery.yaml.common.validator.support.ExceptionVisitor;
import org.eclipse.winery.yaml.common.validator.support.Parameter;
import org.eclipse.winery.yaml.common.validator.support.Result;

public class TypeValidator extends ExceptionVisitor<Result, Parameter> {
    private TypeVisitor typeVisitor;

    public TypeValidator(Path path, String namespace) {
        this.typeVisitor = new TypeVisitor(namespace, path);
        this.typeVisitor.addDataTypes(Defaults.YAML_TYPES, Namespaces.YAML_NS);
        this.typeVisitor.addDataTypes(Defaults.TOSCA_TYPES, Namespaces.TOSCA_NS);
    }

    public void validate(TServiceTemplate serviceTemplate) throws MultiException {
        this.typeVisitor.visit(serviceTemplate, new Parameter());
        if (typeVisitor.hasExceptions()) {
            this.setException(this.typeVisitor.getException());
        }

        this.visit(serviceTemplate, new Parameter());
        if (this.hasExceptions()) {
            throw this.getException();
        }
    }

    /**
     * Validates that a map of lists contains a list mapped to the namespace uri of a QName and the list contains the
     * local name of the QName
     */
    private Boolean validateTypeIsDefined(QName type, Map<String, List<String>> map) {
        return map.containsKey(type.getNamespaceURI()) && map.get(type.getNamespaceURI()).contains(type.getLocalPart());
    }

    private void validateEntityType(TEntityType node, Parameter parameter, Map<String, List<String>> types) {
        if (Objects.nonNull(node.getDerivedFrom()) && !validateTypeIsDefined(node.getDerivedFrom(), types)) {
            String msg = "The parent type \""
                .concat(node.getDerivedFrom().toString())
                .concat("\" is undefined!")
                .concat(getKnownTypes(types))
                .concat("\n").concat(print(parameter.getContext()));
            setException(new InvalidParentType(msg));
        }
    }

    private String getKnownTypes(Map<String, List<String>> types) {
        return "\nKnown types are:\n\t".concat(types.entrySet().stream()
            .flatMap(list -> list.getValue().stream().map(entry -> "{".concat(list.getKey()).concat("}").concat(entry)))
            .collect(Collectors.joining("\n\t")));
    }

    private String getTypeIsUndefinedMsg(Parameter parameter, QName type, Map<String, List<String>> types) {
        return getTypeIsUndefinedMsg(parameter.getKey().concat(":type"), parameter, type, types);
    }

    private String getTypeIsUndefinedMsg(String typeString, Parameter parameter, QName type, Map<String, List<String>> types) {
        return typeString
            .concat(" \"")
            .concat(type.toString())
            .concat("\" is undefined!")
            .concat(getKnownTypes(types))
            .concat("\n")
            .concat(print(parameter.getContext()));
    }

    private String getTypeIsNullMsg(Parameter parameter) {
        return getIsNullMsg(parameter.getKey().concat(":type"), parameter);
    }

    private String getIsNullMsg(String name, Parameter parameter) {
        return name
            .concat(" is null!\n")
            .concat(print(parameter.getContext()));
    }

    private void validatePropertyOrAttributeDefinition(QName type, TEntrySchema entrySchema, Parameter parameter) {
        if (Objects.isNull(type)) {
            setException(new UnknownDataType(getTypeIsNullMsg(parameter)));
            return;
        }

        if (!validateTypeIsDefined(type, typeVisitor.getDataTypes())) {
            setException(new UnknownDataType(getTypeIsUndefinedMsg(parameter, type, typeVisitor.getDataTypes())));
        }

        if (type.getLocalPart().equals("list") || type.getLocalPart().equals("map")) {
            if (Objects.isNull(entrySchema)) {
                setException(new UnknownDataType(getIsNullMsg(parameter.getKey().concat(":entry_schema"), parameter)));
            }

            if (Objects.isNull(entrySchema.getType()) || !validateTypeIsDefined(entrySchema.getType(), typeVisitor.getDataTypes())) {
                setException(new UnknownDataType(getTypeIsUndefinedMsg(
                    parameter.getKey().concat(":entry_schema:type"),
                    parameter,
                    entrySchema.getType(),
                    typeVisitor.getDataTypes()
                )));
            }
        }
    }

    @Override
    public Result visit(TArtifactType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getArtifactTypes());
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TCapabilityType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getCapabilityTypes());

        for (QName source : node.getValidSourceTypes()) {
            if (!validateTypeIsDefined(source, typeVisitor.getNodeTypes())) {
                setException(new UnknownCapabilitySourceType(getTypeIsUndefinedMsg(
                    parameter.getKey().concat(":valid_source_types:").concat(source.toString()),
                    parameter,
                    source,
                    typeVisitor.getDataTypes()
                )));
            }
        }
        super.visit(node, parameter);
        return null;
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
            String msg = "The native data type \"".concat(parameter.getKey())
                .concat("\" cannot be extended with properties! \n").concat(print(parameter.getContext()));
            setException(new InvalidNativeTypeExtend(msg));
        }

        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TGroupType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getGroupTypes());
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TInterfaceType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getInterfaceTypes());
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TRelationshipType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getRelationshipTypes());
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TNodeType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getNodeTypes());
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TPolicyType node, Parameter parameter) {
        validateEntityType(node, parameter, typeVisitor.getPolicyTypes());
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TNodeTemplate node, Parameter parameter) {
        if (Objects.nonNull(node.getType()) && !validateTypeIsDefined(node.getType(), typeVisitor.getNodeTypes())) {
            setException(new MissingNodeType(getTypeIsUndefinedMsg(parameter, node.getType(), typeVisitor.getDataTypes())));
        }
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TPropertyDefinition node, Parameter parameter) {
        validatePropertyOrAttributeDefinition(node.getType(), node.getEntrySchema(), parameter);
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TAttributeDefinition node, Parameter parameter) {
        validatePropertyOrAttributeDefinition(node.getType(), node.getEntrySchema(), parameter);
        super.visit(node, parameter);
        return null;
    }

    private String print(List<String> list) {
        return "Context::INLINE = " + String.join(":", list);
    }
}

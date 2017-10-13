/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.common.validator;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.TArtifactType;
import org.eclipse.winery.model.tosca.yaml.TCapabilityType;
import org.eclipse.winery.model.tosca.yaml.TDataType;
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

    public TypeValidator(String path, String namespace) {
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
     * Validates that a map of lists contains a list mapped to the namespace uri of a QName
     * and the list contains the local name of the QName
     */
    private Boolean validateTypeIsDefined(QName type, Map<String, List<String>> map) {
        return map.containsKey(type.getNamespaceURI()) && map.get(type.getNamespaceURI()).contains(type.getLocalPart());
    }

    @Override
    public Result visit(TArtifactType node, Parameter parameter) {
        if (node.getDerivedFrom() != null && !validateTypeIsDefined(node.getDerivedFrom(), typeVisitor.getArtifactTypes())) {
            String msg = "The parent type \"" + node.getDerivedFrom() + "\" is undefined! \n" + print(parameter.getContext());
            setException(new InvalidParentType(msg));
        }
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TCapabilityType node, Parameter parameter) {
        if (node.getDerivedFrom() != null && !validateTypeIsDefined(node.getDerivedFrom(), typeVisitor.getCapabilityTypes())) {
            String msg = "The parent type \"" + node.getDerivedFrom() + "\" is undefined! \n" + print(parameter.getContext());
            setException(new InvalidParentType(msg));
        }

        if (node.getValidSourceTypes() != null) {
            for (QName source : node.getValidSourceTypes()) {
                if (!validateTypeIsDefined(source, typeVisitor.getNodeTypes())) {
                    String msg = "The valid source type \"" + source + "\" for the capability type \""
                        + parameter.getKey() + "\" is undefined. \n" + print(parameter.getContext());
                    setException(new UnknownCapabilitySourceType(msg));
                }
            }
        }

        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TDataType node, Parameter parameter) {
        if (node.getDerivedFrom() != null && !validateTypeIsDefined(node.getDerivedFrom(), typeVisitor.getDataTypes())) {
            String msg = "The parent type \"" + node.getDerivedFrom() + "\" is undefined! \n" + print(parameter.getContext());
            setException(new InvalidParentType(msg));
        }

        // Extend a native DataType should fail
        if (node.getDerivedFrom() != null &&
            (Defaults.YAML_TYPES.contains(node.getDerivedFrom().getLocalPart()) ||
                Defaults.TOSCA_TYPES.contains(node.getDerivedFrom().getLocalPart())
            ) &&
            node.getProperties() != null && node.getProperties().size() != 0) {
            String msg = "The native data type \"" + parameter.getKey() + "\" cannot be extended with properties! \n" + print(parameter.getContext());
            setException(new InvalidNativeTypeExtend(msg));
        }

        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TGroupType node, Parameter parameter) {
        if (node.getDerivedFrom() != null && !validateTypeIsDefined(node.getDerivedFrom(), typeVisitor.getGroupTypes())) {
            String msg = "The parent type \"" + node.getDerivedFrom() + "\" is undefined! \n" + print(parameter.getContext());
            setException(new InvalidParentType(msg));
        }
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TInterfaceType node, Parameter parameter) {
        if (node.getDerivedFrom() != null && !validateTypeIsDefined(node.getDerivedFrom(), typeVisitor.getInterfaceTypes())) {
            String msg = "The parent type \"" + node.getDerivedFrom() + "\" is undefined! \n" + print(parameter.getContext());
            setException(new InvalidParentType(msg));
        }
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TRelationshipType node, Parameter parameter) {
        if (node.getDerivedFrom() != null && !validateTypeIsDefined(node.getDerivedFrom(), typeVisitor.getRelationshipTypes())) {
            String msg = "The parent type \"" + node.getDerivedFrom() + "\" is undefined! \n" + print(parameter.getContext());
            setException(new InvalidParentType(msg));
        }
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TNodeType node, Parameter parameter) {
        if (node.getDerivedFrom() != null && !validateTypeIsDefined(node.getDerivedFrom(), typeVisitor.getNodeTypes())) {
            String msg = "The parent type \"" + node.getDerivedFrom() + "\" is undefined! \n" + print(parameter.getContext());
            setException(new InvalidParentType(msg));
        }
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TPolicyType node, Parameter parameter) {
        if (node.getDerivedFrom() != null && !validateTypeIsDefined(node.getDerivedFrom(), typeVisitor.getPolicyTypes())) {
            String msg = "The parent type \"" + node.getDerivedFrom() + "\" is undefined! \n" + print(parameter.getContext());
            setException(new InvalidParentType(msg));
        }
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TNodeTemplate node, Parameter parameter) {
        if (node.getType() != null && !validateTypeIsDefined(node.getType(), typeVisitor.getNodeTypes())) {
            // TODO add parameter.getContext to exception
            setException(new MissingNodeType(node.getType().toString()));
        }
        super.visit(node, parameter);
        return null;
    }

    @Override
    public Result visit(TPropertyDefinition node, Parameter parameter) {
        if (node.getType() != null && !validateTypeIsDefined(node.getType(), typeVisitor.getDataTypes())) {
            String msg = parameter.getKey() + ":type \"" + node.getType() + "\" is undefined!\n" + print(parameter.getContext());
            setException(new UnknownDataType(msg));
        }

        if (node.getType() != null && (node.getType().getLocalPart().equals("list") || node.getType().getLocalPart().equals("map"))) {
            TEntrySchema entrySchema = node.getEntrySchema();
            if (node.getEntrySchema() != null && entrySchema.getType() != null && !validateTypeIsDefined(entrySchema.getType(), typeVisitor.getDataTypes())) {
                String msg = parameter.getKey() + "entry_schema:type \"" + entrySchema.getType() + "\" is undefined!" + print(parameter.getContext());
                setException(new UnknownDataType(msg));
            }
        }

        super.visit(node, parameter);
        return null;
    }

    private String print(List<String> list) {
        return "Context::INLINE = " + String.join(":", list);
    }
}

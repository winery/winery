/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.yaml.common.writer.yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.writer.yaml.support.Printer;

public class Writer extends AbstractVisitor<Printer, Writer.Parameter> {
    private final int INDENT_SIZE;

    public Writer() {
        this.INDENT_SIZE = 2;
    }

    public Writer(int indentSize) {
        this.INDENT_SIZE = indentSize;
    }

    public void write(TServiceTemplate serviceTemplate, String fileName) {
        try {
            File file = new File(fileName);
            file.getParentFile().mkdir();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(this.visit(serviceTemplate, new Parameter(0)).toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Printer visit(TServiceTemplate node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("tosca_definitions_version", node.getToscaDefinitionsVersion())
            .printNewLine()
            .print(node.getMetadata().accept(this, parameter))
            .print(printListMap("imports",
                node.getImports().stream().map(TMapImportDefinition::getMap).collect(Collectors.toList()),
                parameter))
            .printKeyValue("description", node.getDescription())
            .print(printMapObject("dsl_definitions", node.getDslDefinitions(), parameter))
            .print(printMap("repositories", node.getRepositories(), parameter))
            .print(printMap("artifact_types", node.getArtifactTypes(), parameter))
            .print(printMap("data_types", node.getDataTypes(), parameter))
            .print(printMap("capability_types", node.getCapabilityTypes(), parameter))
            .print(printMap("interface_types", node.getInterfaceTypes(), parameter))
            .print(printMap("relationship_types", node.getRelationshipTypes(), parameter))
            .print(printMap("node_types", node.getNodeTypes(), parameter))
            .print(printMap("group_types", node.getGroupTypes(), parameter))
            .print(printMap("policy_types", node.getPolicyTypes(), parameter))
            .print(printVisitorNode(node.getTopologyTemplate(), new Parameter(parameter.getIndent()).addContext("topology_template")));
    }

    public Printer visit(TTopologyTemplateDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .print(printMap("inputs", node.getInputs(), parameter))
            .print(printMap("node_templates", node.getNodeTemplates(), parameter))
            .print(printMap("relationship_templates", node.getRelationshipTemplates(), parameter))
            .print(printMap("groups", node.getGroups(), parameter))
            .print(printMap("policies", node.getPolicies(), parameter))
            .print(printMap("outputs", node.getOutputs(), parameter))
            .print(printVisitorNode(node.getSubstitutionMappings(), new Parameter(parameter.getIndent()).addContext("substitution_mappings")));
    }

    public Printer visit(Metadata node, Parameter parameter) {
        Printer printer = new Printer(parameter.getIndent());
        if (!node.isEmpty()) {
            printer.printKey("metadata")
                .indent(INDENT_SIZE);
            node.forEach(printer::printKeyValue);
            printer.indent(-INDENT_SIZE);
        }
        return printer;
    }

    public Printer visit(TRepositoryDefinition node, Parameter parameter) {
        Printer printer = new Printer(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("url", node.getUrl());
        if (Objects.nonNull(node.getCredential())) {
            Credential credential = node.getCredential();
            printer.printKey("credential")
                .indent(INDENT_SIZE)
                .printKeyValue("protocol", credential.getProtocol())
                .printKeyValue("token_type", credential.getTokenType())
                .printKeyValue("token", credential.getToken())
                .printKeyObject("keys", credential.getKeys())
                .printKeyValue("user", credential.getUser())
                .indent(-INDENT_SIZE);
        }
        return printer;
    }

    public Printer visit(TImportDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("file", node.getFile())
            .printKeyValue("repository", node.getRepository())
            .printKeyValue("namespace_uri", node.getNamespaceUri(), !node.getNamespaceUri().equals(Namespaces.DEFAULT_NS))
            .printKeyValue("namespace_prefix", node.getNamespacePrefix());
    }

    public Printer visit(TArtifactType node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("mime_type", node.getMimeType())
            .printKeyValue("file_ext", node.getFileExt());
    }

    public Printer visit(TEntityType node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("version", node.getVersion())
            .printKeyValue("derived_from", node.getDerivedFrom())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter))
            .print(node.getMetadata().accept(this, parameter));
    }

    public Printer visit(TPropertyDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("required", node.getRequired())
            .printKeyObject("default", node.getDefault())
            .printKeyValue("status", node.getStatus())
            .print(printList("constraints", node.getConstraints(), parameter))
            .print(printVisitorNode(node.getEntrySchema(), parameter));
    }

    public Printer visit(TConstraintClause node, Parameter parameter) {
        return new Printer(parameter.getIndent()).printKeyObject("- equal", node.getEqual())
            .printKeyObject("- greater_than", node.getGreaterThan())
            .printKeyObject("- greater_or_equal", node.getGreaterOrEqual())
            .printKeyObject("- less_than", node.getLessThan())
            .printKeyObject("- less_or_equal", node.getLessOrEqual())
            .printKeyListObjectInline("- in_range", node.getInRange())
            .printKeyListObjectInline("- valid_values", node.getValidValues())
            .printKeyObject("- length", node.getLength())
            .printKeyObject("- min_length", node.getMinLength())
            .printKeyObject("- max_length", node.getMaxLength())
            .printKeyObject("- pattern", node.getPattern());
    }

    public Printer visit(TEntrySchema node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(printList("constraints", node.getConstraints(), parameter));
    }

    public Printer visit(TAttributeDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("type", node.getType())
            .printKeyObject("default", node.getDefault())
            .printKeyValue("status", node.getStatus())
            .print(printVisitorNode(node.getEntrySchema(), parameter));
    }

    public Printer visit(TDataType node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .print(printList("constraints", node.getConstraints(), parameter));
    }

    public Printer visit(TCapabilityType node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("valid_source_types", node.getValidSourceTypes());
    }

    public Printer visit(TInterfaceType node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .print(node.getOperations().entrySet().stream()
                .map(entry ->
                    printVisitorNode(entry.getValue(), new Parameter(parameter.getIndent()).addContext(entry.getKey()))
                )
                .reduce(Printer::print)
            )
            .print(printMap("inputs", node.getInputs(), parameter));
    }

    public Printer visit(TOperationDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .print(printMap("inputs", node.getInputs(), new Parameter(parameter.getIndent())))
            .print(printMap("outputs", node.getOutputs(), new Parameter(parameter.getIndent())))
            .print(printVisitorNode(node.getImplementation(), new Parameter(parameter.getIndent()).addContext("implementation")));
    }

    public Printer visit(TImplementation node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("primary", node.getPrimary())
            .printKeyValue("dependencies", node.getDependencies());
    }

    public Printer visit(TRelationshipType node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("valid_target_types", node.getValidTargetTypes())
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public Printer visit(TInterfaceDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .print(printMap("inputs", node.getInputs(), parameter))
            .print(node.getOperations().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry) && Objects.nonNull(entry.getValue()))
                .map(entry ->
                    printVisitorNode(entry.getValue(), new Parameter(parameter.getIndent()).addContext(entry.getKey()))
                )
                .reduce(Printer::print)
            );
    }

    public Printer visit(TNodeType node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .print(printMap("attributes", node.getAttributes(), parameter))
            .print(printListMap("requirements", node.getRequirements().stream().map(TMapRequirementDefinition::getMap).collect(Collectors.toList()), parameter))
            .print(printMap("capabilities", node.getCapabilities(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter))
            .print(printMap("artifacts", node.getArtifacts(), parameter));
    }

    public Printer visit(TRequirementDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("capability", node.getCapability())
            .printKeyValue("node", node.getNode())
            .print(printVisitorNode(node.getRelationship(), new Parameter(parameter.getIndent()).addContext("relationship")))
            .printKeyValue("occurrences", node.getOccurrences())
            .printKeyValue("description", node.getDescription());
    }

    public Printer visit(TRelationshipDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public Printer visit(TCapabilityDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("occurrences", node.getOccurrences())
            .printKeyValue("valid_source_types", node.getValidSourceTypes())
            .printKeyValue("type", node.getType())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter));
    }

    public Printer visit(TArtifactDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("repository", node.getRepository())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("deploy_path", node.getDeployPath())
            .printKeyValue("file", node.getFile())
            .printKeyValue("files", node.getFiles());
    }

    public Printer visit(TGroupType node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("members", node.getMembers())
            .print(printListMap("requirements", node.getRequirements().stream().map(TMapRequirementDefinition::getMap).collect(Collectors.toList()), parameter))
            .print(printMap("capabilities", node.getCapabilities(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public Printer visit(TPolicyType node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("targets", node.getTargets())
            .printKeyObject("triggers", node.getTriggers());
    }

    public Printer visit(TVersion node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .print(node.getVersion());
    }

    public Printer visit(TNodeTemplate node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(node.getMetadata().accept(this, parameter))
            .printKeyValue("directives", node.getDirectives())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter))
            .print(printListMap("requirements", node.getRequirements().stream().map(TMapRequirementAssignment::getMap).collect(Collectors.toList()), parameter))
            .print(printMap("capabilities", node.getCapabilities(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter))
            .print(printMap("artifacts", node.getArtifacts(), parameter))
            .print(printVisitorNode(node.getNodeFilter(), parameter))
            .printKeyValue("copy", node.getCopy());
    }

    public Printer visit(TGroupDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(node.getMetadata().accept(this, parameter))
            .print(printMap("properties", node.getProperties(), parameter))
            .printKeyValue("members", node.getMembers())
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public Printer visit(TPolicyDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(node.getMetadata().accept(this, parameter))
            .print(printMap("properties", node.getProperties(), parameter))
            .printKeyValue("targets", node.getTargets());
    }

    public Printer visit(TPropertyAssignment node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyObject(parameter.getKey(), node.getValue());
    }

    public Printer visit(TAttributeAssignment node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printKeyObject("value", node.getValue());
    }

    public Printer visit(TInterfaceAssignment node, Parameter parameter) {
        return new Printer(parameter.getIndent());
    }

    public Printer visit(TParameterDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyObject(parameter.getKey(), node.getValue());
    }

    public Printer visit(TCapabilityAssignment node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter));
    }

    public Printer visit(TNodeFilterDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .print(printListMap("properties",
                node.getProperties().stream().map(TMapPropertyFilterDefinition::getMap).collect(Collectors.toList()),
                parameter)
            )
            .print(printListMap("capabilities",
                node.getCapabilities().stream().map(TMapObject::getMap).collect(Collectors.toList()),
                parameter)
            );
    }

    public Printer visit(TRelationshipTemplate node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(node.getMetadata().accept(this, parameter))
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter))
            .printKeyValue("copy", node.getCopy());
    }

    public Printer visit(TSubstitutionMappings node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("node_type", node.getNodeType())
            .print(printMapInlineStringList("capabilities", node.getCapabilities(), parameter));
    }

    public Printer visit(TRequirementAssignment node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("node", node.getNode())
            .print(printVisitorNode(node.getRelationship(), new Parameter(parameter.getIndent()).addContext("relationship")))
            .printKeyValue("capability", node.getCapability())
            .print(printVisitorNode(node.getNodeFilter(), new Parameter(parameter.getIndent()).addContext("node_filter")))
            .printKeyValue("occurrences", node.getOccurrences());
    }

    public Printer visit(TRelationshipAssignment node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public Printer visit(TPropertyFilterDefinition node, Parameter parameter) {
        return new Printer(parameter.getIndent())
            .print(printList("constraints", node.getConstraints(), parameter));
    }

    public Printer printVisitorNode(VisitorNode node, Parameter parameter) {
        Printer printer = new Printer(parameter.getIndent());
        if (Objects.nonNull(node)) {
            if (node instanceof TPropertyAssignment) {
                printer.print(node.accept(this,
                    new Parameter(parameter.getIndent()).addContext(parameter.getKey())
                ));
            } else {
                printer.printKey(parameter.getKey())
                    .print(node.accept(this, new Parameter(parameter.getIndent() + INDENT_SIZE)));
            }
        }
        return printer;
    }

    private <T> Printer printList(String keyValue, List<T> list, Parameter parameter) {
        Printer printer = new Printer(parameter.getIndent());
        if (!list.isEmpty()) {
            printer.printKey(keyValue)
                .print(list.stream()
                    .map(entry -> ((VisitorNode) entry).accept(this, new Parameter(parameter.getIndent() + INDENT_SIZE)))
                    .reduce(Printer::print)
                );
        }
        return printer;
    }

    private <T> Printer printMap(String keyValue, Map<String, T> map, Parameter parameter) {
        Printer printer = new Printer(parameter.getIndent());
        if (!map.isEmpty()) {
            printer.printKey(keyValue)
                .print(map.entrySet().stream()
                    .filter(entry -> entry.getValue() instanceof VisitorNode)
                    .map((entry) -> {
                            Printer p = new Printer(parameter.getIndent() + INDENT_SIZE)
                                .print(
                                    printVisitorNode((VisitorNode) entry.getValue(),
                                        new Parameter(parameter.getIndent() + INDENT_SIZE).addContext(entry.getKey())
                                    )
                                );
                            return p;
                        }
                    )
                    .reduce(Printer::print)
                );
        }
        return printer;
    }

    private <T> Printer printListMap(String keyValue, List<Map<String, T>> list, Parameter parameter) {
        Printer printer = new Printer(parameter.getIndent());
        if (!list.isEmpty()) {
            printer.printKey(keyValue)
                .print(list.stream()
                    .flatMap(map -> map.entrySet().stream())
                    .map(
                        (entry) -> new Printer(parameter.getIndent() + INDENT_SIZE)
                            .printListKey(entry.getKey())
                            .print(((VisitorNode) entry.getValue())
                                .accept(this,
                                    new Parameter(parameter.getIndent() + 3 * INDENT_SIZE).addContext(entry.getKey())
                                ))
                    )
                    .reduce(Printer::print)
                );
        }
        return printer;
    }

    private Printer printMapInlineStringList(String keyValue, Map<String, TListString> map, Parameter parameter) {
        Printer printer = new Printer(parameter.getIndent());
        if (!map.isEmpty()) {
            printer.printKey(keyValue)
                .print(map.entrySet().stream()
                    .map(entry -> new Printer(parameter.getIndent() + 2)
                        .printKeyValue(entry.getKey(), entry.getValue())
                    )
                    .reduce(Printer::print)
                );
        }
        return printer;
    }

    private Printer printMapObject(String keyValue, Map<String, ? extends Object> map, Parameter parameter) {
        Printer printer = new Printer(parameter.getIndent());
        if (!map.isEmpty()) {
            printer.printCheckNewLine()
                .printKey(keyValue)
                .indent(INDENT_SIZE);
            map.forEach(printer::printKeyObject);
            printer.indent(-INDENT_SIZE);
        }
        return printer;
    }

    public static class Parameter extends AbstractParameter<Parameter> {
        private final int indent;

        public Parameter(int indent) {
            this.indent = indent;
        }

        public int getIndent() {
            return this.indent;
        }

        @Override
        public Parameter copy() {
            return new Parameter(this.indent).addContext(this.getContext());
        }

        @Override
        public Parameter self() {
            return this;
        }
    }
}

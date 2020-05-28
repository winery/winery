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
package org.eclipse.winery.repository.converter.support.writer;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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
import org.eclipse.winery.model.tosca.yaml.support.TMapObject;
import org.eclipse.winery.model.tosca.yaml.support.TMapPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.tosca.datatypes.Credential;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YamlWriter extends AbstractVisitor<YamlPrinter, YamlWriter.Parameter> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVisitor.class);

    private final int INDENT_SIZE;

    public YamlWriter() {
        this.INDENT_SIZE = 2;
    }

    public YamlWriter(int indentSize) {
        this.INDENT_SIZE = indentSize;
    }

    public InputStream writeToInputStream(TServiceTemplate serviceTemplate) {
        try {
            String output = this.visit(serviceTemplate, new Parameter(0)).toString();
            return new ByteArrayInputStream(output.getBytes());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return null;
    }

    public void write(TServiceTemplate serviceTemplate, Path fileName) {
        Objects.requireNonNull(serviceTemplate);
        try {
            Files.createDirectories(Objects.requireNonNull(fileName).getParent());
        } catch (IOException e) {
            LOGGER.debug("Could not create directory", e);
            return;
        }
        try (FileWriter fileWriter = new FileWriter(fileName.toFile())) {
            fileWriter.write(this.visit(serviceTemplate, new Parameter(0)).toString());
        } catch (IOException e) {
            LOGGER.debug("Could write to file", e);
        }
    }

    /**
     * @deprecated Use {@link YamlWriter#write(TServiceTemplate, Path)}
     */
    @Deprecated
    public void write(TServiceTemplate serviceTemplate, String fileName) {
        this.write(serviceTemplate, Paths.get(fileName));
    }

    public YamlPrinter visit(TServiceTemplate node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("tosca_definitions_version", node.getToscaDefinitionsVersion())
            .printNewLine()
            .print(node.getMetadata().accept(this, parameter))
            .print(printList("imports",
                node.getImports().stream().map(m -> m.getMap().values()).flatMap(Collection::stream).collect(Collectors.toList()),
                parameter))
            // .printKeyValue("description", node.getDescription())
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

    public YamlPrinter visit(TTopologyTemplateDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .print(printMap("inputs", node.getInputs(), parameter))
            .print(printMap("node_templates", node.getNodeTemplates(), parameter))
            .print(printMap("relationship_templates", node.getRelationshipTemplates(), parameter))
            .print(printMap("groups", node.getGroups(), parameter))
            .print(printMap("policies", node.getPolicies(), parameter))
            .print(printMap("outputs", node.getOutputs(), parameter))
            .print(printVisitorNode(node.getSubstitutionMappings(), new Parameter(parameter.getIndent()).addContext("substitution_mappings")));
    }

    public YamlPrinter visit(Metadata node, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent());
        if (!node.isEmpty()) {
            printer.printKey("metadata")
                .indent(INDENT_SIZE);
            node.forEach((key, value) -> printer.printKeyValue(key, value, true));
            printer.indent(-INDENT_SIZE);
        }
        return printer;
    }

    public YamlPrinter visit(TRepositoryDefinition node, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent())
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

    public YamlPrinter visit(TImportDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("- file", node.getFile())
            .printKeyValue("  repository", node.getRepository())
            .printKeyValue("  namespace_uri", node.getNamespaceUri())
            // .printKeyValue("  namespace_uri", node.getNamespaceUri(), !node.getNamespaceUri().equals(Namespaces.DEFAULT_NS))
            .printKeyValue("  namespace_prefix", node.getNamespacePrefix());
    }

    public YamlPrinter visit(TArtifactType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("mime_type", node.getMimeType())
            .printKeyValue("file_ext", node.getFileExt());
    }

    public YamlPrinter visit(TEntityType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("version", node.getVersion())
            .printKeyValue("derived_from", node.getDerivedFrom())
            .print(node.getMetadata().accept(this, parameter))
            .print(printMap("attributes", node.getAttributes(), parameter))
            .print(printMap("properties", node.getProperties(), parameter));
    }

    public YamlPrinter visit(TPropertyDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("required", node.getRequired())
            .printYamlValue("default", node.getDefault())
            .printKeyValue("status", node.getStatus())
            .print(printList("constraints", node.getConstraints(), parameter))
            .print(printVisitorNode(node.getEntrySchema(), parameter));
    }

    public YamlPrinter visit(TEntrySchema node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(printList("constraints", node.getConstraints(), parameter));
    }

    public YamlPrinter visit(TAttributeDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("type", node.getType())
            .printYamlValue("default", node.getDefault())
            .printKeyValue("status", node.getStatus())
            .print(printVisitorNode(node.getEntrySchema(), parameter.addContext("entry_schema")));
    }

    public YamlPrinter visit(TDataType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(printList("constraints", node.getConstraints(), parameter));
    }

    public YamlPrinter visit(TCapabilityType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("valid_source_types", node.getValidSourceTypes());
    }

    public YamlPrinter visit(TInterfaceType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(node.getOperations().entrySet().stream()
                .map(entry ->
                    printVisitorNode(entry.getValue(), new Parameter(parameter.getIndent()).addContext(entry.getKey()))
                )
                .reduce(YamlPrinter::print)
            )
            .print(printMap("inputs", node.getInputs(), parameter));
    }

    public YamlPrinter visit(TOperationDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .print(printMap("inputs", node.getInputs(), new Parameter(parameter.getIndent())))
            .print(printMap("outputs", node.getOutputs(), new Parameter(parameter.getIndent())))
            .print(printVisitorNode(node.getImplementation(), new Parameter(parameter.getIndent()).addContext("implementation")));
    }

    public YamlPrinter visit(TImplementation node, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent())
            .printKeyValue("primary", node.getPrimaryArtifactName())
            .printKeyValue("dependencies", node.getDependencyArtifactNames())
            .printKeyValue("operation_host", node.getOperationHost());
        if (node.getTimeout() != null) {
            printer.printKeyValue("timeout", node.getTimeout().toString());
        }
        return printer;
    }

    public YamlPrinter visit(TRelationshipType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("valid_target_types", node.getValidTargetTypes())
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public YamlPrinter visit(TInterfaceDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .print(printMap("inputs", node.getInputs(), parameter))

            .print(printMap("operations", node.getOperations(), parameter));

//            .print(node.getOperations().entrySet().stream()
//                .filter(entry -> Objects.nonNull(entry) && Objects.nonNull(entry.getValue()))
//                .map(entry ->
//                    printVisitorNode(entry.getValue(), new Parameter(parameter.getIndent()).addContext(entry.getKey()))
//                )
//                .reduce(YamlPrinter::print)
//            );
    }

    public YamlPrinter visit(TNodeType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(printListMap("requirements", node.getRequirements().stream().map(TMapRequirementDefinition::getMap).collect(Collectors.toList()), parameter))
            .print(printMap("capabilities", node.getCapabilities(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter))
            .print(printMap("artifacts", node.getArtifacts(), parameter));
    }

    public YamlPrinter visit(TRequirementDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("capability", node.getCapability())
            .printKeyValue("node", node.getNode())
            .print(printVisitorNode(node.getRelationship(), new Parameter(parameter.getIndent()).addContext("relationship")))
            .printKeyValue("occurrences", node.getOccurrences())
            .printKeyValue("description", node.getDescription());
    }

    public YamlPrinter visit(TRelationshipDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            // Removed to support short notations
            // .printKeyValue("type", node.getType())
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public YamlPrinter visit(TCapabilityDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("occurrences", node.getOccurrences())
            .printKeyValue("valid_source_types", node.getValidSourceTypes())
            .printKeyValue("type", node.getType())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter));
    }

    public YamlPrinter visit(TArtifactDefinition node, Parameter parameter) {
        YamlPrinter output = new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("repository", node.getRepository())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("deploy_path", node.getDeployPath());
        if (node.getFile() != null) {
            output.printKeyValue("file", node.getFile());
        }
        return output;
    }

    public YamlPrinter visit(TGroupType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("members", node.getMembers())
            .print(printListMap("requirements", node.getRequirements().stream().map(TMapRequirementDefinition::getMap).collect(Collectors.toList()), parameter))
            .print(printMap("capabilities", node.getCapabilities(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public YamlPrinter visit(TPolicyType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("targets", node.getTargets())
            .printKeyObject("triggers", node.getTriggers());
    }

    public YamlPrinter visit(TVersion node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(node.getVersion());
    }

    public YamlPrinter visit(TNodeTemplate node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
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

    public YamlPrinter visit(TGroupDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(node.getMetadata().accept(this, parameter))
            .print(printMap("properties", node.getProperties(), parameter))
            .printKeyValue("members", node.getMembers())
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public YamlPrinter visit(TPolicyDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(node.getMetadata().accept(this, parameter))
            .print(printMap("properties", node.getProperties(), parameter))
            .printKeyValue("targets", node.getTargets());
    }

    public YamlPrinter visit(TPropertyAssignment node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printYamlValue(parameter.getKey(), node.getValue(), true);
    }

    public YamlPrinter visit(TAttributeAssignment node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printYamlValue(parameter.getKey(), node.getValue(), true);
    }

    public YamlPrinter visit(TInterfaceAssignment node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent());
    }

    public YamlPrinter visit(TParameterDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("required", node.getRequired())
            .printYamlValue("default", node.getDefault())
            .printKeyValue("status", node.getStatus())
            .print(printList("constraints", node.getConstraints(), parameter))
            .print(printVisitorNode(node.getEntrySchema(), parameter))
            .printYamlValue("value", node.getValue());
    }

    public YamlPrinter visit(TConstraintClause node, Parameter parameter) {
        if (node.getValue() != null) {
            return new YamlPrinter(parameter.getIndent())
                .printKeyValue("- " + node.getKey(), node.getValue());
        } else if (node.getList() != null) {
            return new YamlPrinter(parameter.getIndent())
                .printKeyListObjectInline("- " + node.getKey(), new ArrayList<>(node.getList()));
        }
        return null;
    }

    public YamlPrinter visit(TCapabilityAssignment node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter));
    }

    public YamlPrinter visit(TNodeFilterDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(printListMap("properties",
                node.getProperties().stream().map(TMapPropertyFilterDefinition::getMap).collect(Collectors.toList()),
                parameter)
            )
            .print(printListMap("capabilities",
                node.getCapabilities().stream().map(TMapObject::getMap).collect(Collectors.toList()),
                parameter)
            );
    }

    public YamlPrinter visit(TRelationshipTemplate node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(node.getMetadata().accept(this, parameter))
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter))
            .printKeyValue("copy", node.getCopy());
    }

    public YamlPrinter visit(TSubstitutionMappings node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("node_type", node.getNodeType())
            .print(printMapInlineStringList("capabilities", node.getCapabilities(), parameter));
    }

    public YamlPrinter visit(TRequirementAssignment node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("node", node.getNode())
            .print(printVisitorNode(node.getRelationship(), new Parameter(parameter.getIndent()).addContext("relationship")))
            .printKeyValue("capability", node.getCapability())
            .print(printVisitorNode(node.getNodeFilter(), new Parameter(parameter.getIndent()).addContext("node_filter")))
            .printKeyValue("occurrences", node.getOccurrences());
    }

    public YamlPrinter visit(TRelationshipAssignment node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            // Removed to support short notations
            // .printKeyValue("type", node.getType())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public YamlPrinter visit(TPropertyFilterDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(printList("constraints", node.getConstraints(), parameter));
    }

    public YamlPrinter printVisitorNode(VisitorNode node, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent());
        if (Objects.nonNull(node)) {
            if (node instanceof TPropertyAssignment) {
                printer.print(node.accept(this,
                    new Parameter(parameter.getIndent()).addContext(parameter.getKey())
                ));
            } else if (node instanceof TRelationshipAssignment) {
                printer.print(parameter.getKey() + ": ").printQName(((TRelationshipAssignment) node).getType())
                    .print(node.accept(this, new Parameter(parameter.getIndent() + INDENT_SIZE)));
            } else if (node instanceof TRelationshipDefinition) {
                printer.print(parameter.getKey() + ": ").printQName(((TRelationshipDefinition) node).getType())
                    .print(node.accept(this, new Parameter(parameter.getIndent() + INDENT_SIZE)));
            } else {
                printer.printKey(parameter.getKey())
                    .print(node.accept(this, new Parameter(parameter.getIndent() + INDENT_SIZE)));
            }
        }
        return printer;
    }

    private <T> YamlPrinter printList(String keyValue, List<T> list, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent());
        if (list != null && !list.isEmpty()) {
            printer.printKey(keyValue)
                .print(list.stream()
                    .map(entry -> ((VisitorNode) entry).accept(this, new Parameter(parameter.getIndent() + INDENT_SIZE)))
                    .reduce(YamlPrinter::print)
                );
        }
        return printer;
    }

    private <T> YamlPrinter printMap(String keyValue, Map<String, T> map, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent());
        if (!map.isEmpty()) {
            printer.printKey(keyValue)
                .print(map.entrySet().stream()
                    .filter(entry -> entry.getValue() instanceof VisitorNode)
                    .map((entry) -> {
                            YamlPrinter p = new YamlPrinter(parameter.getIndent() + INDENT_SIZE)
                                .print(
                                    printVisitorNode((VisitorNode) entry.getValue(),
                                        new Parameter(parameter.getIndent() + INDENT_SIZE).addContext(entry.getKey())
                                    )
                                );
                            return p;
                        }
                    )
                    .reduce(YamlPrinter::print)
                );
        }
        return printer;
    }

    private <T> YamlPrinter printListMap(String keyValue, List<Map<String, T>> list, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent());
        if (!list.isEmpty()) {
            printer.printKey(keyValue)
                .print(list.stream()
                    .flatMap(map -> map.entrySet().stream())
                    .map(
                        (entry) -> new YamlPrinter(parameter.getIndent() + INDENT_SIZE)
                            .printListKey(entry.getKey())
                            .print(((VisitorNode) entry.getValue())
                                .accept(this,
                                    new Parameter(parameter.getIndent() + 3 * INDENT_SIZE).addContext(entry.getKey())
                                ))
                    )
                    .reduce(YamlPrinter::print)
                );
        }
        return printer;
    }

    private YamlPrinter printMapInlineStringList(String keyValue, Map<String, TListString> map, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent());
        if (!map.isEmpty()) {
            printer.printKey(keyValue)
                .print(map.entrySet().stream()
                    .map(entry -> new YamlPrinter(parameter.getIndent() + 2)
                        .printKeyValue(entry.getKey(), entry.getValue())
                    )
                    .reduce(YamlPrinter::print)
                );
        }
        return printer;
    }

    private YamlPrinter printMapObject(String keyValue, Map<String, ? extends Object> map, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent());
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

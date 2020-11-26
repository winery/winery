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
package org.eclipse.winery.repository.converter.writer;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
import org.eclipse.winery.model.tosca.yaml.YTSchemaDefinition;
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
import org.eclipse.winery.model.tosca.yaml.YTPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.YTPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipAssignment;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipDefinition;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipTemplate;
import org.eclipse.winery.model.tosca.yaml.YTRelationshipType;
import org.eclipse.winery.model.tosca.yaml.YTRepositoryDefinition;
import org.eclipse.winery.model.tosca.yaml.YTRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.YTRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.YTServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.YTStatusValue;
import org.eclipse.winery.model.tosca.yaml.YTSubstitutionMappings;
import org.eclipse.winery.model.tosca.yaml.YTTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.YTVersion;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.YTListString;
import org.eclipse.winery.model.tosca.yaml.support.YTMapImportDefinition;
import org.eclipse.winery.model.tosca.yaml.support.YTMapObject;
import org.eclipse.winery.model.tosca.yaml.support.YTMapPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.support.YTMapRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.support.YTMapRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.tosca.datatypes.Credential;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// FIXME this belongs with the specific implementation
public class YamlWriter extends AbstractVisitor<YamlPrinter, YamlWriter.Parameter> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVisitor.class);

    private static final String[] PROPERTY_FUNCTIONS = new String[] {
        "get_input", "get_property", "get_attribute", "get_operation_output", "get_nodes_of_type", "get_artifact"
    };

    private final int INDENT_SIZE;

    public YamlWriter() {
        this.INDENT_SIZE = 2;
    }

    public YamlWriter(int indentSize) {
        this.INDENT_SIZE = indentSize;
    }

    public InputStream writeToInputStream(YTServiceTemplate serviceTemplate) {
        try {
            String output = this.visit(serviceTemplate, new Parameter(0)).toString();
            return new ByteArrayInputStream(output.getBytes());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return null;
    }

    public void write(YTServiceTemplate serviceTemplate, Path fileName) {
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
     * @deprecated Use {@link YamlWriter#write(YTServiceTemplate, Path)}
     */
    @Deprecated
    public void write(YTServiceTemplate serviceTemplate, String fileName) {
        this.write(serviceTemplate, Paths.get(fileName));
    }

    public YamlPrinter visit(YTServiceTemplate node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("tosca_definitions_version", node.getToscaDefinitionsVersion())
            .printNewLine()
            .print(node.getMetadata().accept(this, parameter))
            .print(printList("imports",
                node.getImports().stream().map(YTMapImportDefinition::values).flatMap(Collection::stream).collect(Collectors.toList()),
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

    public YamlPrinter visit(YTTopologyTemplateDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .print(printMap("inputs", node.getInputs(), parameter))
            .print(printMap("node_templates", node.getNodeTemplates(), parameter))
            .print(printMap("relationship_templates", node.getRelationshipTemplates(), parameter))
            .print(printMap("groups", node.getGroups(), parameter))
            .print(printListMap("policies",
                node.getPolicies().size() > 0 ? Collections.singletonList(node.getPolicies()) : new ArrayList<>(), parameter))
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

    public YamlPrinter visit(YTRepositoryDefinition node, Parameter parameter) {
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

    public YamlPrinter visit(YTImportDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("file", node.getFile())
            .printKeyValue("repository", node.getRepository())
            .printKeyValue("namespace_uri", node.getNamespaceUri())
            // .printKeyValue("namespace_uri", node.getNamespaceUri(), !node.getNamespaceUri().equals(Namespaces.DEFAULT_NS))
            .printKeyValue("namespace_prefix", node.getNamespacePrefix());
    }

    public YamlPrinter visit(YTArtifactType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("mime_type", node.getMimeType())
            .printKeyValue("file_ext", node.getFileExt());
    }

    public YamlPrinter visit(YTEntityType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("version", node.getVersion())
            .printKeyValue("derived_from", node.getDerivedFrom())
            .print(node.getMetadata().accept(this, parameter))
            .print(printMap("attributes", node.getAttributes(), parameter))
            .print(printMap("properties", node.getProperties(), parameter));
    }

    public YamlPrinter visit(YTPropertyDefinition node, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription());
        // do not print the default value for required to avoid bloating the output
        if (!node.getRequired()) {
            printer.printKeyValue("required", node.getRequired());
        }
        printer.printYamlValue("default", node.getDefault());
        // do not print the default value for status to avoid bloating the output
        if (node.getStatus() != YTStatusValue.supported) {
            printer.printKeyValue("status", node.getStatus());
        }
        printer.print(printList("constraints", node.getConstraints(), parameter));
        if (node.getEntrySchema() != null) {
            printer.printKey("entry_schema").print(visit(node.getEntrySchema(), new Parameter(parameter.getIndent() + INDENT_SIZE)));
        }
        if (node.getKeySchema() != null) {
            printer.printKey("key_schema").print(visit(node.getKeySchema(), new Parameter(parameter.getIndent() + INDENT_SIZE)));
        }

        return printer;
    }

    public YamlPrinter visit(YTSchemaDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(printList("constraints", node.getConstraints(), parameter));
    }

    public YamlPrinter visit(YTAttributeDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("type", node.getType())
            .printYamlValue("default", node.getDefault())
            .printKeyValue("status", node.getStatus())
            .print(printVisitorNode(node.getEntrySchema(), parameter.addContext("entry_schema")));
    }

    public YamlPrinter visit(YTDataType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(printList("constraints", node.getConstraints(), parameter));
    }

    public YamlPrinter visit(YTCapabilityType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("valid_source_types", node.getValidSourceTypes());
    }

    public YamlPrinter visit(YTInterfaceType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(node.getOperations().entrySet().stream()
                .map(entry ->
                    printVisitorNode(entry.getValue(), new Parameter(parameter.getIndent()).addContext(entry.getKey()))
                )
                .reduce(YamlPrinter::print)
            )
            .print(printMap("inputs", node.getInputs(), parameter));
    }

    public YamlPrinter visit(YTOperationDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .print(printMap("inputs", node.getInputs(), new Parameter(parameter.getIndent())))
            .print(printMap("outputs", node.getOutputs(), new Parameter(parameter.getIndent())))
            .print(printVisitorNode(node.getImplementation(), new Parameter(parameter.getIndent()).addContext("implementation")));
    }

    public YamlPrinter visit(YTImplementation node, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent())
            .printKeyValue("primary", node.getPrimaryArtifactName())
            .printKeyValue("dependencies", node.getDependencyArtifactNames())
            .printKeyValue("operation_host", node.getOperationHost());
        if (node.getTimeout() != null) {
            printer.printKeyValue("timeout", node.getTimeout().toString());
        }
        return printer;
    }

    public YamlPrinter visit(YTRelationshipType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("valid_target_types", node.getValidTargetTypes())
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public YamlPrinter visit(YTInterfaceDefinition node, Parameter parameter) {
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

    public YamlPrinter visit(YTNodeType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(printListMap("requirements", node.getRequirements().stream().map(YTMapRequirementDefinition::getMap).collect(Collectors.toList()), parameter))
            .print(printMap("capabilities", node.getCapabilities(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter))
            .print(printMap("artifacts", node.getArtifacts(), parameter));
    }

    public YamlPrinter visit(YTRequirementDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("capability", node.getCapability())
            .printKeyValue("node", node.getNode())
            .print(printVisitorNode(node.getRelationship(), new Parameter(parameter.getIndent()).addContext("relationship")))
            .printKeyValue("occurrences", node.getOccurrences())
            .printKeyValue("description", node.getDescription());
    }

    public YamlPrinter visit(YTRelationshipDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            // Removed to support short notations
            // .printKeyValue("type", node.getType())
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public YamlPrinter visit(YTCapabilityDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printKeyValue("occurrences", node.getOccurrences())
            .printKeyValue("valid_source_types", node.getValidSourceTypes())
            .printKeyValue("type", node.getType())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter));
    }

    public YamlPrinter visit(YTArtifactDefinition node, Parameter parameter) {
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

    public YamlPrinter visit(YTGroupType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("members", node.getMembers());
    }

    public YamlPrinter visit(YTPolicyType node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("targets", node.getTargets())
            .printKeyObject("triggers", node.getTriggers());
    }

    public YamlPrinter visit(YTVersion node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(node.getVersion());
    }

    public YamlPrinter visit(YTNodeTemplate node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(node.getMetadata().accept(this, parameter))
            .printKeyValue("directives", node.getDirectives())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter))
            .print(printListMap("requirements", node.getRequirements().stream().map(YTMapRequirementAssignment::getMap).collect(Collectors.toList()), parameter))
            .print(printMap("capabilities", node.getCapabilities(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter))
            .print(printMap("artifacts", node.getArtifacts(), parameter))
            .print(printVisitorNode(node.getNodeFilter(), parameter))
            .printKeyValue("copy", node.getCopy());
    }

    public YamlPrinter visit(YTGroupDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(node.getMetadata().accept(this, parameter))
            .print(printMap("properties", node.getProperties(), parameter))
            .printKeyValue("members", node.getMembers());
    }

    public YamlPrinter visit(YTPolicyDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(node.getMetadata().accept(this, parameter))
            .print(printMap("properties", node.getProperties(), parameter))
            .printKeyValue("targets", node.getTargets());
    }

    public YamlPrinter visit(YTPropertyAssignment node, Parameter parameter) {
        // nested assignments are implemented by calling #printMap for Map values that are not property functions
        YamlPrinter printer = new YamlPrinter(parameter.getIndent());
        if (node.getValue() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, YTPropertyAssignment> value = (Map<String, YTPropertyAssignment>) node.getValue();
            // special casing for property functions to always be a single-line map value
            if (value.size() == 1 && Arrays.stream(PROPERTY_FUNCTIONS).anyMatch(value::containsKey)) {
                String key = value.keySet().iterator().next();
                final Object rawFunctionArg = value.get(key).getValue();
                final String functionArg;
                if (rawFunctionArg instanceof List) {
                    final String list = ((List<?>) rawFunctionArg).stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));
                    // get_operation_output value is not in brackets, compare Section 4.6.1 of YAML-Standard 1.3
                    if (key.equals("get_operation_output")) {
                        functionArg = list;
                    } else {
                        functionArg = "[ " + list + " ]";
                    }
                } else if (rawFunctionArg instanceof YTPropertyAssignment) {
                    functionArg = visit((YTPropertyAssignment) rawFunctionArg, new Parameter(0)).toString();
                } else if (rawFunctionArg instanceof String) {
                    functionArg = (String) rawFunctionArg;
                } else {
                    // TODO
                    LOGGER.warn("Unexpected value type [{}] in property function definition", rawFunctionArg.getClass().getName());
                    functionArg = "";
                }
                printer.print(parameter.getKey()).print(":")
                    .print(" { ")
                    .print(key).print(": ").print(functionArg)
                    .print(" }").printNewLine();
            } else if (value.isEmpty()) {
                // printMap would skip an empty map
                printer.printKeyValue(parameter.getKey(), "{}");
            } else {
                printer.print(printMap(parameter.getKey(), value, parameter));
            }
        } else if (node.getValue() instanceof List) {
            if (((List<?>) node.getValue()).isEmpty()) {
                // printList would skip an empty list
                printer.printKeyValue(parameter.getKey(), "[]");
            } else {
                printer.print(printList(parameter.getKey(), (List<?>) node.getValue(), parameter));
            }
        } else {
            // printKeyObject skips null and empty values, which is a reasonable default
            // therefore we serialize null values ourselves here
            final String value = Objects.toString(node.getValue());
            final boolean isStringValue = node.getValue() instanceof String;
            printer.printKeyValue(parameter.getKey(), value, isStringValue, false);
        }
        return printer;
    }

    public YamlPrinter visit(YTAttributeAssignment node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("description", node.getDescription())
            .printYamlValue(parameter.getKey(), node.getValue(), true);
    }

    public YamlPrinter visit(YTInterfaceAssignment node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent());
    }

    public YamlPrinter visit(YTParameterDefinition node, Parameter parameter) {
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

    public YamlPrinter visit(YTConstraintClause node, Parameter parameter) {
        if (node.getValue() != null) {
            return new YamlPrinter(parameter.getIndent())
                .printKeyValue(node.getKey(), node.getValue());
        } else if (node.getList() != null) {
            return new YamlPrinter(parameter.getIndent())
                .printKeyListObjectInline(node.getKey(), new ArrayList<>(node.getList()));
        }
        return null;
    }

    public YamlPrinter visit(YTCapabilityAssignment node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter));
    }

    public YamlPrinter visit(YTNodeFilterDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(printListMap("properties",
                node.getProperties().stream().map(YTMapPropertyFilterDefinition::getMap).collect(Collectors.toList()),
                parameter)
            )
            .print(printListMap("capabilities",
                node.getCapabilities().stream().map(YTMapObject::getMap).collect(Collectors.toList()),
                parameter)
            );
    }

    public YamlPrinter visit(YTRelationshipTemplate node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("type", node.getType())
            .printKeyValue("description", node.getDescription())
            .print(node.getMetadata().accept(this, parameter))
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("attributes", node.getAttributes(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter))
            .printKeyValue("copy", node.getCopy());
    }

    public YamlPrinter visit(YTSubstitutionMappings node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("node_type", node.getNodeType())
            .print(printMapInlineStringList("capabilities", node.getCapabilities(), parameter));
    }

    public YamlPrinter visit(YTRequirementAssignment node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .printKeyValue("node", node.getNode())
            .print(printVisitorNode(node.getRelationship(), new Parameter(parameter.getIndent()).addContext("relationship")))
            .printKeyValue("capability", node.getCapability())
            .print(printVisitorNode(node.getNodeFilter(), new Parameter(parameter.getIndent()).addContext("node_filter")))
            .printKeyValue("occurrences", node.getOccurrences());
    }

    public YamlPrinter visit(YTRelationshipAssignment node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            // Removed to support short notations
            // .printKeyValue("type", node.getType())
            .print(printMap("properties", node.getProperties(), parameter))
            .print(printMap("interfaces", node.getInterfaces(), parameter));
    }

    public YamlPrinter visit(YTPropertyFilterDefinition node, Parameter parameter) {
        return new YamlPrinter(parameter.getIndent())
            .print(printList("constraints", node.getConstraints(), parameter));
    }

    public YamlPrinter printVisitorNode(VisitorNode node, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent());
        if (Objects.nonNull(node)) {
            if (node instanceof YTPropertyAssignment) {
                printer.print(node.accept(this,
                    new Parameter(parameter.getIndent()).addContext(parameter.getKey())
                ));
            } else if (node instanceof YTRelationshipAssignment) {
                printer.print(parameter.getKey() + ": ").printQName(((YTRelationshipAssignment) node).getType())
                    .print(node.accept(this, new Parameter(parameter.getIndent() + INDENT_SIZE)));
            } else if (node instanceof YTRelationshipDefinition) {
                printer.print(parameter.getKey() + ": ").printQName(((YTRelationshipDefinition) node).getType())
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
            printer.printKey(keyValue);
            printer.indent(INDENT_SIZE);
            list.stream()
                // transform items to yaml-strings without indentation
                // since indentation is done in printListObject
                .map(entry -> ((VisitorNode) entry).accept(this, new Parameter(0)))
                .reduce(printer, YamlPrinter::printListObject);
            printer.indent(-INDENT_SIZE);
        }
        return printer;
    }

    private <T extends VisitorNode> YamlPrinter printMap(String keyValue, Map<String, T> map, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent());
        if (map == null || map.isEmpty()) {
            return printer;
        }
        if (!keyValue.isEmpty()) {
            printer.printKey(keyValue);
        }
        printer.print(map.entrySet().stream()
            .map((entry) -> {
                    YamlPrinter p = new YamlPrinter(parameter.getIndent() + INDENT_SIZE)
                        .print(
                            printVisitorNode(entry.getValue(),
                                new Parameter(parameter.getIndent() + INDENT_SIZE).addContext(entry.getKey())
                            )
                        );
                    return p;
                }
            )
            .reduce(YamlPrinter::print)
        );
        return printer;
    }

    private <T> YamlPrinter printListMap(String keyValue, List<Map<String, T>> list, Parameter parameter) {
        YamlPrinter printer = new YamlPrinter(parameter.getIndent());
        if (list != null && !list.isEmpty()) {
            printer.printKey(keyValue)
                .print(list.stream()
                    .flatMap(map -> map.entrySet().stream())
                    .map(
                        (entry) -> new YamlPrinter(parameter.getIndent() + INDENT_SIZE)
                            .printListKey(entry.getKey())
                            // FIXME replace <T> with <T extends VisitorNode> to enforce type at compile time
                            //  this also necessitates adjusting the yaml-model to contain that type information
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

    private YamlPrinter printMapInlineStringList(String keyValue, Map<String, YTListString> map, Parameter parameter) {
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

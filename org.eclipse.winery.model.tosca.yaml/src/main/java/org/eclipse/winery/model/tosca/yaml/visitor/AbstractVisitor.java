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
package org.eclipse.winery.model.tosca.yaml.visitor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

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
import org.eclipse.winery.model.tosca.yaml.TSubstitutionMappings;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.TVersion;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.model.tosca.yaml.support.TMapPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.support.TMapRequirementDefinition;

import org.eclipse.jdt.annotation.NonNull;

public abstract class AbstractVisitor<R extends AbstractResult<R>, P extends AbstractParameter<P>> implements IVisitor<R, P> {
    public R concatAndReduce(Stream<R>... streams) {
        return Stream.of(streams)
            .reduce(Stream.empty(), Stream::concat)
            .filter(Objects::nonNull)
            .reduce(this::addR)
            .orElse(null);
    }

    @Override
    public R visit(TArtifactDefinition node, P parameter) {
        return null;
    }

    @Override
    public R visit(TArtifactType node, P parameter) {
        return null;
    }

    @Override
    public R visit(TAttributeAssignment node, P parameter) {
        return null;
    }

    @Override
    public R visit(TAttributeDefinition node, P parameter) {
        return Stream.of(node.getEntrySchema())
            .filter(Objects::nonNull)
            .map(entry -> entry.accept(this, parameter.copy().addContext("entry_schema")))
            .filter(Objects::nonNull)
            .reduce(this::addR)
            .orElse(null);
    }

    @Override
    public R visit(TCapabilityAssignment node, P parameter) {
        R result = visitPropertyAssignment(node.getProperties(), parameter);
        return addR(result, visitAttributeAssignment(node.getAttributes(), parameter));
    }

    @Override
    public R visit(TCapabilityDefinition node, P parameter) {
        R result = visitPropertyDefinition(node.getProperties(), parameter);
        return addR(result, visitAttributeDefinition(node.getAttributes(), parameter));
    }

    @Override
    public R visit(TCapabilityType node, P parameter) {
        return null;
    }

    @Override
    public R visit(TConstraintClause node, P parameter) {
        return null;
    }

    @Override
    public R visit(TDataType node, P parameter) {
        return node.getConstraints().stream()
            .filter(Objects::nonNull)
            .map(entry -> entry.accept(this, parameter.copy().addContext("constraints")))
            .filter(Objects::nonNull)
            .reduce(this::addR)
            .orElse(null);
    }

    @Override
    public R visit(TEntityType node, P parameter) {
        return concatAndReduce(
            Stream.of(node.getVersion())
                .filter(Objects::nonNull)
                .map(entry -> entry.accept(this, parameter.copy().addContext("version"))),
            Stream.of(visitPropertyDefinition(node.getProperties(), parameter)),
            Stream.of(visitAttributeDefinition(node.getAttributes(), parameter)),
            Stream.of(node.getMetadata())
                .map(entry -> entry.accept(this, parameter.copy().addContext("metadata")))
        );
    }

    @Override
    public R visit(TEntrySchema node, P parameter) {
        R result = null;
        for (TConstraintClause entry : node.getConstraints()) {
            result = addR(result, entry.accept(this, parameter.copy().addContext("constraints")));
        }
        return result;
    }

    @Override
    public R visit(TGroupDefinition node, P parameter) {
        R result = node.getMetadata().accept(this, parameter.copy().addContext("metadata"));
        result = addR(result, visitPropertyAssignment(node.getProperties(), parameter));
        result = addR(result, visitInterfaceDefinition(node.getInterfaces(), parameter));
        return result;
    }

    @Override
    public R visit(TGroupType node, P parameter) {
        R result = visitRequirementDefinition(node.getRequirements(), parameter);
        result = addR(result, visitCapabilityDefinition(node.getCapabilities(), parameter));
        result = addR(result, visitInterfaceDefinition(node.getInterfaces(), parameter));
        return result;
    }

    @Override
    public R visit(TImplementation node, P parameter) {
        return null;
    }

    @Override
    public R visit(TImportDefinition node, P parameter) {
        return null;
    }

    @Override
    public R visit(TInterfaceAssignment node, P parameter) {
        return null;
    }

    @Override
    public R visit(TInterfaceDefinition node, P parameter) {
        R result = visitPropertyAssignmentOrDefinition(node.getInputs(), parameter);
        result = addR(result, visitOperationDefinition(node.getOperations(), parameter));
        return result;
    }

    @Override
    public R visit(TInterfaceType node, P parameter) {
        R result = null;
        for (Map.Entry<String, TPropertyDefinition> entry : node.getInputs().entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("inputs", entry.getKey())));
            }
        }
        result = addR(result, visitOperationDefinition(node.getOperations(), parameter));
        return result;
    }

    @Override
    public R visit(TNodeFilterDefinition node, P parameter) {
        R result = null;
        for (TMapPropertyFilterDefinition map : node.getProperties()) {
            for (Map.Entry<String, TPropertyFilterDefinition> entry : map.entrySet()) {
                if (entry.getValue() != null) {
                    result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("properties", entry.getKey())));
                }
            }
        }
        return result;
    }

    @Override
    public R visit(TNodeTemplate node, P parameter) {
        R result = node.getMetadata().accept(this, parameter.copy().addContext("metadata"));
        result = addR(result, visitPropertyAssignment(node.getProperties(), parameter));
        result = addR(result, visitAttributeAssignment(node.getAttributes(), parameter));
        for (TMapRequirementAssignment map : node.getRequirements()) {
            for (Map.Entry<String, TRequirementAssignment> entry : map.entrySet()) {
                if (entry.getValue() != null) {
                    result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("requirements", entry.getKey())));
                }
            }
        }
        for (Map.Entry<String, TCapabilityAssignment> entry : node.getCapabilities().entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("capabilities", entry.getKey())));
            }
        }
        result = addR(result, visitInterfaceDefinition(node.getInterfaces(), parameter));
        result = addR(result, visitArtifactDefinition(node.getArtifacts(), parameter));
        if (node.getNodeFilter() != null) {
            result = addR(result, node.getNodeFilter().accept(this, parameter.copy().addContext("node_filter")));
        }
        return result;
    }

    @Override
    public R visit(TNodeType node, P parameter) {
        R result = visitAttributeDefinition(node.getAttributes(), parameter);
        result = addR(result, visitRequirementDefinition(node.getRequirements(), parameter));
        result = addR(result, visitInterfaceDefinition(node.getInterfaces(), parameter));
        result = addR(result, visitArtifactDefinition(node.getArtifacts(), parameter));
        return result;
    }

    @Override
    public R visit(TOperationDefinition node, P parameter) {
        R result = visitPropertyAssignmentOrDefinition(node.getInputs(), parameter);
        if (node.getImplementation() != null) {
            result = addR(result, node.getImplementation().accept(this, parameter.copy().addContext("implementation")));
        }
        return result;
    }

    @Override
    public R visit(TParameterDefinition node, P parameter) {
        return null;
    }

    @Override
    public R visit(TPolicyDefinition node, P parameter) {
        R result = node.getMetadata().accept(this, parameter.copy().addContext("metadata"));
        return addR(result, visitPropertyAssignment(node.getProperties(), parameter));
    }

    @Override
    public R visit(TPolicyType node, P parameter) {
        return null;
    }

    @Override
    public R visit(TPropertyAssignment node, P parameter) {
        return null;
    }

    @Override
    public R visit(TPropertyDefinition node, P parameter) {
        R result = null;
        for (TConstraintClause entry : node.getConstraints()) {
            result = addR(result, entry.accept(this, parameter.copy().addContext("constraints")));
        }
        if (node.getEntrySchema() != null) {
            result = addR(result, node.getEntrySchema().accept(this, parameter.copy().addContext("entry_schema")));
        }
        return result;
    }

    @Override
    public R visit(TPropertyFilterDefinition node, P parameter) {
        R result = null;
        for (TConstraintClause entry : node.getConstraints()) {
            result = addR(result, entry.accept(this, parameter.copy().addContext("constraints")));
        }
        return result;
    }

    @Override
    public R visit(TRelationshipAssignment node, P parameter) {
        R result = visitPropertyAssignment(node.getProperties(), parameter);
        for (Map.Entry<String, TInterfaceAssignment> entry : node.getInterfaces().entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("interfaces", entry.getKey())));
            }
        }
        return result;
    }

    @Override
    public R visit(TRelationshipDefinition node, P parameter) {
        return visitInterfaceDefinition(node.getInterfaces(), parameter);
    }

    @Override
    public R visit(TRelationshipTemplate node, P parameter) {
        R result = node.getMetadata().accept(this, parameter.copy().addContext("metadata"));
        result = addR(result, visitPropertyAssignment(node.getProperties(), parameter));
        result = addR(result, visitAttributeAssignment(node.getAttributes(), parameter));
        return addR(result, visitInterfaceDefinition(node.getInterfaces(), parameter));
    }

    @Override
    public R visit(TRelationshipType node, P parameter) {
        return visitInterfaceDefinition(node.getInterfaces(), parameter);
    }

    @Override
    public R visit(TRepositoryDefinition node, P parameter) {
        return null;
    }

    @Override
    public R visit(TRequirementAssignment node, P parameter) {
        R result = null;
        if (node.getRelationship() != null) {
            result = node.getRelationship().accept(this, parameter.copy().addContext("relationship"));
        }
        if (node.getNodeFilter() != null) {
            result = addR(result, node.getNodeFilter().accept(this, parameter.copy().addContext("node_filter")));
        }
        return result;
    }

    @Override
    public R visit(TRequirementDefinition node, P parameter) {
        R result = null;
        if (node.getRelationship() != null) {
            result = node.getRelationship().accept(this, parameter.copy().addContext("relationship"));
        }
        return result;
    }

    @Override
    public R visit(TServiceTemplate node, P parameter) {
        return concatAndReduce(
            Stream.of(node.getMetadata().accept(this, parameter.copy().addContext("metadata"))),
            node.getRepositories().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("repositories", entry.getKey()))),
            node.getImports().stream().flatMap(entry -> entry.getMap().entrySet().stream())
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("imports", entry.getKey()))),
            node.getArtifactTypes().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("artifact_types", entry.getKey()))),
            node.getDataTypes().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("data_types", entry.getKey()))),
            node.getCapabilityTypes().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("capability_types", entry.getKey()))),
            node.getInterfaceTypes().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("interface_types", entry.getKey()))),
            node.getRelationshipTypes().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("relationship_types", entry.getKey()))),
            node.getNodeTypes().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("node_types", entry.getKey()))),
            node.getGroupTypes().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("group_types", entry.getKey()))),
            node.getPolicyTypes().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("policy_types", entry.getKey()))),
            Stream.of(node.getTopologyTemplate())
                .filter(Objects::nonNull)
                .map(entry -> entry.accept(this, parameter.copy().addContext("topology_template")))
        );
    }

    @Override
    public R visit(TSubstitutionMappings node, P parameter) {
        return null;
    }

    @Override
    public R visit(TTopologyTemplateDefinition node, P parameter) {
        return concatAndReduce(
            node.getInputs().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("inputs", entry.getKey()))),
            node.getNodeTemplates().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("node_templates", entry.getKey()))),
            node.getRelationshipTemplates().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("relationship_templates", entry.getKey()))),
            node.getGroups().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("groups", entry.getKey()))),
            node.getPolicies().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("policies", entry.getKey()))),
            node.getOutputs().entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> entry.getValue().accept(this, parameter.copy().addContext("outputs", entry.getKey()))),
            Stream.of(node.getSubstitutionMappings())
                .filter(Objects::nonNull)
                .map(entry -> entry.accept(this, parameter.copy().addContext("substitution_mappings")))
        );
    }

    @Override
    public R visit(TVersion node, P parameter) {
        return null;
    }

    @Override
    public R visit(Metadata node, P parameter) {
        return null;
    }

    private R visitPropertyDefinition(@NonNull Map<String, TPropertyDefinition> properties, P parameter) {
        R result = null;
        for (Map.Entry<String, TPropertyDefinition> entry : properties.entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("properties", entry.getKey())));
            }
        }
        return result;
    }

    public R visitPropertyAssignment(@NonNull Map<String, TPropertyAssignment> properties, P parameter) {
        R result = null;
        for (Map.Entry<String, TPropertyAssignment> entry : properties.entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("properties", entry.getKey())));
            }
        }
        return result;
    }

    private R visitAttributeDefinition(@NonNull Map<String, TAttributeDefinition> attributes, P parameter) {
        R result = null;
        for (Map.Entry<String, TAttributeDefinition> entry : attributes.entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("attributes", entry.getKey())));
            }
        }
        return result;
    }

    public R visitAttributeAssignment(@NonNull Map<String, TAttributeAssignment> attributes, P parameter) {
        R result = null;
        for (Map.Entry<String, TAttributeAssignment> entry : attributes.entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("attributes", entry.getKey())));
            }
        }
        return result;
    }

    private R visitInterfaceDefinition(@NonNull Map<String, TInterfaceDefinition> interfaces, P parameter) {
        R result = null;
        for (Map.Entry<String, TInterfaceDefinition> entry : interfaces.entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("interfaces", entry.getKey())));
            }
        }
        return result;
    }

    private R visitRequirementDefinition(@NonNull List<TMapRequirementDefinition> requirements, P parameter) {
        R result = null;
        for (TMapRequirementDefinition map : requirements) {
            for (Map.Entry<String, TRequirementDefinition> entry : map.entrySet()) {
                if (entry.getValue() != null) {
                    result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("requirements", entry.getKey())));
                }
            }
        }
        return result;
    }

    private R visitCapabilityDefinition(@NonNull Map<String, TCapabilityDefinition> capabilities, P parameter) {
        R result = null;
        for (Map.Entry<String, TCapabilityDefinition> entry : capabilities.entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("capabilities", entry.getKey())));
            }
        }
        return result;
    }

    private R visitPropertyAssignmentOrDefinition(@NonNull Map<String, TPropertyAssignmentOrDefinition> inputs, P parameter) {
        R result = null;
        for (Map.Entry<String, TPropertyAssignmentOrDefinition> entry : inputs.entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("inputs", entry.getKey())));
            }
        }
        return result;
    }

    private R visitOperationDefinition(@NonNull Map<String, TOperationDefinition> operations, P parameter) {
        R result = null;
        for (Map.Entry<String, TOperationDefinition> entry : operations.entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("operations", entry.getKey())));
            }
        }
        return result;
    }

    private R visitArtifactDefinition(@NonNull Map<String, TArtifactDefinition> artifacts, P parameter) {
        R result = null;
        for (Map.Entry<String, TArtifactDefinition> entry : artifacts.entrySet()) {
            if (entry.getValue() != null) {
                result = addR(result, entry.getValue().accept(this, parameter.copy().addContext("artifacts", entry.getKey())));
            }
        }
        return result;
    }

    private R addR(R r1, R r2) {
        if (r1 == null) {
            return r2;
        }
        return r1.add(r2);
    }
}

/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.tosca.yaml.visitor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

import org.eclipse.jdt.annotation.NonNull;

public abstract class AbstractVisitor<R extends AbstractResult<R>, P extends AbstractParameter<P>> implements IVisitor<R, P> {
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
        return visitElement(node.getEntrySchema(), parameter, "entry_schema");
    }

    @Override
    public R visit(TCapabilityAssignment node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getProperties(), parameter, "properties"),
            visitElement(node.getAttributes(), parameter, "attributes")
        ));
    }

    @Override
    public R visit(TCapabilityDefinition node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getProperties(), parameter, "properties"),
            visitElement(node.getAttributes(), parameter, "attributes")
        ));
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
        return visitElement(node.getConstraints(), parameter, "constraints");
    }

    @Override
    public R visit(TEntityType node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getVersion(), parameter, "version"),
            visitElement(node.getProperties(), parameter, "properties"),
            visitElement(node.getAttributes(), parameter, "attributes"),
            visitElement(node.getMetadata(), parameter, "metadata")
        ));
    }

    @Override
    public R visit(TEntrySchema node, P parameter) {
        return visitElement(node.getConstraints(), parameter, "constraints");
    }

    @Override
    public R visit(TGroupDefinition node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getMetadata(), parameter, "metadata"),
            visitElement(node.getProperties(), parameter, "properties"),
            visitElement(node.getInterfaces(), parameter, "interfaces")
        ));
    }

    @Override
    public R visit(TGroupType node, P parameter) {
        return reduce(Stream.of(
            visitMapElement(node.getRequirements(), parameter, "requirements"),
            visitElement(node.getCapabilities(), parameter, "capabilities"),
            visitElement(node.getInterfaces(), parameter, "interfaces")
        ));
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
        return reduce(Stream.of(
            visitElement(node.getInputs(), parameter, "inputs"),
            visitElement(node.getOperations(), parameter, "operations")
        ));
    }

    @Override
    public R visit(TInterfaceType node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getInputs(), parameter, "inputs"),
            visitElement(node.getOperations(), parameter, "operations")
        ));
    }

    @Override
    public R visit(TNodeFilterDefinition node, P parameter) {
        return visitMapElement(node.getProperties(), parameter, "properties");
    }

    @Override
    public R visit(TNodeTemplate node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getMetadata(), parameter, "metadata"),
            visitElement(node.getProperties(), parameter, "properties"),
            visitElement(node.getAttributes(), parameter, "attributes"),
            visitMapElement(node.getRequirements(), parameter, "requirements"),
            visitElement(node.getCapabilities(), parameter, "capabilities"),
            visitElement(node.getArtifacts(), parameter, "artifacts"),
            visitElement(node.getInterfaces(), parameter, "interfaces"),
            visitElement(node.getNodeFilter(), parameter, "node_filter")
        ));
    }

    @Override
    public R visit(TNodeType node, P parameter) {
        return reduce(Stream.of(
            visitMapElement(node.getRequirements(), parameter, "requirements"),
            visitElement(node.getInterfaces(), parameter, "interfaces"),
            visitElement(node.getArtifacts(), parameter, "artifacts")
        ));
    }

    @Override
    public R visit(TOperationDefinition node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getInputs(), parameter, "inputs"),
            visitElement(node.getImplementation(), parameter, "implementation")
        ));
    }

    @Override
    public R visit(TParameterDefinition node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getConstraints(), parameter, "constraints"),
            visitElement(node.getEntrySchema(), parameter, "entry_schema")
        ));
    }

    @Override
    public R visit(TPolicyDefinition node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getMetadata(), parameter, "metadata"),
            visitElement(node.getProperties(), parameter, "properties")
        ));
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
        return reduce(Stream.of(
            visitElement(node.getConstraints(), parameter, "constraints"),
            visitElement(node.getEntrySchema(), parameter, "entry_schema")
        ));
    }

    @Override
    public R visit(TPropertyFilterDefinition node, P parameter) {
        return visitElement(node.getConstraints(), parameter, "constraints");
    }

    @Override
    public R visit(TRelationshipAssignment node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getProperties(), parameter, "properties"),
            visitElement(node.getInterfaces(), parameter, "interfaces")
        ));
    }

    @Override
    public R visit(TRelationshipDefinition node, P parameter) {
        return visitElement(node.getInterfaces(), parameter, "interfaces");
    }

    @Override
    public R visit(TRelationshipTemplate node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getMetadata(), parameter, "metadata"),
            visitElement(node.getProperties(), parameter, "properties"),
            visitElement(node.getAttributes(), parameter, "attributes"),
            visitElement(node.getInterfaces(), parameter, "interfaces")
        ));
    }

    @Override
    public R visit(TRelationshipType node, P parameter) {
        return visitElement(node.getInterfaces(), parameter, "interfaces");
    }

    @Override
    public R visit(TRepositoryDefinition node, P parameter) {
        return null;
    }

    @Override
    public R visit(TRequirementAssignment node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getRelationship(), parameter, "relationship"),
            visitElement(node.getNodeFilter(), parameter, "node_filter")
        ));
    }

    @Override
    public R visit(TRequirementDefinition node, P parameter) {
        return visitElement(node.getRelationship(), parameter, "constraints");
    }

    @Override
    public R visit(TServiceTemplate node, P parameter) {
        return reduce(Stream.of(
            node.getMetadata().accept(this, parameter.copy().addContext("metadata")),
            visitElement(node.getRepositories(), parameter, "repositories"),
            visitMapElement(node.getImports(), parameter, "imports"),
            visitElement(node.getArtifactTypes(), parameter, "artifact_types"),
            visitElement(node.getDataTypes(), parameter, "data_types"),
            visitElement(node.getCapabilityTypes(), parameter, "capability_types"),
            visitElement(node.getInterfaceTypes(), parameter, "interface_types"),
            visitElement(node.getRelationshipTypes(), parameter, "relationship_types"),
            visitElement(node.getNodeTypes(), parameter, "node_types"),
            visitElement(node.getGroupTypes(), parameter, "group_types"),
            visitElement(node.getPolicyTypes(), parameter, "policy_types"),
            visitElement(node.getTopologyTemplate(), parameter, "topology_template")
        ));
    }

    @Override
    public R visit(TSubstitutionMappings node, P parameter) {
        return null;
    }

    @Override
    public R visit(TTopologyTemplateDefinition node, P parameter) {
        return reduce(Stream.of(
            visitElement(node.getInputs(), parameter, "inputs"),
            visitElement(node.getNodeTemplates(), parameter, "node_templates"),
            visitElement(node.getRelationshipTemplates(), parameter, "relationship_templates"),
            visitElement(node.getGroups(), parameter, "groups"),
            visitElement(node.getPolicies(), parameter, "policies"),
            visitElement(node.getOutputs(), parameter, "outputs"),
            visitElement(node.getSubstitutionMappings(), parameter, "substitution_mappings")
        ));
    }

    @Override
    public R visit(TVersion node, P parameter) {
        return null;
    }

    @Override
    public R visit(Metadata node, P parameter) {
        return null;
    }

    private R visitMapElement(@NonNull List<? extends Map<String, ? extends VisitorNode>> list, P parameter, String name) {
        return list.stream()
            .filter(Objects::nonNull)
            .flatMap(map -> map.entrySet().stream())
            .filter(this::nonNull)
            .map(entry -> entry.getValue().accept(this, parameter.copy().addContext(name, entry.getKey())))
            .filter(Objects::nonNull)
            .reduce(this::addR).orElse(null);
    }

    private R visitElement(Metadata node, P parameter, String name) {
        return Optional.ofNullable(node)
            .map(entry -> entry.accept(this, parameter.copy().addContext(name)))
            .orElse(null);
    }

    private R visitElement(VisitorNode node, P parameter, String name) {
        return Optional.ofNullable(node)
            .map(entry -> entry.accept(this, parameter.copy().addContext(name)))
            .orElse(null);
    }

    private R visitElement(@NonNull List<? extends VisitorNode> list, P parameter, String name) {
        return list.stream()
            .filter(Objects::nonNull)
            .map(entry -> entry.accept(this, parameter.copy().addContext(name)))
            .filter(Objects::nonNull)
            .reduce(this::addR)
            .orElse(null);
    }

    private R visitElement(@NonNull Map<String, ? extends VisitorNode> map, P parameter, String name) {
        return map.entrySet().stream()
            .filter(this::nonNull)
            .map(entry -> entry.getValue().accept(this, parameter.copy().addContext(name, entry.getKey())))
            .filter(Objects::nonNull)
            .reduce(this::addR).orElse(null);
    }

    private <K> Boolean nonNull(Map.Entry<String, K> entry) {
        return Objects.nonNull(entry) && Objects.nonNull(entry.getKey()) && Objects.nonNull(entry.getValue());
    }

    private R reduce(Stream<R> stream) {
        return stream
            .filter(Objects::nonNull)
            .reduce(this::addR)
            .orElse(null);
    }

    private R addR(R r1, R r2) {
        if (Objects.isNull(r1)) {
            return r2;
        }
        return r1.add(r2);
    }
}

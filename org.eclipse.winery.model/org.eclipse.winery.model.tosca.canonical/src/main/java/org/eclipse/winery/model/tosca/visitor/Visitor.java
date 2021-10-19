/*******************************************************************************
 * Copyright (c) 2019-2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.visitor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.RelationshipSourceOrTarget;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TCapability;
import org.eclipse.winery.model.tosca.TCapabilityRef;
import org.eclipse.winery.model.tosca.TCondition;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityTypeImplementation;
import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPropertyConstraint;
import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementRef;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Visitor with a default visit order to go from top of a definitions to the bottom. No follow up across TDefinitions.
 * In other words: The nesting is followed.
 *
 * In general, for all elements in the hierarchy, a visit method is implemented. This visit method visits all children
 * (in the TOSCA graph metamodel). In case an element in the hierarchy has no children in the TOSCA graph metamodel
 * and no children in the inheritance hierarchy, it is omitted.
 *
 * This class intentionally defines all default methods not as abstract to keep the children simple and to avoid
 * unnecessary lines of code.
 *
 * For more information on options on implementing visitors see
 * <a href="https://dspace.library.uu.nl/handle/1874/2558">Bravenboer, M., & Visser, E. (2001). Guiding visitors:
 * Separating navigation from computation.</a>
 *
 * TODO: Implement it for all DefinitionsChildren (NodeType, NodeTypeImplementation, ...)
 */
@SuppressWarnings("unused")
public abstract class Visitor {

    public void visit(TServiceTemplate serviceTemplate) {
        Objects.requireNonNull(serviceTemplate);
        visit((TExtensibleElements) serviceTemplate);

        final TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        if (topologyTemplate != null) {
            topologyTemplate.accept(this);
        }

        final List<TTag> tags = serviceTemplate.getTags();
        if (tags != null) {
            for (TTag tag : tags) {
                tag.accept(this);
            }
        }

        final List<TPlan> plans = serviceTemplate.getPlans();
        if (plans != null) {
            for (TPlan plan : plans) {
                plan.accept(this);
            }
        }

        final TBoundaryDefinitions boundaryDefinitions = serviceTemplate.getBoundaryDefinitions();
        if (boundaryDefinitions != null) {
            boundaryDefinitions.accept(this);
        }
    }

    public void visit(TPlan plan) {
        Objects.requireNonNull(plan);
        visit((TExtensibleElements) plan);

        final TCondition precondition = plan.getPrecondition();
        if (precondition != null) {
            precondition.accept(this);
        }
        if (plan.getInputParameters() != null) {
            for (TParameter parameter : plan.getInputParameters()) {
                parameter.accept(this);
            }
        }
        if (plan.getOutputParameters() != null) {
            for (TParameter parameter : plan.getOutputParameters()) {
                parameter.accept(this);
            }
        }
    }

    public void visit(TTopologyTemplate topologyTemplate) {
        Objects.requireNonNull(topologyTemplate);
        visit((TExtensibleElements) topologyTemplate);

        for (TNodeTemplate nodeTemplate : topologyTemplate.getNodeTemplates()) {
            nodeTemplate.accept(this);
        }
        for (TRelationshipTemplate relationshipTemplate : topologyTemplate.getRelationshipTemplates()) {
            relationshipTemplate.accept(this);
        }
        // metamodel does not offer more children
    }

    public void visit(TNodeTypeImplementation nodeTypeImplementation) {
        Objects.requireNonNull(nodeTypeImplementation);
        visit((TEntityTypeImplementation) nodeTypeImplementation);

        if (nodeTypeImplementation.getDeploymentArtifacts() != null) {
            for (TDeploymentArtifact da : nodeTypeImplementation.getDeploymentArtifacts()) {
                da.accept(this);
            }
        }
    }

    public void visit(TEntityTypeImplementation implementation) {
        Objects.requireNonNull(implementation);
        visit((TExtensibleElements) implementation);

        if (implementation.getImplementationArtifacts() != null) {
            for (TImplementationArtifact ia : implementation.getImplementationArtifacts()) {
                ia.accept(this);
            }
        }
    }

    public void visit(TExtensibleElements extensibleElement) {
        this.visitOtherAttributes(extensibleElement.getOtherAttributes());
        for (TDocumentation documentation : extensibleElement.getDocumentation()) {
            documentation.accept(this);
        }
        // metamodel does not offer more children
    }

    public void visit(TEntityType entityType) {
        Objects.requireNonNull(entityType);
        if (entityType.getProperties() instanceof TEntityType.YamlPropertiesDefinition) {
            ((TEntityType.YamlPropertiesDefinition) entityType.getProperties()).getProperties()
                .forEach(this::visit);
        }
        visit((TExtensibleElements) entityType);
    }

    public void visitOtherAttributes(Map<QName, String> otherAttributes) {
        // this is a leaf, so no action to take
    }

    public void visit(TEntityTemplate entityTemplate) {
        this.visit((HasId) entityTemplate);
        final TEntityTemplate.Properties properties = entityTemplate.getProperties();
        if (properties != null) {
            visit(properties);
        }
        final List<TPropertyConstraint> propertyConstraints = entityTemplate.getPropertyConstraints();
        if (propertyConstraints != null) {
            propertyConstraints.forEach(this::visit);
        }
        // metamodel does not offer more children
    }

    public void visit(TNodeTemplate nodeTemplate) {
        this.visit((RelationshipSourceOrTarget) nodeTemplate);
        final List<TRequirement> requirements = nodeTemplate.getRequirements();
        if (requirements != null) {
            requirements.forEach(requirement -> requirement.accept(this));
        }
        final List<TCapability> capabilities = nodeTemplate.getCapabilities();
        if (capabilities != null) {
            capabilities.forEach(capability -> capability.accept(this));
        }
        final List<TDeploymentArtifact> deploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
        if (deploymentArtifacts != null) {
            for (TDeploymentArtifact deploymentArtifact : deploymentArtifacts) {
                deploymentArtifact.accept(this);
            }
        }
        final List<TPolicy> policies = nodeTemplate.getPolicies();
        if (policies != null) {
            for (TPolicy policy : policies) {
                policy.accept(this);
            }
        }
        // metamodel does not offer more children
    }

    public void visit(TRelationshipTemplate relationshipTemplate) {
        this.visit((TEntityTemplate) relationshipTemplate);
        final TRelationshipTemplate.RelationshipConstraints relationshipConstraints = relationshipTemplate.getRelationshipConstraints();
        if (relationshipConstraints != null) {
            for (TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint relationshipConstraint : relationshipConstraints.getRelationshipConstraint()) {
                relationshipConstraint.accept(this);
            }
        }
        // metamodel does not offer more children
    }

    public void visit(TEntityTemplate.Properties properties) {
        // in all cases this is a leaf node, so no action to take
    }

    public void visit(List<TPropertyConstraint> propertyConstraints) {
        for (TPropertyConstraint propertyConstraint : propertyConstraints) {
            propertyConstraint.accept(this);
        }
        // metamodel does not offer more children
    }

    public void visit(TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint relationshipConstraint) {
        // this is a leaf, so no action to take
    }

    public void visit(TRequirement requirement) {
        this.visit((TEntityTemplate) requirement);
        // metamodel does not offer more children
    }

    public void accept(TTag tag) {
        // this is a leaf, so no action to take
    }

    public void accept(TCondition condition) {
        // this is a leaf, so no action to take
    }

    public void visit(TParameter parameter) {
        // this is a leaf, so no action to take
    }

    public void visit(@NonNull TBoundaryDefinitions boundaryDefinitions) {
        this.acceptBoundaryDefinitionsProperties(boundaryDefinitions);
        this.acceptBoundaryDefinitionsPropertyConstraints(boundaryDefinitions);
        this.acceptBoundaryDefinitionsPolicies(boundaryDefinitions);
        this.acceptBoundaryDefinitionsRequirements(boundaryDefinitions);
        this.acceptBoundaryDefinitionsCapabilities(boundaryDefinitions);
        this.acceptBoundaryDefinitionsInterfaces(boundaryDefinitions);
    }

    private void acceptBoundaryDefinitionsInterfaces(@NonNull TBoundaryDefinitions boundaryDefinitions) {
        final List<TExportedInterface> interfaces = boundaryDefinitions.getInterfaces();
        if (interfaces != null) {
            for (TExportedInterface exportedInterface : interfaces) {
                exportedInterface.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsCapabilities(@NonNull TBoundaryDefinitions boundaryDefinitions) {
        final List<TCapabilityRef> capabilities = boundaryDefinitions.getCapabilities();
        if (capabilities != null) {
            for (TCapabilityRef capabilityRef : capabilities) {
                capabilityRef.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsRequirements(@NonNull TBoundaryDefinitions boundaryDefinitions) {
        final List<TRequirementRef> requirements = boundaryDefinitions.getRequirements();
        if (requirements != null) {
            for (TRequirementRef requirementRef : requirements) {
                requirementRef.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsPolicies(@NonNull TBoundaryDefinitions boundaryDefinitions) {
        final List<TPolicy> policies = boundaryDefinitions.getPolicies();
        if (policies != null) {
            for (TPolicy policy : policies) {
                policy.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsPropertyConstraints(@NonNull TBoundaryDefinitions boundaryDefinitions) {
        final List<TPropertyConstraint> propertyConstraints = boundaryDefinitions.getPropertyConstraints();
        if (propertyConstraints != null) {
            for (TPropertyConstraint propertyConstraint : propertyConstraints) {
                propertyConstraint.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsProperties(@NonNull TBoundaryDefinitions boundaryDefinitions) {
        final TBoundaryDefinitions.Properties properties = boundaryDefinitions.getProperties();
        if (properties != null) {
            properties.accept(this);
        }
    }

    public void visit(TBoundaryDefinitions.Properties properties) {
        final List<TPropertyMapping> propertyMappings = properties.getPropertyMappings();
        if (propertyMappings != null) {
            for (TPropertyMapping propertyMapping : propertyMappings) {
                propertyMapping.accept(this);
            }
        }
    }

    public void visit(TPropertyMapping propertyMapping) {
        // this is a leaf, so no action to take
    }

    public void visit(TCapabilityRef capabilityRef) {
        // this is a leaf, so no action to take
    }

    public void visit(TRequirementRef requirementRef) {
        // this is a leaf, so no action to take
    }

    public void accept(TExportedInterface exportedInterface) {
        // this is a leaf, so no action to take
    }

    public void visit(TPropertyConstraint propertyConstraint) {
        // this is a leaf, so no action to take
    }

    public void visit(TDocumentation documentation) {
        // this is a leaf, so no action to take
    }

    public void visit(TEntityType.YamlPropertyDefinition propertiesDefinition) {
        // this is a leaf, so no action to take
    }

    public void visit(TDeploymentArtifact artifact) {
        // this is a leaf, so no action to take
    }

    public void visit(TImplementationArtifact artifact) {
        // this is a leaf, so no action to take
    }
}

/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

import java.util.LinkedHashMap;
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
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TImplementationArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TParameter;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPropertyConstraint;
import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TRequirementRef;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTag;
import org.eclipse.winery.model.tosca.TTags;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Visitor with a default visit order to go from top of a definitions to the bottom. No follow up across TDefinitions.
 * In other words: The nesting is followed.
 *
 * In general, for all elements in the hierarchy, a visit method is implemented. This visit method visits all children
 * (in the TOSCA graph meta model). In case an element in the hierarchy has no children in the TOSCA graph meta model
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
public abstract class Visitor {

    public void visit(TServiceTemplate serviceTemplate) {
        Objects.requireNonNull(serviceTemplate);
        visit((TExtensibleElements) serviceTemplate);

        final TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        if (topologyTemplate != null) {
            topologyTemplate.accept(this);
        }

        final TTags tags = serviceTemplate.getTags();
        if (tags != null) {
            for (TTag tag : tags.getTag()) {
                tag.accept(this);
            }
        }

        final TPlans plans = serviceTemplate.getPlans();
        if (plans != null) {
            for (TPlan plan : plans.getPlan()) {
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
        final TPlan.InputParameters inputParameters = plan.getInputParameters();
        if (inputParameters != null) {
            for (TParameter parameter : inputParameters.getInputParameter()) {
                parameter.accept(this);
            }
        }
        plan.getOutputParameters();
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
        // meta model does not offer more children
    }

    public void visit(TExtensibleElements extensibleElement) {
        this.visitOtherAttributes(extensibleElement.getOtherAttributes());
        for (TDocumentation documentation : extensibleElement.getDocumentation()) {
            documentation.accept(this);
        }
        // meta model does not offer more children
    }

    public void visit(TEntityType entityType) {
        Objects.requireNonNull(entityType);
        visit((TExtensibleElements) entityType);
        final List<TEntityType.PropertyDefinition> propertyDefinitions = entityType.getProperties();
        if (propertyDefinitions != null) {
            propertyDefinitions.forEach(this::visit);
        }
    }

    public void visitOtherAttributes(Map<QName, String> otherAttributes) {
        // this is a leaf, so no action to take
    }

    public void visit(TEntityTemplate entityTemplate) {
        this.visit((HasId) entityTemplate);
        final TEntityTemplate.Properties properties = entityTemplate.getProperties();
        if (properties != null) {
            properties.accept(this);
        }
        final TEntityTemplate.PropertyConstraints propertyConstraints = entityTemplate.getPropertyConstraints();
        if (propertyConstraints != null) {
            propertyConstraints.accept(this);
        }
        // meta model does not offer more children
    }

    public void visit(TNodeTemplate nodeTemplate) {
        this.visit((RelationshipSourceOrTarget) nodeTemplate);
        final TNodeTemplate.Requirements requirements = nodeTemplate.getRequirements();
        if (requirements != null) {
            requirements.accept(this);
        }
        final TNodeTemplate.Capabilities capabilities = nodeTemplate.getCapabilities();
        if (capabilities != null) {
            capabilities.accept(this);
        }
        final TDeploymentArtifacts deploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
        if (deploymentArtifacts != null) {
            for (TDeploymentArtifact deploymentArtifact : deploymentArtifacts.getDeploymentArtifact()) {
                deploymentArtifact.accept(this);
            }
        }
        final TPolicies policies = nodeTemplate.getPolicies();
        if (policies != null) {
            for (TPolicy policy : policies.getPolicy()) {
                policy.accept(this);
            }
        }
        // meta model does not offer more children
    }

    public void visit(TRelationshipTemplate relationshipTemplate) {
        this.visit((TEntityTemplate) relationshipTemplate);
        final TRelationshipTemplate.RelationshipConstraints relationshipConstraints = relationshipTemplate.getRelationshipConstraints();
        if (relationshipConstraints != null) {
            for (TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint relationshipConstraint : relationshipConstraints.getRelationshipConstraint()) {
                relationshipConstraint.accept(this);
            }
        }
        // meta model does not offer more children
    }

    public void visit(TEntityTemplate.Properties properties) {
        final LinkedHashMap<String, String> kvProperties = properties.getKVProperties();
        if (kvProperties != null) {
            this.visitKvProperties(kvProperties);
        }
        // meta model does not offer more children
    }

    public void visitKvProperties(LinkedHashMap<String, String> kvProperties) {
        // this is a leaf, so no action to take
    }

    public void visit(TEntityTemplate.PropertyConstraints propertyConstraints) {
        for (TPropertyConstraint propertyConstraint : propertyConstraints.getPropertyConstraint()) {
            propertyConstraint.accept(this);
        }
        // meta model does not offer more children
    }

    public void visit(TRelationshipTemplate.RelationshipConstraints.RelationshipConstraint relationshipConstraint) {
        // this is a leaf, so no action to take
    }

    public void visit(TNodeTemplate.Capabilities capabilities) {
        for (TCapability capability : capabilities.getCapability()) {
            capability.accept(this);
        }
        // meta model does not offer more children
    }

    public void visit(TNodeTemplate.Requirements requirements) {
        for (TRequirement requirement : requirements.getRequirement()) {
            requirement.accept(this);
        }
        // meta model does not offer more children
    }

    public void visit(TRequirement requirement) {
        final TEntityTemplate.Properties properties = requirement.getProperties();
        if (properties != null) {
            properties.accept(this);
        }
        final TEntityTemplate.PropertyConstraints propertyConstraints = requirement.getPropertyConstraints();
        if (propertyConstraints != null) {
            propertyConstraints.accept(this);
        }
        // meta model does not offer more children
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
        final TBoundaryDefinitions.Interfaces interfaces = boundaryDefinitions.getInterfaces();
        if (interfaces != null) {
            for (TExportedInterface exportedInterface : interfaces.getInterface()) {
                exportedInterface.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsCapabilities(@NonNull TBoundaryDefinitions boundaryDefinitions) {
        final TBoundaryDefinitions.Capabilities capabilities = boundaryDefinitions.getCapabilities();
        if (capabilities != null) {
            for (TCapabilityRef capabilityRef : capabilities.getCapability()) {
                capabilityRef.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsRequirements(@NonNull TBoundaryDefinitions boundaryDefinitions) {
        final TBoundaryDefinitions.Requirements requirements = boundaryDefinitions.getRequirements();
        if (requirements != null) {
            for (TRequirementRef requirementRef : requirements.getRequirement()) {
                requirementRef.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsPolicies(@NonNull TBoundaryDefinitions boundaryDefinitions) {
        final TPolicies policies = boundaryDefinitions.getPolicies();
        if (policies != null) {
            for (TPolicy policy : policies.getPolicy()) {
                policy.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsPropertyConstraints(@NonNull TBoundaryDefinitions boundaryDefinitions) {
        final TBoundaryDefinitions.PropertyConstraints propertyConstraints = boundaryDefinitions.getPropertyConstraints();
        if (propertyConstraints != null) {
            for (TPropertyConstraint propertyConstraint : propertyConstraints.getPropertyConstraint()) {
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
        final TBoundaryDefinitions.Properties.PropertyMappings propertyMappings = properties.getPropertyMappings();
        if (propertyMappings != null) {
            for (TPropertyMapping propertyMapping : propertyMappings.getPropertyMapping()) {
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

    public void visit(TEntityType.PropertyDefinition propertiesDefinition) {
        // this is a leaf, so no action to take
    }

    public void visit(TDeploymentArtifact artifact) {
        // this is a leaf, so no action to take
    }

    public void visit(TImplementationArtifact artifact) {
        // this is a leaf, so no action to take
    }
}

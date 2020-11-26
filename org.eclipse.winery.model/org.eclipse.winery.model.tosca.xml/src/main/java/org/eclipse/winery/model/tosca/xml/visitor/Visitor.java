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

package org.eclipse.winery.model.tosca.xml.visitor;

import java.util.Map;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.xml.XHasId;
import org.eclipse.winery.model.tosca.xml.XRelationshipSourceOrTarget;
import org.eclipse.winery.model.tosca.xml.XTBoundaryDefinitions;
import org.eclipse.winery.model.tosca.xml.XTCapability;
import org.eclipse.winery.model.tosca.xml.XTCapabilityRef;
import org.eclipse.winery.model.tosca.xml.XTCondition;
import org.eclipse.winery.model.tosca.xml.XTDocumentation;
import org.eclipse.winery.model.tosca.xml.XTEntityTemplate;
import org.eclipse.winery.model.tosca.xml.XTEntityType;
import org.eclipse.winery.model.tosca.xml.XTExportedInterface;
import org.eclipse.winery.model.tosca.xml.XTExtensibleElements;
import org.eclipse.winery.model.tosca.xml.XTImplementationArtifact;
import org.eclipse.winery.model.tosca.xml.XTNodeTemplate;
import org.eclipse.winery.model.tosca.xml.XTParameter;
import org.eclipse.winery.model.tosca.xml.XTPlan;
import org.eclipse.winery.model.tosca.xml.XTPlans;
import org.eclipse.winery.model.tosca.xml.XTPolicies;
import org.eclipse.winery.model.tosca.xml.XTPolicy;
import org.eclipse.winery.model.tosca.xml.XTPropertyConstraint;
import org.eclipse.winery.model.tosca.xml.XTPropertyMapping;
import org.eclipse.winery.model.tosca.xml.XTRelationshipTemplate;
import org.eclipse.winery.model.tosca.xml.XTRequirementRef;
import org.eclipse.winery.model.tosca.xml.XTServiceTemplate;
import org.eclipse.winery.model.tosca.xml.XTTag;
import org.eclipse.winery.model.tosca.xml.XTTags;
import org.eclipse.winery.model.tosca.xml.XTDeploymentArtifact;
import org.eclipse.winery.model.tosca.xml.XTDeploymentArtifacts;
import org.eclipse.winery.model.tosca.xml.XTRequirement;
import org.eclipse.winery.model.tosca.xml.XTTopologyTemplate;
import org.eclipse.winery.model.tosca.xml.extensions.XOTStringList;

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

    public void visit(XTServiceTemplate serviceTemplate) {
        Objects.requireNonNull(serviceTemplate);
        visit((XTExtensibleElements) serviceTemplate);

        final XTTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        if (topologyTemplate != null) {
            topologyTemplate.accept(this);
        }

        final XTTags tags = serviceTemplate.getTags();
        if (tags != null) {
            for (XTTag tag : tags.getTag()) {
                tag.accept(this);
            }
        }

        final XTPlans plans = serviceTemplate.getPlans();
        if (plans != null) {
            for (XTPlan plan : plans.getPlan()) {
                plan.accept(this);
            }
        }

        final XTBoundaryDefinitions boundaryDefinitions = serviceTemplate.getBoundaryDefinitions();
        if (boundaryDefinitions != null) {
            boundaryDefinitions.accept(this);
        }
    }

    public void visit(XTPlan plan) {
        Objects.requireNonNull(plan);
        visit((XTExtensibleElements) plan);

        final XTCondition precondition = plan.getPrecondition();
        if (precondition != null) {
            precondition.accept(this);
        }
        final XTPlan.InputParameters inputParameters = plan.getInputParameters();
        if (inputParameters != null) {
            for (XTParameter parameter : inputParameters.getInputParameter()) {
                parameter.accept(this);
            }
        }
        plan.getOutputParameters();
    }

    public void visit(XTTopologyTemplate topologyTemplate) {
        Objects.requireNonNull(topologyTemplate);
        visit((XTExtensibleElements) topologyTemplate);

        for (XTNodeTemplate nodeTemplate : topologyTemplate.getNodeTemplates()) {
            nodeTemplate.accept(this);
        }
        for (XTRelationshipTemplate relationshipTemplate : topologyTemplate.getRelationshipTemplates()) {
            relationshipTemplate.accept(this);
        }
        // meta model does not offer more children
    }

    public void visit(XTExtensibleElements extensibleElement) {
        this.visitOtherAttributes(extensibleElement.getOtherAttributes());
        for (XTDocumentation documentation : extensibleElement.getDocumentation()) {
            documentation.accept(this);
        }
        // meta model does not offer more children
    }

    public void visit(XTEntityType entityType) {
        Objects.requireNonNull(entityType);
        visit((XTExtensibleElements) entityType);
        final XTEntityType.PropertiesDefinition propertiesDefinition = entityType.getPropertiesDefinition();
        if (propertiesDefinition != null) {
            propertiesDefinition.accept(this);
        }
    }

    public void visitOtherAttributes(Map<QName, String> otherAttributes) {
        // this is a leaf, so no action to take
    }

    public void visit(XTEntityTemplate entityTemplate) {
        this.visit((XHasId) entityTemplate);
        final XTEntityTemplate.Properties properties = entityTemplate.getProperties();
        if (properties != null) {
            properties.accept(this);
        }
        final XTEntityTemplate.PropertyConstraints propertyConstraints = entityTemplate.getPropertyConstraints();
        if (propertyConstraints != null) {
            propertyConstraints.accept(this);
        }
        // meta model does not offer more children
    }

    public void visit(XTNodeTemplate nodeTemplate) {
        this.visit((XRelationshipSourceOrTarget) nodeTemplate);
        final XTNodeTemplate.Requirements requirements = nodeTemplate.getRequirements();
        if (requirements != null) {
            requirements.accept(this);
        }
        final XTNodeTemplate.Capabilities capabilities = nodeTemplate.getCapabilities();
        if (capabilities != null) {
            capabilities.accept(this);
        }
        final XTDeploymentArtifacts deploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
        if (deploymentArtifacts != null) {
            for (XTDeploymentArtifact deploymentArtifact : deploymentArtifacts.getDeploymentArtifact()) {
                deploymentArtifact.accept(this);
            }
        }
        final XTPolicies policies = nodeTemplate.getPolicies();
        if (policies != null) {
            for (XTPolicy policy : policies.getPolicy()) {
                policy.accept(this);
            }
        }
        // meta model does not offer more children
    }

    public void visit(XTRelationshipTemplate relationshipTemplate) {
        this.visit((XTEntityTemplate) relationshipTemplate);
        final XTRelationshipTemplate.RelationshipConstraints relationshipConstraints = relationshipTemplate.getRelationshipConstraints();
        if (relationshipConstraints != null) {
            for (XTRelationshipTemplate.RelationshipConstraints.RelationshipConstraint relationshipConstraint : relationshipConstraints.getRelationshipConstraint()) {
                relationshipConstraint.accept(this);
            }
        }
        // meta model does not offer more children
    }

    public void visit(XTEntityTemplate.Properties properties) {
        // this is a leaf because the xml model just has an "any" here
    }

    public void visit(XTEntityTemplate.PropertyConstraints propertyConstraints) {
        for (XTPropertyConstraint propertyConstraint : propertyConstraints.getPropertyConstraint()) {
            propertyConstraint.accept(this);
        }
        // meta model does not offer more children
    }

    public void visit(XTRelationshipTemplate.RelationshipConstraints.RelationshipConstraint relationshipConstraint) {
        // this is a leaf, so no action to take
    }

    public void visit(XTNodeTemplate.Capabilities capabilities) {
        for (XTCapability capability : capabilities.getCapability()) {
            capability.accept(this);
        }
        // meta model does not offer more children
    }

    public void visit(XTNodeTemplate.Requirements requirements) {
        for (XTRequirement requirement : requirements.getRequirement()) {
            requirement.accept(this);
        }
        // meta model does not offer more children
    }

    public void visit(XTRequirement requirement) {
        final XTEntityTemplate.Properties properties = requirement.getProperties();
        if (properties != null) {
            properties.accept(this);
        }
        final XTEntityTemplate.PropertyConstraints propertyConstraints = requirement.getPropertyConstraints();
        if (propertyConstraints != null) {
            propertyConstraints.accept(this);
        }
        // meta model does not offer more children
    }

    public void accept(XTTag tag) {
        // this is a leaf, so no action to take
    }

    public void accept(XTCondition condition) {
        // this is a leaf, so no action to take
    }

    public void visit(XTParameter parameter) {
        // this is a leaf, so no action to take
    }

    public void visit(@NonNull XTBoundaryDefinitions boundaryDefinitions) {
        this.acceptBoundaryDefinitionsProperties(boundaryDefinitions);
        this.acceptBoundaryDefinitionsPropertyConstraints(boundaryDefinitions);
        this.acceptBoundaryDefinitionsPolicies(boundaryDefinitions);
        this.acceptBoundaryDefinitionsRequirements(boundaryDefinitions);
        this.acceptBoundaryDefinitionsCapabilities(boundaryDefinitions);
        this.acceptBoundaryDefinitionsInterfaces(boundaryDefinitions);
    }

    private void acceptBoundaryDefinitionsInterfaces(@NonNull XTBoundaryDefinitions boundaryDefinitions) {
        final XTBoundaryDefinitions.Interfaces interfaces = boundaryDefinitions.getInterfaces();
        if (interfaces != null) {
            for (XTExportedInterface exportedInterface : interfaces.getInterface()) {
                exportedInterface.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsCapabilities(@NonNull XTBoundaryDefinitions boundaryDefinitions) {
        final XTBoundaryDefinitions.Capabilities capabilities = boundaryDefinitions.getCapabilities();
        if (capabilities != null) {
            for (XTCapabilityRef capabilityRef : capabilities.getCapability()) {
                capabilityRef.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsRequirements(@NonNull XTBoundaryDefinitions boundaryDefinitions) {
        final XTBoundaryDefinitions.Requirements requirements = boundaryDefinitions.getRequirements();
        if (requirements != null) {
            for (XTRequirementRef requirementRef : requirements.getRequirement()) {
                requirementRef.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsPolicies(@NonNull XTBoundaryDefinitions boundaryDefinitions) {
        final XTPolicies policies = boundaryDefinitions.getPolicies();
        if (policies != null) {
            for (XTPolicy policy : policies.getPolicy()) {
                policy.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsPropertyConstraints(@NonNull XTBoundaryDefinitions boundaryDefinitions) {
        final XTBoundaryDefinitions.PropertyConstraints propertyConstraints = boundaryDefinitions.getPropertyConstraints();
        if (propertyConstraints != null) {
            for (XTPropertyConstraint propertyConstraint : propertyConstraints.getPropertyConstraint()) {
                propertyConstraint.accept(this);
            }
        }
    }

    private void acceptBoundaryDefinitionsProperties(@NonNull XTBoundaryDefinitions boundaryDefinitions) {
        final XTBoundaryDefinitions.Properties properties = boundaryDefinitions.getProperties();
        if (properties != null) {
            properties.accept(this);
        }
    }

    public void visit(XTBoundaryDefinitions.Properties properties) {
        final XTBoundaryDefinitions.Properties.PropertyMappings propertyMappings = properties.getPropertyMappings();
        if (propertyMappings != null) {
            for (XTPropertyMapping propertyMapping : propertyMappings.getPropertyMapping()) {
                propertyMapping.accept(this);
            }
        }
    }

    public void visit(XTPropertyMapping propertyMapping) {
        // this is a leaf, so no action to take
    }

    public void visit(XTCapabilityRef capabilityRef) {
        // this is a leaf, so no action to take
    }

    public void visit(XTRequirementRef requirementRef) {
        // this is a leaf, so no action to take
    }

    public void accept(XTExportedInterface exportedInterface) {
        // this is a leaf, so no action to take
    }

    public void visit(XTPropertyConstraint propertyConstraint) {
        // this is a leaf, so no action to take
    }

    public void visit(XTDocumentation documentation) {
        // this is a leaf, so no action to take
    }

    public void visit(XTEntityType.PropertiesDefinition propertiesDefinition) {
        // this is a leaf, so no action to take
    }

    public void visit(XTDeploymentArtifact artifact) {
        // this is a leaf, so no action to take
    }

    public void visit(XTImplementationArtifact artifact) {
        // this is a leaf, so no action to take
    }

    public void accept(XOTStringList otStringList) {
        // this is a leaf, so no action to take
    }
}

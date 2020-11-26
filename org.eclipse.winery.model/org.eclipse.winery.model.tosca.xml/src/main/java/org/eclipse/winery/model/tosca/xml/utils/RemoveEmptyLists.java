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
package org.eclipse.winery.model.tosca.xml.utils;

import org.eclipse.winery.model.tosca.xml.XTEntityTemplate;
import org.eclipse.winery.model.tosca.xml.XTNodeTemplate;
import org.eclipse.winery.model.tosca.xml.XTPolicies;
import org.eclipse.winery.model.tosca.xml.XTRelationshipTemplate;
import org.eclipse.winery.model.tosca.xml.visitor.Visitor;
import org.eclipse.winery.model.tosca.xml.XTDeploymentArtifacts;
import org.eclipse.winery.model.tosca.xml.XTTopologyTemplate;

import io.github.adr.embedded.ADR;

/**
 * This class removes empty lists. For instance, if a node template has a list of policies and this list is empty, it is
 * removed. This is important for XSD validation.
 *
 * Use it by calling <code>removeEmptyLists(topologyTemplate)</code>
 *
 * TODO: Extend this for all model elements
 */
@ADR(22)
public class RemoveEmptyLists extends Visitor {

    @Override
    public void visit(XTEntityTemplate entityTemplate) {
        final XTEntityTemplate.PropertyConstraints propertyConstraints = entityTemplate.getPropertyConstraints();
        if ((propertyConstraints != null) && propertyConstraints.getPropertyConstraint().isEmpty()) {
            entityTemplate.setPropertyConstraints(null);
        }
        XTEntityTemplate.Properties properties = entityTemplate.getProperties();
        if ((properties != null) && (properties.getAny() == null)) {
            entityTemplate.setProperties(null);
        }
        super.visit(entityTemplate);
    }

    @Override
    public void visit(XTNodeTemplate nodeTemplate) {
        final XTNodeTemplate.Requirements requirements = nodeTemplate.getRequirements();
        if ((requirements != null) && requirements.getRequirement().isEmpty()) {
            nodeTemplate.setRequirements(null);
        }
        final XTNodeTemplate.Capabilities capabilities = nodeTemplate.getCapabilities();
        if ((capabilities != null) && capabilities.getCapability().isEmpty()) {
            nodeTemplate.setCapabilities(null);
        }
        final XTDeploymentArtifacts deploymentArtifacts = nodeTemplate.getDeploymentArtifacts();
        if ((deploymentArtifacts != null) && deploymentArtifacts.getDeploymentArtifact().isEmpty()) {
            nodeTemplate.setDeploymentArtifacts(null);
        }
        final XTPolicies policies = nodeTemplate.getPolicies();
        if ((policies != null) && policies.getPolicy().isEmpty()) {
            nodeTemplate.setPolicies(null);
        }
        super.visit(nodeTemplate);
    }

    @Override
    public void visit(XTRelationshipTemplate relationshipTemplate) {
        final XTRelationshipTemplate.RelationshipConstraints relationshipConstraints = relationshipTemplate.getRelationshipConstraints();
        if ((relationshipConstraints != null) && relationshipConstraints.getRelationshipConstraint().isEmpty()) {
            relationshipTemplate.setRelationshipConstraints(null);
        }
        super.visit(relationshipTemplate);
    }

    /**
     * Removes the empty lists in the given topology template
     *
     * @param topologyTemplate the topology template to modify
     */
    public void removeEmptyLists(XTTopologyTemplate topologyTemplate) {
        this.visit(topologyTemplate);
    }
}

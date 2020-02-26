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

package org.eclipse.winery.common.toscalite;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.edmm.EdmmType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

public class ToscaLiteChecker {

    private static final String LISTING_STRING = "- ";
    private static final String INDENTATION_STRING = "  ";

    private static final int TYPE_LEVEL = 0;
    private static final int TEMPLATE_LEVEL = 1;

    private final Map<QName, TNodeType> nodeTypes;
    private final Map<QName, TRelationshipType> relationshipTypes;
    private final Map<QName, EdmmType> edmmTypeMappings;
    private final Map<QName, EdmmType> oneToOneMappings;
    private QName hostedOn;
    private QName connectsTo;
    private QName dependsOn;
    private Map<QName, StringBuilder> errorList;
    private boolean foundError;

    public ToscaLiteChecker(Map<QName, TNodeType> nodeTypes,
                            Map<QName, TRelationshipType> relationshipTypes,
                            Map<QName, EdmmType> edmmTypeMappings, 
                            Map<QName, EdmmType> oneToOneMappings) {
        this.nodeTypes = nodeTypes;
        this.relationshipTypes = relationshipTypes;
        this.edmmTypeMappings = edmmTypeMappings;
        this.oneToOneMappings = oneToOneMappings;

        this.oneToOneMappings.entrySet().stream()
            .filter(entry -> entry.getValue().equals(EdmmType.DEPENDS_ON))
            .findFirst()
            .ifPresent(entry -> this.dependsOn = entry.getKey());
        this.oneToOneMappings.entrySet().stream()
            .filter(entry -> entry.getValue().equals(EdmmType.CONNECTS_TO))
            .findFirst()
            .ifPresent(entry -> this.connectsTo = entry.getKey());
        this.oneToOneMappings.entrySet().stream()
            .filter(entry -> entry.getValue().equals(EdmmType.HOSTED_ON))
            .findFirst()
            .ifPresent(entry -> this.hostedOn = entry.getKey());
    }

    public boolean isToscaLiteCompliant(TServiceTemplate serviceTemplate) {
        this.checkToscaLiteCompatibility(serviceTemplate);
        return this.foundError;
    }

    public String checkToscaLiteCompatibility(TServiceTemplate serviceTemplate) {
        QName serviceTemplateQName = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getName());
        this.errorList = new HashMap<>();
        this.errorList.put(serviceTemplateQName, new StringBuilder());

        // Tags -> metadata

        if (serviceTemplate.getBoundaryDefinitions() != null) {
            this.addErrorToList(serviceTemplateQName, TYPE_LEVEL, "BOUNDARY DEFINITIONS");
        }
        if (serviceTemplate.getPlans() != null) {
            this.addErrorToList(serviceTemplateQName, TYPE_LEVEL, "PLANS");
        }

        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        if (topologyTemplate != null) {
            topologyTemplate.getNodeTemplates()
                .forEach(tNodeTemplate -> checkToscaLiteCompatibility(tNodeTemplate, serviceTemplateQName));
            topologyTemplate.getRelationshipTemplates()
                .forEach(tRelationshipTemplate -> checkToscaLiteCompatibility(tRelationshipTemplate, serviceTemplateQName));
        }

        return this.errorList.toString();
    }

    private void checkToscaLiteCompatibility(TRelationshipTemplate relation, QName serviceTemplateQName) {
        QName type = relation.getType();

        if (!this.isElementVisited(type)) {
            this.checkRelationType(type);
        }

        if (relation.getPolicies() != null) {
            this.addErrorToList(serviceTemplateQName, TEMPLATE_LEVEL, "relation", relation.getId(), "specifies POLICIES which");
        }
        if (relation.getPropertyConstraints() != null) {
            this.addErrorToList(serviceTemplateQName, TEMPLATE_LEVEL, "relation", relation.getId(), "specifies PROPERTY CONSTRAINTS which");
        }
    }

    private void checkRelationType(QName type) {
        TRelationshipType relType = this.relationshipTypes.get(type);
        if (!ModelUtilities.isOfType(this.hostedOn, type, this.relationshipTypes)
            || !ModelUtilities.isOfType(this.connectsTo, type, this.relationshipTypes)
            || !ModelUtilities.isOfType(this.dependsOn, type, this.relationshipTypes)) {
            this.addErrorToList(type, TYPE_LEVEL, "Relation Type does not inherit from",
                EdmmType.DEPENDS_ON, EdmmType.HOSTED_ON, EdmmType.DEPENDS_ON, "or connectsTo and, thus,");
        }
        
        // todo ?
        relType.getSourceInterfaces();
        relType.getTargetInterfaces();
    }

    private boolean isElementVisited(QName qName) {
        StringBuilder stringBuilder = this.errorList.get(qName);
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder();
            this.errorList.put(qName, stringBuilder);
            return false;
        }
        return true;
    }

    private void checkToscaLiteCompatibility(TNodeTemplate tNodeTemplate, QName serviceTemplateQName) {

    }

    private void addErrorToList(QName qName, int level, Object... messageElements) {
        StringBuilder stringBuilder = this.errorList.get(qName);
        for (int index = 0; index < level; index++) {
            stringBuilder.append(INDENTATION_STRING);
        }
        for (Object error : messageElements) {
            stringBuilder.append(error)
                .append(" ");
        }
        stringBuilder.append("cannot be mapped to TOSCA Lite!\n");
        this.foundError = true;
    }
}

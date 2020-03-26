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

package org.eclipse.winery.common.toscalight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.edmm.EdmmType;
import org.eclipse.winery.model.tosca.HasPolicies;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;

public class ToscaLightChecker {

    private final Map<QName, TNodeType> nodeTypes;
    private final Map<QName, TRelationshipType> relationshipTypes;
    private final Map<QName, EdmmType> edmmTypeMappings;
    private final Map<QName, EdmmType> oneToOneMappings;
    private QName hostedOn;
    private QName connectsTo;
    private QName dependsOn;
    private Map<QName, List<String>> errorList;
    private boolean foundError;

    public ToscaLightChecker(Map<QName, TNodeType> nodeTypes,
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

    public Map<QName, List<String>> getErrorList() {
        return errorList;
    }

    public boolean isToscaLightCompliant(TServiceTemplate serviceTemplate) {
        this.checkToscaLightCompatibility(serviceTemplate);
        return !this.foundError;
    }

    public Map<QName, List<String>> checkToscaLightCompatibility(TServiceTemplate serviceTemplate) {
        QName serviceTemplateQName = new QName(serviceTemplate.getTargetNamespace(), serviceTemplate.getName());
        this.errorList = new HashMap<>();
        this.errorList.put(serviceTemplateQName, new ArrayList<>());

        // Tags -> metadata

        if (serviceTemplate.getBoundaryDefinitions() != null) {
            this.addErrorToList(serviceTemplateQName, "specifies BOUNDARY DEFINITIONS which");
        }
        if (serviceTemplate.getPlans() != null) {
            this.addErrorToList(serviceTemplateQName, "specifies PLANS which");
        }

        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
        if (topologyTemplate != null) {
            topologyTemplate.getNodeTemplates()
                .forEach(tNodeTemplate -> checkToscaLightCompatibility(tNodeTemplate, serviceTemplateQName));
            topologyTemplate.getRelationshipTemplates()
                .forEach(tRelationshipTemplate -> checkToscaLightCompatibility(tRelationshipTemplate, serviceTemplateQName));
        }

        return this.errorList;
    }

    private void checkToscaLightCompatibility(TEntityTemplate template, QName parentElement) {
        String templateClass = template.getClass().getSimpleName().substring(1);

        if (!this.isElementVisited(template.getType())) {
            this.checkType(template, parentElement);
        }
        if (template.getPropertyConstraints() != null) {
            this.addErrorToList(parentElement, templateClass, template.getId(),
                "specifies PROPERTY CONSTRAINTS which");
        }
        if (template instanceof HasPolicies && ((HasPolicies) template).getPolicies() != null) {
            this.addErrorToList(parentElement, templateClass, template.getId(),
                "specifies POLICIES which");
        }
    }

    private void checkType(TEntityTemplate template, QName parentElement) {
        QName type = template.getType();

        if (template instanceof TRelationshipTemplate) {
            this.checkRelationType(type);
            this.checkToscaLightCompatibilityOfRelationshipTemplate((TRelationshipTemplate) template, parentElement);
        } else if (template instanceof TNodeTemplate) {
            this.checkNodeType(type);
            this.checkToscaLightCompatibilityOfNodeTemplate((TNodeTemplate) template, parentElement);
        }
    }

    private void checkNodeType(QName type) {
        TNodeType nodeType = this.nodeTypes.get(type);

        // todo: some inheritance or mapping check?

        if (nodeType.getInterfaces() != null) {
            List<TInterface> interfaceList = nodeType.getInterfaces().getInterface();
            if (interfaceList.size() > 0) {
                interfaceList.stream()
                    .filter(ToscaLightUtils::isNotLifecycleInterface)
                    .forEach(unsupportedInterface -> this.addErrorToList(type,
                        "specifies the INTERFACE", unsupportedInterface.getName(), "which")
                    );
            }
        }
    }

    private void checkRelationType(QName type) {
        TRelationshipType relType = this.relationshipTypes.get(type);
        if (!(ModelUtilities.isOfType(this.hostedOn, type, this.relationshipTypes)
            || ModelUtilities.isOfType(this.connectsTo, type, this.relationshipTypes)
            || ModelUtilities.isOfType(this.dependsOn, type, this.relationshipTypes))) {
            this.addErrorToList(type, "Relation Type does not inherit from",
                EdmmType.DEPENDS_ON, EdmmType.HOSTED_ON, EdmmType.DEPENDS_ON, "or connectsTo and, thus,");
        }

        // todo ?
        relType.getSourceInterfaces();
        relType.getTargetInterfaces();
    }

    private boolean isElementVisited(QName qName) {
        List<String> errors = this.errorList.get(qName);
        if (errors == null) {
            errors = new ArrayList<>();
            this.errorList.put(qName, errors);
            return false;
        }
        return true;
    }

    private void checkToscaLightCompatibilityOfNodeTemplate(TNodeTemplate node, QName serviceTemplateQName) {
        // todo: is there anything more?
    }

    private void checkToscaLightCompatibilityOfRelationshipTemplate(TRelationshipTemplate relation, QName serviceTemplateQName) {
        // todo: is there anything more?
    }

    private void addErrorToList(QName qName, Object... messageElements) {
        List<String> errorList = this.errorList.get(qName);
        StringBuilder stringBuilder = new StringBuilder();
        for (Object error : messageElements) {
            stringBuilder.append(error)
                .append(" ");
        }
        stringBuilder.append("cannot be mapped to TOSCA Light!");
        errorList.add(stringBuilder.toString());
        this.foundError = true;
    }
}

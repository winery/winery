/********************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.compliance;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.extensions.ComplianceRuleId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTComplianceRule;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

public class ToscaModelHelper {

    public static final String TEST_TARGET_NAMESPACE = "http://www.opentosca.de/TESTCASES";

    public static TNodeTemplate createTNodeTemplate(String id) {
        TNodeTemplate template = new TNodeTemplate();
        template.setName(id);
        template.setId(id);
        return template;
    }

    public static TTopologyTemplate createTTopologyTemplate(List<TNodeTemplate> nodeTemplates, List<TRelationshipTemplate> relationshipTemplates) {
        return new TTopologyTemplate.Builder()
            .addNodeTemplates(nodeTemplates)
            .addRelationshipTemplates(relationshipTemplates)
            .build();
    }

    public static TNodeType createTNodeType(NodeTypeId nodeTypeIdAbstractA) {
        return createTNodeType(nodeTypeIdAbstractA.getQName().getLocalPart(), nodeTypeIdAbstractA.getQName().getNamespaceURI());
    }

    public static TNodeType createTNodeType(String id, String namespace) {
        TNodeType nodeType = new TNodeType();
        nodeType.setName(id);
        nodeType.setId(id);
        nodeType.setTargetNamespace(namespace);
        return nodeType;
    }

    public static TServiceTemplate createTServiceTemplate(String id, String namespace) {
        TServiceTemplate serviceTemplate = new TServiceTemplate();
        serviceTemplate.setName(id);
        serviceTemplate.setId(id);
        serviceTemplate.setTargetNamespace(namespace);
        return serviceTemplate;
    }

    public static ToscaEdge addEdge(ToscaGraph graph, ToscaNode source, ToscaNode target, String id, String name) {
        ToscaEdge edge = new ToscaEdge(source, target);
        edge.setId(id);
        graph.addEdge(source, target, edge);
        TRelationshipTemplate template = new TRelationshipTemplate();
        template.setName(name);
        edge.setTemplate(template);
        return edge;
    }

    public static ToscaNode createTOSCANode(String id, String name) {
        TNodeTemplate template = new TNodeTemplate();
        template.setName(name);
        ToscaNode node = new ToscaNode();
        node.setNodeTemplate(template);
        node.setId(id);
        return node;
    }

    public static ToscaNode createTOSCANodeOnlyProperties(ToscaModelPropertiesBuilder propertiesBuilder) {
        ToscaNode node = new ToscaNode();
        node.setNodeTemplate(new TNodeTemplate());
        node.getTemplate().setProperties(propertiesBuilder.build());
        return node;
    }

    public static ToscaNode createTOSCANodeOnlyTypes(TNodeTemplate nodeTemplate) {
        ToscaNode node = new ToscaNode();
        ToscaTransformer.addTEntityTypes(nodeTemplate.getType(), node, TNodeType.class);
        return node;
    }

    public static TNodeTemplate createTNodeTemplate(String id, NodeTypeId nodeTypeId) {
        TNodeTemplate tNodeTemplate = createTNodeTemplate(id);
        tNodeTemplate.setType(nodeTypeId.getQName());
        return tNodeTemplate;
    }

    public static NodeTypeId createNodeTypeId(String id) {
        return new NodeTypeId(new QName(TEST_TARGET_NAMESPACE, id));
    }

    public static void setDerivedFrom(NodeTypeId parentNodeTypeId, TNodeType nodeType) {
        TEntityType.DerivedFrom derivedFrom = new TEntityType.DerivedFrom();
        derivedFrom.setTypeRef(parentNodeTypeId.getQName());
        nodeType.setDerivedFrom(derivedFrom);
    }

    public static TRelationshipTemplate createTRelationshipTemplate(String id) {
        TRelationshipTemplate template = new TRelationshipTemplate();
        template.setName(id);
        template.setId(id);
        return template;
    }

    public static TRelationshipType createTRelationshipType(String id, String namespace) {
        TRelationshipType relationshipType = new TRelationshipType();
        relationshipType.setName(id);
        relationshipType.setId(id);
        relationshipType.setTargetNamespace(namespace);

        return relationshipType;
    }

//	public static TTags createTTags(List<ComplianceRule> ids) {
//		TTags tTags = new TTags();
//		TTag tTag = new TTag();
//		tTag.setName(TAG_NAME);
//		tTag.setValue(ids.stream().map(id -> id.getQName().toString()).collect(Collectors.joining(TAG_DELIMITER, "", "")));
//		tTags.getTag().add(tTag);
//		return tTags;
//	}

    public static OTComplianceRule createTComplianceRule(ComplianceRuleId id) {
        return createTComplianceRule(id.getQName().getLocalPart(), id.getQName().getNamespaceURI());
    }

    public static OTComplianceRule createTComplianceRule(String id, String ns) {
        return new OTComplianceRule(new OTComplianceRule.Builder(id)
            .setTargetNamespace(ns));
    }
}

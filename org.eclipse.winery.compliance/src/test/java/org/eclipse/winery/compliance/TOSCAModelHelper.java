/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

import org.eclipse.winery.common.ids.definitions.ComplianceRuleId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TComplianceRule;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.compliance.model.TOSCAEdge;
import org.eclipse.winery.compliance.model.TOSCAGraph;
import org.eclipse.winery.compliance.model.TOSCANode;
import org.eclipse.winery.compliance.transformation.TOSCATransformer;

public class TOSCAModelHelper {

	public static final String TEST_TARGET_NAMESPACE = "http://www.opentosca.de/TESTCASES";

	public static TNodeTemplate createTNodeTemplate(String id) {
		TNodeTemplate template = new TNodeTemplate();
		template.setName(id);
		template.setId(id);
		return template;
	}

	public static TTopologyTemplate createTTopologyTemplate(List<TNodeTemplate> nodeTemplates, List<TRelationshipTemplate> relationshipTemplates) {
		TTopologyTemplate template = new TTopologyTemplate();
		template.setNodeTemplates(nodeTemplates);
		template.setRelationshipTemplates(relationshipTemplates);
		return template;
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



	public static TOSCAEdge addEdge(TOSCAGraph graph, TOSCANode source, TOSCANode target, String id, String name) {
		TOSCAEdge edge = graph.getEdgeFactory().createEdge(source, target);
		edge.setId(id);
		graph.addEdge(source, target, edge);
		TRelationshipTemplate template = new TRelationshipTemplate();
		template.setName(name);
		edge.setTemplate(template);
		return edge;
	}

	public static TOSCANode createTOSCANode(String id, String name) {
		TNodeTemplate template = new TNodeTemplate();
		template.setName(name);
		TOSCANode node = new TOSCANode();
		node.setNodeTemplate(template);
		return node;
	}

	public static TOSCANode createTOSCANodeOnlyProperties(TOSCAModelPropertiesBuilder bldr) {
		TOSCANode node = new TOSCANode();
		node.setNodeTemplate(new TNodeTemplate());
		node.getNodeTemplate().setProperties(bldr.build());
		return node;
	}

	public static TOSCANode createTOSCANodeOnlyTypes(TNodeTemplate nodeTemplate) {
		TOSCANode node = new TOSCANode();
		TOSCATransformer.addTEntityTypes(nodeTemplate.getType(), node, TNodeType.class);
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

	public static TComplianceRule createTComplianceRule(ComplianceRuleId id) {
		return createTComplianceRule(id.getQName().getLocalPart(), id.getQName().getNamespaceURI());
	}

	public static TComplianceRule createTComplianceRule(String id, String ns) {
		TComplianceRule rule = new TComplianceRule();
		rule.setId(id);
		rule.setTargetNamespace(ns);
		return rule;
	}
}

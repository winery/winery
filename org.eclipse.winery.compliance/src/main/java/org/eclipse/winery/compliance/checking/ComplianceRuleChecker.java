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
package org.eclipse.winery.compliance.checking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.winery.model.tosca.extensions.OTComplianceRule;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.topologygraph.matching.ToscaIsomorphismMatcher;
import org.eclipse.winery.topologygraph.model.ToscaEdge;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.model.ToscaNode;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.jgrapht.GraphMapping;

public class ComplianceRuleChecker {

	private TTopologyTemplate identifierTemplate = null;
	private TTopologyTemplate requiredStructureTemplate = null;
	private TTopologyTemplate toCheckTemplate = null;

	private ToscaGraph identifierGraph = null;
	private ToscaGraph requiredStructureGraph = null;
	private ToscaGraph toCheckGraph = null;

	public ComplianceRuleChecker(OTComplianceRule rule, TTopologyTemplate toCheckTemplate) {
		this(rule.getIdentifier(), rule.getRequiredStructure(), toCheckTemplate);
	}

	public ComplianceRuleChecker(TTopologyTemplate identifierTemplate, TTopologyTemplate requiredStructureTemplate, TTopologyTemplate toCheckTemplate) {
		this.identifierTemplate = identifierTemplate;
		this.requiredStructureTemplate = requiredStructureTemplate;
		this.toCheckTemplate = toCheckTemplate;
	}

	protected static ToscaGraph initializeGraph(TTopologyTemplate topologyTemplate) {
		if (topologyTemplate != null) {
			return ToscaTransformer.createTOSCAGraph(topologyTemplate);
		}
		return null;
	}

	public List<GraphMapping> checkComplianceRule() throws ComplianceCheckingException {
		if (toCheckTemplate == null) {
			throw new ComplianceCheckingException(ComplianceCheckingException.NO_TEMPLATE_TO_CHECK);
		}

		if (isBlacklist()) {
			// check for violations
			return findMatches(getIdentifierGraph(), getToCheckGraph());
		}

		// whitelist??
		if (isWhitelist()) {
			throw new ComplianceCheckingException(ComplianceCheckingException.WHITELISTING_NOT_YET_IMPLEMENTED);
		}

		// completeRule
		if (isCompleteRule()) {
			//check rule validity
			if (!isCompleteRuleValid()) {
				//TODO we need to set reference node
				throw new ComplianceCheckingException(ComplianceCheckingException.IDENTIFIER_NOT_IN_REQUIREDSTRUCTURE);
			}
			//check for violations
			return checkForViolations(getIdentifierGraph(), getRequiredStructureGraph(), getToCheckGraph());
		}
		throw new ComplianceCheckingException(ComplianceCheckingException.EMPTY_COMPLIANCE_RULE);
	}

	public List<GraphMapping> checkForViolations(ToscaGraph identifierGraph, ToscaGraph requiredStructureGraph, ToscaGraph graphToSearchIn) {
		// identify all relevant areas
		List<GraphMapping> identifierMatches = findMatches(identifierGraph, graphToSearchIn);

		// find all required structures
		List<GraphMapping> requiredStructureMatches = findMatches(requiredStructureGraph, graphToSearchIn);

		if (identifierMatches.size() > requiredStructureMatches.size()) {
			List<GraphMapping> violatingMappings = extractViolatingMappings(identifierGraph, identifierMatches, requiredStructureMatches);
			return violatingMappings;
		} else {
			return new ArrayList<>();
		}
	}

	public List<GraphMapping> findMatches(ToscaGraph queryGraph, ToscaGraph searchInGraph) {
		ToscaIsomorphismMatcher matcher = new ToscaIsomorphismMatcher();
		Iterator<GraphMapping<ToscaNode, ToscaEdge>> iterator = matcher.findMatches(queryGraph, searchInGraph, new ToscaComplianceRuleMatcher());
		return convertToList(iterator);
	}

	public List<GraphMapping> extractViolatingMappings(ToscaGraph identifierGraph, List<GraphMapping> identifierMappings, List<GraphMapping> requiredStructureMappings) {
		List<GraphMapping> violatingMappings = new ArrayList<>();

		// for all mappings in identifierMappings, we need to find the corresponding required Structure Mappings
		for (GraphMapping identifierMapping : identifierMappings) {
			boolean foundCorrespondence = false;
			for (GraphMapping requiredStructureMapping : requiredStructureMappings) {
				//get the corresponding ToscaNode from the searchInGraph 
				ToscaNode identifierVertexCorrespondence = (ToscaNode) identifierMapping.getVertexCorrespondence(identifierGraph.getReferenceNode(), false);
				foundCorrespondence = (requiredStructureMapping.getVertexCorrespondence(identifierVertexCorrespondence, true) != null) ? true : false;
			}
			if (!foundCorrespondence) {
				violatingMappings.add(identifierMapping);
			}
		}
		return violatingMappings;
	}

	public List<GraphMapping> convertToList(Iterator<GraphMapping<ToscaNode, ToscaEdge>> iterator) {
		return com.google.common.collect.Lists.newArrayList(iterator);
	}

	public Map<ToscaNode, ToscaNode> getSubGraphMappingAsMap(GraphMapping mapping, ToscaGraph subGraph) {
		Map<ToscaNode, ToscaNode> result = new HashMap<>();
		for (ToscaNode node : subGraph.vertexSet()) {
			Optional.of(mapping.getVertexCorrespondence(node, false)).map(vertex -> (ToscaNode) vertex).ifPresent(vertex -> result.put(node, vertex));
		}
		return result;
	}

	public boolean isRuleValid() {
		return isBlacklist() || isWhitelist() || isCompleteRuleValid();
	}

	public boolean isCompleteRuleValid() {
		return findMatches(getIdentifierGraph(), getRequiredStructureGraph()).size() == 1;
	}

	public boolean isCompleteRule() {
		return isNotEmpty(getIdentifierTemplate()) && isNotEmpty(getRequiredStructureTemplate());
	}

	public boolean isBlacklist() {
		return isNotEmpty(getIdentifierTemplate()) && isEmpty(getRequiredStructureTemplate());
	}

	public boolean isWhitelist() {
		return isEmpty(getIdentifierTemplate()) && isNotEmpty(getRequiredStructureTemplate());
	}

	public TTopologyTemplate getIdentifierTemplate() {
		return identifierTemplate;
	}

	public TTopologyTemplate getRequiredStructureTemplate() {
		return requiredStructureTemplate;
	}

	public TTopologyTemplate getToCheckTemplate() {
		return toCheckTemplate;
	}

	public ToscaGraph getIdentifierGraph() {
		if (identifierGraph == null) {
			identifierGraph = initializeGraph(identifierTemplate);
		}
		return identifierGraph;
	}

	public ToscaGraph getRequiredStructureGraph() {
		if (requiredStructureGraph == null) {
			requiredStructureGraph = initializeGraph(requiredStructureTemplate);
		}
		return requiredStructureGraph;
	}

	public ToscaGraph getToCheckGraph() {
		if (toCheckGraph == null) {
			toCheckGraph = initializeGraph(toCheckTemplate);
		}
		return toCheckGraph;
	}

	public boolean isEmpty(TTopologyTemplate template) {
		return template == null || template.getNodeTemplateOrRelationshipTemplate().size() == 0;
	}

	public boolean isNotEmpty(TTopologyTemplate template) {
		return !isEmpty(template);
	}
	
	public void setIdentifierTemplate(TTopologyTemplate identifierTemplate) {
		this.identifierTemplate = identifierTemplate;
		this.identifierGraph = null;
	}

	public void setRequiredStructureTemplate(TTopologyTemplate requiredStructureTemplate) {
		this.requiredStructureTemplate = requiredStructureTemplate;
		this.requiredStructureGraph = null;
	}

	public void setToCheckTemplate(TTopologyTemplate toCheckTemplate) {
		this.toCheckTemplate = toCheckTemplate;
		this.toCheckGraph = null;
	}
}

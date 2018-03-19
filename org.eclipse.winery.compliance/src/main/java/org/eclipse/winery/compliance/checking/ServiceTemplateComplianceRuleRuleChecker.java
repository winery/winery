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
package org.eclipse.winery.compliance.checking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.ComplianceRule;
import org.eclipse.winery.compliance.model.TOSCANode;
import org.eclipse.winery.model.tosca.TComplianceRule;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;

import com.google.common.collect.Lists;
import org.eclipse.jdt.annotation.NonNull;
import org.jgrapht.GraphMapping;

public class ServiceTemplateComplianceRuleRuleChecker {

	private TServiceTemplate serviceTemplate;

	public ServiceTemplateComplianceRuleRuleChecker(@NonNull TServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	public ServiceTemplateCheckingResult checkComplianceRules() {
		StringBuilder checkingResult = new StringBuilder("Rulechecking result for servicetemplate " + serviceTemplate.getIdFromIdOrNameField() + System.lineSeparator());
		ServiceTemplateCheckingResult result = new ServiceTemplateCheckingResult();
		List<ComplianceRule> ruleIds = getRuleIds(serviceTemplate);
		for (ComplianceRule ruleId : ruleIds) {
			//get Rule

//			if (!RepositoryFactory.getRepository().exists(ruleId)) {
//				result.getException().add(ruleId.getQName());
//				checkingResult.append(ruleId.getQName().toString() + ": could not be found!");
//			} else {

			TComplianceRule tComplianceRule = RepositoryFactory.getRepository().getElement(ruleId);
			ComplianceRuleChecker checker = new ComplianceRuleChecker(tComplianceRule, serviceTemplate.getTopologyTemplate());
			List<GraphMapping> graphMappings = null;
			try {
				graphMappings = checker.checkComplianceRule();
				if (graphMappings.size() > 0) {
					result.getUnsatisfied().add(ruleId.getQName());
					checkingResult.append(ruleId.getQName().toString() + " violated:");
					checkingResult.append(System.lineSeparator());
					for (GraphMapping mapping : graphMappings) {
						Map<TOSCANode, TOSCANode> resultMap = checker.getSubGraphMappingAsMap(mapping, checker.getIdentifierGraph());
						checkingResult.append(System.lineSeparator());
						checkingResult.append(resultMap.values().stream().map(node -> node.getNodeTemplate().getIdFromIdOrNameField()).collect(Collectors.joining(";", "NodeTemplateIds: ", "")));
					}
				} else {
					result.getSatisfied().add(ruleId.getQName());
					checkingResult.append(ruleId.getQName().toString() + " satisfied");
				}
			} catch (ComplianceCheckingException e) {
				result.getException().add(ruleId.getQName());
				checkingResult.append(ruleId.getQName().toString() + ": Exception during checking:");
				checkingResult.append(System.lineSeparator());
				checkingResult.append(e.getMessage());
				checkingResult.append(System.lineSeparator());
//					e.printStackTrace();
			}
		}
//		}
		System.out.println(checkingResult.toString());
		return result;
	}

	public List<ComplianceRule> getRuleIds(TServiceTemplate serviceTemplate) {
		ArrayList<ComplianceRule> complianceRules = Lists.newArrayList();
		Namespace namespace = new Namespace(serviceTemplate.getTargetNamespace(), false);
		Collection<Namespace> componentsNamespaces = RepositoryFactory.getRepository().getComponentsNamespaces(ComplianceRule.class);
		List<Namespace> relevantNamespaces = componentsNamespaces.stream().filter(ns -> namespace.getDecoded().startsWith(ns.getDecoded())).collect(Collectors.toList());

		for (Namespace space : relevantNamespaces) {
			complianceRules.addAll((Collection<? extends ComplianceRule>) ((FilebasedRepository) RepositoryFactory.getRepository()).getAllIdsInNamespace(ComplianceRule.class, space));
		}
		return complianceRules;
	}

	public TServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	public void setServiceTemplate(TServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}
}

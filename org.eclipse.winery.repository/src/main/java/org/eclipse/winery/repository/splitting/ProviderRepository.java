/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Karoline Saatkamp, Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/

package org.eclipse.winery.repository.splitting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.entitytypes.requirementtypes.RequirementTypeResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

public class ProviderRepository {

	public static final ProviderRepository INSTANCE = new ProviderRepository();

	private static final String NS_NAME_START = "http://www.opentosca.org/providers/";

	/**
	 * Pointing to a concrete node template has to be done by putting this node template into a separeate namespace
	 *
	 * The given targetLocation is appended to {@see NS_NAME_START} to gain the namespace.
	 * All NodeTemplates in this namespace and all "lower" namespaces (e.g., starting with that string)
	 * are returned.
	 *
	 * @return All node templates available for the given targetLocation.
	 */

	public List<TTopologyTemplate> getAllTopologyFragmentsForLocationAndOfferingCapability(String targetLocation, TRequirement requirement) {
		QName reqTypeQName = requirement.getType();
		RequirementTypeId reqTypeId = new RequirementTypeId(reqTypeQName);
		RequirementTypeResource reqTypeResource = (RequirementTypeResource) AbstractComponentsResource.getComponentInstaceResource(reqTypeId);
		QName requiredCapabilityType = reqTypeResource.getRequirementType().getRequiredCapabilityType();

		return getAllTopologyFragmentsForLocation(targetLocation).stream()
				.filter(tf -> {
					Optional<TNodeTemplate> nodeTemplate = ModelUtilities.getAllNodeTemplates(tf).stream()
							.filter(nt -> nt.getCapabilities() != null)
							.filter(nt -> nt.getCapabilities().getCapability().stream()
									.anyMatch(cap -> cap.getType().equals(requiredCapabilityType))
							).findAny();
					if (nodeTemplate.isPresent()) {
						return true;
					} else {
						return false;
					}

				})
				.collect(Collectors.toList());

	}

	public List<TTopologyTemplate> getAllTopologyFragmentsForLocation(String targetLocation) {
		String namespaceStr;
		if ("*".equals(targetLocation)) {
			namespaceStr = NS_NAME_START;
		} else {
			namespaceStr = NS_NAME_START + targetLocation.toLowerCase();
		}

		return Repository.INSTANCE.getAllTOSCAComponentIds(ServiceTemplateId.class).stream()
				// get all service templates in the namespace
				.filter(id -> id.getNamespace().getDecoded().toLowerCase().startsWith(namespaceStr))
				// get all contained node templates
				.flatMap(id -> {
					ServiceTemplateResource serviceTemplateResource = (ServiceTemplateResource) AbstractComponentsResource.getComponentInstaceResource(id);
					TTopologyTemplate topologyTemplate = serviceTemplateResource.getServiceTemplate().getTopologyTemplate();
					List<TNodeTemplate> matchedNodeTemplates = topologyTemplate.getNodeTemplateOrRelationshipTemplate().stream()
							.filter(t -> t instanceof TNodeTemplate)
							.map(TNodeTemplate.class::cast)
							.collect(Collectors.toList());

					matchedNodeTemplates.forEach(t -> ModelUtilities.setTargetLabel(t, id.getNamespace().getDecoded().replace(NS_NAME_START, "")));

					return getAllTopologyFragmentsFromServiceTemplate(topologyTemplate).stream();

				})
				.collect(Collectors.toList());
	}

	private List<TTopologyTemplate> getAllTopologyFragmentsFromServiceTemplate (TTopologyTemplate topologyTemplate) {

		List<TTopologyTemplate> topologyFragments = new ArrayList<>();

		Splitting helperFunctions = new Splitting();
		List<TNodeTemplate> nodeTemplatesWithoutIncomingRelationship = helperFunctions.getNodeTemplatesWithoutIncomingHostedOnRelationships(topologyTemplate);
		List<TNodeTemplate> visitedNodeTemplates = new ArrayList<>();

		//It can only be one topology fragment contained in the service template
		if (nodeTemplatesWithoutIncomingRelationship.size() == 1) {
			TDocumentation documentation = new TDocumentation();
			Optional<String> targetLabel = ModelUtilities.getTargetLabel(nodeTemplatesWithoutIncomingRelationship.get(0));
			String label;
			if (!targetLabel.isPresent()) {
				label = "unkown";
			} else {
				label = targetLabel.get();
			}
			documentation.getContent().add("Stack of Node Template " + nodeTemplatesWithoutIncomingRelationship.get(0).getId()
					+ " from Provider Repository " + label);
			topologyTemplate.getDocumentation().add(documentation);
			topologyFragments.add(topologyTemplate);
		} else {
			for (TNodeTemplate nodeWithoutIncomingRel: nodeTemplatesWithoutIncomingRelationship) {
				if (!visitedNodeTemplates.contains(nodeWithoutIncomingRel)) {
					TTopologyTemplate topologyFragment = new TTopologyTemplate();
					TDocumentation documentation = new TDocumentation();
					Optional<String> targetLabel = ModelUtilities.getTargetLabel(nodeWithoutIncomingRel);
					String label;
					if (!targetLabel.isPresent()) {
						label = "unkown";
					} else {
						label = targetLabel.get();
					}
					documentation.getContent().add("Stack of Node Template " + nodeWithoutIncomingRel.getId()
							+ " from Provider Repository " + label);
					topologyFragment.getDocumentation().add(documentation);
					topologyFragment.getNodeTemplateOrRelationshipTemplate().addAll(breadthFirstSearch(nodeWithoutIncomingRel, topologyTemplate));
					topologyFragments.add(topologyFragment);

					topologyFragment.getNodeTemplateOrRelationshipTemplate().stream()
							.filter(et -> et instanceof TNodeTemplate)
							.map(TNodeTemplate.class::cast)
							.forEach(nt -> visitedNodeTemplates.add(nt));
				}
			}
		}
		return topologyFragments;
	}

	private List<TEntityTemplate> breadthFirstSearch(TNodeTemplate nodeTemplate, TTopologyTemplate topologyTemplate) {
		List<TEntityTemplate> topologyFragmentElements = new ArrayList<>();
		topologyFragmentElements.add(nodeTemplate);
		List<TRelationshipTemplate> outgoingRelationships = ModelUtilities.getOutgoingRelationshipTemplates(topologyTemplate, nodeTemplate);

		for (TRelationshipTemplate outgoingRelationship : outgoingRelationships) {
			Object successor = outgoingRelationship.getTargetElement().getRef();
			if (successor instanceof TNodeTemplate) {
				topologyFragmentElements.add(outgoingRelationship);
				topologyFragmentElements.addAll(breadthFirstSearch((TNodeTemplate) successor, topologyTemplate));
			}
		}
		return topologyFragmentElements;
	}

}

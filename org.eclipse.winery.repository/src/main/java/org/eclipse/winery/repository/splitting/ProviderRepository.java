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

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRequirement;
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
    public List<TNodeTemplate> getAllNodeTemplatesForLocation(String targetLocation) {
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
                    List<TNodeTemplate> matchedNodeTemplates = serviceTemplateResource.getServiceTemplate().getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().stream()
                            .filter(t -> t instanceof TNodeTemplate)
                            .map(TNodeTemplate.class::cast)
                            .collect(Collectors.toList());

                    matchedNodeTemplates.stream().forEach(t -> ModelUtilities.setTargetLabel(t, id.getNamespace().getDecoded().replace(NS_NAME_START, "")));

                    return matchedNodeTemplates.stream();

                })
                .collect(Collectors.toList());
    }

    /**
     * @return All node templates available for the given targetLocation
     */
    public List<TNodeTemplate> getAllNodeTemplatesForLocationAndOfferingCapability(String targetLocation, List <TRequirement> requirements) {
        QName reqTypeQName = requirements.get(0).getType();
        RequirementTypeId reqTypeId = new RequirementTypeId(reqTypeQName);
        RequirementTypeResource reqTypeResource = (RequirementTypeResource) AbstractComponentsResource.getComponentInstaceResource(reqTypeId);
        QName requiredCapabilityType = reqTypeResource.getRequirementType().getRequiredCapabilityType();

        return this.getAllNodeTemplatesForLocation(targetLocation).stream()
                .filter(nt -> nt.getCapabilities() != null)
                .filter(nt -> nt.getCapabilities().getCapability().stream()
                        .anyMatch(cap -> cap.getType().equals(requiredCapabilityType))
                ).collect(Collectors.toList());
    }

}

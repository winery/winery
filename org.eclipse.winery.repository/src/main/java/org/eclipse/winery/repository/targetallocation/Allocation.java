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

package org.eclipse.winery.repository.targetallocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.exceptions.AllocationException;
import org.eclipse.winery.repository.targetallocation.criteria.CriteriaCommon;
import org.eclipse.winery.repository.targetallocation.criteria.fulfillpolicies.FulfillPolicies;
import org.eclipse.winery.repository.targetallocation.criteria.minexternalconnections.MinExternalConnections;
import org.eclipse.winery.repository.targetallocation.criteria.minhosts.MinHosts;
import org.eclipse.winery.repository.targetallocation.util.AllocationRequest;
import org.eclipse.winery.repository.targetallocation.util.AllocationUtils;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.LoggerFactory;

/**
 * Main class for the target allocation approach.
 * Receives selected criteria from the GUI via REST
 * -> see wineryTargetAllocation package in Repository UI.
 *
 * Applies the selected {@link Criteria} to the selected topology.
 * Output topologies are saved in the repository under the same namespace and name
 * with the added suffix "-allocated" or "-assigned".
 * The QNames of the created topologies are returned to the GUI.
 */
public class Allocation {

    private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Allocation.class);

    private IRepository repository = RepositoryFactory.getRepository();
    private boolean assignOnly;
    private List<Criteria> criteria = new ArrayList<>();

    public Allocation(AllocationRequest allocationRequest) throws AllocationException {
        assignOnly = allocationRequest.isAssignOnly();
        int outputCap = allocationRequest.getOutputCap();
        for (AllocationRequest.CriteriaRequest criteriaRequest : allocationRequest.getSelectedCriteria()) {
            criteria.add(getCriteria(criteriaRequest, outputCap));
        }
    }

    public List<ServiceTemplateId> allocate(ServiceTemplateId serviceTemplateId) throws Exception {
        AllocationUtils.idCounter = 0;
        TopologyWrapper topology = getTopology(serviceTemplateId);
        Iterator<Criteria> iterator = criteria.iterator();
        Criteria next = iterator.next();

        // assign target labels only
        if (assignOnly) {
            return save(serviceTemplateId, assignOnly(topology, next), "assigned");
        }

        // allocate
        long start = System.currentTimeMillis();
        LOGGER.debug("Allocating using " + criteria.getClass().getSimpleName() + " criteria");
        List<TopologyWrapper> allocatedTopologies = next.allocate(topology);
        LOGGER.debug("Execution time: " + ((System.currentTimeMillis() - start) / 1000d) + "s");

        // filter
        while (iterator.hasNext()) {
            next = iterator.next();
            LOGGER.debug("Filtering using " + next.getClass().getSimpleName() + " criteria");
            allocatedTopologies = next.filter(allocatedTopologies);
            LOGGER.debug("Remaining possible topologies: " + allocatedTopologies.size());
        }
        return save(serviceTemplateId, allocatedTopologies, "allocated");
    }

    private List<TopologyWrapper> assignOnly(TopologyWrapper topology, Criteria criteria) throws Exception {
        if (!(criteria instanceof CriteriaCommon)) {
            throw new AllocationException("Criteria doesn't allow assigning target labels only");
        }
        long start = System.currentTimeMillis();
        LOGGER.debug("Assigning target labels using " + criteria.getClass().getSimpleName() + " criteria");
        List<TopologyWrapper> assignedTopologies = ((CriteriaCommon) criteria).generateTargetLabelTopologies(topology);
        LOGGER.debug("Execution time: " + ((System.currentTimeMillis() - start) / 1000d) + "s");
        return assignedTopologies;
    }

    private List<ServiceTemplateId> save(ServiceTemplateId originalId, List<TopologyWrapper> allocatedTopologies, String suffix)
        throws IOException, AllocationException {
        if (allocatedTopologies.isEmpty()) {
            throw new AllocationException("No topologies were created");
        }
        List<ServiceTemplateId> allocatedIds = new ArrayList<>();

        for (int i = 0; i < allocatedTopologies.size(); i++) {
            // generate id
            ServiceTemplateId allocatedId = new ServiceTemplateId(
                originalId.getNamespace().getDecoded(),
                originalId.getXmlId().getDecoded() + "-" + suffix + (i + 1),
                false);
            repository.forceDelete(allocatedId);
            repository.flagAsExisting(allocatedId);

            // generate and save service template
            TServiceTemplate allocated = createServiceTemplate(allocatedTopologies.get(i));
            repository.setElement(allocatedId, allocated);
            allocatedIds.add(allocatedId);
        }
        LOGGER.debug("Saved " + allocatedIds.size() + " allocated topologies");
        return allocatedIds;
    }

    private Criteria getCriteria(AllocationRequest.CriteriaRequest criteriaRequest, int outputCap) throws AllocationException {
        String criteriaType = criteriaRequest.getCriteria();
        JsonNode params = criteriaRequest.getCriteriaParams();
        switch (criteriaType) {
            case "FulfillPolicies":
                return new FulfillPolicies(params, outputCap);
            case "MinHosts":
                return new MinHosts(params, outputCap);
            case "MinExternalConnections":
                return new MinExternalConnections(params, outputCap);
            default:
                throw new AllocationException("Couldn't find criteria: " + criteriaType);
        }
    }

    private TopologyWrapper getTopology(ServiceTemplateId serviceTemplateId) throws AllocationException {
        TServiceTemplate serviceTemplate = repository.getElement(serviceTemplateId);
        TopologyWrapper topology = new TopologyWrapper(serviceTemplate.getTopologyTemplate());
        checkValidity(topology);
        return topology;
    }

    private TServiceTemplate createServiceTemplate(TopologyWrapper topology) {
        TServiceTemplate serviceTemplate = new TServiceTemplate();
        TTopologyTemplate topologyTemplate = topology.getTopology();

        // reset names for better visual representation
        topologyTemplate.getNodeTemplateOrRelationshipTemplate().forEach(et -> et.setName(et.getType().getLocalPart()));
        // all target labels to lower case for consistency
        for (TNodeTemplate nodeTemplate : topologyTemplate.getNodeTemplates()) {
            Optional<String> targetLabel = ModelUtilities.getTargetLabel(nodeTemplate);
            targetLabel.ifPresent(s -> ModelUtilities.setTargetLabel(nodeTemplate, s));
        }

        serviceTemplate.setTopologyTemplate(topologyTemplate);
        return serviceTemplate;
    }

    private void checkValidity(TopologyWrapper topology) throws AllocationException {
        if (topology.getTopology() == null) {
            throw new AllocationException("No topology present");
        }
        if (topology.getPresentTargetLabels().isEmpty()) {
            throw new AllocationException("No target labels present");
        }
        if (topology.getNodeTemplates().isEmpty()) {
            throw new AllocationException("No Node Templates present");
        }
    }
}

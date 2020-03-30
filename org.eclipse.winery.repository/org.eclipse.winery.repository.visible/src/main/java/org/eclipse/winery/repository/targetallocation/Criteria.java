/*******************************************************************************
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
 *******************************************************************************/

package org.eclipse.winery.repository.targetallocation;

import java.util.List;

import org.eclipse.winery.repository.targetallocation.criteria.minexternalconnections.MinExternalConnections;
import org.eclipse.winery.repository.targetallocation.criteria.minhosts.MinHosts;
import org.eclipse.winery.repository.targetallocation.util.TopologyWrapper;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Class all criteria have to implement for the target allocation approach.
 * A criteria can either create new topologies out af an input topology
 * or filter topologies created by another criteria.
 *
 * @see org.eclipse.winery.repository.targetallocation.criteria.fulfillpolicies.FulfillPolicies
 * @see MinHosts
 * @see MinExternalConnections
 * @see org.eclipse.winery.repository.targetallocation.criteria.CriteriaCommon
 * @see org.eclipse.winery.repository.targetallocation.criteria.CriteriaCached
 */
public abstract class Criteria {

    protected JsonNode params;
    protected int outputCap;

    /**
     * @param params    parameters entered in the Repository UI
     * @param outputCap user preference of how many topologies should be allocated.
     *                  Should be respected to avoid too long runtime
     */
    public Criteria(JsonNode params, int outputCap) {
        this.params = params;
        this.outputCap = outputCap;
    }

    /**
     * Create multiple topologies out of the input topology, where the original node templates have
     * been replaced by node templates of cloud providers.
     *
     * @param topology topology to allocate
     * @return created topologies
     */
    public abstract List<TopologyWrapper> allocate(TopologyWrapper topology) throws Exception;

    /**
     * Simple method to filter topologies crated by other criteria {@link Criteria#allocate(TopologyWrapper)} methods.
     * Should only do basic computations to avoid too long runtime.
     *
     * @param topologies to filter
     * @return filtered topologies
     */
    public abstract List<TopologyWrapper> filter(List<TopologyWrapper> topologies) throws Exception;
}

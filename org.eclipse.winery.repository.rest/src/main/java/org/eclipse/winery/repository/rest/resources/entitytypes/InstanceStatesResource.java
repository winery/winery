/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytypes;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.tosca.TInstanceState;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.InstanceStateApiData;

import org.apache.commons.lang3.StringUtils;

/**
 * Resource for instance states. Used by relationship types and node types
 */
public class InstanceStatesResource {

    private TopologyGraphElementEntityTypeResource typeResource;
    private List<TInstanceState> instanceStates;

    /**
     * @param instanceStates the instanceStates to manage
     * @param typeResource   the type resource, where the instance states are managed. This reference is required to
     *                       fire "persist()" in case of updates
     */
    public InstanceStatesResource(List<TInstanceState> instanceStates, TopologyGraphElementEntityTypeResource typeResource) {
        this.instanceStates = instanceStates;
        this.typeResource = typeResource;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<InstanceStateApiData> getInstanceStates() {
        List<TInstanceState> instanceStates = this.instanceStates;
        ArrayList<InstanceStateApiData> states = new ArrayList<>(instanceStates.size());
        for (TInstanceState instanceState : instanceStates) {
            states.add(new InstanceStateApiData(instanceState.getState()));
        }
        return states;
    }

    @DELETE
    @Path("{instanceState}")
    public Response deleteInstanceState(@PathParam("instanceState") String instanceStateToRemove) {
        if (StringUtils.isEmpty(instanceStateToRemove)) {
            return Response.status(Status.BAD_REQUEST).entity("null instance to remove").build();
        }
        String decodedInstanceState = EncodingUtil.URLdecode(instanceStateToRemove);

        if (!this.instanceStates.removeIf(state -> state.getState().equals(decodedInstanceState))) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return RestUtils.persist(this.typeResource);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addInstanceState(InstanceStateApiData json) {
        String state = json.state;

        if (StringUtils.isEmpty(state)) {
            return Response.notAcceptable(null).build();
        }

        if (this.instanceStates.removeIf(instanceState -> instanceState.getState().equals(state))) {
            // This state is already defined, just return
            return Response.noContent().build();
        }

        instanceStates.add(new TInstanceState(state));

        return RestUtils.persist(this.typeResource);
    }
}

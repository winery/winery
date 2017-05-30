/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytypes;

import java.util.ArrayList;
import java.util.Iterator;
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

import org.eclipse.winery.common.Util;
import org.eclipse.winery.model.tosca.TTopologyElementInstanceStates;
import org.eclipse.winery.model.tosca.TTopologyElementInstanceStates.InstanceState;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.apiData.InstanceStateApiData;

import org.apache.commons.lang3.StringUtils;

/**
 * Resource for instance states <br />
 * Used by relationship types and node types
 */
public class InstanceStatesResource {

	private TopologyGraphElementEntityTypeResource typeResource;
	private TTopologyElementInstanceStates instanceStates;


	/**
	 *
	 * @param instanceStates the instanceStates to manage
	 * @param typeResource the type resource, where the instance states are
	 *            managed. This reference is required to fire "persist()" in
	 *            case of updates
	 */
	public InstanceStatesResource(TTopologyElementInstanceStates instanceStates, TopologyGraphElementEntityTypeResource typeResource) {
		this.instanceStates = instanceStates;
		this.typeResource = typeResource;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
		public List<InstanceStateApiData> getInstanceStates() {
		List<InstanceState> instanceStates = this.instanceStates.getInstanceState();
		ArrayList<InstanceStateApiData> states = new ArrayList<>(instanceStates.size());
		for (InstanceState instanceState : instanceStates) {
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
		instanceStateToRemove = Util.URLdecode(instanceStateToRemove);

		// InstanceState does not override "equals()", therefore we have to manually remove it

		List<InstanceState> instanceStates = this.instanceStates.getInstanceState();
		Iterator<InstanceState> iterator = instanceStates.iterator();
		boolean found = false;
		while (iterator.hasNext() && !found) {
			if (iterator.next().getState().equals(instanceStateToRemove)) {
				found = true;
			}
		}

		if (!found) {
			return Response.status(Status.NOT_FOUND).build();
		}

		iterator.remove();

		return BackendUtils.persist(this.typeResource);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addInstanceState(InstanceStateApiData json) {
		String state = json.state;

		if (StringUtils.isEmpty(state)) {
			return Response.notAcceptable(null).build();
		}

		// InstanceState does not override "equals()", therefore we have to manually check for existance

		List<InstanceState> instanceStates = this.instanceStates.getInstanceState();
		Iterator<InstanceState> iterator = instanceStates.iterator();
		boolean found = false;
		while (iterator.hasNext() && !found) {
			if (iterator.next().getState().equals(state)) {
				found = true;
			}
		}

		if (found) {
			// no error, just return
			return Response.noContent().build();
		}

		InstanceState instanceState = new InstanceState();
		instanceState.setState(state);
		instanceStates.add(instanceState);

		return BackendUtils.persist(this.typeResource);
	}

}

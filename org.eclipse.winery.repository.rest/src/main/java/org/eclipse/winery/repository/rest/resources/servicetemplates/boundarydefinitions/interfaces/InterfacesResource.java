/*******************************************************************************
 * Copyright (c) 2014-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Lukas Harzenetter - post method for interface lists
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions.interfaces;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.TExportedInterface;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdCollectionResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

public class InterfacesResource extends EntityWithIdCollectionResource<ExportedInterfaceResource, TExportedInterface> {

	public InterfacesResource(List<TExportedInterface> list, IPersistable res) {
		super(ExportedInterfaceResource.class, TExportedInterface.class, list, res);
	}

	@Override
	public String getId(TExportedInterface entity) {
		return entity.getName();
	}

	/**
	 * A special handling for TExportedInterface is required as this object uses IDREF, which must not be a string,
	 * but the real object when persisting.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response onPost(TExportedInterface[] exportedInterfacesList) {
		if (exportedInterfacesList == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("a valid XML/JSON element has to be posted").build();
		}
		for (TExportedInterface exportedInterface : exportedInterfacesList) {
			for (TExportedOperation exportedOperation : exportedInterface.getOperation()) {
				if (exportedOperation.getNodeOperation() != null) {
					final String nodeRef = (String) exportedOperation.getNodeOperation().getNodeRef();
					final TNodeTemplate nodeTemplate =
							((ServiceTemplateResource) res).getServiceTemplate()
									.getTopologyTemplate()
									.getNodeTemplates()
									.stream()
									.filter(node -> node.getId().contentEquals(nodeRef))
									.findFirst()
									.get();
					exportedOperation.getNodeOperation().setNodeRef(nodeTemplate);
				} else if (exportedOperation.getRelationshipOperation() != null) {
					final String relationshipRef = (String) exportedOperation.getRelationshipOperation().getRelationshipRef();
					final TRelationshipTemplate relationshipTemplate =
							((ServiceTemplateResource) res).getServiceTemplate()
									.getTopologyTemplate()
									.getRelationshipTemplates()
									.stream()
									.filter(relationship -> relationship.getId().contentEquals(relationshipRef))
									.findFirst()
									.get();
					exportedOperation.getRelationshipOperation().setRelationshipRef(relationshipTemplate);
				} else if (exportedOperation.getPlan() != null) {
					final String planRef = (String) exportedOperation.getPlan().getPlanRef();
					final TPlan planTemplate =
							((ServiceTemplateResource) res).getServiceTemplate()
									.getPlans()
									.getPlan()
									.stream()
									.filter(relationship -> relationship.getId().contentEquals(planRef))
									.findFirst()
									.get();
					exportedOperation.getPlan().setPlanRef(planTemplate);
				} else {
					return Response.status(Response.Status.BAD_REQUEST).entity("No linked operation provided").build();
				}
			}
		}

		this.list.clear();
		this.list.addAll(Arrays.asList(exportedInterfacesList));

		return RestUtils.persist(this.res);
	}

	@Override
	@Path("{id}/")
	public ExportedInterfaceResource getEntityResource(@PathParam("id") String id) {
		return this.getEntityResourceFromEncodedId(id);
	}
}

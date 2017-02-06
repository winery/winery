/*******************************************************************************
 * Copyright (c) 2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions.interfaces;

import java.io.StringWriter;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.model.tosca.TExportedOperation;
import org.eclipse.winery.model.tosca.TExportedOperation.NodeOperation;
import org.eclipse.winery.model.tosca.TExportedOperation.Plan;
import org.eclipse.winery.model.tosca.TExportedOperation.RelationshipOperation;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.resources._support.collections.withid.EntityWithIdResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportedOperationResource extends EntityWithIdResource<TExportedOperation> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExportedOperationResource.class);


	public ExportedOperationResource(IIdDetermination<TExportedOperation> idDetermination, TExportedOperation o, int idx, List<TExportedOperation> list, IPersistable res) {
		super(idDetermination, o, idx, list, res);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getJSON() {
		JsonFactory jsonFactory = new JsonFactory();
		StringWriter sw = new StringWriter();
		try {
			JsonGenerator jg = jsonFactory.createGenerator(sw);
			jg.writeStartObject();
			String type = this.getType();
			jg.writeStringField("type", type);
			jg.writeStringField("ref", this.getReference());
			if ((type != null) && (!type.equals("Plan"))) {
				jg.writeStringField("interfacename", this.getInterfaceName());
				jg.writeStringField("operationname", this.getOperationName());
			}
			jg.writeEndObject();
			jg.close();
		} catch (Exception e) {
			ExportedOperationResource.LOGGER.error(e.getMessage(), e);
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());
		}
		return Response.ok(sw.toString()).build();
	}

	/**
	 *
	 * @return "NodeOperation" | "RelationshipOperation" | "Plan" | null. null
	 *         is returned if no type is set
	 */
	@Path("type")
	@GET
	public String getType() {
		if (this.o.getNodeOperation() != null) {
			return "NodeOperation";
		} else if (this.o.getRelationshipOperation() != null) {
			return "RelationshipOperation";
		} else if (this.o.getPlan() != null) {
			return "Plan";
		} else {
			return null;
		}
	}

	@Path("type")
	@PUT
	public Response setType(String type) {
		switch (type) {
		case "NodeOperation":
			if (this.o.getNodeOperation() == null) {
				// only do something, if the type is really changed
				this.o.setRelationshipOperation(null);
				this.o.setPlan(null);
				NodeOperation no = new NodeOperation();
				this.o.setNodeOperation(no);
			}
			break;
		case "RelationshipOperation":
			if (this.o.getRelationshipOperation() == null) {
				// only do something, if the type is really changed
				this.o.setNodeOperation(null);
				this.o.setPlan(null);
				RelationshipOperation ro = new RelationshipOperation();
				this.o.setRelationshipOperation(ro);
			}
			break;
		case "Plan":
			if (this.o.getPlan() == null) {
				// only do something, if the type is really changed
				this.o.setNodeOperation(null);
				this.o.setRelationshipOperation(null);
				Plan plan = new Plan();
				this.o.setPlan(plan);
			}
			break;
		default:
			return Response.status(Status.BAD_REQUEST).entity("Unknown type " + type).build();
		}
		return BackendUtils.persist(this.res);
	}

	/**
	 * @return null if no reference is set
	 */
	@Path("ref")
	@GET
	public String getReference() {
		if (this.o.getNodeOperation() != null) {
			TNodeTemplate nt = (TNodeTemplate) this.o.getNodeOperation().getNodeRef();
			if (nt == null) {
				return null;
			}
			return nt.getId();
		} else if (this.o.getRelationshipOperation() != null) {
			TRelationshipTemplate rt = (TRelationshipTemplate) this.o.getRelationshipOperation().getRelationshipRef();
			if (rt == null) {
				return null;
			}
			return rt.getId();
		} else if (this.o.getPlan() != null) {
			TPlan plan = (TPlan) this.o.getPlan().getPlanRef();
			if (plan == null) {
				return null;
			}
			return plan.getId();
		} else {
			// no type set -> no reference can be returned
			return null;
		}
	}

	@Path("ref")
	@PUT
	public Response setReference(String ref) {
		TServiceTemplate ste = ((ServiceTemplateResource) this.res).getServiceTemplate();

		// we assume that a correctly set type also means that getX (getNodeOperation, ...) returns non null
		switch (this.getType()) {
		case "NodeOperation":
			TNodeTemplate nodeTemplate = ModelUtilities.resolveNodeTemplate(ste, ref);
			this.o.getNodeOperation().setNodeRef(nodeTemplate);
			break;
		case "RelationshipOperation":
			TRelationshipTemplate relationshipTemplate = ModelUtilities.resolveRelationshipTemplate(ste, ref);
			this.o.getRelationshipOperation().setRelationshipRef(relationshipTemplate);
			break;
		case "Plan":
			TPlan plan = ModelUtilities.resolvePlan(ste, ref);
			this.o.getPlan().setPlanRef(plan);
			break;
		default:
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown type " + this.getType()).build();
		}
		return BackendUtils.persist(this.res);
	}

	@Path("interfacename")
	@GET
	public String getInterfaceName() {
		if (this.o.getNodeOperation() != null) {
			return this.o.getNodeOperation().getInterfaceName();
		} else if (this.o.getRelationshipOperation() != null) {
			return this.o.getRelationshipOperation().getInterfaceName();
		} else if (this.o.getPlan() != null) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("A plan does not carry an interface").build());
		} else {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unsupported state of ExportedOperation").build());
		}
	}

	@Path("interfacename")
	@PUT
	public Response setInterfaceName(String interfacename) {
		if (this.o.getNodeOperation() != null) {
			this.o.getNodeOperation().setInterfaceName(interfacename);
		} else if (this.o.getRelationshipOperation() != null) {
			this.o.getRelationshipOperation().setInterfaceName(interfacename);
		} else if (this.o.getPlan() != null) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("A plan does not carry an interface").build());
		} else {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("No type set").build());
		}
		return BackendUtils.persist(this.res);
	}

	@Path("operationname")
	@GET
	public String getOperationName() {
		if (this.o.getNodeOperation() != null) {
			return this.o.getNodeOperation().getOperationName();
		} else if (this.o.getRelationshipOperation() != null) {
			return this.o.getRelationshipOperation().getOperationName();
		} else if (this.o.getPlan() != null) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("A plan does not carry an operation").build());
		} else {
			throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unsupported state of ExportedOperation").build());
		}
	}

	@Path("operationname")
	@PUT
	public Response setOperationName(String name) {
		if (this.o.getNodeOperation() != null) {
			this.o.getNodeOperation().setOperationName(name);
		} else if (this.o.getRelationshipOperation() != null) {
			this.o.getRelationshipOperation().setOperationName(name);
		} else if (this.o.getPlan() != null) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("A plan does not carry an operation").build());
		} else {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("No type set").build());
		}
		return BackendUtils.persist(this.res);
	}

}

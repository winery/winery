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
package org.eclipse.winery.repository.resources.entitytypes.relationshiptypes;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipType.SourceInterfaces;
import org.eclipse.winery.model.tosca.TRelationshipType.TargetInterfaces;
import org.eclipse.winery.model.tosca.TRelationshipType.ValidSource;
import org.eclipse.winery.model.tosca.TRelationshipType.ValidTarget;
import org.eclipse.winery.model.tosca.TTopologyElementInstanceStates;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.entitytypes.InstanceStatesResource;
import org.eclipse.winery.repository.resources.entitytypes.TopologyGraphElementEntityTypeResource;
import org.eclipse.winery.repository.resources.interfaces.InterfacesResource;

import com.sun.jersey.api.view.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationshipTypeResource extends TopologyGraphElementEntityTypeResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipTypeResource.class);


	public RelationshipTypeResource(RelationshipTypeId id) {
		super(id);
	}

	@Path("implementations/")
	public ImplementationsOfOneRelationshipTypeResource getImplementations() {
		return new ImplementationsOfOneRelationshipTypeResource((RelationshipTypeId) this.id);
	}

	@Path("visualappearance/")
	public VisualAppearanceResource getVisualAppearanceResource() {
		return new VisualAppearanceResource(this, this.getElement().getOtherAttributes(), (RelationshipTypeId) this.id);
	}

	@Path("instancestates/")
	public InstanceStatesResource getInstanceStatesResource() {
		TTopologyElementInstanceStates instanceStates = this.getRelationshipType().getInstanceStates();
		if (instanceStates == null) {
			// if an explicit (empty) list does not exist, create it
			instanceStates = new TTopologyElementInstanceStates();
			this.getRelationshipType().setInstanceStates(instanceStates);
		}
		return new InstanceStatesResource(this.getRelationshipType().getInstanceStates(), this);
	}

	@Path("sourceinterfaces/")
	public InterfacesResource getSourceInterfaces() {
		SourceInterfaces interfaces = this.getRelationshipType().getSourceInterfaces();
		if (interfaces == null) {
			interfaces = new SourceInterfaces();
			this.getRelationshipType().setSourceInterfaces(interfaces);
		}
		return new InterfacesResource(this, interfaces.getInterface(), "source");
	}

	@Path("targetinterfaces/")
	public InterfacesResource getTargetInterfaces() {
		TargetInterfaces interfaces = this.getRelationshipType().getTargetInterfaces();
		if (interfaces == null) {
			interfaces = new TargetInterfaces();
			this.getRelationshipType().setTargetInterfaces(interfaces);
		}
		return new InterfacesResource(this, interfaces.getInterface(), "target");
	}

	@Path("validendings/")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getHTML() {
		Viewable viewable = new Viewable("/jsp/entitytypes/relationshiptypes/validendings.jsp", this);
		return Response.ok().entity(viewable).build();
	}

	@Path("validsource")
	@GET
	public String getValidSource() {
		ValidSource validSource;
		if (((validSource = this.getRelationshipType().getValidSource()) == null) || (validSource.getTypeRef() == null)) {
			return null;
		}
		return this.getRelationshipType().getValidSource().getTypeRef().toString();
	}

	@Path("validsource")
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response setValidSource(String typeRef) {
		ValidSource vs = new ValidSource();
		QName qname = QName.valueOf(typeRef);
		vs.setTypeRef(qname);
		this.getRelationshipType().setValidSource(vs);
		return BackendUtils.persist(this);
	}

	@Path("validtarget")
	@GET
	public String getValidTarget() {
		ValidTarget validTarget;
		if (((validTarget = this.getRelationshipType().getValidTarget()) == null) || (validTarget.getTypeRef() == null)) {
			return null;
		}
		return this.getRelationshipType().getValidTarget().getTypeRef().toString();
	}

	@Path("validtarget")
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response setValidTarget(String typeRef) {
		ValidTarget vt = new ValidTarget();
		QName qname = QName.valueOf(typeRef);
		vt.setTypeRef(qname);
		this.getRelationshipType().setValidTarget(vt);
		return BackendUtils.persist(this);
	}

	/**
	 * Required for validendings.jsp
	 */
	public Collection<NodeTypeId> getPossibleValidEndings() {
		return Repository.INSTANCE.getAllTOSCAComponentIds(NodeTypeId.class);
	}

	/**
	 * Convenience method to avoid casting at the caller's side.
	 */
	public TRelationshipType getRelationshipType() {
		return (TRelationshipType) this.getElement();
	}

	@Override
	protected TExtensibleElements createNewElement() {
		return new TRelationshipType();
	}
}

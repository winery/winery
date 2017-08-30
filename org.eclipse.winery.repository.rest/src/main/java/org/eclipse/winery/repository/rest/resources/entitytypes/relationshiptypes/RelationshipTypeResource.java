/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Nicole Keppler - changes for angular2 validsourcesandtargets
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.relationshiptypes;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipType.SourceInterfaces;
import org.eclipse.winery.model.tosca.TRelationshipType.TargetInterfaces;
import org.eclipse.winery.model.tosca.TRelationshipType.ValidSource;
import org.eclipse.winery.model.tosca.TRelationshipType.ValidTarget;
import org.eclipse.winery.model.tosca.TTopologyElementInstanceStates;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.datatypes.select2.Select2DataItem;
import org.eclipse.winery.repository.rest.resources.apiData.ValidEndingsApiData;
import org.eclipse.winery.repository.rest.resources.apiData.ValidEndingsApiDataSet;
import org.eclipse.winery.repository.rest.resources.entitytypes.InstanceStatesResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.TopologyGraphElementEntityTypeResource;
import org.eclipse.winery.repository.rest.resources.interfaces.InterfacesResource;

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

	/*
	 * Select2DataItem used to send valid source as json to frontend.
	 * Mapping from Select2DataItem.Id as ValidSource.namespace and
	 * from Select2DataItem.Text as Validsource.localname
	 */
	@Path("validsourcesandtargets/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ValidEndingsApiData getValidEndings() {
		ValidEndingsApiData validEndingsData = new ValidEndingsApiData();
		//ToDo enable validSource as RequirementType (not-yet-implemented)
		ValidSource validSource;
		if (((validSource = this.getRelationshipType().getValidSource()) == null) || (validSource.getTypeRef() == null)) {
			validEndingsData.validSource = null;
		} else {
			validSource = this.getRelationshipType().getValidSource();
			validEndingsData.validSource = new ValidEndingsApiDataSet(
				"nodeType",
				new Select2DataItem(validSource.getTypeRef().toString(), validSource.getTypeRef().getLocalPart()));
		}
		// ToDo enable validTarget as CapabilityType (not-yet-implemented)
		ValidTarget validTarget;
		if (((validTarget = this.getRelationshipType().getValidTarget()) == null) || (validTarget.getTypeRef() == null)) {
			validEndingsData.validTarget = null;
		} else {
			validTarget = this.getRelationshipType().getValidTarget();
			validEndingsData.validTarget = new ValidEndingsApiDataSet(
				"nodeType",
				new Select2DataItem(validTarget.getTypeRef().toString(), validTarget.getTypeRef().getLocalPart()));
		}
		return validEndingsData;
	}

	@Path("validsourcesandtargets/")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putValidEnding(ValidEndingsApiData validEndings) {
		if (validEndings.validSource == null || validEndings.validSource.validDataSet == null || validEndings.validSource.validEndingsSelectionType == null) {
			this.getRelationshipType().setValidSource(null);
		} else {
			ValidSource vs = new ValidSource();
			QName qname = QName.valueOf(validEndings.validSource.validDataSet.getId());
			vs.setTypeRef(qname);
			this.getRelationshipType().setValidSource(vs);
		}
		if (validEndings.validTarget == null || validEndings.validTarget.validDataSet == null || validEndings.validTarget.validEndingsSelectionType == null) {
			this.getRelationshipType().setValidTarget(null);
		} else {
			ValidTarget vt = new ValidTarget();
			QName qnameVT = QName.valueOf(validEndings.validTarget.validDataSet.getId());
			vt.setTypeRef(qnameVT);
			this.getRelationshipType().setValidTarget(vt);
		}

		return RestUtils.persist(this);
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

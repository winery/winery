/*******************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytypes.relationshiptypes;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.IdNames;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.model.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.TRelationshipType.ValidSource;
import org.eclipse.winery.model.tosca.TRelationshipType.ValidTarget;
import org.eclipse.winery.model.tosca.TTopologyElementInstanceStates;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.GenericDirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.datatypes.select2.Select2DataItem;
import org.eclipse.winery.repository.rest.resources._support.GenericFileResource;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
import org.eclipse.winery.repository.rest.resources.apiData.ValidEndingsApiData;
import org.eclipse.winery.repository.rest.resources.apiData.ValidEndingsApiDataSet;
import org.eclipse.winery.repository.rest.resources.entitytypes.InstanceStatesResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.InterfaceDefinitionsResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.TopologyGraphElementEntityTypeResource;
import org.eclipse.winery.repository.rest.resources.interfaces.InterfacesResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationshipTypeResource extends TopologyGraphElementEntityTypeResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipTypeResource.class);

    public RelationshipTypeResource(RelationshipTypeId id) {
        super(id);
    }

    @GET
    @Path("implementations/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<QNameApiData> getImplementations() {
        return RestUtils.getAllElementsReferencingGivenType(RelationshipTypeImplementationId.class, this.id.getQName());
    }

    @Path("appearance")
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

    @Path("interfaces/")
    public InterfacesResource getInterfaces() {
        TInterfaces interfaces = this.getRelationshipType().getInterfaces();
        if (interfaces == null) {
            interfaces = new TInterfaces();
            this.getRelationshipType().setInterfaces(interfaces);
        }
        return new InterfacesResource(this, interfaces.getInterface(), "yaml");
    }

    @Path("sourceinterfaces/")
    public InterfacesResource getSourceInterfaces() {
        TInterfaces interfaces = this.getRelationshipType().getSourceInterfaces();
        if (interfaces == null) {
            interfaces = new TInterfaces();
            this.getRelationshipType().setSourceInterfaces(interfaces);
        }
        return new InterfacesResource(this, interfaces.getInterface(), "source");
    }

    @Path("targetinterfaces/")
    public InterfacesResource getTargetInterfaces() {
        TInterfaces interfaces = this.getRelationshipType().getTargetInterfaces();
        if (interfaces == null) {
            interfaces = new TInterfaces();
            this.getRelationshipType().setTargetInterfaces(interfaces);
        }
        return new InterfacesResource(this, interfaces.getInterface(), "target");
    }

    @Path("interfacedefinitions")
    public InterfaceDefinitionsResource InterfaceDefinitionsResource() {
        return new InterfaceDefinitionsResource(this);
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

    @Path("files")
    public GenericFileResource files() {
        DirectoryId dir = new GenericDirectoryId(this.getId(), IdNames.FILES_DIRECTORY);
        return new GenericFileResource(dir);
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

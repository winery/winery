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
package org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes;

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

import org.eclipse.winery.model.ids.IdNames;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.tosca.TArtifact;
import org.eclipse.winery.model.tosca.TArtifacts;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TInterfaces;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeType.RequirementDefinitions;
import org.eclipse.winery.model.tosca.TTopologyElementInstanceStates;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;
import org.eclipse.winery.repository.datatypes.ids.elements.GenericDirectoryId;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.GenericFileResource;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
import org.eclipse.winery.repository.rest.resources.entitytypes.InstanceStatesResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.InterfaceDefinitionsResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.TopologyGraphElementEntityTypeResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.reqandcapdefs.CapabilityDefinitionsResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.reqandcapdefs.RequirementDefinitionsResource;
import org.eclipse.winery.repository.rest.resources.interfaces.InterfacesResource;

public class NodeTypeResource extends TopologyGraphElementEntityTypeResource {

    public NodeTypeResource(NodeTypeId id) {
        super(id);
    }

    /**
     * Convenience method to avoid casting at the caller's side.
     */
    public TNodeType getNodeType() {
        return (TNodeType) this.getElement();
    }

    /**
     * sub-resources
     **/
    @GET
    @Path("implementations/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<QNameApiData> getImplementations() {
        return RestUtils.getAllElementsReferencingGivenType(NodeTypeImplementationId.class, this.id.getQName());
    }

    @Path("instancestates/")
    public InstanceStatesResource getInstanceStatesResource() {
        TTopologyElementInstanceStates instanceStates = this.getNodeType().getInstanceStates();
        if (instanceStates == null) {
            // if an explicit (empty) list does not exist, create it
            instanceStates = new TTopologyElementInstanceStates();
            this.getNodeType().setInstanceStates(instanceStates);
        }
        return new InstanceStatesResource(instanceStates, this);
    }

    @Path("interfaces/")
    public InterfacesResource getInterfaces() {
        TInterfaces interfaces = this.getNodeType().getInterfaces();
        if (interfaces == null) {
            interfaces = new TInterfaces();
            this.getNodeType().setInterfaces(interfaces);
        }
        return new InterfacesResource(this, interfaces.getInterface(), "nodeType");
    }

    @Path("requirementdefinitions/")
    public RequirementDefinitionsResource getRequirementDefinitions() {
        RequirementDefinitions definitions = this.getNodeType().getRequirementDefinitions();
        if (definitions == null) {
            definitions = new RequirementDefinitions();
            this.getNodeType().setRequirementDefinitions(definitions);
        }
        return new RequirementDefinitionsResource(this, definitions.getRequirementDefinition());
    }

    @Path("capabilitydefinitions/")
    public CapabilityDefinitionsResource getCapabilityDefinitions() {
        TNodeType.CapabilityDefinitions definitions = this.getNodeType().getCapabilityDefinitions();
        if (definitions == null) {
            definitions = new TNodeType.CapabilityDefinitions();
            this.getNodeType().setCapabilityDefinitions(definitions);
        }
        return new CapabilityDefinitionsResource(this, definitions.getCapabilityDefinition());
    }

    @Path("appearance")
    public VisualAppearanceResource getVisualAppearanceResource() {
        return new VisualAppearanceResource(this, this.getElement().getOtherAttributes(), (NodeTypeId) this.id);
    }

    @Path("interfacedefinitions")
    public InterfaceDefinitionsResource InterfaceDefinitionsResource() {
        return new InterfaceDefinitionsResource(this);
    }

    @GET
    @Path("artifacts/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TArtifact> getArtifacts() {
        TArtifacts artifacts = this.getNodeType().getArtifacts();
        if (artifacts == null) {
            return new ArrayList<>();
        }
        return artifacts.getArtifact();
    }

    @POST
    @Path("artifacts/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addArtifact(TArtifact artifact) {
        TNodeType nodeType = this.getNodeType();
        if (nodeType.getArtifacts() == null) {
            nodeType.setArtifacts(new TArtifacts());
        }
        this.getNodeType().getArtifacts().addArtifact(artifact);
        return RestUtils.persist(this);
    }

    @DELETE
    @Path("artifacts/f/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteArtifact(@PathParam("name") String name) {
        TNodeType nodeType = this.getNodeType();
        if (nodeType.getArtifacts() == null) {
            nodeType.setArtifacts(new TArtifacts());
        }
        TArtifact artifact = null;
        for (TArtifact item : nodeType.getArtifacts().getArtifact()) {
            if (name.equalsIgnoreCase(item.getName())) {
                artifact = item;
            }
        }
        if (artifact == null) {
            return Response.noContent().build();
        }
        List<TArtifact> artifacts = nodeType.getArtifacts().getArtifact();
        artifacts.remove(artifact);
        this.getNodeType().getArtifacts().setArtifact(artifacts);
        this.uploadArtifact(name).deleteFile(artifact.getFile(), null);
        return RestUtils.persist(this);
    }

    @Path("artifacts/{name}")
    public GenericFileResource uploadArtifact(@PathParam("name") String name) {
        DirectoryId dir = new GenericDirectoryId(this.getId(), IdNames.FILES_DIRECTORY);
        DirectoryId files = new GenericDirectoryId(dir, name);
        return new GenericFileResource(files);
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TNodeType();
    }
}

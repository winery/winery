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
package org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeType.Interfaces;
import org.eclipse.winery.model.tosca.TNodeType.RequirementDefinitions;
import org.eclipse.winery.model.tosca.TTopologyElementInstanceStates;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
import org.eclipse.winery.repository.rest.resources.entitytypes.InstanceStatesResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.TopologyGraphElementEntityTypeResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.reqandcapdefs.CapabilityDefinitionsResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.reqandcapdefs.RequirementDefinitionsResource;
import org.eclipse.winery.repository.rest.resources.interfaces.InterfacesResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
        Interfaces interfaces = this.getNodeType().getInterfaces();
        if (interfaces == null) {
            interfaces = new Interfaces();
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

    @Path("visualappearance/")
    public VisualAppearanceResource getVisualAppearanceResource() {
        return new VisualAppearanceResource(this, this.getElement().getOtherAttributes(), (NodeTypeId) this.id);
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TNodeType();
    }
}

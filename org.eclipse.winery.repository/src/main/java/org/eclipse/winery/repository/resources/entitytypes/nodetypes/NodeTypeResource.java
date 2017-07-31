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
package org.eclipse.winery.repository.resources.entitytypes.nodetypes;

import javax.ws.rs.Path;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeType.Interfaces;
import org.eclipse.winery.model.tosca.TNodeType.RequirementDefinitions;
import org.eclipse.winery.model.tosca.TTopologyElementInstanceStates;
import org.eclipse.winery.repository.resources.entitytypes.InstanceStatesResource;
import org.eclipse.winery.repository.resources.entitytypes.TopologyGraphElementEntityTypeResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.reqandcapdefs.CapabilityDefinitionsResource;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.reqandcapdefs.RequirementDefinitionsResource;
import org.eclipse.winery.repository.resources.interfaces.InterfacesResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeTypeResource extends TopologyGraphElementEntityTypeResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(NodeTypeResource.class);


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

	@Path("implementations/")
	public ImplementationsOfOneNodeTypeResource getImplementations() {
		return new ImplementationsOfOneNodeTypeResource((NodeTypeId) this.id);
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

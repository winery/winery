/*******************************************************************************
 * Copyright (c) 2012-2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Nico Rusam and Alexander Stifel - HAL support
 *******************************************************************************/
package org.eclipse.winery.repository.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.datatypes.select2.Select2DataItem;
import org.eclipse.winery.repository.datatypes.select2.Select2OptGroup;
import org.eclipse.winery.repository.resources.entitytypes.properties.PropertiesDefinitionResource;

import com.theoryinpractise.halbuilder.api.Representation;

public abstract class EntityTypeResource extends AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal {
	
	protected EntityTypeResource(TOSCAComponentId id) {
		super(id);
	}
	
	@Override
	protected void copyIdToFields() {
		TEntityType entityType = this.getEntityType();
		entityType.setTargetNamespace(this.getId().getNamespace().getDecoded());
		entityType.setName(this.getId().getXmlId().getDecoded());
	}
	
	/**
	 * Convenience method to avoid casting. Required by
	 * PropertiesDefinitionResource's jsp
	 */
	public TEntityType getEntityType() {
		return (TEntityType) this.element;
	}
	
	/**
	 * Models PropertiesDefinition
	 */
	@Path("propertiesdefinition/")
	public PropertiesDefinitionResource getPropertiesDefinitionResource() {
		return new PropertiesDefinitionResource(this);
	}
	
	/**
	 * Used by children to implement getListOfAllInstances()
	 */
	protected SortedSet<Select2OptGroup> getListOfAllInstances(Class<? extends TOSCAComponentId> clazz) {
		Map<String, Select2OptGroup> idx = new HashMap<>();
		
		Collection<? extends TOSCAComponentId> instanceIds = BackendUtils.getAllElementsRelatedWithATypeAttribute(clazz, this.id.getQName());
		
		for (TOSCAComponentId instanceId : instanceIds) {
			String groupText = instanceId.getNamespace().getDecoded();
			Select2OptGroup optGroup = idx.get(groupText);
			if (optGroup == null) {
				optGroup = new Select2OptGroup(groupText);
				idx.put(groupText, optGroup);
			}
			
			String text = BackendUtils.getName(instanceId);
			Select2DataItem item = new Select2DataItem(instanceId.getQName().toString(), text);
			optGroup.addItem(item);
		}
		
		// convert the index to the real result
		SortedSet<Select2OptGroup> res = new TreeSet<>();
		for (Select2OptGroup optGroup : idx.values()) {
			res.add(optGroup);
		}
		
		return res;
	}
	
	/**
	 * Returns an array suitable for processing in a {@code select2} field See
	 * {@link http://ivaynberg.github.io/select2}
	 * 
	 * Each element: {id: "{ns}localname", text: "name/id"}
	 */
	@Path("instances/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SortedSet<Select2OptGroup> getListOfAllInstances() {
		Response res = Response.status(Status.INTERNAL_SERVER_ERROR).entity("not yet implemented").build();
		throw new WebApplicationException(res);
	}
	
	@Override
	protected Representation fillHALRepresentation(Representation res) {
		res = super.fillHALRepresentation(res);
		//@formatter:off

		res = res.withLink("propertiesdefinition/", "propertiesdefinition/");
		//@formatter:on
		return res;
	}
}

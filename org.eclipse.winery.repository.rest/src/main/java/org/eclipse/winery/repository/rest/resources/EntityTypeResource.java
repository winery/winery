/**
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.rest.resources;

import java.util.Collection;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.eclipse.winery.repository.rest.datatypes.select2.Select2DataWithOptGroups;
import org.eclipse.winery.repository.rest.datatypes.select2.Select2OptGroup;
import org.eclipse.winery.repository.rest.resources.entitytypes.properties.PropertiesDefinitionResource;

public abstract class EntityTypeResource extends AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal {

	protected EntityTypeResource(DefinitionsChildId id) {
		super(id);
	}

	/**
	 * Convenience method to avoid casting.
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
	protected SortedSet<Select2OptGroup> getListOfAllInstances(Class<? extends DefinitionsChildId> clazz) throws RepositoryCorruptException {
		Select2DataWithOptGroups data = new Select2DataWithOptGroups();

		Collection<? extends DefinitionsChildId> instanceIds = RepositoryFactory.getRepository().getAllElementsReferencingGivenType(clazz, this.id.getQName());

		for (DefinitionsChildId instanceId : instanceIds) {
			String groupText = instanceId.getNamespace().getDecoded();
			String text = BackendUtils.getName(instanceId);
			data.add(groupText, instanceId.getQName().toString(), text);
		}

		return data.asSortedSet();
	}

	/**
	 * Returns an array suitable for processing in a {@code select2} field See <a href="http://ivaynberg.github.io/select2">http://ivaynberg.github.io/select2</a>
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
}

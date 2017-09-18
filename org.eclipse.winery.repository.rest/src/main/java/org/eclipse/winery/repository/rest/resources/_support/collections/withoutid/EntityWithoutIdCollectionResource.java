/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources._support.collections.withoutid;

import java.lang.reflect.Constructor;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.rest.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.EntityCollectionResource;

import com.sun.jersey.api.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class managing a list of entities. It is intended to manage subresources, where the TOSCA specification did not
 * specify a unique key. Currently, the hashCode of the XML String representation is used. If other representation
 * should be used, the method {@code getEntityResource} has to be overriden.
 *
 * @param <EntityResourceT> the resource modeling the entity
 * @param <EntityT>         the entity type of single items in the list
 */
public abstract class EntityWithoutIdCollectionResource<EntityResourceT extends EntityWithoutIdResource<EntityT>, EntityT> extends EntityCollectionResource<EntityResourceT, EntityT> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EntityWithoutIdCollectionResource.class);


	/**
	 * {@inheritDoc}
	 */
	public EntityWithoutIdCollectionResource(Class<EntityResourceT> entityResourceTClazz, Class<EntityT> entityTClazz, List<EntityT> list, IPersistable res) {
		super(entityResourceTClazz, entityTClazz, list, res);
	}

	/**
	 * Method searching the list for an id with the hashcode instead of getId(EntityT)
	 */
	@Override
	@Path("{id}/")
	public EntityResourceT getEntityResource(@PathParam("id") String id) {
		id = Util.URLdecode(id);
		int idInt;
		try {
			idInt = Integer.parseInt(id);
		} catch (java.lang.NumberFormatException e) {
			throw new NotFoundException(id + " is not a valid id");
		}
		EntityT entity = null;
		int idx = -1;
		for (EntityT c : this.list) {
			idx++;
			// speed optimization - instead of using getId() we directly use the hash code
			int hash = BackendUtils.getXMLAsString(c).hashCode();
			if (hash == idInt) {
				entity = c;
				break;
			}
		}
		if (entity == null) {
			throw new NotFoundException();
		} else {
			return this.getEntityResourceInstance(entity, idx);
		}
	}

	@Override
	public String getId(EntityT entity) {
		return IdDeterminationWithHashCode.INSTANCE.getId(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EntityResourceT getEntityResourceInstance(EntityT entity, int idx) {
		Constructor<EntityResourceT> constructor;
		try {
			constructor = this.entityResourceTClazz.getConstructor(this.entityTClazz, int.class, List.class, AbstractComponentInstanceResource.class);
		} catch (Exception e) {
			try {
				constructor = this.entityResourceTClazz.getConstructor(this.entityTClazz, int.class, List.class, IPersistable.class);
			} catch (NoSuchMethodException | SecurityException e1) {
				EntityWithoutIdCollectionResource.LOGGER.debug("Could not get constructor", e);
				throw new IllegalStateException(e);
			}
		}
		EntityResourceT newInstance;
		try {
			newInstance = constructor.newInstance(entity, idx, this.list, this.res);
		} catch (Exception e) {
			EntityWithoutIdCollectionResource.LOGGER.debug("Could not instantiate class", e);
			throw new IllegalStateException(e);
		}
		return newInstance;
	}
}

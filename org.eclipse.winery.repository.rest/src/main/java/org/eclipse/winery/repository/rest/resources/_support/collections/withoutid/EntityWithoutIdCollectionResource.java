/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources._support.collections.withoutid;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.NotFoundException;

import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.EntityCollectionResource;

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

    public EntityWithoutIdCollectionResource(Class<EntityResourceT> entityResourceTClazz, Class<EntityT> entityTClazz, List<EntityT> list, IPersistable res) {
        super(entityResourceTClazz, entityTClazz, list, res);
    }

    /**
     * Method searching the list for an id with the hashcode instead of getId(EntityT)
     */
    @Override
    protected EntityResourceT getEntityResourceFromDecodedId(String id) {
        Objects.requireNonNull(id);
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
            int hash = BackendUtils.getXMLAsString(c, requestRepository).hashCode();
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

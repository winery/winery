/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources._support.collections.withid;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.NotFoundException;

import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.EntityCollectionResource;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EntityWithIdCollectionResource<EntityResourceT extends EntityWithIdResource<EntityT>, EntityT> extends EntityCollectionResource<EntityResourceT, EntityT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityWithIdCollectionResource.class);


    /**
     * {@inheritDoc}
     */
    public EntityWithIdCollectionResource(Class<EntityResourceT> entityResourceTClazz, Class<EntityT> entityTClazz, List<EntityT> list, IPersistable res) {
        super(entityResourceTClazz, entityTClazz, list, res);
    }

    /**
     * Each CollectionResource has to implement the id getting by itself as
     * TOSCA XSD does not provide a general purpose id fetching mechanism
     */
    @Override
    public abstract String getId(EntityT entity);

    @Override
    protected EntityResourceT getEntityResourceInstance(EntityT entity, int idx) {
        Constructor<EntityResourceT> constructor;
        try {
            constructor = this.entityResourceTClazz.getConstructor(IIdDetermination.class, this.entityTClazz, int.class, List.class, this.res.getClass());
        } catch (Exception e) {
            try {
                constructor = this.entityResourceTClazz.getConstructor(IIdDetermination.class, this.entityTClazz, int.class, List.class, IPersistable.class);
            } catch (Exception e2) {
                EntityWithIdCollectionResource.LOGGER.debug("Could not get constructor", e);
                EntityWithIdCollectionResource.LOGGER.debug("res.getClass() was {}", this.res.getClass());
                throw new IllegalStateException(e2);
            }
        }
        EntityResourceT newInstance;
        try {
            newInstance = constructor.newInstance(this, entity, idx, this.list, this.res);
        } catch (Exception e) {
            EntityWithIdCollectionResource.LOGGER.debug("Could not instantiate class", e);
            throw new IllegalStateException(e);
        }
        return newInstance;
    }

    @Override
    protected EntityResourceT getEntityResourceFromDecodedId(String id) {
        Objects.requireNonNull(id);
        EntityT entity = null;
        int idx = -1;
        for (EntityT c : this.list) {
            idx++;
            String cId = this.getId(c);
            if (cId.equals(id)) {
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
}

/*******************************************************************************
 * Copyright (c) 2012-2015 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources._support.collections;

import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.List;

/**
 * Class to hold a single entity residing in a list of entities
 *
 * @param <EntityT> the entity type contained in the list
 */
public abstract class EntityResource<EntityT> {

    // This is non-final as a "PUT" may update the object
    // it might be unnecessary to update this object as the resource is created at each request
    // We update the reference nevertheless to be safe if the resource is used in another context
    protected EntityT o;

    protected final int idx;

    protected final List<EntityT> list;

    protected final IPersistable res;

    protected IIdDetermination<EntityT> idDetermination;
    
    protected final IRepository requestRepository = RepositoryFactory.getRepository();


    /**
     * @param idDetermination the object offering determination of an id of
     *                        EntityT. May be null. If null, then setIdDetermination(obj)
     *                        has to be called to enable this class functioning properly
     * @param o               the object this resource is representing
     * @param idx             the index of the object in the list
     * @param list            the list, where the object is stored in
     * @param res             the resource the object/list belongs to
     */
    public EntityResource(IIdDetermination<EntityT> idDetermination, EntityT o, int idx, List<EntityT> list, IPersistable res) {
        this.idDetermination = idDetermination;
        this.o = o;
        this.idx = idx;
        this.list = list;
        this.res = res;
    }

    /**
     * Quick hack for AbstractReqOrCapDefResource which is itself an
     * IIdDetermination
     */
    protected final void setIdDetermination(IIdDetermination<EntityT> idDetermination) {
        this.idDetermination = idDetermination;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJSON() {
        assert (this.o != null);
        return Response.ok().entity(this.o).build();
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    @SuppressWarnings("unchecked")
    public Response getXML() {
        assert (this.o != null);
        // Utils.getXML has to be used as Jersey can only serialize XMLRootElements
        return RestUtils.getXML((Class<EntityT>) this.o.getClass(), this.o, requestRepository);
    }

    /**
     * Replaces the whole entity by the given entity
     * <p>
     * As we use the hash code as index, the index changes when the resource is
     * updated. This is not in line with REST. The alternative implementation is
     * to use the index in the list as resource identification. That changes at
     * each modification of the list itself (if elements are deleted / inserted
     * before the current entry). When using the hash value, users may
     * concurrently edit items and the list may also be updated
     *
     * @return the new id.
     */
    @PUT
    @Consumes( {MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.TEXT_PLAIN)
    public Response setValue(EntityT o) {
        this.list.set(this.idx, o);
        this.o = o;
        return CollectionsHelper.persist(this.res, this.idDetermination, o, false);
    }

    @DELETE
    public Response onDelete() {
        try {
            this.list.remove(this.idx);
        } catch (IndexOutOfBoundsException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Could not delete entity, even if it should exist").build();
        }
        return RestUtils.persist(this.res);
    }

}

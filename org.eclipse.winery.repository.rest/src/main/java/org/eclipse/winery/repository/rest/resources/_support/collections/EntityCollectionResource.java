/*******************************************************************************
 * Copyright (c) 2012-2014 Contributors to the Eclipse Foundation
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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.common.json.JacksonProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class managing a list of entities. It is intended to manage subresources, which are stored in a list. Either all
 * entities have a unique key given by the TOSCA specification (subclass EntityWithIdCollectionResource) or a unique key
 * is generated (subclass EntityWithoutIdCollectionResource)
 *
 * @param <EntityResourceT> the resource modeling the entity
 * @param <EntityT>         the entity type of single items in the list
 */
public abstract class EntityCollectionResource<EntityResourceT extends EntityResource<EntityT>, EntityT> implements IIdDetermination<EntityT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityCollectionResource.class);

    protected final List<EntityT> list;

    protected final IPersistable res;

    protected final Class<EntityT> entityTClazz;

    protected final Class<EntityResourceT> entityResourceTClazz;

    /**
     * @param entityTClazz the class of EntityT. Required as it is not possible to call new EntityT (see
     *                     http://stackoverflow.com/a/1090488/873282)
     * @param list         the list of entities contained in this resource. Has to be typed <Object> as not all TOSCA
     *                     elements in the specification inherit from TExtensibleElements
     * @param res          the main resource the list is belonging to. Required for persistence.
     */
    public EntityCollectionResource(Class<EntityResourceT> entityResourceTClazz, Class<EntityT> entityTClazz, List<EntityT> list, IPersistable res) {
        this.entityResourceTClazz = entityResourceTClazz;
        this.entityTClazz = entityTClazz;
        this.list = list;
        this.res = res;
    }

    public List<String> getListOfAllEntityIdsAsList() {
        return this.list.stream().map(et -> this.getId(et)).collect(Collectors.toList());
    }

    /**
     * XML is currently not possible. One has to use Utils.getXMLAsString((Class<EntityT>) this.o.getClass(), this.o,
     * false);
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllEntityResources(@QueryParam(value = "noId") boolean noId) {
        return this.getListOfAllEntityIdsAsList().stream()
            .map(this::getEntityResourceFromDecodedId)
            .map((EntityResourceT res) -> {
                String id = this.getId(res.o);
                // some objects already have an id field
                // we set it nevertheless, because it might happen that the name of the id field is not "id", but something else (such as "name")

                // general method, same as with data binding
                JsonNode jsonNode = JacksonProvider.mapper.valueToTree(res.o);
                if (!noId) {
                    ((ObjectNode) jsonNode).put("id", id);
                }
                try {
                    return JacksonProvider.mapper.writeValueAsString(jsonNode);
                } catch (JsonProcessingException e) {
                    throw new WebApplicationException(e);
                }
            })
            .collect(Collectors.joining(",", "[", "]"));
    }

    protected abstract EntityResourceT getEntityResourceFromDecodedId(String id);

    protected EntityResourceT getEntityResourceFromEncodedId(String id) {
        return this.getEntityResourceFromDecodedId(EncodingUtil.URLdecode(Objects.requireNonNull(id)));
    }

    /**
     * Needs to be implemented at the children to get the SWAGGER tooling working. Each implementation needs to be
     * annotated with <code>@Path("{id}/")</code> and call <code>getEntityResourceFromEncodedId</code>
     */
    public abstract EntityResourceT getEntityResource(String id);

    /**
     * @param entity the entity to create a resource for
     * @param idx    the index in the list
     * @return the resource managing the given entity
     */
    protected abstract EntityResourceT getEntityResourceInstance(EntityT entity, int idx);

    /**
     * Adds a new entity
     * <p>
     * In case the element already exists, we return "CONFLICT"
     */
    @POST
    public Response addNewElement(EntityT entity) {
        if (entity == null) {
            return Response.status(Status.BAD_REQUEST).entity("a valid XML/JSON element has to be posted").build();
        }
        if (this.alreadyContains(entity)) {
            // we do not replace the element, but replace it
            return Response.status(Status.CONFLICT).build();
        }
        this.list.add(entity);
        return CollectionsHelper.persist(this.res, this, entity, true);
    }

    @Override
    public abstract String getId(EntityT entity);

    /**
     * Checks for containment of e in the list. <code>equals</code> is not used as most EntityT do not offer a valid
     * implementation
     *
     * @return true if list already contains e.
     */
    public boolean alreadyContains(EntityT e) {
        String id = this.getId(e);
        for (EntityT el : this.list) {
            if (this.getId(el).equals(id)) {
                // break loop
                // we found an equal list item
                return true;
            }
        }
        // all items checked: nothing equal contained
        return false;
    }
}

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
package org.eclipse.winery.repository.rest.resources.admin.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.winery.model.ids.EncodingUtil;
import org.eclipse.winery.model.ids.admin.TypesId;
import org.eclipse.winery.repository.rest.datatypes.TypeWithShortName;
import org.eclipse.winery.repository.rest.datatypes.select2.Select2DataItem;
import org.eclipse.winery.repository.rest.resources.admin.AbstractAdminResource;
import org.eclipse.winery.repository.rest.resources.apiData.TypeWithShortNameApiData;

import org.apache.commons.lang3.StringUtils;

/**
 * Handles longname/shortname by using properties
 * <p>
 * FIXME: This class does NOT support dynamic reloading of the underlying
 * Configuration instance
 */
public abstract class AbstractTypesManager extends AbstractAdminResource {

    @Context
    private UriInfo uriInfo;

    // hashes from a long type string to the type object holding complete type data
    private final HashMap<String, TypeWithShortName> hashTypeStringToType;


    public AbstractTypesManager(TypesId id) {
        super(id);
        // now, this.configuration is filled with stored data

        // copy over information from configuration to internal data structure
        this.hashTypeStringToType = new HashMap<>();
        Iterator<String> keys = this.configuration.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = this.configuration.getString(key);
            TypeWithShortName typeInfo = new TypeWithShortName(key, value);
            this.hashTypeStringToType.put(key, typeInfo);
        }
    }

    protected void addData(String longName, String shortName) {
        TypeWithShortName t = new TypeWithShortName(longName, shortName);
        this.addData(t);
    }

    /**
     * Adds data to the internal data structure WITHOUT persisting it
     * <p>
     * More or less a quick hack to enable adding default types without
     * persisting them in the storage
     *
     * @param t the type to add
     */
    private void addData(TypeWithShortName t) {
        this.hashTypeStringToType.put(t.getType(), t);
    }

    public synchronized void addTypeWithShortName(TypeWithShortName type) {
        this.addData(type);
        this.configuration.setProperty(type.getType(), type.getShortName());
    }

    /**
     * Removes a type. Will not remove a type added by "addData"
     */
    @DELETE
    @Path("{type}")
    public Response removeTypeWithResponse(@PathParam("type") String type) {
        type = EncodingUtil.URLdecode(type);
        if (this.configuration.containsKey(type)) {
            this.hashTypeStringToType.remove(type);
            this.configuration.clearProperty(type);
            return Response.noContent().build();
        } else if (this.hashTypeStringToType.containsKey(type)) {
            // predefined types may not be deleted
            // this branch is hit at types added via addData (e.g., predefined plantypes)
            return Response.status(Status.FORBIDDEN).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    /**
     * Returns a sorted list of all available types
     */
    public Collection<TypeWithShortName> getTypes() {
        return new TreeSet<TypeWithShortName>(this.hashTypeStringToType.values());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object getTypesAsJSONArrayList(@QueryParam("ngSelect") String ngSelect) {
        if (ngSelect == null) {
            return this.getTypes();
        } else {
            // ngSelect mode
            SortedSet<Select2DataItem> res = new TreeSet<>();
            for (TypeWithShortName t : this.getTypes()) {
                Select2DataItem item = new Select2DataItem(t.getType(), t.getShortName());
                res.add(item);
            }
            return res;
        }
    }

    /**
     * <b>SIDEEFFECT:</b> If there currently isn't any short type name, it is
     * created
     */
    public TypeWithShortName getTypeWithShortName(String typeString) {
        TypeWithShortName t = this.hashTypeStringToType.get(typeString);
        if (t == null) {
            String shortName = this.getShortName(typeString);
            t = new TypeWithShortName(typeString, shortName);
            this.addTypeWithShortName(t);
        }
        return t;
    }

    /**
     * <b>SIDEEFFECT:</b> If there currently isn't any short type name, it is
     * created
     */
    public String getShortName(String typeString) {
        TypeWithShortName type = this.hashTypeStringToType.get(typeString);
        String res;
        if (type == null) {
            // happens if artifact type is not registered in artifacttypes.list
            // (DATAFILENAME)
            res = typeString;
        } else {
            res = type.getShortName();
        }
        return res;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTypeMapping(TypeWithShortNameApiData newType) {
        if (StringUtils.isEmpty(newType.shortName)) {
            return Response.status(Status.BAD_REQUEST).entity("shortName has to be given").build();
        }
        if (StringUtils.isEmpty(newType.type)) {
            return Response.status(Status.BAD_REQUEST).entity("type has to be given").build();
        }
        String shortName = EncodingUtil.URLdecode(newType.shortName);
        String type = EncodingUtil.URLdecode(newType.type);
        TypeWithShortName tws = new TypeWithShortName(type, shortName);
        this.addTypeWithShortName(tws);
        return Response.noContent().build();
    }

}

/*******************************************************************************
 * Copyright (c) 2012-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.reqandcapdefs;

import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.TConstraint;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.ConstraintsResource;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypeResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bundles common properties of TRequirementDefinition and TCapabilityDefinition
 * <p>
 * We agreed in the project not to modify org.eclipse.winery.model.tosca. Therefore, this resource models the common
 * properties of a TRequirementDefinition and a TCapabilityDefinition
 */
public abstract class AbstractReqOrCapDefResource<ReqOrCapDef> extends EntityWithIdResource<ReqOrCapDef> implements IIdDetermination<ReqOrCapDef> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReqOrCapDefResource.class);

    protected NodeTypeResource parent;

    // the capability or the requirement
    protected Object reqOrCapDef;

    private List<TConstraint> constraints;

    /**
     * @param constraints additional parameter (in comparison to the constructor of EntityWithIdResource) as we require
     *                    that sublist for the constrinats sub resource
     */
    public AbstractReqOrCapDefResource(IIdDetermination<ReqOrCapDef> idDetermination, ReqOrCapDef reqOrCapDef, int idx, List<ReqOrCapDef> list, NodeTypeResource res, List<TConstraint> constraints) {
        super(idDetermination, reqOrCapDef, idx, list, res);
        assert ((reqOrCapDef instanceof TRequirementDefinition) || (reqOrCapDef instanceof TCapabilityDefinition));
        this.parent = res;
        this.reqOrCapDef = reqOrCapDef;
        this.constraints = constraints;
    }

    @GET
    @Path("name")
    public String getName() {
        return (String) this.invokeGetter("getName");
    }

    static String getName(Object reqOrCapDef) {
        return (String) AbstractReqOrCapDefResource.invokeGetter(reqOrCapDef, "getName");
    }

    @GET
    @Path("lowerbound")
    public int getLowerBound() {
        return (int) this.invokeGetter("getLowerBound");
    }

    @GET
    @Path("upperbound")
    public String getUpperBound() {
        return (String) this.invokeGetter("getUpperBound");
    }

    @PUT
    @Path("name")
    public Response setName(@FormParam(value = "name") String name) {
        // TODO: type check - see also min/max Instance of a node template
        this.invokeSetter("setName", name);
        return RestUtils.persist(this.parent);
    }

    @PUT
    @Path("lowerbound")
    public Response setLowerBound(@FormParam(value = "lowerbound") String value) {
        // TODO: type check
        this.invokeSetter("setLowerBound", value);
        return RestUtils.persist(this.parent);
    }

    @PUT
    @Path("upperbound")
    public Response setUpperBound(@FormParam(value = "upperbound") String value) {
        // TODO: type check
        this.invokeSetter("setUpperBound", value);
        return RestUtils.persist(this.parent);
    }

    @Path("constraints/")
    public ConstraintsResource getConstraintsResource() {
        return new ConstraintsResource(this.constraints, this.parent);
    }

    private static Object invokeGetter(Object reqOrCapDef, String getterName) {
        Method method;
        Object res;
        try {
            method = reqOrCapDef.getClass().getMethod(getterName);
            res = method.invoke(reqOrCapDef);
        } catch (Exception e) {
            AbstractReqOrCapDefResource.LOGGER.error("Could not invoke getter {}", getterName, e);
            throw new IllegalStateException(e);
        }
        return res;
    }

    private Object invokeGetter(String getterName) {
        return AbstractReqOrCapDefResource.invokeGetter(this.reqOrCapDef, getterName);
    }

    /**
     * Quick hack method for RequirementOrCapabilityDefinitionsResource
     */
    static void invokeSetter(Object reqOrCapDef, String setterName, Object value) {
        Method method;
        try {
            method = reqOrCapDef.getClass().getMethod(setterName, value.getClass());
            method.invoke(reqOrCapDef, value);
        } catch (Exception e) {
            AbstractReqOrCapDefResource.LOGGER.error("Could not invoke setter {}", setterName, e);
            throw new IllegalStateException(e);
        }
    }

    private void invokeSetter(String setterName, Object value) {
        AbstractReqOrCapDefResource.invokeSetter(this.reqOrCapDef, setterName, value);
    }

    @GET
    @Path("type")
    @Produces(MediaType.TEXT_PLAIN)
    public String getTypeAsString() {
        return this.getType().toString();
    }

    /**
     * required by the JSP.
     * <p>
     * Therefore, we have two getters for the type: QName for the JSP and String for REST clients
     */
    public abstract QName getType();
}

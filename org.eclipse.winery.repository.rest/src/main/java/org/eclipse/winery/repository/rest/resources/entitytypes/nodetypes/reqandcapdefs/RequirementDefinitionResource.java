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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TConstraint;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TRequirementDefinition.Constraints;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypeResource;

public final class RequirementDefinitionResource extends AbstractReqOrCapDefResource<TRequirementDefinition> {

    /**
     * Constructor has to follow the pattern of EnetityTResource as the constructor is invoked by reflection in
     * EntityWithIdcollectionResource
     *
     * @param res the resource this req def is nested in. Has to be of Type "NodeTypeResource". Due to the
     *            implementation of org.eclipse.winery .repository.resources._support.collections.
     *            withid.EntityWithIdCollectionResource .getEntityResourceInstance(EntityT, int), we have to use
     *            "AbstractComponentInstanceResource" as type
     */
    public RequirementDefinitionResource(IIdDetermination<TRequirementDefinition> idDetermination, TRequirementDefinition reqDef, int idx, List<TRequirementDefinition> list, AbstractComponentInstanceResource res) {
        super(idDetermination, reqDef, idx, list, (NodeTypeResource) res, RequirementDefinitionResource.getConstraints(reqDef));
    }

    /**
     * Quick fix to avoid internal server error when opening RequirementDefinitions Tab
     */
    public RequirementDefinitionResource(IIdDetermination<TRequirementDefinition> idDetermination, TRequirementDefinition reqDef, int idx, List<TRequirementDefinition> list, IPersistable res) {
        this(idDetermination, reqDef, idx, list, (AbstractComponentInstanceResource) res);
    }

    public TRequirementDefinition getDefinition() {
        return (TRequirementDefinition) this.reqOrCapDef;
    }

    /**
     * Fetch the list of constraints from the given definition. If the list does not exist, the list is created an
     * stored in the given def
     */
    public static List<TConstraint> getConstraints(TRequirementDefinition def) {
        Constraints constraints = def.getConstraints();
        if (constraints == null) {
            constraints = new Constraints();
            def.setConstraints(constraints);
        }
        return constraints.getConstraint();
    }

    public QName getType() {
        return this.getDefinition().getRequirementType();
    }

    @PUT
    @Path("type")
    public Response setType(@FormParam(value = "type") String value) {
        QName qname = QName.valueOf(value);
        this.getDefinition().setRequirementType(qname);
        return RestUtils.persist(this.parent);
    }

    @Override
    public String getId(TRequirementDefinition e) {
        return e.getName();
    }

    // The following methods support the YAML specs
    @GET
    @Path("capability")
    @Produces(MediaType.APPLICATION_JSON)
    public QNameApiData getCapability() {
        return QNameApiData.fromQName(this.getDefinition().getCapability());
    }

    @PUT
    @Path("capability")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setCapability(QNameApiData data) {
        QName qName = data.asQName();
        this.getDefinition().setCapability(qName);
        return RestUtils.persist(this.parent);
    }

    @GET
    @Path("node")
    @Produces(MediaType.APPLICATION_JSON)
    public QNameApiData getNode() {
        return QNameApiData.fromQName(this.getDefinition().getNode());
    }

    @PUT
    @Path("node")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setNode(QNameApiData data) {
        QName qName = data.asQName();
        this.getDefinition().setNode(qName);
        return RestUtils.persist(this.parent);
    }

    @GET
    @Path("relationship")
    @Produces(MediaType.APPLICATION_JSON)
    public QNameApiData getRelationship() {
        return QNameApiData.fromQName(this.getDefinition().getRelationship());
    }

    @PUT
    @Path("relationship")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setRelationship(QNameApiData data) {
        QName qName = data.asQName();
        this.getDefinition().setRelationship(qName);
        return RestUtils.persist(this.parent);
    }
}

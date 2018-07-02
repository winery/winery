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
package org.eclipse.winery.repository.rest.resources._support;

import org.eclipse.winery.model.tosca.TConstraint;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.collections.withoutid.EntityWithoutIdResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypeResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class ConstraintResource extends EntityWithoutIdResource<TConstraint> {

    /**
     * @param constraint the current constraint value
     * @param list       the list this constraint belongs to
     * @param res        the node type resource this constraint belongs to. Required for saving
     */
    public ConstraintResource(TConstraint constraint, int idx, List<TConstraint> list, NodeTypeResource res) {
        super(constraint, idx, list, res);
    }

    /**
     * Required for collectionResource
     *
     * @throws ClassCastException of !(res instanceof NodeTypeResource)
     */
    public ConstraintResource(TConstraint constraint, int idx, List<TConstraint> list, AbstractComponentInstanceResource res) {
        this(constraint, idx, list, (NodeTypeResource) res);
    }

    private TConstraint getConstraint() {
        return this.o;
    }

    @GET
    @Path("type")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConstraintType() {
        return this.getConstraint().getConstraintType();
    }

    @PUT
    @Path("type")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response putConstraintType(String constraintType) {
        this.getConstraint().setConstraintType(constraintType);
        return RestUtils.persist(this.res);
    }
}

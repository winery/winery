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
package org.eclipse.winery.repository.rest.resources.entitytypes;

import org.eclipse.winery.common.ids.definitions.TopologyGraphElementEntityTypeId;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * specifies the methods required by implementations.jsp
 */
public abstract class ImplementationsOfOneType {

    private final TopologyGraphElementEntityTypeId typeId;


    public ImplementationsOfOneType(TopologyGraphElementEntityTypeId typeId) {
        this.typeId = typeId;
    }

    public TopologyGraphElementEntityTypeId getTypeId() {
        return this.typeId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public abstract Response getJSON();

    /**
     * @return a list of type implementations implementing the associated node type
     */
    public abstract String getImplementationsTableData();

    /**
     * The string used as URL part
     */
    public abstract String getType();

    /**
     * The string displayed to the user
     */
    public abstract String getTypeStr();
}

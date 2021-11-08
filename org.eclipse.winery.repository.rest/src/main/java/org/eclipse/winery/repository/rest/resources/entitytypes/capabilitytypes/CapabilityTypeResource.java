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
package org.eclipse.winery.repository.rest.resources.entitytypes.capabilitytypes;

import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.model.tosca.TCapabilityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
import org.eclipse.winery.repository.rest.resources.apiData.ValidTypesListApiData;
import org.eclipse.winery.repository.rest.resources.entitytypes.EntityTypeResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CapabilityTypeResource extends EntityTypeResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CapabilityTypeResource.class);

    /**
     * Constructor has to be public because of test cases
     */
    public CapabilityTypeResource(CapabilityTypeId id) {
        super(id);
    }

    /**
     * Convenience method to avoid casting at the caller's side.
     *
     * @return the CapabilityType object this resource is representing
     */
    public TCapabilityType getCapabilityType() {
        return (TCapabilityType) this.getElement();
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TCapabilityType();
    }

    @GET
    @Path("constraints/")
    public ValidTypesListApiData getValidSourceTypes() {
        return new ValidTypesListApiData(getCapabilityType().getValidNodeTypes());
    }

    @PUT
    @Path("constraints/")
    public Response saveValidSourceTypes(ValidTypesListApiData newValidSourceTypes) {
        TCapabilityType t = this.getCapabilityType();
        t.setValidNodeTypes(newValidSourceTypes
            .getNodes()
            .stream()
            .map(QNameApiData::asQName)
            .collect(Collectors.toList()));

        return RestUtils.persist(this);
    }
}

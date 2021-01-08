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
package org.eclipse.winery.repository.rest.resources.entitytypes.requirementtypes;

import org.eclipse.winery.model.ids.definitions.RequirementTypeId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TRequirementType;
import org.eclipse.winery.repository.rest.resources.entitytypes.EntityTypeResource;

import javax.ws.rs.Path;

public class RequirementTypeResource extends EntityTypeResource {

    public RequirementTypeResource(RequirementTypeId id) {
        super(id);
    }

    /**
     * Convenience method to avoid casting at the caller's side.
     */
    public TRequirementType getRequirementType() {
        return (TRequirementType) this.getElement();
    }

    @Override
    protected TExtensibleElements createNewElement() {
        return new TRequirementType();
    }

    @Path("requiredcapabilitytype/")
    public RequiredCapabilityTypeResource getRequiredCapabilityTypeResource() {
        return new RequiredCapabilityTypeResource(this);
    }
}

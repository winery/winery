/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.datatypes.ids.elements.VisualAppearanceId;
import org.eclipse.winery.repository.rest.resources.apiData.VisualsApiData;

public class VisualAppearanceResource extends GenericVisualAppearanceResource {

    /**
     * @param otherAttributes the other attributes of the node/relationship type
     * @param id              the id of this subresource required for storing the images
     */
    public VisualAppearanceResource(AbstractComponentInstanceResource res, Map<QName, String> otherAttributes, DefinitionsChildId id) {
        super(res, otherAttributes, new VisualAppearanceId(id));
    }

    public String getColor() {
        return null;
    }

    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public VisualsApiData getJsonData() {
        return new VisualsApiData(this);
    }
}

/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.apiData.boundarydefinitions;

import java.util.List;

import org.eclipse.winery.model.tosca.TPropertyMapping;
import org.eclipse.winery.model.tosca.TServiceTemplate;

public class PropertyMappingsApi {

    public List<TPropertyMapping> propertyMappings;

    @SuppressWarnings("unused") // required for deserialization 
    public PropertyMappingsApi() {
    }

    public PropertyMappingsApi(TServiceTemplate ste) {
        if (ste.getBoundaryDefinitions() != null && ste.getBoundaryDefinitions().getProperties() != null) {
            this.propertyMappings = ste.getBoundaryDefinitions().getProperties().getPropertyMappings();
        }
    }
}

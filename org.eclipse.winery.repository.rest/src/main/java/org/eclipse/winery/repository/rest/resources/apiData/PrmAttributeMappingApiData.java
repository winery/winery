/*******************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.apiData;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMapping;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMappingType;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PrmAttributeMappingApiData extends AbstractPrmMappingElement {

    public OTAttributeMappingType type;
    public String detectorProperty;
    public String refinementProperty;

    public PrmAttributeMappingApiData() {
    }

    @JsonIgnore
    public OTAttributeMapping createTPrmPropertyMapping(TEntityTemplate detectorNodeTemplate, TEntityTemplate refinementNodeTemplate) {
        return new OTAttributeMapping(new OTAttributeMapping.Builder(this.id)
            .setDetectorElement(detectorNodeTemplate)
            .setRefinementElement(refinementNodeTemplate)
            .setType(this.type)
            .setDetectorProperty(this.detectorProperty)
            .setRefinementProperty(this.refinementProperty)
        );
    }
}

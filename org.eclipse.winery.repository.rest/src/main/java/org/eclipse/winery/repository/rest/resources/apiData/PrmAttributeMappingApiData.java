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

package org.eclipse.winery.repository.rest.resources.apiData;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPrmAttributeMapping;
import org.eclipse.winery.model.tosca.TPrmAttributeMappingType;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PrmAttributeMappingApiData extends AbstractPrmMappingElement {

    public TPrmAttributeMappingType type;
    public String detectorProperty;
    public String refinementProperty;

    public PrmAttributeMappingApiData() {
    }

    @JsonIgnore
    public TPrmAttributeMapping createTPrmPropertyMapping(TNodeTemplate detectorNodeTemplate, TNodeTemplate refinementNodeTemplate) {
        TPrmAttributeMapping mapping = new TPrmAttributeMapping();
        mapping.setId(this.id);
        mapping.setDetectorNode(detectorNodeTemplate);
        mapping.setRefinementNode(refinementNodeTemplate);
        mapping.setType(this.type);
        mapping.setDetectorProperty(this.detectorProperty);
        mapping.setRefinementProperty(this.refinementProperty);

        return mapping;
    }
}

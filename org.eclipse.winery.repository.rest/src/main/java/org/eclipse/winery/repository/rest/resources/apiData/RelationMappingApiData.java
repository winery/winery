/********************************************************************************
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

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.extensions.OTRelationDirection;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RelationMappingApiData extends AbstractPrmMappingElement {

    public OTRelationDirection direction;
    public QName relationType;
    public QName validSourceOrTarget;

    public RelationMappingApiData() {
    }

    @JsonIgnore
    public OTRelationMapping createTRelationMapping(TNodeTemplate detectorNodeTemplate, TNodeTemplate refinementNodeTemplate) {
        return new OTRelationMapping(new OTRelationMapping.Builder(this.id)
            .setDetectorElement(detectorNodeTemplate)
            .setRefinementElement(refinementNodeTemplate)
            .setDirection(this.direction)
            .setRelationType(this.relationType)
            .setValidSourceOrTarget(this.validSourceOrTarget));
    }
}

/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.refinementmodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.Path;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.extensions.OTBehaviorPatternMapping;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;

public class PatternRefinementModelResource extends TopologyFragmentRefinementModelResource {

    public PatternRefinementModelResource(DefinitionsChildId id) {
        super(id);
    }

    @Override
    public OTPatternRefinementModel getTRefinementModel() {
        return (OTPatternRefinementModel) this.getElement();
    }

    @Path("behaviorpatternmappings")
    public BehaviorPatternMappingsResource getBehaviorPatternMappings() {
        List<OTBehaviorPatternMapping> behaviorPatternMappings = this.getTRefinementModel().getBehaviorPatternMappings();

        if (Objects.isNull(behaviorPatternMappings)) {
            behaviorPatternMappings = new ArrayList<>();
            this.getTRefinementModel().setBehaviorPatternMappings(behaviorPatternMappings);
        }
        return new BehaviorPatternMappingsResource(this, behaviorPatternMappings);
    }
}

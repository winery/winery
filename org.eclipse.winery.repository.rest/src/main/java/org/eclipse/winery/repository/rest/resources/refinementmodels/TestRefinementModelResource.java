/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

import javax.ws.rs.Path;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.extensions.OTTestRefinementModel;
import org.eclipse.winery.repository.rest.resources._support.AbstractRefinementModelResource;
import org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates.TopologyTemplateResource;

public class TestRefinementModelResource extends AbstractRefinementModelResource {

    public TestRefinementModelResource(DefinitionsChildId id) {
        super(id);
    }

    @Override
    public OTTestRefinementModel getTRefinementModel() {
        return (OTTestRefinementModel) this.getElement();
    }

    @Override
    protected OTTestRefinementModel createNewElement() {
        return new OTTestRefinementModel(new OTTestRefinementModel.Builder());
    }

    @Path("testfragment")
    public TopologyTemplateResource getRefinementTopologyResource() {
        return new TopologyTemplateResource(this, this.getTRefinementModel().getRefinementTopology(), REFINEMENT_TOPOLOGY);
    }
}

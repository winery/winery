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
package org.eclipse.winery.model.adaptation.substitution.refinement.patterns;

import org.eclipse.winery.model.ids.extensions.PatternRefinementModelId;
import org.eclipse.winery.model.adaptation.substitution.SubstitutionUtils;
import org.eclipse.winery.model.adaptation.substitution.refinement.DefaultRefinementChooser;
import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementChooser;
import org.eclipse.winery.model.adaptation.substitution.refinement.topologyrefinement.TopologyFragmentRefinement;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

public class PatternRefinement extends TopologyFragmentRefinement {

    public PatternRefinement(RefinementChooser refinementChooser) {
        super(refinementChooser, PatternRefinementModelId.class, "refined");
    }

    public PatternRefinement() {
        this(new DefaultRefinementChooser());
    }

    @Override
    public boolean getLoopCondition(TTopologyTemplate topology) {
        return SubstitutionUtils.containsPatterns(topology.getNodeTemplates(), this.nodeTypes);
    }
}

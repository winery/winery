/********************************************************************************
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

package org.eclipse.winery.model.substitution.pattern.refinement;

import java.util.List;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

public class DefaultPatternRefinementChooser implements PatternRefinementChooser {
    public PatternRefinementCandidate choosePatternRefinement(List<PatternRefinementCandidate> candidates,
                                                              ServiceTemplateId refinementServiceTemplate,
                                                              TTopologyTemplate currentTopology) {
        return candidates.get(0);
    }
}

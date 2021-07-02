/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologygraph.matching.patterndetection;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;

public class ComponentPatternCandidate {

    private final TNodeTemplate detectorElement;
    private final TNodeTemplate refinementElement;
    private final OTPatternRefinementModel otherPrm;
    private final TNodeTemplate otherDetectorElement;

    public ComponentPatternCandidate(TNodeTemplate detectorElement, TNodeTemplate refinementElement,
                                     OTPatternRefinementModel otherPrm, TNodeTemplate otherDetectorElement) {
        this.detectorElement = detectorElement;
        this.refinementElement = refinementElement;
        this.otherPrm = otherPrm;
        this.otherDetectorElement = otherDetectorElement;
    }

    public TNodeTemplate getDetectorElement() {
        return detectorElement;
    }

    public TNodeTemplate getRefinementElement() {
        return refinementElement;
    }

    public OTPatternRefinementModel getOtherPrm() {
        return otherPrm;
    }

    public TNodeTemplate getOtherDetectorElement() {
        return otherDetectorElement;
    }
}

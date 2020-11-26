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
import { Directive, Input, OnInit } from '@angular/core';
import { ShowOnFeatureDirective } from './wineryRepository.showOnFeature.direct';

export enum FeatureEnum {
    Accountability = 'accountability', Completion = 'completion', Compliance = 'compliance', EdmmModeling = 'edmmModeling',
    FreezeAndDefrost = 'freezeAndDefrost', ManagementFeatureEnrichment = 'managementFeatureEnrichment', NFV = 'nfv', PatternRefinement = 'patternRefinement',
    ProblemDetection = 'problemDetection', Radon = 'radon', Splitting = 'splitting', MultiParticipant = 'multiParticipant', TestRefinement = 'testRefinement',
    TopologyFragmentRefinementModel = 'topologyFragmentRefinementModel', Placement = 'placement', updateTemplates = 'updateTemplates', Yaml = 'yaml'
}

@Directive({
    selector: '[wineryRepositoryFeatureToggle]'
})
export class FeatureToggleDirective extends ShowOnFeatureDirective implements OnInit {

    @Input('wineryRepositoryFeatureToggle') data: string | string[];

    ngOnInit() {
        this.featuresToShow = this.data;
        super.ngOnInit();
    }
}

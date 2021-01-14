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
import { Component } from '@angular/core';
import { InstanceModelService } from './instanceModel.service';
import { PatternRefinementModel } from '../refinement/refinementWebSocket.service';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { TopologyRendererActions } from '../../redux/actions/topologyRenderer.actions';

@Component({
    selector: 'winery-instance-model',
    templateUrl: './instanceModel.component.html',
    providers: [
        InstanceModelService
    ]
})
export class InstanceModelComponent {

    constructor(private ngRedux: NgRedux<IWineryState>,
                private rendererActions: TopologyRendererActions,
                private service: InstanceModelService) {
    }

    start() {
        this.service.start();
    }

    onHoverOver(candidate: PatternRefinementModel) {
        this.ngRedux.dispatch(this.rendererActions.highlightNodes(candidate.nodeIdsToBeReplaced));
    }
}

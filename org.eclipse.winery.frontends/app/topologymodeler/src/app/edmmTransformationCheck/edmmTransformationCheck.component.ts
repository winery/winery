/********************************************************************************
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
import { Component } from '@angular/core';
import { EdmmTechnologyTransformationCheck, EdmmTransformationCheckService } from './edmmTransformationCheck.service';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { Subscription } from 'rxjs';
import { TTopologyTemplate } from '../models/ttopology-template';

@Component({
    selector: 'winery-edmm-transformation-check',
    templateUrl: 'edmmTransformationCheck.component.html',
    styleUrls: ['edmmTransformationCheck.component.css'],
    providers: [
        EdmmTransformationCheckService
    ]
})
export class EdmmTransformationCheckComponent {

    loading = false;
    checkResult: EdmmTechnologyTransformationCheck[];

    private changeSubscription: Subscription;
    private topologyTemplate: TTopologyTemplate;
    private numberRelations = 0;
    private numberNodes = 0;

    constructor(private service: EdmmTransformationCheckService,
                private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions) {
        this.ngRedux.select(state => state.topologyRendererState.buttonsState.edmmTransformationCheck)
            .subscribe(value => {
                if (value) {
                    this.init();
                } else {
                    this.hide();
                }
            });
        this.ngRedux.select(currentState => currentState.wineryState.currentJsonTopology)
            .subscribe(topologyTemplate => this.topologyTemplate = topologyTemplate);
    }

    init() {
        this.changeSubscription = this.ngRedux.select(state => state.wineryState.currentJsonTopology)
            .subscribe(element => {
                if (element.relationshipTemplates && element.relationshipTemplates.length !== this.numberRelations
                    || element.nodeTemplates && element.nodeTemplates.length !== this.numberNodes) {
                    this.numberRelations = element.relationshipTemplates.length;
                    this.numberNodes = element.nodeTemplates.length;
                    this.doTransformationCheck();
                }
            });
    }

    hide() {
        if (this.changeSubscription) {
            this.changeSubscription.unsubscribe();
        }
        this.changeSubscription = null;
    }

    doTransformationCheck() {
        if (!this.loading) {
            this.loading = true;
            this.service.doTransformationCheck(this.topologyTemplate)
                .subscribe(technologyChecks => {
                    this.loading = false;
                    this.checkResult = technologyChecks;
                });
        }
    }

    onHoverOver(candidate: EdmmTechnologyTransformationCheck) {
        const idList = candidate.unsupportedComponents.map(component =>
            this.topologyTemplate.nodeTemplates.find(node => node.name === component).id
        );
        this.ngRedux.dispatch(this.actions.highlightNodes(idList));
    }

    hoverOut() {
        this.ngRedux.dispatch(this.actions.highlightNodes([]));
    }

    getColorClass(candidate: EdmmTechnologyTransformationCheck): string {
        if (candidate.supports > 0.95) {
            return 'applicable';
        } else if (candidate.supports >= 0.5) {
            return 'partlyApplicable';
        }

        return 'notSupported';
    }
}

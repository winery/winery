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

import { Injectable } from '@angular/core';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyTemplateUtil } from '../models/topologyTemplateUtil';
import { TTopologyTemplate } from '../models/ttopology-template';
import { WineryActions } from '../redux/actions/winery.actions';
import { LiveModelingStates } from '../models/enums';
import { LiveModelingActions } from '../redux/actions/live-modeling.actions';
import { BackendService } from './backend.service';

@Injectable()
export class TopologyService {
    private currentJsonTopologyTemplate: TTopologyTemplate;
    private lastSavedJsonTopologyTemplate: TTopologyTemplate;
    private deployedJsonTopologyTemplate: TTopologyTemplate;
    private enabled = false;
    private liveModelingState: LiveModelingStates;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private wineryActions: WineryActions,
                private liveModelingActions: LiveModelingActions,
                private backendService: BackendService) {
        this.ngRedux.select((state) => {
            return state.wineryState.currentJsonTopology;
        })
            .subscribe((topologyTemplate) => {
                this.currentJsonTopologyTemplate = topologyTemplate;
                this.checkForSaveChanges();
            });
        this.ngRedux.select((state) => {
            return state.wineryState.lastSavedJsonTopology;
        })
            .subscribe((topologyTemplate) => {
                this.lastSavedJsonTopologyTemplate = topologyTemplate;
            });
        this.ngRedux.select((state) => {
            return state.liveModelingState.deployedJsonTopology;
        })
            .subscribe((topologyTemplate) => {
                this.deployedJsonTopologyTemplate = topologyTemplate;
                this.checkForDeployChanges();
            });
        this.ngRedux.select((state) => {
            return state.liveModelingState.state;
        })
            .subscribe((state) => {
                this.liveModelingState = state;
            });
    }

    public enableCheck() {
        this.enabled = true;
    }

    public checkForSaveChanges() {
        if (!this.enabled) {
            return;
        }
        const changed = TopologyTemplateUtil.hasTopologyTemplateChanged(this.currentJsonTopologyTemplate, this.lastSavedJsonTopologyTemplate);
        this.ngRedux.dispatch(this.wineryActions.setUnsavedChanges(changed));
    }

    public checkForDeployChanges() {
        if (this.liveModelingState === LiveModelingStates.DISABLED || this.liveModelingState == null) {
            return;
        }
        this.backendService.requestTopologyTemplate().subscribe((resp) => {
            const changed = TopologyTemplateUtil.hasTopologyTemplateChanged(resp, this.deployedJsonTopologyTemplate);
            this.ngRedux.dispatch(this.liveModelingActions.setDeploymentChanges(changed));
        });
    }
}

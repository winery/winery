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
import { Component, OnDestroy } from '@angular/core';
import {
    InstanceModelPlugin, InstanceModelReceiveData, InstanceModelService, SubGraphData
} from './instanceModel.service';
import { PatternRefinementModel } from '../refinement/refinementWebSocket.service';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { TopologyRendererActions } from '../../redux/actions/topologyRenderer.actions';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs/Subscription';
import { TopologyTemplateUtil } from '../../models/topologyTemplateUtil';
import { WineryActions } from '../../redux/actions/winery.actions';
import { EntityTypesModel } from '../../models/entityTypesModel';
import { WineryRepositoryConfigurationService } from '../../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'winery-instance-model',
    templateUrl: './instanceModel.component.html',
    providers: [
        InstanceModelService
    ]
})
export class InstanceModelComponent implements OnDestroy {

    applicablePlugins: InstanceModelReceiveData;
    running = false;
    started = false;
    inputs: string[];
    inputMap: Map<string, string>;
    private subscription: Subscription;
    private entityTypes: EntityTypesModel;
    private selectedPlugin: InstanceModelPlugin;
    private selectedMatch: string;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private rendererActions: TopologyRendererActions,
                private wineryActions: WineryActions,
                private configurationService: WineryRepositoryConfigurationService,
                private notify: ToastrService,
                private service: InstanceModelService) {
        this.subscription = this.ngRedux.select(state => state.topologyRendererState.buttonsState)
            .subscribe(buttons => {
                if (!buttons.refineInstanceModelButton) {
                    this.stop();
                }
            });
        this.ngRedux.select(state => state.wineryState.entityTypes)
            .subscribe(types => this.entityTypes = types);
    }

    start() {
        this.started = true;
        this.service.start().subscribe(
            value => this.handleInput(value),
            error => this.handleError(error),
            () => this.handleComplete()
        );
    }

    onHoverOver(candidate: PatternRefinementModel) {
        this.ngRedux.dispatch(this.rendererActions.highlightNodes(candidate.nodeIdsToBeReplaced));
    }

    stop() {
        this.service.cancel();
        this.running = false;
    }

    private handleError(error: HttpErrorResponse) {
        this.running = false;
    }

    private handleInput(value: InstanceModelReceiveData) {
        this.applicablePlugins = value;
        this.running = false;
        if (value && value.topologyTemplate) {
            TopologyTemplateUtil.updateTopologyTemplate(this.ngRedux, this.wineryActions, value.topologyTemplate,
                this.entityTypes, this.configurationService.isYaml());
        }
    }

    private handleComplete() {
        this.running = false;
        this.started = false;
        this.notify.success('Instance Model Refinement completed!');
    }

    ngOnDestroy(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
            this.subscription = null;
        }
    }

    highlight(nodeIdsToBeReplace: string[]) {
        this.ngRedux.dispatch(this.rendererActions.highlightNodes(nodeIdsToBeReplace));
    }

    hoverOut() {
        this.ngRedux.dispatch(this.rendererActions.highlightNodes([]));
    }

    selectPlugin(plugin: InstanceModelPlugin, subGraph: SubGraphData) {
        this.selectedPlugin = plugin;
        this.selectedMatch = subGraph.id;

        if (subGraph.additionalInputs && subGraph.additionalInputs.length > 0) {
            this.inputs = subGraph.additionalInputs;
            this.inputMap = new Map<string, string>();
            // subGraph.additionalInputs.forEach( input => this.inputMap[input] = '');
        } else {
            this.sendRequest();
        }
    }

    sendRequest() {
        this.service.send({ pluginId: this.selectedPlugin.id, matchId: this.selectedMatch, userInputs: this.inputMap });
        this.inputMap = null;
        this.inputs = null;
        this.running = true;
    }
}

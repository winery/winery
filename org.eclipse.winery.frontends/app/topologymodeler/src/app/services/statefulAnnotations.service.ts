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
import { HttpClient } from '@angular/common/http';
import { BackendService } from './backend.service';
import { TTopologyTemplate } from '../models/ttopology-template';
import { TopologyTemplateUtil } from '../models/topologyTemplateUtil';
import { ErrorHandlerService } from './error-handler.service';
import { WineryActions } from '../redux/actions/winery.actions';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { ToastrService } from 'ngx-toastr';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';

interface TopologyAndErrorList {
    errorList: string[];
    topologyTemplate: TTopologyTemplate;
}

@Injectable()
export class StatefulAnnotationsService {

    constructor(private ngRedux: NgRedux<IWineryState>,
                private backendService: BackendService,
                private http: HttpClient,
                private actions: WineryActions,
                private rendererActions: TopologyRendererActions,
                private configurationService: WineryRepositoryConfigurationService,
                private alert: ToastrService,
                private errorHandler: ErrorHandlerService) {
        this.ngRedux.select(state => state.topologyRendererState.buttonsState.determineStatefulComponents)
            .subscribe(data => {
                if (data) {
                    this.updateTopology('determineStatefulComponents');
                    this.ngRedux.dispatch(this.rendererActions.determineStatefulComponents());
                }
            });
        this.ngRedux.select(state => state.topologyRendererState.buttonsState.determineFreezableComponentsButton)
            .subscribe(data => {
                if (data) {
                    this.determineFreezableComponents();
                    this.ngRedux.dispatch(this.rendererActions.determineFreezableComponents());
                }
            });
        this.ngRedux.select(state => state.topologyRendererState.buttonsState.cleanFreezableComponentsButton)
            .subscribe(data => {
                if (data) {
                    this.updateTopology('cleanFreezableComponents');
                    this.ngRedux.dispatch(this.rendererActions.cleanFreezableComponents());
                }
            });
    }

    private updateTopology(operation: string) {
        const config = this.backendService.configuration;
        const url = config.repositoryURL + '/' + config.parentPath
            + '/' + encodeURIComponent(encodeURIComponent(config.ns))
            + '/' + config.id
            + '/' + config.elementPath
            + '/' + operation.toLocaleLowerCase();

        this.http.get<TTopologyTemplate>(url)
            .subscribe(
                data =>
                    TopologyTemplateUtil.updateTopologyTemplate(this.ngRedux, this.actions, data, this.configurationService.isYaml()),
                error => this.errorHandler.handleError(error)
            );
    }

    private determineFreezableComponents() {
        const config = this.backendService.configuration;
        const url = config.repositoryURL + '/' + config.parentPath
            + '/' + encodeURIComponent(encodeURIComponent(config.ns))
            + '/' + config.id
            + '/' + config.elementPath
            + '/determinefreezablecomponents';

        this.http.get<TopologyAndErrorList>(url)
            .subscribe(
                data => {
                    TopologyTemplateUtil.updateTopologyTemplate(this.ngRedux, this.actions, data.topologyTemplate, this.configurationService.isYaml());
                    if (data.errorList && data.errorList.length > 0) {
                        this.alert.warning(
                            'There were no freeze operations found for some stateful components!',
                            'Error determining Freezability'
                        );
                    }
                },
                error => this.errorHandler.handleError(error)
            );
    }
}

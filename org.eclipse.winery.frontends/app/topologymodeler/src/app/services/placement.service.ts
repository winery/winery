/********************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
import { ToastrService } from 'ngx-toastr';
import { BackendService } from './backend.service';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { ErrorHandlerService } from './error-handler.service';

@Injectable()
export class PlaceComponentsService {

    constructor(private alert: ToastrService,
                private topologyRendererActions: TopologyRendererActions) {
    }

    /**
     * Place the components of the topology.
     * @param backendService    the backend service for accessing the post method for splitting
     * @param ngRedux    the redux instance
     * @param topologyRendererActions    the redux actions for toggling the placement button
     */
    placeComponents(backendService: BackendService, ngRedux: NgRedux<IWineryState>, topologyRendererActions: TopologyRendererActions,
                    errorHandler: ErrorHandlerService): void {

        backendService.placeComponents().subscribe(async res => {
                ngRedux.dispatch(topologyRendererActions.placeComponents());
                if (res.ok) {
                    const url = res.headers.get('location');
                    this.alert.success(' Reloading the page...', 'Components successfully placed.');
                    await this.delay(2000);
                    window.location.reload();
                }
            },
            error => {
                errorHandler.handleError(error);
            });
    }

    private delay(ms: number) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

}

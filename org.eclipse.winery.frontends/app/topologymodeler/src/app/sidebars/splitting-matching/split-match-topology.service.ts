/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
import { BackendService } from '../../services/backend.service';
import { ToastrService } from 'ngx-toastr';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { TopologyRendererActions } from '../../redux/actions/topologyRenderer.actions';
import { ErrorHandlerService } from '../../services/error-handler.service';
import { Observable } from 'rxjs/Observable';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { InjectorReplaceOptions } from './matchingEntity';
import { InjectorReplaceData } from './injectorData';
import { QNameApiData } from '../../../../../tosca-management/src/app/model/qNameApiData';

@Injectable()
export class SplitMatchTopologyService {

    constructor(private http: HttpClient,
                private alert: ToastrService,
                private backendService: BackendService) {
    }

    /**
     * Splits the topology.
     * @param backendService    the backend service for accessing the post method for splitting
     * @param ngRedux    the redux instance
     * @param topologyRendererActions    the redux actions for toggling the split button
     */
    splitTopology(backendService: BackendService, ngRedux: NgRedux<IWineryState>, topologyRendererActions: TopologyRendererActions,
                  errorHandler: ErrorHandlerService): void {

        backendService.splitTopology().subscribe(res => {
                ngRedux.dispatch(topologyRendererActions.splitTopology());
                this.openModelerFor(res.localname, res.namespace, false);
            },
            error => {
                errorHandler.handleError(error);
                ngRedux.dispatch(topologyRendererActions.splitTopology());
            });
    }

    /**
     * Matches the topology.
     * @param backendService    the backend service for accessing the post method for matching
     * @param ngRedux    the redux instance
     * @param topologyRendererActions    the redux actions for toggling the match button
     */
    matchTopology(backendService: BackendService, ngRedux: NgRedux<IWineryState>, topologyRendererActions: TopologyRendererActions,
                  errorHandler: ErrorHandlerService): void {
        backendService.matchTopology().subscribe(res => {
                ngRedux.dispatch(topologyRendererActions.matchTopology());
                if (res.ok) {
                    const url = res.headers.get('location');
                    this.alert.success('', 'Successfully matched.');
                    window.open(url, '_blank');
                }
            },
            error => {
                errorHandler.handleError(error);
                ngRedux.dispatch(topologyRendererActions.matchTopology());
            });
    }

    /**
     * For placing the components to available components in the target environment.
     * @param backendService    the backend service for accessing the post method for matching
     * @param ngRedux    the redux instance
     * @param topologyRendererActions    the redux actions for toggling the match button
     */
    getInjectionOptions(): Observable<InjectorReplaceOptions> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        const matchUrl = this.backendService.configuration.repositoryURL
            + '/servicetemplates/'
            + encodeURIComponent(encodeURIComponent(this.backendService.configuration.ns))
            + '/'
            + encodeURIComponent(this.backendService.configuration.id)
            + '/injector/options';
        return this.http.get<InjectorReplaceOptions>(matchUrl, { headers: headers }
        );
    }

    inject(selectedOptions: InjectorReplaceData): Observable<QNameApiData> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        const matchUrl = this.backendService.configuration.repositoryURL
            + '/servicetemplates/'
            + encodeURIComponent(encodeURIComponent(this.backendService.configuration.ns))
            + '/'
            + encodeURIComponent(this.backendService.configuration.id)
            + '/injector/replace';
        return this.http.post<QNameApiData>(matchUrl, selectedOptions, { headers: headers }
        );
    }

    private openModelerFor(id: string, ns: string, readonly: boolean) {
        let editorConfig = '?repositoryURL=' + encodeURIComponent(this.backendService.configuration.repositoryURL)
            + '&uiURL=' + encodeURIComponent(this.backendService.configuration.uiURL)
            + '&ns=' + encodeURIComponent(ns)
            + '&id=' + id
            + '&parentPath=' + this.backendService.configuration.parentPath
            + '&elementPath=' + this.backendService.configuration.elementPath;
        if (readonly) {
            editorConfig += '&isReadonly=true';
        }
        window.open(editorConfig, '_blank');
    }
}

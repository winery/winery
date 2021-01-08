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
import { HttpClient } from '@angular/common/http';
import { BackendService } from '../services/backend.service';
import { WineryVersion } from '../../../../tosca-management/src/app/model/wineryVersion';
import { TTopologyTemplate } from '../models/ttopology-template';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

@Injectable()
export class VersionSliderService {

    constructor(private http: HttpClient,
                private backendService: BackendService) {
    }

    getVersions(): Observable<WineryVersion[]> {
        const url = this.backendService.configuration.parentElementUrl + '?versions';
        return this.http.get<WineryVersion[]>(url)
            .map(array => array.reverse())
            // recreate class to access methods
            .map(array => array.map(v => VersionSliderService.toWineryVersion(v)));
    }

    hasMultipleVersions(): Observable<boolean> {
        return this.getVersions()
            .map(versions => versions && versions.length > 1);
    }

    getTopologyTemplate(id: string): Observable<TTopologyTemplate> {
        const url = this.backendService.configuration.repositoryURL + '/'
            + 'servicetemplates/'
            + encodeURIComponent(encodeURIComponent(this.backendService.configuration.ns)) + '/'
            + id + '/'
            + 'topologytemplate';
        return this.http.get<TTopologyTemplate>(url);
    }

    private static toWineryVersion(wv: any): WineryVersion {
        return new WineryVersion(
            wv.componentVersion,
            wv.wineryVersion,
            wv.workInProgressVersion,
            wv.currentVersion,
            wv.latestVersion,
            wv.releasable,
            wv.editable
        );
    }
}

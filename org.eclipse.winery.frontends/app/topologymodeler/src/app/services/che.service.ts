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
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { map } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';

export interface CheResponse {
    url: string;
}

@Injectable({
    providedIn: 'root'
})
export class CheService {

    constructor(private http: HttpClient,
                private toastrService: ToastrService,
                private configurationService: WineryRepositoryConfigurationService) {
    }

    getCheTheiaUrl(backendUrl: String): Observable<String> {
        return this.http.get<CheResponse>(`${backendUrl}/admin/che`).pipe(map((res) => res.url));
    }

    async openChe(backendUrl: String, id: String, namespace: String, type: String) {
        try {
            const theiaUrl = await this.getCheTheiaUrl(backendUrl).toPromise();
            const repositoryConfiguration = await this.configurationService.getRepositoryConfiguration().toPromise();
            const pathToOpen = theiaUrl + `/?path=${repositoryConfiguration.repositoryRoot}/${type}/${namespace}/${id}#/projects`;
            window.open(pathToOpen, '_blank');
        } catch (err) {
            if (err instanceof HttpErrorResponse) {
                if (err.status === 500) {
                    this.toastrService.error('Winery is not properly configured for IDE usage', 'Configuration Error');
                }
            }
        }
    }
}

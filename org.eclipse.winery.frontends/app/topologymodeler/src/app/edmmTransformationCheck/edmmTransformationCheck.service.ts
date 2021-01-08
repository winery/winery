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
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BackendService } from '../services/backend.service';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { TTopologyTemplate } from '../models/ttopology-template';
import 'rxjs/add/operator/mergeMap';

export interface EdmmTechnologyTransformationCheck {
    id: string;
    name: string;
    supports: number;
    unsupportedComponents: string[];
}

@Injectable()
export class EdmmTransformationCheckService {

    constructor(private http: HttpClient,
                private backendService: BackendService,
                private config: WineryRepositoryConfigurationService) {
    }

    doTransformationCheck(topologyTemplate: TTopologyTemplate): Observable<EdmmTechnologyTransformationCheck[]> {
        const edmmUrl = this.backendService.configuration.parentElementUrl + '?edmm';

        return this.backendService.saveTopologyTemplate(topologyTemplate)
            .flatMap(() =>
                this.http.get(edmmUrl, { headers: new HttpHeaders('Accept: text/xml'), responseType: 'text' })
                    .flatMap(edmmText =>
                        this.http.post<EdmmTechnologyTransformationCheck[]>(
                            this.config.configuration.endpoints.edmmTransformationTool,
                            edmmText,
                            { headers: new HttpHeaders('Content-Type: text/plain') }
                        )
                    )
            );
    }
}

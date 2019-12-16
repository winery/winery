/*******************************************************************************
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
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BackendService } from '../services/backend.service';
import { TopologyModelerConfiguration } from '../models/topologyModelerConfiguration';
import { Observable } from 'rxjs';
import { Enrichment } from './enrichmentEntity';
import { TTopologyTemplate } from '../models/ttopology-template';

@Injectable({
    providedIn: 'root'
})
export class EnricherService {

    private readonly configuration: TopologyModelerConfiguration;
    private readonly httpHeaders: HttpHeaders;

    constructor(private http: HttpClient,
                backendService: BackendService) {
        this.configuration = backendService.configuration;
        this.httpHeaders = new HttpHeaders().set('Accept', 'application/json');
    }

    /**
     * This method fetches available enrichments/features for a topology template.
     */
    getAvailableFeatures(): Observable<Enrichment> {
        const url = this.configuration.repositoryURL
            + '/servicetemplates/'
            + encodeURIComponent(encodeURIComponent(this.configuration.ns))
            + '/'
            + encodeURIComponent(this.configuration.id)
            + '/topologytemplate/availablefeatures';
        return this.http.get<Enrichment>(url, { headers: this.httpHeaders });
    }

    /**
     * This method applies selected enrichments/features for a topology template.
     * @param toApplyFeatures: Enrichment Object containing selected features for node templates.
     */
    applySelectedFeatures(toApplyFeatures: Enrichment[]): Observable<TTopologyTemplate> {
        const url = this.configuration.repositoryURL
            + '/servicetemplates/'
            + encodeURIComponent(encodeURIComponent(this.configuration.ns))
            + '/'
            + encodeURIComponent(this.configuration.id)
        + '/topologytemplate/availablefeatures';
        return this.http.put<TTopologyTemplate>(url, toApplyFeatures, { headers: this.httpHeaders });
    }
}

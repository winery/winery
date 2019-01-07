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
import { Observable } from 'rxjs/Observable';
import { ProblemFindings } from './problemEntity';

@Injectable()
export class ProblemDetectionService {

    readonly headers = new HttpHeaders().set('Accept', 'application/json');

    constructor(private http: HttpClient,
                private backendService: BackendService) {

    }

    detectProblems(): Observable<ProblemFindings[]> {
        const configuration = this.backendService.configuration;
        const url =  configuration.topologyProDecURL + '/checkProblems?'
            + 'wineryURL=' + encodeURIComponent(configuration.repositoryURL)
            + '&serviceTemplateNS=' + encodeURIComponent(configuration.ns)
            + '&serviceTemplateID=' + encodeURIComponent(configuration.id);

        return this.http.get<ProblemFindings[]>(url, {headers: this.headers})
    }
}

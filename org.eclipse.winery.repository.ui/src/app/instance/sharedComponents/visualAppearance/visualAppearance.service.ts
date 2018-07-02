/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class VisualAppearanceService {

    constructor(private http: HttpClient,
                private route: Router) {
    }

    getImg16x16Path(): string {
        return backendBaseURL + this.route.url + '/16x16';
    }

    getImg50x50Path(): string {
        return backendBaseURL + this.route.url + '/50x50';
    }

    getData() {
        return this.http.get(backendBaseURL + this.route.url + '/');
    }

    saveVisuals(data: any): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .put(
                backendBaseURL + this.route.url + '/',
                data,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    get path(): string {
        return this.route.url;
    }
}

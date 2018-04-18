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
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';
import { InstanceStateApiData } from './InstanceStateApiData';
import { HttpClient, HttpResponse } from '@angular/common/http';

@Injectable()
export class InstanceStateService {

    private readonly path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = backendBaseURL + this.route.url + '/';
    }

    getInstanceStates(): Observable<InstanceStateApiData[]> {
        return this.http.get<InstanceStateApiData[]>(this.path);
    }

    addPropertyData(newStateData: InstanceStateApiData): Observable<HttpResponse<string>> {
        // const headers = new Headers({'Content-Type': 'application/json'});
        return this.http
            .post(
                this.path, newStateData,
                { observe: 'response', responseType: 'text' }
            );
    }

    deleteState(stateToRemove: InstanceStateApiData): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                this.path + stateToRemove.state,
                { observe: 'response', responseType: 'text' }
            );
    }
}

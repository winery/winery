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
import { backendBaseURL } from '../../../../configuration';
import { RequirementOrCapability } from './requirementsOrCapabilitiesApiData';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class RequirementsOrCapabilitiesService {

    private path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = backendBaseURL + this.route.url + '/';
    }

    /**
     * gets all requirements or capabilities
     * @returns {Observable<RequirementOrCapability[]>}
     */
    getRequirementsOrCapabilities(): Observable<RequirementOrCapability[]> {
        return this.http.get<RequirementOrCapability[]>(this.path);
    }

    /**
     * Method to add a new requirement or capability.
     * @param reqOrCap
     * @returns {Observable<HttpResponse<string>>}
     */
    sendPostRequest(reqOrCap: RequirementOrCapability): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });

        return this.http
            .post(
                this.path,
                reqOrCap,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    /**
     * Method to delete a requirement or capability.
     * @param id
     * @returns {Observable<HttpResponse<string>>}
     */
    deleteCapOrReqDef(id: string): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                this.path + id + '/',
                { observe: 'response', responseType: 'text' }
            );
    }
}

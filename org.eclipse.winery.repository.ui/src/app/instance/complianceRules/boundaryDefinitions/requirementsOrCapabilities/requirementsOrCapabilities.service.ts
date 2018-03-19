/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../../configuration';
import { RequirementOrCapability } from './requirementsOrCapabilitiesApiData';

@Injectable()
export class RequirementsOrCapabilitiesService {

    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = backendBaseURL + this.route.url + '/';
    }

    /**
     * gets all requirements or capabilities
     * @returns {Observable<RequirementOrCapability[]>}
     */
    getRequirementsOrCapabilities(): Observable<RequirementOrCapability[]> {
        return this.sendJsonRequest(this.path);
    }

    /**
     * Method to add a new requirement or capability.
     * @param reqOrCap
     * @returns {Observable<Response>}
     */
    sendPostRequest(reqOrCap: RequirementOrCapability): Observable<any> {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.post(this.path, reqOrCap, options);
    }

    /**
     * Method to delete a requirement or capability.
     * @param id
     * @returns {Observable<Response>}
     */
    deleteCapOrReqDef(id: any): Observable<any> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });
        return this.http.delete(this.path + id + '/', options);
    }

    /**
     * Private method for DRY principle. It is used to get all kinds of data
     * for the specified sub path.
     *
     * @param requestPath string The path which is specific for each request.
     * @returns {Observable<any>}
     */
    private sendJsonRequest(requestPath: string): Observable<any> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });

        return this.http.get(requestPath, options)
            .map(res => res.json());
    }

}

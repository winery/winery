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
import { PropertyConstraintApiData } from './propertyConstraintApiData';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ConstraintTypeApiData } from './constraintTypesApiData';

@Injectable()
export class PropertyConstraintsService {

    private path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = backendBaseURL + this.route.url + '/';
    }

    getConstraints(): Observable<PropertyConstraintApiData[]> {
        return this.http.get<PropertyConstraintApiData[]>(this.path);
    }

    postConstraint(data: PropertyConstraintApiData): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        data.fragments = null;
        return this.http
            .post(
                this.path,
                data,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    deleteConstraints(data: PropertyConstraintApiData): Observable<HttpResponse<string>> {
        return this.http
            .delete(
                this.path + encodeURIComponent(encodeURIComponent(data.property)),
                { observe: 'response', responseType: 'text' }
            );
    }

    getConstraintTypes(): Observable<ConstraintTypeApiData[]> {
        return this.http.get<ConstraintTypeApiData[]>(backendBaseURL + '/admin/constrainttypes/');
    }
}

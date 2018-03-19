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
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { backendBaseURL } from '../../../../configuration';
import { PropertyConstraintApiData } from './propertyConstraintApiData';

@Injectable()
export class PropertyConstraintsService {

    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = backendBaseURL + this.route.url + '/';
    }

    getConstraints(): Observable<PropertyConstraintApiData[]> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.get(this.path, options)
            .map(res => res.json());
    }

    postConstraint(data: PropertyConstraintApiData): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});
        data.fragments = null;
        return this.http.post(this.path, JSON.stringify(data), options);
    }

    deleteConstraints(data: PropertyConstraintApiData) {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.delete(this.path + data.property, options);
    }

    getConstraintTypes(): Observable<any> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.get(backendBaseURL + '/admin/constrainttypes/', options)
            .map(res => res.json());
    }
}

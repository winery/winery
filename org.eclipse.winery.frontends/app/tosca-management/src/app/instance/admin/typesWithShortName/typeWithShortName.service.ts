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

export class TypeWithShortName {
    type: string;
    shortName: string;

    constructor(pType = '', pShortName = '') {
        this.type = pType;
        this.shortName = pShortName;
    }
}

@Injectable()
export class TypeWithShortNameService {

    private path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    getAllTypes(): Observable<TypeWithShortName[]> {
        return this.http.get<TypeWithShortName[]>(backendBaseURL + this.path + '/');
    }

    postTypes(types: TypeWithShortName[]): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .post(
                backendBaseURL + this.path + '/',
                types,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

    postType(type: TypeWithShortName): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        return this.http
            .post(
                backendBaseURL + this.path + '/',
                type,
                { headers: headers, observe: 'response', responseType: 'text' }
            );
    }

}

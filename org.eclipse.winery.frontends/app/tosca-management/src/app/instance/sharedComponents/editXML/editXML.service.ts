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
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

@Injectable()
export class EditXMLService {

    private readonly path: string;

    constructor(private http: HttpClient,
                private route: Router) {
        this.path = this.route.url;
        if (this.path.endsWith('xml')) {
            this.path = this.path.slice(0, -3);
        }
    }

    getXmlData(): Observable<string> {
        const headers = new HttpHeaders({ 'Accept': 'application/xml' });

        let getPath = this.path;
        if (!getPath.endsWith('properties') && !getPath.endsWith(('selfserviceportal/'))) {
            getPath += 'xml/';
        }

        return this.http.get(
            backendBaseURL + getPath,
            { headers: headers, responseType: 'text' }
        );
    }

    saveXmlData(xmlData: String): Observable<HttpResponse<string>> {
        const headers = new HttpHeaders({ 'Content-Type': 'application/xml' });

        return this.http.put(
            backendBaseURL + this.path,
            xmlData,
            { headers: headers, observe: 'response', responseType: 'text' }
        );
    }
}
